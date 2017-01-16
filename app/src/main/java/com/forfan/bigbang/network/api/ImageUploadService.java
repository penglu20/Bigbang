package com.forfan.bigbang.network.api;

import com.forfan.bigbang.entity.ImageUpload;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by wangyan-pd on 2017/1/9.
 */
//Content-Disposition: form-data; name="smfile"; filename="1.png"
//Content-Type: image/png
public interface ImageUploadService {
    @Headers({
            "User-Agent: Mozilla/5.0",
            "Content-Type: image/png"
    })
    @POST("")
    Observable<ImageUpload> uploadImage4search(@Body RequestBody imgs);
   // Observable<ImageUpload> uploadImage4search(@PartMap Map<String, Object> fields, @Body RequestBody imgs);
}
