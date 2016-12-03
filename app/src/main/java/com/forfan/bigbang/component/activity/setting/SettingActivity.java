package com.forfan.bigbang.component.activity.setting;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;
import com.forfan.bigbang.baseCard.CardListAdapter;
import com.forfan.bigbang.baseCard.DividerItemDecoration;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.util.ChanelUtil;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.UpdateUtil;
import com.forfan.bigbang.util.XposedEnableUtil;
import com.shang.commonjar.contentProvider.SPHelper;
import com.shang.utils.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

import static com.forfan.bigbang.util.ConstantUtil.BROADCAST_RELOAD_SETTING;


public class SettingActivity extends BaseActivity {

    private static final String TAG = "SettingActivity";


    protected RecyclerView cardList;
    protected List<AbsCard> cardViews = new ArrayList<>();
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isChecked = intent.getBooleanExtra(ConstantUtil.SHOW_TENCENT_SETTINGS, true);
            if (isChecked) {
                int index = 0;
                if (!newAdapter.containsView(settingCard))
                    index = cardViews.size() - 4;
                if (XposedEnableUtil.isEnable())
                    index = index - 1;
                newAdapter.addView(settingCard, index);
            } else {
                if (newAdapter.containsView(settingCard))
                    newAdapter.deleteView(settingCard);
            }
        }
    };
    private MonitorSettingCard settingCard;
    private CardListAdapter newAdapter;
    private MediaProjectionManager mMediaProjectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        StatusBarCompat.setupStatusBarView(this, (ViewGroup) getWindow().getDecorView(), true, R.color.colorPrimary);

        cardList = (RecyclerView) findViewById(R.id.card_list);

        cardList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        cardViews.add(new FunctionSettingCard(this));
        settingCard = new MonitorSettingCard(this);
        //  cardViews.add(new OcrCard(this));
        if (XposedEnableUtil.isEnable()) {
            cardViews.add(new XposedCard(this));
        }
        cardViews.add(new FloatAndNotifySettingCard(this));
        cardViews.add(new BigBangSettingCard(this));
        if (SPHelper.getBoolean(ConstantUtil.MONITOR_CLICK, true)) {
            cardViews.add(settingCard);
        }
        cardViews.add(new FeedBackAndUpdateCard(this));
        cardViews.add(new AboutCard(this));


        newAdapter = new CardListAdapter(this, false);
        newAdapter.setCardViews(cardViews);
        cardList.setItemAnimator(new SlideInRightAnimator());
        cardList.setAdapter(newAdapter);

        Observable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<Long, Observable<String>>() {
                    @Override
                    public Observable<String> call(Long aLong) {
                        return Observable.just("");
                    }
                })
                .subscribe(s -> {
                    if (s.equals("")) {
                        boolean hasShared = SPHelper.getBoolean(ConstantUtil.HAD_SHARED, false);
                        int openTimes = SPHelper.getInt(ConstantUtil.SETTING_OPEN_TIMES, 0);

                        //// TODO: 2016/11/1 第一期先不上分享功能了
                        // TODO: 2016/10/31 如果用户选择不分享，应该短期内不再显示
                        if (!hasShared && openTimes >= 3 && openTimes % 3 == 0) {
                            newAdapter.addView(new ShareCard(this), 0);
                        }
                    }
                });


        Observable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<Long, Observable<String>>() {
                    @Override
                    public Observable<String> call(Long aLong) {
                        return Observable.just("");
                    }
                })
                .subscribe(s -> {
                    if (s.equals("")) {
                        try {
                            if (!ChanelUtil.isXposedApk(getApplicationContext())) {
                                UpdateUtil.autoCheckUpdate();
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                });

        initLocalBroadcast();
        checkPermission();
        int openTimes = SPHelper.getInt(ConstantUtil.SETTING_OPEN_TIMES, 0);
        SPHelper.save(ConstantUtil.SETTING_OPEN_TIMES, openTimes + 1);
    }


    private void checkPermission() {
        checkPermission(new CheckPermListener() {
                            @Override
                            public void superPermission() {
                            }
                        }, R.string.ask_again,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void initLocalBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstantUtil.Setting_content_Changes);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sendBroadcast(new Intent(BROADCAST_RELOAD_SETTING));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
