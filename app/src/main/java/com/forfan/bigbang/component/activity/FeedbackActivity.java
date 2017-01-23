package com.forfan.bigbang.component.activity;

import android.Manifest;
import android.os.Bundle;
import android.view.ViewGroup;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.forfan.bigbang.BigBangApp;
import com.forfan.bigbang.R;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.util.ConstantUtil;
import com.shang.utils.StatusBarCompat;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by penglu on 2017/1/4.
 */

public class FeedbackActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        StatusBarCompat.setupStatusBarView(this, (ViewGroup) getWindow().getDecorView(), true, R.color.colorPrimary);
        checkPermission();
    }

    private void startFeedback() {
        //2.0的反馈sdk调用方式
//        FeedbackAPI.init(BigBangApp.getInstance(), ConstantUtil.ALI_APP_KEY);
//
//        FragmentManager fm = getSupportFragmentManager();
//        final FragmentTransaction transaction = fm.beginTransaction();
//        final Fragment feedback = FeedbackAPI.getFeedbackFragment();
//        // must be called
//        FeedbackAPI.setFeedbackFragment(new Callable() {
//            @Override
//            public Object call() throws Exception {
//                transaction.replace(R.id.content, feedback);
//                transaction.commit();
//                return null;
//            }
//        }/*success callback*/, null/*fail callback*/);


        FeedbackAPI.initAnnoy(BigBangApp.getInstance(), ConstantUtil.ALI_APP_KEY);


        //可以设置UI自定义参数，如主题色等,map的key值具体为：
        Map<String, String> uiCustomInfoMap = new HashMap<String, String>();
        uiCustomInfoMap.put("enableAudio", "1");
        uiCustomInfoMap.put("hideLoginSuccess", "true");
        //enableAudio(是否开启语音 1：开启 0：关闭)
        //bgColor(消息气泡背景色 "#ffffff")，
        //color(消息内容文字颜色 "#ffffff")，
        //avatar(当前登录账号的头像)，string，为http url
        //toAvatar(客服账号的头像),string，为http url
        //themeColor(标题栏自定义颜色 "#ffffff")
        //profilePlaceholder: (顶部联系方式)，string
        //profileTitle: （顶部联系方式左侧提示内容）, String
        //chatInputPlaceholder: (输入框里面的内容),string
        //profileUpdateTitle:(更新联系方式标题), string
        //profileUpdateDesc:(更新联系方式文字描述), string
        //profileUpdatePlaceholder:(更新联系方式), string
        //profileUpdateCancelBtnText: (取消更新), string
        //profileUpdateConfirmBtnText: (确定更新),string
        //sendBtnText: (发消息),string
        //sendBtnTextColor: ("white"),string
        //sendBtnBgColor: ('red'),string
        //hideLoginSuccess: true  隐藏登录成功的toast
        //pageTitle: （Web容器标题）, string
        //photoFromCamera: (拍摄一张照片),String
        //photoFromAlbum: (从相册选取), String
        //voiceContent:(点击这里录制语音), String
        //voiceCancelContent: (滑到这里取消录音), String
        //voiceReleaseContent: (松开取消录音), String

        FeedbackAPI. setUICustomInfo(uiCustomInfoMap);


        //设置自定义联系方式
        //@param customContact  自定义联系方式
        //@param hideContactView 是否隐藏联系人设置界面

//        FeedbackAPI.setCustomContact("null", false);

        FeedbackAPI.openFeedbackActivity(this);
        finish();
    }

    private void checkPermission() {
        checkPermission(new CheckPermListener() {
                            @Override
                            public void grantPermission() {
                                startFeedback();
                            }

                            @Override
                            public void denyPermission() {
                                startFeedback();
                            }
                        }, R.string.ask_again,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

}
