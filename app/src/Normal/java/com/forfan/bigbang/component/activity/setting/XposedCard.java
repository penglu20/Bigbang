package com.forfan.bigbang.component.activity.setting;

import android.content.Context;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;

/**
 * Created by wangyan-pd on 2016/11/25.
 */

public class XposedCard extends AbsCard {
    public XposedCard(Context context) {
        super(context);
        inflate(context, R.layout.card_xposed,this);
        initView(context);
    }

    private void initView(Context context) {
    }

}
