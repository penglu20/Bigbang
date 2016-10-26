package com.forfan.bigbang.entity;

import java.util.List;

/**
 * Created by wangyan-pd on 2016/10/26.
 */

public class TranslationItem {

    private BasicBean basic;
    /**
     * translation : ["API"]
     * basic : {"explains":["abbr. 应用程序界面（Application Program Interface）；精确位置指示器（Accurate Position Indicator）；美国石油学会（American Petroleum Institute）；自动发音教学机（Automated Pronunciation Instructor）"]}
     * query : API
     * errorCode : 0
     * web : [{"value":["阿比峰","应用程序编程接口","应用程序接口"],"key":"Api"},{"value":["Windows API","윈도 API","Windows API"],"key":"Windows API"},{"value":["Native API","Native API","本地API"],"key":"Native API"}]
     */

    private String query;
    private int errorCode;
    private List<String> translation;
    /**
     * value : ["阿比峰","应用程序编程接口","应用程序接口"]
     * key : Api
     */

    private List<WebBean> web;

    public BasicBean getBasic() {
        return basic;
    }

    public void setBasic(BasicBean basic) {
        this.basic = basic;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public List<String> getTranslation() {
        return translation;
    }

    public void setTranslation(List<String> translation) {
        this.translation = translation;
    }

    public List<WebBean> getWeb() {
        return web;
    }

    public void setWeb(List<WebBean> web) {
        this.web = web;
    }

    public static class BasicBean {
        private List<String> explains;

        public List<String> getExplains() {
            return explains;
        }

        public void setExplains(List<String> explains) {
            this.explains = explains;
        }
    }

    public static class WebBean {
        private String key;
        private List<String> value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public List<String> getValue() {
            return value;
        }

        public void setValue(List<String> value) {
            this.value = value;
        }
    }
}
