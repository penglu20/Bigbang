package com.forfan.bigbang.component.activity.screen;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.activity.BigBangActivity;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.util.ColorUtil;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.LogUtil;
import com.forfan.bigbang.util.OcrAnalsyser;
import com.forfan.bigbang.util.ToastUtil;
import com.forfan.bigbang.util.UrlCountUtil;
import com.forfan.bigbang.util.ViewUtil;
import com.forfan.bigbang.view.DialogFragment;
import com.forfan.bigbang.view.SimpleDialog;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.shang.commonjar.contentProvider.SPHelper;
import com.umeng.onlineconfig.OnlineConfigAgent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by penglu on 2016/10/27.
 */

public class CaptureResultActivity extends BaseActivity {
    private ImageView capturedImage;
    private Bitmap bitmap;

    private TextView share, save, ocr, bigbang;
    private TextView ocrResult;
    int alpha = SPHelper.getInt(ConstantUtil.BIGBANG_ALPHA, 100);
    int lastPickedColor = SPHelper.getInt(ConstantUtil.BIGBANG_DIY_BG_COLOR, Color.parseColor("#000000"));

    private void initWindow() {
        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(localDisplayMetrics);
        localLayoutParams.width = ((int) (localDisplayMetrics.widthPixels * 0.99D));
        localLayoutParams.gravity = 17;
        localLayoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(localLayoutParams);
        getWindow().setGravity(17);
        getWindow().getAttributes().windowAnimations = R.anim.anim_scale_in;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnlineConfigAgent.getInstance().updateOnlineConfig(getApplicationContext());
        alpha = SPHelper.getInt(ConstantUtil.BIGBANG_ALPHA, 100);
        lastPickedColor = SPHelper.getInt(ConstantUtil.BIGBANG_DIY_BG_COLOR, Color.parseColor("#000000"));

        CardView cardView = new CardView(this);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_capture_result, null, false);
        cardView.setRadius(ViewUtil.dp2px(10));

