package com.forfan.bigbang.component.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.util.DensityUtils;

import java.lang.reflect.Field;

import static android.webkit.WebSettings.LOAD_NO_CACHE;


public class WebActivity
        extends BaseActivity {
    private LinearLayout mContentLayout;
    private ObjectAnimator mEnterAnim;
    private FrameLayout mFrameLayout;
    private ContentLoadingProgressBar mProgressBar;
    private TextView mTitle;
    private String mUrl;
    private WebView mWebView;

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

    private void initViews() {
        this.mTitle = ((TextView) findViewById(R.id.title));
        this.mFrameLayout = ((FrameLayout) findViewById(android.R.id.content));
        this.mContentLayout = ((LinearLayout) findViewById(R.id.content_view));
        this.mWebView = new WebView(this);
        this.mContentLayout.addView(this.mWebView, -1, -1);
        this.mProgressBar = ((ContentLoadingProgressBar) findViewById(R.id.progress));
        mProgressBar.onAttachedToWindow();
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.setBackgroundColor(-1);
        findViewById(R.id.finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.open_chrome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) this.mWebView.getLayoutParams();
        int i = DensityUtils.dp2px(this, 2.0F);
        localLayoutParams.setMargins(i, 0, i, i);
        this.mWebView.setLayoutParams(localLayoutParams);
        this.mWebView.setWebViewClient(new WebViewClient() {
            public void onFormResubmission(WebView paramAnonymousWebView, Message paramAnonymousMessage1, Message paramAnonymousMessage2) {
                paramAnonymousMessage2.sendToTarget();
            }

            public boolean shouldOverrideUrlLoading(WebView paramAnonymousWebView, String paramAnonymousString) {
                paramAnonymousWebView.loadUrl(paramAnonymousString);
                mUrl = paramAnonymousWebView.getUrl();
                return true;
            }
        });
        this.mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView paramAnonymousWebView, int paramAnonymousInt) {
                if (paramAnonymousInt == 100) {
                    WebActivity.this.mProgressBar.hide();
                    WebActivity.this.mTitle.setText(WebActivity.this.mWebView.getTitle());
                    return;
                }
                WebActivity.this.mProgressBar.setProgress(paramAnonymousInt);
                WebActivity.this.mProgressBar.show();
                WebActivity.this.mTitle.setText("加载中...");
            }
        });
        this.mWebView.getSettings().setCacheMode(LOAD_NO_CACHE);
    }


    private void initWindow() {
        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(localDisplayMetrics);
        localLayoutParams.width = ((int) (localDisplayMetrics.widthPixels * 0.99D));
        localLayoutParams.gravity = 17;
        localLayoutParams.height = ((int) (localDisplayMetrics.widthPixels * 1.1D));
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
        if (this.mUrl == null) {
            finish();
        }
        initWindow();
        initViews();
        initAnim();
        this.mEnterAnim.start();
        this.mWebView.loadUrl(this.mUrl);
        setConfigCallback((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
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
        if(mProgressBar != null){
            mProgressBar.onDetachedFromWindow();
        }
        finish();
    }

    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
        if (paramInt == 4) {
            if (this.mWebView.canGoBack()) {
                this.mWebView.goBack();
                return true;
            }
            finish();
            return true;
        }
        return super.onKeyDown(paramInt, paramKeyEvent);
    }
}