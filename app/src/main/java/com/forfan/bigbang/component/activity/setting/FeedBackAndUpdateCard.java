package com.forfan.bigbang.component.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;
import com.forfan.bigbang.component.activity.DonateActivity;
import com.forfan.bigbang.component.activity.IntroActivity;
import com.forfan.bigbang.component.service.GetAwayNotificationListenerService;
import com.forfan.bigbang.util.ChanelUtil;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.CountLinkMovementMethod;
import com.forfan.bigbang.util.NetWorkUtil;
import com.forfan.bigbang.util.SnackBarUtil;
import com.forfan.bigbang.util.UpdateUtil;
import com.forfan.bigbang.util.UrlCountUtil;
import com.forfan.bigbang.view.Dialog;
import com.forfan.bigbang.view.DialogFragment;
import com.forfan.bigbang.view.SimpleDialog;
import com.umeng.fb.FeedbackAgent;
import com.umeng.onlineconfig.OnlineConfigAgent;

/**
 * Created by penglu on 2015/11/23.
 */
public class FeedBackAndUpdateCard extends AbsCard {
    private TextView feedback;
    private TextView checkUpdate;
    private TextView problems;
    private TextView about;
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
        problems = (TextView) findViewById(R.id.problems);
        about = (TextView) findViewById(R.id.about);
        introduction = (TextView) findViewById(R.id.introduction);

        checkUpdate.setOnClickListener(myOnClickListener);
        feedback.setOnClickListener(myOnClickListener);
        problems.setOnClickListener(myOnClickListener);
        about.setOnClickListener(myOnClickListener);
        introduction.setOnClickListener(myOnClickListener);
//        if (ChanelHandler.is360SDK(context)){
//            feedback.setVisibility(View.GONE);
//        }
        findViewById(R.id.donate).setOnClickListener(myOnClickListener);
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
                UpdateUtil.UserCheckUpdate(FeedBackAndUpdateCard.this);
                break;
            case R.id.feedback:
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_FEEDBACK);
                startFeedBack();
                break;
            case R.id.about:
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_ABOUT);
                showAboutDialog();
                break;
            case R.id.introduction:
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_HOW_TO_USE);
                showIntro();
                break;
            case R.id.problems:
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_PROBLEM);
                showProblemDialog();
                break;
            case R.id.donate:
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_DONATE);
                toDonate();
                break;
            default:
                break;
        }
        }
    };

    private void toDonate() {
        Intent intent = new Intent();
        intent.setClass(mContext, DonateActivity.class);
        mContext.startActivity(intent);
    }

    private void showIntro() {
        Intent intent = new Intent();
        intent.setClass(mContext, IntroActivity.class);
        mContext.startActivity(intent);
    }

    protected void startFeedBack() {
        com.umeng.fb.util.Res.setPackageName(R.class.getPackage().getName());
        FeedbackAgent agent = new FeedbackAgent(mContext);
        agent.startFeedbackActivity();
    }
    public  static String zhifubao="https://mobilecodec.alipay.com/client_download.htm?qrcode=ap13zwff7wggcfdn80";
    public  static String qqJump= Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + "Ruk-hM-hLlIBoODmgTUpymQcrXjCPXqV").toString();

    private void showAboutDialog(){
        PackageManager manager = mContext.getPackageManager();
        PackageInfo info = null;
        String version="1.3.0";
        try {
            info = manager.getPackageInfo(mContext.getPackageName(), 0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Dialog.Builder builder = new SimpleDialog.Builder( R.style.SimpleDialogLight){
            @Override
            protected void onBuildDone(Dialog dialog) {
                ((SimpleDialog)dialog).getMessageTextView().setMovementMethod(CountLinkMovementMethod.getInstance());
                super.onBuildDone(dialog);
            }
        };
        String donate=mContext.getString(R.string.donate);


        String qq=mContext.getString(R.string.join_qq);
        ((SimpleDialog.Builder) builder).
                message( Html.fromHtml(
                        String.format(mContext.getString(R.string.about_content),version).replaceAll("\n","<br />")
                                +"<br /><a href='"+zhifubao+"'>"+donate+"</a>"
                                +"<br /><br /><a href='"+qqJump+"'>"+qq+"</a>"))
                .title(mContext.getString(R.string.about))
                .positiveAction(mContext.getString(R.string.confirm));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(((AppCompatActivity)mContext).getSupportFragmentManager(), null);
    }

    private void showProblemDialog(){

        // TODO: 2016/10/29
        Dialog.Builder builder = new SimpleDialog.Builder( R.style.SimpleDialogLight){
            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
//                if (Build.VERSION.SDK_INT >= 18 && !GetAwayNotificationListenerService.checkNotificationListenerEnabled(mContext)){
//                    try {
//                        mContext.startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
//    //                    mContext.startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
//                    } catch (Throwable e){
//                        SnackBarUtil.show(FeedBackAndUpdateCard.this,R.string.open_setting_failed);
//                    }
//                }else {
                    startFeedBack();
//                }
                super.onNegativeActionClicked(fragment);
            }
        };
        String msg=mContext.getString(R.string.problem_content);
//        if (Build.VERSION.SDK_INT >= 18 && !GetAwayNotificationListenerService.checkNotificationListenerEnabled(mContext)){
//            builder.negativeAction(mContext.getString(R.string.go_set));
//            msg=msg+"\n"+mContext.getString(R.string.go_set_msg);
//        }else {
            builder.negativeAction(mContext.getString(R.string.feed_back));
//        }
        ((SimpleDialog.Builder) builder)
                .message(msg)
                .title(mContext.getString(R.string.problems))
                .positiveAction(mContext.getString(R.string.confirm_known));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(((AppCompatActivity)mContext).getSupportFragmentManager(), null);
    }

}
