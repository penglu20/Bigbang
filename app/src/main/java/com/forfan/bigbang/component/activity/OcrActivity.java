package com.forfan.bigbang.component.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.cropper.BitmapUtil;
import com.forfan.bigbang.cropper.CropHandler;
import com.forfan.bigbang.cropper.CropHelper;
import com.forfan.bigbang.cropper.CropParams;
import com.forfan.bigbang.cropper.ImageUriUtil;
import com.forfan.bigbang.entity.OcrItem;
import com.forfan.bigbang.network.RetrofitHelper;
import com.forfan.bigbang.util.LogUtil;
import com.forfan.bigbang.util.SnackBarUtil;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangyan-pd on 2016/11/9.
 */

public class OcrActivity extends BaseActivity implements View.OnClickListener, CropHandler {
    private static final String TAG = OcrActivity.class.getName();
    private CropParams mCropParams;
    private ImageView mImageView;
    private TextView mResultTextView;
    private Button mPicReOcr;
    private Uri mCurrentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orc);
        mCropParams = new CropParams(this);
        mImageView = (ImageView) findViewById(R.id.image);
        mResultTextView = (TextView) findViewById(R.id.result);
        mPicReOcr = (Button) findViewById(R.id.re_ocr);
        findViewById(R.id.take_pic).setOnClickListener(this);
        findViewById(R.id.select_pic).setOnClickListener(this);
        findViewById(R.id.re_ocr).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mCropParams.refreshUri();

        switch (v.getId()) {
            case R.id.take_pic:
                mCropParams.enable = true;
                mCropParams.compress = false;
                Intent intent = CropHelper.buildCameraIntent(mCropParams);
                startActivityForResult(intent, CropHelper.REQUEST_CAMERA);
                mPicReOcr.setVisibility(View.GONE);

                break;
            case R.id.select_pic:
                mCropParams.enable = true;
                mCropParams.compress = false;
                Intent intent1 = CropHelper.buildGalleryIntent(mCropParams);
                startActivityForResult(intent1, CropHelper.REQUEST_CROP);
                mPicReOcr.setVisibility(View.GONE);
                break;
            case R.id.re_ocr:
                if(mCurrentUri != null)
                    uploadImage4Ocr(mCurrentUri);
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CropHelper.handleResult(this, requestCode, resultCode, data);
        if (requestCode == 1) {
            Log.e(TAG, "");
        }
    }

    @Override
    protected void onDestroy() {
        CropHelper.clearCacheDir();
        super.onDestroy();
    }

    @Override
    public CropParams getCropParams() {
        return mCropParams;
    }

    @Override
    public void onPhotoCropped(Uri uri) {
        // Original or Cropped uri
        Log.d(TAG, "Crop Uri in path: " + uri.getPath());
        mImageView.setVisibility(View.VISIBLE);
        if (!mCropParams.compress) {
            mImageView.setImageBitmap(BitmapUtil.decodeUriAsBitmap(this, uri));
            uploadImage4Ocr(uri);
            mCurrentUri = uri;

        }

    }

    private void uploadImage4Ocr(Uri uri) {
        String img_path = ImageUriUtil.getImageAbsolutePath(this, uri);
        File file = new File(img_path);
        String descriptionString = "hello, this is description speaking";
        RequestBody requestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        RetrofitHelper.getOcrService()
                .uploadImage("e02e6b613488957", descriptionString, requestBody)
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recommendInfo -> {
                    LogUtil.d(recommendInfo.toString());
                    mResultTextView.setText(getPasedText(recommendInfo));
                }, throwable -> {
                    LogUtil.d(throwable.toString());
                    mResultTextView.setText("抱歉，解析失败");
                    mPicReOcr.setVisibility(View.VISIBLE);
                });

    }

    private CharSequence getPasedText(OcrItem recommendInfo) {
        if (recommendInfo != null && recommendInfo.getParsedResults() != null) {
            StringBuffer stringBuffer = new StringBuffer();
            for (OcrItem.ParsedResultsBean bean : recommendInfo.getParsedResults()) {
                if (!TextUtils.isEmpty(bean.getParsedText())) {
                    stringBuffer.append(bean.getParsedText() + "\n");
                }
            }

            String result = stringBuffer.toString();
            if (!TextUtils.isEmpty(result)) {
                showBigBang(result);
                return result;
            }
        }
        return "抱歉，未获取到文本。";
    }

    private void showBigBang(String result) {
        Intent intent = new Intent(this, BigBangActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(BigBangActivity.TO_SPLIT_STR, result);
        startActivity(intent);
    }

    @Override
    public void onCompressed(Uri uri) {
        // Compressed uri
        mImageView.setImageBitmap(BitmapUtil.decodeUriAsBitmap(this, uri));

    }

    @Override
    public void onCancel() {
        SnackBarUtil.show(mResultTextView, "Crop canceled!");
    }

    @Override
    public void onFailed(String message) {
        SnackBarUtil.show(mResultTextView, "Crop failed: " + message);
    }

    @Override
    public void handleIntent(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }


}
