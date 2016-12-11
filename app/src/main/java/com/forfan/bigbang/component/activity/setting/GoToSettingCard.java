package com.forfan.bigbang.component.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;

/**
 * Created by penglu on 2016/12/11.
 */

public class GoToSettingCard extends AbsCard {
    public GoToSettingCard(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;

        LayoutInflater.from(context).inflate(R.layout.card_goto_setting, this);
        findViewById(R.id.goto_access_rl).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoAccessbilitySetting();
            }
        });
    }

    private void gotoAccessbilitySetting() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        mContext.startActivity(intent);
    }

}
