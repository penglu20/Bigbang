package com.forfan.bigbang.component.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.SnackBarUtil;
import com.shang.commonjar.contentProvider.SPHelper;

import static com.forfan.bigbang.util.ConstantUtil.BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED;

/**
 * Created by penglu on 2016/12/11.
 */

public class GoToSettingCard extends AbsCard {


    private SwitchCompat autoOpenSwitch;

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
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            findViewById(R.id.goto_voice_rl).setVisibility(VISIBLE);
//            findViewById(R.id.goto_voice_rl).setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    try {
//                        Intent intent = new Intent(Settings.ACTION_VOICE_INPUT_SETTINGS);
//                        mContext.startActivity(intent);
//                    } catch (Throwable e) {
//                        SnackBarUtil.show(v, R.string.open_setting_failed_diy);
//                    }
//                }
//            });
//        }

        autoOpenSwitch= (SwitchCompat) findViewById(R.id.auto_open_switch);
        autoOpenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPHelper.save(ConstantUtil.AUTO_OPEN_SETTING,isChecked);
            }
        });
        findViewById(R.id.auto_open_rl).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                autoOpenSwitch.setChecked(!autoOpenSwitch.isChecked());
                mContext.sendBroadcast(new Intent(BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED));
            }
        });

        autoOpenSwitch.setChecked(SPHelper.getBoolean(ConstantUtil.AUTO_OPEN_SETTING,false));
    }

}
