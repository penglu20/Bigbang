package com.forfan.bigbang.test;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.network.RetrofitHelper;
import com.forfan.bigbang.util.LogUtil;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }
    public void onRetrofit(View view){
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("你好啊");
        arrayList.add("你好啊");
        arrayList.add("你好啊");
        RetrofitHelper.getWordSegmentService()
                .getWordSegsList("BigBang 是您的快捷助手，您只需在设置中开启BigBang的辅助功能，便可以在任意app中对文字进行进行编辑，包括分词，翻译，复制以及动态调整，希望您能在日常生活中获得便利")
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recommendInfo -> {
                    LogUtil.d(recommendInfo.toString());
                }, throwable -> {
                    LogUtil.d(throwable.toString());
                });
    }

    public void onTranslate(View view){

        RetrofitHelper.getTranslationService()
                .getTranslationItem("我好累哦 我想休息一下下 怎么办么")
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recommendInfo -> {
                    LogUtil.d(recommendInfo.toString());
                }, throwable -> {
                    LogUtil.d(throwable.toString());
                });
    }
    public void onOcr(View view){
        String descriptionString = "hello, this is description speaking";
        File file = new File("/storage/emulated/0/share.png");
        RequestBody requestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        RetrofitHelper.getOcrService()
                .uploadImage("e02e6b613488957",descriptionString,requestBody)
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recommendInfo -> {
                    LogUtil.d(recommendInfo.toString());
                }, throwable -> {
                    LogUtil.d(throwable.toString());
                });
//                .enqueue(new Callback<String>() {
//                    @Override
//                    public void onResponse(Call<String> call, Response<String> response) {
//                        LogUtil.d(response.body());
//                    }
//
//                    @Override
//                    public void onFailure(Call<String> call, Throwable t) {
//                        LogUtil.d(t.getMessage());
//                    }
//                });

    }
    public static void upload(String path){

//        String descriptionString = "hello, this is description speaking";
//
//        String[] m = new String[2];
//        m[0]= "share.png";
//        m[1]=  "Screenshot_20160128-140709.png";
//        File[]  ssssss= new  File[2];
//        File file1 = new File("/storage/emulated/0/sc/share.png");
//        File file = new File("/storage/emulated/0/Pictures/ScreenShots/Screenshot_20160128-140709.png");
//        ssssss[0]=file;
//        ssssss[0]=file1;
//        RequestBody requestBody[] = new RequestBody[3];
//        RequestBody requestBody1 =
//                RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        RequestBody requestBody2 =
//                RequestBody.create(MediaType.parse("multipart/form-data"), file1);
//        requestBody[0]=requestBody1;
//        requestBody[1]=requestBody2;
//        Call<String> call = apiManager.uploadImage( m[0],requestBody1,requestBody2,null);
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Response<String> response, Retrofit retrofit) {
//                Log.v("Upload", response.message());
//                Log.v("Upload", "success");
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                Log.e("Upload", t.toString());
//            }
//        });

    }
}
