package com.forfan.bigbang.onestep;

import android.content.pm.ResolveInfo;

/**
 * Created by wangyan-pd on 2016/12/21.
 */

public class ResolveInfoWrap {
    public ResolveInfo resolveInfo;
    public int type;

    public ResolveInfoWrap(ResolveInfo localResolveInfo, int typeUrl) {
        resolveInfo = localResolveInfo;
        type = typeUrl;
    }
}
