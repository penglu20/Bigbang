package com.forfan.bigbang.component.activity.setting;

import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;
import com.forfan.bigbang.component.contentProvider.SPHelper;
import com.forfan.bigbang.util.ConstantUtil;


/**
 * Created by penglu on 2015/11/23.
 */
public class FunctionSettingCard extends AbsCard {

    private RelativeLayout monitorClipBoardRl;
    private RelativeLayout monitorClickRl;
    private RelativeLayout showFloatViewRL;
    private RelativeLayout remainSymbolRL;

//    private TextView monitorClipBoardTV;
//    private TextView showFloatViewTV;
//    private TextView remainSymbolTV;
    private TextView defaultSettingTV;

    private SwitchCompat monitorClipBoardSwitch;
    private SwitchCompat monitorClickSwitch;
    private SwitchCompat showFloarViewSwitch;
    private SwitchCompat remainSymbolSwitch;

    private boolean monitorClipBoard =true;
    private boolean monitorClick =true;
    private boolean showFloatView =true;
    private boolean remainSymbol =false;

    private BottomSheetDialog mBottomSheetDialog;

    public FunctionSettingCard(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context){
        mContext=context;


        LayoutInflater.from(context).inflate(R.layout.card_function_setting,this);

        monitorClipBoardRl = (RelativeLayout) findViewById(R.id.monitor_clipboard_rl);
        monitorClickRl = (RelativeLayout) findViewById(R.id.monitor_click_rl);
        showFloatViewRL = (RelativeLayout) findViewById(R.id.show_float_view_rl);
        remainSymbolRL = (RelativeLayout) findViewById(R.id.remain_symbol_rl);

        monitorClipBoardSwitch = (SwitchCompat) findViewById(R.id.monitor_clipboard_switch);
        monitorClickSwitch = (SwitchCompat) findViewById(R.id.monitor_click_switch);
        showFloarViewSwitch = (SwitchCompat) findViewById(R.id.show_float_view_switch);
        remainSymbolSwitch = (SwitchCompat) findViewById(R.id.remain_symbol_switch);


//        monitorClipBoardTV= (TextView) findViewById(R.id.monitor_clipboard_tv);
//        showFloatViewTV= (TextView) findViewById(R.id.show_float_view_tv);
//        remainSymbolTV= (TextView) findViewById(R.id.remain_symbol_tv);
        defaultSettingTV= (TextView) findViewById(R.id.default_setting);

        monitorClipBoardSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton aSwitch, boolean isChecked) {
                monitorClipBoard = isChecked;
                SPHelper.save(ConstantUtil.MONITOR_CLIP_BOARD, monitorClipBoard);
                if (monitorClipBoard) {
                    // TODO: 2016/10/29
                } else {
                    // TODO: 2016/10/29
                }
            }
        });


        monitorClickSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton aSwitch, boolean isChecked) {
                monitorClick = isChecked;
                SPHelper.save(ConstantUtil.MONITOR_CLICK, monitorClick);
                if (monitorClick) {
                    // TODO: 2016/10/29
                } else {
                    // TODO: 2016/10/29
                }
            }
        });

        showFloarViewSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showFloatView = isChecked;
                SPHelper.save(ConstantUtil.SHOW_FLOAT_VIEW, showFloatView);
                if (showFloatView) {
                    // TODO: 2016/10/29
                } else {
                    // TODO: 2016/10/29
                }
            }
        });

        remainSymbolSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                remainSymbol = isChecked;
                SPHelper.save(ConstantUtil.REMAIN_SYMBOL, remainSymbol);
                if (remainSymbol) {
                    // TODO: 2016/10/29
                } else {
                    // TODO: 2016/10/29
                }
            }
        });


        monitorClipBoardRl.setOnClickListener(myOnClickListerner);
        showFloatViewRL.setOnClickListener(myOnClickListerner);
        remainSymbolRL.setOnClickListener(myOnClickListerner);
        monitorClickRl.setOnClickListener(myOnClickListerner);

//        monitorClipBoardTV.setOnClickListener(myOnClickListerner);
//        showFloatViewTV.setOnClickListener(myOnClickListerner);
//        remainSymbolTV.setOnClickListener(myOnClickListerner);
        defaultSettingTV.setOnClickListener(myOnClickListerner);

        refresh();
    }

    private OnClickListener myOnClickListerner=new OnClickListener(){

        @Override
        public void onClick(View v) {
            int id=v.getId();
            switch (id) {
                case R.id.monitor_clipboard_rl:
                    monitorClipBoardSwitch.setChecked(!monitorClipBoardSwitch.isChecked());
                    break;
                case R.id.monitor_click_rl:
                    monitorClickSwitch.setChecked(!monitorClickSwitch.isChecked());
                    break;
                case R.id.show_float_view_rl:
                    showFloarViewSwitch.setChecked(!showFloarViewSwitch.isChecked());
                    break;
                case R.id.remain_symbol_rl:
                    remainSymbolSwitch.setChecked(!remainSymbolSwitch.isChecked());
                    break;
                case R.id.default_setting:
                    // TODO: 2016/10/29  
                    break;
                default:
                    break;
            }
        }
    };


    private void refresh(){
        monitorClipBoard = SPHelper.getBoolean(ConstantUtil.MONITOR_CLIP_BOARD,true);
        monitorClick = SPHelper.getBoolean(ConstantUtil.MONITOR_CLICK,true);
        showFloatView = SPHelper.getBoolean(ConstantUtil.SHOW_FLOAT_VIEW,true);
        remainSymbol = SPHelper.getBoolean(ConstantUtil.REMAIN_SYMBOL,false) ;

        monitorClipBoardSwitch.setChecked(monitorClipBoard);
        monitorClickSwitch.setChecked(monitorClick);
        showFloarViewSwitch.setChecked(showFloatView);
        remainSymbolSwitch.setChecked(remainSymbol);
    }

}
