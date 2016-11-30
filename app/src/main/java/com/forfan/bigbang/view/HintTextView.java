package com.forfan.bigbang.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.util.ViewUtil;

/**
 * Created by penglu on 2016/11/26.
 */

public class HintTextView extends LinearLayout {

    private static final int DEFAULT_MSG_COLOR= Color.BLACK;
    private static final int DEFAULT_HINT_COLOR = Color.GRAY;

    private static final int DEFAULT_MSG_SIZE = 16;
    private static final int DEFAULT_HINT_SIZE = 10;

    private String msg;
    private String hint;
    private int msgColor = DEFAULT_MSG_COLOR;
    private int hintColor = DEFAULT_HINT_COLOR;
    private int msgSize = DEFAULT_MSG_SIZE;
    private int hintSize = DEFAULT_HINT_SIZE;
    private boolean showHint =false;

    private TextView msgTv,hintTv;
    private boolean showAnimation=false;
    private boolean showAnimationOnce=false;

    public HintTextView(Context context) {
        super(context);
        initView(context, null);
    }

    public HintTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public HintTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HintTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(Context context,AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.hint_text_view_layout,this);
        if (attrs!=null){
            TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.HintTextView);
            int length = typedArray.getIndexCount();
            for (int i=0;i<length;i++) {
                switch (typedArray.getIndex(i)){
                    case R.styleable.HintTextView_msg:
                        msg = typedArray.getString(R.styleable.HintTextView_msg);
                        break;
                    case R.styleable.HintTextView_hint:
                        hint = typedArray.getString(R.styleable.HintTextView_hint);
                        break;
                    case R.styleable.HintTextView_msgTextColor:
                        msgColor = typedArray.getColor(R.styleable.HintTextView_msgTextColor, DEFAULT_MSG_COLOR);
                        break;
                    case R.styleable.HintTextView_hintTextColor:
                        hintColor = typedArray.getColor(R.styleable.HintTextView_hintTextColor, DEFAULT_HINT_COLOR);
                        break;
                    case R.styleable.HintTextView_msgTextSize:
                        msgSize = (int) ViewUtil.px2sp(typedArray.getDimension(R.styleable.HintTextView_msgTextSize, ViewUtil.sp2px(DEFAULT_MSG_SIZE)));
                        break;
                    case R.styleable.HintTextView_hintTextSize:
                        hintSize = (int) ViewUtil.px2sp(typedArray.getDimension(R.styleable.HintTextView_hintTextSize, ViewUtil.sp2px(DEFAULT_HINT_SIZE)));
                        break;
                }
            }
        }

        msgTv= (TextView) findViewById(R.id.msg);
        hintTv= (TextView) findViewById(R.id.hint);

        msgTv.setText(msg);
        msgTv.setTextSize(msgSize);
        msgTv.setTextColor(msgColor);

        hintTv.setText(hint);
        hintTv.setTextSize(hintSize);
        hintTv.setTextColor(hintColor);


    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int msgTopOld=msgTv.getTop();
        int hintTopOld = hintTv.getTop();

        super.onLayout(changed, l, t, r, b);

        int msgTopNew=msgTv.getTop();
        int hintTopNew=hintTv.getTop();

        if (showAnimation && showAnimationOnce) {
            if (msgTopOld!=0 && msgTopNew!=msgTopOld) {
                msgTv.clearAnimation();
                msgTv.setY(msgTopOld);
                msgTv.animate().y(msgTopNew).setDuration(300).start();
            }

            if (hintTopOld!=0) {
                if (showHint) {
                    hintTv.clearAnimation();
                    hintTv.setY(hintTopOld);
                    hintTv.setAlpha(0);
                    hintTv.animate().y(hintTopNew).alpha(1).setDuration(300).start();
                } else {
                    hintTv.clearAnimation();
                    hintTv.setY(hintTopOld);
                    hintTv.setAlpha(1);
                    hintTv.animate().y(hintTopNew).alpha(0).setDuration(300).start();
                }
            }

            showAnimationOnce=false;

        }

    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
         msgTv.setText(msg);
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
        hintTv.setText(hint);
    }

    public int getMsgColor() {
        return msgColor;
    }

    public void setMsgColor(int msgColor) {
        this.msgColor = msgColor;
        msgTv.setTextColor(msgColor);
    }

    public int getHintColor() {
        return hintColor;
    }

    public void setHintColor(int hintColor) {
        this.hintColor = hintColor;
        hintTv.setTextColor(hintColor);
    }

    public int getMsgSize() {
        return msgSize;
    }

    public void setMsgSize(int msgSize) {
        this.msgSize = msgSize;
        msgTv.setTextSize(msgSize);
    }

    public int getHintSize() {
        return hintSize;
    }

    public void setHintSize(int hintSize) {
        this.hintSize = hintSize;
        hintTv.setTextSize(hintSize);
    }

    public boolean isShowHint() {
        return showHint;
    }

    public void setShowHint(boolean showHint) {
        this.showHint = showHint;
        if (showHint){
            hintTv.setVisibility(VISIBLE);
        }else {
            hintTv.setVisibility(GONE);
        }
        showAnimationOnce=true;
    }

    public void setShowAnimation(boolean showAnimation) {
        this.showAnimation = showAnimation;
    }
}
