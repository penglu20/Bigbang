package com.forfan.bigbang.util;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.flask.colorpicker.CircleColorDrawable;
import com.forfan.bigbang.BigBangApp;
import com.forfan.bigbang.R;
import com.forfan.bigbang.component.activity.SettingFloatViewActivity;
import com.forfan.bigbang.component.activity.screen.ScreenCaptureActivity;
import com.forfan.bigbang.view.ArcMenu;
import com.forfan.bigbang.view.PathMenu;
import com.shang.commonjar.contentProvider.SPHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ArcTipViewController implements View.OnTouchListener {
    private static final String TAG = "TipViewController";

    private static final int MOVETOEDGE = 10010;
    private static final int HIDETOEDGE = 10011;


    private static float MAX_LENGTH = 146;
    private static float MIN_LENGTH = 50;
    private ArcMenu archMenu;
    private boolean isShowIcon;
    private ImageView floatImageView;
    private float mCurrentIconAlpha = 0.7f;

    public void showTipViewForStartActivity(Intent intent) {
        boolean isNotify = SPHelper.getBoolean(ConstantUtil.IS_SHOW_NOTIFY, false);
        boolean showFloat = SPHelper.getBoolean(ConstantUtil.SHOW_FLOAT_VIEW, false);
        boolean floatTrigger = SPHelper.getBoolean(ConstantUtil.USE_FLOAT_VIEW_TRIGGER, false);
        if (!floatTrigger && (isNotify || showFloat)) {
            //直接打开bigbang
            try {
                mContext.startActivity(intent);
            } catch (Throwable e) {
            }
            return;
        }
        if (floatTrigger || mWholeView == null || isRemoved || isTempAdd || (!isNotify && !showFloat)) {
            isTempAdd = true;
            //没显示悬浮窗的情况下，用户点击才打开Bigbang
//            show();
            showFloatImageView();
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    synchronized (ArcTipViewController.this) {
                        if (floatView == null) {
                            return;
                        }
                        floatView.clearAnimation();
                        floatView.setOnTouchListener(null);
                        floatView.setAlpha(0);
                        floatView.setScaleX(0);
                        floatView.setScaleY(0);
                        floatView.animate().alpha(1).scaleX(1.0f).scaleY(1.0f).setDuration(1000).setInterpolator(new AnticipateOvershootInterpolator()).setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                isTempAdd = false;
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }).start();
                        floatView.setOnClickListener(new View.OnClickListener() {
                                                         @Override
                                                         public void onClick(View v) {
                                                             try {
                                                                 mContext.startActivity(intent);
                                                             } catch (Throwable e) {
                                                                 e.printStackTrace();
                                                             }
                                                             removeViewRunnanble.run();
                                                             isTempAdd = false;
                                                         }
                                                     }
                        );
                    }
                }
            });
            mainHandler.removeCallbacks(showViewRunnable);
            mainHandler.removeCallbacks(removeViewRunnanble);
            mainHandler.postDelayed(removeViewRunnanble, 3000);
        } else {
            //直接打开bigbang
            try {
                mContext.startActivity(intent);
            } catch (Throwable e) {
            }
        }
    }
    // private ArcLayout arcLayout;


    private static class InnerClass {
        private static ArcTipViewController instance = new ArcTipViewController(BigBangApp.getInstance());
    }

    public static ArcTipViewController getInstance() {
        return InnerClass.instance;
    }

    private WindowManager mWindowManager;
    private Context mContext;
    private ViewGroup mWholeView;
    private LinearLayout floatView;


    private Handler mainHandler;
    private WindowManager.LayoutParams layoutParams;
    private float mTouchStartX, mTouchStartY;
    private int rotation;
    private boolean isMovingToEdge = false;
    private float density = 0;
    private boolean showBigBang = true;
    private boolean isMoving = false;
    private boolean isLongPressed = false;
    private int mScaledTouchSlop;

    private boolean isRemoved = false;
    private boolean isTempAdd = false;

    private List<ActionListener> mActionListener;
    private int mStatusBarHeight = 0;

    private ArcTipViewController(Context application) {
        mContext = application;
        mWindowManager = (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);

        int resourceId = application.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            mStatusBarHeight = application.getResources().getDimensionPixelSize(resourceId);
        }


        mainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                synchronized (ArcTipViewController.this) {
                    switch (msg.what) {
                        case MOVETOEDGE:
                            int desX = (int) msg.obj;
                            if (desX == 0) {
                                layoutParams.x = (int) (layoutParams.x - density * 10);
                                if (layoutParams.x < 0) {
                                    layoutParams.x = 0;
                                }
                            } else {
                                layoutParams.x = (int) (layoutParams.x + density * 10);
                                if (layoutParams.x > desX) {
                                    layoutParams.x = desX;
                                }
                            }
                            updateViewPosition(layoutParams.x, layoutParams.y);
                            if (layoutParams.x != desX) {
                                mainHandler.sendMessageDelayed(mainHandler.obtainMessage(MOVETOEDGE, desX), 10);
                            } else {
                                isMovingToEdge = false;
                                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
                                    SPHelper.save(ConstantUtil.FLOAT_VIEW_PORT_X, layoutParams.x);
                                    SPHelper.save(ConstantUtil.FLOAT_VIEW_PORT_Y, layoutParams.y);
                                } else {
                                    SPHelper.save(ConstantUtil.FLOAT_VIEW_LAND_X, layoutParams.x);
                                    SPHelper.save(ConstantUtil.FLOAT_VIEW_LAND_Y, layoutParams.y);
                                }
                            }
                            break;
                        case HIDETOEDGE:
                            if (layoutParams.x == 0 && ((layoutParams.gravity & (Gravity.TOP | Gravity.LEFT)) == (Gravity.TOP | Gravity.LEFT))) {
                                floatImageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.floatview_hide_left));
                            } else {
                                floatImageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.floatview_hide_right));
                            }
                            floatView.setOnTouchListener(ArcTipViewController.this);
                            mWholeView.setOnTouchListener(ArcTipViewController.this);

                            LinearLayout.LayoutParams layoutParams_ = (LinearLayout.LayoutParams) floatImageView.getLayoutParams();
                            layoutParams_.width = (int) ViewUtil.dp2px(20);
                            layoutParams_.gravity = Gravity.NO_GRAVITY;
                            floatImageView.setLayoutParams(layoutParams_);
                            //TODO 不贴边的问题
