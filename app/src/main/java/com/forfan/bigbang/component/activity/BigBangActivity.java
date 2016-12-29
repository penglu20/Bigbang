package com.forfan.bigbang.component.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.network.RetrofitHelper;
import com.forfan.bigbang.onestep.AppsAdapter;
import com.forfan.bigbang.onestep.ResolveInfoWrap;
import com.forfan.bigbang.util.ClipboardUtils;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.LogUtil;
import com.forfan.bigbang.util.RegexUtil;
import com.forfan.bigbang.util.SearchEngineUtil;
import com.forfan.bigbang.util.SharedIntentHelper;
import com.forfan.bigbang.util.ToastUtil;
import com.forfan.bigbang.util.UrlCountUtil;
import com.forfan.bigbang.util.ViewUtil;
import com.forfan.bigbang.view.BigBangLayout;
import com.forfan.bigbang.view.BigBangLayoutWrapper;
import com.shang.commonjar.contentProvider.SPHelper;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by penglu on 2016/10/27.
 */

public class BigBangActivity extends BaseActivity {
    public static final String TO_SPLIT_STR = "to_split_str";
    private BigBangLayout bigBangLayout;
    private BigBangLayoutWrapper bigBangLayoutWrapper;
    private ContentLoadingProgressBar loading;
    private boolean remainSymbol = true;
    private EditText toTrans;
    private EditText transResult;
    private RelativeLayout transRl;
    private String originString;

    private List<String> netWordSegments;
    private static String lastString;


    int alpha;
    int lastPickedColor;
    private SwipeMenuRecyclerView mAppsRecyclerView;
    private View mAppsRecyclerViewLL;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        OnlineConfigAgent.getInstance().updateOnlineConfig(getApplicationContext());
        boolean fullScreen = SPHelper.getBoolean(ConstantUtil.IS_FULL_SCREEN, false);
        boolean stickHeader = SPHelper.getBoolean(ConstantUtil.IS_STICK_HEADER, false);
        alpha = SPHelper.getInt(ConstantUtil.BIGBANG_ALPHA, 100);
        lastPickedColor = SPHelper.getInt(ConstantUtil.BIGBANG_DIY_BG_COLOR, Color.parseColor("#94a4bb"));
        int value = (int) ((alpha / 100.0f) * 255);

        if (fullScreen) {
            setTheme(R.style.PreSettingTheme);
            setContentView(R.layout.activity_big_bang);
            getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bigbang_activity_window_full));
            getWindow().getDecorView().setBackgroundColor(Color.argb(value, Color.red(lastPickedColor), Color.green(lastPickedColor), Color.blue(lastPickedColor)));
            showAppList4OneStep();
        } else {
            CardView cardView = new CardView(this);
            View view = LayoutInflater.from(this).inflate(R.layout.activity_big_bang, null, false);
            cardView.setRadius(ViewUtil.dp2px(10));


            cardView.setCardBackgroundColor(Color.argb(value, Color.red(lastPickedColor), Color.green(lastPickedColor), Color.blue(lastPickedColor)));
            cardView.addView(view);

            getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.trans));
            setContentView(cardView);

        }


