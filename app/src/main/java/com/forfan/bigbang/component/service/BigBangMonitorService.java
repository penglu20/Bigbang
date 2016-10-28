package com.forfan.bigbang.component.service;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.forfan.bigbang.BigBangApp;
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

    @Override
    public void onCreate() {
        super.onCreate();
        tipViewController=new TipViewController(getApplicationContext());
        tipViewController.setActionListener(bigBangActionListener);
        tipViewController.show();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int type=event.getEventType();
        if (type!=TYPE_NOTIFICATION_STATE_CHANGED){
            LogUtil.e(TAG,"AccessibilityEvent:"+event);
        }
        switch (type){
            case TYPE_VIEW_CLICKED:
                LogUtil.e(TAG,"TYPE_VIEW_CLICKED:"+event);
                break;
            case TYPE_VIEW_LONG_CLICKED:
                LogUtil.e(TAG,"TYPE_VIEW_LONG_CLICKED:"+event);
                getText(event);
                break;
            case TYPE_VIEW_CONTEXT_CLICKED:
                LogUtil.e(TAG,"TYPE_VIEW_CONTEXT_CLICKED:"+event);
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
        AccessibilityNodeInfo info=event.getSource();
        LogUtil.e(TAG,"getText:"+info.getText());
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
            ToastUtil.show(txt.toString());
            tipViewController.showBigBang(txt.toString().split("[!%！  ，~,-_=+。\\.\\\\]"));
        }else {
            tipViewController.showImage();
        }
    }

    BigBangLayout.ActionListener bigBangActionListener=new BigBangLayout.ActionListener() {

        @Override
        public void onSearch(String text) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.baidu.com/s?wd=" + URLEncoder.encode(text, "utf-8")));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                tipViewController.showImage();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onShare(String text) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(sharingIntent);
            tipViewController.showImage();
        }

        @Override
        public void onCopy(String text) {
            if (!TextUtils.isEmpty(text)) {
                ClipboardManager service = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                service.setPrimaryClip(ClipData.newPlainText("BigBang", text));
                ToastUtil.show("已复制");
                tipViewController.showImage();
            }
        }
    };

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
