package com.forfan.bigbang.component.activity.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;
import com.forfan.bigbang.component.activity.SettingBigBangActivity;
import com.forfan.bigbang.component.contentProvider.SPHelper;
import com.forfan.bigbang.component.service.BigBangMonitorService;
import com.forfan.bigbang.component.service.ListenClipboardService;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.SnackBarUtil;
import com.forfan.bigbang.util.UrlCountUtil;
import com.forfan.bigbang.view.DialogFragment;
import com.forfan.bigbang.view.SimpleDialog;

import static com.forfan.bigbang.util.ConstantUtil.BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED;
import static com.forfan.bigbang.util.ConstantUtil.BROADCAST_CLIPBOARD_LISTEN_SERVICE_MODIFIED;


/**
 * Created by penglu on 2015/11/23.
 */
public class FunctionSettingCard extends AbsCard {
    private static final String IS_LONG_PREESS_TIPS_SHOW = "show_long_pressed_tips";

    private RelativeLayout monitorClipBoardRl;
    private RelativeLayout monitorClickRl;
    private RelativeLayout showFloatViewRL;
    private RelativeLayout remainSymbolRL;

//    private TextView monitorClipBoardTV;
//    private TextView showFloatViewTV;
//    private TextView remainSymbolTV;
    private TextView defaultSettingTV;
    private TextView requestFloatViewTv;

    private SwitchCompat monitorClipBoardSwitch;
    private SwitchCompat monitorClickSwitch;
    private SwitchCompat showFloarViewSwitch;
    private SwitchCompat remainSymbolSwitch;

    private boolean monitorClipBoard =true;
    private boolean monitorClick =true;
    private boolean showFloatView =true;
    private boolean remainSymbol =false;
    private boolean isInFirst = true;
    private boolean isClickFloat = false;

    private Handler handler;

    public FunctionSettingCard(Context context) {
        super(context);
        initView(context);
    }

    @Override
    protected void onDetachedFromWindow() {
        handler.removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
    }

    private void initView(Context context){
        mContext=context;

        handler=new Handler();

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
        requestFloatViewTv= (TextView) findViewById(R.id.show_float_view_request);

        monitorClipBoardSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton aSwitch, boolean isChecked) {
                monitorClipBoard = isChecked;
                SPHelper.save(ConstantUtil.MONITOR_CLIP_BOARD, monitorClipBoard);
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_CLIPBOARD,isChecked);

