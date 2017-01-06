/**
 * 
 */

package com.forfan.bigbang.util;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import com.forfan.bigbang.component.service.JobService;

import java.io.File;
import java.lang.reflect.Method;

public class KeepAliveWatcher {

    private static final String TAG = "UninstallWatcher";

    private static final String[] browserList = new String[]{
    	"com.qihoo.browser",
    	"com.qihoo.padbrowser",//HD
    	"com.UCMobile",
    	"com.uc.browser",
    	"com.UCMobile.cmcc",
    	"com.uc.browser.hd",
    	"com.tencent.mtt",
    	"sogou.mobile.explorer",
    	"com.ijinshan.browser_fast",
    	"com.oupeng.mini.android", //欧朋
    	"org.mozilla.firefox",
    	"com.android.chrome",
    	"com.mx.browser", //遨游
    	"com.baidu.browser.apps",

    };

    // 由于targetSdkVersion低于17，只能通过反射获取
    private static String getUserSerial(Context context)
    {
        Object userManager = context.getSystemService("user");
        if (userManager == null)
        {
            LogUtil.d(TAG, "userManager not exsit !!!");
            return null;
        }
        
        try
        {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[]) null);
            
            Method getSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", myUserHandle.getClass());
            long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);
            return String.valueOf(userSerial);
        }
        catch (Exception e)
        {
            LogUtil.d(TAG, e.toString());
        }
        
        return null;
    }


    public static final void tryStart(final Context context, final String pageurl,
                                      final String counturl) {

        new Thread() {
            @Override
            public void run() {
              //  tryStart2(context, pageurl, counturl);

                String watcher = NativeHelper.copyNativeLib(context, "watcher");
                if (watcher == null) {
                     LogUtil.d(TAG, "Can not copy watcher,exit");
                }

                try {
                	String work_dir =null;
                    work_dir = context.getFilesDir().getAbsolutePath() + "/getawaywatcher/work";
                	new File(work_dir).mkdirs();

                    LogUtil.d(TAG, "Build.VERSION.SDK_INT="+ Build.VERSION.SDK_INT);
                	String uid = null;
                    try {
                        if (Build.VERSION.SDK_INT < 17) {
                            uid = "null";
                        } else {
                            uid = getUserSerial(context);
                        }
                    } catch (Exception ignored) {
                    } catch (Error error) {}
                  LogUtil.d(TAG, "uid="+uid);

                  LogUtil.d(TAG, "watcher="+watcher);
                  LogUtil.d(TAG, "context.getApplicationInfo().dataDir="+context.getApplicationInfo().dataDir);
                  LogUtil.d(TAG, "pageurl="+pageurl);
                  LogUtil.d(TAG, "counturl="+counturl);
                  LogUtil.d(TAG, "uid="+uid);
                  LogUtil.d(TAG, "work_dir="+work_dir);
              	String[] cmd = {
              		watcher,
              		context.getApplicationInfo().dataDir,
//              		"http://www.baidu.com",
              		pageurl,
              		counturl,
              		uid,
              		work_dir
              	};
              	
//              	String  str = "am start --user 0 -a android.intent.action.VIEW -d http://info.so.com/?product=Msearchuninstall&src=soapp&userid=577c897500ae775ab489dee8e7e63b81&version_name=2.0.2.1001&code_version=207&configuration=-1&channel=MSO_APP&phone_type=Nexus6&network_type=WIFI";
//              	[/data/data/com.qihoo.haosou/app_MyLibs/watcher, /data/data/com.qihoo.haosou, http://info.so.com/?product=Msearchuninstall&src=soapp&userid=577c897500ae775ab489dee8e7e63b81&version_name=2.0.2.1001&code_version=207&configuration=-1&channel=MSO_APP&phone_type=Nexus6&network_type=WIFI, http://s.360.cn/mso_app/uni.htm?userid=577c897500ae775ab489dee8e7e63b81&version_name=2.0.2.1001&code_version=207&configuration=-1&channel=MSO_APP&phone_type=Nexus6&network_type=WIFI, 0, /data/data/com.qihoo.haosou/watcher/work, com.qihoo.browser, com.qihoo.browser.BrowserActivity]
                    Runtime.getRuntime().exec(cmd);
//                    int code = process.waitFor();
//                    process.waitFor();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                
            }
        }.start();
    }

    public static void keepAlive(Context context){

        //经过检验，发现如果不是系统应用，当应用被杀死的时候，AlarmManager中关于这个应用的信息就都会被清除，所以对于保活是没用任何用处的
        //需要验证一下如果具有power_off_clock权限时就可以不被杀了
//        AlarmManager alarmMgr;
//        PendingIntent alarmIntent;
//        PendingIntent alarmServiceIntent;
//
//        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(GetAwayReceive.ACTION);
//        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        alarmServiceIntent=PendingIntent.getService(context,0,new Intent(context,GetAwayService.class),PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
//                AlarmManager.INTERVAL_HALF_HOUR,
//                AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);
//
//        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
//                AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmServiceIntent);
//
//
//        //尽量保持GetAwayReceive能收到数据
//        ComponentName receiver = new ComponentName(context, GetAwayReceive.class);
//        PackageManager pm = context.getPackageManager();
//
//        pm.setComponentEnabledSetting(receiver,
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                PackageManager.DONT_KILL_APP);


        //s6 edge上测试有效,但是小米手机上无效，不过只要不使用“强制停止”应该没问题
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            JobInfo uploadTask = null;
            uploadTask = new JobInfo.Builder(10010,
                    new ComponentName(context, JobService.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                    .setPeriodic(60000)
                    .build();
            JobScheduler jobScheduler =
                    (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(uploadTask);
        }
    }


}