        int value = (int) ((alpha / 100.0f) * 255);
        cardView.setCardBackgroundColor(Color.argb(value, Color.red(lastPickedColor), Color.green(lastPickedColor), Color.blue(lastPickedColor)));
        cardView.addView(view);

        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.trans));
        setContentView(cardView);
        initWindow();

        Intent intent = getIntent();
        String fileName = intent.getStringExtra(ScreenCaptureService.FILE_NAME);
        if (fileName == null) {
            ToastUtil.show(R.string.screen_capture_fail);
            finish();
            return;
        }
        LogUtil.e("CaptureResultActivity", fileName);
        File capturedFile = new File(fileName);
        if (capturedFile.exists()) {
            bitmap = BitmapFactory.decodeFile(fileName);
        } else {
            ToastUtil.show(R.string.screen_capture_fail);
            finish();
            return;
        }
        ocrResult = (TextView) findViewById(R.id.ocr_result);
        capturedImage = (ImageView) findViewById(R.id.captured_pic);
        share = (TextView) findViewById(R.id.share);
        save = (TextView) findViewById(R.id.save);
        ocr = (TextView) findViewById(R.id.recognize);
        bigbang = (TextView) findViewById(R.id.bigbang);

        ocrResult.setVisibility(View.GONE);

        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(localDisplayMetrics);
        if (bitmap.getHeight() > localDisplayMetrics.heightPixels * 2 / 3 || 1.0 * bitmap.getHeight() / bitmap.getWidth() >= 1.2) {
            LinearLayout container = (LinearLayout) findViewById(R.id.container);
            container.setOrientation(LinearLayout.HORIZONTAL);

            capturedImage.setMaxWidth(localDisplayMetrics.widthPixels / 2);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) capturedImage.getLayoutParams();
            if (bitmap.getWidth() > localDisplayMetrics.widthPixels / 2) {
                layoutParams.width = bitmap.getWidth() * 2 / 5;
                layoutParams.height = bitmap.getHeight() * 2 / 5;
            } else {
                layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            }
            capturedImage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) ocrResult.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            ocrResult.setLayoutParams(layoutParams);

        }


        capturedImage.setImageBitmap(bitmap);

        save.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            UrlCountUtil.onEvent(UrlCountUtil.CLICK_CAPTURERESULT_SAVE);
                                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                                            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/", format.format(new Date()) + ".jpg");
                                            file.getParentFile().mkdirs();
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
                                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                            Uri uri = Uri.fromFile(file);
                                            intent.setData(uri);
                                            sendBroadcast(intent);
                                            ToastUtil.show(getResources().getString(R.string.save_sd_card));
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                            ToastUtil.show(R.string.save_sd_card_fail);
                                        }

                                    }
                                }
        );

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    UrlCountUtil.onEvent(UrlCountUtil.CLICK_CAPTURERESULT_SHARE);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/", format.format(new Date()) + ".jpg");
                    file.getParentFile().mkdirs();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
                    shareMsg("分享给", "截图", "来自bigbang的截图", file.getAbsolutePath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        ocr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SPHelper.getInt(ConstantUtil.OCR_TIME, 0) == ConstantUtil.OCR_TIME_TO_ALERT) {
                    showBeyondQuoteDialog();
                    int time = SPHelper.getInt(ConstantUtil.OCR_TIME, 0) + 1;
                    SPHelper.save(ConstantUtil.OCR_TIME, time);
                    return;
                }
                ToastUtil.show(R.string.ocr_recognize);
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_CAPTURERESULT_OCR);
                OcrAnalsyser.getInstance().analyse(CaptureResultActivity.this, fileName, true, new OcrAnalsyser.CallBack() {
                    @Override
                    public void onSucess(OCR ocr) {
                        ocrResult.setVisibility(View.VISIBLE);
                        ocrResult.setText(OcrAnalsyser.getInstance().getPasedMiscSoftText(ocr));
                        ocrResult.setTextColor(ColorUtil.getPropertyTextColor(lastPickedColor,alpha));
                    }

                    @Override
                    public void onFail(Throwable throwable) {
                        if (SPHelper.getString(ConstantUtil.DIY_OCR_KEY,"").equals("")) {
                            ToastUtil.show(getResources().getString(R.string.ocr_useup_toast));
                        }else {
                            ToastUtil.show(throwable.getMessage());
                        }
                    }
                });
            }
        });

        bigbang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SPHelper.getInt(ConstantUtil.OCR_TIME, 0) == ConstantUtil.OCR_TIME_TO_ALERT) {
                    showBeyondQuoteDialog();
                    int time = SPHelper.getInt(ConstantUtil.OCR_TIME, 0) + 1;
                    SPHelper.save(ConstantUtil.OCR_TIME, time);
                    return;
                }
                ToastUtil.show(R.string.ocr_recognize);
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_CAPTURERESULT_BIGBANG);
                OcrAnalsyser.getInstance().analyse(CaptureResultActivity.this, fileName, true, new OcrAnalsyser.CallBack() {
                    @Override
                    public void onSucess(OCR ocr) {
                        if (!TextUtils.isEmpty(ocrResult.getText())) {
                            Intent intent = new Intent(CaptureResultActivity.this, BigBangActivity.class);
                            intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(BigBangActivity.TO_SPLIT_STR, ocrResult.getText());
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(CaptureResultActivity.this, BigBangActivity.class);
                            intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(BigBangActivity.TO_SPLIT_STR, OcrAnalsyser.getInstance().getPasedMiscSoftText(ocr));
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFail(Throwable throwable) {

                        if (SPHelper.getString(ConstantUtil.DIY_OCR_KEY,"").equals("")) {
                            ToastUtil.show(getResources().getString(R.string.ocr_useup_toast));
                        }else {
                            ToastUtil.show(throwable.getMessage());
                        }
                    }
                });
            }

        });

        ocrResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_CAPTURERESULT_OCRRESULT);
                if (!TextUtils.isEmpty(ocrResult.getText())) {
                    Intent intent = new Intent(CaptureResultActivity.this, BigBangActivity.class);
                    intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(BigBangActivity.TO_SPLIT_STR, ocrResult.getText());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void showBeyondQuoteDialog() {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                // 这里是保持开启
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_SHOW_BEYOND_QUOTE);
                super.onPositiveActionClicked(fragment);
                Intent intent = new Intent();
                intent.setClass(CaptureResultActivity.this,DiyOcrKeyActivity.class);
                startActivity(intent);

            }

            @Override
            public void onDismiss(DialogInterface dialog) {
                super.onCancel(dialog);
            }
        };
        builder.message(this.getString(R.string.ocr_quote_beyond_time))
                .positiveAction(this.getString(R.string.free_use));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getSupportFragmentManager(), null);
    }


    /**
     * 分享功能
     *
     * @param context       上下文
     * @param activityTitle Activity的名字
     * @param msgTitle      消息标题
     * @param msgText       消息内容
     * @param imgPath       图片路径，不分享图片则传null
     */
    public void shareMsg(String activityTitle, String msgTitle, String msgText,
                         String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/jpg");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, activityTitle));
    }

    @Override
    public void onBackPressed() {
//        if (bigBangLayoutWrapper.getVisibility() == View.GONE) {
//            bigBangLayoutWrapper.setVisibility(View.VISIBLE);
//            transRl.setVisibility(View.GONE);
//        } else {
        super.onBackPressed();
//        }
    }

}
