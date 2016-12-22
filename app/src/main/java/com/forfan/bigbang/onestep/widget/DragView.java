package com.forfan.bigbang.onestep.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by Gavin on 2016/10/19.
 */

public class DragView extends ImageView  {

    private int mTouchX, mTouchY;
    private int mCreateX, mCreateY;

    private int layoutWidth, layoutHeight;

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;

    private View mTargetView;

    private FrameLayout layout;

    @Deprecated
    public DragView(Context context) {
        super(context);
    }

    public DragView(Context context, View targetView) {
        super(context);
        this.mTargetView = targetView;
        init();
    }

    @Override
    public void setScaleX(float scaleX) {
        super.setScaleX(scaleX);
    }

    private void init() {
        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

        int[] position = new int[2];
        mTargetView.getLocationOnScreen(position);
        int x = position[0];
        int y = position[1];

        mCreateX = x;
        mCreateY = y;
        layoutWidth = mTargetView.getWidth();
        layoutHeight = mTargetView.getHeight();
    }

    public void dragStart() {
        show();
        setBackgroundColor(0xDCFFFFFF);
    }

    public void dragEnd() {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(this, "x", getX(), mCreateX);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(this, "y", getY(), mCreateY);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(550);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.play(animatorX).with(animatorY);
        animatorSet.addListener(animatorListener);
        animatorSet.start();
    }

    public void drop() {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(this, "x", getX(), mTouchX - layoutWidth / 2);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(this, "y", getY(), mTouchY - layoutHeight / 2);
        ObjectAnimator animatorWidth = ObjectAnimator.ofFloat(this, "scaleX", 1f, 0f);
        ObjectAnimator animatorHeight = ObjectAnimator.ofFloat(this, "scaleY", 1f, 0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(450);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.play(animatorX).with(animatorY).with(animatorWidth).with(animatorHeight);
        animatorSet.addListener(animatorListener);
        animatorSet.start();

        mTargetView.setVisibility(VISIBLE);
    }

    Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {

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
    };

    private void show() {
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.flags = mLayoutParams.flags | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        mLayoutParams.flags = mLayoutParams.flags | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mLayoutParams.gravity = Gravity.START | Gravity.TOP;

        mLayoutParams.x = 0;
        mLayoutParams.y = 0;

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(dm);

        mLayoutParams.width = dm.widthPixels;
        mLayoutParams.height = dm.heightPixels;
        layout = new FrameLayout(getContext());
        mWindowManager.addView(layout, mLayoutParams);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(layoutWidth, layoutHeight);
        setX(mCreateX);
        setY(mCreateY);
        layout.addView(this, params);

        mTargetView.setVisibility(INVISIBLE);
    }

    public void move(int offsetX, int offsetY) {
        setX(mCreateX - offsetX);
        setY(mCreateY - offsetY);
    }

    private void remove() {
        this.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
        mWindowManager.removeView(layout);

        recycleBitmap();

        mTargetView.setVisibility(VISIBLE);
    }

    public void setTouchPosition(int touchX, int touchY) {
        mTouchX = touchX;
        mTouchY = touchY;
    }

    private void recycleBitmap() {
        Drawable drawable = getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }

}
