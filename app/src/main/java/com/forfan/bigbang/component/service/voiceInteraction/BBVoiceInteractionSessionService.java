package com.forfan.bigbang.component.service.voiceInteraction;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.service.voice.VoiceInteractionSession;
import android.service.voice.VoiceInteractionSessionService;

import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.LogUtil;
import com.shang.commonjar.contentProvider.SPHelper;

/**
 * Created by penglu on 2016/12/16.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BBVoiceInteractionSessionService extends VoiceInteractionSessionService {
    @Override
    public VoiceInteractionSession onNewSession(Bundle args) {
        return new BBVoiceInteractionSession(this);
    }


}
