package com.forfan.bigbang.view;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.util.ViewUtil;

class BigBangBottom extends ViewGroup implements View.OnClickListener {

    ImageView mDrag;
    ImageView mType;
    ImageView mSelectOther;


    private int mActionGap;
    private int mContentPadding;
    private ActionListener mActionListener;
    private boolean dragMode=false;
    private boolean isLocal=false;

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


        mType=new ImageView(context);
        mType.setImageResource(R.mipmap.bigbang_action_cloud);
        mType.setOnClickListener(this);

        mSelectOther=new ImageView(context);
        mSelectOther.setImageResource(R.mipmap.bigbang_action_select_other);
        mSelectOther.setOnClickListener(this);

        addView(mDrag, createLayoutParams());
        addView(mType, createLayoutParams());
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

        setMeasuredDimension(width,  mContentPadding*2 + mDrag.getMeasuredHeight());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        layoutSubView(mType, width - mActionGap * 3 - mDrag.getMeasuredWidth()*2 - mDrag.getMeasuredWidth() , mContentPadding);
        layoutSubView(mSelectOther, width - mActionGap * 2 - mDrag.getMeasuredWidth() - mDrag.getMeasuredWidth(), mContentPadding);
        layoutSubView(mDrag, width - mActionGap - mDrag.getMeasuredWidth(), mContentPadding);

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
        }else if (v==mType){
            isLocal= !isLocal;
            setIsLocal(isLocal);
        }else if (v==mSelectOther){
            mActionListener.onSelectOther();
        }
    }

    public void setIsLocal(boolean isLocal){
        this.isLocal=isLocal;
        mActionListener.onSwitchType(isLocal);
        if (isLocal) {
            mType.setImageResource(R.mipmap.bigbang_action_local);
        }else {
            mType.setImageResource(R.mipmap.bigbang_action_cloud);
        }
    }

    interface ActionListener {
        void onDrag();
        void onSwitchType(boolean isLocal);
        void onSelectOther();
    }
}
