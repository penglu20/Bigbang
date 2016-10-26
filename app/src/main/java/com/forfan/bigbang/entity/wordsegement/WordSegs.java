package com.forfan.bigbang.entity.wordsegement;

import java.util.List;

/**
 * Created by wangyan-pd on 2016/10/26.
 */

public class WordSegs {

    private List<String> tag;
    private List<String> word;

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public List<String> getWord() {
        return word;
    }

    public void setWord(List<String> word) {
        this.word = word;
    }
}
