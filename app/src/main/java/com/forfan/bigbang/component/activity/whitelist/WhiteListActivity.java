package com.forfan.bigbang.component.activity.whitelist;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.DividerItemDecoration;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.ToastUtil;
import com.shang.commonjar.contentProvider.SPHelper;
import com.shang.utils.StatusBarCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WhiteListActivity extends BaseActivity {

    private TextView mAppListTV;
    private RecyclerView mAppListView;
    private AppListAdapter mAppAdapter;

    private List<AppListAdapter.ApplicationInfoWrap> mCanOpenApplicationInfos;
    private List<AppListAdapter.ApplicationInfoWrap> mAllApplicationInfos;
    private List<AppListAdapter.ApplicationInfoWrap> mShowApplicationInfos;
    private List<AppListAdapter.ApplicationInfoWrap> mSelectedApplicationInfos;

    private ContentLoadingProgressBar mLoadingProgressBar;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        super.onCreate(savedInstanceState);
        StatusBarCompat.setupStatusBarView(this, (ViewGroup) this.getWindow().getDecorView(), true, R.color.colorPrimary);
        setContentView(R.layout.activity_monitor_white_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.white_list);

        initView();

        ToastUtil.show(R.string.white_list_tip);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getMenuInflater().inflate(R.menu.white_list_activity_menu, menu);
            SearchManager searchManager =
                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            final SearchView searchView = (SearchView) menu.findItem(R.id.ab_search).getActionView();

            final SearchView.SearchAutoComplete searchEditText = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);

            searchEditText.setTextColor(getResources().getColor(R.color.white));
            searchView.setQueryHint("Search");

            // 将搜索按钮放到搜索输入框的外边
            searchView.setIconifiedByDefault(false);


            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    refreshListByQuery(searchView.getQuery().toString());
                    return true;
                }
            });
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    refreshListByQuery("");
                    return false;
                }
            });

