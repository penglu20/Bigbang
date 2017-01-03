package com.forfan.bigbang.component.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.forfan.bigbang.BigBangApp;
import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.DividerItemDecoration;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.cropper.CropFileUtils;
import com.forfan.bigbang.cropper.CropHelper;
import com.forfan.bigbang.cropper.handler.CropImage;
import com.forfan.bigbang.cropper.handler.CropImageView;
import com.forfan.bigbang.util.ArcTipViewController;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.IOUtil;
import com.forfan.bigbang.util.UrlCountUtil;
import com.forfan.bigbang.util.ViewUtil;
import com.shang.commonjar.contentProvider.SPHelper;
import com.shang.utils.StatusBarCompat;

import java.io.File;

/**
 * Created by penglu on 2016/11/9.
 */

public class SettingFloatViewActivity extends BaseActivity {
    private static final int BIGBANG_BACKGROUND_COLOR_ARRAY_RES = R.array.bigbang_background_color;


    private static final int MIN_ITEM_PADDING = 50;
    private static final int MAX_ITEM_PADDING = 150;
    private static final int ALPHA_MIN = 20;
    private static final int ALPHA_MAX = 100;


    private SeekBar mItemPaddingSeekBar;

    private TextView  itemPadding;
    private TextView bigbangAlpha;
    private SeekBar mBigbangAlphaSeekBar;

    private View delPic;

    private RecyclerView backgroundRV;
    private int[] bigbangBackgroungColors;
    private int lastPickedColor;//只存rgb
    private int alpha;//只存alpha，0-100
    public static String FLOATVIEW_IMAGE_PATH = BigBangApp.getInstance().getFilesDir()+ File.separator + "floatview.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_floatview);

        StatusBarCompat.setupStatusBarView(this, (ViewGroup) getWindow().getDecorView(), true, R.color.colorPrimary);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.setting_floatview);


        mItemPaddingSeekBar = (SeekBar) findViewById(R.id.set_item_padding);
        mBigbangAlphaSeekBar = (SeekBar) findViewById(R.id.set_bigbang_alpha);

        itemPadding = (TextView) findViewById(R.id.item_padding);
        bigbangAlpha = (TextView) findViewById(R.id.bigbang_alpha);

        backgroundRV = (RecyclerView) findViewById(R.id.bigbang_background);


        mItemPaddingSeekBar.setMax(MAX_ITEM_PADDING - MIN_ITEM_PADDING);
        mBigbangAlphaSeekBar.setMax(ALPHA_MAX -ALPHA_MIN);

        mItemPaddingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = (int) (MIN_ITEM_PADDING + progress);
                //mBigBangLayout.setTextPadding(value);
                itemPadding.setText(getString(R.string.setting_floatview_size) + value);
                SPHelper.save(ConstantUtil.FLOATVIEW_SIZE, (float)value);
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_FLOATVIEW_SET_SIZE, value + "");
                ArcTipViewController.getInstance().showForSettings();
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
                int value = progress + ALPHA_MIN ;
