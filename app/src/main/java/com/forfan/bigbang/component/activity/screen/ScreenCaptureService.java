package com.forfan.bigbang.component.activity.screen;

import android.annotation.TargetApi;
import android.app.Service;
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
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.forfan.bigbang.BigBangApp;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.LogUtil;
import com.forfan.bigbang.view.MarkSizeView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;

public class ScreenCaptureService extends Service {
    private static final String TAG = "ScreenCaptureActivity";
    public static final String SCREEN_CUT_RECT = "screen_cut";
    public static final String SCREEN_CUT_GRAPHIC_PATH = "graph_path";
    public static final String NAVIGATION_BAR_HEIGHT = "navigation_bar_height";
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
    private int mNavigationBarHeight = 0;

    Handler handler = new Handler(Looper.getMainLooper());
    private Rect mRect;
    private MarkSizeView.GraphicPath mGraphicPath;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            createVirtualEnvironment();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void toCapture() {
        try {

            handler.postDelayed(new Runnable() {
                public void run() {
                    startVirtual();
                }
            }, 10);

            handler.postDelayed(new Runnable() {
                public void run() {
                    //capture the screen
                    try {
                        startCapture();
                    } catch (Exception e) {
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
        Intent intent = new Intent(ConstantUtil.SCREEN_CAPTURE_OVER_BROADCAST);
        intent.putExtra(MESSAGE, "截屏失败");
        sendBroadcast(intent);
        stopSelf();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            mRect = intent.getParcelableExtra(SCREEN_CUT_RECT);
            mGraphicPath = intent.getParcelableExtra(SCREEN_CUT_GRAPHIC_PATH);
            mNavigationBarHeight = intent.getIntExtra(NAVIGATION_BAR_HEIGHT, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        toCapture();
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createVirtualEnvironment() {
        dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
        strDate = dateFormat.format(new java.util.Date());
        pathImage = Environment.getExternalStorageDirectory().getPath() + "/Pictures/";
        nameImage = pathImage + strDate + ".png";
        mMediaProjectionManager1 = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mWindowManager1 = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        windowWidth = mWindowManager1.getDefaultDisplay().getWidth();
        windowHeight = mWindowManager1.getDefaultDisplay().getHeight();
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
            LogUtil.d(TAG, "start screen capture intent");
            LogUtil.d(TAG, "want to build mediaprojection and display virtual");
            setUpMediaProjection();

            virtualDisplay();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setUpMediaProjection() {
        try {
            mResultData = ((BigBangApp) getApplication()).getIntent();
            mResultCode = ((BigBangApp) getApplication()).getResult();
            mMediaProjectionManager1 = ((BigBangApp) getApplication()).getMediaProjectionManager();
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

    //获取状态栏高度
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startCapture() throws Exception {
        strDate = dateFormat.format(new java.util.Date());
        nameImage = pathImage + strDate + ".png";

        Image image = mImageReader.acquireLatestImage();
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_4444);
        bitmap.copyPixelsFromBuffer(buffer);
        image.close();
        LogUtil.d(TAG, "image data captured");

        int totalHeight=height+mNavigationBarHeight;
        double multipleHeight= totalHeight*1.0/height;

        if (mNavigationBarHeight!=0){
            bitmap=Bitmap.createBitmap(bitmap, (int) (rowPadding / pixelStride / multipleHeight), 0, (int) (width/multipleHeight), height);
        }
        double multipleWidth =width*1.0/ bitmap.getWidth();

        if (mGraphicPath!=null){
            mRect=new Rect(mGraphicPath.getLeft(),mGraphicPath.getTop(),mGraphicPath.getRight(),mGraphicPath.getBottom());
        }
        if (mRect != null) {
            mRect.left= (int) (mRect.left/multipleWidth);
            mRect.right= (int) (mRect.right/multipleWidth);

            mRect.top= (int) (mRect.top/multipleHeight);
            mRect.bottom= (int) (mRect.bottom/multipleHeight);

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
                        path.moveTo((float) ((mGraphicPath.pathX.get(0)/multipleWidth-mRect.left)), (float) ((mGraphicPath.pathY.get(0)/multipleHeight- mRect.top)));
                        for (int i = 1; i < mGraphicPath.size(); i++) {
                            path.lineTo((float) ((mGraphicPath.pathX.get(i)/multipleWidth-mRect.left)), (float) ((mGraphicPath.pathY.get(i)/multipleHeight- mRect.top)));
                        }
                    } else {
                        return;
                    }
                    canvas.drawPath(path, paint);
                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

                    // 关键代码，关于Xfermode和SRC_IN请自行查阅
                    canvas.drawBitmap(cutBitmap, 0 , 0, paint);

                    saveCutBitmap(temp);

                }else {
                    saveCutBitmap(cutBitmap);
                }
            }
        } else {
            saveCutBitmap(bitmap);
        }
        bitmap.recycle();//自由选择是否进行回收
//        byte[] result = output.toByteArray();//转换成功了
//        try {
//            sendBroadcast(new Intent(ConstantUtil.SCREEN_CAPTURE_OVER_BROADCAST));
//            ToastUtil.showLong(R.string.ocr_recognize);
//            output.close();
//            OcrAnalsyser.getInstance().analyse(result, new OcrAnalsyser.CallBack() {
//                @Override
//                public void onSucess(OCR ocr) {
//                    LogUtil.d(TAG, "ocr--success");
//
//
//                    String str = OcrAnalsyser.getInstance().getPasedMiscSoftText(ocr);
//                    Intent intent = new Intent(ScreenCaptureService.this, BigBangActivity.class);
//                    intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtra(BigBangActivity.TO_SPLIT_STR, str);
//                    startActivity(intent);
//                    stopSelf();
//                }
//
//                @Override
//                public void onFail() {
//                    LogUtil.d(TAG, "ocr--fail");
//                    ToastUtil.show(R.string.sorry_for_ocr_parse_fail);
//                    stopSelf();
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


    }

    private void saveCutBitmap(Bitmap cutBitmap) {
        Intent intent = new Intent(ConstantUtil.SCREEN_CAPTURE_OVER_BROADCAST);
        File localFile = new File(getFilesDir(), "temp.png");
        try {
            if (!localFile.exists()) {
                localFile.createNewFile();
                Log.i("ContentValues", "image file created");
            }
            FileOutputStream fileOutputStream = new FileOutputStream(localFile);
            if (fileOutputStream != null) {
                cutBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            intent.putExtra(MESSAGE, "保存失败");
            return;
        }
        intent.putExtra(MESSAGE, "保存成功");
        intent.putExtra(FILE_NAME, localFile.getAbsolutePath());
        sendBroadcast(intent);
        stopSelf();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        LogUtil.d(TAG, "mMediaProjection undefined");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void stopVirtual() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        mVirtualDisplay = null;
        LogUtil.d(TAG, "virtual display stopped");
    }

    @Override
    public void onDestroy() {
        // to remove mFloatLayout from windowManager
        super.onDestroy();
        tearDownMediaProjection();
        LogUtil.d(TAG, "application destroy");
    }
}