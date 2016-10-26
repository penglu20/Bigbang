package com.forfan.bigbang.network.api;

import com.forfan.bigbang.entity.TranslationItem;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by wangyan-pd on 2016/10/26.
 *  http://fanyi.youdao.com/openapi.do?keyfrom=BIgbang&key=633767736&type=data&doctype=json&callback=show&version=1.1&q=你好啊
 */

public interface TranslationService {
    @GET("openapi.do?keyfrom=BIgbang&key=633767736&type=data&doctype=json&callback=show&version=1.1")
    Observable<TranslationItem> getTranslationItem(@Query("q") String query);
}
