package com.forfan.bigbang.component.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;
import com.forfan.bigbang.component.activity.OcrActivity;
import com.forfan.bigbang.util.UrlCountUtil;
import com.shang.xposed.XposedEnable;
import com.shang.xposed.setting.XposedAppManagerActivity;

/**
 * Created by wangyan-pd on 2016/11/9.
 */

public class OcrCard extends AbsCard {
    public OcrCard(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mContext=context;
        LayoutInflater.from(context).inflate(R.layout.card_ocr,this);
        findViewById(R.id.orc).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_OPEN_OCR);
                Intent intent = new Intent();
                intent.setClass(mContext, OcrActivity.class);
                mContext.startActivity(intent);
            }
        });
        if(!XposedEnable.isEnable()){
            findViewById(R.id.xposed_whiteList).setVisibility(GONE);
        }
        findViewById(R.id.xposed_whiteList).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_OPEN_OCR);
                Intent intent = new Intent();
                intent.setClass(mContext, XposedAppManagerActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

}
