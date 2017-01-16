package com.forfan.bigbang.entity;

/**
 * Created by wangyan-pd on 2017/1/9.
 */

public class ImageUpload {

    /**
     * width : 1365
     * height : 768
     * type : jpg
     * size : 32570
     * ubburl : [url=http://tietuku.com/fed3e1c4dc63d1ab2][img]http://i2.piimg.com/fed3e1c4dc63d1ab.jpg[/img][/url]
     * linkurl : http://i2.piimg.com/fed3e1c4dc63d1ab.jpg
     * htmlurl : <a href='http://tietuku.com/fed3e1c4dc63d1ab2' target='_blank'><img src='http://i2.piimg.com/fed3e1c4dc63d1ab.jpg' /></a>
     * s_url : http://i2.piimg.com/fed3e1c4dc63d1abs.jpg
     * t_url : http://i2.piimg.com/fed3e1c4dc63d1abt.jpg
     */

    private String width;
    private String height;
    private String type;
    private String size;
    private String ubburl;
    private String linkurl;
    private String htmlurl;
    private String s_url;
    private String t_url;

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUbburl() {
        return ubburl;
    }

    public void setUbburl(String ubburl) {
        this.ubburl = ubburl;
    }

    public String getLinkurl() {
        return linkurl;
    }

    public void setLinkurl(String linkurl) {
        this.linkurl = linkurl;
    }

    public String getHtmlurl() {
        return htmlurl;
    }

    public void setHtmlurl(String htmlurl) {
        this.htmlurl = htmlurl;
    }

    public String getS_url() {
        return s_url;
    }

    public void setS_url(String s_url) {
        this.s_url = s_url;
    }

    public String getT_url() {
        return t_url;
    }

    public void setT_url(String t_url) {
        this.t_url = t_url;
    }
}
