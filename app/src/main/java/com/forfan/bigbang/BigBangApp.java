package com.forfan.bigbang;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Looper;
import android.os.MessageQueue;

import com.forfan.bigbang.component.service.BigBangMonitorService;
import com.forfan.bigbang.component.service.ListenClipboardService;
import com.forfan.bigbang.component.service.voiceInteraction.BBVoiceInteractionService;
import com.forfan.bigbang.component.service.voiceInteraction.BBVoiceInteractionSessionService;
import com.forfan.bigbang.onestep.AppManager;
import com.forfan.bigbang.util.CrashHandler;
import com.forfan.bigbang.util.KeepAliveWatcher;
import com.forfan.bigbang.util.SharedIntentHelper;
import com.shang.commonjar.contentProvider.Global;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by penglu on 2016/10/26.
 */

public class BigBangApp extends Application {
    private static BigBangApp instance;

    public static BigBangApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Global.init(this);
        LeakCanary.install(this);
//        CrashHandler.getInstance().init(this);
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {

                KeepAliveWatcher.keepAlive(BigBangApp.this);
                startService(new Intent(BigBangApp.this, ListenClipboardService.class));
                startService(new Intent(BigBangApp.this, BigBangMonitorService.class));
                return false;
            }
        });
        AppManager.getInstance(this);
    }
}
