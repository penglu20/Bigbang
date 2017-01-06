package com.shang.xposed;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.forfan.bigbang.copy.CopyNode;

import java.util.ArrayList;
import java.util.List;

import static com.shang.xposed.XposedConstant.UNIVERSAL_COPY_BROADCAST_XP;

/**
 * Created by penglu on 2017/1/4.
 */

public class XposedUniversalCopyHandler {
    public static final String TAG="UniversalCopyHandler";


    List<Activity> mActivities=new ArrayList<>();
    IntentFilter intentFilter=new IntentFilter(UNIVERSAL_COPY_BROADCAST_XP);
    Handler handler;
    List<Filter> mFilters;

    public void setFilters(List<Filter> mFilters) {
        this.mFilters = mFilters;
    }

    public void onStart(Activity activity){
        mActivities.add(activity);
        try {
            activity.getApplication().registerReceiver(mUniversalCopyBR,intentFilter);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void onStop(Activity activity){
        mActivities.remove(activity);
        if (mActivities.size()==0){
            try {
                activity.getApplication().unregisterReceiver(mUniversalCopyBR);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private void startUniversalCopy(){
        Log.e(TAG,"startUniversalCopy");
        Activity topActivity=null;
        ActivityManager activityManager= (ActivityManager) mActivities.get(0).getApplication().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfos=activityManager.getRunningTasks(1);
        if (taskInfos.size()>0){
            ComponentName top=taskInfos.get(0).topActivity;
            if (top!=null){
                String name=top.getClassName();
                for (Activity activity:mActivities){
                    if (activity.getClass().getName().equals(name)){
                        topActivity=activity;
                        break;
                    }
                }
            }
        }
        if (topActivity==null){
            if (mActivities.size()>0) {
                topActivity = mActivities.get(mActivities.size() - 1);
                if (topActivity.isFinishing()){
                    topActivity=null;
                }
            }
        }
        UniversalCopy(topActivity);
    }

    private int retryTimes=0;
    private void UniversalCopy(final Activity activity) {
        if (activity==null){
            return;
        }
        boolean isSuccess=false;
        label37: {
            View decirView =activity.getWindow().getDecorView();
            if(this.retryTimes < 10) {
                String packageName;
                packageName = activity.getPackageName();

                if(decirView == null || packageName != null && packageName.contains("com.android.systemui")) {
                    ++this.retryTimes;
                    this.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            UniversalCopy(activity);
                        }
                    }, 100);
                    return;
                }

                WindowManager var5 = (WindowManager)activity.getSystemService(Context.WINDOW_SERVICE);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                var5.getDefaultDisplay().getMetrics(displayMetrics);
                int var1 = displayMetrics.heightPixels;
                int var2 = displayMetrics.widthPixels;
                ArrayList<CopyNode> nodeList = traverseNode(decirView, var2, var1);
                for (CopyNode node:nodeList) {
                    Log.e(TAG, "traverseNode result= " + node);
                }
                if(nodeList.size() > 0) {
//                    Intent intent = new Intent(activity, CopyActivity.class);
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(XposedConstant.PACKAGE_NAME,"com.forfan.bigbang.copy.CopyActivity"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle=new Bundle();
                    bundle.setClassLoader(CopyNode.class.getClassLoader());
                    bundle.putString("source_package", packageName);
                    bundle.putParcelableArrayList("copy_nodes", nodeList);
                    intent.putExtras(bundle);
                    try {
                        activity.startActivity(intent);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    isSuccess = true;
                    break label37;
                }

//                ae.a(this.getApplication(), "APP_DATA", "UC_MODE_FAILED", packageName);
            }

            isSuccess = false;
        }

        if(!isSuccess) {
            try {
                Toast.makeText(activity, "error" , Toast.LENGTH_SHORT).show();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        this.retryTimes = 0;
    }

    private ArrayList<CopyNode> traverseNode(View nodeInfo, int screenWidth, int scerrnHeight) {
        ArrayList nodeList = new ArrayList();
        if(nodeInfo != null ) {
            if (!nodeInfo.isShown()){
                return nodeList;
            }
            if (nodeInfo instanceof ViewGroup){
                ViewGroup viewGroup = (ViewGroup) nodeInfo;
                for(int var4 = 0; var4 < viewGroup.getChildCount(); ++var4) {
                    nodeList.addAll(this.traverseNode(viewGroup.getChildAt(var4), screenWidth, scerrnHeight));
                }
            }
            if(nodeInfo.getClass().getName() != null && nodeInfo.getClass().getName().equals("android.webkit.WebView")) {
                return nodeList;
            } else {
                String content = null;
                String description = content;
                if(nodeInfo.getContentDescription() != null) {
                    description = content;
                    if(!"".equals(nodeInfo.getContentDescription())) {
                        description = nodeInfo.getContentDescription().toString();
                    }
                }

                content = description;
                String text=getTextInFilters(nodeInfo,mFilters);
                if(text != null) {
                    content = description;
                    if(!"".equals(text)) {
                        content = text.toString();
                    }
                }

                if(content != null) {
                    Rect var8 = new Rect();
                    nodeInfo.getGlobalVisibleRect(var8);
                    if(checkBound(var8, screenWidth, scerrnHeight)) {
                        nodeList.add(new CopyNode(var8, content));
                    }
                }

                return nodeList;
            }
        } else {
            return nodeList;
        }
    }

    private String getTextInFilters(View v,List<Filter> filters){
        for (Filter filter:filters){
            if (filter.filter(v)){
                return filter.getContent(v);
            }
        }
        return null;
    }

    private boolean checkBound(Rect var1, int var2, int var3) {
        return var1.bottom >= 0 && var1.right >= 0 && var1.top <= var3 && var1.left <= var2;
    }


    private BroadcastReceiver mUniversalCopyBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (handler==null){
                handler=new Handler(Looper.getMainLooper());
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    startUniversalCopy();
                }
            });
        }
    };


}
