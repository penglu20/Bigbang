package com.forfan.bigbang.component.activity.share;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.onestep.ResolveInfoWrap;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.SharedIntentHelper;
import com.shang.utils.StatusBarCompat;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ShareAppManagerActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    public static final String SHARE_APPS = "shareapps";
    private List<ShareAppInfo> mAllAppInfo = new ArrayList<>();
    private List<ShareAppInfo> mResultAppInfo = new ArrayList<>();
    private SwipeMenuRecyclerView mRV;
    private Set<String> mDisAppSet = new HashSet<>();
    private String mKeyWord = "";
    private Toolbar toolbar;
    private List<ResolveInfoWrap> resolveInfoWraps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_app_manager);
        StatusBarCompat.setupStatusBarView(this, (ViewGroup) getWindow().getDecorView(), true, R.color.primary_dark);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRV = (SwipeMenuRecyclerView) findViewById(R.id.rv);
        mRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        Adapter.AppInfoAdapter appsAdapter = new Adapter.AppInfoAdapter(mResultAppInfo);
        mRV.setAdapter(appsAdapter);
        new LoadAppInfoClass().execute();
        mRV.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        mRV.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        mRV.setLongPressDragEnabled(true);// 开启拖拽，就这么简单一句话。
        mRV.setOnItemMoveListener(new OnItemMoveListener() {
            @Override
            public boolean onItemMove(int fromPosition, int toPosition) {
                // 当Item被拖拽的时候。
                Collections.swap(mResultAppInfo, fromPosition, toPosition);
                if (resolveInfoWraps != null) {
                    Collections.swap(resolveInfoWraps, fromPosition, toPosition);
                }
                appsAdapter.notifyItemMoved(fromPosition, toPosition);


                return true;// 返回true表示处理了，返回false表示你没有处理。
            }

            @Override
            public void onItemDismiss(int position) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (resolveInfoWraps != null)
            SharedIntentHelper.saveShareAppIndexs2Sp(resolveInfoWraps, ShareAppManagerActivity.this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_manger, menu);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(this);

        final SearchView.SearchAutoComplete searchEditText = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);

        searchEditText.setTextColor(getResources().getColor(R.color.white));

        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                search(searchView.getQuery().toString());
            }
        });
        return true;

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private void search(String query) {
        if (mKeyWord.equals(query)) {
            return;
        }
        mKeyWord = query;
        mResultAppInfo.clear();
        if (TextUtils.isEmpty(query)) {
            mResultAppInfo.addAll(mAllAppInfo);
        } else {
            for (ShareAppInfo appInfo : mAllAppInfo) {
                if (appInfo.appName.contains(query)) {
                    mResultAppInfo.add(appInfo);
                }
            }
        }
        updateResultAppInfo();
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        search(newText);
        return false;
    }


    class LoadAppInfoClass extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDisAppSet = getSharedPreferences(SHARE_APPS, ContextThemeWrapper.MODE_PRIVATE).getStringSet(ConstantUtil.SHARE_APPS_DIS, mDisAppSet);
        }

        @Override
        protected Void doInBackground(Void... params) {
            resolveInfoWraps = SharedIntentHelper.listIntents(ShareAppManagerActivity.this);
            PackageManager packageManager = getPackageManager();
            for (ResolveInfoWrap resolveInfoWrap : resolveInfoWraps) {
                mAllAppInfo.add(new ShareAppInfo(resolveInfoWrap.resolveInfo,
                        resolveInfoWrap.resolveInfo.loadLabel(packageManager).toString(), resolveInfoWrap.resolveInfo.activityInfo.packageName,
                        !mDisAppSet.contains(resolveInfoWrap.resolveInfo.loadLabel(packageManager).toString())));
            }

            mResultAppInfo.addAll(mAllAppInfo);
            Collections.sort(mResultAppInfo, new Comparator<ShareAppInfo>() {
                @Override
                public int compare(ShareAppInfo o1, ShareAppInfo o2) {
                    if (o1.enable != o2.enable) {
                        return o1.enable ? -1 : 1;
                    }
                    return 0;
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateResultAppInfo();
        }

    }

    private void updateResultAppInfo() {
        ((Adapter.AppInfoAdapter) mRV.getAdapter()).loadData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            finish();
            return true;
        } else if (i == R.id.search) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void finish() {
        Set<String> disApps = new HashSet<>();
        for (ShareAppInfo appInfo : mAllAppInfo) {
            if (!appInfo.enable) {
                disApps.add(appInfo.appName);
            }
        }
        getSharedPreferences(SHARE_APPS, ContextThemeWrapper.MODE_PRIVATE).edit().putStringSet(ConstantUtil.SHARE_APPS_DIS, disApps).apply();
        super.finish();
    }
}
