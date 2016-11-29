package com.forfan.bigbang.util;

import android.content.Context;

import com.umeng.analytics.AnalyticsConfig;

/**
 * Created by l4656_000 on 2015/12/26.
 */
public class ChanelUtil {


    public static String getChanelName(Context context){
        return AnalyticsConfig.getChannel(context);
    }

    public static boolean is360SDK(Context context){
//        String chanel=getChanelName(context);
//        if (chanel.equalsIgnoreCase(_360)||chanel.equalsIgnoreCase(GOAPK)||chanel.equalsIgnoreCase(EOE)){
//            return true;
//        }else{
//            return false;
//        }
        return true;
    }

    public static boolean isForTest(Context context){
        return "for_test".equals(getChanelName(context));
//        return false;
    }

    public static boolean isXposedApk(Context context){
        return "xposed".equals(getChanelName(context));
    }
}
