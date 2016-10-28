package com.forfan.bigbang.component.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.component.service.BigBangMonitorService;
import com.forfan.bigbang.util.LogUtil;


public class SettingActivity extends BaseActivity {

    private static final String TAG="SettingActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (!BigBangMonitorService.isAccessibilitySettingsOn(this)) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        }
    }

    public void start(View view){
        Intent intent = new Intent(this,BigBangActivity.class);
        startActivity(intent);
    }


}
