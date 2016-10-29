package com.forfan.bigbang.component.activity.setting;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;
import com.forfan.bigbang.component.contentProvider.SPHelper;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.ViewUtil;
import com.umeng.fb.FeedbackAgent;

/**
 * Created by penglu on 2015/11/23.
 */
public class ShareCard extends AbsCard {
    private Context mContext;
    private TextView shareTV;
    private Button cancelBtn;
    private Button confirmBtn;
    private int[] shareRequest={R.string.share_request_msg,R.string.share_request_msg_like,R.string.share_request_msg_dislike};
    private int[] shareCancel={R.string.share_request_cancel,R.string.share_request_cancel_like,R.string.share_request_cancel_dislike};
    private int[] shareConfirm={R.string.share_request_confirm,R.string.share_request_confirm_like,R.string.share_request_confirm_dislike};

    private int state=0;
    private boolean isShown=false;

    public ShareCard(Context context) {
        super(context);
        initView(context);
    }

    public ShareCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ShareCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    protected void onAttachedToWindow() {
        //eventbus在这里注册
        super.onAttachedToWindow();
//        PEventBus.getInstance().register(this);
        if (!isShown) {
            show();
        }

    }

    public void setHeight(int height){
        getLayoutParams().height=height;
        requestLayout();
    }

    @Override
    protected void onDetachedFromWindow() {
        //eventbus在这里注销
//        PEventBus.getInstance().unregister(this);
        super.onDetachedFromWindow();
    }

    protected void initView(Context context) {
        mContext=context;
        LayoutInflater.from(mContext).inflate(R.layout.card_share,this);
        shareTV= (TextView) findViewById(R.id.share_msg);
        cancelBtn= (Button) findViewById(R.id.share_cancel);
        confirmBtn= (Button) findViewById(R.id.share_confirm);
        cancelBtn.setOnClickListener(myOnClickListener);
        confirmBtn.setOnClickListener(myOnClickListener);
        refreshText();
    }

    private void refreshText() {
        shareTV.post(new Runnable() {
            @Override
            public void run() {
//                shareTV.setText(mContext.getString(shareRequest[state]));
//                cancelBtn.setText(mContext.getString(shareCancel[state]));
//                confirmBtn.setText(mContext.getString(shareConfirm[state]));
//                shareTV.setTextColor(mContext.getResources().getColor(R.color.white));
//                cancelBtn.setTextColor(mContext.getResources().getColor(R.color.white));
//                confirmBtn.setTextColor(mContext.getResources().getColor(R.color.primary));
                showText(shareTV,mContext.getString(shareRequest[state]),getResources().getColor(R.color.primary),mContext.getResources().getColor(R.color.white));
                showText(cancelBtn,mContext.getString(shareCancel[state]),getResources().getColor(R.color.primary),mContext.getResources().getColor(R.color.white));
                showText(confirmBtn,mContext.getString(shareConfirm[state]),getResources().getColor(R.color.white),mContext.getResources().getColor(R.color.primary));
//                    shareTV.setTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.title_text) );
//                    cancelBtn.setTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.title_text));
//                    confirmBtn.setTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.title_text));
            }
        });
    }

    private OnClickListener myOnClickListener =new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id=v.getId();
            switch (id){
                case R.id.share_cancel:
                    if (state==0){
                        state=2;
                        refreshText();
                    }else{
                        // TODO: 2016/2/27 删除组件
                        hide();
                    }
                    break;
                case R.id.share_confirm:
                    if (state==0){
                        state=1;
                        refreshText();
                    }else if (state==1){
                        // TODO: 2016/2/27 分享
                        SPHelper.save(ConstantUtil.HAD_SHARED,true);
                        hide();
                    }else {
                        // TODO: 2016/2/27 反馈
                        FeedbackAgent agent = new FeedbackAgent(mContext);
                        agent.startFeedbackActivity();
                        hide();
                    }
                    
                    break;
                default:
                    break;
            }
        }
    };

    public void show(){
        setHeight(0);
        post(new Runnable() {
            @Override
            public void run() {
                int height= (int) ViewUtil.dp2px(120);
                ObjectAnimator objectAnimator=ObjectAnimator.ofInt(ShareCard.this,"height",height);
                objectAnimator.setDuration(500);
                objectAnimator.setInterpolator(new OvershootInterpolator());
                objectAnimator.setRepeatCount(0);
                objectAnimator.start();
                isShown=true;
            }
        });
    }

    public void hide(){
        post(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator objectAnimator=ObjectAnimator.ofInt(ShareCard.this,"height",0);
                objectAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        setVisibility(GONE);
                        if (mListener!=null){
                            mListener.onClick(ShareCard.this);
                        }
                    }
                });
                objectAnimator.setDuration(500);
                objectAnimator.setInterpolator(new AnticipateInterpolator());
                objectAnimator.setRepeatCount(0);
                objectAnimator.start();
            }
        });
    }

    public void showText(TextView textView, String text, int fromColor, int toColor){
        textView.setText(text);
            ObjectAnimator objectAnimator= ObjectAnimator.ofInt(textView,"textColor",fromColor,toColor);
            objectAnimator.setEvaluator(new ArgbEvaluator());
            objectAnimator.setDuration(300);
            objectAnimator.setRepeatCount(0);
            objectAnimator.start();
    }

    private OnClickListener mListener;
    public void setDisMissListener(View.OnClickListener listener){
        mListener=listener;
    }
}
