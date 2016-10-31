package com.forfan.bigbang.component.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.clipboard.ClipboardManagerCompat;
import com.forfan.bigbang.component.activity.BigBangActivity;
import com.forfan.bigbang.component.activity.setting.SettingActivity;
import com.forfan.bigbang.component.contentProvider.SPHelper;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.LogUtil;
import com.forfan.bigbang.util.TipViewController;
import com.forfan.bigbang.util.ToastUtil;
import com.forfan.bigbang.view.Dialog;
import com.forfan.bigbang.view.DialogFragment;
import com.forfan.bigbang.view.SimpleDialog;


public final class ListenClipboardService extends Service  {
    private static final String TAG="ListenClipboardService";

    private static final int GRAY_SERVICE_ID = -1001;
    private static CharSequence sLastContent = null;
    private ClipboardManagerCompat mClipboardWatcher;
    private ClipboardManagerCompat.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener = new ClipboardManagerCompat.OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {
            performClipboardCheck();
        }
    };

    private TipViewController.ActionListener actionListener=new TipViewController.ActionListener() {
        @Override
        public void isShow(boolean isShow) {
            showBigBang=isShow;
            String text = isShow ? "BigBang 功能已打开":"BigBang 功能已关闭";
            ToastUtil.show(text);

        }

        @Override
        public boolean longPressed() {
            Intent intent=new Intent(ListenClipboardService.this, SettingActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
    };



    private TipViewController tipViewController;
    private boolean showBigBang = true;
    private boolean monitorClipborad = true;
    private boolean showFloatView = true;

    public static class  GrayInnerService extends Service{

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(GRAY_SERVICE_ID,new Notification());
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

        tipViewController=TipViewController.getInstance();
        tipViewController.addActionListener(actionListener);


        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(ConstantUtil.BROADCAST_CLIPBOARD_LISTEN_SERVICE_MODIFIED);
        registerReceiver(clipboardBroadcastReceiver,intentFilter);

        readSettingFromSp();
    }

    @Override
    public void onDestroy() {
        mClipboardWatcher.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
        tipViewController.removeActionListener(actionListener);
        tipViewController.remove();
        sLastContent = null;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT < 18) {
            startForeground(GRAY_SERVICE_ID, new Notification());
        } else {
            Intent innerIntent = new Intent(ListenClipboardService.this, GrayInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, new Notification());
        }
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

    private void showContent(CharSequence content) {
        if (!showBigBang || !monitorClipborad){
            sLastContent=null;
            return;
        }
        if (sLastContent != null && sLastContent.equals(content) || content == null) {
            return;
        }
        LogUtil.d(TAG,"showContent:"+content);
        sLastContent = content;
        Intent intent=new Intent(this, BigBangActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(BigBangActivity.TO_SPLIT_STR,sLastContent);
        startActivity(intent);
    }

    private void readSettingFromSp(){
        monitorClipborad= SPHelper.getBoolean(ConstantUtil.MONITOR_CLIP_BOARD,true);
        showFloatView =SPHelper.getBoolean(ConstantUtil.SHOW_FLOAT_VIEW,true);
        if (showFloatView){
            tipViewController.show();
        }else {
            tipViewController.remove();
        }
        if (monitorClipborad){
            mClipboardWatcher.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
        }else {
            mClipboardWatcher.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
        }
    }

    private BroadcastReceiver clipboardBroadcastReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            readSettingFromSp();
        }
    };
}