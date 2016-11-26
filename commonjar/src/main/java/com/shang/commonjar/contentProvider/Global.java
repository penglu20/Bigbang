package com.shang.commonjar.contentProvider;

import android.content.Context;

/**
 * Created by wangyan-pd on 2016/11/25.
 */

public class Global {
    private static Context mContext;

    public static void init(Context context){
        mContext = context;
    }

    public static Context getInstance() {
        if(mContext == null)
            throw new NullPointerException("Global must be inited");
        return mContext;
    }
}
