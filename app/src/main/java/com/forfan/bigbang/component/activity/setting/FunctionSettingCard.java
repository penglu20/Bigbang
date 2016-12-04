package com.forfan.bigbang.component.activity.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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
import com.forfan.bigbang.component.activity.OcrActivity;
import com.forfan.bigbang.component.service.BigBangMonitorService;
import com.forfan.bigbang.component.service.ListenClipboardService;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.SnackBarUtil;
import com.forfan.bigbang.util.UrlCountUtil;
import com.forfan.bigbang.util.XposedEnableUtil;
import com.forfan.bigbang.view.DialogFragment;
import com.forfan.bigbang.view.SimpleDialog;
import com.shang.commonjar.contentProvider.SPHelper;

import static com.forfan.bigbang.util.ConstantUtil.BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED;
import static com.forfan.bigbang.util.ConstantUtil.BROADCAST_CLIPBOARD_LISTEN_SERVICE_MODIFIED;


/**
 * Created by penglu on 2015/11/23.
 */
public class FunctionSettingCard extends AbsCard {
    private static final String IS_LONG_PREESS_TIPS_SHOW = "show_long_pressed_tips";

    private RelativeLayout monitorClipBoardRl;
    private RelativeLayout monitorClickRl;
    private RelativeLayout totalSwitchRL;

    //    private TextView monitorClipBoardTV;
//    private TextView showFloatViewTV;
//    private TextView remainSymbolTV;
    private TextView defaultSettingTV;

    private SwitchCompat monitorClipBoardSwitch;
    private SwitchCompat monitorClickSwitch;
    private SwitchCompat totalSwitchSwitch;

    private boolean monitorClipBoard = true;
    private boolean monitorClick = true;
    private boolean totalSwitch = true;
    private boolean isInFirst = true;
    private boolean isClickTotalSwitch = false;

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

    private void initView(Context context) {
        mContext = context;

        handler = new Handler();

        LayoutInflater.from(context).inflate(R.layout.card_function_setting, this);

        monitorClipBoardRl = (RelativeLayout) findViewById(R.id.monitor_clipboard_rl);
        monitorClickRl = (RelativeLayout) findViewById(R.id.monitor_click_rl);
        totalSwitchRL = (RelativeLayout) findViewById(R.id.total_switch_rl);

        monitorClipBoardSwitch = (SwitchCompat) findViewById(R.id.monitor_clipboard_switch);
        monitorClickSwitch = (SwitchCompat) findViewById(R.id.monitor_click_switch);
        totalSwitchSwitch = (SwitchCompat) findViewById(R.id.total_switch_switch);

        findViewById(R.id.orc).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_OPEN_OCR);
                Intent intent = new Intent();
                intent.setClass(mContext, OcrActivity.class);
                mContext.startActivity(intent);
            }
        });
