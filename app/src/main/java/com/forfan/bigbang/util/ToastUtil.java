package com.forfan.bigbang.util;

import android.widget.Toast;

import com.forfan.bigbang.BigBangApp;


/**
 * Created by l4656_000 on 2015/12/27.
 */
public class ToastUtil {
    public static void show(String msg){
        Toast.makeText(BigBangApp.getInstance(), msg, Toast.LENGTH_SHORT).show();
    }
    public static void show(int rid){
        Toast.makeText(BigBangApp.getInstance(), rid, Toast.LENGTH_SHORT).show();
    }
    public static void showLong(int rid){
        Toast.makeText(BigBangApp.getInstance(), rid, Toast.LENGTH_LONG).show();
    }
}
