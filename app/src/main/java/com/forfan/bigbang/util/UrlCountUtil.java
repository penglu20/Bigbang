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

    public static final String CLICK_SETTINGS_OPEN_OCR ="click_settings_open_ocr";
    public static final String CLICK_SETTINGS_FEEDBACK ="click_settings_feedback";
    public static final String CLICK_SETTINGS_SET_STYLE_BIGBANG ="click_settings_set_style_bigbang";
    public static final String CLICK_SETTINGS_CHECK_FOR_UPDATE ="click_settings_check_for_update";
    public static final String CLICK_SETTINGS_HOW_TO_USE ="click_settings_how_to_use";
    public static final String CLICK_SETTINGS_ABOUT ="click_settings_about";
    public static final String CLICK_SETTINGS_JOIN_QQ ="click_settings_join_qq";
    public static final String CLICK_SETTINGS_DONATE ="click_settings_donate";
    public static final String CLICK_SETTINGS_PROBLEM = "click_settings_problem";
    public static final String CLICK_SETTINGS_WHITELIST = "click_settings_whitelist";
    public static final String CLICK_SETTINGS_DOUBLECLICK_SETTING = "click_settings_doubleclick_setting";
    public static final String CLICK_SETTINGS_DOUBLECLICK_SETTING_CONFORM = "click_settings_doubleclick_setting_conform";

    public static final String STATUS_ACCESSABILITY = "status_accessability";
    public static final String STATUS_CLIPBOARD = "status_clipboard";
    public static final String STATUS_SHOW_FLOAT_WINDOW = "status_show_float_window";
    public static final String STATUS_PUNCTUATION = "status_punctuation";
    public static final String STATUS_USE_BUILTIN_BROWSER= "status_use_built_in_browser";
    public static final String STATUS_ONLY_TEXT_MONITOR= "status_only_text_monitor";

    public static final String STATUS_SPINNER_WEIXIN= "status_spinner_weixin";
    public static final String STATUS_SPINNER_QQ= "status_spinner_qq";
    public static final String STATUS_SPINNER_OTHER= "status_spinner_other";

    public static final String CLICK_OCR_PICK_FROM_GALLERY= "click_ocr_pick_from_gallery";
    public static final String CLICK_OCR_TAKEPICTURE= "click_ocr_takepicture";
    public static final String CLICK_OCR_FROM_SHARE= "click_ocr_from_share";
    public static final String CLICK_OCR_REOCR= "click_ocr_re_ocr";
    public static final String CLICK_OCR_TO_BIGBANG_ACTIVITY= "click_ocr_to_bigbang_activity";

    public static final String CLICK_TIPVIEW_BACK= "click_tipview_back";
    public static final String CLICK_TIPVIEW_SCREEN= "click_tipview_screen";
    public static final String CLICK_TIPVIEW_COPY= "click_tipview_copy";
    public static final String CLICK_TIPVIEW_SWITCH= "click_tipview_switch";
    public static final String CLICK_TIPVIEW_IMAAGEVIEW= "click_tipview_imaageview";
    public static final String CLICK_TIPVIEW_SETTING_ACTICITY= "longclick_tipview_setting_acticity";

    public static final String CLICK_BROWSER_EXIT= "click_browser_exit";
    public static final String CLICK_BROWSER_TO_SYS_BROWSER= "click_browser_to_sys_browser";
    public static final String STATE_BROWSER_Engines= "state_browser_engines";

    public static final String CLICK_UNIVERSAL_COPY_EXIT_RETUN= "click_universal_copy_exit_retun";
    public static final String CLICK_UNIVERSAL_COPY_EXIT= "click_universal_copy_exit";
    public static final String CLICK_UNIVERSAL_COPY_EDIT= "click_universal_copy_edit";
    public static final String CLICK_UNIVERSAL_COPY_EXIT_FAB= "click_universal_copy_exit_fab";
    public static final String CLICK_UNIVERSAL_COPY_EXIT_FULLSCREEN_ACTION= "click_universal_copy_exit_fullscreen_action";
    public static final String CLICK_UNIVERSAL_COPY_EXIT_FULLSCREEN_FAB= "click_universal_copy_exit_fullscreen_fab";
    public static final String CLICK_UNIVERSAL_COPY_COPY_FAB= "click_universal_copy_copy_fab";
    public static final String CLICK_UNIVERSAL_COPY_COPY_ACTION= "click_universal_copy_copy_action";



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
    public static void onEvent(String eventId, boolean value){
        MobclickAgent.onEvent(mContext, eventId, String.valueOf(value));
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
