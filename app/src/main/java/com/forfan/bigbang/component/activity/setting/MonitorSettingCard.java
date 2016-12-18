package com.forfan.bigbang.component.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;
import com.forfan.bigbang.component.activity.whitelist.WhiteListActivity;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.LogUtil;
import com.forfan.bigbang.util.UrlCountUtil;
import com.forfan.bigbang.util.ViewUtil;
import com.shang.commonjar.contentProvider.SPHelper;

import static com.forfan.bigbang.util.ConstantUtil.BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED;


/**
 * Created by penglu on 2015/11/23.
 */
public class MonitorSettingCard extends AbsCard {
    private static final String TAG = "MonitorSettingCard";

    public static final int SPINNER_ARRAY = R.array.click_or_long_click;
    private RelativeLayout onlyTextRL, doubleClickIntervalRl;
    private TextView whiteList, mDoubleClick;

    private SwitchCompat onlyTextSwitch;

    private TextInputLayout doubleClickInputLayout;
    private EditText doubleClickEditText;
    private Button doubleClickConfirm;

    private boolean onlyText = false;

    public MonitorSettingCard(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;


        LayoutInflater.from(context).inflate(R.layout.card_monitor_setting, this);

        onlyTextRL = (RelativeLayout) findViewById(R.id.text_only_rl);
        onlyTextSwitch = (SwitchCompat) findViewById(R.id.text_only_switch);

        whiteList = (TextView) findViewById(R.id.white_list);

        doubleClickIntervalRl = (RelativeLayout) findViewById(R.id.double_click_interval_rl);
        mDoubleClick = (TextView) findViewById(R.id.double_click_setting);
        doubleClickEditText = (EditText) findViewById(R.id.double_click_interval_edit);
        doubleClickInputLayout = (TextInputLayout) findViewById(R.id.double_click_interval);
        doubleClickConfirm = (Button) findViewById(R.id.double_click_confirm);

        onlyTextSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton aSwitch, boolean isChecked) {
                //这里由于文案从“只监控文本”改成了“增强型监控”,所以意思完全相反了，为了防止改动太多，就在这里做一个反置
                onlyText = !isChecked;
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_ONLY_TEXT_MONITOR, !isChecked);
                SPHelper.save(ConstantUtil.TEXT_ONLY, onlyText);
                mContext.sendBroadcast(new Intent(BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED));
            }
        });

        onlyTextRL.setOnClickListener(myOnClickListerner);
        whiteList.setOnClickListener(myOnClickListerner);
        mDoubleClick.setOnClickListener(myOnClickListerner);
        doubleClickConfirm.setOnClickListener(myOnClickListerner);

        refresh();
    }

    private OnClickListener myOnClickListerner = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.text_only_rl:
                    onlyTextSwitch.setChecked(!onlyTextSwitch.isChecked());
                    break;
                case R.id.white_list:
                    UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_WHITELIST);
                    mContext.startActivity(new Intent(mContext, WhiteListActivity.class));
                    break;
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
                    int time =0;
                    if (doubleClickEditText.getText()==null|| TextUtils.isEmpty(doubleClickEditText.getText())){
                        time = SPHelper.getInt(ConstantUtil.DOUBLE_CLICK_INTERVAL, ConstantUtil.DEFAULT_DOUBLE_CLICK_INTERVAL);
                    }else {
                        time = Integer.parseInt(doubleClickEditText.getText().toString());
                        SPHelper.save(ConstantUtil.DOUBLE_CLICK_INTERVAL, time);
                    }
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
    };

    private void refresh() {
        onlyText = SPHelper.getBoolean(ConstantUtil.TEXT_ONLY, true);

        onlyTextSwitch.setChecked(!onlyText);

        int t = SPHelper.getInt(ConstantUtil.DOUBLE_CLICK_INTERVAL, ConstantUtil.DEFAULT_DOUBLE_CLICK_INTERVAL);
        String text = mContext.getString(R.string.double_click_intercal);
        text = text.replace("#", "<font color=\"#009688\">" + t + "</font>");
        mDoubleClick.setText(Html.fromHtml(text));

    }


}
