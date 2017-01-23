package com.forfan.bigbang.network.api;

/**
 * Created by wangyan-pd on 2017/1/16.
 */

import com.forfan.bigbang.entity.ImageUpload;

import java.io.File;

import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

/**
 * https://yotuku.cn/api/upload/auto
 */
public interface PicUploadService {
    @Headers({
            "Accept:*/*",
            "Accept-Encoding:gzip, deflate",
            "Accept-Language:zh-CN,zh;q=0.8",
            "Connection:keep-alive",
            "Content-Type:text/plain",
            "User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36",
            "X-Requested-With:XMLHttpRequest"
    })
    @POST("api/upload/auto?")
    Observable<ImageUpload> uploadImage4search(@Query("name") String name, @Query("type") String type, @Body String imgs);
}
