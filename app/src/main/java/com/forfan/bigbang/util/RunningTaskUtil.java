package com.forfan.bigbang.util;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.forfan.bigbang.BigBangApp;
import com.forfan.bigbang.R;
import com.forfan.bigbang.view.Dialog;
import com.forfan.bigbang.view.DialogFragment;
import com.forfan.bigbang.view.SimpleDialog;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeMap;

/**
 * Created by penglu on 2016/8/14.
 */
public class RunningTaskUtil {
    private static final String TAG="RunningTaskUtil";
    public static final int TWENTYSECOND = 1000 * 20;
    public static final int THIRTYSECOND = 1000 * 60 * 60 * 3;
    private ActivityManager activityManager;
    private UsageStatsManager mUsageStatsManager;
    private Field mLastEventField;

    public RunningTaskUtil(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mUsageStatsManager = (UsageStatsManager)context.getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        }
        activityManager = (ActivityManager)context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
    }

    public ComponentName getTopActivtyFromLolipopOnwards(){
        //用两次取当前应用的办法来提高正确性
        return getTopActivtyFromLolipopOnwards(true);
    }

    public ComponentName getTopActivtyFromLolipopOnwards(boolean isFirst){
        ComponentName runningTopActivity=null;
        String topPackageName =null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            long time = System.currentTimeMillis();
            // We get usage stats for the last 10 seconds
            List<UsageStats> stats ;
//            long start=System.currentTimeMillis();
            if (isFirst){
                stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - TWENTYSECOND, time);
            }else {
                stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - THIRTYSECOND, time);
            }
//            LogUtil.e(TAG,"isFirst="+isFirst+",queryUsageStats cost:"+ (System.currentTimeMillis()-start));
            // Sort the stats by the last time used
            if(stats != null) {
                TreeMap<Long,UsageStats> mySortedMap = new TreeMap<Long,UsageStats>();
//                start=System.currentTimeMillis();
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(),usageStats);
                }
//                LogUtil.e(TAG,"isFirst="+isFirst+",mySortedMap cost:"+ (System.currentTimeMillis()-start));
                if(mySortedMap != null && !mySortedMap.isEmpty()) {
                    NavigableSet<Long> keySet=mySortedMap.navigableKeySet();
                    Iterator iterator=keySet.descendingIterator();
                    while(iterator.hasNext()){
                        UsageStats usageStats = mySortedMap.get(iterator.next());
                        if (mLastEventField==null) {
                            try {
                                mLastEventField = UsageStats.class.getField("mLastEvent");
                            } catch (NoSuchFieldException e) {
                                break;
                            }
                        }
                        if (mLastEventField!=null) {
                            int lastEvent = 0;
                            try {
                                lastEvent = mLastEventField.getInt(usageStats);
                            } catch (IllegalAccessException e) {
                                break;
                            }
                            if (lastEvent==1){
                                topPackageName=usageStats.getPackageName();
                                break;
                            }
                        }else {
                            break;
                        }
                    }
                    if (topPackageName==null){
                        topPackageName =  mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                        if ("com.android.systemui".equals(topPackageName)){
                            Long currentKey = null;
                            String tempPackage = topPackageName;
                            currentKey = (Long) ((TreeMap) mySortedMap).floorKey(mySortedMap.lastKey()-1);
                            if (currentKey!=null) {
                                tempPackage = mySortedMap.get(currentKey).getPackageName();
                            }
                            if (tempPackage!=null){
                                if (BigBangApp.getInstance().getPackageName().equals(tempPackage)){
                                    currentKey = (Long) ((TreeMap) mySortedMap).floorKey(currentKey-1);
                                    if (currentKey!=null) {
                                        tempPackage = mySortedMap.get(currentKey).getPackageName();
                                    }
                                }
                            }
                            if (tempPackage!=null){
                                topPackageName=tempPackage;
                            }
                        }
                    }
                    runningTopActivity=new ComponentName(topPackageName,"");
                    LogUtil.d(TAG,topPackageName);
                }else {
                    LogUtil.d(TAG,"mySortedMap.isEmpty");
                    if (isFirst){
                        runningTopActivity = getTopActivtyFromLolipopOnwards(false);
                    }else {
                        runningTopActivity=getTopRunningTasks();
                    }
                }
            }
            if (runningTopActivity.getPackageName().equals(BigBangApp.getInstance().getPackageName())){
                runningTopActivity = getTopRunningTasks();
            }
        }else {
            runningTopActivity = getTopRunningTasks();
        }
        return runningTopActivity;
    }

    public ComponentName getTopRunningTasks(){
        ComponentName runningTopActivity = activityManager.getRunningTasks(1).get(0).topActivity;
        LogUtil.d(TAG,"runningTopActivity="+runningTopActivity.getClassName());
        return runningTopActivity;
    }

    public boolean needToSet(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            long time = System.currentTimeMillis();
            // We get usage stats for the last 10 seconds
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000*60, time);
            if (stats.size()==0){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    public interface SettingRequestListener{
        void onPositive();
        void onNegative();
    }

    public void showSettingRequestDialog(final View view, final boolean needSet, final Context context, final SettingRequestListener settingRequestListener){
        Dialog.Builder builder = new SimpleDialog.Builder( R.style.SimpleDialogLight ){
            boolean isSet=false;
            @Override
            protected void onBuildDone(final Dialog dialog) {
                dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                if (needSet) {
                    try {
                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        context.startActivity(intent);
                    }catch (Throwable e){
                        SnackBarUtil.show(view,R.string.open_setting_failed_diy);
                    }
                }
                isSet = true;
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onDismiss(DialogInterface dialog) {
                super.onDismiss(dialog);
                if (isSet){
                    if (settingRequestListener!=null) {
                        settingRequestListener.onPositive();
                    }
                }else {
                    if (settingRequestListener!=null) {
                        settingRequestListener.onNegative();
                    }
                }
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                isSet=false;
                super.onNegativeActionClicked(fragment);
            }
        };

        ((SimpleDialog.Builder)builder)
                .message(context.getString(R.string.request_usage_setting_msg))
                .positiveAction(context.getString(R.string.goto_setting))
                .negativeAction(context.getString(R.string.cancel));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(((AppCompatActivity)context).getSupportFragmentManager(), null);
    }
}
