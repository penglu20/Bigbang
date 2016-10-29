package com.forfan.bigbang.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.forfan.bigbang.R;
import com.forfan.bigbang.view.BigBangLayout;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class TipViewController implements  View.OnTouchListener {
    private static final String TAG="TipViewController";

    private static final int MOVETOEDGE=10010;


    private WindowManager mWindowManager;
    private Context mContext;
    private View mWholeView;
    private BigBangLayout bigBangLayout;
    private FrameLayout bangWrap;
    private CheckBox floagImage;
    private Handler mainHandler;
    private WindowManager.LayoutParams layoutParams;
    private float mTouchStartX,mTouchStartY;
    private int rotation;
    private boolean isMovingToEdge=false;
    private float density=0;
    private boolean showBigBang=false;
    private boolean isMoving=false;
    private int mScaledTouchSlop;

    private ActionListener mActionListener;

    public TipViewController(Context application) {
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
                        }
                        break;
                }
            }
        };

        mScaledTouchSlop = (int) ViewUtil.dp2px(20);
    }

    public void show() {

        if (mWholeView!=null){
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

        mWholeView = View.inflate(mContext, R.layout.view_float, null);

        floagImage = (CheckBox) mWholeView.findViewById(R.id.image);
        bigBangLayout= (BigBangLayout) mWholeView.findViewById(R.id.bang_ll);
        bangWrap= (FrameLayout) mWholeView.findViewById(R.id.bang_wrap);


        DisplayMetrics displayMetrics=new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
        density=displayMetrics.density;

        // event listeners
        mWholeView.setOnTouchListener(this);

        floagImage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showBigBang=isChecked;
                if (mActionListener!=null){
                    mActionListener.isShow(showBigBang);
                }
            }
        });
        floagImage.setChecked(true);

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

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                mWindowManager.addView(mWholeView, layoutParams);
            }
        });
    }

    public void hide(){
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mWholeView!=null) {
                    mWholeView.setVisibility(View.GONE);
                }
            }
        });
    }

    public void remove(){
        if (mWindowManager!=null&&mWholeView!=null) {
            mWindowManager.removeView(mWholeView);
        }
    }

    public void showImage(){
        if (mWholeView==null){
            show();
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                floagImage.setVisibility(View.VISIBLE);
                bangWrap.setVisibility(View.GONE);
                mWindowManager.updateViewLayout(mWholeView, layoutParams);
            }
        });
    }

    public void showBigBang(final String ... txts){
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
                floagImage.setVisibility(View.GONE);
                bangWrap.setVisibility(View.VISIBLE);
                mWindowManager.updateViewLayout(mWholeView, layoutParams);
            }
        });

    }

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
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = x;
                mTouchStartY = y;
                isMoving=false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(x-mTouchStartX)>mScaledTouchSlop||Math.abs(y-mTouchStartY)>mScaledTouchSlop){
                    isMoving=true;
                }
                updateViewPosition(x-mWholeView.getWidth()/2,y-mWholeView.getHeight());
                break;
            case MotionEvent.ACTION_UP:
                if (isMoving||Math.abs(x-mTouchStartX)>mScaledTouchSlop||Math.abs(y-mTouchStartY)>mScaledTouchSlop){
                }else {
                    floagImage.setChecked(!floagImage.isChecked());
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

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public interface ActionListener{
        void isShow(boolean isShow);
    }

}
