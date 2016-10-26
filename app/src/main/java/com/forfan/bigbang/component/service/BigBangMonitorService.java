package com.forfan.bigbang.component.service;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;


public class BigBangMonitorService extends AccessibilityService {

    private static final String TAG="BigBangMonitorService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.e(TAG,"onAccessibilityEvent");
    }

    @Override
    public void onInterrupt() {
        Log.e(TAG,"onInterrupt");
    }

    @Override
    protected void onServiceConnected()
    {
        Log.e(TAG,"onServiceConnected");
        super.onServiceConnected();
    }

}
