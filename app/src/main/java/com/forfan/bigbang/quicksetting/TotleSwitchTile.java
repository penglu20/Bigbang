package com.forfan.bigbang.quicksetting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.activity.TotalSwitchActivity;
import com.forfan.bigbang.component.activity.screen.ScreenCaptureActivity;
import com.forfan.bigbang.util.ConstantUtil;
import com.shang.commonjar.contentProvider.SPHelper;

/**
 * Created by wangyan-pd on 2017/1/12.
 */

public class TotleSwitchTile extends TileService {
    private static final String LOG_TAG = TotleSwitchTile.class.getCanonicalName();
    private final int STATE_OFF = 0;
    private final int STATE_ON = 1;
    private int toggleState = STATE_ON;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           boolean enable = SPHelper.getBoolean(ConstantUtil.TOTAL_SWITCH,true);
            toggleState = enable ? STATE_ON :STATE_OFF;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstantUtil.BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED);
        try {
            registerReceiver(receiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick() {
        Log.d(LOG_TAG, "onClick state = " + Integer.toString(getQsTile().getState()));
        Icon icon;
        String title;
        if (toggleState == STATE_ON) {
            toggleState = STATE_OFF;
            title = getResources().getString(R.string.notify_total_switch_off);
            icon =  Icon.createWithResource(getApplicationContext(), R.drawable.notify_off);
            getQsTile().setState(Tile.STATE_INACTIVE);// 更改成非活跃状态
        } else {
            toggleState = STATE_ON;
            title = getResources().getString(R.string.notify_total_switch_on);
            icon = Icon.createWithResource(getApplicationContext(), R.drawable.notify_on);
            getQsTile().setState(Tile.STATE_ACTIVE);//更改成活跃状态
        }
        getQsTile().setIcon(icon);//设置图标
        getQsTile().setLabel(title);
        getQsTile().updateTile();//更新Tile

        Intent intent = new Intent();
        intent.setClass(this, TotalSwitchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityAndCollapse(intent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
