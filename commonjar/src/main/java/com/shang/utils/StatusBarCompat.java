package com.shang.utils;

import android.app.Activity;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class StatusBarCompat {

    public static void setupStatusBarView(Activity activity, ViewGroup decorViewGroup, boolean on, int colorRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = activity.getWindow();
            setTranslucentStatus(win, on);
            View mStatusBarTintView = new View(activity);
            int mStatusBarHeight = 0;
            int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                mStatusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
            }
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, mStatusBarHeight);
            params.gravity = Gravity.TOP;
            mStatusBarTintView.setLayoutParams(params);
            mStatusBarTintView.setBackgroundResource(colorRes);
            mStatusBarTintView.setVisibility(View.VISIBLE);
            decorViewGroup.addView(mStatusBarTintView);
        }
    }

    public static void setTranslucentStatus(Window window, boolean on){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams winParams = window.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if (on) {
                winParams.flags |= bits;
            } else {
                winParams.flags &= ~bits;
            }
            window.setAttributes(winParams);
        }
    }

}  