package com.forfan.bigbang.component.activity;

import android.animation.Animator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.activity.setting.SettingActivity;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.util.ClipboardUtils;
import com.forfan.bigbang.util.SnackBarUtil;
import com.forfan.bigbang.util.ToastUtil;
import com.forfan.bigbang.view.BigBangLayout;
import com.forfan.bigbang.view.GuideView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class IntroActivity extends BaseActivity {

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
        setContentView(R.layout.activity_intro);
        handler=new Handler();
        initView();
        showClickIntro();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showClickIntro() {

        TextView tv = new TextView(this);
        tv.setText(R.string.try_long_click_text);
        tv.setTextColor(getResources().getColor(R.color.white));
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.hand_down);
        Animation animation = AnimationUtils.loadAnimation(IntroActivity.this,R.anim.click_here_anim);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imageView.startAnimation(animation);
                    }
                }, 500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        guideView = GuideView.Builder
                .newInstance(this)
                .setTargetView(mIntro)//设置目标
                .setCustomGuideView(tv)
                .setCenterView(imageView)
                .setDirction(GuideView.Direction.BOTTOM)
                .setShape(GuideView.MyShape.CIRCULAR)   // 设置圆形显示区域，
                .setOffset(0,mIntro.getMeasuredHeight()+100)
                .setBgColor(getResources().getColor(R.color.shadow))
                .setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        animation.cancel();
                        guideView.hide();
                        mIntro.setVisibility(View.GONE);
                        mBigBangWraper.setVisibility(View.VISIBLE);
                        mBigBangWraper.setScaleX(0);
                        mBigBangWraper.setScaleY(0);
                        mBigBangWraper.animate().scaleY(1).scaleX(1)
                                .setInterpolator(new AnticipateOvershootInterpolator())
                                .setDuration(200)
                                .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        showBigBangIntro();
                                    }
                                }, 300);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }).start();

                    }
                })
                .setOnViewAddedListener(view -> {
                    view.setAnimation(animation);
                    animation.start();
                })
                .build();
        guideView.setClickable(false);
        guideView.setLongClickable(false);
        guideView.setFocusable(false);
        guideView.show();
    }

    private void initView() {
        mIntro = (TextView)findViewById(R.id.intro);
        mFunctionIntroTV= (TextView) findViewById(R.id.enter_bigbang_intro);
        mBigBangLayout= (BigBangLayout) findViewById(R.id.bigbang);
        mBigBangLayout.setActionListener(bigBangActionListener);

        mBigBangWraper= (CardView) findViewById(R.id.bigbang_wraper);
        String[] txts=new String[]{"BigBang","是","您","的","快捷","助手","，",
                "您","只","需","在","设置","中","开启","BigBang","的","辅助","功能","，",
                "便","可以","在","任意","app","中","对","文字","进行","进行","编辑","，",
                "包括","分词","，","翻译","，","复制","以及","动态","调整","，","希望","您","能","在","日常","生活","中","获得","便利"};
        for (String t:txts) {
            mBigBangLayout.addTextItem(t);
        }

        mIntro.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                guideView.performClick();
                return true;
            }
        });
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

    private void showBigBangIntro() {
        TextView tv = new TextView(this);
        tv.setText(R.string.try_click_text);
        tv.setTextColor(getResources().getColor(R.color.white));

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.hand_swipe);
        Animation animation = AnimationUtils.loadAnimation(IntroActivity.this,R.anim.swipe_here_anim);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imageView.startAnimation(animation);
                    }
                }, 500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        guideView = GuideView.Builder
                .newInstance(this)
                .setTargetView(mBigBangWraper)//设置目标
                .setCustomGuideView(tv)
                .setCenterView(imageView)
                .setDirction(GuideView.Direction.BOTTOM)
                .setShape(GuideView.MyShape.RECTANGULAR)   // 设置圆形显示区域，
                .setRadius(5)
                .setOffset(0,mBigBangWraper.getMeasuredHeight()/2+100)
                .setBgColor(getResources().getColor(R.color.shadow))
                .setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        animation.cancel();
                        guideView.hide();
                        mEnterBtn.setVisibility(View.VISIBLE);
                        mEnterBtn.setScaleY(0);
                        mEnterBtn.setScaleX(0);
                        mEnterBtn.setAlpha(0);
                        mEnterBtn.animate().scaleX(1).scaleY(1).alpha(1)
                                .setInterpolator(new AnticipateOvershootInterpolator())
                                .setStartDelay(500)
                                .start();
                        mFunctionIntroTV.setVisibility(View.VISIBLE);
                    }
                })
                .setOnViewAddedListener(view -> {
                    view.setAnimation(animation);
                    animation.start();
                })
                .build();
        guideView.setClickable(false);
        guideView.setLongClickable(false);
        guideView.setFocusable(false);
        guideView.show();
    }

    BigBangLayout.ActionListener bigBangActionListener=new BigBangLayout.ActionListener() {

        private boolean firstSelected=true,firstSearch=true,firstShare=true,firstCopy=true,firstTrans=true,firstDrag=true;
        @Override
        public void onSelected(String text) {
            if (firstSelected){
                guideView.performClick();
                firstSelected=false;
            }
        }

        @Override
        public void onSearch(String text) {
            if (firstSearch){
                mFunctionIntroTV.setScaleY(0);
                mFunctionIntroTV.setScaleX(0);
                mFunctionIntroTV.setText(R.string.search_mode_help);
                mFunctionIntroTV.animate().scaleY(1).scaleX(1).start();
            }else {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.baidu.com/s?wd=" + URLEncoder.encode(text, "utf-8")));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }


        }

        @Override
        public void onShare(String text) {
            if (firstShare){
                mFunctionIntroTV.setScaleY(0);
                mFunctionIntroTV.setScaleX(0);
                mFunctionIntroTV.setText(R.string.share_mode_help);
                mFunctionIntroTV.animate().scaleY(1).scaleX(1).start();
            }else {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(sharingIntent);

            }

        }

        @Override
        public void onCopy(String text) {
            if (firstCopy){
                mFunctionIntroTV.setScaleY(0);
                mFunctionIntroTV.setScaleX(0);
                mFunctionIntroTV.setText(R.string.copy_mode_help);
                mFunctionIntroTV.animate().scaleY(1).scaleX(1).start();
            }else {
                if (!TextUtils.isEmpty(text)) {
                    ClipboardUtils.setText(getApplicationContext(), text);
                    ToastUtil.show(R.string.copyed);
                }
            }


        }

        @Override
        public void onTrans(String text) {
            if (firstTrans){
                mFunctionIntroTV.setScaleY(0);
                mFunctionIntroTV.setScaleX(0);
                mFunctionIntroTV.setText(R.string.translate_mode_help);
                mFunctionIntroTV.animate().scaleY(1).scaleX(1).start();
            }else {
                if (!TextUtils.isEmpty(text)) {
                    SnackBarUtil.show(mIntro, R.string.open_bang_for_translate);
                }
            }
        }

        @Override
        public void onDrag() {
            if (firstDrag){
                mFunctionIntroTV.setText(R.string.sort_mode_help);
            }else {
                mFunctionIntroTV.setText(R.string.choose_sentences_mode);
            }
            firstDrag=!firstDrag;
            mFunctionIntroTV.setScaleY(0);
            mFunctionIntroTV.setScaleX(0);
            mFunctionIntroTV.animate().scaleY(1).scaleX(1).start();
        }
    };
    @Override
    public void onBackPressed() {
        if(guideView != null && guideView.isShown()){
            guideView.hide();
        }
        super.onBackPressed();
    }
}
