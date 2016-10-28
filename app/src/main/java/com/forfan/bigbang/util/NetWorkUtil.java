package com.forfan.bigbang.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by l4656_000 on 2015/11/19.
 */
public class NetWorkUtil {
    public static boolean isWifi(Context context){
        ConnectivityManager cm= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        if (networkInfo!=null){
            boolean isConnected=networkInfo.isConnected();
            int type=networkInfo.getType();
            return isConnected&&type== ConnectivityManager.TYPE_WIFI;
        }
        return false;

    }
    public static boolean isConnected(Context context){
        ConnectivityManager cm= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        if (networkInfo!=null){
            return networkInfo.isConnected();
        }
        return false;
    }
}
