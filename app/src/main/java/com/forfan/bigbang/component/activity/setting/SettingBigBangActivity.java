package com.forfan.bigbang.component.activity.setting;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.widget.Button;
import android.widget.TextView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.view.BigBangLayout;
import com.forfan.bigbang.view.GuideView;

/**
 * Created by penglu on 2016/11/9.
 */

public class SettingBigBangActivity extends BaseActivity {

    private TextView mIntro;
    private TextView mFunctionIntroTV;
    private GuideView guideView;
    private BigBangLayout mBigBangLayout;
    private CardView mBigBangWraper;
    private Button mEnterBtn;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_bigbang);
        handler=new Handler();

        mBigBangLayout= (BigBangLayout) findViewById(R.id.bigbang);

        mBigBangWraper= (CardView) findViewById(R.id.bigbang_wraper);
        String[] txts=new String[]{"BigBang","是","您","的","快捷","助手","，",
                "您","只","需","在","设置","中","开启","BigBang","的","辅助","功能","，",
                "便","可以","在","任意","app","中","对","文字","进行","进行","编辑","，",
                "包括","分词","，","翻译","，","复制","以及","动态","调整","，","希望","您","能","在","日常","生活","中","获得","便利"};
        for (String t:txts) {
            mBigBangLayout.addTextItem(t);
        }
    }

}
