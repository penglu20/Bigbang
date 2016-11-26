package com.forfan.bigbang.component.activity.setting;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.SnackBarUtil;
import com.forfan.bigbang.util.UrlCountUtil;
import com.forfan.bigbang.util.ViewUtil;
import com.shang.commonjar.contentProvider.SPHelper;
import com.umeng.fb.FeedbackAgent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by penglu on 2015/11/23.
 */
public class ShareCard extends AbsCard {
    private Context mContext;
    private TextView shareTV;
    private Button cancelBtn;
    private Button confirmBtn;
    private int[] shareRequest = {R.string.share_request_msg, R.string.share_request_msg_like, R.string.share_request_msg_dislike};
    private int[] shareCancel = {R.string.share_request_cancel, R.string.share_request_cancel_like, R.string.share_request_cancel_dislike};
    private int[] shareConfirm = {R.string.share_request_confirm, R.string.share_request_confirm_like, R.string.share_request_confirm_dislike};

    private int state = 0;
    private boolean isShown = false;

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

    public void setHeight(int height) {
        getLayoutParams().height = height;
        requestLayout();
    }

    @Override
    protected void onDetachedFromWindow() {
        //eventbus在这里注销
//        PEventBus.getInstance().unregister(this);
        super.onDetachedFromWindow();
    }

    protected void initView(Context context) {
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.card_share, this);
        setCardBackgroundColor(getResources().getColor(R.color.primary));
        shareTV = (TextView) findViewById(R.id.share_msg);
        cancelBtn = (Button) findViewById(R.id.share_cancel);
        confirmBtn = (Button) findViewById(R.id.share_confirm);
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
                showText(shareTV, mContext.getString(shareRequest[state]), getResources().getColor(R.color.primary), mContext.getResources().getColor(R.color.white));
                showText(cancelBtn, mContext.getString(shareCancel[state]), getResources().getColor(R.color.primary), mContext.getResources().getColor(R.color.white));
                showText(confirmBtn, mContext.getString(shareConfirm[state]), getResources().getColor(R.color.white), mContext.getResources().getColor(R.color.primary));
//                    shareTV.setTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.title_text) );
//                    cancelBtn.setTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.title_text));
//                    confirmBtn.setTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.title_text));
            }
        });
    }

    private OnClickListener myOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.share_cancel:
                    if (state == 0) {
                        state = 2;
                        refreshText();
                        UrlCountUtil.onEvent(UrlCountUtil.CLICK_SHARE_CARD_DISLIKE);
                    } else {
                        // TODO: 2016/2/27 删除组件
                        hide();
                        UrlCountUtil.onEvent(UrlCountUtil.CLICK_SHARE_CARD_CANCEL);
                    }
                    break;
                case R.id.share_confirm:
                    if (state == 0) {
                        state = 1;
                        refreshText();
                        UrlCountUtil.onEvent(UrlCountUtil.CLICK_SHARE_CARD_LIKE);
                    } else if (state == 1) {
                        // TODO: 2016/2/27 分享
                        shareToWeChat(v, mContext);
                        SPHelper.save(ConstantUtil.HAD_SHARED, true);
                        UrlCountUtil.onEvent(UrlCountUtil.CLICK_SHARE_CARD_SHARE);
                        hide();
                    } else {
                        // TODO: 2016/2/27 反馈
                        FeedbackAgent agent = new FeedbackAgent(mContext);
                        agent.startFeedbackActivity();
                        UrlCountUtil.onEvent(UrlCountUtil.CLICK_SHARE_CARD_FEEDBACK);
                        hide();
                    }

                    break;
                default:
                    break;
            }
        }
    };

    private static boolean checkInstallation(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void shareToWeChat(View view, Context context) {
        // TODO: 2015/12/13 将需要分享到微信的图片准备好
        if (!checkInstallation(context, "com.tencent.mm")) {
            SnackBarUtil.show(view, R.string.share_no_wechat);
            return;
        }
        Intent intent = new Intent();
        //分享精确到微信的页面，朋友圈页面，或者选择好友分享页面
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");
//        intent.setType("text/plain");
        //添加Uri图片地址
//        String msg=String.format(getString(R.string.share_content), getString(R.string.app_name), getLatestWeekStatistics() + "");
        String msg = context.getString(R.string.share_content);
        intent.putExtra("Kdescription", msg);
        ArrayList<Uri> imageUris = new ArrayList<Uri>();
        // TODO: 2016/3/8 根据不同图片来设置分享
        File dir = context.getExternalFilesDir(null);
        if (dir == null || dir.getAbsolutePath().equals("")) {
            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        }
        File pic = new File(dir, "temp.jpg");
        pic.deleteOnExit();
        BitmapDrawable bitmapDrawable;
        if (Build.VERSION.SDK_INT < 22) {
            bitmapDrawable = (BitmapDrawable) context.getResources().getDrawable(R.mipmap.bannar);
        } else {
            bitmapDrawable = (BitmapDrawable) context.getDrawable(R.mipmap.bannar);
        }
        try {
            bitmapDrawable.getBitmap().compress(Bitmap.CompressFormat.JPEG, 75, new FileOutputStream(pic));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        imageUris.add(Uri.fromFile(pic));
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        ((Activity) context).startActivityForResult(intent, 1000);
    }

    public void show() {
        setHeight(0);
        post(new Runnable() {
            @Override
            public void run() {
                int height = (int) ViewUtil.dp2px(120);
                ObjectAnimator objectAnimator = ObjectAnimator.ofInt(ShareCard.this, "height", height);
                objectAnimator.setDuration(500);
                objectAnimator.setInterpolator(new OvershootInterpolator());
                objectAnimator.setRepeatCount(0);
                objectAnimator.start();
                isShown = true;
            }
        });
    }

    public void hide() {
        post(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator objectAnimator = ObjectAnimator.ofInt(ShareCard.this, "height", 0);
                objectAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        setVisibility(GONE);
                        if (mListener != null) {
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

    public void showText(TextView textView, String text, int fromColor, int toColor) {
        textView.setText(text);
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(textView, "textColor", fromColor, toColor);
        objectAnimator.setEvaluator(new ArgbEvaluator());
        objectAnimator.setDuration(300);
        objectAnimator.setRepeatCount(0);
        objectAnimator.start();
    }

    private OnClickListener mListener;

    public void setDisMissListener(View.OnClickListener listener) {
        mListener = listener;
    }
}
