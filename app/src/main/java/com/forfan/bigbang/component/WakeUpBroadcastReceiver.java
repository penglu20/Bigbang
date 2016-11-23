package com.forfan.bigbang.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.forfan.bigbang.component.service.BigBangMonitorService;
import com.forfan.bigbang.component.service.ListenClipboardService;
import com.forfan.bigbang.util.LogUtil;

/**
 * Created by wangyan-pd on 2016/11/23.
 */

public class WakeUpBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.e("shang","xposed wake");
        try {
            context.startService(new Intent(context,ListenClipboardService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }
    }
}
