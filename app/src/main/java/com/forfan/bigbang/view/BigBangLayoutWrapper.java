package com.forfan.bigbang.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.forfan.bigbang.R;

/**
 * Created by Administrator on 2016/11/21.
 */
public class BigBangLayoutWrapper extends FrameLayout  {

    private BigBangLayout mBigBangLayout;
    private BigBangBottom mBottom;
    private ScrollView mScrollView;

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
        });


        mBottom.setActionListener(new BigBangBottom.ActionListener() {
            @Override
            public void onDrag() {
                mBigBangLayout.onDrag();
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childHeight=mBottom.getMeasuredHeight()+mBigBangLayout.getMeasuredHeight();
        if (height>0) {
            setMeasuredDimension(getMeasuredWidth(), Math.min(childHeight, height));
        }else {
            setMeasuredDimension(getMeasuredWidth(), Math.max(childHeight, height));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mScrollView.layout(left,top,right,bottom-mBottom.getMeasuredHeight());
        mBottom.layout(left,bottom-mBottom.getMeasuredHeight(),right,bottom);
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
    }
}
