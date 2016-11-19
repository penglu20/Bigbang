package com.forfan.bigbang.util;

import android.content.Context;

import com.forfan.bigbang.BigBangApp;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * Created by penglu on 2015/12/17.
 */
public class UrlCountUtil {

    //打点的点的定义统一在这里
    public static final String CLICK_STATISTICS_CHOOSE_DATE ="click_statistics_choose_date";
    public static final String CLICK_SETTINGS_OPEN_OCR ="CLICK_SETTINGS_OPEN_OCR";
    public static final String CLICK_SETTINGS_FEEDBACK ="CLICK_SETTINGS_FEEDBACK";
    public static final String CLICK_SETTINGS_SET_STYLE_BIGBANG ="CLICK_SETTINGS_SET_STYLE_BIGBANG";
    public static final String CLICK_SETTINGS_CHECK_FOR_UPDATE ="CLICK_SETTINGS_CHECK_FOR_UPDATE";
    public static final String CLICK_SETTINGS_HOW_TO_USE ="CLICK_SETTINGS_HOW_TO_USE";
    public static final String CLICK_SETTINGS_ABOUT ="CLICK_SETTINGS_HOW_TO_USE";
    public static final String CLICK_SETTINGS_JOIN_QQ ="CLICK_SETTINGS_JOIN_QQ";
    public static final String CLICK_SETTINGS_DONATE ="CLICK_SETTINGS_DONATE";
    public static final String CLICK_SETTINGS_PROBLEM = "CLICK_SETTINGS_PROBLEM";

    public static Context mContext= BigBangApp.getInstance();
    /*
        context指当前的Activity，eventId为当前统计的事件ID。
        示例：统计微博应用中"转发"事件发生的次数，那么在转发的函数里调用
        MobclickAgent.onEvent(mContext,"Forward");
     */
    public static void onEvent(String eventId){
        MobclickAgent.onEvent(mContext, eventId);
    }

    /*
        map 为当前事件的属性和取值（Key-Value键值对）。
        示例：统计电商应用中“购买”事件发生的次数，以及购买的商品类型及数量，那么在购买的函数里调用：
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("type","book");
        map.put("quantity","3");
        MobclickAgent.onEvent(mContext, "purchase", map);
     */
    public static void onEvent(String eventId, HashMap<String,String> map){
        MobclickAgent.onEvent(mContext, eventId, map);
    }

    public static void onEvent(String eventId, String value){
        MobclickAgent.onEvent(mContext, eventId, value);
    }

    /*
        统计一个数值类型的连续变量（该变量必须为整数），用户每次触发的数值的分布情况，如事件持续时间、每次付款金额等，可以调用如下方法：
        MobclickAgent.onEventValue(Context context, String id, Map<String,String> m, int du)
        id 为事件ID
        map 为当前事件的属性和取值
        du 为当前事件的数值为当前事件的数值，取值范围是-2,147,483,648 到 +2,147,483,647 之间的有符号整数，即int 32类型，如果数据超出了该范围，会造成数据丢包，影响数据统计的准确性。
        示例：统计一次音乐播放，包括音乐类型，作者和播放时长，可以在音乐播放结束后这么调用：
        int duration = 12000; //开发者需要自己计算音乐播放时长
        　　Map<String, String> map_value = new HashMap<String, String>();
        　　map_value.put("type", "popular");
        　　map_value.put("artist", "JJLin");
        MobclickAgent.onEventValue(this, "music", map_value, duration);
     */
    public static void onEventValue(String id, HashMap<String,String> map, int value){
        MobclickAgent.onEventValue(mContext, id, map, value);
    }



}
