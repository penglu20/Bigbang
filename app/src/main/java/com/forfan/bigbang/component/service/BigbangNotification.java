package com.forfan.bigbang.component.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.activity.SplashActivity;
import com.forfan.bigbang.component.activity.screen.ScreenCaptureActivity;
import com.forfan.bigbang.component.activity.setting.SettingActivity;
import com.forfan.bigbang.util.ConstantUtil;
import com.shang.commonjar.contentProvider.SPHelper;


public class BigbangNotification {

	public static final String SEARCH_ACTION="show_search_action";

    private Notification notification;
    private NotificationManager notificationManager;
    private Context mContext;
    private RemoteViews contentView;

    public BigbangNotification(Context context){
    	try{
	        this.mContext = context;
	        initNotification();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}catch(Error ignored) {
        }
    }
    
    /**
     * 初始化Notification，此处做Notification的基本设置
     */
    private void initNotification(){
    	try{//解决崩溃问题
	        PendingIntent contentPendingIntent = createPendingIntent(mContext,
					SettingActivity.class);
	        //获取notification管理的实例
	        notificationManager = (NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
	        createNotification();
			notification.flags = Notification.FLAG_ONGOING_EVENT;
	        notification.contentIntent = contentPendingIntent;
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}catch(Throwable e){
    		e.printStackTrace();
    	}
    }
    
    /**
     * 创建通知栏
     * 设置setWhen(0)解决通知栏置顶问题
     * nnd,以前老被搜狗挤下去==
     */
	private void createNotification(){
    	NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(mContext);
    	//设置notification, 到时候替换压缩图片， 通过没ContentBean获取
		notification = mNotifyBuilder.setContentTitle("")
				 .setContentText("")
// 		        .setTicker(mContext.getResources().getString(R.string.notify_quick_message_ticket))
				 .setWhen(0)
				 .setSmallIcon(R.mipmap.ic_launcher)
				 .setPriority(Notification.PRIORITY_MIN)//这里改成PRIORITY_MAX_Min就可以不显示状态栏的图标了
				 .setAutoCancel(true)
				 .build();
		setContetView();
    }
    
    /**
     * 设置contentView
     * 
     */
    public void setContetView()
    {
		// 在2.3到5.1以前, 通知栏的背景是黑色的, 所以RemoteView的背景色可以设置为透明
		contentView = new RemoteViews(mContext.getPackageName(),R.layout.notification_trans);

		boolean isRun= SPHelper.getBoolean(ConstantUtil.TOTAL_SWITCH,true);

		boolean clipborad= SPHelper.getBoolean(ConstantUtil.MONITOR_CLIP_BOARD,true);
		boolean click = SPHelper.getBoolean(ConstantUtil.MONITOR_CLICK,true);


		String totalSwitch=!isRun?mContext.getString(R.string.notify_total_switch_off):mContext.getString(R.string.notify_total_switch_on);
		String monitorClick=!click?mContext.getString(R.string.notify_monitor_click_off):mContext.getString(R.string.notify_monitor_click_on);
		String monitorClipboard=!clipborad?mContext.getString(R.string.notify_monitor_clipboard_off):mContext.getString(R.string.notify_monitor_clipboard_on);

		int totalSwitccRes=!isRun?R.drawable.notify_off:R.drawable.notify_on;
		int monitorClickRes=!click?R.drawable.notify_click_off:R.drawable.notify_click_on;
		int monitorClipboardRes=!clipborad?R.drawable.notify_clipboare_off:R.drawable.notify_clipboard_on;


		int totalSwitccColor=!isRun?R.color.text_color_notify:R.color.colorPrimary;
		int monitorClickColor=!click?R.color.text_color_notify:R.color.colorPrimary;
		int monitorClipboardColor=!clipborad?R.color.text_color_notify:R.color.colorPrimary;


        //SettingActivity的跳转， 在SDK 3.0 （11）之上
        try {
			contentView.setViewVisibility(R.id.total_switch, View.VISIBLE);
			contentView.setOnClickPendingIntent(R.id.total_switch, createPendingIntent(mContext.getPackageName(),123456 ,ConstantUtil.TOTAL_SWITCH_BROADCAST));
			contentView.setTextViewText(R.id.total_switch, totalSwitch);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				contentView.setTextViewCompoundDrawables(R.id.total_switch,0,totalSwitccRes,0,0);
			}
			contentView.setTextColor(R.id.total_switch,mContext.getResources().getColor(totalSwitccColor));

			contentView.setViewVisibility(R.id.monitor_click, View.VISIBLE);
			contentView.setOnClickPendingIntent(R.id.monitor_click, createPendingIntent(mContext.getPackageName(), 123457 ,ConstantUtil.MONITOR_CLICK_BROADCAST));
			contentView.setTextViewText(R.id.monitor_click,monitorClick);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				contentView.setTextViewCompoundDrawables(R.id.monitor_click,0,monitorClickRes,0,0);
			}
			contentView.setTextColor(R.id.monitor_click,mContext.getResources().getColor(monitorClickColor));

			contentView.setViewVisibility(R.id.monitor_clipboard, View.VISIBLE);
			contentView.setOnClickPendingIntent(R.id.monitor_clipboard, createPendingIntent(mContext.getPackageName(), 123458 ,ConstantUtil.MONITOR_CLIPBOARD_BROADCAST));
			contentView.setTextViewText(R.id.monitor_clipboard,monitorClipboard);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				contentView.setTextViewCompoundDrawables(R.id.monitor_clipboard,0,monitorClipboardRes,0,0);
			}
			contentView.setTextColor(R.id.monitor_clipboard,mContext.getResources().getColor(monitorClipboardColor));