//        CardView cardView = new CardView(this);
//        View view = LayoutInflater.from(this).inflate(R.layout.activity_big_bang, null, false);
//        cardView.setRadius(ViewUtil.dp2px(10));
//
//        int value = (int) ((alpha / 100.0f) * 255);
//        cardView.setCardBackgroundColor(Color.argb(value, Color.red(lastPickedColor), Color.green(lastPickedColor), Color.blue(lastPickedColor)));
//        cardView.addView(view);
//
//        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.trans));
//        setContentView(cardView);

        Intent intent = getIntent();
        String str = intent.getStringExtra(TO_SPLIT_STR);

        if (TextUtils.isEmpty(str)) {
            String action = intent.getAction();
            String type = intent.getType();
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                    str = sharedText;
                }
            }
        }

        if (TextUtils.isEmpty(str)) {
            str = lastString;
        }

        if (TextUtils.isEmpty(str)) {
            finish();
            return;
        }

        lastString = str;
        mSelectText = lastString;

        str = str.replaceAll("@", " @ ");

        remainSymbol = SPHelper.getBoolean(ConstantUtil.REMAIN_SYMBOL, true);


        int text = SPHelper.getInt(ConstantUtil.TEXT_SIZE, ConstantUtil.DEFAULT_TEXT_SIZE);
        int line = SPHelper.getInt(ConstantUtil.LINE_MARGIN, ConstantUtil.DEFAULT_LINE_MARGIN);
        int item = SPHelper.getInt(ConstantUtil.ITEM_MARGIN, ConstantUtil.DEFAULT_ITEM_MARGIN);
        int padding = SPHelper.getInt(ConstantUtil.ITEM_PADDING, (int) ViewUtil.dp2px(ConstantUtil.DEFAULT_ITEM_PADDING));


        bigBangLayout = (BigBangLayout) findViewById(R.id.bigbang);
        loading = (ContentLoadingProgressBar) findViewById(R.id.loading);
        bigBangLayoutWrapper = (BigBangLayoutWrapper) findViewById(R.id.bigbang_wrap);


        loading.show();
        bigBangLayout.reset();
        bigBangLayoutWrapper.setVisibility(View.GONE);
        if (fullScreen) {
            bigBangLayoutWrapper.setFullScreenMode(true);
        }
        bigBangLayoutWrapper.setStickHeader(stickHeader);

        bigBangLayout.setTextSize(text);
        bigBangLayout.setLineSpace(line);
        bigBangLayout.setItemSpace(item);
        bigBangLayout.setTextPadding(padding);
//        bigBangLayoutWrapper.setBackgroundColorWithAlpha(lastPickedColor, alpha);


