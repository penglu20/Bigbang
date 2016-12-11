package com.forfan.bigbang.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class NativeHelper {
    private static final String DEFAULT_IMEI = "DEFAULT_IMEI";

    public static String getCpuAbi() {
        try {
            String abi1 = Build.CPU_ABI;
            String abi2 = Build.CPU_ABI2;
            if ((abi1 != null && abi1.contains("armeabi"))
                    || (abi2 != null && abi2.contains("armeabi"))) {
                return "armeabi";
            } else if ((abi1 != null && abi1.contains("mips"))
                    || (abi2 != null && abi2.contains("mips"))) {
                return "mips";
            } else if ((abi1 != null && abi1.contains("x86"))
                    || (abi2 != null && abi2.contains("x86"))) {
                return "x86";
            } else {
                return "armeabi";
            }
        } catch (Exception e) {
            return "armeabi";
        }
    }


    public final static String getImei(Context context) {
        if (context != null) {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            try {
                if (tm != null && !TextUtils.isEmpty(tm.getDeviceId())) {
                    return tm.getDeviceId();
                }
            } catch (Exception e) {

            }
        }
        return DEFAULT_IMEI;
    }

    public static String copyNativeLib(Context context, String name) {
        File dir = context.getDir("MyLibs", Context.MODE_PRIVATE);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String assetpath = String.format("library/%s/%s", getCpuAbi(), name);
        File dstFile = new File(dir, name);
        AssetManager assets = context.getAssets();
        if (dstFile.exists()) {
            String assetMd5 = md5AssetFile(context, assetpath);
            String fileMd5 = Md5Util.md5(dstFile);
            if (assetMd5 != null && assetMd5.equalsIgnoreCase(fileMd5)) {
                try {
                    Process process = Runtime.getRuntime().exec("chmod 755 " + dstFile.getPath());
                    process.waitFor();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                // md5 一致，说明不需要再拷贝了。直接返回true；
                return dstFile.getPath();
            } else {
                dstFile.delete();
            }
        }
        if (copyAssetFile(context, assetpath, dstFile.getPath())) {
            String assetMd5 = md5AssetFile(context, assetpath);
            String fileMd5 = Md5Util.md5(dstFile);
            if (assetMd5 != null && assetMd5.equalsIgnoreCase(fileMd5)) {
                try {
                    Process process = Runtime.getRuntime().exec("chmod 755 " + dstFile.getPath());
                    process.waitFor();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                return dstFile.getPath();
            } else {
                dstFile.delete();
                return null;
            }
        } else {
            return null;
        }
    }

    private static String md5AssetFile(Context context, String assetpath) {
        InputStream in = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            in = context.getAssets().open(assetpath);
            byte[] buffer = new byte[8192];
            int readed = 0;
            while ((readed = in.read(buffer)) != -1) {
                out.write(buffer, 0, readed);
            }
            return Md5Util.md5(out.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private static boolean copyAssetFile(Context context, String name, String dstPath) {
        InputStream in = null;
        FileOutputStream out = null;
        try {
            in = context.getAssets().open(name);
            out = new FileOutputStream(dstPath);
            byte[] buffer = new byte[8192];
            int readed = 0;
            while ((readed = in.read(buffer)) != -1) {
                out.write(buffer, 0, readed);
            }
            return new File(dstPath).exists();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                }
            }
        }
        return false;
    }
}
