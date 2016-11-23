package com.forfan.bigbang.component.activity.screen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.forfan.bigbang.BigBangApp;
import com.forfan.bigbang.R;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.component.contentProvider.SPHelper;
import com.forfan.bigbang.util.ToastUtil;

public class ScreenCaptureActivity extends BaseActivity {
    private String TAG = "Service";
    private int result = 0;
    private Intent intent = null;
    private int REQUEST_MEDIA_PROJECTION = 1;
    private MediaProjectionManager mMediaProjectionManager;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        mMediaProjectionManager = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        boolean isFirst = SPHelper.getBoolean("is_fist",true);
        if(isFirst){
            ToastUtil.show(R.string.need_capture_perssion);
        }
        startIntent();
    }
    private void initWindow() {
        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(localDisplayMetrics);
        localLayoutParams.width = ((int) (localDisplayMetrics.widthPixels * 0.001D));
        localLayoutParams.gravity = 17;
        localLayoutParams.height = ((int) (localDisplayMetrics.heightPixels * 0.001D));
        getWindow().setAttributes(localLayoutParams);
        getWindow().setGravity(17);
        getWindow().getAttributes().windowAnimations = R.anim.anim_scale_in;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startIntent() {
        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
        //ScreenCaptureService.mMediaProjectionManager1 = mMediaProjectionManager;
        ((BigBangApp) getApplication()).setMediaProjectionManager(mMediaProjectionManager);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            } else if (data != null && resultCode != 0) {
                Log.i(TAG, "user agree the application to capture screen");
                //ScreenCaptureService.mResultCode = resultCode;
                //ScreenCaptureService.mResultData = data;
                result = resultCode;
                intent = data;
                ((BigBangApp) getApplication()).setResult(resultCode);
                ((BigBangApp) getApplication()).setIntent(data);
                Intent intent = new Intent(getApplicationContext(), ScreenCaptureService.class);
                startService(intent);
                Log.i(TAG, "start service ScreenCaptureService");

                SPHelper.save("is_fist",false);
                finish();
            }
        }
    }
}
