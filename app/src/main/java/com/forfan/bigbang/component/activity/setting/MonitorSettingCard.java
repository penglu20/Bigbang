package com.forfan.bigbang.component.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;
import com.forfan.bigbang.component.contentProvider.SPHelper;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.LogUtil;

import static com.forfan.bigbang.util.ConstantUtil.BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED;


/**
 * Created by penglu on 2015/11/23.
 */
public class MonitorSettingCard extends AbsCard {
    private static final String TAG="MonitorSettingCard";

    public static final int SPINNER_ARRAY=R.array.click_or_long_click;
    private RelativeLayout onlyTextRL;

    private SwitchCompat onlyTextSwitch;

    private Spinner qqSpinner,weixinSpinner,otherSpinner;
    private String qqSelection;
    private String weixinSelection;
    private String otherSelection;

    private boolean onlyText =false;
    private String[] spinnerArray;

    public MonitorSettingCard(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context){
        mContext=context;


        LayoutInflater.from(context).inflate(R.layout.card_monitor_setting,this);
        spinnerArray=context.getResources().getStringArray(SPINNER_ARRAY);

        qqSpinner= (Spinner) findViewById(R.id.qq_spinner);
        weixinSpinner= (Spinner) findViewById(R.id.weixin_spinner);
        otherSpinner= (Spinner) findViewById(R.id.other_spinner);


        onlyTextRL = (RelativeLayout) findViewById(R.id.text_only_rl);
        onlyTextSwitch = (SwitchCompat) findViewById(R.id.text_only_switch);

        onlyTextSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton aSwitch, boolean isChecked) {
                onlyText = isChecked;
                SPHelper.save(ConstantUtil.TEXT_ONLY, onlyText);
                mContext.sendBroadcast(new Intent(BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED));
            }
        });

        qqSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogUtil.d(TAG,"onItemSelected:"+spinnerArray[position]);
                SPHelper.save(ConstantUtil.QQ_SELECTION,spinnerArray[position]);
                mContext.sendBroadcast(new Intent(BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                LogUtil.d(TAG,"onNothingSelected:");

            }
        });


        weixinSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogUtil.d(TAG,"onItemSelected:"+spinnerArray[position]);
                SPHelper.save(ConstantUtil.WEIXIN_SELECTION,spinnerArray[position]);
                mContext.sendBroadcast(new Intent(BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                LogUtil.d(TAG,"onNothingSelected:");

            }
        });


        otherSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogUtil.d(TAG,"onItemSelected:"+spinnerArray[position]);
                SPHelper.save(ConstantUtil.OTHER_SELECTION,spinnerArray[position]);
                mContext.sendBroadcast(new Intent(BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                LogUtil.d(TAG,"onNothingSelected:");

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
        onlyText = SPHelper.getBoolean(ConstantUtil.TEXT_ONLY,true) ;
        qqSelection= SPHelper.getString(ConstantUtil.QQ_SELECTION,spinnerArray[2]);
        weixinSelection = SPHelper.getString(ConstantUtil.WEIXIN_SELECTION,spinnerArray[2]);
        otherSelection= SPHelper.getString(ConstantUtil.OTHER_SELECTION,spinnerArray[2]);

        onlyTextSwitch.setChecked(onlyText);

        qqSpinner.setSelection(spinnerArrayIndex(qqSelection));
        weixinSpinner.setSelection(spinnerArrayIndex(weixinSelection));
        otherSpinner.setSelection(spinnerArrayIndex(otherSelection));


    }

    private int spinnerArrayIndex(String txt){
        int length=spinnerArray.length;
        for (int i=0;i<length;i++){
            if (spinnerArray[i].equals(txt)){
                return i;
            }
        }
        return 2;
    }

}
