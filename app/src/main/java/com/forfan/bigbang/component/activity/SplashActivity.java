package com.forfan.bigbang.component.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.activity.setting.SettingActivity;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.util.ConstantUtil;
import com.shang.commonjar.contentProvider.SPHelper;
import com.umeng.onlineconfig.OnlineConfigAgent;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class SplashActivity extends BaseActivity {

    private static final String KEY = "key";
    private static final java.lang.String GOTO_HOME = "go_home";
    private static final java.lang.String GOTO_INTRO = "go_intro";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        if (intent.getAction().equals(ConstantUtil.NOTIFY_SCREEN_CAPTURE_OVER_BROADCAST)){
            finish();
            sendBroadcast(new Intent(ConstantUtil.SCREEN_CAPTURE_OVER_BROADCAST));
        }else if(intent.getAction().equals(ConstantUtil.NOTIFY_UNIVERSAL_COPY_BROADCAST)){
            finish();
            sendBroadcast(new Intent(ConstantUtil.UNIVERSAL_COPY_BROADCAST));
        }

        setContentView(R.layout.activity_splash);
        OnlineConfigAgent.getInstance().updateOnlineConfig(getApplicationContext());
        setUpSplash();
    }
    private void setUpSplash(){
        Observable.timer(2, TimeUnit.SECONDS)
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<Long,Observable<String>>(){
                    @Override
                    public Observable<String> call(Long aLong){
                        boolean isShowIntro = SPHelper.getBoolean(KEY, false);
                        if (isShowIntro)
                            return Observable.just(GOTO_HOME);
                        else
                            return Observable.just(GOTO_INTRO);
                    }
                })
                .subscribe(s -> {
                    if (s.equals(GOTO_HOME)){
                        startActivity(new Intent(SplashActivity.this, SettingActivity.class));
                        finish();
                    }else{
                        startActivity(new Intent(SplashActivity.this, IntroActivity.class));
                        SPHelper.save(KEY, true);
                        finish();
                    }
                });
    }
}
