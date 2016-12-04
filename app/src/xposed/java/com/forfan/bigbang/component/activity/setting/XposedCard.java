package com.forfan.bigbang.component.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;
import com.forfan.bigbang.component.activity.whitelist.WhiteListActivity;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.UrlCountUtil;
import com.forfan.bigbang.util.ViewUtil;
import com.shang.commonjar.contentProvider.SPHelper;
import com.shang.xposed.setting.XposedAppManagerActivity;

import static android.content.Context.MODE_WORLD_READABLE;
import static com.forfan.bigbang.util.ConstantUtil.BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED;
import static com.shang.xposed.XposedConstant.SP_DOBLUE_CLICK;
import static com.shang.xposed.XposedConstant.SP_NAME;

/**
 * Created by wangyan-pd on 2016/11/25.
 */

public class XposedCard extends AbsCard implements View.OnClickListener{
    private RelativeLayout doubleClickIntervalRl;
    private TextView mDoubleClick;
    private EditText doubleClickEditText;
    private TextInputLayout doubleClickInputLayout;
    private Button doubleClickConfirm;
    private SharedPreferences mPreferences;

    public XposedCard(Context context) {
        super(context);
        inflate(context, R.layout.card_xposed,this);
        initView(context);
    }

    private void initView(Context context) {
        findViewById(R.id.xposed_whiteList).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_XPOSED_WHITELIST);
                Intent intent = new Intent();
                intent.setClass(context, XposedAppManagerActivity.class);
                context.startActivity(intent);
            }
        });
        findViewById(R.id.xposed_touch_setting).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(context, ForceTouchActivity.class);
//                context.startActivity(intent);
            }
        });
        findViewById(R.id.xposed_touch_setting).setVisibility(GONE);

        doubleClickIntervalRl = (RelativeLayout) findViewById(R.id.double_click_interval_rl);
        mDoubleClick = (TextView) findViewById(R.id.double_click_setting);
        doubleClickEditText = (EditText) findViewById(R.id.double_click_interval_edit);
        doubleClickInputLayout = (TextInputLayout) findViewById(R.id.double_click_interval);
        doubleClickConfirm = (Button) findViewById(R.id.double_click_confirm);


        mDoubleClick.setOnClickListener(this);
        doubleClickConfirm.setOnClickListener(this);

        mPreferences = context.getSharedPreferences(SP_NAME, MODE_WORLD_READABLE);
        int t = mPreferences.getInt(SP_DOBLUE_CLICK,1000);
        //int t = SPHelper.getInt(ConstantUtil.DOUBLE_CLICK_INTERVAL, ConstantUtil.DEFAULT_DOUBLE_CLICK_INTERVAL);
        String text = mContext.getString(R.string.double_click_intercal);
        text = text.replace("#", "<font color=\"#009688\">" + t + "</font>");
        mDoubleClick.setText(Html.fromHtml(text));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {

            case R.id.double_click_setting:
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_DOUBLECLICK_SETTING);
                doubleClickIntervalRl.setVisibility(VISIBLE);
                mDoubleClick.setVisibility(GONE);
                int t = SPHelper.getInt(ConstantUtil.DOUBLE_CLICK_INTERVAL, ConstantUtil.DEFAULT_DOUBLE_CLICK_INTERVAL);
                doubleClickEditText.setText(t + "");
                doubleClickEditText.requestFocus();
                break;
            case R.id.double_click_confirm:
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_DOUBLECLICK_SETTING_CONFORM);
                int time = Integer.parseInt(doubleClickEditText.getText().toString());
                mPreferences.edit().putInt(SP_DOBLUE_CLICK,time).apply();
                String text = mContext.getString(R.string.double_click_intercal);
                text = text.replace("#", "<font color=\"#009688\">" + time + "</font>");
                mDoubleClick.setText(Html.fromHtml(text));
                doubleClickIntervalRl.setVisibility(GONE);
                mDoubleClick.setVisibility(VISIBLE);
                ViewUtil.hideInputMethod(mDoubleClick);
                mContext.sendBroadcast(new Intent(BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED));
                break;
            default:
                break;
        }
    }
}
