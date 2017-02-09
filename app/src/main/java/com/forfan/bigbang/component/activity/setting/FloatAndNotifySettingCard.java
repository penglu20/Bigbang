package com.forfan.bigbang.component.activity.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;
import com.forfan.bigbang.component.activity.SettingFloatViewActivity;
import com.forfan.bigbang.component.activity.floatviewwhitelist.FloatViewWhiteListActivity;
import com.forfan.bigbang.component.activity.howtouse.HowToUseActivity;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.NotificationCheckUtil;
import com.forfan.bigbang.util.SnackBarUtil;
import com.forfan.bigbang.util.ToastUtil;
import com.forfan.bigbang.util.UrlCountUtil;
import com.forfan.bigbang.view.DialogFragment;
import com.forfan.bigbang.view.HintTextView;
import com.forfan.bigbang.view.SimpleDialog;
import com.shang.commonjar.contentProvider.SPHelper;

import static com.forfan.bigbang.util.ConstantUtil.BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED;
import static com.forfan.bigbang.util.ConstantUtil.BROADCAST_CLIPBOARD_LISTEN_SERVICE_MODIFIED;


/**
 * Created by penglu on 2015/11/23.
 */
public class FloatAndNotifySettingCard extends AbsCard {
    private static final String IS_LONG_PREESS_TIPS_SHOW = "show_long_pressed_tips";

    private RelativeLayout showFloatViewRL;
    private RelativeLayout showNotifyRL;
    private RelativeLayout longPressRL;

    private HintTextView showFloatViewTV;
    private HintTextView showNotifyTV;

    private SwitchCompat showFloarViewSwitch;
    private SwitchCompat showNotifySwitch;

    private boolean showFloatView = true;
    private boolean showNotify = false;
    private boolean isInFirst = true;
    private boolean isClickFloat = false, isClickNotify = false;

    private Handler handler;

    public FloatAndNotifySettingCard(Context context) {
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

        LayoutInflater.from(context).inflate(R.layout.card_float_notify_setting, this);

        showFloatViewRL = (RelativeLayout) findViewById(R.id.show_float_view_rl);
        showFloarViewSwitch = (SwitchCompat) findViewById(R.id.show_float_view_switch);
        showFloatViewTV = (HintTextView) findViewById(R.id.show_float_view_tv);

        showNotifyRL = (RelativeLayout) findViewById(R.id.show_notify_rl);
        showNotifySwitch = (SwitchCompat) findViewById(R.id.show_notify_switch);
        showNotifyTV = (HintTextView) findViewById(R.id.show_notify_tv);

        longPressRL = (RelativeLayout) findViewById(R.id.long_press_rl);

//        requestFloatViewTv= (TextView) findViewById(R.id.show_float_view_request);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            longPressRL.setVisibility(GONE);
        }
        showFloarViewSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_SHOW_FLOAT_WINDOW, isChecked);

                showFloatView = isChecked;
                SPHelper.save(ConstantUtil.SHOW_FLOAT_VIEW, showFloatView);
                mContext.sendBroadcast(new Intent(BROADCAST_CLIPBOARD_LISTEN_SERVICE_MODIFIED));
                mContext.sendBroadcast(new Intent(BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED));
