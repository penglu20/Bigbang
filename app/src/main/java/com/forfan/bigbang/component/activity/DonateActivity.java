package com.forfan.bigbang.component.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.util.IOUtil;
import com.forfan.bigbang.util.StatusBarCompat;
import com.forfan.bigbang.util.ToastUtil;
import com.forfan.bigbang.util.UrlCountUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wangyan-pd on 2016/11/19.
 */

public class DonateActivity extends BaseActivity {
    private static final String SAVE_PIC_PATH= Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";//保存到SD卡
    private static final String SAVE_REAL_PATH = SAVE_PIC_PATH+ "/Pictures";//保存的确切位置
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setupStatusBarView(this, (ViewGroup) getWindow().getDecorView(), true, R.color.colorPrimary);
        setContentView(R.layout.activity_donate);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.donate_title);


        findViewById(R.id.image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_DONATE_ALIPAY_SAVE);
                File file = new File(SAVE_REAL_PATH,"alipay.jpg");
                if(file.exists()){
                    ToastUtil.show(R.string.picture_saved);
                    sendBrodcast4Update(file);
                    return;
                }else {
                    InputStream is=getResources().openRawResource(R.drawable.alipay);
                    try {
                        IOUtil.saveToFile(is,file);
                        ToastUtil.show(R.string.picture_saved);
                        sendBrodcast4Update(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        findViewById(R.id.image1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_DONATE_WECHAT_SAVE);
                File file = new File(SAVE_REAL_PATH,"wechat.jpg");
                if(file.exists()){
                    ToastUtil.show(R.string.picture_saved);
                    sendBrodcast4Update(file);
                    return;
                }else {
                    InputStream is=getResources().openRawResource(R.drawable.wechat);
                    try {
                        IOUtil.saveToFile(is,file);
                        ToastUtil.show(R.string.picture_saved);
                        sendBrodcast4Update(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void sendBrodcast4Update(File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        sendBroadcast(intent);
    }
}
