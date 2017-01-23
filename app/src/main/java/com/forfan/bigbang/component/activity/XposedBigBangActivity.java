package com.forfan.bigbang.component.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.util.ArcTipViewController;
import com.forfan.bigbang.util.ConstantUtil;
import com.shang.commonjar.contentProvider.SPHelper;

/**
 * Created by wangyan-pd on 2016/12/5.
 */

public class XposedBigBangActivity extends BaseActivity {
    private String str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //xposed uri进入
        if (getIntent().getData() != null) {
            String query = getIntent().getData().getQuery();
            if (!TextUtils.isEmpty(query) && query.startsWith("extra_text=")) {
                str = query.replace("extra_text=", "");
            }
        }
        if(!TextUtils.isEmpty(str)){
            str = str.replace("\1","%");
            Intent intent=new Intent(this, BigBangActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(BigBangActivity.TO_SPLIT_STR,str);
            boolean click = SPHelper.getBoolean(ConstantUtil.MONITOR_CLICK,true);
            boolean totalSwitch = SPHelper.getBoolean(ConstantUtil.TOTAL_SWITCH,true);
            if(click && totalSwitch){
                //放到TipViewController中触发试试
                ArcTipViewController.getInstance().showTipViewForStartActivity(intent);
            }
//
        }
        finish();
    }
}