//                if(!SPHelper.getBoolean(IS_LONG_PREESS_TIPS_SHOW,false) && isChecked){
//                    if(!isInFirst){
//                        showLongClickDialog();
//                        SPHelper.save(IS_LONG_PREESS_TIPS_SHOW,true);
//                    }
//
//                }

                if (isClickFloat && isChecked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(mContext)) {
                        SnackBarUtil.show(buttonView,
                                mContext.getString(R.string.punish_float_problem),
                                mContext.getString(R.string.punish_float_action),
                                new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
//                                        Uri packageURI = Uri.parse("package:" +  mContext.getPackageName());
//                                        Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);
//                                        mContext.startActivity(intent);

                                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                                    Uri.parse("package:" + mContext.getPackageName()));
                                            mContext.startActivity(intent);

                                        } catch (Throwable e) {
                                            SnackBarUtil.show(buttonView, R.string.open_setting_failed_diy);
                                        }
                                    }
                                });
                    } else {
                        SnackBarUtil.show(buttonView, mContext.getString(R.string.punish_float_problem));
                    }
                }
                isInFirst = false;
                showFloatViewTV.setShowHint(!showFloatView);
            }
        });

        showNotifySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_SHOW_NOTIFY, isChecked);

                showNotify = isChecked;
                SPHelper.save(ConstantUtil.IS_SHOW_NOTIFY, showNotify);
                mContext.sendBroadcast(new Intent(BROADCAST_CLIPBOARD_LISTEN_SERVICE_MODIFIED));
                showNotifyTV.setShowHint(!showNotify);
                if (isClickNotify && isChecked) {
                    if (!NotificationCheckUtil.areNotificationsEnabled(mContext.getApplicationContext())) {
                        SnackBarUtil.show(buttonView,
                                mContext.getString(R.string.notify_enable),
                                mContext.getString(R.string.notify_disabled_title),
                                new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            Intent intent = new Intent();
                                            intent.setClassName("com.android.settings", "com.android.settings.Settings$AppNotificationSettingsActivity");
                                            intent.putExtra("app_package", mContext.getPackageName());
                                            intent.putExtra("app_uid", mContext.getApplicationInfo().uid);
                                            mContext.startActivity(intent);
                                        } catch (Throwable e) {
                                            SnackBarUtil.show(buttonView, R.string.open_setting_failed_diy);
                                        }
                                    }
                                });
                    } else {
                        SnackBarUtil.show(buttonView, mContext.getString(R.string.notify_enable));
                    }
                }
            }
        });


        findViewById(R.id.setting_floatview).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_SET_STYLE_BIGBANG);
                Intent intent = new Intent(mContext, SettingFloatViewActivity.class);
                mContext.startActivity(intent);
            }
        });

        findViewById(R.id.float_white_list_rl).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_FLOAT_WHITE_LIST);
                Intent intent = new Intent(mContext, FloatViewWhiteListActivity.class);
                mContext.startActivity(intent);
            }
        });

        findViewById(R.id.open_from_outside_rl).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_OPEN_FROM_OUTSIDE);
                Intent intent = new Intent(mContext, HowToUseActivity.class);
                intent.putExtra(HowToUseActivity.GO_TO_OPEN_FROM_OUTER,true);
                mContext.startActivity(intent);
            }
        });

        longPressRL.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showLongPressDialog(v);
            }
        });

        showFloatViewRL.setOnClickListener(myOnClickListerner);
        showNotifyRL.setOnClickListener(myOnClickListerner);
        refresh();
    }


    private void showLongPressDialog(View view) {
        String[] longpress = mContext.getResources().getStringArray(R.array.long_press_key);
        int index = SPHelper.getInt(ConstantUtil.LONG_PRESS_KEY_INDEX, 0);

        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                // 这里是保持开启
                super.onPositiveActionClicked(fragment);
                int index = getSelectedIndex();
                SPHelper.save(ConstantUtil.LONG_PRESS_KEY_INDEX, index);
                mContext.sendBroadcast(new Intent(BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED));
                if (index == 2) {
                    SnackBarUtil.show(view, "", R.string.long_press_toast, new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent(Settings.ACTION_VOICE_INPUT_SETTINGS);
                                mContext.startActivity(intent);
                            } catch (Throwable e) {
                                SnackBarUtil.show(v, R.string.open_setting_failed_diy);
                            }
                        }
                    });
                }
            }

            @Override
            public void onDismiss(DialogInterface dialog) {
                super.onCancel(dialog);
            }
        };
        builder.items(longpress, index)
                .title(mContext.getString(R.string.long_press))
                .positiveAction(mContext.getString(R.string.confirm))
                .negativeAction(mContext.getString(R.string.cancel));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), null);
    }

    private OnClickListener myOnClickListerner = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.show_float_view_rl:
                    isClickFloat = true;
                    showFloarViewSwitch.setChecked(!showFloarViewSwitch.isChecked());
                    break;
                case R.id.show_notify_rl:
                    isClickNotify = true;
                    showNotifySwitch.setChecked(!showNotifySwitch.isChecked());
                    break;
                default:
                    break;
            }
        }
    };

    private void refresh() {
        showFloatView = SPHelper.getBoolean(ConstantUtil.SHOW_FLOAT_VIEW, false);
        showNotify = SPHelper.getBoolean(ConstantUtil.IS_SHOW_NOTIFY, false);


        showFloarViewSwitch.setChecked(showFloatView);
        showNotifySwitch.setChecked(showNotify);

        showNotifyTV.setShowAnimation(true);
        showFloatViewTV.setShowAnimation(true);
    }

}
