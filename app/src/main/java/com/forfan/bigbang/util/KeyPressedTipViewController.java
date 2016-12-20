package com.forfan.bigbang.util;

import android.animation.Animator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.forfan.bigbang.BigBangApp;
import com.forfan.bigbang.R;
import com.forfan.bigbang.component.activity.screen.ScreenCaptureActivity;
import com.forfan.bigbang.view.KeyRelativeLayout;
import com.shang.commonjar.contentProvider.SPHelper;


public class KeyPressedTipViewController implements  View.OnTouchListener {
    private static final String TAG="KeyPressedTipViewController";


    private static final long DELAY_STEP=100;
    public static final int LONG_PRESS_DELAY = 500;


    private static class InnerClass{
        private static KeyPressedTipViewController instance=new KeyPressedTipViewController(BigBangApp.getInstance());
    }

    public static KeyPressedTipViewController getInstance(){
        return InnerClass.instance;
    }

    private WindowManager mWindowManager;
    private Context mContext;
    private KeyRelativeLayout mWholeView;
    private LinearLayout longPressedLL;
    private TextView floatSwitch,floatClick,floatClipboard;
    private TextView floatScreen, floatUniversalCopy;


    private Handler mainHandler;
    private WindowManager.LayoutParams layoutParams;
    private int rotation;
    private boolean isLongPressedCancel =false;
    private boolean isLongPressedHome =false;
    private boolean isLongPressedRecent =false;

    private boolean isRemoved=false;
    private boolean isToRemoved=false;

    private CloseListener mCloseListener;

    private int keyPressIndex=0;
    private int currentKeyCode=0;
    private KeyEvent lastKeyEvent;

    private KeyPressedTipViewController(Context application) {
        mContext = application;
        mWindowManager = (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);
        mainHandler=new Handler();
    }

    public interface CloseListener{
        void onRemove();
    }

    public synchronized void show(CloseListener closeListener) {
        mCloseListener=closeListener;
        mContext.registerReceiver(mHomeKeyEventReceiver, new IntentFilter(
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        if (mWholeView!=null){
            if (isToRemoved){
                mainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        show(closeListener);
                    }
                }, 500);
                return;
            }
            if (isRemoved){
                addViewInternal();
            }
            return;
        }

        boolean isRun= SPHelper.getBoolean(ConstantUtil.TOTAL_SWITCH,true);

        boolean clipborad= SPHelper.getBoolean(ConstantUtil.MONITOR_CLIP_BOARD,true);
        boolean click = SPHelper.getBoolean(ConstantUtil.MONITOR_CLICK,true);


        String totalSwitch=!isRun?mContext.getString(R.string.notify_total_switch_off):mContext.getString(R.string.notify_total_switch_on);
        String monitorClick=!click?mContext.getString(R.string.notify_monitor_click_off):mContext.getString(R.string.notify_monitor_click_on);
        String monitorClipboard=!clipborad?mContext.getString(R.string.notify_monitor_clipboard_off):mContext.getString(R.string.notify_monitor_clipboard_on);

        Drawable totalSwitccRes=!isRun?mContext.getResources().getDrawable(R.drawable.notify_off):mContext.getResources().getDrawable(R.drawable.notify_on);
        Drawable monitorClickRes=!click?mContext.getResources().getDrawable(R.drawable.notify_click_off):mContext.getResources().getDrawable(R.drawable.notify_click_on);
        Drawable monitorClipboardRes=!clipborad?mContext.getResources().getDrawable(R.drawable.notify_clipboare_off):mContext.getResources().getDrawable(R.drawable.notify_clipboard_on);
        Drawable universalCopyRes=mContext.getResources().getDrawable(R.drawable.notify_copy);
        Drawable screenRes=mContext.getResources().getDrawable(R.drawable.notify_screen);


        int totalSwitccColor=!isRun?R.color.primary_text:R.color.colorPrimary;
        int monitorClickColor=!click?R.color.primary_text:R.color.colorPrimary;
        int monitorClipboardColor=!clipborad?R.color.primary_text:R.color.colorPrimary;



