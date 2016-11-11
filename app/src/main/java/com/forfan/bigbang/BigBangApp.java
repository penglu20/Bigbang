package com.forfan.bigbang;

import android.app.Application;
import android.content.Intent;

import com.forfan.bigbang.component.service.BigBangMonitorService;
import com.forfan.bigbang.component.service.ListenClipboardService;
import com.forfan.bigbang.util.KeepAliveWatcher;

/**
 * Created by penglu on 2016/10/26.
 */

public class BigBangApp extends Application {
    private static BigBangApp instance;

    public static BigBangApp getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        KeepAliveWatcher.keepAlive(this);
        startService(new Intent(this, ListenClipboardService.class));
        startService(new Intent(this, BigBangMonitorService.class));
    }
}
