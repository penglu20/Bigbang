package com.forfan.bigbang.component.service;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.TransactionTooLargeException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.forfan.bigbang.BigBangApp;
import com.forfan.bigbang.component.activity.BigBangActivity;
import com.forfan.bigbang.util.LogUtil;
import com.forfan.bigbang.util.TipViewController;
import com.forfan.bigbang.util.ToastUtil;
import com.forfan.bigbang.view.BigBangLayout;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static android.view.accessibility.AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
import static android.view.accessibility.AccessibilityEvent.TYPE_VIEW_CLICKED;
import static android.view.accessibility.AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED;
import static android.view.accessibility.AccessibilityEvent.TYPE_VIEW_LONG_CLICKED;


public class BigBangMonitorService extends AccessibilityService {

    private static final String TAG="BigBangMonitorService";
    private TipViewController tipViewController;

    private CharSequence mWindowClassName;

    private boolean showBigBang = true;
    @Override
    public void onCreate() {
        super.onCreate();
        tipViewController=new TipViewController(getApplicationContext());
        tipViewController.show();
        tipViewController.setActionListener(isShow ->{
                showBigBang=isShow;
        });
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        LogUtil.e(TAG,"onAccessibilityEvent:"+event);
        int type=event.getEventType();
        CharSequence className = event.getClassName();
        switch (type){
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                mWindowClassName = className;
                break;
            case TYPE_VIEW_CLICKED:
                if ("com.tencent.mm.ui.LauncherUI".equals(mWindowClassName)){

                }
                getText(event);
                break;
            case TYPE_VIEW_LONG_CLICKED:
                getText(event);
                break;
        }
    }

    @Override
    public void onInterrupt() {
        Log.e(TAG,"onInterrupt");
    }

    private void getText(AccessibilityEvent event){
        LogUtil.e(TAG,"getText:"+event);
        if (!showBigBang){
            return;
        }
        AccessibilityNodeInfo info=event.getSource();
        CharSequence txt=info.getText();
        if (TextUtils.isEmpty(txt)){
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
}
