package com.forfan.bigbang.component.activity.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;
import com.forfan.bigbang.component.activity.SettingFloatViewActivity;
import com.forfan.bigbang.component.activity.whitelist.SelectionDbHelper;
import com.forfan.bigbang.component.service.BigBangMonitorService;
import com.forfan.bigbang.util.AESUtils;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.IOUtil;
import com.forfan.bigbang.util.LogUtil;
import com.forfan.bigbang.util.NativeHelper;
import com.forfan.bigbang.util.ToastUtil;
import com.forfan.bigbang.view.Dialog;
import com.forfan.bigbang.view.DialogFragment;
import com.forfan.bigbang.view.SimpleDialog;
import com.shang.commonjar.contentProvider.SPHelper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;

import static com.forfan.bigbang.util.ConstantUtil.BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED;
import static com.forfan.bigbang.util.ConstantUtil.BROADCAST_CLIPBOARD_LISTEN_SERVICE_MODIFIED;
import static com.shang.commonjar.contentProvider.SPHelperImpl.MAINSPNAME;


/**
 * Created by penglu on 2015/11/23.
 */
public class SLSettingCard extends AbsCard {

    public SLSettingCard(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;

        LayoutInflater.from(context).inflate(R.layout.card_sl_setting, this);

        findViewById(R.id.default_setting_rl).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDefaultDialog();
            }
        });

        findViewById(R.id.save_setting_rl).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaveDialog();
            }
        });

        findViewById(R.id.load_setting_rl).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadDialog();
            }
        });

    }

    private void showLoadDialog() {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

            @Override
            protected void onBuildDone(Dialog dialog) {
                dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                super.onBuildDone(dialog);
            }

            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                // 这里是保持开启
                super.onPositiveActionClicked(fragment);
                loadSettings();
            }

            @Override
            public void onDismiss(DialogInterface dialog) {
                super.onCancel(dialog);
            }
        };
        builder.message(mContext.getString(R.string.load_setting_tips))
                .positiveAction(mContext.getString(R.string.confirm))
                .negativeAction(mContext.getString(R.string.cancel));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), null);
    }


    private void showSaveDialog() {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

            @Override
            protected void onBuildDone(Dialog dialog) {
                dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                super.onBuildDone(dialog);
            }

            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                // 这里是保持开启
                super.onPositiveActionClicked(fragment);
                saveSettings();
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
                saveOCR();
            }

            @Override
            public void onDismiss(DialogInterface dialog) {
                super.onCancel(dialog);
            }
        };
        builder.message(mContext.getString(R.string.save_setting_tips))
                .positiveAction(mContext.getString(R.string.save_other))
                .negativeAction(mContext.getString(R.string.only_save_ocr))
                .neutralAction(mContext.getString(R.string.cancel));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), null);
    }


    private void showDefaultDialog() {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

            @Override
            protected void onBuildDone(Dialog dialog) {
                dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                super.onBuildDone(dialog);
            }

            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                // 这里是保持开启
                defaultSettings();
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onDismiss(DialogInterface dialog) {
                super.onCancel(dialog);
            }
        };
        builder.message(mContext.getString(R.string.default_setting_tips))
                .positiveAction(mContext.getString(R.string.confirm))
                .negativeAction(mContext.getString(R.string.cancel));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), null);
    }

    private void defaultSettings() {
        String ocr = SPHelper.getString(ConstantUtil.DIY_OCR_KEY, "");
        SPHelper.clear();
        SPHelper.save(ConstantUtil.DIY_OCR_KEY, ocr);
        IOUtil.delete(SettingFloatViewActivity.FLOATVIEW_IMAGE_PATH);
        SelectionDbHelper helper=new SelectionDbHelper(mContext);
        helper.deleteAll();

        mContext.sendBroadcast(new Intent(ConstantUtil.EFFECT_AFTER_REBOOT_BROADCAST));

        ToastUtil.show(R.string.effect_after_reboot);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Process.killProcess(Process.myPid());
            }
        },1500);
    }

    private void saveSettings(){
        String ocr = SPHelper.getString(ConstantUtil.DIY_OCR_KEY, "");
        SPHelper.save(ConstantUtil.DIY_OCR_KEY, "");

        File file = mContext.getFilesDir();
        File dbDir=new File(file.getParentFile(),"databases");
        File spDir=new File(file.getParentFile(),"shared_prefs");

        File desDir= new File(Environment.getExternalStorageDirectory()+File.separator+"quannengfenci/backup");

        try {
            IOUtil.copyFile(dbDir.getAbsolutePath(),new File(desDir,"databases").getAbsolutePath());
            IOUtil.copyFile(SettingFloatViewActivity.FLOATVIEW_IMAGE_PATH,new File(desDir,"floatview.png").getAbsolutePath());
            IOUtil.copyFile(spDir.getAbsolutePath()+File.separator+"BigBang_sp_main.xml",new File(desDir,"shared_prefs").getAbsolutePath()+File.separator+"BigBang_sp_main.xml");
            ToastUtil.show(R.string.save_success);
        } catch (IOException e) {
            ToastUtil.show(R.string.save_fail);
        }
        SPHelper.save(ConstantUtil.DIY_OCR_KEY, ocr);
    }

    private void saveOCR(){
        String ocr = SPHelper.getString(ConstantUtil.DIY_OCR_KEY, "");
        String imei= NativeHelper.getImei(mContext);
        String cpu=NativeHelper.getCpuAbi();
        LogUtil.d("ocr="+ocr);
        LogUtil.d("imei="+imei);
        LogUtil.d("cpu="+cpu);

        File desOCRFile= new File(Environment.getExternalStorageDirectory()+File.separator+"quannengfenci/backup/OCR/ocr.txt");
        desOCRFile.getParentFile().mkdirs();
        String ocrEncrypt = AESUtils.encrypt(imei+cpu,ocr);
        InputStream inputStream=new ByteArrayInputStream(ocrEncrypt.getBytes());
        try {
            IOUtil.saveToFile(inputStream,desOCRFile);
            ToastUtil.show(R.string.save_success);
        } catch (IOException e) {
            ToastUtil.show(R.string.save_fail);
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private void loadSettings(){
        boolean dbRestore=true;
        boolean spRestore=true;
        String toast="";
        String toastEnd=mContext.getString(R.string.effect_after_reboot);
        File file = mContext.getFilesDir();
        File dbDir=new File(file.getParentFile(),"databases");
        File spDir=new File(file.getParentFile().getAbsolutePath()+"/shared_prefs/BigBang_sp_main.xml");



        File desDbDir= new File(Environment.getExternalStorageDirectory()+File.separator+"quannengfenci/backup/databases");
        File desSpFile= new File(Environment.getExternalStorageDirectory()+File.separator+"quannengfenci/backup/shared_prefs/BigBang_sp_main.xml");
        File floatViewFile = new File(Environment.getExternalStorageDirectory()+File.separator+"quannengfenci/backup/","floatview.png");

        if (floatViewFile.exists()){
            try {
                IOUtil.copyFile(floatViewFile.getAbsolutePath(),SettingFloatViewActivity.FLOATVIEW_IMAGE_PATH);
            } catch (IOException e) {
            }
        }

        if (desDbDir.exists()) {
            IOUtil.deleteDirs(dbDir.getAbsolutePath());
            try {
                IOUtil.copyFile(desDbDir.getAbsolutePath(),dbDir.getAbsolutePath());
                dbRestore=true;
            } catch (IOException e) {
                dbRestore=false;

            }
        }

        String ocrOrigin = SPHelper.getString(ConstantUtil.DIY_OCR_KEY, "");
        if (desSpFile.exists()) {
            SPHelper.clear();
            IOUtil.deleteDirs(spDir.getAbsolutePath());
            try {
                IOUtil.copyFile(desSpFile.getAbsolutePath(),spDir.getAbsolutePath());
                spRestore=true;
            } catch (IOException e) {
                spRestore=false;
            }
        }


        String imei= NativeHelper.getImei(mContext);
        String cpu=NativeHelper.getCpuAbi();
        LogUtil.d("imei="+imei);
        LogUtil.d("cpu="+cpu);

        File desOCRFile= new File(Environment.getExternalStorageDirectory()+File.separator+"quannengfenci/backup/OCR/ocr.txt");
        if (!desOCRFile.exists()){
            return;
        }
        String ocrBackup=null;
        try {
            String ocrEncrypt = IOUtil.readString(desOCRFile);
            ocrBackup = AESUtils.decrypt(imei+cpu,ocrEncrypt);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String ocr="";
        if (!dbRestore||!spRestore){
            toast=mContext.getString(R.string.restore_failed);
        }else {
            toast=mContext.getString(R.string.restore_success);
        }
        if (!TextUtils.isEmpty(ocrOrigin)){
            ocr=ocrOrigin;
            toast+=mContext.getString(R.string.restore_ocr_origin);
        }else if (!TextUtils.isEmpty(ocrBackup)){
            ocr=ocrBackup;
            toast+=mContext.getString(R.string.restore_ocr_back);
        }
        saveOcrKeyWithSP(ocr);

        mContext.sendBroadcast(new Intent(ConstantUtil.EFFECT_AFTER_REBOOT_BROADCAST));
        ToastUtil.show(toast+toastEnd);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Process.killProcess(Process.myPid());
            }
        },1500);
    }

    private void saveOcrKeyWithSP(String ocrKey){
        SharedPreferences sp = mContext.getSharedPreferences(MAINSPNAME, Context.MODE_PRIVATE);
        sp.edit().putString(ConstantUtil.DIY_OCR_KEY,ocrKey).commit();
    }

}
