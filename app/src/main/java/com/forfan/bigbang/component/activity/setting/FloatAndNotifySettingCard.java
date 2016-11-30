package com.forfan.bigbang.component.activity.setting;

import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.LogUtil;
import com.forfan.bigbang.util.NotificationCheckUtil;
import com.forfan.bigbang.util.SnackBarUtil;
import com.forfan.bigbang.util.UrlCountUtil;
import com.forfan.bigbang.view.Dialog;
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

    private HintTextView showFloatViewTV;
    private HintTextView showNotifyTV;

    private SwitchCompat showFloarViewSwitch;
    private SwitchCompat showNotifySwitch;

    private LinearLayout showFloatRequestLL;

    private boolean showFloatView =true;
    private boolean showNotify =false;
    private boolean isInFirst = true;
    private boolean isClickFloat=false,isClickNotify = false;

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

    private void initView(Context context){
        mContext=context;

        handler=new Handler();

        LayoutInflater.from(context).inflate(R.layout.card_float_notify_setting,this);

        showFloatViewRL = (RelativeLayout) findViewById(R.id.show_float_view_rl);
        showFloarViewSwitch = (SwitchCompat) findViewById(R.id.show_float_view_switch);
        showFloatViewTV = (HintTextView) findViewById(R.id.show_float_view_tv);

        showNotifyRL = (RelativeLayout) findViewById(R.id.show_notify_rl);
        showNotifySwitch = (SwitchCompat) findViewById(R.id.show_notify_switch);
        showNotifyTV = (HintTextView) findViewById(R.id.show_notify_tv);

        showFloatRequestLL= (LinearLayout) findViewById(R.id.show_float_request_ll);

//        requestFloatViewTv= (TextView) findViewById(R.id.show_float_view_request);

        showFloarViewSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_SHOW_FLOAT_WINDOW,isChecked);

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
                showFloatTip();
            }
        });

        showNotifySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_SHOW_NOTIFY,isChecked);

                showNotify = isChecked;
                SPHelper.save(ConstantUtil.IS_SHOW_NOTIFY, showNotify);
                mContext.sendBroadcast(new Intent(BROADCAST_CLIPBOARD_LISTEN_SERVICE_MODIFIED));
                showNotifyTV.setShowHint(!showNotify);
                showFloatTip();
                if (isClickNotify&&isChecked){
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
                                        }catch (Throwable e){
                                            SnackBarUtil.show(buttonView,R.string.open_setting_failed_diy);
                                        }
                                    }
                                });
                    } else {
                        SnackBarUtil.show(buttonView, mContext.getString(R.string.notify_enable));
                    }
                }
            }
        });

        showFloatViewRL.setOnClickListener(myOnClickListerner);
        showNotifyRL.setOnClickListener(myOnClickListerner);
        refresh();
    }


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
        builder.message(mContext.getString(R.string.access_open_tips))
                .positiveAction(mContext.getString(R.string.ok));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(((AppCompatActivity)mContext).getSupportFragmentManager(), null);
    }

    private OnClickListener myOnClickListerner=new OnClickListener(){

        @Override
        public void onClick(View v) {
            int id=v.getId();
            switch (id) {
                case R.id.show_float_view_rl:
                    isClickFloat=true;
                    showFloarViewSwitch.setChecked(!showFloarViewSwitch.isChecked());
                    break;
                case R.id.show_notify_rl:
                    isClickNotify=true;
                    showNotifySwitch.setChecked(!showNotifySwitch.isChecked());
                    break;
                case R.id.default_setting:
                    // TODO: 2016/10/29  恢复默认设置
                    break;
                default:
                    break;
            }
        }
    };

    private void showFloatTip(){
        showFloatRequestLL.clearAnimation();
        if (!showFloatView && !showNotify) {
            showFloatRequestLL.setVisibility(VISIBLE);
            showFloatRequestLL.setAlpha(0);
            showFloatRequestLL.animate().alpha(1).setDuration(300).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    showFloatRequestLL.setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).start();
        }else {
            showFloatRequestLL.setAlpha(1);
            showFloatRequestLL.animate().alpha(0).setDuration(300).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    showFloatRequestLL.setVisibility(GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).start();
        }
    }


    private void refresh(){
        showFloatView = SPHelper.getBoolean(ConstantUtil.SHOW_FLOAT_VIEW,false);
        showNotify = SPHelper.getBoolean(ConstantUtil.IS_SHOW_NOTIFY,false);


        showFloarViewSwitch.setChecked(showFloatView);
        showNotifySwitch.setChecked(showNotify);

        showFloatTip();

        showNotifyTV.setShowAnimation(true);
        showFloatViewTV.setShowAnimation(true);
    }

}
