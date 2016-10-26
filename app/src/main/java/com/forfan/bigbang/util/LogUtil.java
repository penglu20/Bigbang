package com.forfan.bigbang.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.forfan.bigbang.BigBangApp;

/**
 * Created by l4656_000 on 2015/11/28.
 */
public class LogUtil {

    public static boolean isEshow=true;
    public static boolean isWshow=true;
    public static boolean isIshow=true;
    public static boolean isDshow=true;
    public static boolean isVshow=true;


    static{
        if (isApkDebugable(BigBangApp.getInstance())){
            isEshow=true;
            isWshow=true;
            isIshow=true;
            isDshow=true;
            isVshow=true;
        }else {
            isEshow=true;
            isWshow=false;
            isIshow=false;
            isDshow=false;
            isVshow=false;
        }
    }

    public static boolean isApkDebugable(Context context) {
        try {
            ApplicationInfo info= context.getApplicationInfo();
            return (info.flags& ApplicationInfo.FLAG_DEBUGGABLE)== ApplicationInfo.FLAG_DEBUGGABLE;
        } catch (Exception e) {

        }
        return false;
    }

    public static void d(String msg){
        if(isDshow)
        Log.d("GetAway", msg);
    }
    public static void d(String tag, String msg){
        d(tag+","+msg);
    }
    public static void e(String msg){
        if(isEshow) Log.e("GetAway", msg);
    }
    public static void e(String tag, String msg){
        e(tag+","+msg);
    }
    public static void i(String msg){
        if(isIshow) Log.i("GetAway", msg);
    }
    public static void i(String tag, String msg){
        i(tag+","+msg);
    }
    public static void w(String msg){
        if(isWshow) Log.w("GetAway", msg);
    }
    public static void w(String tag, String msg){
        w(tag+","+msg);
    }
    public static void v(String msg){
        if(isVshow) Log.v("GetAway", msg);
    }
    public static void v(String tag, String msg){
        v(tag+","+msg);
    }
}
