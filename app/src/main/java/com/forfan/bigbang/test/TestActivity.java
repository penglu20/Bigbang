package com.forfan.bigbang.test;

import android.os.Bundle;
import android.view.View;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.network.RetrofitHelper;
import com.forfan.bigbang.util.LogUtil;

import java.util.ArrayList;

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
}
