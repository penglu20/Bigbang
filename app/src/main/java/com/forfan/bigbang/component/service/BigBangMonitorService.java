package com.forfan.bigbang.component.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import com.forfan.bigbang.BigBangApp;
import com.forfan.bigbang.R;
import com.forfan.bigbang.component.activity.BigBangActivity;
import com.forfan.bigbang.component.activity.setting.SettingActivity;
import com.forfan.bigbang.component.contentProvider.SPHelper;
import com.forfan.bigbang.copy.CopyActivity;
import com.forfan.bigbang.copy.CopyNode;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.LogUtil;
import com.forfan.bigbang.util.TipViewController;
import com.forfan.bigbang.util.ToastUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.accessibilityservice.AccessibilityServiceInfo.FEEDBACK_GENERIC;
import static com.forfan.bigbang.component.activity.setting.MonitorSettingCard.SPINNER_ARRAY;


public class BigBangMonitorService extends AccessibilityService {

    private static final String TAG="BigBangMonitorService";

    private static final int TYPE_VIEW_CLICKED=AccessibilityEvent.TYPE_VIEW_CLICKED;
    private static final int TYPE_VIEW_LONG_CLICKED=AccessibilityEvent.TYPE_VIEW_LONG_CLICKED;
    private static final int TYPE_VIEW_DOUBLD_CLICKED=3;
    private static final int TYPE_VIEW_NONE=3;
    public  int double_click_interval = ConstantUtil.DEFAULT_DOUBLE_CLICK_INTERVAL;

    private CharSequence mWindowClassName;

    private TipViewController tipViewController;
    private boolean showBigBang = true;
    private boolean monitorClick =true;
    private boolean showFloatView =true;
    private boolean onlyText =true;

    private int qqSelection = TYPE_VIEW_LONG_CLICKED;
    private int weixinSelection = TYPE_VIEW_LONG_CLICKED;
    private int otherSelection = TYPE_VIEW_LONG_CLICKED;

    private boolean hasShowTipToast;
    private Handler handler;
    private HashSet<String> whiteList;

    private AccessibilityServiceInfo mAccessibilityServiceInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        tipViewController=TipViewController.getInstance();
        tipViewController.addActionListener(actionListener);

        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(ConstantUtil.BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED);
        intentFilter.addAction(ConstantUtil.REFRESH_WHITE_LIST_BROADCAST);
        intentFilter.addAction(ConstantUtil.UNIVERSAL_COPY_BROADCAST);
        intentFilter.addAction(ConstantUtil.SCREEN_CAPTURE_OVER_BROADCAST);
        registerReceiver(bigBangBroadcastReceiver,intentFilter);

        readSettingFromSp();

