package com.forfan.bigbang.component.activity.screen;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.forfan.bigbang.R;
import com.forfan.bigbang.util.LogUtil;
import com.forfan.bigbang.util.ToastUtil;
import com.forfan.bigbang.util.ViewUtil;
import com.forfan.bigbang.view.MarkSizeView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;

public class ScreenCapture {
    private static final String TAG = "ScreenCaptureActivity";
    public static final String MESSAGE = "message";
    public static final String FILE_NAME = "temp_file";
    private SimpleDateFormat dateFormat = null;
    private String strDate = null;
    private String pathImage = null;
    private String nameImage = null;

    private MediaProjection mMediaProjection = null;
    private VirtualDisplay mVirtualDisplay = null;

    public static int mResultCode = 0;
    public static Intent mResultData = null;
    public static MediaProjectionManager mMediaProjectionManager1 = null;

    private WindowManager mWindowManager1 = null;
    private int windowWidth = 0;
    private int windowHeight = 0;
    private ImageReader mImageReader = null;
    private DisplayMetrics metrics = null;
    private int mScreenDensity = 0;
    private int mScreenWidth = 0;

    Handler handler = new Handler(Looper.getMainLooper());
    private Rect mRect;
    private MarkSizeView.GraphicPath mGraphicPath;
    private ScreenCaptureActivity activity;


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public ScreenCapture(ScreenCaptureActivity activity, Intent intent, int resultCode
        , Rect mRect, MarkSizeView.GraphicPath mGraphicPath){
        this.activity=activity;
        mResultData=intent;
        mResultCode=resultCode;
        this.mRect = mRect;
        this.mGraphicPath = mGraphicPath;
        this.mScreenWidth = ViewUtil.getScreenWidth(activity);
        try {
            createVirtualEnvironment();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void toCapture() {
        try {

            handler.postDelayed(new Runnable() {
                public void run() {
                    LogUtil.d(TAG, "before startVirtual");
                    startVirtual();
                    LogUtil.d(TAG, "after startVirtual");
                }
            }, 10);

            handler.postDelayed(new Runnable() {
                public void run() {
                    //capture the screen
                    try {
                        LogUtil.d(TAG, "before startCapture");
                        startCapture();
                        LogUtil.d(TAG, "after startCapture");
                    } catch (Exception e) {
                        e.printStackTrace();
                        sendBroadcastCaptureFail();
                    }
                }
            }, 100);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    private void sendBroadcastCaptureFail() {
        ToastUtil.show(R.string.screen_capture_fail);
        activity.finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createVirtualEnvironment() {
        dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
        strDate = dateFormat.format(new java.util.Date());
        pathImage = Environment.getExternalStorageDirectory().getPath() + "/Pictures/";
        nameImage = pathImage + strDate + ".png";
        mMediaProjectionManager1 = (MediaProjectionManager) activity.getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mWindowManager1 = (WindowManager) activity.getApplication().getSystemService(Context.WINDOW_SERVICE);
        windowWidth = ViewUtil.getScreenWidth(activity);
        windowHeight = ViewUtil.getSceenHeight(activity);
        metrics = new DisplayMetrics();
        mWindowManager1.getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mImageReader = ImageReader.newInstance(windowWidth, windowHeight, 0x1, 2); //ImageFormat.RGB_565

        LogUtil.d(TAG, "prepared the virtual environment");
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startVirtual() {
        if (mMediaProjection != null) {
            LogUtil.d(TAG, "want to display virtual");
            virtualDisplay();
        } else {
            LogUtil.d(TAG, "want to build mediaprojection and display virtual");
            setUpMediaProjection();

            virtualDisplay();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setUpMediaProjection() {
        try {
            mMediaProjection = mMediaProjectionManager1.getMediaProjection(mResultCode, mResultData);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d(TAG, "mMediaProjection defined");
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void virtualDisplay() {
        try {
            mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                    windowWidth, windowHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mImageReader.getSurface(), null, null);
            LogUtil.d(TAG, "virtual displayed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startCapture() throws Exception {
        strDate = dateFormat.format(new java.util.Date());
        nameImage = pathImage + strDate + ".png";

        Image image = mImageReader.acquireLatestImage();

        if (image==null){
            LogUtil.d(TAG, "image==null,restart");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    toCapture();
                }
            });
            return;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        image.close();
        LogUtil.d(TAG, "image data captured");

        if (width!=mScreenWidth ||rowPadding !=0){
            int[] pixel=new int[width + rowPadding / pixelStride];
            bitmap.getPixels(pixel,0,width + rowPadding / pixelStride,0,0,width + rowPadding / pixelStride,1);
            int leftPadding=0;
            int rightPadding=width + rowPadding / pixelStride;
            for (int i=0;i<pixel.length;i++){
                if (pixel[i]!=0){
                    leftPadding=i;
                    break;
                }
            }
            for (int i=pixel.length-1;i>=0;i--){
                if (pixel[i]!=0){
                    rightPadding=i;
                    break;
                }
            }
            width=Math.min(width,mScreenWidth);
            if (rightPadding-leftPadding>width){
                rightPadding= width;
            }
            bitmap=Bitmap.createBitmap(bitmap,leftPadding, 0, rightPadding-leftPadding, height);
        }

        LogUtil.d(TAG, "bitmap cuted first");
        if (mGraphicPath!=null){
            mRect=new Rect(mGraphicPath.getLeft(),mGraphicPath.getTop(),mGraphicPath.getRight(),mGraphicPath.getBottom());
        }
        if (mRect != null) {

            if (mRect.left < 0)
                mRect.left = 0;
            if (mRect.right < 0)
                mRect.right = 0;
            if (mRect.top < 0)
                mRect.top = 0;
            if (mRect.bottom < 0)
                mRect.bottom = 0;
            int cut_width = Math.abs(mRect.left - mRect.right);
            int cut_height = Math.abs(mRect.top - mRect.bottom);
            if (cut_width > 0 && cut_height > 0) {
                Bitmap cutBitmap = Bitmap.createBitmap(bitmap, mRect.left, mRect.top, cut_width, cut_height);
                LogUtil.d(TAG, "bitmap cuted second");
                if (mGraphicPath!=null){
                    // 准备画笔
                    Paint paint = new Paint();
                    paint.setAntiAlias(true);
                    paint.setStyle(Paint.Style.FILL_AND_STROKE);
                    paint.setColor(Color.WHITE);
                    Bitmap temp = Bitmap.createBitmap(cut_width, cut_height, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(temp);

                    Path path = new Path();
                    if (mGraphicPath.size() > 1) {
                        path.moveTo((float) ((mGraphicPath.pathX.get(0)-mRect.left)), (float) ((mGraphicPath.pathY.get(0)- mRect.top)));
                        for (int i = 1; i < mGraphicPath.size(); i++) {
                            path.lineTo((float) ((mGraphicPath.pathX.get(i)-mRect.left)), (float) ((mGraphicPath.pathY.get(i)- mRect.top)));
                        }
                    } else {
                        return;
                    }
                    canvas.drawPath(path, paint);
                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

                    // 关键代码，关于Xfermode和SRC_IN请自行查阅
                    canvas.drawBitmap(cutBitmap, 0 , 0, paint);
                    LogUtil.d(TAG, "bitmap cuted third");

                    saveCutBitmap(temp);

                }else {
                    saveCutBitmap(cutBitmap);
                }
            }
        } else {
            saveCutBitmap(bitmap);
        }
        bitmap.recycle();//自由选择是否进行回收
    }

    private void saveCutBitmap(Bitmap cutBitmap) {
        File localFile = new File(activity.getFilesDir(), "temp.png");
        String fileName=localFile.getAbsolutePath();
        try {
            if (!localFile.exists()) {
                localFile.createNewFile();
                LogUtil.d(TAG,"image file created");
            }
            FileOutputStream fileOutputStream = new FileOutputStream(localFile);
            if (fileOutputStream != null) {
                cutBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (IOException e) {
            sendBroadcastCaptureFail();
            return;
        }
        Intent newIntent = new Intent(activity, CaptureResultActivity.class);
        newIntent.putExtra(ScreenCapture.MESSAGE, "保存成功");
        newIntent.putExtra(ScreenCapture.FILE_NAME,fileName );
        activity.startActivity(newIntent);
        activity.finish();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        LogUtil.d(TAG, "mMediaProjection undefined");
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void stopVirtual() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        mVirtualDisplay = null;
        LogUtil.d(TAG, "virtual display stopped");
    }

    public void onDestroy() {
        stopVirtual();
        tearDownMediaProjection();
        LogUtil.d(TAG, "application destroy");
    }
}