//                            layoutParams.width = (int) ViewUtil.dp2px(20);
                            reuseSavedWindowMangerPosition(ViewUtil.dp2px(20), WindowManager.LayoutParams.WRAP_CONTENT);
                            updateViewPosition(layoutParams.x, layoutParams.y);

                            break;
                    }
                }
            }
        };
        mActionListener = new ArrayList<>();
        mScaledTouchSlop = ViewUtil.dp2px(20);
        initView();
        isRemoved = true;
    }

    int[] icons;

    private void initView() {
        showBigBang = SPHelper.getBoolean(ConstantUtil.TOTAL_SWITCH, true);

        floatView = (LinearLayout) View.inflate(mContext, R.layout.arc_float_icon, null);
        floatImageView = ((ImageView) floatView.findViewById(R.id.float_image));
        mWholeView = (RelativeLayout) View.inflate(mContext, R.layout.arc_view_float, null);
        archMenu = (ArcMenu) mWholeView.findViewById(R.id.arc_menu);
        initIcon();
        archMenu.setOnModeSeletedListener(new ArcMenu.OnModeSeletedListener() {
            @Override
            public void onModeSelected() {
                showFloatImageView();
            }

            @Override
            public void onNothing() {

            }
        });
        // event listeners
        mWholeView.setOnTouchListener(this);
        floatView.setOnTouchListener(this);
    }

    private void initIcon() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            icons = new int[]{R.mipmap.ic_float_switch};
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            icons = new int[]{R.mipmap.ic_float_switch, R.mipmap.ic_float_copy};
        } else {
            icons = new int[]{R.mipmap.ic_float_switch, R.mipmap.ic_float_copy, R.mipmap.ic_float_screen};

        }
    }

    private void initArcMenu(ArcMenu menu, int[] itemDrawables) {
        menu.removeAllItemViews();
        final int itemCount = itemDrawables.length;
        if(archMenu != null){
            if (SPHelper.getInt(ConstantUtil.FLOATVIEW_DIY_BG_COLOR, 0) != 0) {
                CircleColorDrawable circleColorDrawable = new CircleColorDrawable(SPHelper.getInt(ConstantUtil.FLOATVIEW_DIY_BG_COLOR, 0));
                archMenu.getHintView().setBackground(circleColorDrawable);
            }

        }
        for (int i = 0; i < itemCount; i++) {
            ImageView item = new ImageView(mWholeView.getContext());
            item.setImageResource(itemDrawables[i]);
            if (i == 0) {
                if (showBigBang) {
                    item.setAlpha(0.7f);
                    mCurrentIconAlpha = 0.7f;
                } else {
                    item.setAlpha(0.3f);
                    mCurrentIconAlpha = 0.3f;
                }
            }
            item.setPadding(ViewUtil.dp2px(8), ViewUtil.dp2px(8), ViewUtil.dp2px(8), ViewUtil.dp2px(8));
            item.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            if (SPHelper.getInt(ConstantUtil.FLOATVIEW_DIY_BG_COLOR, 0) != 0) {
                CircleColorDrawable circleColorDrawable = new CircleColorDrawable(SPHelper.getInt(ConstantUtil.FLOATVIEW_DIY_BG_COLOR, 0));
                item.setBackground(circleColorDrawable);
            } else {
                item.setBackgroundResource(R.drawable.float_view_image_bg);
            }
            if (SPHelper.getInt(ConstantUtil.FLOATVIEW_ALPHA, 100) != 100) {
                item.setAlpha(SPHelper.getInt(ConstantUtil.FLOATVIEW_ALPHA, 100) / 100f);
            }



            final int position = i;

            menu.addItem(item, new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showFuncation(position);
                    showFloatImageView();
                }
            });
        }
    }

    private void showFuncation(int position) {
        switch (position) {
            case 0:
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIPVIEW_SWITCH);
                showBigBang = !showBigBang;
                SPHelper.save(ConstantUtil.TOTAL_SWITCH, showBigBang);
                if (mActionListener != null) {
                    for (ActionListener listener : mActionListener) {
                        listener.isShow(showBigBang);
                    }
                }
                initArcMenu(archMenu, icons);
                break;

            case 1:
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIPVIEW_COPY);
                mContext.sendBroadcast(new Intent(ConstantUtil.UNIVERSAL_COPY_BROADCAST));
                break;
            case 2:
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIPVIEW_SCREEN);
                Intent intent = new Intent();
                intent.setClass(mContext, ScreenCaptureActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                break;
        }
    }

    public void syncStates() {
        showBigBang = SPHelper.getBoolean(ConstantUtil.TOTAL_SWITCH, true);
        initArcMenu(archMenu, icons);
        if (floatView != null) {
            floatView.setAlpha(mCurrentIconAlpha);
        }
    }

    private void showArcMenuView() {
        //TODO 设置了大小会导致放到最右侧贴边展开时不贴边
        // reuseSavedWindowMangerPosition(ViewUtil.dp2px(MAX_LENGTH), ViewUtil.dp2px(MAX_LENGTH));
        reuseSavedWindowMangerPosition();
        removeAllView();
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (ArcTipViewController.this) {
                    mWholeView.setVisibility(View.VISIBLE);
                    mWholeView.setOnTouchListener(ArcTipViewController.this);
                    int position = getArcPostion(layoutParams);
                    mWindowManager.addView(mWholeView, layoutParams);
                    reMeasureHeight(position, layoutParams);
                    initArcMenu(archMenu,icons);
                    archMenu.refreshPathMenu(position);
                    mWindowManager.updateViewLayout(mWholeView, layoutParams);
                    archMenu.performClickShowMenu(position);

                    isShowIcon = false;
                }
            }
        });
    }

    private void reMeasureHeight(int position, WindowManager.LayoutParams layoutParams) {

        if (position == PathMenu.LEFT_CENTER || position == PathMenu.RIGHT_CENTER) {
            layoutParams.y = layoutParams.y - (ViewUtil.dp2px((MAX_LENGTH - MIN_LENGTH) / 2));

            int mScreenWidth, mScreenHeight;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                Point point = new Point();
                mWindowManager.getDefaultDisplay().getSize(point);
                mScreenWidth = point.x;
            } else {
                mScreenWidth = mWindowManager.getDefaultDisplay().getWidth();
            }
            if (layoutParams.x > mScreenWidth / 2) {
                layoutParams.x = mScreenWidth;
            } else {
                layoutParams.x = 0;
            }

        }
    }

    private void removeAllView() {
        if (mWholeView == null)
            initView();
        if (mWindowManager == null)
            mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        try {
            mWindowManager.removeView(mWholeView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mWindowManager.removeView(floatView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (archMenu != null)
            archMenu.reset();
        if (mWholeView != null) {
            mWholeView.setVisibility(View.GONE);
            mWholeView.setOnTouchListener(null);
        }
        if (floatView != null) {
            floatView.setVisibility(View.INVISIBLE);
            floatView.setOnTouchListener(null);
        }


    }

    private int getArcPostion(WindowManager.LayoutParams layoutParams) {
        int wmX = layoutParams.x;
        int wmY = layoutParams.y;
        int position = PathMenu.RIGHT_CENTER;
        int mScreenWidth, mScreenHeight;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point point = new Point();
            mWindowManager.getDefaultDisplay().getSize(point);
            mScreenWidth = point.x;
            mScreenHeight = point.y;
        } else {
            mScreenWidth = mWindowManager.getDefaultDisplay().getWidth();
            mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();
        }
        mScreenHeight = mScreenHeight - mStatusBarHeight;
        if (wmX <= mScreenWidth / 3) //左边  竖区域
        {
            if (wmY <= ViewUtil.dp2px((MAX_LENGTH - MIN_LENGTH) / 2)) {
                position = PathMenu.LEFT_TOP;//左上
            } else if (wmY > mScreenHeight - ViewUtil.dp2px((MAX_LENGTH + MIN_LENGTH) / 2)) {
                position = PathMenu.LEFT_BOTTOM;//左下
            } else {
                position = PathMenu.LEFT_CENTER;//左中
            }
        }
//        else if (wmX >= mScreenWidth / 3 && wmX <= mScreenWidth * 2 / 3)//中间 竖区域
//        {
//            if (wmY <= mScreenHeight / 3) {
//                position = PathMenu.CENTER_TOP;//中上
//            } else if (wmY > mScreenHeight / 3
//                    && wmY < mScreenHeight * 2 / 3) {
//                position = PathMenu.CENTER;//中
//            } else if (wmY >= mScreenHeight * 2 / 3) {
//                position = PathMenu.CENTER_BOTTOM;//中下
//            }
//        }
        else if (wmX >= mScreenWidth * 2 / 3)//右边竖区域
        {
            if (wmY <= ViewUtil.dp2px((MAX_LENGTH - MIN_LENGTH) / 2)) {
                position = PathMenu.RIGHT_TOP;//左上
            } else if (wmY > mScreenHeight - ViewUtil.dp2px((MAX_LENGTH + MIN_LENGTH) / 2)) {
                position = PathMenu.RIGHT_BOTTOM;//左下
            } else {
                position = PathMenu.RIGHT_CENTER;//左中
            }
        }
        return position;
    }

    private void showFloatImageView() {
        if(layoutParams == null)
            reuseSavedWindowMangerPosition();
        showFloatIcon();
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (ArcTipViewController.this) {
                    reuseSavedWindowMangerPosition(ViewUtil.dp2px(MIN_LENGTH), ViewUtil.dp2px(MIN_LENGTH));
                    removeAllView();
                    LogUtil.e("shang", "addView1");
                    floatView.setAlpha(mCurrentIconAlpha);
                    floatView.setScaleX(1);
                    floatView.setScaleY(1);
                    floatView.setOnTouchListener(ArcTipViewController.this);
                    floatView.setVisibility(View.VISIBLE);
                    mWindowManager.addView(floatView, layoutParams);

//                    mWindowManager.updateViewLayout(floatView, layoutParams);
                    isShowIcon = true;

                }
            }
        });
    }

    private void showFloatIcon() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                mainHandler.removeMessages(HIDETOEDGE);
                LogUtil.e("shang", "addView0");
                if (new File(SettingFloatViewActivity.FLOATVIEW_IMAGE_PATH).exists()) {
                    floatImageView.setImageURI(Uri.parse(SettingFloatViewActivity.FLOATVIEW_IMAGE_PATH));
                } else {
                    floatImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.float_view_bg));
                }
                LinearLayout.LayoutParams floatImageViewlayoutParams = (LinearLayout.LayoutParams) floatImageView.getLayoutParams();
                floatImageViewlayoutParams.width = ViewUtil.dp2px(MIN_LENGTH);
                floatImageView.setLayoutParams(floatImageViewlayoutParams);

                reuseSavedWindowMangerPosition(ViewUtil.dp2px(MIN_LENGTH), WindowManager.LayoutParams.WRAP_CONTENT);
                try {
                    mWindowManager.updateViewLayout(floatView, layoutParams);
                } catch (Throwable e) {
                    LogUtil.e("shang", "showFloatIcon e=" + e);
                }
                mainHandler.sendEmptyMessageDelayed(HIDETOEDGE, 5000);
            }
        });

    }

    private void reuseSavedWindowMangerPosition() {
        reuseSavedWindowMangerPosition(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void reuseSavedWindowMangerPosition(int width_vale, int height_value) {
        //获取windowManager
        int w = width_vale;
        int h = height_value;
        if (layoutParams == null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
            density = displayMetrics.density;

            int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            int type = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(mContext)) {
                type = WindowManager.LayoutParams.TYPE_PHONE;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                type = WindowManager.LayoutParams.TYPE_TOAST;
            } else {
                type = WindowManager.LayoutParams.TYPE_PHONE;
            }

            int width, height;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                Point point = new Point();
                mWindowManager.getDefaultDisplay().getSize(point);
                width = point.x;
                height = point.y;
            } else {
                width = mWindowManager.getDefaultDisplay().getWidth();
                height = mWindowManager.getDefaultDisplay().getHeight();
            }
            rotation = mWindowManager.getDefaultDisplay().getRotation();
            int x = 0, y = 0;
            if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
                x = SPHelper.getInt(ConstantUtil.FLOAT_VIEW_PORT_X, width);
                y = SPHelper.getInt(ConstantUtil.FLOAT_VIEW_PORT_Y, height / 2);
            } else {
                x = SPHelper.getInt(ConstantUtil.FLOAT_VIEW_LAND_X, width);
                y = SPHelper.getInt(ConstantUtil.FLOAT_VIEW_LAND_Y, height / 2);
            }

            layoutParams = new WindowManager.LayoutParams(w, h, type, flags, PixelFormat.TRANSLUCENT);
            layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
            layoutParams.x = x;
            layoutParams.y = y;
        } else {
            layoutParams.width = w;
            layoutParams.height = h;
        }

    }

    public synchronized void showForSettings() {
        isRemoved = true;
    }

    public synchronized void show() {
        if (isRemoved) {
            LogUtil.e("shang", "添加floatview");
            showFloatImageView();
            isRemoved = false;
        }

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (ArcTipViewController.this) {
                    if (floatView != null) {
                        floatView.setVisibility(View.VISIBLE);
                        if (rotation != mWindowManager.getDefaultDisplay().getRotation()) {
                            moveToEdge();
                        }
                    }
                }
            }
        });

        return;

    }


    public synchronized void remove() {
        mainHandler.removeCallbacks(showViewRunnable);
        mainHandler.removeMessages(HIDETOEDGE);
        if (mWindowManager != null && floatView != null && !isRemoved) {
            removeAllView();
            isRemoved = true;
            LogUtil.e("shang", "移除floatview");
        }
    }

    Runnable removeViewRunnanble = new Runnable() {
        @Override
        public void run() {
            synchronized (ArcTipViewController.this) {
                if (floatView == null) {
                    return;
                }
                floatView.setOnClickListener(null);
                floatView.animate().alpha(0).scaleX(0).scaleY(0).setDuration(1000).setInterpolator(new AnticipateOvershootInterpolator()).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!isTempAdd) {
                            remove();
                            isTempAdd = false;
                            mainHandler.removeCallbacks(showViewRunnable);
                            mainHandler.removeCallbacks(removeViewRunnanble);
                            mainHandler.postDelayed(showViewRunnable, 1000);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
            }
        }
    };
    Runnable showViewRunnable = new Runnable() {
        @Override
        public void run() {
            if (SPHelper.getBoolean(ConstantUtil.SHOW_FLOAT_VIEW, false)) {
                show();
            }
        }
    };

    /**
     * touch the outside of the content view, remove the popped view
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        LogUtil.e("shang", "event:" + event);
        if (!isShowIcon) {
            showFloatImageView();
            return false;
        }
        if (isMovingToEdge) {
            return true;
        }
        float x = event.getRawX();
        float y = event.getRawY();
//        Rect rect = new Rect();
//        floatView.getGlobalVisibleRect(rect);
//        if (!rect.contains((int) x, (int) y)) {
//            showImage();
//            setFloatViewToDefault();
//        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mTouchStartX = x;
                mTouchStartY = y;
                isMoving = false;
                isLongPressed = false;
                LogUtil.e(TAG, "ACTION_DOWN time=" + System.currentTimeMillis());
                if (isShowIcon)
                    mainHandler.postDelayed(longPressRunnable, 500);
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(x - mTouchStartX) > mScaledTouchSlop || Math.abs(y - mTouchStartY) > mScaledTouchSlop) {
                    if (!isMoving) {
                        showFloatIcon();
                    }
                    isMoving = true;
                    mainHandler.removeCallbacks(longPressRunnable);
                } else {
                }
                updateViewPosition(x - floatView.getWidth() / 2, y - floatView.getHeight());
                break;
            case MotionEvent.ACTION_UP:
                LogUtil.e(TAG, "ACTION_UP time=" + System.currentTimeMillis());
                if (isMoving || Math.abs(x - mTouchStartX) > mScaledTouchSlop || Math.abs(y - mTouchStartY) > mScaledTouchSlop) {
                    mainHandler.removeCallbacks(longPressRunnable);
                } else {
                    if (!isLongPressed) {
                        mainHandler.removeCallbacks(longPressRunnable);
                        UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIPVIEW_IMAAGEVIEW);
                        if (!isMoving) {
                            showArcMenuView();
                        }
                    }
                }
                updateViewPosition(x - floatView.getWidth() / 2, y - floatView.getHeight());
                mTouchStartX = mTouchStartY = 0;
                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
                    SPHelper.save(ConstantUtil.FLOAT_VIEW_PORT_X, layoutParams.x);
                    SPHelper.save(ConstantUtil.FLOAT_VIEW_PORT_Y, layoutParams.y);
                } else {
                    SPHelper.save(ConstantUtil.FLOAT_VIEW_LAND_X, layoutParams.x);
                    SPHelper.save(ConstantUtil.FLOAT_VIEW_LAND_Y, layoutParams.y);
                }
                moveToEdge();

            case MotionEvent.ACTION_OUTSIDE:
                if (!isShowIcon) {
                    showFloatImageView();
                }
                break;
        }
        return true;
    }


    private synchronized void updateViewPosition(float x, float y) {
        layoutParams.x = (int) (x);
        layoutParams.y = (int) (y);

        if (layoutParams.x < 0) {
            layoutParams.x = 0;
        }
        if (layoutParams.y < 0) {
            layoutParams.y = 0;
        }

        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        try {
            mWindowManager.updateViewLayout(floatView, layoutParams);
        } catch (Throwable e) {
        }
    }

    private void moveToEdge() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                isMovingToEdge = true;
                rotation = mWindowManager.getDefaultDisplay().getRotation();

                int width = 0, height = 0;
                int x = 0, y = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                    Point point = new Point();
                    mWindowManager.getDefaultDisplay().getSize(point);
                    width = point.x;
                    height = point.y;
                } else {
                    width = mWindowManager.getDefaultDisplay().getWidth();
                    height = mWindowManager.getDefaultDisplay().getHeight();
                }

                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
                    x = SPHelper.getInt(ConstantUtil.FLOAT_VIEW_PORT_X, layoutParams.x);
                    y = SPHelper.getInt(ConstantUtil.FLOAT_VIEW_PORT_Y, layoutParams.y);
                } else {
                    x = SPHelper.getInt(ConstantUtil.FLOAT_VIEW_LAND_X, layoutParams.x);
                    y = SPHelper.getInt(ConstantUtil.FLOAT_VIEW_LAND_Y, layoutParams.y);
                }
                layoutParams.x = x;
                layoutParams.y = y;
                int desX = 0;
                if (layoutParams.x > width / 2) {
                    desX = width;
                } else {
                    desX = 0;
                }
                mainHandler.sendMessage(mainHandler.obtainMessage(MOVETOEDGE, desX));

            }
        });
    }

    public synchronized void addActionListener(ActionListener actionListener) {
        mActionListener.add(actionListener);

    }

    public synchronized void removeActionListener(ActionListener actionListener) {
        mActionListener.remove(actionListener);
    }

    public interface ActionListener {
        void isShow(boolean isShow);

        boolean longPressed();
    }

    private Runnable longPressRunnable = new Runnable() {
        @Override
        public void run() {
            LogUtil.e(TAG, "longPressRunnable time=" + System.currentTimeMillis());
            isLongPressed = true;
            if (mActionListener != null) {
                Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(10);
                for (ActionListener listener : mActionListener) {
                    if (listener.longPressed()) {
                        UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIPVIEW_SETTING_ACTICITY);
                        break;
                    }
                }
            }
        }
    };

}
