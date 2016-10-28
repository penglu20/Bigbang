package com.forfan.bigbang.component.activity;

import android.content.Intent;
import android.support.v4.app.INotificationSideChannel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.forfan.bigbang.R;
import com.forfan.bigbang.util.UpdateUtil;
import com.qihoo.updatesdk.lib.UpdateTipDialogActivity;
import com.umeng.fb.ConversationActivity;
import com.umeng.fb.FeedbackAgent;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
    }

    private void initView() {
        findViewById(R.id.converstation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.umeng.fb.util.Res.setPackageName(R.class.getPackage().getName());
                FeedbackAgent agent = new FeedbackAgent(HomeActivity.this);
                agent.startFeedbackActivity();
            }
        });
        findViewById(R.id.upgrade).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateUtil.UserCheckUpdate(v);
            }
        });
        findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }
}
