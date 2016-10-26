package com.forfan.bigbang.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.activity.BaseActivity;
import com.forfan.bigbang.network.RetrofitHelper;
import com.forfan.bigbang.util.LogUtil;
import com.google.gson.Gson;

import java.util.ArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
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
                .getWordSegsList("你好啊")
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recommendInfo -> {
                    LogUtil.d(recommendInfo.toString());
                }, throwable -> {
                    LogUtil.d(throwable.toString());
                });
    }
}
