package com.forfan.bigbang.component.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;
import com.forfan.bigbang.component.activity.FeedbackActivity;
import com.forfan.bigbang.component.activity.howtouse.HowToUseActivity;
import com.forfan.bigbang.util.ChanelUtil;
import com.forfan.bigbang.util.NetWorkUtil;
import com.forfan.bigbang.util.SnackBarUtil;
import com.forfan.bigbang.util.UpdateUtil;
import com.forfan.bigbang.util.UrlCountUtil;

/**
 * Created by penglu on 2015/11/23.
 */
public class FeedBackAndUpdateCard extends AbsCard {
    private TextView feedback;
    private TextView checkUpdate;
    private TextView introduction;

    public FeedBackAndUpdateCard(Context context) {
        super(context);
        initView(context);
    }

    public FeedBackAndUpdateCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public FeedBackAndUpdateCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    protected void initView(Context context) {
        mContext=context;
        LayoutInflater.from(mContext).inflate(R.layout.card_feedback_update,this);
        checkUpdate= (TextView) findViewById(R.id.check_update);
        feedback = (TextView) findViewById(R.id.feedback);
        introduction = (TextView) findViewById(R.id.introduction);

        checkUpdate.setOnClickListener(myOnClickListener);
        feedback.setOnClickListener(myOnClickListener);
        introduction.setOnClickListener(myOnClickListener);
//        if (ChanelHandler.is360SDK(context)){
//            feedback.setVisibility(View.GONE);
//        }
    }

    private View.OnClickListener myOnClickListener =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.check_update:
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_CHECK_FOR_UPDATE);
                if (!NetWorkUtil.isConnected(mContext)){
                    SnackBarUtil.show(v,R.string.snackbar_net_error);
                    return;
                }
                if (!ChanelUtil.isXposedApk(mContext)) {
                    UpdateUtil.UserCheckUpdate(FeedBackAndUpdateCard.this);
                }else {
                    SnackBarUtil.show(v,R.string.check_update_close);
                }
                break;
            case R.id.feedback:
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_FEEDBACK);
                startFeedBack();
                break;
            case R.id.introduction:
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_HOW_TO_USE);
                showIntro();
                break;
            default:
                break;
        }
        }
    };


    private void showIntro() {
        Intent intent = new Intent();
        intent.setClass(mContext, HowToUseActivity.class);
        mContext.startActivity(intent);
    }

    protected void startFeedBack() {
        Intent intent = new Intent();
        intent.setClass(mContext, FeedbackActivity.class);
        mContext.startActivity(intent);
    }

}
