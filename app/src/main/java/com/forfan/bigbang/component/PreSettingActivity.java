package com.forfan.bigbang.component;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.activity.setting.SettingActivity;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.util.ClipboardUtils;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.UrlCountUtil;
import com.forfan.bigbang.view.ColorTextView;
import com.forfan.bigbang.view.DialogFragment;
import com.forfan.bigbang.view.SimpleDialog;
import com.shang.commonjar.contentProvider.SPHelper;
import com.shang.utils.StatusBarCompat;

import static com.forfan.bigbang.util.ConstantUtil.BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED;
import static com.forfan.bigbang.util.ConstantUtil.BROADCAST_CLIPBOARD_LISTEN_SERVICE_MODIFIED;

/**
 * Created by wangyan-pd on 2016/11/28.
 */

public class PreSettingActivity extends BaseActivity {
    public static final String SHOW = "pre_is_show";
    private ColorTextView colorText;
    private ColorTextView title;
    private ColorTextView colorTextInto;

    private CheckBox controlByFloat,controlByNotify,triggerByFloat;
    private TextView confirmSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presetting);
//        StatusBarCompat.setupStatusBarView(this, (ViewGroup) getWindow().getDecorView(), true, R.color.colorPrimary);
        initView();
        refresh();
    }

    private void refresh(){
        controlByFloat.setChecked(SPHelper.getBoolean(ConstantUtil.SHOW_FLOAT_VIEW,true));
        controlByNotify.setChecked(SPHelper.getBoolean(ConstantUtil.IS_SHOW_NOTIFY,true));
        triggerByFloat.setChecked(SPHelper.getBoolean(ConstantUtil.USE_FLOAT_VIEW_TRIGGER,false));
    }

    private void initView() {
        title = (ColorTextView)findViewById(R.id.title);
        title.setColorTextColor(getResources().getColor(R.color.colorPrimary));
        title.setColorText(getResources().getString(R.string.pre_setting_title));
        colorText = (ColorTextView)findViewById(R.id.control_setting_title);
        colorText.setColorTextColor(getResources().getColor(R.color.colorPrimary));
        colorText.setColorText(getResources().getString(R.string.pre_setting_intro1));
        colorTextInto = (ColorTextView)findViewById(R.id.introduction);
        colorTextInto.setColorTextColor(getResources().getColor(R.color.colorPrimary));
        colorTextInto.setColorText(getResources().getString(R.string.pre_setting_intro2));

        colorText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardUtils.setText(getApplicationContext(),getResources().getString(R.string.pre_setting_intro2));
            }
        });

        controlByFloat= (CheckBox) findViewById(R.id.contron_by_float);
        controlByFloat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UrlCountUtil.onEvent(UrlCountUtil.PRE__FLOATVIEW,isChecked);
                SPHelper.save(ConstantUtil.SHOW_FLOAT_VIEW, isChecked);
                sendBroadcast(new Intent(BROADCAST_CLIPBOARD_LISTEN_SERVICE_MODIFIED));
                sendBroadcast(new Intent(BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED));
            }
        });
        controlByNotify= (CheckBox) findViewById(R.id.contron_by_notify);
        controlByNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPHelper.save(ConstantUtil.IS_SHOW_NOTIFY, isChecked);
                UrlCountUtil.onEvent(UrlCountUtil.PRE__NOTIFY,isChecked);
                sendBroadcast(new Intent(BROADCAST_CLIPBOARD_LISTEN_SERVICE_MODIFIED));
            }
        });
        triggerByFloat= (CheckBox) findViewById(R.id.trigger_by_float);
        triggerByFloat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPHelper.save(ConstantUtil.USE_FLOAT_VIEW_TRIGGER, isChecked);
                UrlCountUtil.onEvent(UrlCountUtil.PRE__TRIGGER,isChecked);
            }
        });
        confirmSetting= (TextView) findViewById(R.id.confirm);
        confirmSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_PRE_CONFIRM);
                showConfirmDialog();
            }
        });


        initAnimation();

    }

    private void initAnimation(){
        colorTextInto.setVisibility(View.GONE);
        controlByFloat.setVisibility(View.GONE);
        controlByNotify.setVisibility(View.GONE);
        triggerByFloat.setVisibility(View.GONE);
        confirmSetting.setVisibility(View.GONE);

        colorText.setScaleX(0.8f);
        colorText.setScaleY(0.8f);
        colorText.setAlpha(0.5f);

        colorText.animate().alpha(1).scaleX(1).scaleY(1).setInterpolator(new AnticipateOvershootInterpolator()).setDuration(500).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animationTwo();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();

    }

    private void animationTwo() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                colorTextInto.setVisibility(View.VISIBLE);
                colorTextInto.setScaleX(0.5f);
                colorTextInto.setScaleY(0.5f);
                colorTextInto.setAlpha(0);
                colorTextInto.animate().alpha(1).scaleX(1).scaleY(1).setInterpolator(new AnticipateOvershootInterpolator()).setDuration(500).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animationThree();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
            }
        }, 1500);

    }

    private void animationThree() {

        controlByFloat.setVisibility(View.VISIBLE);
        controlByNotify.setVisibility(View.VISIBLE);
        triggerByFloat.setVisibility(View.VISIBLE);
        confirmSetting.setVisibility(View.VISIBLE);

        controlByFloat.setScaleX(0.5f);
        controlByFloat.setScaleY(0.5f);

        controlByNotify.setScaleX(0.5f);
        controlByNotify.setScaleY(0.5f);

        triggerByFloat.setScaleX(0.5f);
        triggerByFloat.setScaleY(0.5f);

        confirmSetting.setScaleX(0.5f);
        confirmSetting.setScaleY(0.5f);

        controlByFloat.animate().scaleX(1).scaleY(1).setDuration(500).setInterpolator(new AnticipateOvershootInterpolator()).start();
        controlByNotify.animate().scaleX(1).scaleY(1).setDuration(500).setInterpolator(new AnticipateOvershootInterpolator()).start();
        triggerByFloat.animate().scaleX(1).scaleY(1).setDuration(500).setInterpolator(new AnticipateOvershootInterpolator()).start();
        confirmSetting.animate().scaleX(1).scaleY(1).setDuration(500).setInterpolator(new AnticipateOvershootInterpolator()).start();
    }

    private void showConfirmDialog() {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                // 这里是保持开启
                UrlCountUtil.onEvent(UrlCountUtil.PRE__FLOATVIEW,false);
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_PRE_CONFIRM_IN_DIALOG);
                SPHelper.save(SHOW,false);
                Intent intent=new Intent(PreSettingActivity.this,SettingActivity.class);
                startActivity(intent);
                finish();
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onDismiss(DialogInterface dialog) {
                super.onCancel(dialog);
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_PRE_CANCEL_IN_DIALOG);
            }
        };
        builder.message(getString(R.string.pre_setting_intro3))
                .negativeAction(getString(R.string.confirm_setting))
                .positiveAction(getString(R.string.pre_setting_cancel));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getSupportFragmentManager(), null);
    }
}
