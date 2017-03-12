package com.forfan.bigbang.component.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.forfan.bigbang.R;
import com.forfan.bigbang.clipboard.ClipboardManagerCompat;
import com.forfan.bigbang.component.activity.BigBangActivity;
import com.forfan.bigbang.component.activity.setting.SettingActivity;
import com.forfan.bigbang.util.ArcTipViewController;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.LogUtil;
import com.forfan.bigbang.util.ToastUtil;
import com.forfan.bigbang.util.UrlCountUtil;
import com.shang.commonjar.contentProvider.Global;
import com.shang.commonjar.contentProvider.SPHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class ListenClipboardService extends Service {
    private static final String TAG = "ListenClipboardService";

    private static final int GRAY_SERVICE_ID = -1001;
    private static final int NOTIFYID=10010;
    private static CharSequence sLastContent = null;
    private ClipboardManagerCompat mClipboardWatcher;
    private Handler handler;
    private boolean isGrayGuardOn;
    private Pattern wordPattern;
    BigbangNotification bigbangNotification;
    boolean isRun;



    private boolean monitorClipborad = true;
    private boolean showFloatView = true;

    private boolean isForegroundShow=false;

    private ClipboardManagerCompat.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener = new ClipboardManagerCompat.OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {
            performClipboardCheck();
        }
    };

    private ArcTipViewController.ActionListener actionListener = new ArcTipViewController.ActionListener() {
        @Override
        public void isShow(boolean isShow) {
            isRun = isShow;
            isForegroundShow=false;
            adjustService();
        }

        @Override
        public boolean longPressed() {
            Intent intent = new Intent(ListenClipboardService.this, SettingActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
    };


    public static class GrayInnerService extends Service {

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(GRAY_SERVICE_ID, new Notification());
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
    }

    public static void start(Context context) {
        Intent serviceIntent = new Intent(context, ListenClipboardService.class);
        context.startService(serviceIntent);
    }

    @Override
    public void onCreate() {
        mClipboardWatcher = ClipboardManagerCompat.create(this);
        Global.init(this);
        handler = new Handler();
        readSettingFromSp();


        ArcTipViewController.getInstance().addActionListener(actionListener);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstantUtil.BROADCAST_CLIPBOARD_LISTEN_SERVICE_MODIFIED);
        intentFilter.addAction(ConstantUtil.BROADCAST_SET_TO_CLIPBOARD);
        intentFilter.addAction(ConstantUtil.MONITOR_CLIPBOARD_BROADCAST);
        intentFilter.addAction(ConstantUtil.TOTAL_SWITCH_BROADCAST);
        registerReceiver(clipboardBroadcastReceiver, intentFilter);
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    startService(new Intent(ListenClipboardService.this, BigBangMonitorService.class));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                handler.postDelayed(this, 3000);
            }
        });

        wordPattern = Pattern.compile("\\w");
    }

    @Override
    public void onDestroy() {
        mClipboardWatcher.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
        ArcTipViewController.getInstance().removeActionListener(actionListener);
        ArcTipViewController.getInstance().remove();
//        sLastContent = null;
        isGrayGuardOn = false;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        adjustService();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void performClipboardCheck() {
        CharSequence content = mClipboardWatcher.getText();
        if (TextUtils.isEmpty(content)) {
            return;
        }
        showContent(content);
    }

    private void showContent(CharSequence contentc) {
        if (!monitorClipborad || contentc == null) {
            return;
        }
        if (!isRun){
            return;
        }
        if (showFloatView && !isRun) {
            return;
        }
        boolean isValidString = true;
        String content = contentc.toString().trim();
        LogUtil.d(TAG, "showContent:" + content);
        LogUtil.d(TAG, "sLastContent:" + sLastContent);
        if (TextUtils.isEmpty(content) || (sLastContent != null && sLastContent.toString().trim().equals(content))) {
//        if ( content == null) {
            LogUtil.d(TAG, "TextUtils.isEmpty(content) || (sLastContent != null && sLastContent.toString().trim().equals(content)): " + true);
            sLastContent = null;
            isValidString = false;
        }
        Matcher matcher = wordPattern.matcher(content);
        if (sLastContent != null) {
            Matcher matcher2 = wordPattern.matcher(sLastContent);
            if (!matcher2.find()) {
                isValidString = false;
            }
            LogUtil.d(TAG, "sLastContent isValidString=" + isValidString);
        }
        if (!isValidString || !matcher.find()) {
            sLastContent = mClipboardWatcher.getText();
            handler.removeCallbacks(cleanLaseContent);
            handler.postDelayed(cleanLaseContent, 2000);
            LogUtil.d(TAG, "!isValidString || !matcher.find()" + content);
            return;
        }
//        sLastContent = content;
        Intent intent = new Intent(this, BigBangActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra(BigBangActivity.TO_SPLIT_STR,sLastContent);
        intent.putExtra(BigBangActivity.TO_SPLIT_STR, content);
//        startActivity(intent);
        //放到ArcTipViewController中触发试试
        ArcTipViewController.getInstance().showTipViewForStartActivity(intent);
    }

    Runnable cleanLaseContent = new Runnable() {
        @Override
        public void run() {
            LogUtil.d(TAG, "sLastContent=null," + sLastContent);
            sLastContent = null;
        }
    };



    private void adjustService() {
        boolean isForground = SPHelper.getBoolean(ConstantUtil.IS_SHOW_NOTIFY, false);
        if (isForground) {
            if (!isForegroundShow) {
                if (bigbangNotification == null) {
                    bigbangNotification = new BigbangNotification(this);
                }
                bigbangNotification.setContetView();
                startForeground(NOTIFYID, bigbangNotification.getNotification());
                isForegroundShow = true;
                isGrayGuardOn=false;
            }
        } else {
            stopForeground();
        }
    }


    private void stopForeground(){
        if (isForegroundShow) {
            stopForeground(true);
            isForegroundShow=false;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isGrayGuardOn) {
                    if (Build.VERSION.SDK_INT < 18) {
                        startForeground(GRAY_SERVICE_ID, new Notification());
                    } else {
                        Intent innerIntent = new Intent(ListenClipboardService.this, GrayInnerService.class);
                        startService(innerIntent);
                        startForeground(GRAY_SERVICE_ID, new Notification());
                    }
                    isGrayGuardOn=true;
                }
            }
        }, 3000);
    }

    private void readSettingFromSp(){
        isRun = SPHelper.getBoolean(ConstantUtil.TOTAL_SWITCH, true);
        showFloatView =SPHelper.getBoolean(ConstantUtil.SHOW_FLOAT_VIEW,false);
        if (showFloatView){
            ArcTipViewController.getInstance().show();

        } else {
            ArcTipViewController.getInstance().remove();
        }
        if (!isRun){
            monitorClipborad=false;
            showFloatView=false;
//            ArcTipViewController.getInstance().remove();
            isForegroundShow=false;
            adjustService();
            return;
        }

        monitorClipborad= SPHelper.getBoolean(ConstantUtil.MONITOR_CLIP_BOARD,true);
        showFloatView =SPHelper.getBoolean(ConstantUtil.SHOW_FLOAT_VIEW,false);
        if (showFloatView){
            ArcTipViewController.getInstance().show();

        } else {
            ArcTipViewController.getInstance().remove();
        }
        if (monitorClipborad) {
            mClipboardWatcher.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
        } else {
            mClipboardWatcher.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
        }
        isForegroundShow=false;
        adjustService();
    }

    private BroadcastReceiver clipboardBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConstantUtil.BROADCAST_SET_TO_CLIPBOARD)) {
                sLastContent = intent.getStringExtra(ConstantUtil.BROADCAST_SET_TO_CLIPBOARD_MSG);
                LogUtil.d(TAG, "onReceive:" + sLastContent);
            }else if(intent.getAction().equals(ConstantUtil.MONITOR_CLIPBOARD_BROADCAST)){
                if (!isRun){
                    ToastUtil.show(R.string.open_total_switch_first);
                    return;
                }
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_NOFITY_CLIPBOARD,!monitorClipborad);
                SPHelper.save(ConstantUtil.MONITOR_CLIP_BOARD,!monitorClipborad);
                readSettingFromSp();
                if (monitorClipborad){
                    ToastUtil.show(R.string.monitor_clipboard_open);
                }else {
                    ToastUtil.show(R.string.monitor_clipboard_close);
                }
            } else if(intent.getAction().equals(ConstantUtil.TOTAL_SWITCH_BROADCAST)){
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_NOFITY_SWITCH,!isRun);
                SPHelper.save(ConstantUtil.TOTAL_SWITCH,!isRun);
                ArcTipViewController.getInstance().syncStates();
                sendBroadcast(new Intent(ConstantUtil.BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED));
                readSettingFromSp();
                if (isRun){
                    ToastUtil.show(R.string.bigbang_open);
                }else {
                    ToastUtil.show(R.string.bigbang_close);
                }
            } else {
                readSettingFromSp();
            }
        }
    };
}