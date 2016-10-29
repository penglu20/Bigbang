package com.forfan.bigbang.component.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.activity.setting.SettingActivity;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.network.RetrofitHelper;
import com.forfan.bigbang.util.ClipboardUtils;
import com.forfan.bigbang.util.LogUtil;
import com.forfan.bigbang.util.SnackBarUtil;
import com.forfan.bigbang.util.ToastUtil;
import com.forfan.bigbang.view.BigBangLayout;
import com.forfan.bigbang.view.GuideView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IntroActivity extends BaseActivity {

    private TextView mIntro;
    private GuideView guideView;
    private BigBangLayout mBigBangLayout;
    private Button mEnterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        initView();
        showIntro();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showIntro() {

        TextView tv = new TextView(this);
        tv.setText("请试着点击上方文字");
        tv.setTextColor(getResources().getColor(R.color.white));

//        GuideView.Builder
//                .newInstance(this)      // 必须调用
//                .setTargetView(mIntro)    // 必须调用，设置需要Guide的View
//                .setCustomTipsView(iv)  // 必须调用，设置GuideView，可以使任意View的实例，比如ImageView 或者TextView
//                .setDirction(GuideView.Direction.LEFT_BOTTOM)   // 设置GuideView 相对于TargetView的位置，有八种，不设置则默认在屏幕左上角,其余的可以显示在右上，右下等等
//                .setShape(GuideView.MyShape.RECTANGULAR)   // 设置显示形状，支持圆形，椭圆，矩形三种样式，矩形可以是圆角矩形，
//                .setBackGround(getResources().getColor(R.color.shadow)) // 设置背景颜色，默认透明
//                .setOnclickExit(null)   // 设置点击消失，可以传入一个Callback，执行被点击后的操作
//                .setRadius(32)          // 设置圆形或矩形透明区域半径，默认是targetView的显示矩形的半径，如果是矩形，这里是设置矩形圆角大小
//                .setCenter(300, 300)    // 设置圆心，默认是targetView的中心
//                .setOffset(200, 60)     // 设置偏移，一般用于微调GuideView的位置
//                .showOnce()             // 设置首次显示，设置后，显示一次后，不再显示
//                .build()                // 必须调用，Buider模式，返回GuideView实例
//                .show();                // 必须调用，显示GuideView
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.click);
        guideView = GuideView.Builder
                .newInstance(this)
                .setTargetView(mIntro)//设置目标
                .setCustomGuideView(tv)
                .setCenterView(imageView)
                .setDirction(GuideView.Direction.BOTTOM)
                .setShape(GuideView.MyShape.CIRCULAR)   // 设置圆形显示区域，
                .setOffset(0,100)
                .setBgColor(getResources().getColor(R.color.shadow))
                .setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        guideView.hide();
                        mIntro.performClick();
                    }
                })
                .build();
        guideView.show();
    }

    private void initView() {
        mIntro = (TextView)findViewById(R.id.intro);
        mIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBigBangLayout.setVisibility(View.VISIBLE);
                showBigBangInto();
            }
        });

        mBigBangLayout= (BigBangLayout) findViewById(R.id.bigbang);
        mBigBangLayout.setActionListener(bigBangActionListener);
        String[] txts=new String[]{"BigBang","是","您","的","快捷","助手","，",
                "您","只","需","在","设置","中","开启","BigBang","的","辅助","功能","，",
                "便","可以","在","任意","app","中","对","文字","进行","进行","编辑","，",
                "包括","分词","，","翻译","，","复制","以及","动态","调整","，","希望","您","能","在","日常","生活","中","获得","便利"};
        for (String t:txts) {
            mBigBangLayout.addTextItem(t);
        }

        mEnterBtn = (Button)findViewById(R.id.enter_bigbang);
        mEnterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(IntroActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showBigBangInto() {
        TextView tv = new TextView(this);
        tv.setText("请试着点击或滑动上方文字\n 我知道了");
        tv.setTextColor(getResources().getColor(R.color.white));

        guideView = GuideView.Builder
                .newInstance(this)
                .setTargetView(mBigBangLayout)//设置目标
                .setCustomGuideView(tv)
               // .setCenterView(tv)
                .setDirction(GuideView.Direction.BOTTOM)
                .setShape(GuideView.MyShape.RECTANGULAR)   // 设置圆形显示区域，
                .setRadius(5)
                .setOffset(0,550)
                .setBgColor(getResources().getColor(R.color.black))
                .setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        guideView.hide();
                        mEnterBtn.setVisibility(View.VISIBLE);
                    }
                })
                .build();
        guideView.show();
    }

    BigBangLayout.ActionListener bigBangActionListener=new BigBangLayout.ActionListener() {

        @Override
        public void onSearch(String text) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.baidu.com/s?wd=" + URLEncoder.encode(text, "utf-8")));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onShare(String text) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(sharingIntent);
        }

        @Override
        public void onCopy(String text) {
            if (!TextUtils.isEmpty(text)) {
                ClipboardUtils.setText(getApplicationContext(),text);
                ToastUtil.show("已复制");
            }
        }

        @Override
        public void onTrans(String text) {
            if (!TextUtils.isEmpty(text)) {
                SnackBarUtil.show(mIntro,"翻译功能开启BigBang即可体验");
            }
        }
    };
}
