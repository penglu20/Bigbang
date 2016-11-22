package com.forfan.bigbang.util;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.forfan.bigbang.BigBangApp;
import com.forfan.bigbang.R;
import com.forfan.bigbang.component.activity.screen.ScreenCaptureActivity;
import com.forfan.bigbang.component.contentProvider.SPHelper;
import com.forfan.bigbang.view.BigBangLayout;
import com.forfan.bigbang.view.BigBangLayoutWrapper;

import java.util.ArrayList;
import java.util.List;


public class TipViewController implements  View.OnTouchListener {
    private static final String TAG="TipViewController";

    private static final int MOVETOEDGE=10010;
    private static final int HIDETOEDGE=10011;


    private static final long DELAY_STEP=100;


    private static class InnerClass{
        private static TipViewController instance=new TipViewController(BigBangApp.getInstance());
    }

    public static TipViewController getInstance(){
        return InnerClass.instance;
    }

    private WindowManager mWindowManager;
    private Context mContext;
    private ViewGroup mWholeView;
    private BigBangLayoutWrapper bigBangLayout;
    private FrameLayout bangWrap;
    private CheckBox floatSwitch;
    private ImageView floatImageView,floatScreen,floatCopy,floatBack;


    private Handler mainHandler;
    private WindowManager.LayoutParams layoutParams;
    private float mTouchStartX,mTouchStartY;
    private int rotation;
    private boolean isMovingToEdge=false;
    private float density=0;
    private boolean showBigBang=true;
    private boolean isMoving=false;
    private boolean isLongPressed=false;
    private int mScaledTouchSlop;

    private boolean isRemoved=false;
    private boolean isTempAdd=false;

    private List<ActionListener> mActionListener;

    private TipViewController(Context application) {
        mContext = application;
        mWindowManager = (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);
        mainHandler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MOVETOEDGE:
                        int desX= (int) msg.obj;
                        if (desX==0){
                            layoutParams.x= (int) (layoutParams.x - density*10);
                            if (layoutParams.x<0){
                                layoutParams.x=0;
                            }
                        }else {
                            layoutParams.x= (int) (layoutParams.x + density*10);
                            if (layoutParams.x>desX){
                                layoutParams.x=desX;
                            }
                        }
                        updateViewPosition(layoutParams.x,layoutParams.y);
                        if (layoutParams.x!=desX) {
                            mainHandler.sendMessageDelayed(mainHandler.obtainMessage(MOVETOEDGE, desX),10);
                        }else {
                            isMovingToEdge = false;
                            // TODO: 2016/11/21
                            setFloatViewToDefault();
                        }
                        break;
                    case HIDETOEDGE:
                        if (layoutParams.x==0 && ((layoutParams.gravity&( Gravity.TOP| Gravity.LEFT))==( Gravity.TOP| Gravity.LEFT))){
                            floatImageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.floatview_hide_left));
                        }else {
                            floatImageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.floatview_hide_right));
                        }
                        floatImageView.setVisibility(View.VISIBLE);
                        floatSwitch.setVisibility(View.GONE);
                        floatCopy.setVisibility(View.GONE);
                        floatScreen.setVisibility(View.GONE);
                        floatBack.setVisibility(View.GONE);
                        mWholeView.setOnTouchListener(TipViewController.this);
                        LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) floatImageView.getLayoutParams();
                        layoutParams.width=(int) ViewUtil.dp2px(20);
                        floatImageView.setLayoutParams(layoutParams);
                        break;
                }
            }
        };
        mActionListener=new ArrayList<>();
        mScaledTouchSlop = (int) ViewUtil.dp2px(20);
    }

    public synchronized void show() {

        if (mWholeView!=null){
            if (isRemoved){
                addViewInternal();
            }
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    mWholeView.setVisibility(View.VISIBLE);
                    if (rotation!=mWindowManager.getDefaultDisplay().getRotation()){
                        moveToEdge();
                    }
                }
            });
            return;
        }

        mWholeView = (ViewGroup) View.inflate(mContext, R.layout.view_float, null);

        floatImageView = (ImageView) mWholeView.findViewById(R.id.float_image);
        bigBangLayout= (BigBangLayoutWrapper) mWholeView.findViewById(R.id.bang_ll);
        bangWrap= (FrameLayout) mWholeView.findViewById(R.id.bang_wrap);

        floatSwitch= (CheckBox) mWholeView.findViewById(R.id.float_switch);
        floatScreen= (ImageView) mWholeView.findViewById(R.id.float_screen);
        floatCopy= (ImageView) mWholeView.findViewById(R.id.float_copy);
        floatBack= (ImageView) mWholeView.findViewById(R.id.float_back);

