package com.forfan.bigbang.component.service;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.content.Intent;
import android.os.Build;

import com.forfan.bigbang.util.LogUtil;


/**
 * Created by penglu on 2016/8/29.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JobService extends android.app.job.JobService {
    private static final String TAG="JobService";
    @Override
    public boolean onStartJob(JobParameters params) {
        LogUtil.d(TAG,"onStartJob");

        startService(new Intent(JobService.this,BigBangMonitorService.class));
        startService(new Intent(JobService.this,ListenClipboardService.class));
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        LogUtil.d(TAG,"onStopJob");
        startService(new Intent(JobService.this,BigBangMonitorService.class));
        startService(new Intent(JobService.this,ListenClipboardService.class));
        return false;
    }
}
