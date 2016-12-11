package com.shang.commonjar.contentProvider;

/**
 * Created by l4656_000 on 2015/11/30.
 */
public class ConstantUtil {

    public static final String CONTENT="content://";
    public static final String AUTHORITY="com.forfun.bigbang";
    public static final String SEPARATOR= "/";
    public static final String CONTENT_URI =CONTENT+AUTHORITY;

    public static final String TYPE_STRING="string";
    public static final String TYPE_INT="int";
    public static final String TYPE_LONG="long";
    public static final String TYPE_FLOAT="float";
    public static final String TYPE_BOOLEAN="boolean";
    public static final String TYPE_CONTAIN="contain";
    public static final String TYPE_CLEAN="clean";
    public static final String DEFAULT_CURSOR_NAME= "default";
    public static final String VALUE= "value";
    public static final String NULL_STRING= "null";


    public static final String BROADCAST_RELOAD_SETTING="broadcast_reload_setting";
    public static final String BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED ="broadcast_bigbang_monitor_service_modified";

    public static final String BROADCAST_CLIPBOARD_LISTEN_SERVICE_MODIFIED="broadcast_clipboard_listen_service_modified";

    public static final String BROADCAST_SET_TO_CLIPBOARD="broadcast_set_to_clipboard";
    public static final String BROADCAST_SET_TO_CLIPBOARD_MSG="broadcast_set_to_clipboard_msg";




    //shareCard
    public static final String HAD_SHARED="had_shared";
    public static final String SETTING_OPEN_TIMES="setting_open_times";

    //FunctionSettingCard
    public static final String MONITOR_CLIP_BOARD="monitor_clip_board";
    public static final String MONITOR_CLICK="monitor_click";
    public static final String TOTAL_SWITCH="total_switch";
    public static final String SHOW_FLOAT_VIEW="show_float_view";
    public static final String REMAIN_SYMBOL="remain_symbol";
    public static final String REMAIN_SECTION="remain_section";

    //floatview
    public static final String FLOAT_SWITCH_STATE="float_switch_state";
    public static final String FLOAT_VIEW_LAND_X="float_view_land_x";
    public static final String FLOAT_VIEW_LAND_Y="float_view_land_Y";
    public static final String FLOAT_VIEW_PORT_X="float_view_port_x";
    public static final String FLOAT_VIEW_PORT_Y="float_view_port_y";

    //FeedBackAndUpdateCard

    //MonitorSettingCard
    public static final String TEXT_ONLY="text_only";
    public static final String QQ_SELECTION="qq_selection";
    public static final String WEIXIN_SELECTION="weixin_selection";
    public static final String OTHER_SELECTION="other_selection";

    public static final String BROWSER_SELECTION="browser_selection";


    public static final String Setting_content_Changes ="tencent_contents_change";
    public static final String SHOW_TENCENT_SETTINGS = "tencent_settings";


    public static final String ONLINE_CONFIG_OPEN_UPDATE="online_config_open_update";
    public static final String DOUBLE_CLICK_INTERVAL="double_click_interval";
    public static final int DEFAULT_DOUBLE_CLICK_INTERVAL = 1000;


    //SettingBigBangActivity
    public static final String TEXT_SIZE="text_size";
    public static final String LINE_MARGIN="line_margin";
    public static final String ITEM_MARGIN="item_margin";
    public static final String USE_LOCAL_WEBVIEW="use_local_webview";



    public static final int DEFAULT_TEXT_SIZE=14;
    public static final int DEFAULT_LINE_MARGIN=8;
    public static final int DEFAULT_ITEM_MARGIN=0;


    //whiteListActivity
    public static final String WHITE_LIST_COUNT ="white_list_count";
    public static final String WHITE_LIST ="white_list";
    public static final String REFRESH_WHITE_LIST_BROADCAST ="refresh_white_list_broadcast";

    public static final String HAS_ADDED_LAUNCHER_AS_WHITE_LIST="has_added_launcher_as_white_list";


    public static final String UNIVERSAL_COPY_BROADCAST="universal_copy_broadcast";
    public static final String SCREEN_CAPTURE_OVER_BROADCAST="screen_capture_over_broadcast";


    //xposed
    public static final String XPOSED_USE_CLICK="xposed_use_click";
}
