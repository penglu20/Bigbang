package com.forfan.bigbang.component.activity.searchengine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.activity.searchengine.Adapter.IonSlidingViewClickListener;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.util.IOUtil;
import com.forfan.bigbang.util.ToastUtil;
import com.forfan.bigbang.util.UrlCountUtil;
import com.shang.utils.StatusBarCompat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wangyan-pd on 2016/11/19.
 */

public class SearchEngineActivity extends BaseActivity {
    private static final String SAVE_PIC_PATH= Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";//保存到SD卡
    private static final String SAVE_REAL_PATH = SAVE_PIC_PATH+ "/Pictures";//保存的确切位置
    private RecyclerView mRecyclerView;
    private Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setupStatusBarView(this, (ViewGroup) getWindow().getDecorView(), true, R.color.colorPrimary);
        setContentView(R.layout.activity_search_engine);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.donate_title);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter = new Adapter(this));
        mAdapter.setIonSlidingViewClickListener(new IonSlidingViewClickListener(){

            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onDeleteBtnCilck(View view, int position) {
                mAdapter.removeData(position);
            }
        });
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }


}
