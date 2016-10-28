package com.forfan.bigbang.component.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.network.RetrofitHelper;
import com.forfan.bigbang.util.ClipboardUtils;
import com.forfan.bigbang.util.LogUtil;
import com.forfan.bigbang.util.ToastUtil;
import com.forfan.bigbang.view.BigBangLayout;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by penglu on 2016/10/27.
 */

public class BigBangActivity extends BaseActivity {
    public static final String TO_SPLIT_STR="to_split_str";
    private BigBangLayout bigBangLayout;
    private ContentLoadingProgressBar loading;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_bang);

        Intent intent=getIntent();
        String str=intent.getStringExtra(TO_SPLIT_STR);

        if (TextUtils.isEmpty(str)){
            finish();
            return;
        }

        bigBangLayout= (BigBangLayout) findViewById(R.id.bigbang);
        loading= (ContentLoadingProgressBar) findViewById(R.id.loading);

        loading.show();
        bigBangLayout.reset();

        RetrofitHelper.getWordSegmentService()
                .getWordSegsList(str)
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recommendInfo -> {
                    LogUtil.d(recommendInfo.toString());
                    List<String> txts=recommendInfo.get(0).getWord();
                    for (String t:txts) {
                        bigBangLayout.addTextItem(t);
                    }
                    loading.hide();
                }, throwable -> {
                    LogUtil.d(throwable.toString());
                    ToastUtil.show("解析错误，请重试");
                    finish();
                });
        bigBangLayout.setActionListener(bigBangActionListener);


    }

    BigBangLayout.ActionListener bigBangActionListener=new BigBangLayout.ActionListener() {

        @Override
        public void onSearch(String text) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.baidu.com/s?wd=" + URLEncoder.encode(text, "utf-8")));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onShare(String text) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(sharingIntent);
            finish();
        }

        @Override
        public void onCopy(String text) {
            if (!TextUtils.isEmpty(text)) {
                ClipboardUtils.setText(getApplicationContext(),text);
                ToastUtil.show("已复制");
                finish();
            }
        }

        @Override
        public void onTrans(String text) {
            if (!TextUtils.isEmpty(text)) {
//                loading.show();
                bigBangLayout.setVisibility(View.GONE);
                ViewStub viewStub= (ViewStub) findViewById(R.id.trans_view_stub);
                viewStub.inflate();
                EditText toTrans= (EditText) findViewById(R.id.to_translate);
                EditText transResult= (EditText) findViewById(R.id.translate_result);
                toTrans.setText(text);
                transResult.setText("正在翻译");
                RetrofitHelper.getTranslationService()
                        .getTranslationItem(text)
                        .compose(BigBangActivity.this.bindToLifecycle())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(recommendInfo -> {
                            List<String> transes=recommendInfo.getTranslation();
                            if (transes.size()>0){
                                String trans=transes.get(0);
                                transResult.setText(trans);
                            }
                            LogUtil.d(recommendInfo.toString());
                        }, throwable -> {
                            LogUtil.d(throwable.toString());
                        });
            }
        }
    };
}
