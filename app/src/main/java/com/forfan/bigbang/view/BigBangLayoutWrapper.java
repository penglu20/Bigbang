package com.forfan.bigbang.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.util.ViewUtil;

/**
 * Created by Administrator on 2016/11/21.
 */
public class BigBangLayoutWrapper extends FrameLayout  {

    private BigBangLayout mBigBangLayout;
    private BigBangBottom mBottom;
    private BigBangHeader mHeader;
    private ScrollView mScrollView;

    private boolean fullScreenMode=false;
    private boolean stickHeader = false;


    public BigBangLayoutWrapper(Context context) {
        super(context);
        init();
    }

    public BigBangLayoutWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BigBangLayoutWrapper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BigBangLayoutWrapper(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setBackgroundColorWithAlpha(int color,int alpha) {
        alpha = (int) ((alpha / 100.0f) * 255);
        setBackgroundColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)));
    }

    public void addTextItem(String text) {
        mBigBangLayout.addTextItem(text);
    }

    public void reset() {
        mBigBangLayout.reset();
    }
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.bigbang_layout,this);

        mBigBangLayout= (BigBangLayout) findViewById(R.id.bigbang);
        mBottom= (BigBangBottom) findViewById(R.id.bottom);
        mHeader= (BigBangHeader) findViewById(R.id.header);
        mScrollView = (ScrollView) findViewById(R.id.bigbang_scroll);

        mBigBangLayout.setActionListener(new BigBangLayout.ActionListener() {
            @Override
            public void onSelected(String text) {
                if (mActionListener!=null){
                    mActionListener.onSelected(text);
                }
            }

            @Override
            public void onSearch(String text) {
                if (mActionListener!=null){
                    mActionListener.onSearch(text);
                }
            }

            @Override
            public void onShare(String text) {
                if (mActionListener!=null){
                    mActionListener.onShare(text);
                }
            }

            @Override
            public void onCopy(String text) {
                if (mActionListener!=null){
                    mActionListener.onCopy(text);
                }
            }

            @Override
            public void onTrans(String text) {
                if (mActionListener!=null){
                    mActionListener.onTrans(text);
                }
            }

            @Override
            public void onDrag() {
                if (mActionListener!=null){
                    mActionListener.onDrag();
                }
            }

            @Override
            public void onDragSelectEnd() {
                mBottom.onDragSelectEnd();
            }
        });

        mHeader.setActionListener(new BigBangHeader.ActionListener() {
            @Override
            public void onSearch() {
                mBigBangLayout.onSearch();
            }

            @Override
            public void onShare() {
                mBigBangLayout.onShare();
            }

            @Override
            public void onCopy() {
                mBigBangLayout.onCopy();
            }

            @Override
            public void onTrans() {
                mBigBangLayout.onTrans();
            }
        });
        mBottom.setActionListener(new BigBangBottom.ActionListener() {
            @Override
            public void onDrag() {
                mBigBangLayout.onDrag();
            }

            @Override
            public void onDragSelect(boolean isDragSelcetion) {
                mBigBangLayout.onDragSelect(isDragSelcetion);
                if (mActionListener!=null){
                    mActionListener.onDragSelection();
                }

            }

            @Override
            public void onSwitchType(boolean isLocal) {
                if (mActionListener!=null){
                    mActionListener.onSwitchType(isLocal);
                }
            }

            @Override
            public void onSelectOther() {
                mBigBangLayout.onSelectOther();
            }

            @Override
            public void onSwitchSymbol(boolean isShow) {
                mBigBangLayout.setShowSymbol(isShow);
                if (mActionListener!=null){
                    mActionListener.onSwitchSymbol(isShow);
                }
            }

            @Override
            public void onSwitchSection(boolean isShow) {
                mBigBangLayout.setShowSection(isShow);
                if (mActionListener!=null){
                    mActionListener.onSwitchSection(isShow);
                }
            }
        });

    }

    public void onSwitchType(boolean isLocal) {
        mBottom.setIsLocal(isLocal);
    }


    public void setShowSymbol(boolean showSymbol) {
        mBottom.setShowSymbol(showSymbol);
    }

    public void setShowSection(boolean showSection) {
        mBottom.setShowSection(showSection);
    }

    public void setFullScreenMode(boolean fullScreenMode){
        this.fullScreenMode=fullScreenMode;
    }

    public void setStickHeader(boolean stickHeader) {
        this.stickHeader = stickHeader;
        mBigBangLayout.setStickHeader(stickHeader);
        mHeader.setStickHeader(stickHeader);
        if (stickHeader){
            mHeader.setVisibility(VISIBLE);
        }else {
            mHeader.setVisibility(GONE);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeader.measure(widthMeasureSpec,MeasureSpec.makeMeasureSpec(0,MeasureSpec.EXACTLY));
        int childHeight= (int) (mBottom.getMeasuredHeight()+mBigBangLayout.getMeasuredHeight() + (stickHeader?mHeader.getMeasuredHeight()*4.0/3:0));
        if (fullScreenMode){
            setMeasuredDimension(MeasureSpec.makeMeasureSpec(width,MeasureSpec.AT_MOST),MeasureSpec.makeMeasureSpec(getRootView().getHeight(),MeasureSpec.AT_MOST));
        }else {
            if (height > 0) {
                setMeasuredDimension(getMeasuredWidth(), Math.min(childHeight, height));
            } else {
                setMeasuredDimension(getMeasuredWidth(), Math.max(childHeight, height));
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int topPadding = 0;

        if (stickHeader){
            topPadding += (int) (mHeader.getMeasuredHeight()*1.0/3);
            if (fullScreenMode){
                topPadding += mHeader.getMeasuredHeight()*2.0/3+ViewUtil.getNavigationBarHeight((Activity) getContext());
            }
            mHeader.layout(left,top+topPadding,right,top+topPadding+mHeader.getMeasuredHeight());
            top = top + topPadding + mHeader.getMeasuredHeight();
        }else {
            if (fullScreenMode) {
                topPadding += (int) (mHeader.getMeasuredHeight() * 2.0 / 3)+ViewUtil.getNavigationBarHeight((Activity) getContext());
                top = top + topPadding ;
            }
        }
        if (fullScreenMode){
            if (mBigBangLayout.getMeasuredHeight()<bottom-top- mBottom.getMeasuredHeight()*3.0/3){
                //显示在中部
                int layoutBottom= (int) ((bottom- mBottom.getMeasuredHeight()*3.0/3+top)/2+mBigBangLayout.getMeasuredHeight()/2);
                int layoutTop= (int) ((bottom- mBottom.getMeasuredHeight()*3.0/3+top)/2-mBigBangLayout.getMeasuredHeight()/2);
                mScrollView.layout(left,layoutTop,right,layoutBottom);
            }else {
                mScrollView.layout(left, top, right, (int) (bottom - mBottom.getMeasuredHeight() * 3.0 / 3));
            }
            mBottom.layout(left, (int) (bottom - mBottom.getMeasuredHeight()*3.0/3), right, (int) (bottom- mBottom.getMeasuredHeight()*0.0/3));
        }else {
            mScrollView.layout(left, top, right, bottom - mBottom.getMeasuredHeight());
            mBottom.layout(left, bottom - mBottom.getMeasuredHeight(), right, bottom);
        }
    }

    public void setBottomVibility(int vibility){
        mBottom.setVisibility(vibility);
    }

    private ActionListener mActionListener;

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public interface ActionListener {
        void onSelected(String text);

        void onSearch(String text);

        void onShare(String text);

        void onCopy(String text);

        void onTrans(String text);

        void onDrag();

        void onSwitchType(boolean isLocal);

        void onSwitchSymbol(boolean isShow);

        void onSwitchSection(boolean isShow);

        void onDragSelection();
    }
}
