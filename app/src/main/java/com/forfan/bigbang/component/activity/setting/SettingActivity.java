package com.forfan.bigbang.component.activity.setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;
import com.forfan.bigbang.baseCard.CardListAdapter;
import com.forfan.bigbang.baseCard.DividerItemDecoration;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.component.contentProvider.SPHelper;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;

import static com.forfan.bigbang.util.ConstantUtil.BROADCAST_RELOAD_SETTING;


public class SettingActivity extends BaseActivity {

    private static final String TAG="SettingActivity";


    protected RecyclerView cardList;
    protected List<AbsCard> cardViews=new ArrayList<>();
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isChecked = intent.getBooleanExtra(ConstantUtil.SHOW_TENCENT_SETTINGS,true);
            if(isChecked){
                if(!newAdapter.containsView(settingCard))
                   newAdapter.addView(settingCard,  cardViews.size()-1);
            }else {
                if(newAdapter.containsView(settingCard))
                    newAdapter.deleteView(settingCard);
            }
        }
    };
    private MonitorSettingCard settingCard;
    private CardListAdapter newAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        StatusBarCompat.setupStatusBarView(this, (ViewGroup) getWindow().getDecorView(),true,R.color.colorPrimary);

        cardList = (RecyclerView) findViewById(R.id.card_list);

        cardList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));

        settingCard = new MonitorSettingCard(this);
        cardViews.add(new FunctionSettingCard(this));
        if (SPHelper.getBoolean(ConstantUtil.MONITOR_CLICK,true)) {
            cardViews.add(settingCard);
        }
        cardViews.add(new FeedBackAndUpdateCard(this));


        newAdapter = new CardListAdapter(this, false);
        newAdapter.setCardViews(cardViews);
        cardList.setItemAnimator(new SlideInRightAnimator());
        cardList.setAdapter(newAdapter);

//        Observable.timer(3, TimeUnit.SECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .flatMap(new Func1<Long,Observable<String>>(){
//                    @Override
//                    public Observable<String> call(Long aLong){
//                        return Observable.just("");
//                    }
//                })
//                .subscribe(s -> {
//                    if (s.equals("")){
//                        boolean hasShared=SPHelper.getBoolean(ConstantUtil.HAD_SHARED,false);
//                        //// TODO: 2016/11/1 第一期先不上分享功能了
//                        // TODO: 2016/10/31 如果用户选择不分享，应该短期内不再显示
//                        if (!hasShared){
//                            newAdapter.addView(new ShareCard(this),0);
//                        }
//                    }
//                });

      initLocalBroadcast();

    }

    private void initLocalBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstantUtil.Setting_content_Changes);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,intentFilter);
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
