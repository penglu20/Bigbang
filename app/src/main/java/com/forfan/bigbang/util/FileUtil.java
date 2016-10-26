package com.forfan.bigbang.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil{
    public static final int FILEDONOTEXIST=-2;
    public static final int ERROR=-1;
    public static final int OK=0;
    public static final int BUFFERSIZE=1024;

    public static int copyFile(String src, String des){
        File srcFile=new File(src);
        File desFile=new File(des);
        if (!srcFile.exists()){
            return FILEDONOTEXIST;
        }
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
             bis=new BufferedInputStream(new FileInputStream(srcFile));
             bos=new BufferedOutputStream(new FileOutputStream(desFile));
            byte[] temp=new byte[BUFFERSIZE];
            int length=bis.read(temp);
            while(length!=-1){
                bos.write(temp,0,length);
                length=bis.read(temp);
            }
            return OK;
        } catch (IOException e) {
            e.printStackTrace();
            return ERROR;
        } finally {
            try {
                if (bis!=null){
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (bos!=null){
                    bos.flush();
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static String readFromFile(String filePath){
        return "";
    }
    public static int saveFile(String filePath, String content){
        return OK;
    }
}