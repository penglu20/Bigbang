package com.forfan.bigbang.component.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.forfan.bigbang.R;
import com.forfan.bigbang.view.BigBangLayout;

/**
 * Created by penglu on 2016/10/27.
 */

public class BigBangActivity extends AppCompatActivity {

    private BigBangLayout bigBangLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_bang);
        bigBangLayout= (BigBangLayout) findViewById(R.id.bigbang);

        bigBangLayout.reset();

        String txt="123 不 经过 制片人 授权 ， 456 其他人 不能 对 电影 做 拷贝 、 发行 、 反映 ， 不能 通过 网络 来 传播 ， 既 不能 把 电影 改编 成 小说 、 连环画 等 其他 艺术 形式 发表 ， 也 不能 把 一 部 几 个 小时 才能 放 完 的 电影 改编 成 半 个 小时 就 能 放 完 的 短片 。  ";
        String[] txts=txt.split("[ /,.，。！!、]");
        for (String t:txts) {
            bigBangLayout.addTextItem(t);
        }

    }
}
