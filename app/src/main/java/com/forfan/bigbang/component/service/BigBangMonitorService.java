package com.forfan.bigbang.component.service;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.forfan.bigbang.BigBangApp;
import com.forfan.bigbang.R;
import com.forfan.bigbang.component.activity.BigBangActivity;
import com.forfan.bigbang.component.activity.setting.SettingActivity;
import com.forfan.bigbang.component.contentProvider.SPHelper;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.LogUtil;
import com.forfan.bigbang.util.TipViewController;
import com.forfan.bigbang.util.ToastUtil;

import java.util.List;

import static android.view.accessibility.AccessibilityEvent.TYPE_VIEW_CLICKED;
import static android.view.accessibility.AccessibilityEvent.TYPE_VIEW_LONG_CLICKED;
import static com.forfan.bigbang.component.activity.setting.MonitorSettingCard.SPINNER_ARRAY;


public class BigBangMonitorService extends AccessibilityService {

    private static final String TAG="BigBangMonitorService";

    private static final int TYPE_VIEW_CLICKED=AccessibilityEvent.TYPE_VIEW_CLICKED;
    private static final int TYPE_VIEW_LONG_CLICKED=AccessibilityEvent.TYPE_VIEW_LONG_CLICKED;
    private static final int TYPE_VIEW_NONE=3;

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

    @Override
    public void onCreate() {
        super.onCreate();
        tipViewController=TipViewController.getInstance();
        tipViewController.addActionListener(actionListener);

        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(ConstantUtil.BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED);
        registerReceiver(bigBangBroadcastReceiver,intentFilter);

        readSettingFromSp();
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
            String text = isShow ? "BigBang 功能已打开":"BigBang 功能已关闭";
            ToastUtil.show(text);
        }

        @Override
        public boolean longPressed() {
            Intent intent=new Intent(BigBangMonitorService.this, SettingActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
    };

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        LogUtil.e(TAG,"onAccessibilityEvent:"+event);
        int type=event.getEventType();
        switch (type){
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                mWindowClassName = event.getClassName();
                break;
            case TYPE_VIEW_CLICKED:
            case TYPE_VIEW_LONG_CLICKED:
                getText(event);
                break;
        }
    }

    @Override
    public void onInterrupt() {
        Log.e(TAG,"onInterrupt");
    }

    private synchronized void getText(AccessibilityEvent event){
        LogUtil.e(TAG,"getText:"+event);
        if (!showBigBang || !monitorClick){
            return;
        }
        int type=event.getEventType();
        CharSequence className = event.getClassName();
        if ("com.tencent.mm.ui.LauncherUI".equals(mWindowClassName)){
            if (type!=weixinSelection){
                return;
            }
        }else if ("com.tencent.mobileqq.activity.SplashActivity".equals(mWindowClassName)){
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
        return 2;
    }

    private BroadcastReceiver bigBangBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            readSettingFromSp();
        }
    };
}
