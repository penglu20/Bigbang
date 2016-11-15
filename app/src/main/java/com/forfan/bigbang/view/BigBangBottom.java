package com.forfan.bigbang.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.util.ViewUtil;

class BigBangBottom extends ViewGroup implements View.OnClickListener {

    ImageView mDrag;
    ImageView mSelectAll;
    ImageView mSelectOther;


    private int mActionGap;
    private int mContentPadding;
    private ActionListener mActionListener;
    private boolean dragMode=false;

    public BigBangBottom(Context context) {
        this(context, null);
    }

    public BigBangBottom(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BigBangBottom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSubViews();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BigBangBottom(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initSubViews();
    }

    private void initSubViews() {
        Context context = getContext();


        mDrag=new ImageView(context);
        mDrag.setImageResource(R.mipmap.ic_sort_white_36dp);
        mDrag.setOnClickListener(this);


        mSelectAll=new ImageView(context);
        mSelectAll.setImageResource(R.mipmap.bigbang_action_select_all);
        mSelectAll.setOnClickListener(this);

        mSelectOther=new ImageView(context);
        mSelectOther.setImageResource(R.mipmap.bigbang_action_select_other);
        mSelectOther.setOnClickListener(this);

        addView(mDrag, createLayoutParams());
        addView(mSelectAll, createLayoutParams());
        addView(mSelectOther, createLayoutParams());

        setWillNotDraw(false);

        mActionGap = (int) ViewUtil.dp2px(15);
        mContentPadding = (int) ViewUtil.dp2px(10);
    }

    private LayoutParams createLayoutParams() {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        return params;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        int measureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.measure(measureSpec, measureSpec);
        }

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width, height + mContentPadding + mDrag.getMeasuredHeight());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        layoutSubView(mSelectAll, width - mActionGap * 3 - mDrag.getMeasuredWidth()*2 - mDrag.getMeasuredWidth() , 0);
        layoutSubView(mSelectOther, width - mActionGap * 2 - mDrag.getMeasuredWidth() - mDrag.getMeasuredWidth(), 0);
        layoutSubView(mDrag, width - mActionGap - mDrag.getMeasuredWidth(), 0);

    }

    private void layoutSubView(View view, int l, int t) {
        view.layout(l, t, view.getMeasuredWidth() + l, view.getMeasuredHeight() + t);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }


    public int getContentPadding() {
        return mContentPadding;
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    @Override
    public void onClick(View v) {
        if (mActionListener == null) {
            return;
        }
        if (v==mDrag){
            dragMode=!dragMode;
            if (dragMode) {
                mDrag.setImageResource(R.mipmap.ic_done_white_36dp);
            }else {
                mDrag.setImageResource(R.mipmap.ic_sort_white_36dp);
            }
            mActionListener.onDrag();
        }else if (v==mSelectAll){
            mActionListener.onSelectAll();
        }else if (v==mSelectOther){
            mActionListener.onSelectOther();
        }
    }

    interface ActionListener {
        void onDrag();
        void onSelectAll();
        void onSelectOther();
    }
}
