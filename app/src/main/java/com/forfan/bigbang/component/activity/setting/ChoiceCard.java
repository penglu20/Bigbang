package com.forfan.bigbang.component.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;
import com.forfan.bigbang.component.activity.OcrActivity;
import com.forfan.bigbang.component.activity.SettingBigBangActivity;
import com.forfan.bigbang.component.contentProvider.SPHelper;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.UrlCountUtil;

import static com.forfan.bigbang.util.ConstantUtil.BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED;

/**
 * Created by wangyan-pd on 2016/11/9.
 */

public class ChoiceCard extends AbsCard {
    private SwitchCompat browserSwitch;

    public ChoiceCard(Context context) {
        super(context);
        initView(context);
        refresh();
    }

    private void refresh() {
        browserSwitch.setChecked(SPHelper.getBoolean(ConstantUtil.USE_LOCAL_WEBVIEW,true));
    }

    private void initView(Context context) {
        mContext=context;
        LayoutInflater.from(context).inflate(R.layout.card_choice,this);
        findViewById(R.id.setting_bigbang).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_SET_STYLE_BIGBANG);
                Intent intent=new Intent(mContext,SettingBigBangActivity.class);
                mContext.startActivity(intent);
            }
        });

        browserSwitch = (SwitchCompat)findViewById(R.id.browser_switch);
        browserSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton aSwitch, boolean isChecked) {
                SPHelper.save(ConstantUtil.USE_LOCAL_WEBVIEW, isChecked);
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_USE_BUILTIN_BROWSER,isChecked);
            }
        });

        findViewById(R.id.browser_setting).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                browserSwitch.setChecked(!browserSwitch.isChecked());
            }
        });

    }

}
