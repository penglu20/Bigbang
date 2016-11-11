package com.forfan.bigbang.component.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.component.contentProvider.SPHelper;
import com.forfan.bigbang.network.RetrofitHelper;
import com.forfan.bigbang.util.ClipboardUtils;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.LogUtil;
import com.forfan.bigbang.util.ToastUtil;
import com.forfan.bigbang.util.ViewUtil;
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
    private boolean remainSymbol=true;
    private EditText toTrans;
    private EditText transResult;
    private RelativeLayout transRl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CardView cardView=new CardView(this);
        View view= LayoutInflater.from(this).inflate(R.layout.activity_big_bang,null,false);
        cardView.setRadius(ViewUtil.dp2px(5));
        cardView.addView(view);

        setContentView(cardView);

        Intent intent=getIntent();
        String str=intent.getStringExtra(TO_SPLIT_STR);

        if (TextUtils.isEmpty(str)){
            finish();
            return;
        }

        remainSymbol= SPHelper.getBoolean(ConstantUtil.REMAIN_SYMBOL,true);

        bigBangLayout= (BigBangLayout) findViewById(R.id.bigbang);
        loading= (ContentLoadingProgressBar) findViewById(R.id.loading);

        loading.show();
        bigBangLayout.reset();

        if (!remainSymbol){
            str = str.replaceAll("[,\\./:\"\\\\\\[\\]\\|`~!@#\\$%\\^&\\*\\(\\)_\\+=<->\\?;'，。、；：‘’“”【】《》？\\{\\}！￥…（）—=]","");
        }
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
        public void onSelected(String text) {

        }

        @Override
        public void onSearch(String text) {
            if (!TextUtils.isEmpty(text)) {
//                try {
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.baidu.com/s?wd=" + URLEncoder.encode(text, "utf-8")));
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    finish();
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
                Intent intent = new Intent();
                try {
                    intent.putExtra("url","https://www.baidu.com/s?wd=" + URLEncoder.encode(text, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                intent.setClass(BigBangActivity.this,WebActivity.class);
                startActivity(intent);
            }
        }

        @Override
        public void onShare(String text) {
            if (!TextUtils.isEmpty(text)) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(sharingIntent);
                finish();
            }
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
                if (transRl==null){
                    ViewStub viewStub= (ViewStub) findViewById(R.id.trans_view_stub);
                    viewStub.inflate();
                    transRl= (RelativeLayout) findViewById(R.id.trans_rl);
                    toTrans= (EditText) findViewById(R.id.to_translate);
                    transResult= (EditText) findViewById(R.id.translate_result);
                    ImageView transAgain = (ImageView) findViewById(R.id.trans_again);
                    transAgain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ViewUtil.hideInputMethod(toTrans);
                            translate(toTrans.getText().toString());
                        }
                    });
                }
                translate(text);
            }
        }

        @Override
        public void onDrag() {

        }
    };

    private void translate(String text) {
        if (TextUtils.isEmpty(text)) {
            transResult.setText("");
            return;
        }
        bigBangLayout.setVisibility(View.GONE);
        transRl.setVisibility(View.VISIBLE);
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

    @Override
    public void onBackPressed() {
        if (bigBangLayout.getVisibility()==View.GONE){
            bigBangLayout.setVisibility(View.VISIBLE);
            transRl.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
        }
    }
}