//            MenuItem menuItem=menu.add("恢复默认");
//            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
//            menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//                    if(mSelectedApplicationInfos==null){
//                        SnackBarUtil.show(toolbar,R.string.wait_until_loaded);
//                        return true;
//                    }
//                    for (AppListAdapter.ApplicationInfoWrap app:mSelectedApplicationInfos){
//                        app.isSelected=false;
//                    }
//                    mSelectedApplicationInfos.clear();
//                    mAppAdapter.notifyDataSetChanged();
//                    refreshTV();
//                    return true;
//                }
//            });
        }
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            refreshListByQuery(query);
        }
    }

    private void refreshListByQuery(String query) {
        if (mCanOpenApplicationInfos == null) {
            return;
        }
        if (TextUtils.isEmpty(query)) {
            mShowApplicationInfos = mCanOpenApplicationInfos;
        } else {
            mShowApplicationInfos = new ArrayList<>();
            for (AppListAdapter.ApplicationInfoWrap app : mCanOpenApplicationInfos) {
                if (app.applicationInfo.loadLabel(getPackageManager()).toString().toLowerCase().contains(query.toLowerCase())) {
                    mShowApplicationInfos.add(app);
                }
            }
        }
        if (mAppAdapter != null) {
            mAppAdapter.setAppList(mShowApplicationInfos);
            mAppAdapter.notifyDataSetChanged();
            refreshTV();
        }
    }


    @Override
    protected void onStop() {
        saveSelectedApp();
        super.onStop();
    }

    private void initView() {
        mLoadingProgressBar = (ContentLoadingProgressBar) findViewById(R.id.loading);

        mAppListView = (RecyclerView) findViewById(R.id.app_list);
        mAppListTV = (TextView) findViewById(R.id.toselectApp_tv);

        mLoadingProgressBar.show();
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                queryFilterAppInfo();
                querySelectedApp();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingProgressBar.hide();
                        initAppList();
                        mAppListTV.setVisibility(View.GONE);
                        mAppListView.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();
    }

    private void querySelectedApp() {
        mSelectedApplicationInfos = new ArrayList<>();
        Set<String> selectedPackageNames = new HashSet<>();
        int size = SPHelper.getInt(ConstantUtil.WHITE_LIST_COUNT, 0);
        for (int i = 0; i < size; i++) {
            String packageName = SPHelper.getString(ConstantUtil.WHITE_LIST + "_" + i, "");
            selectedPackageNames.add(packageName);
        }
        for (AppListAdapter.ApplicationInfoWrap app : mAllApplicationInfos) {
            String packageName = app.applicationInfo.packageName;
            if (selectedPackageNames.contains(packageName)) {
                app.isSelected = true;
                mSelectedApplicationInfos.add(app);
            }
        }
    }


    private void saveSelectedApp() {
        if (mSelectedApplicationInfos != null) {
            SPHelper.save(ConstantUtil.WHITE_LIST_COUNT, mSelectedApplicationInfos.size());
//            HashMap<String, String> map = new HashMap<>();
            for (int i = 0; i < mSelectedApplicationInfos.size(); i++) {
                String value = mSelectedApplicationInfos.get(i).applicationInfo.packageName;
                SPHelper.save(ConstantUtil.WHITE_LIST + "_" + i, value);
//                map.put(UrlCountUtil.VALUE_MONITOR_WHITE_LIST_CLASS + "_" + i, value);
            }
            sendBroadcast(new Intent(ConstantUtil.REFRESH_WHITE_LIST_BROADCAST));
//            UrlCountUtil.onEvent(UrlCountUtil.VALUE_MONITOR_WHITE_LIST_CLASS, map);
        }
    }

    private void initAppList() {
        refreshTV();

        mAppListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        divider.setDivider(this.getResources().getDrawable(R.drawable.situation_divider));
        mAppListView.addItemDecoration(divider);

        mAppAdapter = new AppListAdapter(this);
        mAppAdapter.setAppList(mShowApplicationInfos);
        mAppAdapter.setmListener(new AppListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, boolean isChecked) {
                if (position < 0 || position >= mShowApplicationInfos.size()) {
                    return;
                }
                final AppListAdapter.ApplicationInfoWrap select = mShowApplicationInfos.get(position);
                select.isSelected = isChecked;
                if (isChecked) {
                    mSelectedApplicationInfos.add(select);
                } else {
                    mSelectedApplicationInfos.remove(select);
                }
                refreshTV();
            }
        });
        mAppListView.setAdapter(mAppAdapter);
    }

    private void refreshTV() {
        mAppListTV.setText(getString(R.string.select_list) + "(共" + mCanOpenApplicationInfos.size() + "个,已选" + mSelectedApplicationInfos.size() + "个)");
    }

    //全部程序包
    private void queryFilterAppInfo() {

        final PackageManager pm = this.getPackageManager();
        // 查询所有已经安装的应用程序
        List<ApplicationInfo> appInfos = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);// GET_UNINSTALLED_PACKAGES代表已删除，但还有安装目录的


        List<AppListAdapter.ApplicationInfoWrap> applicationInfos = new ArrayList<>();
        List<AppListAdapter.ApplicationInfoWrap> allApp = new ArrayList<>();

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        // 通过getPackageManager()的queryIntentActivities方法遍历,得到所有能打开的app的packageName
        List<ResolveInfo> resolveinfoList = pm.queryIntentActivities(resolveIntent, 0);

        Set<String> allowPackages = new HashSet();
        for (ResolveInfo resolveInfo : resolveinfoList) {
            allowPackages.add(resolveInfo.activityInfo.packageName);
        }
        for (ApplicationInfo app : appInfos) {
//            if((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0)//排除系统应用
//            {
//                applicationInfos.add(app);
//            }
//            if(app.uid > 10000){
//                applicationInfos.add(app);
//            }


            AppListAdapter.ApplicationInfoWrap wrap = new AppListAdapter.ApplicationInfoWrap();
            wrap.applicationInfo = app;
            if (allowPackages.contains(app.packageName)) {
                applicationInfos.add(wrap);
            }
            allApp.add(wrap);
        }
        Collections.sort(applicationInfos, new Comparator<AppListAdapter.ApplicationInfoWrap>() {//按名字排序，便于找到应用

            @Override
            public int compare(AppListAdapter.ApplicationInfoWrap lhs, AppListAdapter.ApplicationInfoWrap rhs) {
                // TODO 自动生成的方法存根
                return lhs.applicationInfo.loadLabel(pm).toString().compareToIgnoreCase(rhs.applicationInfo.loadLabel(pm).toString());
            }
        });
        mAllApplicationInfos = allApp;
        mCanOpenApplicationInfos = applicationInfos;

        mShowApplicationInfos = mCanOpenApplicationInfos;
    }
}