//        floatImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                refreshViewState(true);
//            }
//        });

        floatScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIPVIEW_SCREEN);
                refreshViewState(false);
                Intent intent = new Intent();
                intent.setClass(mContext,ScreenCaptureActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                showLoadingAnim();
            }
        });

        floatCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIPVIEW_COPY);
                refreshViewState(false);
                mContext.sendBroadcast(new Intent(ConstantUtil.UNIVERSAL_COPY_BROADCAST));
            }
        });

        floatBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIPVIEW_BACK);
                refreshViewState(false);
            }
        });


        DisplayMetrics displayMetrics=new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
        density=displayMetrics.density;

        // event listeners
        mWholeView.setOnTouchListener(this);

        showBigBang= SPHelper.getBoolean(ConstantUtil.FLOAT_SWITCH_STATE,true);
        floatSwitch.setChecked(showBigBang);
        floatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIPVIEW_SWITCH);
                showBigBang=isChecked;
                if (mActionListener!=null){
                    for (ActionListener listener:mActionListener) {
                        listener.isShow(showBigBang);
                    }
                    SPHelper.save(ConstantUtil.FLOAT_SWITCH_STATE,showBigBang);
                }
                refreshViewState(false);
            }
        });

        int w = WindowManager.LayoutParams.WRAP_CONTENT;
        int h = WindowManager.LayoutParams.WRAP_CONTENT;

        int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        int type = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        layoutParams = new WindowManager.LayoutParams(w, h, type, flags, PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL| Gravity.RIGHT;
        layoutParams.x=0;
        layoutParams.y=0;

        addViewInternal();
        refreshViewState(false);

    }

    private void addViewInternal() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                mWindowManager.addView(mWholeView, layoutParams);
            }
        });
        isRemoved=false;
    }

    private void refreshViewState(boolean showFun){
        mainHandler.post(new Runnable() {
            long delay=0;
            @Override
            public void run() {
                if (showFun){
                    floatImageView.setVisibility(View.GONE);
                    floatSwitch.setVisibility(View.VISIBLE);
                    floatCopy.setVisibility(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN ? View.GONE :View.VISIBLE);
                    floatScreen.setVisibility(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ? View.GONE :View.VISIBLE);
                    floatBack.setVisibility(View.VISIBLE);

                    floatBack.animate().alpha(0.8f).setStartDelay(delay).start();
                    delay+=DELAY_STEP;
                    showInAnimation(floatSwitch,delay,showBigBang?0.8f:0.3f);
                    delay+=DELAY_STEP;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        showInAnimation(floatCopy, delay);
                        delay += DELAY_STEP;
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        showInAnimation(floatScreen, delay);
                    }
                    mWholeView.setOnTouchListener(null);
                }else {
                    floatImageView.setVisibility(View.VISIBLE);
                    floatSwitch.setVisibility(View.GONE);
                    floatCopy.setVisibility(View.GONE);
                    floatScreen.setVisibility(View.GONE);
                    floatBack.setVisibility(View.GONE);
                    mWholeView.setOnTouchListener(TipViewController.this);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        hideInAnimation(floatScreen, delay);
                        delay+=DELAY_STEP;
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        hideInAnimation(floatCopy, delay);
                        delay += DELAY_STEP;
                    }
                    hideInAnimation(floatSwitch,delay);
                    delay+=DELAY_STEP;
                    hideInAnimation(floatBack,delay);
                }
                if (showBigBang){
                    floatImageView.setImageLevel(0);
                    floatImageView.setAlpha(0.8f);
                }else {
                    floatImageView.setImageLevel(1);
                    floatImageView.setAlpha(0.3f);
                }
                mWindowManager.updateViewLayout(mWholeView, layoutParams);
                setFloatViewToDefault();
            }
        });
    }

    private void showInAnimation(View view,long delay){
        showInAnimation(view, delay,0.8f);
    }

    private void showInAnimation(View view,long delay,float toAlpha){
        view.setAlpha(0);
        int y= (int) view.getHeight();
        view.setY(view.getY()-view.getHeight());
        view.animate().alpha(toAlpha).translationYBy(y).setDuration(DELAY_STEP+50).setStartDelay(delay).start();
    }
    private void hideInAnimation(View view,long delay){
        view.animate().alpha(0).setStartDelay(delay).start();
    }

    public synchronized void hide(){
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mWholeView!=null) {
                    mWholeView.setVisibility(View.GONE);
                }
            }
        });
    }

    public synchronized void remove(){
        mainHandler.removeMessages(HIDETOEDGE);
        if (mWindowManager!=null && mWholeView!=null && !isRemoved) {
            mWindowManager.removeView(mWholeView);
            isRemoved=true;
        }
    }

    public synchronized void showImage(){
        if (mWholeView==null){
            show();
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                floatImageView.setVisibility(View.VISIBLE);
                bangWrap.setVisibility(View.GONE);
                mWindowManager.updateViewLayout(mWholeView, layoutParams);
            }
        });
    }

    private boolean showAnimator=false;
    public synchronized void showLoadingAnim(){
        mainHandler.post(new Runnable() {
            int times=0;
            @Override
            public void run() {
                showAnimator=true;
                floatImageView.setVisibility(View.VISIBLE);
                floatImageView.animate().
                        rotationBy(360).
                        setDuration(1000).
                        setInterpolator(new AccelerateDecelerateInterpolator()).
                        withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                times++;
                                if (showAnimator && times < 4){
                                    floatImageView.animate().
                                            rotationBy(360).
                                            setDuration(1000).
                                            setInterpolator(new AccelerateDecelerateInterpolator()).
                                            withEndAction(this).start();
                                }
                            }
                        }).start();

            }
        });
    }

    public synchronized void stopLoadingAnim(){
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                floatImageView.clearAnimation();
                showAnimator=false;
            }
        });
    }

    public synchronized void showBigBang(final String ... txts){
        if (mWholeView==null){
            show();
        }
        if (!showBigBang){
            return;
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                bigBangLayout.reset();
                for (String txt: txts){
                    bigBangLayout.addTextItem(txt);
                }
                floatImageView.setVisibility(View.GONE);
                bangWrap.setVisibility(View.VISIBLE);
                mWindowManager.updateViewLayout(mWholeView, layoutParams);
            }
        });

    }

    public void showTipViewForStartActivity(Intent intent){
        if (mWholeView==null || isRemoved || isTempAdd){
            isTempAdd=true;
            //没显示悬浮窗的情况下，用户点击才打开Bigbang
            show();
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    setFloatViewToDefault();
                    mWholeView.setAlpha(0);
                    mWholeView.setScaleX(0);
                    mWholeView.setScaleY(0);
                    mWholeView.animate().alpha(1).scaleX(1).scaleY(1).setDuration(1000).setInterpolator(new AnticipateOvershootInterpolator()).start();
                    floatImageView.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  mContext.startActivity(intent);
                                  remove();
                                  isTempAdd=false;
                              }
                          }
                    );
                }
            });
            mainHandler.removeCallbacks(removeViewRunnanble);
            mainHandler.postDelayed(removeViewRunnanble, 3000);
        }else {
            //直接打开bigbang
            try {
                mContext.startActivity(intent);
            } catch (Throwable e) {
            }
        }
    }
    Runnable removeViewRunnanble=new Runnable() {
        @Override
        public void run() {
            floatImageView.setOnClickListener(null);
            remove();
            mWholeView=null;
            isTempAdd=false;
        }
    };

    /**
     * touch the outside of the content view, remove the popped view
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (isMovingToEdge){
            return true;
        }
        float x = event.getRawX();
        float y = event.getRawY();
        Rect rect = new Rect();
        mWholeView.getGlobalVisibleRect(rect);
        if (!rect.contains((int)x, (int)y)) {
            showImage();
            setFloatViewToDefault();
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = x;
                mTouchStartY = y;
                isMoving=false;
                isLongPressed=false;
                LogUtil.e(TAG,"ACTION_DOWN time="+System.currentTimeMillis());
                mainHandler.postDelayed(longPressRunnable, 500);
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(x-mTouchStartX)>mScaledTouchSlop||Math.abs(y-mTouchStartY)>mScaledTouchSlop){
                    isMoving=true;
                    mainHandler.removeCallbacks(longPressRunnable);
                }else {
                }
                updateViewPosition(x-mWholeView.getWidth()/2,y-mWholeView.getHeight());
                break;
            case MotionEvent.ACTION_UP:
                LogUtil.e(TAG,"ACTION_UP time="+System.currentTimeMillis());
                if (isMoving||Math.abs(x-mTouchStartX)>mScaledTouchSlop||Math.abs(y-mTouchStartY)>mScaledTouchSlop){
                    mainHandler.removeCallbacks(longPressRunnable);
                }else {
                    if (!isLongPressed) {
                        mainHandler.removeCallbacks(longPressRunnable);
//                        floatSwitch.setChecked(!floatSwitch.isChecked());
                        UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIPVIEW_IMAAGEVIEW);
                        refreshViewState(true);
                    }
                }
                updateViewPosition(x-mWholeView.getWidth()/2,y-mWholeView.getHeight());
                mTouchStartX = mTouchStartY = 0;
                moveToEdge();
                break;
            case MotionEvent.ACTION_OUTSIDE:
                showImage();
                break;
        }
        return true;
    }

    private void setFloatViewToDefault() {
        mainHandler.removeMessages(HIDETOEDGE);
        floatImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.float_view_bg));

        LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) floatImageView.getLayoutParams();
        layoutParams.width=(int) ViewUtil.dp2px(40);
        floatImageView.setLayoutParams(layoutParams);


        mainHandler.sendEmptyMessageDelayed(HIDETOEDGE,5000);
    }


    private void updateViewPosition(float x,float y) {
        layoutParams.x = (int) (x );
        layoutParams.y = (int) (y );

        if (layoutParams.x<0){
            layoutParams.x=0;
        }
        if (layoutParams.y<0){
            layoutParams.y=0;
        }
//        layoutParams.x= (int) x;
//        layoutParams.y= (int) y;
        layoutParams.gravity= Gravity.TOP| Gravity.LEFT;
        mWindowManager.updateViewLayout(mWholeView, layoutParams);
    }

    private void moveToEdge(){
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                isMovingToEdge=true;
                rotation=mWindowManager.getDefaultDisplay().getRotation();
                int width=0,height=0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                    Point point=new Point();
                    mWindowManager.getDefaultDisplay().getSize(point);
                    width=point.x;
                    height=point.y;
                }else {
                    width= mWindowManager.getDefaultDisplay().getWidth();
                    height= mWindowManager.getDefaultDisplay().getHeight();
                }
                int desX=0;
                if (layoutParams.x>width/2){
                    desX=width;
                }else {
                    desX=0;
                }
                mainHandler.sendMessage(mainHandler.obtainMessage(MOVETOEDGE,desX));
            }
        });
    }

    public synchronized void addActionListener(ActionListener actionListener) {
        mActionListener.add(actionListener);
    }

    public synchronized void removeActionListener(ActionListener actionListener){
        mActionListener.remove(actionListener);
    }

    public interface ActionListener{
        void isShow(boolean isShow);
        boolean longPressed();
    }

    private Runnable longPressRunnable = new Runnable() {
        @Override
        public void run() {
            LogUtil.e(TAG,"longPressRunnable time="+System.currentTimeMillis());
            isLongPressed=true;
            if (mActionListener!=null){
                Vibrator vibrator= (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(10);
                for (ActionListener listener:mActionListener) {
                    if (listener.longPressed()){
                        UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIPVIEW_SETTING_ACTICITY);
                        break;
                    }
                }
            }
        }
    };

}
