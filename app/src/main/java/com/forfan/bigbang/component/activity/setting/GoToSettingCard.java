package com.forfan.bigbang.component.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;
import com.forfan.bigbang.util.SnackBarUtil;

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
                try {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    mContext.startActivity(intent);
                } catch (Throwable e) {
                    SnackBarUtil.show(v, R.string.open_setting_failed_diy);
                }
            }
        });
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            findViewById(R.id.goto_voice_rl).setVisibility(VISIBLE);
            findViewById(R.id.goto_voice_rl).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Settings.ACTION_VOICE_INPUT_SETTINGS);
                        mContext.startActivity(intent);
                    } catch (Throwable e) {
                        SnackBarUtil.show(v, R.string.open_setting_failed_diy);
                    }
                }
            });
        }
    }

}
