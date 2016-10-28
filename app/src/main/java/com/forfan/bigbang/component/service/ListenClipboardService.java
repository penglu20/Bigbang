package com.forfan.bigbang.component.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.forfan.bigbang.clipboard.ClipboardManagerCompat;
import com.forfan.bigbang.util.LogUtil;


public final class ListenClipboardService extends Service  {
    private static final String TAG="ListenClipboardService";

    private static CharSequence sLastContent = null;
    private ClipboardManagerCompat mClipboardWatcher;
    private ClipboardManagerCompat.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener = new ClipboardManagerCompat.OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {
            performClipboardCheck();
        }
    };

    public static void start(Context context) {
        Intent serviceIntent = new Intent(context, ListenClipboardService.class);
        context.startService(serviceIntent);
    }

    @Override
    public void onCreate() {
        mClipboardWatcher = ClipboardManagerCompat.create(this);
        mClipboardWatcher.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mClipboardWatcher.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
        sLastContent = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
        if (sLastContent != null && sLastContent.equals(content) || content == null) {
            return;
        }
        LogUtil.e(TAG,"showContent:"+content);
        sLastContent = content;
    }

}