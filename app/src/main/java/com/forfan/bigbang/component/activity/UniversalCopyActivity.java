package com.forfan.bigbang.component.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.UrlCountUtil;

public class UniversalCopyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendBroadcast(new Intent(ConstantUtil.UNIVERSAL_COPY_BROADCAST_DELAY));
        finish();
        overridePendingTransition(0,0);
        return;
    }
}
