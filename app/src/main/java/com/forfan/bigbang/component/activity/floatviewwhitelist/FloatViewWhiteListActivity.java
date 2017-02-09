package com.forfan.bigbang.component.activity.floatviewwhitelist;

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
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.DividerItemDecoration;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.RunningTaskUtil;
import com.forfan.bigbang.util.ToastUtil;
import com.forfan.bigbang.util.UrlCountUtil;
import com.shang.commonjar.contentProvider.SPHelper;
import com.shang.utils.StatusBarCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.forfan.bigbang.component.activity.setting.MonitorSettingCard.SPINNER_ARRAY;
import static com.forfan.bigbang.component.activity.whitelist.AppListAdapter.ApplicationInfoWrap.NON_SELECTION;

public class FloatViewWhiteListActivity extends BaseActivity {

    private RecyclerView mAppListView;
    private AppListAdapter mAppAdapter;

    private List<AppListAdapter.ApplicationInfoWrap> mCanOpenApplicationInfos;
    private List<AppListAdapter.ApplicationInfoWrap> mAllApplicationInfos;
    private List<AppListAdapter.ApplicationInfoWrap> mShowApplicationInfos;
    private Set<AppListAdapter.ApplicationInfoWrap> mSelectedApplicationInfos;

    private ContentLoadingProgressBar mLoadingProgressBar;
    private Toolbar toolbar;

    private MenuItem selectAll;
    private MenuItem setSelection;

    private RunningTaskUtil runningTaskUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        super.onCreate(savedInstanceState);
        StatusBarCompat.setupStatusBarView(this, (ViewGroup) this.getWindow().getDecorView(), true, R.color.colorPrimary);
        setContentView(R.layout.activity_monitor_white_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.float_white_list);

