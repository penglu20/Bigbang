package com.forfan.bigbang.component.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.activity.searchengine.SearchEngineActivity;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.LogUtil;
import com.forfan.bigbang.util.SearchEngineUtil;
import com.forfan.bigbang.util.UrlCountUtil;
import com.forfan.bigbang.util.ViewUtil;
import com.shang.commonjar.contentProvider.SPHelper;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;


public class WebActivity
        extends BaseActivity {
    private static final java.lang.String TAG = "webActivity";
    private LinearLayout mContentLayout;
    private ObjectAnimator mEnterAnim;
    private FrameLayout mFrameLayout;
    private ContentLoadingProgressBar mProgressBar;
    private AppCompatSpinner mTitleSpinner;
    private String mUrl;
    private WebView mWebView;
    private int browserSelection;
    private String mQuery;

    private void initAnim() {
        this.mEnterAnim = ObjectAnimator.ofFloat(this.mFrameLayout, "_enter", new float[]{0.0F, 1.0F}).setDuration(250L);
        this.mEnterAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator) {
                float f = ((Float) paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
                WebActivity.this.mFrameLayout.setScaleX(f);
                WebActivity.this.mFrameLayout.setScaleY(f);
                if (f == 0.0F) {
                    WebActivity.this.mFrameLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    private  boolean isFistIn = true;
    private void initViews() {
        refreshSpinner();

        browserSelection = SPHelper.getInt(ConstantUtil.BROWSER_SELECTION, 0);
        this.mFrameLayout = ((FrameLayout) findViewById(android.R.id.content));
        this.mContentLayout = ((LinearLayout) findViewById(R.id.content_view));
        mContentLayout.removeAllViews();
        this.mWebView = new WebView(this);
        this.mContentLayout.addView(this.mWebView, -1, -1);
        this.mProgressBar = ((ContentLoadingProgressBar) findViewById(R.id.progress));
        mProgressBar.onAttachedToWindow();
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.setBackgroundColor(-1);
        findViewById(R.id.finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_BROWSER_EXIT);
                finish();
            }
        });
        findViewById(R.id.open_chrome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_BROWSER_TO_SYS_BROWSER);
                Uri uri = getUri();
                if (uri != null) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) this.mWebView.getLayoutParams();
        int i = (int) ViewUtil.dp2px(2.0F);
        localLayoutParams.setMargins(i, 0, i, i);
        this.mWebView.setLayoutParams(localLayoutParams);
        this.mWebView.setWebViewClient(new WebViewClient() {
            public void onFormResubmission(WebView paramAnonymousWebView, Message paramAnonymousMessage1, Message paramAnonymousMessage2) {
                paramAnonymousMessage2.sendToTarget();
            }

            public boolean shouldOverrideUrlLoading(WebView paramAnonymousWebView, String paramAnonymousString) {
                mUrl = paramAnonymousWebView.getUrl();
                return super.shouldOverrideUrlLoading(paramAnonymousWebView, paramAnonymousString);

            }
        });
        this.mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView paramAnonymousWebView, int paramAnonymousInt) {
                if (paramAnonymousInt == 100) {
                    WebActivity.this.mProgressBar.hide();
                    return;
                }
                WebActivity.this.mProgressBar.setProgress(paramAnonymousInt);
                WebActivity.this.mProgressBar.show();
            }
        });
        this.mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
    }

    @NonNull
    private void refreshSpinner() {
        ArrayList<String> engines = SearchEngineUtil.getInstance().getSearchEngineNames();
        engines.add(getResources().getString(R.string.setting_search_engine_web));
        this.mTitleSpinner = ((AppCompatSpinner) findViewById(R.id.title));
        mTitleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==engines.size()-1){
                    UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_SEARCH_ENGINE_WEB);
                    Intent intent = new Intent(WebActivity.this, SearchEngineActivity.class);
                    startActivity(intent);
                }else {
                    UrlCountUtil.onEvent(UrlCountUtil.STATE_BROWSER_ENGINES, engines.get(position));
//                SPHelper.save(ConstantUtil.BROWSER_SELECTION, position);
                    if (isFistIn) {
                        isFistIn = false;
                        return;
                    }
                    browserSelection=position;
                    toLoadUrl("", mQuery);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                LogUtil.d(TAG, "onNothingSelected:");

            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.simple_spinner_item,engines);
        mTitleSpinner.setAdapter(adapter);
        mTitleSpinner.setSelection(browserSelection);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        return ;
    }

    private Uri getUri() {
        if (!TextUtils.isEmpty(mUrl)) {
            return Uri.parse(mUrl);
        } else {
            if (!TextUtils.isEmpty(mQuery))
                return Uri.parse(getUrlStrBySelect(mQuery));
        }
        return null;
    }

    /**
     *
     */
    private void toLoadUrl(String url, String query) {
        if (!TextUtils.isEmpty(url)) {
            mWebView.loadUrl(url);
        } else {
            String url_ = getUrlStrBySelect(query);
            mWebView.loadUrl(url_);
        }

    }


    private String getUrlStrBySelect(String query) {
        query = query.replaceAll("\n","");
        String url = SearchEngineUtil.getInstance().getSearchEngines().get(browserSelection).url;
        if(!url.startsWith("http"))
            url = "http://"+url;
        try {
            return url + URLEncoder.encode(query, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        }
        return url + query;
    }


    private void initWindow() {
        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(localDisplayMetrics);
        localLayoutParams.width = ((int) (localDisplayMetrics.widthPixels * 0.9D));
        localLayoutParams.gravity = 17;
        localLayoutParams.height = ((int) (localDisplayMetrics.heightPixels * 0.8D));
        getWindow().setAttributes(localLayoutParams);
        getWindow().setGravity(17);
        getWindow().getAttributes().windowAnimations = R.anim.anim_scale_in;
    }

    private void setConfigCallback(WindowManager paramWindowManager) {
        try {
            Field localField = WebView.class.getDeclaredField("mWebViewCore").getType().getDeclaredField("mBrowserFrame").getType().getDeclaredField("sConfigCallback");
            localField.setAccessible(true);
            Object localObject = localField.get(null);
            if (localObject == null) {
                return;
            }
            localField = localField.getType().getDeclaredField("mWindowManager");
            localField.setAccessible(true);
            localField.set(localObject, paramWindowManager);
            return;
        } catch (Exception ex) {
        }
    }


    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setFinishOnTouchOutside(true);
        setContentView(R.layout.activity_web);
        this.mUrl = getIntent().getStringExtra("url");
        this.mQuery = getIntent().getStringExtra("query");
        initWindow();
        initViews();
        initAnim();
        this.mEnterAnim.start();

        toLoadUrl(mUrl, mQuery);
        setConfigCallback((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshSpinner();
    }

    protected void onDestroy() {
        setConfigCallback(null);
        super.onDestroy();
        if (this.mWebView != null) {
            ((ViewGroup) this.mWebView.getParent()).removeView(this.mWebView);
            this.mWebView.removeAllViews();
            this.mWebView.destroy();
            this.mWebView = null;
        }
        if (mProgressBar != null) {
            mProgressBar.onDetachedFromWindow();
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        if (mWebView.isFocused() && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
            finish();
        }
    }
}