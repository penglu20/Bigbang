package com.forfan.bigbang.util;

import android.view.View;

import com.forfan.bigbang.BigBangApp;
import com.forfan.bigbang.R;
import com.qihoo.updatesdk.lib.UpdateHelper;

/**
 * Created by penglu on 2016/3/3.
 */
public class UpdateUtil {

    static {
        UpdateHelper.getInstance().init(BigBangApp.getInstance(),
                BigBangApp.getInstance().getResources().getColor(R.color.primary));
    }

    public static void autoCheckUpdate(){
        UpdateHelper.getInstance().autoUpdate(BigBangApp.getInstance().getPackageName());
    }

    public static void UserCheckUpdate(final View view){
        if (!NetWorkUtil.isWifi(BigBangApp.getInstance())){
            SnackBarUtil.show(view,"在非wifi下升级，可能会消耗您少量流量！");
        }
        UpdateHelper.getInstance().manualUpdate(BigBangApp.getInstance().getPackageName());
    }
}
