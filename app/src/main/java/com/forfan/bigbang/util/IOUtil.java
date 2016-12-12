
package com.forfan.bigbang.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Arrays;
import java.util.LinkedList;

public class IOUtil {
    public IOUtil() {
    }

    public static void delete(File file) {
        file.delete();
    }

    public static void delete(String file) {
        (new File(file)).delete();
    }

    public static void mv(String source, String target) {
        try {
            Runtime.getRuntime().exec(String.format("mv %s %s", new Object[]{source, target}));
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    public static void mv(File source, File target) {
        target.deleteOnExit();
        source.renameTo(target);
    }

    public static String readString(String file) throws IOException {
        return readString(new File(file));
    }

    public static String readString(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        String str = readString((InputStream)in);
        in.close();
        return str;
    }

    public static byte[] readBytes(InputStream in) throws IOException {
        byte[] buf = new byte[1024];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean c = false;

        int c1;
        while((c1 = in.read(buf)) > 0) {
            out.write(buf, 0, c1);
        }

        byte[] bytes = out.toByteArray();
        out.close();
        return bytes;
    }

    public static byte[] readBytes(String path) throws IOException {
        FileInputStream in = new FileInputStream(path);
        byte[] bytes = readBytes((InputStream)in);
        in.close();
        return bytes;
    }

    public static String readString(InputStream in) throws IOException {
        byte[] bytes = readBytes(in);
        return new String(bytes, "UTF-8");
    }

    public static void writeString(OutputStream out, String str) throws IOException {
        out.write(str.getBytes());
    }

    public static void appendString(OutputStream out, String str) throws IOException {
        out.write(str.getBytes());
    }

    public static void appendString(File file, String str) throws IOException {
        FileOutputStream out = new FileOutputStream(file, true);
        out.write(str.getBytes());
        out.close();
    }

    public static void appendString(String file, String str) throws IOException {
        appendString(new File(file), str);
    }

    public static void writeString(File file, String str) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        out.write(str.getBytes());
        out.close();
    }

    public static void writeUTF8String(File file, String str) throws IOException {
        OutputStreamWriter outw = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        outw.write(str);
        outw.close();
    }

    public static void writeUTF8String(String file, String str) throws IOException {
        writeUTF8String(new File(file), str);
    }

    public static void writeString(String file, String str) throws IOException {
        writeString(new File(file), str);
    }

    public static boolean copy(InputStream in, String target) throws IOException {
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(new File(target));
            byte[] buf = new byte[10240];
            boolean c = false;

            int c1;
            while((c1 = in.read(buf)) > 0) {
                out.write(buf, 0, c1);
            }
        } finally {
            if(out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException var10) {
                    var10.printStackTrace();
                }
            }

        }

