package com.forfan.bigbang.component.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;
import com.forfan.bigbang.component.activity.DonateActivity;
import com.forfan.bigbang.component.activity.IntroActivity;
import com.forfan.bigbang.component.activity.screen.DiyOcrKeyActivity;
import com.forfan.bigbang.component.service.GetAwayNotificationListenerService;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.CountLinkMovementMethod;
import com.forfan.bigbang.util.NetWorkUtil;
import com.forfan.bigbang.util.SnackBarUtil;
import com.forfan.bigbang.util.UpdateUtil;
import com.forfan.bigbang.util.UrlCountUtil;
import com.forfan.bigbang.view.Dialog;
import com.forfan.bigbang.view.DialogFragment;
import com.forfan.bigbang.view.SimpleDialog;
import com.shang.commonjar.contentProvider.SPHelper;
import com.umeng.fb.FeedbackAgent;

/**
 * Created by penglu on 2015/11/23.
 */
public class AboutCard extends AbsCard {
    private TextView about;
    private TextView share;

    public AboutCard(Context context) {
        super(context);
        initView(context);
    }

    public AboutCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AboutCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    protected void initView(Context context) {
        mContext=context;
        LayoutInflater.from(mContext).inflate(R.layout.card_about,this);
        about = (TextView) findViewById(R.id.about);
        share = (TextView) findViewById(R.id.share);

        about.setOnClickListener(myOnClickListener);
        share.setOnClickListener(myOnClickListener);
//        if (ChanelHandler.is360SDK(context)){
//            feedback.setVisibility(View.GONE);
//        }
        findViewById(R.id.donate).setOnClickListener(myOnClickListener);

        findViewById(R.id.diy_ocr_key).setOnClickListener(myOnClickListener);

    }

    private OnClickListener myOnClickListener =new OnClickListener() {
        @Override
        public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.about:
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_ABOUT);
                showAboutDialog();
                break;
            case R.id.share:
                ShareCard.shareToWeChat(v,mContext);
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_SHARE);
                break;
            case R.id.donate:
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_DONATE);
                toDonate();
                break;
            case R.id.diy_ocr_key:
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_DIY_OCR_KEY);
                Intent intent=new Intent(mContext, DiyOcrKeyActivity.class);
                mContext.startActivity(intent);
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

}