        mWholeView = (KeyRelativeLayout) View.inflate(mContext, R.layout.long_pressed_view_float, null);
        longPressedLL= (LinearLayout) mWholeView.findViewById(R.id.long_pressed_ll);

        floatSwitch= (TextView) mWholeView.findViewById(R.id.total_switch);
        floatClick= (TextView) mWholeView.findViewById(R.id.monitor_click);
        floatClipboard= (TextView) mWholeView.findViewById(R.id.monitor_clipboard);
        floatUniversalCopy = (TextView) mWholeView.findViewById(R.id.universal_copy);
        floatScreen= (TextView) mWholeView.findViewById(R.id.screen_cap);

        Rect bounds=new Rect(0,0,ViewUtil.dp2px(30),ViewUtil.dp2px(30));
        floatSwitch.setText(totalSwitch);
        totalSwitccRes.setBounds(bounds);
        floatSwitch.setCompoundDrawables(null,totalSwitccRes,null,null);
//        floatSwitch.setCompoundDrawablePadding(ViewUtil.dp2px(5));
        floatSwitch.setTextColor(mContext.getResources().getColor(totalSwitccColor));

        floatClick.setText(monitorClick);
        monitorClickRes.setBounds(bounds);
        floatClick.setCompoundDrawables(null,monitorClickRes,null,null);
//        floatClick.setCompoundDrawablePadding(ViewUtil.dp2px(5));
        floatClick.setTextColor(mContext.getResources().getColor(monitorClickColor));

        floatClipboard.setText(monitorClipboard);
        monitorClipboardRes.setBounds(bounds);
        floatClipboard.setCompoundDrawables(null,monitorClipboardRes,null,null);
//        floatClipboard.setCompoundDrawablePadding(ViewUtil.dp2px(5));
        floatClipboard.setTextColor(mContext.getResources().getColor(monitorClipboardColor));

        universalCopyRes.setBounds(bounds);
        floatUniversalCopy.setCompoundDrawables(null,universalCopyRes,null,null);
//        floatUniversalCopy.setCompoundDrawablePadding(ViewUtil.dp2px(5));

        screenRes.setBounds(bounds);
        floatScreen.setCompoundDrawables(null,screenRes,null,null);
//        floatScreen.setCompoundDrawablePadding(ViewUtil.dp2px(5));