        initView();
        runningTaskUtil=new RunningTaskUtil(this);
        if (runningTaskUtil.needToSet()){
            runningTaskUtil.showSettingRequestDialog(toolbar, true, this, new RunningTaskUtil.SettingRequestListener() {
                @Override
                public void onPositive() {
                }

                @Override
                public void onNegative() {
                    ToastUtil.show(R.string.float_white_list_nagetive);
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getMenuInflater().inflate(R.menu.white_list_activity_menu, menu);
            SearchManager searchManager =
                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            final SearchView searchView = (SearchView) menu.findItem(R.id.ab_search).getActionView();

            selectAll=menu.findItem(R.id.select_all);
            setSelection=menu.findItem(R.id.setSelection);

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
        refreshMenu(false);
        return true;
    }

    private void refreshMenu(boolean isEditMode){
        setSelection.setVisible(false);
        if (mSelectedApplicationInfos==null){
            return;
        }
        selectAll.setVisible(true);
        selectAll.setIcon(R.drawable.select_all);
        selectAll.setTitle(R.string.select_all);
        selectAll.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (mShowApplicationInfos==null){
                    return false;
                }
                boolean isAllSelected=true;
                for (AppListAdapter.ApplicationInfoWrap app:mShowApplicationInfos) {
                    if (!app.isSelected){
                        isAllSelected=false;
                        break;
                    }
                }
                for (AppListAdapter.ApplicationInfoWrap app:mShowApplicationInfos) {
                    if (isAllSelected){
                        app.isSelected = false;
                        mSelectedApplicationInfos.remove(app);
                    }else {
                        app.isSelected = true;
                        mSelectedApplicationInfos.add(app);
                    }
                }
                mAppAdapter.notifyDataSetChanged();
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_WL_SELECT_ALL,!isAllSelected);
                return true;
            }
        });
    }

    private void unSelectAll() {
        for (AppListAdapter.ApplicationInfoWrap app:mSelectedApplicationInfos){
            app.isSelected=false;
        }
        mSelectedApplicationInfos.clear();
        mAppAdapter.notifyDataSetChanged();
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
        }
    }

    @Override
    protected void onStop() {
        saveSelectedApp();
        super.onStop();
    }

    private void initView() {
        mSelectedApplicationInfos = new HashSet<>();
        mLoadingProgressBar = (ContentLoadingProgressBar) findViewById(R.id.loading);

        mAppListView = (RecyclerView) findViewById(R.id.app_list);

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
                        mAppListView.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();
    }

    private void querySelectedApp() {
        int numbers = SPHelper.getInt(ConstantUtil.FLOAT_WHITE_LIST_COUNT,0);
        List<String> selectedPackageNames=new ArrayList<>();
        for (int i=0;i<numbers;i++){
            selectedPackageNames.add(SPHelper.getString(ConstantUtil.FLOAT_WHITE_LIST+i,""));
        }
        for (AppListAdapter.ApplicationInfoWrap app:mCanOpenApplicationInfos){
            if (selectedPackageNames.contains(app.applicationInfo.packageName)){
                app.isSelected = true;
            }else {
                app.isSelected = false;
            }
        }
        final PackageManager pm = this.getPackageManager();
        Collections.sort(mCanOpenApplicationInfos, new Comparator<AppListAdapter.ApplicationInfoWrap>() {//按名字排序，便于找到应用

            @Override
            public int compare(AppListAdapter.ApplicationInfoWrap lhs, AppListAdapter.ApplicationInfoWrap rhs) {
                // TODO 自动生成的方法存根
                String lhsName=lhs.applicationInfo.packageName;
                String rhsName=rhs.applicationInfo.packageName;


                if (lhs.isSelected){
                    return -8000000;
                }
                if (rhs.isSelected){
                    return 8000000;
                }
                if (rhsName.equals("im.yixin")){
                    return 1000000;
                }else if (rhsName.equals("com.alibaba.android.rimet")){
                    return 2000000;
                }else if (rhsName.equals("com.immomo.momo")){
                    return 3000000;
                }else if (rhsName.equals("com.sina.weibo")){
                    return 4000000;
                }else if (rhsName.equals("com.eg.android.AlipayGphone")){
                    return 5000000;
                }else if (rhsName.equals("com.tencent.mobileqq")){
                    return 6000000;
                }else if (rhsName.equals("com.tencent.mm")){
                    return 7000000;
                }
                if (lhsName.equals("im.yixin")){
                    return -1000000;
                }else if (lhsName.equals("com.alibaba.android.rimet")){
                    return -2000000;
                }else if (lhsName.equals("com.immomo.momo")){
                    return -3000000;
                }else if (lhsName.equals("com.sina.weibo")){
                    return -4000000;
                }else if (lhsName.equals("com.eg.android.AlipayGphone")){
                    return -5000000;
                }else if (lhsName.equals("com.tencent.mobileqq")){
                    return -6000000;
                }else if (lhsName.equals("com.tencent.mm")){
                    return -7000000;
                }
                return lhs.applicationInfo.loadLabel(pm).toString().compareToIgnoreCase(rhs.applicationInfo.loadLabel(pm).toString());
            }
        });
    }


    private void saveSelectedApp() {
        if (mCanOpenApplicationInfos!=null) {
            List<String> selectedPackageNames=new ArrayList<>();
            for (AppListAdapter.ApplicationInfoWrap app:mCanOpenApplicationInfos){
                if (app.isSelected){
                    selectedPackageNames.add(app.applicationInfo.packageName);
                }
            }
            int numbers = selectedPackageNames.size();
            SPHelper.save(ConstantUtil.FLOAT_WHITE_LIST_COUNT,numbers);

            for (int i=0;i<numbers;i++){
                SPHelper.save(ConstantUtil.FLOAT_WHITE_LIST+i,selectedPackageNames.get(i));
            }

            sendBroadcast(new Intent(ConstantUtil.FLOAT_REFRESH_WHITE_LIST_BROADCAST));
        }
    }

    private void initAppList() {

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
                int size=mSelectedApplicationInfos.size();
                final AppListAdapter.ApplicationInfoWrap select = mShowApplicationInfos.get(position);
                select.isSelected = isChecked;
                if (isChecked) {
                    mSelectedApplicationInfos.add(select);
                } else {
                    mSelectedApplicationInfos.remove(select);
                }
                if (size==mSelectedApplicationInfos.size()){
                    return;
                }
                refreshMenu(false);
            }

        });
        mAppListView.setAdapter(mAppAdapter);
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

        mAllApplicationInfos = allApp;
        mCanOpenApplicationInfos = applicationInfos;

        mShowApplicationInfos = mCanOpenApplicationInfos;
    }
}
