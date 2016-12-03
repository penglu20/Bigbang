package com.forfan.bigbang.component.activity.screen;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.util.ClipboardUtils;
import com.forfan.bigbang.util.ConstantUtil;
import com.forfan.bigbang.util.ToastUtil;
import com.shang.commonjar.contentProvider.SPHelper;
import com.shang.utils.StatusBarCompat;

public class DiyOcrKeyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diy_ocr_key);

        StatusBarCompat.setupStatusBarView(this, (ViewGroup) getWindow().getDecorView(), true, R.color.colorPrimary);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.set_diy_ocr_key);

        findViewById(R.id.copy_url).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardUtils.setText(DiyOcrKeyActivity.this,"https://www.microsoft.com/cognitive-services/");
                ToastUtil.show(R.string.copyed);
            }
        });
        EditText keyInput= (EditText) findViewById(R.id.ocr_diy_key_edit);
        keyInput.setText(SPHelper.getString(ConstantUtil.DIY_OCR_KEY,""));

        findViewById(R.id.ocr_diy_key_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (keyInput.getText()!=null)
                SPHelper.save(ConstantUtil.DIY_OCR_KEY,keyInput.getText().toString());
                ToastUtil.show(R.string.set_diy_ocr_key_ok);
                finish();
            }
        });

    }
}