        mWholeView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                LogUtil.d("setOnKeyListener",event.toString());
                if (event.getKeyCode()!=KeyEvent.KEYCODE_BACK){
                    isLongPressedCancel =false;
                }
                if (isLongPressedCancel && event.getKeyCode()==KeyEvent.KEYCODE_BACK && event.getAction()== KeyEvent.ACTION_UP){
                    isLongPressedCancel =false;
                }
                if (!isLongPressedCancel && event.getKeyCode()==KeyEvent.KEYCODE_BACK && event.getAction()== KeyEvent.ACTION_DOWN) {
                    refreshViewState(false);
                }
                return false;
            }
        });

        floatSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_KEY_PRESS_TIPVIEW_SWITCH);
                refreshViewState(false);
                mContext.sendBroadcast(new Intent(ConstantUtil.TOTAL_SWITCH_BROADCAST));
            }
        });


        floatClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_KEY_PRESS_TIPVIEW_CLICK);
                refreshViewState(false);
                mContext.sendBroadcast(new Intent(ConstantUtil.MONITOR_CLICK_BROADCAST));
            }
        });

        floatClipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_KEY_PRESS_TIPVIEW_CLIPBOARD);
                refreshViewState(false);
                mContext.sendBroadcast(new Intent(ConstantUtil.MONITOR_CLIPBOARD_BROADCAST));
            }
        });

        floatUniversalCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_KEY_PRESS_TIPVIEW_COPY);
                //这里不用动画，免得引起
                remove();
                mContext.sendBroadcast(new Intent(ConstantUtil.UNIVERSAL_COPY_BROADCAST));
            }
        });

        floatScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_KEY_PRESS_TIPVIEW_SCREEN);
                refreshViewState(false);
                Intent intent = new Intent();
                intent.setClass(mContext,ScreenCaptureActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        DisplayMetrics displayMetrics=new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);

        // event listeners
        mWholeView.setOnTouchListener(this);


        int w = WindowManager.LayoutParams.MATCH_PARENT;
        int h = WindowManager.LayoutParams.WRAP_CONTENT;

        int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                |WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        int type = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(mContext)){
            type = WindowManager.LayoutParams.TYPE_PHONE;
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        int width,height;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point point=new Point();
            mWindowManager.getDefaultDisplay().getSize(point);
            width=point.x;
            height=point.y;
        }else {
            width= mWindowManager.getDefaultDisplay().getWidth();
            height= mWindowManager.getDefaultDisplay().getHeight();
        }
        rotation = mWindowManager.getDefaultDisplay().getRotation();
        int x=0,y=height;

        layoutParams = new WindowManager.LayoutParams(w, h, type, flags, PixelFormat.TRANSLUCENT);
        layoutParams.gravity= Gravity.BOTTOM| Gravity.LEFT;
        layoutParams.x=x;
        layoutParams.y=y;

        addViewInternal();
        refreshViewState(true);

    }

    private void addViewInternal() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized(KeyPressedTipViewController.this) {
                    if (mWholeView != null) {
                        try {
                            mWindowManager.addView(mWholeView, layoutParams);
                        } catch (Throwable e) {
                        }
                    } else {
                        isRemoved = true;
                        show(mCloseListener);
                    }
                }
            }
        });
        isRemoved=false;
    }

    public synchronized void refreshViewState(boolean showFun){
        isToRemoved=!showFun;
        mainHandler.post(new Runnable() {
            long delay=0;
            @Override
            public void run() {
                synchronized (KeyPressedTipViewController.this) {
                    if (showFun) {
                        longPressedLL.setVisibility(View.VISIBLE);
                        floatSwitch.setVisibility(View.VISIBLE);
                        floatClick.setVisibility(View.VISIBLE);
                        floatClipboard.setVisibility(View.VISIBLE);
                        floatUniversalCopy.setVisibility(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN ? View.GONE : View.VISIBLE);
                        floatScreen.setVisibility(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ? View.GONE : View.VISIBLE);

                        longPressedLL.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                            @Override
                            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                                if (top!=0){
                                    longPressedLL.removeOnLayoutChangeListener(this);
                                    int y= (int) longPressedLL.getY();
                                    int height=longPressedLL.getHeight();
                                    longPressedLL.setY(y+height);
                                    longPressedLL.animate().y(y).setDuration(500).setInterpolator(new FastOutSlowInInterpolator()).start();
                                }

                            }
                        });

                    } else {
                        if (longPressedLL==null){
                            remove();
                            return;
                        }
                        int y= (int) longPressedLL.getY();
                        int height=longPressedLL.getHeight();
                        longPressedLL.animate().translationY(y+height).setDuration(500).setInterpolator(new FastOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                remove();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }).start();
                    }
                    try {
                        mWindowManager.updateViewLayout(mWholeView, layoutParams);
                    } catch (Throwable e) {
                    }
                }
            }
        });
    }

    private void showInAnimation(View view,long delay){
        showInAnimation(view, delay,1);
    }

    private void showInAnimation(View view,long delay,float toAlpha){
        synchronized(KeyPressedTipViewController.this) {
            view.setAlpha(0);
            int y = (int) view.getHeight();
            view.setY(view.getY() - view.getHeight());
            view.animate().alpha(toAlpha).translationYBy(y).setDuration(DELAY_STEP + 50).setStartDelay(delay).start();
        }
    }
    private void hideInAnimation(View view,long delay){
        synchronized(KeyPressedTipViewController.this) {
            view.animate().alpha(0).setStartDelay(delay).start();
        }
    }

    public synchronized void remove(){
        try {
            mContext.unregisterReceiver(mHomeKeyEventReceiver);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (mWindowManager!=null && mWholeView!=null && !isRemoved) {
            try {
                mWindowManager.removeView(mWholeView);
                if (mCloseListener!=null){
                    mCloseListener.onRemove();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            isRemoved=true;
            mWholeView=null;
        }
    }


    /**
     * touch the outside of the content view, remove the popped view
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_UP) {
            refreshViewState(false);
        }
        return true;
    }

    private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
        String SYSTEM_REASON = "reason";
        String SYSTEM_HOME_KEY = "homekey";
        String SYSTEM_HOME_KEY_LONG = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
                    //表示按了home键,程序到了后台
                    if (currentKeyCode==KeyEvent.KEYCODE_HOME && isLongPressedHome){
                        isLongPressedHome=false;
                        return;
                    }
                }else if(TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)){
                    //表示长按home键,显示最近使用的程序列表
                    if (currentKeyCode==KeyEvent.KEYCODE_APP_SWITCH && isLongPressedRecent){
                        isLongPressedRecent=false;
                        return;
                    }
                }else {
                    return;
                }
                refreshViewState(false);
            }
        }
    };

    Runnable longPressRunnable=new Runnable() {
        @Override
        public void run() {
            Vibrator vibrator= (Vibrator)mContext.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(10);
            if (currentKeyCode == KeyEvent.KEYCODE_BACK){
                isLongPressedCancel = true;
            }else {
                isLongPressedCancel = false;
            }
            if (currentKeyCode==KeyEvent.KEYCODE_HOME){
                isLongPressedHome = true;
            }
            if (currentKeyCode==KeyEvent.KEYCODE_APP_SWITCH){
                isLongPressedRecent = true;
            }
            KeyPressedTipViewController.getInstance().show(null);
        }
    };

    public void onKeyEvent(KeyEvent keyEvent){
        if (keyPressIndex==0){
            return;
        }
        if (keyPressIndex==7){
            if (lastKeyEvent!=null){
                if ((lastKeyEvent.getKeyCode()==KeyEvent.KEYCODE_VOLUME_DOWN && keyEvent.getKeyCode()==KeyEvent.KEYCODE_VOLUME_UP )
                        ||(keyEvent.getKeyCode()==KeyEvent.KEYCODE_VOLUME_DOWN && lastKeyEvent.getKeyCode()==KeyEvent.KEYCODE_VOLUME_UP )){
                    if (keyEvent.getEventTime()-lastKeyEvent.getEventTime()<LONG_PRESS_DELAY){
                        longPressRunnable.run();
                    }
                }
            }
            lastKeyEvent=keyEvent;
        }else {
            if (keyEvent.getKeyCode() == currentKeyCode) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    mainHandler.postDelayed(longPressRunnable, LONG_PRESS_DELAY);
                }
                if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    mainHandler.removeCallbacks(longPressRunnable);
                }
            }
        }
    }

    public void onKeyLongPress(int keyCode){
        if (keyPressIndex==0){
            return;
        }
        if (keyPressIndex==7){
            return;
        }else {
            if (keyCode == currentKeyCode) {
                longPressRunnable.run();
            }
        }
    }

    public void updateTriggerType(){
        keyPressIndex=SPHelper.getInt(ConstantUtil.LONG_PRESS_KEY_INDEX,0);
        switch (keyPressIndex){
            case 1:
                currentKeyCode=KeyEvent.KEYCODE_BACK;
                break;
            case 2:
                currentKeyCode=KeyEvent.KEYCODE_HOME;
                break;
            case 3:
                currentKeyCode=KeyEvent.KEYCODE_APP_SWITCH;
                break;
            case 4:
                currentKeyCode=KeyEvent.KEYCODE_MENU;
                break;
            case 5:
                currentKeyCode=KeyEvent.KEYCODE_VOLUME_UP;
                break;
            case 6:
                currentKeyCode=KeyEvent.KEYCODE_VOLUME_DOWN;
                break;
            case 7:
            default:
                currentKeyCode=0;
        }
        lastKeyEvent=null;
    }

}
