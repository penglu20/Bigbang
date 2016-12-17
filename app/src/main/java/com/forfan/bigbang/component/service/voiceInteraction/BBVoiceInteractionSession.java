package com.forfan.bigbang.component.service.voiceInteraction;

import android.annotation.TargetApi;
import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.service.voice.VoiceInteractionSession;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.forfan.bigbang.R;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.KeyPressedTipViewController;
import com.shang.commonjar.contentProvider.SPHelper;

/**
 * Created by penglu on 2016/12/16.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BBVoiceInteractionSession extends VoiceInteractionSession {
    private Context mContext;
    public BBVoiceInteractionSession(Context context) {
        super(context);
        mContext=context;
    }

    public BBVoiceInteractionSession(Context context, Handler handler) {
        super(context, handler);
        mContext=context;
    }

    @Override
    public void onHandleAssist(Bundle data, AssistStructure structure, AssistContent content) {
        super.onHandleAssist(data, structure, content);
    }

    @Override
    public void onHandleAssistSecondary(Bundle data, AssistStructure structure, AssistContent content, int index, int count) {
        super.onHandleAssistSecondary(data, structure, content, index, count);
    }

    @Override
    public void onShow(Bundle args, int showFlags) {
        super.onShow(args, showFlags);
        if (2== SPHelper.getInt(ConstantUtil.LONG_PRESS_KEY_INDEX,0)) {
            KeyPressedTipViewController.getInstance().show(new KeyPressedTipViewController.CloseListener() {
                @Override
                public void onRemove() {
                    finish();
                }
            });
        }else {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
        }
    }


    @Override
    public void onHide() {
        KeyPressedTipViewController.getInstance().refreshViewState(false);
        super.onHide();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onHandleScreenshot(Bitmap screenshot) {
        super.onHandleScreenshot(screenshot);
    }
}
