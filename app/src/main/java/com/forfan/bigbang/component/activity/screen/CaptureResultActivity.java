package com.forfan.bigbang.component.activity.screen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.activity.BigBangActivity;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.LogUtil;
import com.forfan.bigbang.util.OcrAnalsyser;
import com.forfan.bigbang.util.ToastUtil;
import com.forfan.bigbang.util.ViewUtil;
import com.forfan.bigbang.view.BigBangLayout;
import com.forfan.bigbang.view.BigBangLayoutWrapper;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.shang.commonjar.contentProvider.SPHelper;
import com.umeng.onlineconfig.OnlineConfigAgent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by penglu on 2016/10/27.
 */

public class CaptureResultActivity extends BaseActivity {
    private ImageView capturedImage;
    private Bitmap bitmap;

    private ImageView share,save,ocr,bigbang;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnlineConfigAgent.getInstance().updateOnlineConfig(getApplicationContext());
        int alpha = SPHelper.getInt(ConstantUtil.BIGBANG_ALPHA, 100);

        CardView cardView = new CardView(this);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_capture_result, null, false);
        cardView.setRadius(ViewUtil.dp2px(10));

        int value = (int) ((alpha / 100.0f) * 255);
        cardView.setCardBackgroundColor(Color.argb(value, 00, 00, 00));
        cardView.addView(view);

        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.trans));
        setContentView(cardView);

        Intent intent = getIntent();
        String fileName = intent.getStringExtra(ScreenCaptureService.FILE_NAME);
        if (fileName==null){
            ToastUtil.show(R.string.screen_capture_fail);
            finish();
            return;
        }
        LogUtil.e("CaptureResultActivity",fileName);
        File capturedFile=new File(fileName );
        if (capturedFile.exists()) {
            bitmap= BitmapFactory.decodeFile(fileName);
        }else {
            ToastUtil.show(R.string.screen_capture_fail);
            finish();
            return;
        }

        capturedImage= (ImageView) findViewById(R.id.captured_pic);
        share= (ImageView) findViewById(R.id.share);
        save= (ImageView) findViewById(R.id.save);
        ocr= (ImageView) findViewById(R.id.recognize);
        bigbang = (ImageView) findViewById(R.id.bigbang);


        capturedImage.setImageBitmap(bitmap);

        save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/picture",format.format(new Date())+".jpg");
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100,new FileOutputStream(file));
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = Uri.fromFile(file);
                        intent.setData(uri);
                        sendBroadcast(intent);
//                        ToastUtil.show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
//                        ToastUtil.show();
                    }

                }
            }
        );

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ocr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OcrAnalsyser.getInstance().analyse(CaptureResultActivity.this, fileName, true, new OcrAnalsyser.CallBack() {
                    @Override
                    public void onSucess(OCR ocr) {
                        Intent intent = new Intent(CaptureResultActivity.this, BigBangActivity.class);
                        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(BigBangActivity.TO_SPLIT_STR, OcrAnalsyser.getInstance().getPasedMiscSoftText(ocr));
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFail() {
//                        ToastUtil.show();
                    }
                });
            }
        });

        bigbang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OcrAnalsyser.getInstance().analyse(CaptureResultActivity.this, fileName, true, new OcrAnalsyser.CallBack() {
                    @Override
                    public void onSucess(OCR ocr) {
                        Intent intent = new Intent(CaptureResultActivity.this, BigBangActivity.class);
                        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(BigBangActivity.TO_SPLIT_STR, OcrAnalsyser.getInstance().getPasedMiscSoftText(ocr));
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFail() {
//                        ToastUtil.show();
                    }
                });
            }
        });
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
