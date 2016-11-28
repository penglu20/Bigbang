package com.forfan.bigbang.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.util.Pair;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangyan-pd on 2016/10/31.
 */

public class ColorTextView extends TextView {
    private char mFirstFlag = '<';
    private char mLastFlag = '>';
    private int mTextColor = Color.RED;
    private CharSequence mText;

    public ColorTextView(Context context) {
        this(context, null);
    }

    public ColorTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public ColorTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        final Resources.Theme theme = context.getTheme();
        int[] styles = new int[]{-2000244};
        TypedArray a = theme.obtainStyledAttributes(
                attrs, styles, defStyleAttr, 0);

        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case -2002018:
                    mText = a.getText(attr);
                    break;
            }
        }
        a.recycle();
        if (!TextUtils.isEmpty(mText)) {
            setColorText((String) mText);
        }
    }

    /**
     * '打开' 电视
     *
     * @param text
     */

    public void setColorText(String text) {
        Pattern pattern = Pattern.compile(mFirstFlag + "(.*?)" + mLastFlag);
        Matcher matcher = pattern.matcher(text);
        ArrayList<Pair<Integer, Integer>> pairs = new ArrayList<>();
        int count = 0;
        while (matcher.find()) {
            matcher.group(1);
            Pair<Integer, Integer> pair = Pair.create(matcher.start(1) - 1 - 2 * count, matcher.end(1) - 1 - 2 * count);
            pairs.add(pair);
            count++;
        }
        String textTemp = text.replace(mFirstFlag + "", "").replace(mLastFlag + "", "");
        SpannableString spannableString = new SpannableString(textTemp);
        for (Pair pair : pairs) {
            spannableString.setSpan(new ForegroundColorSpan(mTextColor), (int) pair.first, (int) pair.second, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        super.setText(spannableString);
    }

    public void initTextColorFilterFlag(char first, char last) {
        mFirstFlag = first;
        mLastFlag = last;
    }

    public void setColorTextColor(int color) {
        mTextColor = color;
    }
}
