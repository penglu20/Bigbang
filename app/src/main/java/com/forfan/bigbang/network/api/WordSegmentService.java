package com.forfan.bigbang.network.api;

import com.forfan.bigbang.entity.WordSegs;

import java.util.ArrayList;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by wangyan-pd on 2016/10/26.
 */

public interface WordSegmentService {
    //请自行在http://bosonnlp.com/account/register 注册账号，并用自己的API密钥替换下面的X-Token的值
    String XToken="8LgdUIaz.11946.eK7nidaC_A_U";
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
            "X-Token: "+XToken
    })
    @POST("tag/analysis?space_mode=1&oov_level=3&t2s=0&special_char_conv=1")
    Observable<ArrayList<WordSegs>> getWordSegsList(@Body String string);
}
