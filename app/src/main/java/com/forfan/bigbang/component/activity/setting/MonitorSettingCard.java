package com.forfan.bigbang.component.activity.setting;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;
import com.forfan.bigbang.component.contentProvider.SPHelper;
import com.forfan.bigbang.util.ConstantUtil;


/**
 * Created by penglu on 2015/11/23.
 */
public class MonitorSettingCard extends AbsCard {

    private RelativeLayout onlyTextRL;

    private SwitchCompat onlyTextSwitch;

    private boolean isRunService=true;
    private boolean isForground=true;
    private boolean needConfirm=false;

    private boolean onlyText =false;

    public MonitorSettingCard(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context){
        mContext=context;


        LayoutInflater.from(context).inflate(R.layout.card_monitor_setting,this);

        onlyTextRL = (RelativeLayout) findViewById(R.id.text_only_rl);

        onlyTextSwitch = (SwitchCompat) findViewById(R.id.text_only_switch);

        onlyTextSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton aSwitch, boolean isChecked) {
                onlyText = isChecked;
                SPHelper.save(ConstantUtil.TEXT_ONLY, onlyText);
                if (onlyText) {
                    // TODO: 2016/10/29
                } else {
                    // TODO: 2016/10/29
                }
            }
        });

        onlyTextRL.setOnClickListener(myOnClickListerner);

        refresh();
    }

    private OnClickListener myOnClickListerner=new OnClickListener(){

        @Override
        public void onClick(View v) {
            int id=v.getId();
            switch (id) {
                case R.id.text_only_rl:
                    onlyTextSwitch.setChecked(!onlyTextSwitch.isChecked());
                    break;
                default:
                    break;
            }
        }
    };

    private void refresh(){
        onlyText = SPHelper.getBoolean(ConstantUtil.TEXT_ONLY,false) ;
        onlyTextSwitch.setChecked(onlyText);
    }

}
