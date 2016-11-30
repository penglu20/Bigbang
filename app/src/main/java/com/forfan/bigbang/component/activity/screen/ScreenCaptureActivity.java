package com.forfan.bigbang.component.activity.screen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.forfan.bigbang.BigBangApp;
import com.forfan.bigbang.R;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.LogUtil;
import com.forfan.bigbang.util.ToastUtil;
import com.forfan.bigbang.view.MarkSizeView;
import com.shang.commonjar.contentProvider.SPHelper;

public class ScreenCaptureActivity extends BaseActivity {
    private String TAG = "Service";
    private int result = 0;
    private Intent intent = null;
    private int REQUEST_MEDIA_PROJECTION = 1;
    private MediaProjectionManager mMediaProjectionManager;
    private MarkSizeView markSizeView;
    private Rect markedArea;
    private TextView captureTips;
    private Button captureAll;

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


        setContentView(R.layout.activity_screen_capture);

        markSizeView = (MarkSizeView) findViewById(R.id.mark_size);
        captureTips = (TextView) findViewById(R.id.capture_tips);
        captureAll = (Button) findViewById(R.id.capture_all);


        markSizeView.setmOnClickListener(new MarkSizeView.onClickListener() {
            @Override
            public void onConfirm(Rect markedArea) {
//                boolean isFirst = SPHelper.getBoolean("is_fist", true);
//                if(isFirst){
//                    ToastUtil.show(R.string.need_capture_perssion);
//                }
                ScreenCaptureActivity.this.markedArea = new Rect(markedArea);
                markSizeView.reset();
                markSizeView.setUnmarkedColor(getResources().getColor(R.color.transparent));
                markSizeView.setEnabled(false);
                startIntent();
            }

            @Override
            public void onCancel() {
                captureTips.setVisibility(View.VISIBLE);
                captureAll.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTouch() {
                captureTips.setVisibility(View.GONE);
                captureAll.setVisibility(View.GONE);
            }
        });

        captureAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                boolean isFirst = SPHelper.getBoolean("is_fist", true);
//                if (isFirst) {
//                    ToastUtil.show(R.string.need_capture_perssion);
//                }
                markSizeView.setUnmarkedColor(getResources().getColor(R.color.transparent));
                captureTips.setVisibility(View.GONE);
                captureAll.setVisibility(View.GONE);
                startIntent();
            }
        });
    }

    private void initWindow() {
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.trans));
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startIntent() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
                //ScreenCaptureService.mMediaProjectionManager1 = mMediaProjectionManager;
                ((BigBangApp) getApplication()).setMediaProjectionManager(mMediaProjectionManager);

            }
        });

    }

    @Override
    protected void onStop() {
        unregisterReceiver(captureResultReceiver);
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstantUtil.SCREEN_CAPTURE_OVER_BROADCAST);
        registerReceiver(captureResultReceiver, intentFilter);
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
        intent.putExtra(ScreenCaptureService.SCREEN_CUT_RECT, markedArea);
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


//                finish();
            }
        }
    }

    private BroadcastReceiver captureResultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConstantUtil.SCREEN_CAPTURE_OVER_BROADCAST)) {
                String fileName=intent.getStringExtra(ScreenCaptureService.FILE_NAME);
                if (TextUtils.isEmpty(fileName)){
                    finish();
                }else {
                    Intent newIntent = new Intent(context, CaptureResultActivity.class);
                    newIntent.putExtra(ScreenCaptureService.MESSAGE, intent.getStringExtra(ScreenCaptureService.MESSAGE));
                    newIntent.putExtra(ScreenCaptureService.FILE_NAME,fileName );
                    startActivity(newIntent);
                    finish();
                }
            }
        }
    };
}
