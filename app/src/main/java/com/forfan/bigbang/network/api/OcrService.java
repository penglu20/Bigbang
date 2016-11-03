package com.forfan.bigbang.network.api;

import com.forfan.bigbang.entity.OcrItem;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by wangyan-pd on 2016/11/2.
 *
 * e02e6b613488957
 */

public interface OcrService {
    @Headers({
            "User-Agent: Mozilla/5.0"
    })
    @Multipart// 参数的类型都应该是RequestBody，不然上传的图片的时候会报JSON must start with an array or an object错误
    @POST("parse/image/")
    Observable<OcrItem> uploadImage(@Part("apikey")String key , @Part("fileName") String description,
                             @Part("file\"; filename=\"image.png\"") RequestBody imgs);
}