			contentView.setViewVisibility(R.id.universal_copy, View.VISIBLE);
			contentView.setOnClickPendingIntent(R.id.universal_copy, createPendingIntent(mContext,  SplashActivity.class,ConstantUtil.NOTIFY_UNIVERSAL_COPY_BROADCAST));
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				contentView.setTextViewCompoundDrawables(R.id.universal_copy,0,R.drawable.notify_copy,0,0);
			}

			contentView.setViewVisibility(R.id.screen_cap, View.VISIBLE);
			contentView.setOnClickPendingIntent(R.id.screen_cap, createPendingIntent(mContext,  ScreenCaptureActivity.class ,ConstantUtil.NOTIFY_SCREEN_CAPTURE_OVER_BROADCAST));
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				contentView.setTextViewCompoundDrawables(R.id.screen_cap,0,R.drawable.notify_screen,0,0);
			}

		} catch (Exception ignored) {
        } catch (Error error) {}
        contentView.setOnClickPendingIntent(R.id.Layout_notify_msearch, createPendingIntent(mContext,
				SettingActivity.class));
        notification.contentView = contentView;
    }

    /**
     * 生产PendingIntent
     */
    private PendingIntent createPendingIntent(String packageName, int requestCode,String target){
        Intent contentIntent = new Intent(target);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, requestCode, contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

	/**
	 * 生产PendingIntent
	 */
	private PendingIntent createPendingIntent(Context context, Class<? extends Activity> activity,String action) {
		Intent contentIntent = new Intent(context, activity);
		contentIntent.setAction(action);
		contentIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext, R.string.app_name, contentIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}

	/**
	 * 生产PendingIntent
	 */
	private PendingIntent createPendingIntent(Context context, Class<? extends Activity> action){
		Intent contentIntent = new Intent(context,action);
		contentIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext, R.string.app_name, contentIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);


//		Intent shortcutIntent = getShortCutIntent(context, UrlConstants.NTP_URL, "com.qihoo.browser.activity.SplashActivity");
//		shortcutIntent.setData(Uri.parse(action));
//
//		shortcutIntent.setAction(ACTION_DESKTOP_LINK);
//		shortcutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		if (CompatibilitySupport.isNX511J()) {
//			shortcutIntent.setComponent(new ComponentName(context, ChromeLauncherActivity.class));
//		}
//		shortcutIntent.putExtra("jumpTo", "youlike");
//		shortcutIntent.putExtra("from", BrowserControllerHelper.EXTRA_ANDROID_BROWSER);
//		shortcutIntent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
		return pendingIntent;

	}

	public Notification getNotification(){
		return notification;
	}
    
}