//        if (!remainSymbol){
//            str = str.replaceAll("[,\\./:\"\\\\\\[\\]\\|`~!@#\\$%\\^&\\*\\(\\)_\\+=<->\\?;'，。、；：‘’“”【】《》？\\{\\}！￥…（）—=]","");
//        }
        bigBangLayoutWrapper.setShowSymbol(remainSymbol);
        bigBangLayoutWrapper.setShowSection(SPHelper.getBoolean(ConstantUtil.REMAIN_SECTION, false));
        originString = str;
        String finalStr = str;
        bigBangLayoutWrapper.setActionListener(bigBangActionListener);
        bigBangLayoutWrapper.onSwitchType(SPHelper.getBoolean(ConstantUtil.DEFAULT_LOCAL, false));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        init();
    }

    private void showAppList4OneStep() {
        mAppsRecyclerView = (SwipeMenuRecyclerView) findViewById(R.id.app_list);
        mAppsRecyclerViewLL = findViewById(R.id.app_list_ll);
        if (SPHelper.getBoolean(ConstantUtil.IS_STICK_SHAREBAR, true)) {
            mAppsRecyclerViewLL.setVisibility(View.VISIBLE);
            mAppsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            List<ResolveInfoWrap> addedItems = SharedIntentHelper.listFilterIntents(this);
            mAppsRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
            mAppsRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
            AppsAdapter appsAdapter = new AppsAdapter(this);
            appsAdapter.setItems(addedItems);
            appsAdapter.setOnItemClickListener(new AppsAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(ResolveInfoWrap item) {
                    if (!TextUtils.isEmpty(mSelectText)) {
                        try {
                            SharedIntentHelper.share(BigBangActivity.this, item, mSelectText);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        ToastUtil.show("请选择文字");
                    }

                }
            });
            mAppsRecyclerView.setLongPressDragEnabled(true);// 开启拖拽，就这么简单一句话。
            mAppsRecyclerView.setOnItemMoveListener(new OnItemMoveListener() {
                @Override
                public boolean onItemMove(int fromPosition, int toPosition) {
                    // 当Item被拖拽的时候。
                    Collections.swap(addedItems, fromPosition, toPosition);
                    appsAdapter.notifyItemMoved(fromPosition,toPosition);
                    SharedIntentHelper.saveShareAppIndexs2Sp(addedItems,BigBangActivity.this);

                    return true;// 返回true表示处理了，返回false表示你没有处理。
                }

                @Override
                public void onItemDismiss(int position) {

                }
            });
            mAppsRecyclerView.setAdapter(appsAdapter);

        } else {
            mAppsRecyclerViewLL.setVisibility(View.GONE);
        }

    }

    private void getSegment(String str) {
        RetrofitHelper.getWordSegmentService()
                .getWordSegsList(str)
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(5000, TimeUnit.MILLISECONDS)
                .subscribe(recommendInfo -> {
                    LogUtil.d(recommendInfo.toString());
                    List<String> txts = recommendInfo.get(0).getWord();
                    netWordSegments = txts;

                    for (String t : txts) {
                        bigBangLayout.addTextItem(t);
                    }
                    loading.hide();
                    bigBangLayoutWrapper.setVisibility(View.VISIBLE);

                    if (!SPHelper.getBoolean(ConstantUtil.HAD_SHOW_LONG_PRESS_TOAST,false)){
                        ToastUtil.show(R.string.bb_long_press_toast);
                        SPHelper.save(ConstantUtil.HAD_SHOW_LONG_PRESS_TOAST,true);
                    }
                }, throwable -> {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.d(throwable.toString());
                            ToastUtil.show(R.string.no_internet_for_fenci);
//                    List<String> txts= new ArrayList<String>();
//                    for(int index = 0; index < finalStr.length() ; index++){
//                        txts.add(finalStr.charAt(index)+"");
//                    }
//                    for (String t:txts) {
//                        bigBangLayout.addTextItem(t);
//                    }
//                    loading.hide();
//                    bigBangLayoutWrapper.setBottomVibility(View.VISIBLE);
                            bigBangLayoutWrapper.onSwitchType(true);
                        }
                    });

                });
    }

    private String mSelectText;
    BigBangLayoutWrapper.ActionListener bigBangActionListener = new BigBangLayoutWrapper.ActionListener() {


        @Override
        public void onSelected(String text) {
            mSelectText = text;
        }

        @Override
        public void onSearch(String text) {
            if (TextUtils.isEmpty(text)) {
                text = originString;
            }
            UrlCountUtil.onEvent(UrlCountUtil.CLICK_BIGBANG_SEARCH);
            boolean isUrl = false;
            Uri uri = null;
            try {
//                    Pattern p = Pattern.compile("^(http|www|ftp|)?(://)?(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*((:\\d+)?)(/(\\w+(-\\w+)*))*(\\.?(\\w)*)(\\?)?(((\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*(\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*)*(\\w*)*)$", Pattern.CASE_INSENSITIVE );
                Pattern p = Pattern.compile("^((https?|ftp|news):\\/\\/)?([a-z]([a-z0-9\\-]*[\\.。])+([a-z]{2}|aero|arpa|biz|com|coop|edu|gov|info|int|jobs|mil|museum|name|nato|net|org|pro|travel)|(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))(\\/[a-z0-9_\\-\\.~]+)*(\\/([a-z0-9_\\-\\.]*)(\\?[a-z0-9+_\\-\\.%=&]*)?)?(#[a-z][a-z0-9_]*)?$", Pattern.CASE_INSENSITIVE);
                Matcher matcher = p.matcher(text);
                if (!matcher.matches()) {
                    uri = Uri.parse(SearchEngineUtil.getInstance().getSearchEngines().get(SPHelper.getInt(ConstantUtil.BROWSER_SELECTION,0)).url + URLEncoder.encode(text, "utf-8"));
                    isUrl = false;
                } else {
                    uri = Uri.parse(text);
                    if (!text.startsWith("http"))
                        text = "http://" + text;
                    isUrl = true;
                }

                boolean t = SPHelper.getBoolean(ConstantUtil.USE_LOCAL_WEBVIEW, true);
                Intent intent;
                if (t) {
                    intent = new Intent();
                    if (isUrl) {
                        intent.putExtra("url", text);
                    } else {
                        intent.putExtra("query", text);
                    }
                    intent.setClass(BigBangActivity.this, WebActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent();
                if (isUrl) {
                    intent.putExtra("url", text);
                } else {
                    intent.putExtra("query", text);
                }
                intent.setClass(BigBangActivity.this, WebActivity.class);
                startActivity(intent);
            }

        }

        @Override
        public void onShare(String text) {
            if (TextUtils.isEmpty(text)) {
                text = originString;
            }
            UrlCountUtil.onEvent(UrlCountUtil.CLICK_BIGBANG_SHARAE);

//                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//                sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                sharingIntent.setType("text/plain");
//                sharingIntent.putExtra(Intent.EXTRA_TEXT, text);
            SharedIntentHelper.sendShareIntent(BigBangActivity.this, text);
//                finish();
        }

        @Override
        public void onCopy(String text) {
            if (TextUtils.isEmpty(text)) {
                text = originString;
            }
            UrlCountUtil.onEvent(UrlCountUtil.CLICK_BIGBANG_COPY);

            Intent intent = new Intent(ConstantUtil.BROADCAST_SET_TO_CLIPBOARD);
            intent.putExtra(ConstantUtil.BROADCAST_SET_TO_CLIPBOARD_MSG, text);
            sendBroadcast(intent);
            String finalText = text;
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    ClipboardUtils.setText(getApplicationContext(), finalText);
                    ToastUtil.show("已复制");
                    finish();
                }
            }, 100);
        }

        @Override
        public void onTrans(String text) {
            if (mAppsRecyclerView != null)
                mAppsRecyclerViewLL.setVisibility(View.GONE);
            if (TextUtils.isEmpty(text)) {
                text = originString;
            }
            UrlCountUtil.onEvent(UrlCountUtil.CLICK_BIGBANG_TRANSLATE);

//                loading.show();
            if (transRl == null) {
                ViewStub viewStub = (ViewStub) findViewById(R.id.trans_view_stub);
                viewStub.inflate();
                transRl = (RelativeLayout) findViewById(R.id.trans_rl);
                toTrans = (EditText) findViewById(R.id.to_translate);
                transResult = (EditText) findViewById(R.id.translate_result);
                TextView title = (TextView) findViewById(R.id.title);
                findViewById(R.id.trans_again).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(toTrans.getText())) {
                            translate(toTrans.getText().toString());
                        }
                        ViewUtil.hideInputMethod(toTrans);
                    }
                });


            }
            translate(text);
        }

        @Override
        public void onDrag() {
            UrlCountUtil.onEvent(UrlCountUtil.CLICK_BIGBANG_DRAG);
        }

        @Override
        public void onSwitchType(boolean isLocal) {
            showSegment(isLocal);
        }

        @Override
        public void onSwitchSymbol(boolean isShow) {
            SPHelper.save(ConstantUtil.REMAIN_SYMBOL, isShow);
            UrlCountUtil.onEvent(UrlCountUtil.CLICK_BIGBANG_REMAIN_SYMBOL);
        }

        @Override
        public void onSwitchSection(boolean isShow) {
            SPHelper.save(ConstantUtil.REMAIN_SECTION, isShow);
            UrlCountUtil.onEvent(UrlCountUtil.CLICK_BIGBANG_REMAIN_SECTION);
        }

        @Override
        public void onDragSelection() {
            UrlCountUtil.onEvent(UrlCountUtil.CLICK_BIGBANG_DRAG_SELECTION);

        }
    };

    private void showSegment(boolean isLocal) {
        loading.show();
        UrlCountUtil.onEvent(UrlCountUtil.CLICK_BIGBANG_SWITCH_TYPE);
        bigBangLayout.reset();
        if (!isLocal) {
            if (netWordSegments == null) {
                getSegment(originString);
            } else {
                for (String t : netWordSegments) {
                    bigBangLayout.addTextItem(t);
                }
                loading.hide();
                bigBangLayoutWrapper.setVisibility(View.VISIBLE);
            }
        } else {
            List<String> txts = getLocalSegments(originString);
            for (String t : txts) {
                bigBangLayout.addTextItem(t);
            }
            loading.hide();
            bigBangLayoutWrapper.setVisibility(View.VISIBLE);
        }
    }

    private static final String DEVIDER="__DEVIDER___DEVIDER__";
    @NonNull
    private List<String> getLocalSegments(String str) {
        List<String> txts = new ArrayList<String>();
        String s = "";
        for (int i = 0; i < str.length(); i++) {
            char first = str.charAt(i);
            //当到达末尾的时候
            if (i + 1 >= str.length()) {
                s = s + first;
                break;
            }
            char next = str.charAt(i + 1);
            if ((RegexUtil.isChinese(first) && !RegexUtil.isChinese(next)) || (!RegexUtil.isChinese(first) && RegexUtil.isChinese(next)) ||
                    (Character.isLetter(first) && !Character.isLetter(next)) || (Character.isDigit(first) && !Character.isDigit(next))) {
                s = s + first + DEVIDER;
            } else if (RegexUtil.isSymbol(first)) {
                s = s + DEVIDER + first + DEVIDER;
            } else {
                s = s + first;
            }
        }
        str = s;
        str.replace("\n", DEVIDER+"\n"+DEVIDER);
        String[] texts = str.split(DEVIDER);
        for (String text : texts) {
            if (text.equals(DEVIDER))
                continue;
            //当首字母是英文字母时，默认该字符为英文
            if (RegexUtil.isEnglish(text)) {
                txts.add(text);
                continue;
            }
            if (RegexUtil.isNumber(text)) {
                txts.add(text);
                continue;
            }
            for (int i = 0; i < text.length(); i++) {
                txts.add(text.charAt(i) + "");
            }
        }
        return txts;
    }

    private void translate(String text) {
        if (TextUtils.isEmpty(text)) {
            transResult.setText("");
            return;
        }
        bigBangLayoutWrapper.setVisibility(View.GONE);
        transRl.setVisibility(View.VISIBLE);
        toTrans.setText(text);
        toTrans.setSelection(text.length());
        transResult.setText("正在翻译");
        RetrofitHelper.getTranslationService()
                .getTranslationItem(text.replaceAll("\n", ""))
                .compose(BigBangActivity.this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recommendInfo -> {
                    List<String> transes = recommendInfo.getTranslation();
                    if (transes.size() > 0) {
                        String trans = transes.get(0);
                        transResult.setText(trans);
                    }
                    LogUtil.d(recommendInfo.toString());
                }, throwable -> {
                    LogUtil.d(throwable.toString());
                });
    }

    @Override
    public void onBackPressed() {
        if (bigBangLayoutWrapper != null && bigBangLayoutWrapper.getVisibility() == View.GONE) {
            boolean stickSharebar = SPHelper.getBoolean(ConstantUtil.IS_STICK_SHAREBAR, false);
            if (mAppsRecyclerViewLL != null)
                mAppsRecyclerViewLL.setVisibility(stickSharebar ? View.VISIBLE : View.GONE);
            bigBangLayoutWrapper.setVisibility(View.VISIBLE);
            if (transRl != null) {
                transRl.setVisibility(View.GONE);
            }
        } else {
            super.onBackPressed();
        }
    }
}
