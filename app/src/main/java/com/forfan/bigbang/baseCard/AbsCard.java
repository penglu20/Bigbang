package com.forfan.bigbang.baseCard;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;


/**
 * Created by penglu on 2016/3/6.
 */
public class AbsCard extends CardView {
    protected Context mContext;

    public AbsCard(Context context) {
        super(context);
        mContext=context;
    }

    public AbsCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
    }

    public AbsCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
    }


}
