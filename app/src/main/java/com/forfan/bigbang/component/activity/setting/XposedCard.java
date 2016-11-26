package com.forfan.bigbang.component.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;
import com.shang.xposed.forcetouch.ForceTouchActivity;
import com.shang.xposed.setting.XposedAppManagerActivity;

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
        findViewById(R.id.xposed_whiteList).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, XposedAppManagerActivity.class);
                context.startActivity(intent);
            }
        });
        findViewById(R.id.xposed_touch_setting).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, ForceTouchActivity.class);
                context.startActivity(intent);
            }
        });
        findViewById(R.id.xposed_touch_setting).setVisibility(GONE);
    }

}
