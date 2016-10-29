package com.forfan.bigbang;

import android.app.Application;
import android.content.Intent;

import com.forfan.bigbang.component.service.BigBangMonitorService;
import com.forfan.bigbang.component.service.ListenClipboardService;

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
        startService(new Intent(this, ListenClipboardService.class));
        startService(new Intent(this, BigBangMonitorService.class));
    }
}
