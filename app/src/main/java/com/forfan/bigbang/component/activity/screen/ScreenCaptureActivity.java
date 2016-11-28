package com.forfan.bigbang.component.activity.screen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.forfan.bigbang.BigBangApp;
import com.forfan.bigbang.R;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.util.LogUtil;
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
        super.onCreate(null);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ToastUtil.show(R.string.can_not_capture_under_5_0);
            finish();
            return;
        }


        initWindow();
        mMediaProjectionManager = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
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

//    @Override
//    protected void onDestroy() {
//        LogUtil.e("shang", "destory 了");
//        boolean isFirst = SPHelper.getBoolean("is_fist", true);
//
//        if (isFirst) {
//            Intent intent = new Intent();
//            intent.setClass(BigBangApp.getInstance(),ScreenCaptureActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            BigBangApp.getInstance().startActivity(intent);
//            SPHelper.save("is_fist", false);
//        }
//        super.onDestroy();
//    }

    private void startScreenCapture() {
        Intent intent = new Intent(getApplicationContext(), ScreenCaptureService.class);
        Rect rect = getIntent().getParcelableExtra(ScreenCaptureService.SCREEN_CUT_RECT);
        if (rect != null)
            intent.putExtra(ScreenCaptureService.SCREEN_CUT_RECT, rect);
        startService(intent);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.e("shang", "进入了");
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
                startScreenCapture();
                Log.i(TAG, "start service ScreenCaptureService");


                finish();
            }
        }
    }

}
