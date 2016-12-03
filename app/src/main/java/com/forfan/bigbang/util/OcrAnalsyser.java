package com.forfan.bigbang.util;

import android.text.TextUtils;

import com.forfan.bigbang.BigBangApp;
import com.forfan.bigbang.R;
import com.forfan.bigbang.component.base.BaseActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.LanguageCodes;
import com.microsoft.projectoxford.vision.contract.Line;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.microsoft.projectoxford.vision.contract.Region;
import com.microsoft.projectoxford.vision.contract.Word;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;
import com.microsoft.projectoxford.vision.rest.WebServiceRequest;
import com.shang.commonjar.contentProvider.SPHelper;

import java.io.IOException;
import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangyan-pd on 2016/11/19.
 */

public class OcrAnalsyser {
    //别人的 00b0e581e4124a2583ea7dba57aaf281
    // 我自己的 56c87e179c084cfaae9b70a2f58fa8d3 56c87e179c084cfaae9b70a2f58fa8d3
    //彭露的 9e88939475894dec85a2019fd36243be
    //进发的 eac11887004a4c88a7c3f527d6852bb3
    //王岩2 cc750e4c195d497391e9fe18f6d21bae
    static ArrayList<String> keys;

    static {
        keys = new ArrayList<>();
        keys.add("9e88939475894dec85a2019fd36243be");
        keys.add("56c87e179c084cfaae9b70a2f58fa8d3");
        keys.add("eac11887004a4c88a7c3f527d6852bb3");
        keys.add("cc750e4c195d497391e9fe18f6d21bae");
        keys.add("ca5041c264f04f2e8c09f789ac19dbf1");
        keys.add("2a681f188c9c43b3a6581f0b3d4e5de7");
        keys.add("00b0e581e4124a2583ea7dba57aaf281");
    }

    //String[] keys = { "9e88939475894dec85a2019fd36243be", "56c87e179c084cfaae9b70a2f58fa8d3"};
    int currentIndex = 0;
    private static OcrAnalsyser instance = new OcrAnalsyser();
    VisionServiceRestClient client = new VisionServiceRestClient(keys.get(currentIndex));
    private String img_path;
    Observable.OnSubscribe<OCR> mOnSubscrube = new Observable.OnSubscribe<OCR>() {
        @Override
        public void call(Subscriber<? super OCR> subscriber) {
            client.setOnTimeUseUp(new WebServiceRequest.OnResult() {
                @Override
                public void onTimeUseUp() {
                    //返回403
                    currentIndex = (currentIndex + 1) % keys.size();
                    client = new VisionServiceRestClient(keys.get(currentIndex));
                    subscriber.onError(new IOException(BigBangApp.getInstance().getResources().getString(R.string.ocr_useup_toast)));
                }

                @Override
                public void onSuccess() {

                }
            });
            byte[] data = IOUtil.getBytes(img_path);
            try {
                String ocr = client.recognizeText(data, LanguageCodes.AutoDetect, verticalOrentation);
                if (!TextUtils.isEmpty(ocr)) {
                    OCR ocrItem = new Gson().fromJson(ocr, new TypeToken<OCR>() {
                    }.getType());
                    subscriber.onNext(ocrItem);
                }
            } catch (VisionServiceException e) {
                e.printStackTrace();
                subscriber.onError(e);
            } catch (IOException e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        }
    };
    private boolean verticalOrentation = true;
    Observable.OnSubscribe<OCR> mOnSubscrube1 = new Observable.OnSubscribe<OCR>() {
        @Override
        public void call(Subscriber<? super OCR> subscriber) {

            try {
                client.setOnTimeUseUp(new WebServiceRequest.OnResult() {
                    @Override
                    public void onTimeUseUp() {
                        //返回403
                        currentIndex = (currentIndex + 1) % keys.size();
                        client = new VisionServiceRestClient(keys.get(currentIndex));
                        subscriber.onError(new IOException(BigBangApp.getInstance().getResources().getString(R.string.ocr_useup_toast)));
                    }

                    @Override
                    public void onSuccess() {

                    }
                });
                String ocr = client.recognizeText(img, LanguageCodes.AutoDetect, verticalOrentation);

                if (!TextUtils.isEmpty(ocr)) {
                    OCR ocrItem = new Gson().fromJson(ocr, new TypeToken<OCR>() {
                    }.getType());
                    subscriber.onNext(ocrItem);
                }
            } catch (VisionServiceException e) {
                e.printStackTrace();
                subscriber.onError(e);
            } catch (IOException e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        }
    };
    private byte[] img;

    public static OcrAnalsyser getInstance() {
        return instance;
    }

    public interface CallBack {
        void onSucess(OCR ocr);

        void onFail(Throwable throwable);
    }

    public void analyse(BaseActivity activity, String img_path, boolean isVertical, CallBack callback) {
        String diykey = SPHelper.getString(ConstantUtil.DIY_OCR_KEY, "");
        if (!TextUtils.isEmpty(diykey) && !keys.contains(diykey)) {
            keys.add(0, SPHelper.getString(ConstantUtil.DIY_OCR_KEY, ""));
            currentIndex = 0;
            client = new VisionServiceRestClient(keys.get(currentIndex));
        }
        if (callback == null)
            return;
        int time = SPHelper.getInt(ConstantUtil.OCR_TIME, 0) + 1;
        SPHelper.save(ConstantUtil.OCR_TIME, time);
        this.img_path = img_path;
        this.verticalOrentation = isVertical;
        Observable.create(mOnSubscrube)
                .subscribeOn(Schedulers.io())
                .compose(activity.bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> callback.onSucess(s),
                        throwable -> {
                            callback.onFail(throwable);
                            SPHelper.save(ConstantUtil.SHOULD_SHOW_DIY_OCR, true);
                        });
    }

//    public void analyse(byte[] img, CallBack callback) {
//        if (callback == null)
//            return;
//        this.img = img;
//        try {
//            Observable.create(mOnSubscrube1)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(s -> callback.onSucess(s),
//                            throwable -> callback.onFail(throwable));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    public String getPasedMiscSoftText(OCR ocr) {

        String result = "";
        for (Region reg : ocr.regions) {
            for (Line line : reg.lines) {
                for (Word word : line.words) {
                    result += word.text + " ";
                }
                result += "\n";
            }
            result += "\n\n";
        }
        if (ocr.language.equalsIgnoreCase(LanguageCodes.ChineseSimplified) || ocr.language.equalsIgnoreCase(LanguageCodes.ChineseTraditional)) {
            result = result.replaceAll(" ", "");
        }
        if (TextUtils.isEmpty(result))
            result = "no text found";
        return result;
    }

}
