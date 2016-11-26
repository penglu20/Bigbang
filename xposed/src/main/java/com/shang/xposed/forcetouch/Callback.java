package com.shang.xposed.forcetouch;

/**
 * Created by Michele on 22/10/2016.
 */
public interface Callback {

    /**
     * Invoked when force pressure is detected
     */
    void onForceTouch();

    /**
     * Invoked when normal pressure is detected
     */
    void onNormalTouch();
}