//                mBigBangLayoutWrap.setBackgroundColorWithAlpha(lastPickedColor,value);
//                cardView.setCardBackgroundColor(Color.argb((int) ((alpha / 100.0f) * 255), Color.red(lastPickedColor), Color.green(lastPickedColor), Color.blue(lastPickedColor)));
                bigbangAlpha.setText(getString(R.string.setting_floatview_alpha_percent) + value + "%");
                SPHelper.save(ConstantUtil.FLOATVIEW_ALPHA, value);
                alpha = value;
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_FLOATVIEW_SET_ALPHA, value + "");
                ArcTipViewController.getInstance().showForSettings();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        delPic=findViewById(R.id.del_pic);
        delPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IOUtil.delete(FLOATVIEW_IMAGE_PATH);
                ArcTipViewController.getInstance().showForSettings();
                refresh();
            }
        });



        findViewById(R.id.select_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, CropHelper.REQUEST_CROP);
            }
        });


        int padding = (int) SPHelper.getFloat(ConstantUtil.FLOATVIEW_SIZE, 100.0f);
        alpha = SPHelper.getInt(ConstantUtil.FLOATVIEW_ALPHA, 70);
        lastPickedColor = SPHelper.getInt(ConstantUtil.FLOATVIEW_DIY_BG_COLOR, Color.parseColor("#94a4bb"));


        mItemPaddingSeekBar.setProgress((int) ((MIN_ITEM_PADDING)));
        mItemPaddingSeekBar.setProgress((int) ((MAX_ITEM_PADDING- MIN_ITEM_PADDING)));
        mItemPaddingSeekBar.setProgress((int) ((padding - MIN_ITEM_PADDING)));


        bigbangAlpha.setText(getString(R.string.setting_floatview_alpha_percent) + alpha + "%");
        mBigbangAlphaSeekBar.setProgress(alpha-ALPHA_MIN);


        applyColor(lastPickedColor);

        bigbangBackgroungColors = getResources().getIntArray(BIGBANG_BACKGROUND_COLOR_ARRAY_RES);
        backgroundRV.setLayoutManager(new GridLayoutManager(this, 4));
        backgroundRV.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.GRID_LIST));
        backgroundRV.setAdapter(backgroundColorAdapter);


        refresh();
    }

    private RecyclerView.Adapter backgroundColorAdapter = new RecyclerView.Adapter() {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ColorVIewHolder(new TextView(SettingFloatViewActivity.this));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TextView view = (TextView) holder.itemView;
            view.setMinimumHeight((int) ViewUtil.dp2px(40));
            if (position == bigbangBackgroungColors.length) {
                view.setBackgroundColor(getResources().getColor(R.color.white));
                view.setText(R.string.set_background_myself);
                view.setTextColor(getResources().getColor(R.color.black));
                view.setTextSize(14);
                view.setGravity(Gravity.CENTER);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showColorPickDialog();
                        UrlCountUtil.onEvent(UrlCountUtil.CLICK_SET_BB_BGCOLOR_DIY);
                    }
                });
            } else {
                view.setText("");
                view.setBackgroundColor(bigbangBackgroungColors[position]);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (bigbangBackgroungColors.length > position) {
                            applyColor(bigbangBackgroungColors[position]);
                            lastPickedColor = bigbangBackgroungColors[position];
                            SPHelper.save(ConstantUtil.FLOATVIEW_DIY_BG_COLOR, bigbangBackgroungColors[position]);
                            UrlCountUtil.onEvent(UrlCountUtil.STATUS_SET_FLOATVIEW_BGCOLOR, lastPickedColor + "");
                            ArcTipViewController.getInstance().showForSettings();
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return bigbangBackgroungColors.length + 1;
        }

        class ColorVIewHolder extends RecyclerView.ViewHolder {
            public ColorVIewHolder(View itemView) {
                super(itemView);
            }
        }

    };

    private void applyColor(int color) {
    }


    private void applyColor(int color, int alpha) {
    }

    private void showColorPickDialog() {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle(R.string.set_background_myself)
                .initialColor(lastPickedColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        applyColor(Color.rgb(Color.red(selectedColor), Color.green(selectedColor), Color.blue(selectedColor)), (int) (Color.alpha(selectedColor) * 100.0 / 255));
                    }
                })
                .setPositiveButton(R.string.confirm, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        lastPickedColor = Color.rgb(Color.red(selectedColor), Color.green(selectedColor), Color.blue(selectedColor));
                        alpha = (int) (Color.alpha(selectedColor) * 100.0 / 255);
                        applyColor(Color.rgb(Color.red(selectedColor), Color.green(selectedColor), Color.blue(selectedColor)));
//                        SPHelper.save(ConstantUtil.BIGBANG_DIY_BG_COLOR, lastPickedColor);
//                        UrlCountUtil.onEvent(UrlCountUtil.STATUS_SET_BB_BGCOLOR, lastPickedColor + "");
                        SPHelper.save(ConstantUtil.FLOATVIEW_DIY_BG_COLOR, lastPickedColor);
                        UrlCountUtil.onEvent(UrlCountUtil.STATUS_SET_FLOATVIEW_BGCOLOR, lastPickedColor + "");
                        mBigbangAlphaSeekBar.setProgress(alpha);
                        ArcTipViewController.getInstance().showForSettings();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        applyColor(lastPickedColor);
                    }
                })
                .showColorEdit(true)
                .setColorEditTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_bright))
                .build()
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == -1) {
            CropImage.ActivityResult result = data.getExtras().getParcelable(CropImage.CROP_IMAGE_EXTRA_RESULT);
            CropFileUtils.copyRoundImageFile(result.getUri().getPath(),FLOATVIEW_IMAGE_PATH);
            ArcTipViewController.getInstance().showForSettings();
            refresh();
        } else if (requestCode == CropHelper.REQUEST_CROP && resultCode == -1) {
            if (data.getData() != null) {
                CropImage.activity(data.getData())
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setFixAspectRatio(true)
                        .setMultiTouchEnabled(true)
                        .start(SettingFloatViewActivity.this);
            }
            // CropHelper.handleResult(this, requestCode, resultCode, data);
        }
    }

    private void refresh(){
        if (new File(FLOATVIEW_IMAGE_PATH).exists()){
            delPic.setVisibility(View.VISIBLE);
        }else {
            delPic.setVisibility(View.GONE);
        }
    }
}