//        monitorClipBoardTV= (TextView) findViewById(R.id.monitor_clipboard_tv);
//        showFloatViewTV= (TextView) findViewById(R.id.show_float_view_tv);
//        remainSymbolTV= (TextView) findViewById(R.id.remain_symbol_tv);
        defaultSettingTV = (TextView) findViewById(R.id.default_setting);

        monitorClipBoardSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton aSwitch, boolean isChecked) {
                monitorClipBoard = isChecked;
                SPHelper.save(ConstantUtil.MONITOR_CLIP_BOARD, monitorClipBoard);
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_CLIPBOARD, isChecked);

                if (monitorClipBoard) {
                    mContext.startService(new Intent(context, ListenClipboardService.class));
                }
                mContext.sendBroadcast(new Intent(BROADCAST_CLIPBOARD_LISTEN_SERVICE_MODIFIED));
            }
        });


        monitorClickSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton aSwitch, boolean isChecked) {
                // TODO: 2016/10/29 关闭的时候，应该把MonitorSettingCard隐藏起来
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_ACCESSABILITY, isChecked);

                monitorClick = isChecked;
                sendTencentSettingsBroadcast(isChecked);
                SPHelper.save(ConstantUtil.MONITOR_CLICK, monitorClick);
                if (monitorClick) {
                    mContext.startService(new Intent(context, BigBangMonitorService.class));
                    if (!BigBangMonitorService.isAccessibilitySettingsOn(mContext) && !XposedEnableUtil.isEnable()) {
                        handler.removeCallbacks(showAccess);
                        handler.postDelayed(showAccess, 2000);
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

        totalSwitchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_TOTAL_SWITCH,isChecked);

                totalSwitch = isChecked;
                SPHelper.save(ConstantUtil.TOTAL_SWITCH, totalSwitch);
                if (isClickTotalSwitch) {
                    if (totalSwitch){
                        SnackBarUtil.show(buttonView, mContext.getString(R.string.bigbang_open));
                        try {
                            mContext.startService(new Intent(mContext,BigBangMonitorService.class));
                            mContext.startService(new Intent(mContext,ListenClipboardService.class));
                        } catch (Throwable e) {
                        }
                    }else{
                        SnackBarUtil.show(buttonView, mContext.getString(R.string.bigbang_close));
                    }
                }
                mContext.sendBroadcast(new Intent(BROADCAST_CLIPBOARD_LISTEN_SERVICE_MODIFIED));
                mContext.sendBroadcast(new Intent(BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED));
//                if (totalSwitch){
//                    requestFloatViewTv.setVisibility(GONE);
//                }else {
//                    requestFloatViewTv.setVisibility(VISIBLE);
//                }
            }
        });


        totalSwitchRL.setOnClickListener(myOnClickListerner);
        monitorClipBoardRl.setOnClickListener(myOnClickListerner);
        monitorClickRl.setOnClickListener(myOnClickListerner);

//        monitorClipBoardTV.setOnClickListener(myOnClickListerner);
//        showFloatViewTV.setOnClickListener(myOnClickListerner);
//        remainSymbolTV.setOnClickListener(myOnClickListerner);
        defaultSettingTV.setOnClickListener(myOnClickListerner);

        refresh();
    }

    Runnable showAccess = new Runnable() {
        @Override
        public void run() {
            if (monitorClick) {

                try {
                    if (!BigBangMonitorService.isAccessibilitySettingsOn(mContext)) {
                        showOpenAccessibilityDialog();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void showLongClickDialog() {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

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
        builder.message(mContext.getString(R.string.float_tips))
                .positiveAction(mContext.getString(R.string.ok));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), null);
    }

    private void showOpenAccessibilityDialog() {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
            private boolean isPositive = false;

            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                // 这里是保持开启
                try {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    mContext.startActivity(intent);
                } catch (Throwable e) {
                }
                isPositive = true;
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!isPositive) {
                    monitorClickSwitch.setChecked(false);
                }
                super.onCancel(dialog);
            }
        };
        builder.message(mContext.getString(R.string.access_open_tips))
                .positiveAction(mContext.getString(R.string.request_accessibility_confirm))
                .negativeAction(mContext.getString(R.string.cancel));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), null);
    }

    private void sendTencentSettingsBroadcast(boolean isChecked) {
        Intent intent = new Intent();
        intent.setAction(ConstantUtil.Setting_content_Changes);
        intent.putExtra(ConstantUtil.SHOW_TENCENT_SETTINGS, isChecked);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    private OnClickListener myOnClickListerner = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.monitor_clipboard_rl:
                    monitorClipBoardSwitch.setChecked(!monitorClipBoardSwitch.isChecked());
                    break;
                case R.id.monitor_click_rl:
                    monitorClickSwitch.setChecked(!monitorClickSwitch.isChecked());
                    break;
                case R.id.total_switch_rl:
                    isClickTotalSwitch =true;
                    totalSwitchSwitch.setChecked(!totalSwitchSwitch.isChecked());
                    break;
                case R.id.default_setting:
                    // TODO: 2016/10/29  恢复默认设置
                    break;
                default:
                    break;
            }
        }
    };


    private void refresh() {
        totalSwitch = SPHelper.getBoolean(ConstantUtil.TOTAL_SWITCH, true);
        monitorClipBoard = SPHelper.getBoolean(ConstantUtil.MONITOR_CLIP_BOARD, true);
        monitorClick = SPHelper.getBoolean(ConstantUtil.MONITOR_CLICK, true);

        monitorClipBoardSwitch.setChecked(monitorClipBoard);
        monitorClickSwitch.setChecked(monitorClick);
        totalSwitchSwitch.setChecked(totalSwitch);
    }

}
