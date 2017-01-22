package com.forfan.bigbang.entity;

/**
 * Created by wangyan-pd on 2017/1/9.
 */

public class ImageUpload {


    /**
     * code : success
     * data : {"width":1157,"height":680,"filename":"image_2015-08-26_10-54-48.png","storename":"56249afa4e48b.png","size":69525,"path":"/2015/10/19/56249afa4e48b.png","hash":"nLbCw63NheaiJp1","timestamp":1445239546,"url":"https://ooo.0o0.ooo/2015/10/19/56249afa4e48b.png","delete":"https://sm.ms/api/delete/nLbCw63NheaiJp1"}
     */

    private String code;
    /**
     * width : 1157
     * height : 680
     * filename : image_2015-08-26_10-54-48.png
     * storename : 56249afa4e48b.png
     * size : 69525
     * path : /2015/10/19/56249afa4e48b.png
     * hash : nLbCw63NheaiJp1
     * timestamp : 1445239546
     * url : https://ooo.0o0.ooo/2015/10/19/56249afa4e48b.png
     * delete : https://sm.ms/api/delete/nLbCw63NheaiJp1
     */

    private DataBean data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private int width;
        private int height;
        private String filename;
        private String storename;
        private int size;
        private String path;
        private String hash;
        private int timestamp;
        private String url;
        private String delete;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getStorename() {
            return storename;
        }

        public void setStorename(String storename) {
            this.storename = storename;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public int getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(int timestamp) {
            this.timestamp = timestamp;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDelete() {
            return delete;
        }

        public void setDelete(String delete) {
            this.delete = delete;
        }
    }
}
