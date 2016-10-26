package com.forfan.bigbang.network;

import com.forfan.bigbang.BigBangApp;
import com.forfan.bigbang.network.api.TranslationService;
import com.forfan.bigbang.network.api.WordSegmentService;
import com.forfan.bigbang.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wangyan-pd on 2016/10/26.
 */

public class RetrofitHelper {
    private static OkHttpClient mOkHttpClient;
    private static final String BASE_URL = "http://api.bosonnlp.com/";
    private static final String YOUDAO_URL = "http://fanyi.youdao.com/";
    static {
        initOkHttpClient();
    }



    public static class Log implements HttpLoggingInterceptor.Logger{
        @Override
        public void log(String message) {
            LogUtil.d(message);
        }
    }
    /**
     * 初始化OKHttpClient
     * 设置缓存
     * 设置超时时间
     * 设置打印日志
     * 设置UA拦截器
     */
    private static void initOkHttpClient(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new Log());
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (mOkHttpClient == null){
            synchronized (RetrofitHelper.class){
                if (mOkHttpClient == null){
                    //设置Http缓存
                    Cache cache = new Cache(new File(BigBangApp.getInstance()
                            .getCacheDir(), "HttpCache"), 1024 * 1024 * 100);
                    mOkHttpClient = new OkHttpClient.Builder()
                            .cache(cache)
                            .addInterceptor(interceptor)
                            .retryOnConnectionFailure(true)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
    }
    public static TranslationService getTranslationService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(YOUDAO_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(TranslationService.class);
    }

    public static WordSegmentService getWordSegmentService(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(WordSegmentService.class);
    }

    /**
     * 添加UA拦截器
     * B站请求API文档需要加上UA
     */
    private static class UserAgentInterceptor implements Interceptor{

        private static final String COMMON_UA_STR = "";

        @Override
        public Response intercept(Chain chain) throws IOException{
            Request originalRequest = chain.request();
            Request requestWithUserAgent = originalRequest.newBuilder()
                    .removeHeader("User-Agent")
                    .addHeader("User-Agent", COMMON_UA_STR)
                    .build();
            return chain.proceed(requestWithUserAgent);
        }
    }
}
