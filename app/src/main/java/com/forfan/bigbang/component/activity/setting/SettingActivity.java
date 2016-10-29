package com.forfan.bigbang.component.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;
import com.forfan.bigbang.baseCard.CardListAdapter;
import com.forfan.bigbang.baseCard.DividerItemDecoration;
import com.forfan.bigbang.component.activity.BigBangActivity;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.component.contentProvider.SPHelper;
import com.forfan.bigbang.component.service.BigBangMonitorService;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;


public class SettingActivity extends BaseActivity {

    private static final String TAG="SettingActivity";


    protected RecyclerView cardList;
    protected List<AbsCard> cardViews=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        cardList = (RecyclerView) findViewById(R.id.card_list);

        cardList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));

        cardViews.add(new ShareCard(this));
        cardViews.add(new FunctionSettingCard(this));
        cardViews.add(new MonitorSettingCard(this));
        cardViews.add(new FeedBackAndUpdateCard(this));


        CardListAdapter newAdapter = new CardListAdapter(this, false);
        newAdapter.setCardViews(cardViews);
        cardList.setItemAnimator(new FadeInAnimator());
        cardList.setAdapter(newAdapter);



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
