package com.forfan.bigbang.component;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.activity.setting.SettingActivity;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.UrlCountUtil;
import com.forfan.bigbang.view.ColorTextView;
import com.shang.commonjar.contentProvider.SPHelper;
import com.shang.utils.StatusBarCompat;

/**
 * Created by wangyan-pd on 2016/11/28.
 */

public class PreSettingActivity extends BaseActivity {
    public static final String SHOW = "pre_is_show";
    private ColorTextView colorText;
    private ColorTextView colorTextInto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presetting);
        StatusBarCompat.setupStatusBarView(this, (ViewGroup) getWindow().getDecorView(), true, R.color.colorPrimary);
        initView();
    }

    private void initView() {
        colorText = (ColorTextView)findViewById(R.id.colortext);
        colorText.setColorTextColor(getResources().getColor(R.color.colorPrimary));
        colorText.setColorText(getResources().getString(R.string.pre_setting_intro1));
        colorTextInto = (ColorTextView)findViewById(R.id.into);
        colorTextInto.setColorTextColor(getResources().getColor(R.color.colorPrimary));
        colorTextInto.setColorText(getResources().getString(R.string.pre_setting_intro2));

        findViewById(R.id.show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PreSettingActivity.this, SettingActivity.class);
                UrlCountUtil.onEvent(UrlCountUtil.PRE__FLOATVIEW,true);
                SPHelper.save(ConstantUtil.SHOW_FLOAT_VIEW, true);
                startActivity(intent);
                SPHelper.save(SHOW,false);
                finish();
            }
        });
        findViewById(R.id.gone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPHelper.save(ConstantUtil.SHOW_FLOAT_VIEW, false);
                SPHelper.save(ConstantUtil.IS_SHOW_NOTIFY, false);
                Intent intent = new Intent();
                intent.setClass(PreSettingActivity.this, SettingActivity.class);
                startActivity(intent);
                UrlCountUtil.onEvent(UrlCountUtil.PRE__FLOATVIEW,false);
                SPHelper.save(SHOW,false);
                finish();
            }
        });
    }
}
