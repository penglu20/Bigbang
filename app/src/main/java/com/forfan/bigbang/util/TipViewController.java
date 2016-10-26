package com.forfan.bigbang.util;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.forfan.bigbang.R;


public class TipViewController implements View.OnClickListener, View.OnTouchListener {
    private static final String TAG="TipViewController";

    private static final int MOVETOEDGE=10010;


    private WindowManager mWindowManager;
    private Context mContext;
    private View mWholeView;
    private Handler mainHandler;
    private WindowManager.LayoutParams layoutParams;
    private float mTouchStartX,mTouchStartY;
    private int rotation;
    private boolean isMovingToEdge=false;
    private float density=0;

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

        DisplayMetrics displayMetrics=new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
        density=displayMetrics.density;

        // event listeners
        mWholeView.setOnTouchListener(this);

        int w = WindowManager.LayoutParams.WRAP_CONTENT;
        int h = WindowManager.LayoutParams.WRAP_CONTENT;

        int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
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

    @Override
    public void onClick(View v) {
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
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                updateViewPosition(x,y);
                break;
            case MotionEvent.ACTION_UP:
                updateViewPosition(x,y);
                mTouchStartX = mTouchStartY = 0;
                moveToEdge();
                break;
        }
        return true;
    }

    private void updateViewPosition(float x,float y) {
        layoutParams.x = (int) (x - mTouchStartX);
        layoutParams.y = (int) (y - mTouchStartY);
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

}