        handler=new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    startService(new Intent(BigBangMonitorService.this,ListenClipboardService.class));
                    if (showFloatView){
                        tipViewController.show();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                handler.postDelayed(this,3000);
            }
        });
        mAccessibilityServiceInfo=new AccessibilityServiceInfo();
        mAccessibilityServiceInfo.feedbackType=FEEDBACK_GENERIC;
        mAccessibilityServiceInfo.eventTypes=AccessibilityEvent.TYPE_VIEW_CLICKED|AccessibilityEvent.TYPE_VIEW_LONG_CLICKED|AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        mAccessibilityServiceInfo.flags=AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
        mAccessibilityServiceInfo.notificationTimeout=100;
        setServiceInfo(mAccessibilityServiceInfo);

        whiteList =new HashSet<>();
        if (!SPHelper.getBoolean(ConstantUtil.HAS_ADDED_LAUNCHER_AS_WHITE_LIST,false)){
            whiteList.addAll(getLauncherAsWhiteList());
            whiteList.addAll(getInputMethodAsWhiteList());
            saveSelectedApp();
            SPHelper.save(ConstantUtil.HAS_ADDED_LAUNCHER_AS_WHITE_LIST,true);
        }

        readWhiteList();
    }

    @Override
    public void onDestroy() {
        tipViewController.removeActionListener(actionListener);
        tipViewController.remove();
        unregisterReceiver(bigBangBroadcastReceiver);
        super.onDestroy();
    }

    private TipViewController.ActionListener actionListener=new TipViewController.ActionListener() {
        @Override
        public void isShow(boolean isShow) {
            showBigBang=isShow;
//            int text = isShow ? R.string.bigbang_open: R.string.bigbang_close;
//            ToastUtil.show(text);
        }

        @Override
        public boolean longPressed() {
            Intent intent=new Intent(BigBangMonitorService.this, SettingActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
    };

    private Set<String> getLauncherAsWhiteList(){
        HashSet<String> packages=new HashSet<>();
        PackageManager packageManager = getPackageManager();
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
//        final ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for(ResolveInfo ri : resolveInfo){
            packages.add(ri.activityInfo.packageName);
        }
        return packages;
    }

    private Set<String> getInputMethodAsWhiteList(){
        HashSet<String> packages=new HashSet<>();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> methodList = imm.getInputMethodList();
        for (InputMethodInfo info: methodList) {
            packages.add(info.getPackageName());
        }
        return packages;
    }



    private void saveSelectedApp(){
        if (whiteList!=null) {
            SPHelper.save(ConstantUtil.WHITE_LIST_COUNT, whiteList.size());
//            HashMap<String, String> map = new HashMap<>();
            List<String> list=new ArrayList<>();
            list.addAll(whiteList);
            for (int i = 0; i < list.size(); i++) {
                String value = list.get(i);
                SPHelper.save(ConstantUtil.WHITE_LIST + "_" + i, value);
//                map.put(UrlCountUtil.VALUE_MONITOR_WHITE_LIST_CLASS + "_" + i, value);
            }
            sendBroadcast(new Intent(ConstantUtil.REFRESH_WHITE_LIST_BROADCAST));
//            UrlCountUtil.onEvent(UrlCountUtil.VALUE_MONITOR_WHITE_LIST_CLASS, map);
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        LogUtil.e(TAG,"onAccessibilityEvent:"+event);
        int type=event.getEventType();
        switch (type){
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                mWindowClassName = event.getClassName();
                if ("com.tencent.mm.plugin.sns.ui.SnsTimeLineUI".equals(mWindowClassName)){
                    setCapabilities(true);
                }else {
                    setCapabilities(false);
                }
                break;
            case TYPE_VIEW_CLICKED:
            case TYPE_VIEW_LONG_CLICKED:
                getText(event);
                break;
        }
    }

    private void setCapabilities(boolean isPengYouQuan) {
        int flag= 0;
        flag = mAccessibilityServiceInfo.flags;
        if (isPengYouQuan) {
            mAccessibilityServiceInfo.flags=flag | (AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS);
        }else {
            mAccessibilityServiceInfo.flags=flag & (~AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS);
        }
        this.setServiceInfo(mAccessibilityServiceInfo);
    }

    @Override
    public void onInterrupt() {
        Log.e(TAG,"onInterrupt");
    }

    private synchronized void getText(AccessibilityEvent event){
        LogUtil.e(TAG,"getText:"+event);
        if (!monitorClick || event==null ) {
            return;
        }
        if (showFloatView && !showBigBang) {
            return;
        }
        int type=getClickType(event);
        CharSequence className = event.getClassName();
        if (mWindowClassName==null || whiteList.contains(event.getPackageName())|| whiteList.contains(mWindowClassName)){
            return;
        }
//        if ("com.tencent.mm.ui.LauncherUI".equals(mWindowClassName)){
        if ("com.tencent.mm".equals(event.getPackageName())){
            if (type!=weixinSelection){
                return;
            }
//        }else if ("com.tencent.mobileqq.activity.SplashActivity".equals(mWindowClassName)){
        }else if ("com.tencent.mobileqq".equals(event.getPackageName())){
            if (type!=qqSelection){
                return;
            }
        }else {
            if (type!=otherSelection){
                return;
            }
            if (mWindowClassName.toString().startsWith("com.forfan.bigbang")){
                //自己的应用不监控
                return;
            }
        }
        if (onlyText){
            if (className==null || !className.equals("android.widget.TextView")){
                if (!hasShowTipToast){
                    ToastUtil.show(R.string.toast_tip_content);
                    hasShowTipToast=true;
                }
                return;
            }
        }
        AccessibilityNodeInfo info=event.getSource();
        if(info==null){
            return;
        }
        CharSequence txt=info.getText();
        if (TextUtils.isEmpty(txt) && !onlyText){
            List<CharSequence> txts=event.getText();
            if (txts!=null) {
                StringBuilder sb=new StringBuilder();
                for (CharSequence t : txts) {
                    sb.append(t);
                }
                txt=sb.toString();
            }
        }
        if (!TextUtils.isEmpty(txt)) {
            Intent intent=new Intent(this, BigBangActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(BigBangActivity.TO_SPLIT_STR,txt.toString());
            startActivity(intent);
        }
    }


    private Method getSourceNodeIdMethod;
    private long mLastSourceNodeId;
    private long mLastClickTime;

    private long getSourceNodeId(AccessibilityEvent event)  {
        if (getSourceNodeIdMethod==null) {
            Class<AccessibilityEvent> eventClass = AccessibilityEvent.class;
            try {
                getSourceNodeIdMethod = eventClass.getMethod("getSourceNodeId");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        if (getSourceNodeIdMethod!=null) {
            try {
                return (long) getSourceNodeIdMethod.invoke(event);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    private int getClickType(AccessibilityEvent event){
        int type = event.getEventType();
        long time = event.getEventTime();
        long id=getSourceNodeId(event);
        if (type!=TYPE_VIEW_CLICKED){
            mLastClickTime=time;
            mLastSourceNodeId=-1;
            return type;
        }
        if (id==-1){
            mLastClickTime=time;
            mLastSourceNodeId=-1;
            return type;
        }
        if (type==TYPE_VIEW_CLICKED && time - mLastClickTime<= double_click_interval && id==mLastSourceNodeId){
            mLastClickTime=-1;
            mLastSourceNodeId=-1;
            return TYPE_VIEW_DOUBLD_CLICKED;
        }else {
            mLastClickTime=time;
            mLastSourceNodeId=id;
            return type;
        }
    }



    private int retryTimes = 0;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void UniversalCopy() {
        boolean isSuccess=false;
        label37: {
            AccessibilityNodeInfo rootInActiveWindow = this.getRootInActiveWindow();
            if(this.retryTimes < 10) {
                String packageName;
                if(rootInActiveWindow != null) {
                    packageName = String.valueOf(rootInActiveWindow.getPackageName());
                } else {
                    packageName = null;
                }

                if(rootInActiveWindow == null || packageName != null && packageName.contains("com.android.systemui")) {
                    ++this.retryTimes;
                    this.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            UniversalCopy();
                        }
                    }, 100);
                    return;
                }

                WindowManager var5 = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                var5.getDefaultDisplay().getMetrics(displayMetrics);
                int var1 = displayMetrics.heightPixels;
                int var2 = displayMetrics.widthPixels;
                ArrayList nodeList = traverseNode(new AccessibilityNodeInfoCompat(rootInActiveWindow), var2, var1);
                if(nodeList.size() > 0) {
                    Intent intent = new Intent(this, CopyActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if(!getPackageName().equals(packageName)) {
                        // TODO: 2016/11/17 研究下这里的逻辑
//                        intent.addFlags('耀');
                    }

                    intent.putParcelableArrayListExtra("copy_nodes", nodeList);
                    intent.putExtra("source_package", packageName);
                    this.startActivity(intent, ActivityOptions.makeCustomAnimation(this.getBaseContext(), android.R.anim.fade_in, android.R.anim.fade_out).toBundle());
                    isSuccess = true;
                    break label37;
                }

//                ae.a(this.getApplication(), "APP_DATA", "UC_MODE_FAILED", packageName);
            }

            isSuccess = false;
        }

        if(!isSuccess) {
            if (!BigBangMonitorService.isAccessibilitySettingsOn(this)){
                ToastUtil.show(R.string.error_in_permission);
            }else {
                ToastUtil.show(R.string.error_in_copy);
            }

        }

        this.retryTimes = 0;
    }

    private ArrayList<CopyNode> traverseNode(AccessibilityNodeInfoCompat nodeInfo, int var2, int var3) {
        ArrayList nodeList = new ArrayList();
        if(nodeInfo != null && nodeInfo.getInfo() != null) {
                 nodeInfo.refresh();

            for(int var4 = 0; var4 < nodeInfo.getChildCount(); ++var4) {
                nodeList.addAll(this.traverseNode(nodeInfo.getChild(var4), var2, var3));
            }

            if(nodeInfo.getClassName() != null && nodeInfo.getClassName().equals("android.webkit.WebView")) {
                return nodeList;
            } else {
                String content = null;
                String description = content;
                if(nodeInfo.getContentDescription() != null) {
                    description = content;
                    if(!"".equals(nodeInfo.getContentDescription())) {
                        description = nodeInfo.getContentDescription().toString();
                    }
                }

                content = description;
                if(nodeInfo.getText() != null) {
                    content = description;
                    if(!"".equals(nodeInfo.getText())) {
                        content = nodeInfo.getText().toString();
                    }
                }

                if(content != null) {
                    Rect var8 = new Rect();
                    nodeInfo.getBoundsInScreen(var8);
                    if(this.checkBound(var8, var2, var3)) {
                        nodeList.add(new CopyNode(var8, content));
                    }
                }

                return nodeList;
            }
        } else {
            return nodeList;
        }
    }


    private boolean checkBound(Rect var1, int var2, int var3) {
        return var1.bottom >= 0 && var1.right >= 0 && var1.top <= var3 && var1.left <= var2;
    }

    // To check if service is enabled
    public static boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = BigBangApp.getInstance().getPackageName() + "/" + BigBangMonitorService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            LogUtil.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            LogUtil.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            LogUtil.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    LogUtil.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        LogUtil.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            LogUtil.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }

    private synchronized void readSettingFromSp(){
        monitorClick = SPHelper.getBoolean(ConstantUtil.MONITOR_CLICK,true);
        showFloatView =SPHelper.getBoolean(ConstantUtil.SHOW_FLOAT_VIEW,true);
        onlyText = SPHelper.getBoolean(ConstantUtil.TEXT_ONLY,true) ;
        double_click_interval=SPHelper.getInt(ConstantUtil.DOUBLE_CLICK_INTERVAL,ConstantUtil.DEFAULT_DOUBLE_CLICK_INTERVAL);

        String[] spinnerArray= getResources().getStringArray(SPINNER_ARRAY);
        String qq = SPHelper.getString(ConstantUtil.QQ_SELECTION,spinnerArray[1]);
        String weixin = SPHelper.getString(ConstantUtil.WEIXIN_SELECTION,spinnerArray[1]);
        String other = SPHelper.getString(ConstantUtil.OTHER_SELECTION,spinnerArray[1]);
        if (showFloatView){
            tipViewController.show();
        }else {
            tipViewController.remove();
        }

        qqSelection=spinnerArrayIndex(spinnerArray, qq)+1;
        weixinSelection=spinnerArrayIndex(spinnerArray, weixin)+1;
        otherSelection=spinnerArrayIndex(spinnerArray, other)+1;
    }


    private int spinnerArrayIndex(String[] array,String txt){
        int length=array.length;
        for (int i=0;i<length;i++){
            if (array[i].equals(txt)){
                return i;
            }
        }
        return 3;
    }


    public synchronized void readWhiteList(){
        whiteList.clear();
        int size = SPHelper.getInt(ConstantUtil.WHITE_LIST_COUNT, 0);
        for (int i = 0; i < size; i++) {
            String packageName = SPHelper.getString(ConstantUtil.WHITE_LIST + "_" + i, "");
            whiteList.add(packageName);
        }
    }

    private BroadcastReceiver bigBangBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConstantUtil.REFRESH_WHITE_LIST_BROADCAST)){
                readWhiteList();
            }else if (intent.getAction().equals(ConstantUtil.UNIVERSAL_COPY_BROADCAST)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    UniversalCopy();
                }
            }else if (intent.getAction().equals(ConstantUtil.SCREEN_CAPTURE_OVER_BROADCAST)){
                tipViewController.stopLoadingAnim();
            } else {
                readSettingFromSp();
            }
        }
    };
}
