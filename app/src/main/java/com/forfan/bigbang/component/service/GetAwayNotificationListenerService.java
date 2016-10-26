package com.forfan.bigbang.component.service;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@TargetApi(18)
public class GetAwayNotificationListenerService extends NotificationListenerService
{
    private static final String TAG = GetAwayNotificationListenerService.class.getSimpleName();

    private static List getEnabledNotificationListeners(ContentResolver paramContentResolver)
    {
        ArrayList localArrayList = new ArrayList();
        if(paramContentResolver!=null){
            String resolver = Settings.Secure.getString(paramContentResolver, "enabled_notification_listeners");
            if (resolver!=null && (!"".equals(resolver))) {
                String[] resolvers = resolver.split(":");
                if (resolvers!=null){
                    int i = 0;
                    while (i < resolvers.length) {
                        try {
                            ComponentName localComponentName = ComponentName.unflattenFromString(resolvers[i]);
                            if (localComponentName != null) {
                                localArrayList.add(localComponentName);
                            }
                        } catch (Exception localException) {
                            localException.printStackTrace();
                        }
                        i += 1;
                    }
                }

            }
        }
        return localArrayList;
    }

    private static void addNotificationListeners(ContentResolver paramContentResolver, List paramList)
    {
        ComponentName localComponentName = null;
        Iterator localIterator = paramList.iterator();
        StringBuilder listenersString =new StringBuilder();
        while (localIterator.hasNext())
        {
            localComponentName = (ComponentName)localIterator.next();
            listenersString.append(localComponentName.flattenToString());
            listenersString.append(':');
        }
        if (listenersString.length()>1) {
            listenersString.deleteCharAt(listenersString.length() - 1);
        }
        Settings.Secure.putString(paramContentResolver, "enabled_notification_listeners", listenersString.toString());
    }

    public static void enableNotificationListener(Context paramContext, ComponentName paramComponentName, boolean paramBoolean){
        if (Build.VERSION.SDK_INT >= 18)
        {
            if (checkWriteSecureSettingPermission(paramContext))
            {
                if (paramBoolean)
                {
                    PackageManager packageManager = paramContext.getPackageManager();
                    if (packageManager.getComponentEnabledSetting(paramComponentName) != 1) {
                        packageManager.setComponentEnabledSetting(paramComponentName, 1, 1);
                    }
                }
                ContentResolver contentResolver = paramContext.getContentResolver();
                List localObject = getEnabledNotificationListeners(contentResolver);
                if (paramBoolean) {
                    if (!(localObject).contains(paramComponentName))
                    {
                        (localObject).add(paramComponentName);
                        addNotificationListeners(contentResolver, localObject);
                    }
                }else {
                    localObject.remove(localObject);
                    addNotificationListeners(contentResolver, localObject);
                }
                return;
            }
            throw new SecurityException("android.permission.WRITE_SECURE_SETTINGS not be granted on this devices(SDK=)" + Build.VERSION.SDK_INT);
        }
        throw new UnsupportedOperationException("ENABLED_NOTIFICATION_LISTENERS not be supported on this devices(SDK=)" + Build.VERSION.SDK_INT);
    }

    public static boolean checkNotificationListenerEnabled(Context paramContext)
    {
        return checkNotificationListenerEnabled(paramContext, new ComponentName(paramContext.getPackageName(), GetAwayNotificationListenerService.class.getName()));
    }

    private static boolean checkNotificationListenerEnabled(Context paramContext, ComponentName paramComponentName)
    {
        if ((paramContext == null) || (paramComponentName == null)) {
            return false;
        }
        return getEnabledNotificationListeners(paramContext.getContentResolver()).contains(paramComponentName);
    }

    public static boolean enableNotificationListenerComponent(Context paramContext, boolean paramBoolean)
    {
        try {
            if (Build.VERSION.SDK_INT < 18) {
                return false;
            }
            if (paramContext != null)
            {
                PackageManager localPackageManager = paramContext.getPackageManager();
                ComponentName componentName = new ComponentName(paramContext.getPackageName(), GetAwayNotificationListenerService.class.getName());
                if (paramBoolean) {
                    localPackageManager.setComponentEnabledSetting(componentName, 1, 1);
                } else {
                    localPackageManager.setComponentEnabledSetting(componentName, 2, 1);
                }
            }
            return true;
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static void autoStart(Context paramContext)
    {
        ComponentName localComponentName;
        PackageManager localPackageManager;
        try {
            localComponentName = new ComponentName(paramContext.getPackageName(), GetAwayNotificationListenerService.class.getName());
            if (Build.VERSION.SDK_INT < 18){
                paramContext.getPackageManager().setComponentEnabledSetting(localComponentName, 2, 1);
                return;
            }
            if (checkNotificationListenerEnabled(paramContext, localComponentName)) {
                return;
            }
            localPackageManager = paramContext.getPackageManager();
            if (Build.VERSION.SDK_INT < 18) {
                return;
            }
            if (checkWriteSecureSettingPermission(paramContext)){
                localPackageManager.setComponentEnabledSetting(localComponentName, 1, 1);
                enableNotificationListener(paramContext, localComponentName, true);
                return;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }



    private static boolean checkWriteSecureSettingPermission(Context paramContext)
    {
        return paramContext.checkPermission("android.permission.WRITE_SECURE_SETTINGS", Process.myPid(), Process.myUid()) == PackageManager.PERMISSION_GRANTED;
    }


    @TargetApi(21)
    public void onInterruptionFilterChanged(int paramInt)
    {
        super.onInterruptionFilterChanged(paramInt);
    }

    @TargetApi(21)
    public void onListenerConnected()
    {
        super.onListenerConnected();
    }

    @TargetApi(18)
    public void onNotificationPosted(StatusBarNotification paramStatusBarNotification)
    {
        super.onNotificationPosted(paramStatusBarNotification);
    }

    @TargetApi(21)
    public void onNotificationPosted(StatusBarNotification paramStatusBarNotification, NotificationListenerService.RankingMap paramRankingMap)
    {
        super.onNotificationPosted(paramStatusBarNotification, paramRankingMap);
    }

    @TargetApi(21)
    public void onNotificationRankingUpdate(NotificationListenerService.RankingMap paramRankingMap)
    {
        super.onNotificationRankingUpdate(paramRankingMap);
    }

    @TargetApi(18)
    public void onNotificationRemoved(StatusBarNotification paramStatusBarNotification)
    {
        super.onNotificationRemoved(paramStatusBarNotification);
    }

    @TargetApi(21)
    public void onNotificationRemoved(StatusBarNotification paramStatusBarNotification, NotificationListenerService.RankingMap paramRankingMap)
    {
        super.onNotificationRemoved(paramStatusBarNotification, paramRankingMap);
    }
}
