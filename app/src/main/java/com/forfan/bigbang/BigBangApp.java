package com.forfan.bigbang;

import android.app.Application;

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
    }
}
