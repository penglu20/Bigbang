package com.forfan.bigbang.component.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.forfan.bigbang.util.ConstantUtil;

public class TotalSwitchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendBroadcast(new Intent(ConstantUtil.TOTAL_SWITCH_BROADCAST));
        finish();
        overridePendingTransition(0,0);
        return;
    }
}
