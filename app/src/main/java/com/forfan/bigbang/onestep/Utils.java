package com.forfan.bigbang.onestep;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * Created by wangyan-pd on 2016/12/21.
 */
public class Utils {
    public static List<ResolveInfo> getAllAppsInfo(Context context) {
        return context.getPackageManager().queryIntentActivities(Intent.makeMainActivity(null), 0);
    }

}
