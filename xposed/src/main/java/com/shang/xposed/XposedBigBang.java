package com.shang.xposed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.shang.xposed.XposedConstant.PACKAGE_NAME;
import static com.shang.xposed.XposedConstant.SP_DISABLE_KEY;
import static com.shang.xposed.XposedConstant.SP_NAME;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by dim on 16/10/23.
 */

public class XposedBigBang implements IXposedHookLoadPackage {

    private static final String TAG = "XposedBigBang";

    private final TouchEventHandler mTouchHandler = new TouchEventHandler();
    private final List<Filter> mFilters = new ArrayList<>();

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        setXpoedEnable(loadPackageParam);
        findAndHookMethod("android.app.Application", loadPackageParam.classLoader, "onCreate", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Context context = (Context) param.thisObject;
//                IntentFilter filter = new IntentFilter();
//                filter.addAction("com.shang.bigbang.wake");
//                filter.addCategory(Intent.CATEGORY_DEFAULT);
                Intent intent = new Intent();
                intent.setAction("com.shang.bigbang.wake");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                context.sendBroadcast(intent);
            }
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
        Logger.d(TAG,loadPackageParam.packageName);
        XSharedPreferences appXSP = new XSharedPreferences(PACKAGE_NAME, SP_NAME);
        appXSP.makeWorldReadable();
        Set<String> disAppSet = appXSP.getStringSet(SP_DISABLE_KEY, null);
        if (disAppSet != null && disAppSet.contains(loadPackageParam.packageName)) {
            return;
        }
        mFilters.add(new Filter.TextViewValidFilter());
        //优化微信 下的体验。
        if ("com.tencent.mm".equals(loadPackageParam.packageName)) {
            //朋友圈内容拦截。
            mFilters.add(new Filter.WeChatValidFilter(loadPackageParam.classLoader));
            //聊天详情中的文字点击事件优化
            try {
                findAndHookMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.ui.widget.MMTextView"), "onTouchEvent",
                        MotionEvent.class, new MMTextViewTouchEvent());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                findAndHookMethod(loadPackageParam.classLoader.loadClass("com.tencent.mm.ui.base.MMTextView"), "onTouchEvent",
                        MotionEvent.class, new MMTextViewTouchEvent());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        // installer  不注入。 防止代码出错。进不去installer 中。
        if (!"de.robv.android.xposed.installer".equals(loadPackageParam.packageName)) {
            findAndHookMethod(Activity.class, "onTouchEvent", MotionEvent.class, new ActivityTouchEvent());
            findAndHookMethod(View.class, "onTouchEvent", MotionEvent.class, new ViewTouchEvent());
        }
    }


    private void setXpoedEnable(XC_LoadPackage.LoadPackageParam loadPackageParam) throws ClassNotFoundException {
        if (loadPackageParam.packageName.startsWith("com.forfan.bigbang")) {
            findAndHookMethod(loadPackageParam.classLoader.loadClass("com.shang.xposed.XposedEnable"), "isEnable", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    return true;
                }
            });
        }
    }

    private class MMTextViewTouchEvent extends XC_MethodHook {

        private boolean intercept = false;

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
            //拦截聊天界面快速点击进入信息详情
            View view = (View) param.thisObject;
            MotionEvent event = (MotionEvent) param.args[0];

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    long preClickTimeMillis = mTouchHandler.getClickTimeMillis(view);
                    long currentTimeMillis = System.currentTimeMillis();
                    if (preClickTimeMillis != 0) {
                        long interval = currentTimeMillis - preClickTimeMillis;
                        if (interval < TouchEventHandler.BIG_BANG_RESPONSE_TIME) {
                            intercept = true;
                        } else {
                            intercept = false;
                        }
                    } else {
                        intercept = false;
                    }
                    break;
            }
            mTouchHandler.hookTouchEvent(view, event, mFilters, true);
            if (intercept) {
                param.setResult(true);
            }

        }
    }


    private class ActivityTouchEvent extends XC_MethodHook {

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
            Activity activity = (Activity) param.thisObject;
            View view = activity.findViewById(android.R.id.content);
            MotionEvent event = (MotionEvent) param.args[0];
            Logger.d(TAG, "activityTouchEvent: " + event.getAction());
            mTouchHandler.hookTouchEvent(view, event, mFilters, false);
        }
    }


    private class ViewTouchEvent extends XC_MethodHook {

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);

            if ((Boolean) param.getResult()) {
                View view = (View) param.thisObject;
                MotionEvent event = (MotionEvent) param.args[0];
                Logger.d(TAG, view.getClass().getSimpleName());
                Logger.d(TAG, "viewTouchEvent: " + event.getAction());
                mTouchHandler.hookTouchEvent(view, event, mFilters, false);
            }
        }
    }

}