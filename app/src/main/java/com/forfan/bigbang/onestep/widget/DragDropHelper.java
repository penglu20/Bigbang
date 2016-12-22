package com.forfan.bigbang.onestep.widget;

import android.graphics.Bitmap;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Gavin on 2016/10/21.
 */
public class DragDropHelper {

    private float mMotionDownX, mMotionDownY;
    private boolean isInit = false;

    private DragView mDragView;
    private DropView mDropView;

    private OnDropedListener mOnDropedListener;

    public void setDropView(View dropView) {
        mDropView = new DropView(dropView);
    }

    public void setOnDropedListener(OnDropedListener onDropedListener) {
        mOnDropedListener = onDropedListener;
    }

    public void startDrag(View v) {
        isInit = true;
        mDragView = new DragView(v.getContext(), v);
        mDragView.setImageBitmap(getViewBitmap(v));
        mDragView.dragStart();
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mMotionDownX = event.getRawX();
                mMotionDownY = event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                if(!isInit) return false;
                int moveX = (int) (mMotionDownX - event.getRawX());
                int moveY = (int) (mMotionDownY - event.getRawY());

                if (mDragView != null) {
                    mDragView.move(moveX, moveY);
                    mDragView.setTouchPosition((int) event.getRawX(), (int) event.getRawY());
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mDropView != null) {
                    if (mDropView.isContains(event.getRawX(), event.getRawY())) {
                        mDragView.drop();
                        if (mOnDropedListener != null) {
                            mOnDropedListener.onDroped();
                        }
                    } else if (mDragView != null) {
                        mDragView.dragEnd();
                        mDragView = null;
                    }
                    isInit = false;
                }
                break;
        }
        return true;
    }


    private Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

    public interface OnDropedListener {
        void onDroped();
    }

}