                if (monitorClipBoard) {
                    mContext.startService(new Intent(context,ListenClipboardService.class));
                }
                mContext.sendBroadcast(new Intent(BROADCAST_CLIPBOARD_LISTEN_SERVICE_MODIFIED));
            }
        });


        monitorClickSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton aSwitch, boolean isChecked) {
                // TODO: 2016/10/29 关闭的时候，应该把MonitorSettingCard隐藏起来
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_ACCESSABILITY,isChecked);

                monitorClick = isChecked;
                sendTencentSettingsBroadcast(isChecked);
                SPHelper.save(ConstantUtil.MONITOR_CLICK, monitorClick);
                if (monitorClick) {
                    mContext.startService(new Intent(context,BigBangMonitorService.class));
                    if (!BigBangMonitorService.isAccessibilitySettingsOn(mContext)) {
                        handler.removeCallbacks(showAccess);
                        handler.postDelayed(showAccess, 1000);
                    }
                }
                mContext.sendBroadcast(new Intent(BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED));

                monitorClickRl.setClickable(false);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        monitorClickRl.setClickable(true);
                    }
                }, 500);
            }
        });

        showFloarViewSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_SHOW_FLOAT_WINDOW,isChecked);

                showFloatView = isChecked;
                SPHelper.save(ConstantUtil.SHOW_FLOAT_VIEW, showFloatView);
                mContext.sendBroadcast(new Intent(BROADCAST_CLIPBOARD_LISTEN_SERVICE_MODIFIED));
                mContext.sendBroadcast(new Intent(BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED));
                if(!SPHelper.getBoolean(IS_LONG_PREESS_TIPS_SHOW,false) && isChecked){
                    if(!isInFirst){
                        showLongClickDialog();
                        SPHelper.save(IS_LONG_PREESS_TIPS_SHOW,true);
                    }

                }
                if (isClickFloat && isChecked){
                    SnackBarUtil.show(buttonView,
                            mContext.getString(R.string.punish_float_problem),
                            mContext.getString(R.string.punish_float_action),
                            new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        Uri packageURI = Uri.parse("package:" +  mContext.getPackageName());
                                        Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);
                                        mContext.startActivity(intent);
                                    }catch (Throwable e){
                                        SnackBarUtil.show(buttonView,R.string.open_setting_failed_diy);
                                    }
                                }
                            });
                }
                isInFirst = false;
                if (showFloatView){
                    requestFloatViewTv.setVisibility(GONE);
                }else {
                    requestFloatViewTv.setVisibility(VISIBLE);
                }
            }
        });

        remainSymbolSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_PUNCTUATION,isChecked);
                remainSymbol = isChecked;
                SPHelper.save(ConstantUtil.REMAIN_SYMBOL, remainSymbol);
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

    Runnable showAccess= new Runnable() {
        @Override
        public void run() {
            if (monitorClick) {

                try {
                    if(!BigBangMonitorService.isAccessibilitySettingsOn(mContext)) {
                        showOpenAccessibilityDialog();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void showLongClickDialog() {
        SimpleDialog.Builder builder=new SimpleDialog.Builder(R.style.SimpleDialogLight){

            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                // 这里是保持开启
                super.onPositiveActionClicked(fragment);
            }
            @Override
            public void onDismiss(DialogInterface dialog) {
                super.onCancel(dialog);
            }
        };
        builder.message("单击悬浮窗切换开关，长按悬浮打开设置页。")
                .positiveAction(mContext.getString(R.string.ok));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(((AppCompatActivity)mContext).getSupportFragmentManager(), null);
    }

    private void showOpenAccessibilityDialog() {
        SimpleDialog.Builder builder=new SimpleDialog.Builder(R.style.SimpleDialogLight){
            private boolean isPositive=false;
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                // 这里是保持开启
                try {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    mContext.startActivity(intent);
                } catch (Throwable e) {
                }
                isPositive=true;
                super.onPositiveActionClicked(fragment);
            }
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!isPositive){
                    monitorClickSwitch.setChecked(false);
                }
                super.onCancel(dialog);
            }
        };
        builder.message("监控单击/长按需要开启辅助功能，请在设置中开启！")
                .positiveAction(mContext.getString(R.string.request_accessibility_confirm))
                .negativeAction(mContext.getString(R.string.cancel));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(((AppCompatActivity)mContext).getSupportFragmentManager(), null);
    }

    private void sendTencentSettingsBroadcast(boolean isChecked) {
        Intent intent = new Intent();
        intent.setAction(ConstantUtil.Setting_content_Changes);
        intent.putExtra(ConstantUtil.SHOW_TENCENT_SETTINGS,isChecked);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
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
                    isClickFloat=true;
                    showFloarViewSwitch.setChecked(!showFloarViewSwitch.isChecked());
                    break;
                case R.id.remain_symbol_rl:
                    remainSymbolSwitch.setChecked(!remainSymbolSwitch.isChecked());
                    break;
                case R.id.default_setting:
                    // TODO: 2016/10/29  恢复默认设置
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
        remainSymbol = SPHelper.getBoolean(ConstantUtil.REMAIN_SYMBOL,true) ;

        monitorClipBoardSwitch.setChecked(monitorClipBoard);
        monitorClickSwitch.setChecked(monitorClick);
        showFloarViewSwitch.setChecked(showFloatView);
        remainSymbolSwitch.setChecked(remainSymbol);
    }

}