        return true;
    }

    public static boolean copy(String source, String target) {
        FileInputStream in = null;
        FileOutputStream out = null;

        try {
            in = new FileInputStream(new File(source));
            out = new FileOutputStream(new File(target));
            byte[] e = new byte[1024];
            boolean c = false;

            int c1;
            while((c1 = in.read(e)) > 0) {
                out.write(e, 0, c1);
            }

            return true;
        } catch (FileNotFoundException var21) {
            var21.printStackTrace();
            return false;
        } catch (IOException var22) {
            var22.printStackTrace();
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException var20) {
                    var20.printStackTrace();
                }
            }

            if(out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException var19) {
                    var19.printStackTrace();
                }
            }

        }

        return false;
    }

    public static boolean copyWithFileLock(String source, String target) {
        FileInputStream in = null;
        FileOutputStream out = null;
        FileChannel fileChannel = null;
        File targetFile=new File(target);
        FileLock fileLock = null;
        boolean hasBeenLocked = false;

        try {
            in = new FileInputStream(new File(source));
            out = new FileOutputStream(targetFile,true);
            //用源文件做锁，不然目标文件会被置空
            fileChannel=out.getChannel();
            fileLock=fileChannel.tryLock();
            if (fileLock==null){
                hasBeenLocked=true;
                fileLock=fileChannel.lock();
            }

            //由于是复制，所以复制一次就够了，等其他地方复制完毕，就返回
            if (hasBeenLocked){
                return true;
            }

            out = new FileOutputStream(targetFile);
            byte[] e = new byte[1024];
            boolean c = false;

            int c1;
            while((c1 = in.read(e)) > 0) {
                out.write(e, 0, c1);
            }

            out.flush();
            return true;
        } catch (FileNotFoundException var21) {
            var21.printStackTrace();
            return false;
        } catch (IOException var22) {
            var22.printStackTrace();
        } finally {
            if (fileLock!=null){
                try {
                    fileLock.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(in != null) {
                try {
                    in.close();
                } catch (IOException var20) {
                    var20.printStackTrace();
                }
            }
            if(out != null) {
                try {
                    out.close();
                } catch (IOException var19) {
                    var19.printStackTrace();
                }
            }


        }
        return false;
    }
    public static void serialize(Serializable obj, String file) throws FileNotFoundException, IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));

        try {
            oos.writeObject(obj);
        } catch (IOException var11) {
            var11.printStackTrace();
            throw var11;
        } finally {
            try {
                oos.close();
            } catch (IOException var10) {
                var10.printStackTrace();
            }

        }

    }

    public static Object unserialize(String file) throws Exception {
        ObjectInputStream ois = null;

        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            Object var4 = ois.readObject();
            return var4;
        } catch (Exception var12) {
            ;
        } finally {
            try {
                if(ois != null) {
                    ois.close();
                }
            } catch (IOException var11) {
                var11.printStackTrace();
            }

        }

        return null;
    }

    public static Object cloneObject(Object obj){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = null;

        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(out);
            oos.writeObject(obj);

            in = new ByteArrayInputStream(out.toByteArray());
            ois = new ObjectInputStream(in);
            return ois.readObject();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            try {
                if(oos != null)
                    oos.close();
                if(ois != null)
                    ois.close();
                if(in != null)
                    in.close();
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void saveToFile(InputStream in, String file) throws IOException {
        File outputFile=new File(file);
        outputFile.deleteOnExit();
        outputFile.getParentFile().mkdirs();
        BufferedOutputStream outputStream=new BufferedOutputStream(new FileOutputStream(outputFile));
        byte[] buffer=new byte[2048];
        int length=in.read(buffer);
        while (length!=-1){
            outputStream.write(buffer,0,length);
            length=in.read(buffer);
        }
        outputStream.flush();
        outputStream.close();
    }
    public static void saveToFile(InputStream in, File file) throws IOException {
        File outputFile= file;
        outputFile.deleteOnExit();
        outputFile.getParentFile().mkdirs();
        BufferedOutputStream outputStream=new BufferedOutputStream(new FileOutputStream(outputFile));
        byte[] buffer=new byte[2048];
        int length=in.read(buffer);
        while (length!=-1){
            outputStream.write(buffer,0,length);
            length=in.read(buffer);
        }
        outputStream.flush();
        outputStream.close();
    }
    /**
     * 获得指定文件的byte数组
     */
    public static byte[] getBytes(String filePath){
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
            byte[] b = new byte[(int) file.length()];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public static void deleteDirs(String themePath){
        LinkedList<File> themeLinkedList=new LinkedList<File>();
        File themeDir=new File(themePath);
        if (!themeDir.exists()) {
            return;
        }else if (themeDir.isDirectory()){
            themeLinkedList.addAll(Arrays.asList(themeDir.listFiles()));
            while(!themeLinkedList.isEmpty())
                deleteContent(themeLinkedList.pollLast());
        }else {
            themeDir.delete();
        }
    }

    private static void deleteContent(File file){
        LinkedList<File> themeLinkedList=new LinkedList<File>();
        if (file.isDirectory()) {
            themeLinkedList.addAll(Arrays.asList(file.listFiles()));
            while (!themeLinkedList.isEmpty()) {
                File subFile=themeLinkedList.pollLast();
                deleteContent(subFile);
            }
        }
        file.delete();
    }

    public static void copyFile(String srcPath,String desPath){
        File srcDir=new File(srcPath);
        File desDir=new File(desPath);
        if (!srcDir.exists()){
            return;
        }
        if (srcDir.isDirectory()) {
            desDir.mkdirs();
            File[] files=srcDir.listFiles();
            for (int i=0;i<files.length;i++){
                File file = files[i];
                File des=new File(desDir,file.getName());
                copyFile(file.getAbsolutePath(),des.getAbsolutePath());
            }
        }else {
            desDir.getParentFile().mkdirs();
            IOUtil.copy(srcPath,desPath);
        }
    }

}
