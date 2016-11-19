package com.forfan.bigbang.component.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.umeng.onlineconfig.OnlineConfigAgent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        OnlineConfigAgent.getInstance().updateOnlineConfig(getApplicationContext());
        CardView cardView=new CardView(this);
        View view= LayoutInflater.from(this).inflate(R.layout.activity_big_bang,null,false);
        cardView.setRadius(ViewUtil.dp2px(10));
        cardView.setCardBackgroundColor(getResources().getColor(R.color.bigbang_bg));
        cardView.addView(view);

        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.trans));
        setContentView(cardView);

        Intent intent=getIntent();
        String str=intent.getStringExtra(TO_SPLIT_STR);

        if (TextUtils.isEmpty(str)){
            String action = intent.getAction();
            String type = intent.getType();
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                    str=sharedText;
                }
            }
        }

        if (TextUtils.isEmpty(str)){
            finish();
            return;
        }
        remainSymbol= SPHelper.getBoolean(ConstantUtil.REMAIN_SYMBOL,true);



        int text=SPHelper.getInt(ConstantUtil.TEXT_SIZE,ConstantUtil.DEFAULT_TEXT_SIZE);
        int line=SPHelper.getInt(ConstantUtil.LINE_MARGIN,ConstantUtil.DEFAULT_LINE_MARGIN);
        int item=SPHelper.getInt(ConstantUtil.ITEM_MARGIN,ConstantUtil.DEFAULT_ITEM_MARGIN);


        bigBangLayout= (BigBangLayout) findViewById(R.id.bigbang);
        loading= (ContentLoadingProgressBar) findViewById(R.id.loading);

        loading.show();
        bigBangLayout.reset();

        bigBangLayout.setTextSize(text);
        bigBangLayout.setLineSpace(line);
        bigBangLayout.setItemSpace(item);


        if (!remainSymbol){
            str = str.replaceAll("[,\\./:\"\\\\\\[\\]\\|`~!@#\\$%\\^&\\*\\(\\)_\\+=<->\\?;'，。、；：‘’“”【】《》？\\{\\}！￥…（）—=]","");
        }
        String finalStr = str;
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
                    ToastUtil.show(R.string.no_internet_for_fenci);
                    List<String> txts= new ArrayList<String>();
                    for(int index = 0; index < finalStr.length() ; index++){
                        txts.add(finalStr.charAt(index)+"");
                    }
                    for (String t:txts) {
                        bigBangLayout.addTextItem(t);
                    }
                    loading.hide();
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
                try {
                    Uri uri=null;
//                    Pattern p = Pattern.compile("^(http|www|ftp|)?(://)?(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*((:\\d+)?)(/(\\w+(-\\w+)*))*(\\.?(\\w)*)(\\?)?(((\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*(\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*)*(\\w*)*)$", Pattern.CASE_INSENSITIVE );
                    Pattern p = Pattern.compile("^((https?|ftp|news):\\/\\/)?([a-z]([a-z0-9\\-]*[\\.。])+([a-z]{2}|aero|arpa|biz|com|coop|edu|gov|info|int|jobs|mil|museum|name|nato|net|org|pro|travel)|(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))(\\/[a-z0-9_\\-\\.~]+)*(\\/([a-z0-9_\\-\\.]*)(\\?[a-z0-9+_\\-\\.%=&]*)?)?(#[a-z][a-z0-9_]*)?$", Pattern.CASE_INSENSITIVE );
                    Matcher matcher=p.matcher(text);
                    boolean isUrl;
                    if (!matcher.matches()){
                        uri=Uri.parse("https://m.baidu.com/s?word=" + URLEncoder.encode(text, "utf-8"));
                        isUrl = false;
                    }else {
                        uri=Uri.parse(text);
                        if(!text.startsWith("http"))
                            text = "http://"+text;
                        isUrl = true;
                    }

                    boolean t=SPHelper.getBoolean(ConstantUtil.USE_LOCAL_WEBVIEW,true);
                    Intent intent;
                    if (t){
                        intent = new Intent();
                        if(isUrl){
                            intent.putExtra("url",text);
                        }else {
                            intent.putExtra("query",text);
                        }
                        intent.setClass(BigBangActivity.this,WebActivity.class);
                    }else {
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    startActivity(intent);
                    finish();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onShare(String text) {
            if (!TextUtils.isEmpty(text)) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_to)));
                finish();
            }
        }

        @Override
        public void onCopy(String text) {
            if (!TextUtils.isEmpty(text)) {
                Intent intent=new Intent(ConstantUtil.BROADCAST_SET_TO_CLIPBOARD);
                intent.putExtra(ConstantUtil.BROADCAST_SET_TO_CLIPBOARD_MSG,text);
                sendBroadcast(intent);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ClipboardUtils.setText(getApplicationContext(),text);
                        ToastUtil.show("已复制");
                        finish();
                    }
                }, 100);

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
