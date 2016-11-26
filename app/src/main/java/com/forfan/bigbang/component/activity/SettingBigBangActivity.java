package com.forfan.bigbang.component.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.ViewUtil;
import com.forfan.bigbang.view.BigBangLayout;
import com.forfan.bigbang.view.BigBangLayoutWrapper;
import com.shang.commonjar.contentProvider.SPHelper;
import com.shang.utils.StatusBarCompat;

/**
 * Created by penglu on 2016/11/9.
 */

public class SettingBigBangActivity extends BaseActivity {

    private static final int MIN_TEXT_SIZE = 8;
    private static final int MAX_TEXT_SIZE = 25;

    private static final int MIN_LINE_MARGIN = (int) ViewUtil.dp2px(0);
    private static final int MAX_LINE_MARGIN = (int) ViewUtil.dp2px(25);


    private static final int MIN_ITEM_MARGIN = (int) ViewUtil.dp2px(0);
    private static final int MAX_ITEM_MARGIN = (int) ViewUtil.dp2px(20);


    private BigBangLayoutWrapper mBigBangLayoutWrap;
    private BigBangLayout mBigBangLayout;
    private SeekBar mTextSizeSeekBar;
    private SeekBar mLineMarginSeekBar;
    private SeekBar mItemMarginSeekBar;

    private TextView textSize, lineMargin, itemMargin;
    private TextView bigbangAlpha;
    private SeekBar mBigbangAlphaSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_bigbang);

        StatusBarCompat.setupStatusBarView(this, (ViewGroup) getWindow().getDecorView(), true, R.color.colorPrimary);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.setting_bigbang);


        mBigBangLayout = (BigBangLayout) findViewById(R.id.bigbang);
        mBigBangLayoutWrap = (BigBangLayoutWrapper) findViewById(R.id.bigbang_wrap);

        mTextSizeSeekBar = (SeekBar) findViewById(R.id.set_text_size);
        mLineMarginSeekBar = (SeekBar) findViewById(R.id.set_line_margin);
        mItemMarginSeekBar = (SeekBar) findViewById(R.id.set_item_margin);
        mBigbangAlphaSeekBar = (SeekBar) findViewById(R.id.set_bigbang_alpha);

        textSize = (TextView) findViewById(R.id.text_size);
        lineMargin = (TextView) findViewById(R.id.line_margin);
        itemMargin = (TextView) findViewById(R.id.item_margin);
        bigbangAlpha = (TextView) findViewById(R.id.bigbang_alpha);


        mTextSizeSeekBar.setMax(MAX_TEXT_SIZE - MIN_TEXT_SIZE);
        mLineMarginSeekBar.setMax(MAX_LINE_MARGIN - MIN_LINE_MARGIN);
        mItemMarginSeekBar.setMax(MAX_ITEM_MARGIN - MIN_ITEM_MARGIN);
        mItemMarginSeekBar.setMax(100);


        mTextSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = (int) (MIN_TEXT_SIZE + progress);
                mBigBangLayout.setTextSize(value);
                textSize.setText(getString(R.string.setting_text_size) + value);
                SPHelper.save(ConstantUtil.TEXT_SIZE, value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mLineMarginSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = (int) (MIN_LINE_MARGIN + progress);
                mBigBangLayout.setLineSpace(value);
                lineMargin.setText(getString(R.string.setting_line_margin) + value);
                SPHelper.save(ConstantUtil.LINE_MARGIN, value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mItemMarginSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = (int) (MIN_ITEM_MARGIN + progress);
                mBigBangLayout.setItemSpace(value);
                itemMargin.setText(getString(R.string.setting_item_margin) + value);
                SPHelper.save(ConstantUtil.ITEM_MARGIN, value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mBigbangAlphaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = (int) (  progress);
                mBigBangLayoutWrap.setBackgroundColorAlpha(value);
                bigbangAlpha.setText(getString(R.string.setting_alpha_percent) + value +"%");
                SPHelper.save(ConstantUtil.BIGBANG_ALPHA, value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        int text = SPHelper.getInt(ConstantUtil.TEXT_SIZE, ConstantUtil.DEFAULT_TEXT_SIZE);
        int line = SPHelper.getInt(ConstantUtil.LINE_MARGIN, ConstantUtil.DEFAULT_LINE_MARGIN);
        int item = SPHelper.getInt(ConstantUtil.ITEM_MARGIN, ConstantUtil.DEFAULT_ITEM_MARGIN);
        int alpha = SPHelper.getInt(ConstantUtil.BIGBANG_ALPHA, 100);


        mTextSizeSeekBar.setProgress((int) ((MIN_TEXT_SIZE)));
        mLineMarginSeekBar.setProgress((int) ((MIN_LINE_MARGIN)));
        mItemMarginSeekBar.setProgress((int) ((MIN_ITEM_MARGIN)));

        mTextSizeSeekBar.setProgress((int) ((MAX_TEXT_SIZE)));
        mLineMarginSeekBar.setProgress((int) ((MAX_LINE_MARGIN)));
        mItemMarginSeekBar.setProgress((int) ((MAX_ITEM_MARGIN)));

        mTextSizeSeekBar.setProgress((int) ((text - MIN_TEXT_SIZE)));
        mLineMarginSeekBar.setProgress((int) ((line - MIN_LINE_MARGIN)));
        mItemMarginSeekBar.setProgress((int) ((item - MIN_ITEM_MARGIN)));

        bigbangAlpha.setText(getString(R.string.setting_alpha_percent) + alpha +"%");
        mBigbangAlphaSeekBar.setProgress(alpha);




        String[] txts = new String[]{"BigBang", "可以", "对", "文字", "进行", "编辑",
                "包括", "分词", "翻译", "复制", "以及", "动态", "调整"};

        for (String t : txts) {
            mBigBangLayout.addTextItem(t);
        }


    }


}
