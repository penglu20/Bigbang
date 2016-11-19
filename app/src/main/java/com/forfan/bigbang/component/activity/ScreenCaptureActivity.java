//package com.forfan.bigbang.component.activity;
//
//import android.annotation.TargetApi;
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.hardware.display.DisplayManager;
//import android.hardware.display.VirtualDisplay;
//import android.media.Image;
//import android.media.ImageReader;
//import android.media.projection.MediaProjection;
//import android.media.projection.MediaProjectionManager;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Looper;
//import android.support.annotation.RequiresApi;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.WindowManager;
//
//import com.forfan.bigbang.component.base.BaseActivity;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
///**
// * Created by wangyan-pd on 2016/11/19.
// */
//
//public class ScreenCaptureActivity extends BaseActivity {
//    private static final String TAG = ScreenCaptureActivity.class.getName();
//    private MediaProjectionManager mMediaProjectionManager;
//    private int REQUEST_MEDIA_PROJECTION = 1;
//    private SimpleDateFormat dateFormat;
//    private String pathImage;
//    private WindowManager mWindowManager;
//    private ImageReader mImageReader;
//    private MediaProjection mMediaProjection;
//    private int mResultCode;
//    private Intent mResultData;
//    private VirtualDisplay mVirtualDisplay;
//    private String strDate;
//    private int windowWidth;
//    private int windowHeight;
//    private String nameImage;
//    private int mScreenDensity;
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mMediaProjectionManager = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//        createVirtualEnvironment();
//        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
//    }
//
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_MEDIA_PROJECTION) {
//            if (resultCode != Activity.RESULT_OK) {
//                return;
//            } else if (data != null && resultCode != 0) {
//                mResultCode = resultCode;
//                mResultData = data;
//
//                startVirtual();
//                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        startCapture();
//                    }
//                },1000);
//            }
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    private void createVirtualEnvironment() {
//        dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
//        strDate = dateFormat.format(new Date());
//        pathImage = Environment.getExternalStorageDirectory().getPath() + "/Pictures/";
//        nameImage = pathImage + strDate + ".png";
//        mMediaProjectionManager = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//        mWindowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
//        windowWidth = mWindowManager.getDefaultDisplay().getWidth();
//        windowHeight = mWindowManager.getDefaultDisplay().getHeight();
//        DisplayMetrics metrics = new DisplayMetrics();
//        mWindowManager.getDefaultDisplay().getMetrics(metrics);
//        mScreenDensity = metrics.densityDpi;
//        mImageReader = ImageReader.newInstance(windowWidth, windowHeight, 0x1, 2); //ImageFormat.RGB_565
//
//        Log.i(TAG, "prepared the virtual environment");
//    }
//
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public void startVirtual() {
//        if (mMediaProjection != null) {
//            Log.i(TAG, "want to display virtual");
//            virtualDisplay();
//        } else {
//            Log.i(TAG, "start screen capture intent");
//            Log.i(TAG, "want to build mediaprojection and display virtual");
//            setUpMediaProjection();
//            virtualDisplay();
//        }
//    }
//
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public void setUpMediaProjection() {
//        mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, mResultData);
//        Log.i(TAG, "mMediaProjection defined");
//    }
//
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    private void virtualDisplay() {
//        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
//                windowWidth, windowHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
//                mImageReader.getSurface(), null, null);
//        Log.i(TAG, "virtual displayed");
//    }
//
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    private void startCapture() {
//        strDate = dateFormat.format(new java.util.Date());
//        nameImage = pathImage + strDate + ".png";
//
//        Image image = mImageReader.acquireLatestImage();
//        int width = image.getWidth();
//        int height = image.getHeight();
//        final Image.Plane[] planes = image.getPlanes();
//        final ByteBuffer buffer = planes[0].getBuffer();
//        int pixelStride = planes[0].getPixelStride();
//        int rowStride = planes[0].getRowStride();
//        int rowPadding = rowStride - pixelStride * width;
//        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
//        bitmap.copyPixelsFromBuffer(buffer);
//        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
//        image.close();
//        Log.i(TAG, "image data captured");
//
//        if (bitmap != null) {
//            try {
//                File fileImage = new File(nameImage);
//                if (!fileImage.exists()) {
//                    fileImage.createNewFile();
//                    Log.i(TAG, "image file created");
//                }
//                FileOutputStream out = new FileOutputStream(fileImage);
//                if (out != null) {
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//                    out.flush();
//                    out.close();
//                    Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                    Uri contentUri = Uri.fromFile(fileImage);
//                    media.setData(contentUri);
//                    this.sendBroadcast(media);
//                    Log.i(TAG, "screen image saved");
//                }
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    private void tearDownMediaProjection() {
//        if (mMediaProjection != null) {
//            mMediaProjection.stop();
//            mMediaProjection = null;
//        }
//        Log.i(TAG, "mMediaProjection undefined");
//    }
//}
