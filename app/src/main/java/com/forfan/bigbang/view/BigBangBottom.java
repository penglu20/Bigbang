package com.forfan.bigbang.view;

import android.annotation.TargetApi;
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

public class BigBangBottom extends ViewGroup implements View.OnClickListener {

    ImageView mDragSelect;
    ImageView mDrag;
    ImageView mSelectOther;

    ImageView mType;
    ImageView mSection;
    ImageView mSymbol;


    private int mActionGap;
    private int mContentPadding;
    private ActionListener mActionListener;
    private boolean dragMode=false;
    private boolean dragSelectionMode=false;
    private boolean isLocal=false;
    private boolean showSymbol=false;
    private boolean showSection = false;

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BigBangBottom(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initSubViews();
    }

    private void initSubViews() {
        Context context = getContext();


        mDragSelect=new ImageView(context);
        mDragSelect.setImageResource(R.mipmap.ic_drag_select_36dp_n);
        mDragSelect.setOnClickListener(this);
        mDragSelect.setContentDescription(getContext().getString(R.string.drag_select_mode));

        mDrag=new ImageView(context);
        mDrag.setImageResource(R.mipmap.ic_sort_white_36dp);
        mDrag.setOnClickListener(this);
        mDrag.setContentDescription(getContext().getString(R.string.drag_mode));


        mType=new ImageView(context);
        mType.setImageResource(R.mipmap.bigbang_action_cloud);
        mType.setOnClickListener(this);
        mType.setContentDescription(getContext().getString(R.string.offline_segment));

        mSelectOther=new ImageView(context);
        mSelectOther.setImageResource(R.mipmap.bigbang_action_select_other);
        mSelectOther.setOnClickListener(this);
        mSelectOther.setContentDescription(getContext().getString(R.string.select_other));

        mSymbol=new ImageView(context);
        mSymbol.setImageResource(R.mipmap.bigbang_action_symbol);
        mSymbol.setOnClickListener(this);
        mSymbol.setContentDescription(getContext().getString(R.string.no_symbol));

        mSection=new ImageView(context);
        mSection.setImageResource(R.mipmap.bigbang_action_enter);
        mSection.setOnClickListener(this);
        mSection.setContentDescription(getContext().getString(R.string.no_section));

        addView(mDragSelect, createLayoutParams());
        addView(mDrag, createLayoutParams());
        addView(mType, createLayoutParams());
        addView(mSelectOther, createLayoutParams());
        addView(mSection, createLayoutParams());
        addView(mSymbol, createLayoutParams());

        setWillNotDraw(false);

        mActionGap = (int) ViewUtil.dp2px(5);
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
        layoutSubView(mSymbol,  mActionGap , mContentPadding);
        layoutSubView(mSection,  mActionGap * 2 + mSymbol.getMeasuredWidth() , mContentPadding);
        layoutSubView(mType,  mActionGap * 3 + mSymbol.getMeasuredWidth()*2  , mContentPadding);


        layoutSubView(mSelectOther, width - mActionGap * 3 - mSelectOther.getMeasuredWidth() - mDragSelect.getMeasuredWidth() - mDrag.getMeasuredWidth(), mContentPadding);
        layoutSubView(mDrag, width - mActionGap * 2 - mDragSelect.getMeasuredWidth() - mDrag.getMeasuredWidth(), mContentPadding);
        layoutSubView(mDragSelect, width - mActionGap - mDragSelect.getMeasuredWidth(), mContentPadding);

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

    public void onDragSelectEnd(){
        mDragSelect.setImageResource(R.mipmap.ic_drag_select_36dp_n);
        mDragSelect.setContentDescription(getContext().getString(R.string.drag_select_mode));
        mActionListener.onDragSelect(false);
        mDrag.setVisibility(VISIBLE);
        dragSelectionMode=false;
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
                mDrag.setContentDescription(getContext().getString(R.string.drag_mode_done));
                mDragSelect.setVisibility(INVISIBLE);
            }else {
                mDrag.setImageResource(R.mipmap.ic_sort_white_36dp);
                mDrag.setContentDescription(getContext().getString(R.string.drag_mode));
                mDragSelect.setVisibility(VISIBLE);
            }
            mActionListener.onDrag();
        }else if (v==mType){
            isLocal= !isLocal;
            setIsLocal(isLocal);
        }else if (v==mSelectOther){
            mActionListener.onSelectOther();
        }else if (v==mSection){
            showSection= !showSection;
            setShowSection(showSection);
        }else if (v==mSymbol){
            showSymbol=!showSymbol;
            setShowSymbol(showSymbol);
        }else if (v==mDragSelect){
            if (dragSelectionMode){
                onDragSelectEnd();
            }else {
                mDragSelect.setImageResource(R.mipmap.ic_drag_select_36dp_p);
                mDrag.setVisibility(INVISIBLE);
                mDragSelect.setContentDescription(getContext().getString(R.string.drag_select_mode_done));
                mActionListener.onDragSelect(true);
                dragSelectionMode=true;
            }

        }
    }

    public void setIsLocal(boolean isLocal){
        this.isLocal=isLocal;
        mActionListener.onSwitchType(isLocal);
        if (isLocal) {
            mType.setImageResource(R.mipmap.bigbang_action_local);
            mType.setContentDescription(getContext().getString(R.string.online_segment));
        }else {
            mType.setImageResource(R.mipmap.bigbang_action_cloud);
            mType.setContentDescription(getContext().getString(R.string.offline_segment));
        }
    }

    public void setShowSymbol(boolean showSymbol) {
        this.showSymbol = showSymbol;
        if (mActionListener!=null){
            mActionListener.onSwitchSymbol(showSymbol);
        }
        if (showSymbol){
            mSymbol.setImageResource(R.mipmap.bigbang_action_symbol);
            mSymbol.setContentDescription(getContext().getString(R.string.no_symbol));
        }else {
            mSymbol.setImageResource(R.mipmap.bigbang_action_no_symbol);
            mSymbol.setContentDescription(getContext().getString(R.string.remain_symbol));
        }
    }

    public void setShowSection(boolean showSection) {
        this.showSection = showSection;
        if (mActionListener!=null){
            mActionListener.onSwitchSection(showSection);
        }
        if (showSection){
            mSection.setImageResource(R.mipmap.bigbang_action_enter);
            mSection.setContentDescription(getContext().getString(R.string.no_section));
        }else {
            mSection.setImageResource(R.mipmap.bigbang_action_no_enter);
            mSection.setContentDescription(getContext().getString(R.string.remain_section));
        }
    }

    interface ActionListener {
        void onDrag();
        void onDragSelect(boolean isDragSelect);
        void onSwitchType(boolean isLocal);
        void onSelectOther();
        void onSwitchSymbol(boolean isShow);
        void onSwitchSection(boolean isShow);
    }
}
