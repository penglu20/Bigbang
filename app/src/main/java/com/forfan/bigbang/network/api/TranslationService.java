package com.forfan.bigbang.network.api;

import com.forfan.bigbang.entity.TranslationItem;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by wangyan-pd on 2016/10/26.
 *  http://fanyi.youdao.com/openapi.do?keyfrom=BIgbang&key=*****&type=data&doctype=json&callback=show&version=1.1&q=你好啊
 */

public interface TranslationService {
    //请自行在http://fanyi.youdao.com/openapi 注册账号，并用自己的API密钥替换下面的key的值
    String key=633767736+"";
    @GET("openapi.do?keyfrom=BIgbang&key="+key+"&type=data&doctype=json&callback=show&version=1.1")
    Observable<TranslationItem> getTranslationItem(@Query("q") String query);
}
