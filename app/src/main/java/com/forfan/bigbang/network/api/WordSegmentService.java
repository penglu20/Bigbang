package com.forfan.bigbang.network.api;

import com.forfan.bigbang.entity.wordsegement.WordSegs;

import java.util.ArrayList;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by wangyan-pd on 2016/10/26.
 */

public interface WordSegmentService {
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
            "X-Token: IZGVLxjs.10189.fkzlXn0jsUBp"
    })
    @POST("tag/analysis?space_mode=0&oov_level=3&t2s=0")
    Observable<ArrayList<WordSegs>> getWordSegsList(@Body String string);
}
