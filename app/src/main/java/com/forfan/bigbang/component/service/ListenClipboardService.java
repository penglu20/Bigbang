package com.forfan.bigbang.component.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class ListenClipboardService extends Service  {
    private static final String TAG="ListenClipboardService";

    private static final int GRAY_SERVICE_ID = -1001;
    private static CharSequence sLastContent = null;
    private ClipboardManagerCompat mClipboardWatcher;
    private Handler handler;
    private boolean isGrayGuardOn;
    private Pattern wordPattern;

    private ClipboardManagerCompat.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener = new ClipboardManagerCompat.OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {
            performClipboardCheck();
        }
    };

    private TipViewController.ActionListener actionListener=new TipViewController.ActionListener() {
        @Override
        public void isShow(boolean isShow) {
            showBigBang=isShow;
            int text = isShow ?R.string.bigbang_open:R.string.bigbang_close;
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
        intentFilter.addAction(ConstantUtil.BROADCAST_SET_TO_CLIPBOARD);
        registerReceiver(clipboardBroadcastReceiver,intentFilter);

        readSettingFromSp();

        handler=new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    startService(new Intent(ListenClipboardService.this,BigBangMonitorService.class));
                    if (showFloatView){
                        tipViewController.show();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                handler.postDelayed(this,3000);
            }
        });

        wordPattern=Pattern.compile("\\w");
    }

    @Override
    public void onDestroy() {
        mClipboardWatcher.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
        tipViewController.removeActionListener(actionListener);
        tipViewController.remove();
//        sLastContent = null;
        isGrayGuardOn=false;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
        if (!monitorClipborad || contentc==null) {
            return;
        }
        if (showFloatView && !showBigBang) {
           return;
        }
        boolean isValidString=true;
        String content = contentc.toString().trim();
        LogUtil.d(TAG,"showContent:"+content);
        LogUtil.d(TAG,"sLastContent:"+sLastContent);
        if (TextUtils.isEmpty(content) || (sLastContent != null && sLastContent.toString().trim().equals(content))  ) {
//        if ( content == null) {
            LogUtil.d(TAG,"TextUtils.isEmpty(content) || (sLastContent != null && sLastContent.toString().trim().equals(content)): "+true);
            sLastContent=null;
            isValidString=false;
        }
        Matcher matcher=wordPattern.matcher(content);
        if (sLastContent!=null) {
            Matcher matcher2 = wordPattern.matcher(sLastContent);
            if (!matcher2.find()){
                isValidString=false;
            }
            LogUtil.d(TAG,"sLastContent isValidString="+isValidString);
        }
        if (!isValidString || !matcher.find()){
            sLastContent=mClipboardWatcher.getText();
            handler.removeCallbacks(cleanLaseContent);
            handler.postDelayed(cleanLaseContent, 2000);
            LogUtil.d(TAG,"!isValidString || !matcher.find()"+content);
            return;
        }
//        sLastContent = content;
        Intent intent=new Intent(this, BigBangActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra(BigBangActivity.TO_SPLIT_STR,sLastContent);
        intent.putExtra(BigBangActivity.TO_SPLIT_STR,content);
//        startActivity(intent);
        //放到TipViewController中触发试试
        tipViewController.showTipViewForStartActivity(intent);
    }
    Runnable cleanLaseContent = new Runnable() {
        @Override
        public void run () {
            LogUtil.d(TAG, "sLastContent=null," + sLastContent);
            sLastContent = null;
        }
    };
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
            if (intent.getAction().equals(ConstantUtil.BROADCAST_SET_TO_CLIPBOARD)){
                sLastContent=intent.getStringExtra(ConstantUtil.BROADCAST_SET_TO_CLIPBOARD_MSG);
                LogUtil.d(TAG,"onReceive:"+sLastContent);
            }else {
                readSettingFromSp();
            }
        }
    };
}