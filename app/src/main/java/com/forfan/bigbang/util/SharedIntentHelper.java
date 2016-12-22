package com.forfan.bigbang.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.widget.Toast;

import com.forfan.bigbang.component.activity.BigBangActivity;
import com.forfan.bigbang.onestep.ResolveInfoWrap;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SharedIntentHelper {
    public static final int TYPE_URL = 0;
    private static final int TYPE_GEO = 1;
    private static final int TYPE_TEL = 2;
    private static final int TYPE_SEND = 3;

    public static List<ResolveInfoWrap> listIntents(Context paramContext) {
        ArrayList localArrayList = new ArrayList();
        Intent localIntent1 = new Intent("android.intent.action.VIEW", Uri.parse("geo:0,0"));
        Intent localIntent2 = new Intent("android.intent.action.DIAL", Uri.parse("tel:10086"));
        Object localObject = new Intent("android.intent.action.VIEW", Uri.parse("http://www.baidu.com"));
        localObject = paramContext.getPackageManager().queryIntentActivities((Intent) localObject, 0).iterator();
        while (((Iterator) localObject).hasNext()) {
            ResolveInfo localResolveInfo = (ResolveInfo) ((Iterator) localObject).next();
            if (localResolveInfo.activityInfo.name.contains("taobao")) {
                localArrayList.add(new ResolveInfoWrap(localResolveInfo, TYPE_URL));
            }
        }
        for (ResolveInfo r : paramContext.getPackageManager().queryIntentActivities(localIntent1, 0)) {
            localArrayList.add(new ResolveInfoWrap(r, TYPE_GEO));
        }
        for (ResolveInfo r : paramContext.getPackageManager().queryIntentActivities(localIntent2, 0)) {
            localArrayList.add(new ResolveInfoWrap(r, TYPE_TEL));
        }

        localIntent1 = new Intent("android.intent.action.SEND");
        localIntent1.setType("text/plain");
        for (ResolveInfo r : paramContext.getPackageManager().queryIntentActivities(localIntent1, 0)) {
            localArrayList.add(new ResolveInfoWrap(r, TYPE_SEND));
        }
        return localArrayList;
    }

    public static void sendImageShareIntent(Context paramContext, String paramString) {
        Intent localIntent = new Intent("android.intent.action.SEND");
        localIntent.addFlags(268435456);
        localIntent.setType("image/*");
        localIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(paramString)));
        Intent.createChooser(localIntent, "请选择").addFlags(268435456);
        paramContext.startActivity(localIntent);
    }

    public static void sendShareIntent(Context context, String paramString) {
        ArrayList<ResolveInfo> resolveArrayList = new ArrayList();
        Intent geoIntent = new Intent("android.intent.action.VIEW", Uri.parse("geo:0,0?q=" + paramString));
        Intent callIntent = new Intent("android.intent.action.DIAL", Uri.parse("tel:10086"));
        Intent urlInent = new Intent("android.intent.action.VIEW", Uri.parse("http://www.baidu.com"));
        List<ResolveInfo> callResoves = context.getPackageManager().queryIntentActivities(callIntent, 0);
        List<ResolveInfo> geoResolves = context.getPackageManager().queryIntentActivities((Intent) geoIntent, 0);
        List<ResolveInfo> urlResolves = context.getPackageManager().queryIntentActivities((Intent) urlInent, 0);
        Intent textIntent = new Intent("android.intent.action.SEND");
        textIntent.setType("text/plain");
        List<ResolveInfo> textResolves = context.getPackageManager().queryIntentActivities(textIntent, 0);

        resolveArrayList.addAll(callResoves);
        resolveArrayList.addAll(geoResolves);
        resolveArrayList.addAll(textResolves);
        ArrayList<Intent> intentArrayList = new ArrayList<>();
        if (textResolves.size() > 0) {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra("android.intent.extra.TEXT", paramString);
            intentArrayList.add(intent);
        }
        for (ResolveInfo resolveInfo : geoResolves) {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("geo:0,0?q=" + paramString));
            intent.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
            intentArrayList.add(intent);
        }
        for(ResolveInfo resolveInfo :urlResolves){
            if(resolveInfo.activityInfo.packageName.equalsIgnoreCase("com.taobao.taobao")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://s.taobao.com/search?q=" + paramString));
                intent.setPackage(resolveInfo.activityInfo.packageName);
                intentArrayList.add(intent);
            }
        }
        if (callResoves.size() > 0) {
            intentArrayList.add(new Intent("android.intent.action.DIAL", Uri.parse("tel:" + paramString)));
        }
        if (intentArrayList.size() < 0) {
            ToastUtil.show("未能找到可以分享的应用");
            return;
        }

//        LabeledIntent[] extraIntents = new LabeledIntent[intentArrayList.size()];
//        extraIntents = intentArrayList.toArray(extraIntents);
        Intent firstIntent = intentArrayList.remove(0); // assuming you will have at least one Intent
        if (firstIntent == null) {
            ToastUtil.show("未能找到可以分享的应用");
            return;
        }
        Intent openInChooser = Intent.createChooser(firstIntent, "分享到");
        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, (Parcelable[]) ((List) intentArrayList).toArray(new Parcelable[intentArrayList.size()]));
        try {
            context.startActivity(openInChooser);
            return;
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "未能找到可以分享的应用", 0).show();
        }
    }

    public static void share(BigBangActivity bigBangActivity, ResolveInfoWrap item, String mSelectText) {
        if(item.type == TYPE_GEO){
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("geo:0,0?q=" + mSelectText));
            intent.setClassName(item.resolveInfo.activityInfo.packageName, item.resolveInfo.activityInfo.name);
            bigBangActivity.startActivity(intent);
        }else if(item.type == TYPE_SEND){
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra("android.intent.extra.TEXT", mSelectText);
            intent.setPackage(item.resolveInfo.activityInfo.packageName);
            bigBangActivity.startActivity(intent);
        }else if(item.type == TYPE_TEL){
           Intent intent =  new Intent("android.intent.action.DIAL", Uri.parse("tel:" + mSelectText));
            intent.setPackage(item.resolveInfo.activityInfo.packageName);
            bigBangActivity.startActivity(intent);
        }else if(item.type ==TYPE_URL){
            if(item.resolveInfo.activityInfo.packageName.equalsIgnoreCase("com.taobao.taobao")){
                Intent intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://s.taobao.com/search?q="+mSelectText));
                intent.setPackage(item.resolveInfo.activityInfo.packageName);
                bigBangActivity.startActivity(intent);
            }else {
                Intent intent =  new Intent(Intent.ACTION_VIEW, Uri.parse(mSelectText));
                intent.setPackage(item.resolveInfo.activityInfo.packageName);
                bigBangActivity.startActivity(intent);
            }
        }
    }
}
