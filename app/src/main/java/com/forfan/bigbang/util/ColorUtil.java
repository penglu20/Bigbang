package com.forfan.bigbang.util;

import android.graphics.Color;

/**
 * Created by Administrator on 2016/12/3.
 */
public class ColorUtil {
    public static int getPropertyTextColor(int color,int alpha){
        if (alpha<20){

        }
        int red= Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        if (red>128 && green>128 && blue>128){
            return Color.BLACK;
        }else {
            return Color.WHITE;
        }
    }
}
