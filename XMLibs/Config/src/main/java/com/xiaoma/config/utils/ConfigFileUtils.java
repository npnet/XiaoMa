package com.xiaoma.config.utils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 * Created by youthyj on 2018/9/5.
 */
public class ConfigFileUtils {
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String Tag = ConfigFileUtils.class.getSimpleName();
    private static final int BUFFER_SIZE_DEFAULT = 8 * 1024;

    private ConfigFileUtils() throws Exception {
        throw new Exception();
    }

    // 读
    public static String read(String path) {
        if (TextUtils.isEmpty(path)) {
            Log.w(Tag, "read file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return "";
        }
        return read(new File(path));
    }

    public static String read(File file) {
        return read(file, null, null, BUFFER_SIZE_DEFAULT);
    }

    public static String read(String path, String encoding, String separator, int bufferLength) {
        if (TextUtils.isEmpty(path)) {
            Log.w(Tag, "read file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return "";
        }
        return read(new File(path), encoding, separator, bufferLength);
    }

    public static String read(File file, String encoding, String separator, int bufferLength) {
        if (separator == null || separator.equals("")) {
            separator = NEW_LINE;
        }
        if (!file.exists()) {
            return "";
        }
        StringBuffer str = new StringBuffer();
        FileInputStream fs = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fs = new FileInputStream(file);
            if (encoding == null || encoding.trim().equals("")) {
                isr = new InputStreamReader(fs);
            } else {
                isr = new InputStreamReader(fs, encoding.trim());
            }
            br = new BufferedReader(isr, bufferLength);

            String data;
            while ((data = br.readLine()) != null) {
                str.append(data).append(separator);
            }
            if (str.length() > 0) {
                return str.substring(0, str.lastIndexOf(separator));
            } else {
                return str.toString();
            }
        } catch (IOException e) {
            Log.w(Tag, "read file content failure: "
                    + NEW_LINE + Log.getStackTraceString(e));
        } finally {
            close(br);
            close(isr);
            close(fs);
        }
        return "";
    }

    /**
     * 读取带注释的配置文件，该配置文件内部可用 # 开头作为 配置注释，
     * 配置文件中被 # 注释的该行不会被读取作为配置内容
     *
     * @param file         　file
     * @param encoding     　encoding
     * @param separator    　separator
     * @param bufferLength 　bufferLength
     * @return 配置内容
     */
    public static String readWidthDoc(File file, String encoding, String separator, int bufferLength) {
        if (separator == null || separator.equals("")) {
            separator = NEW_LINE;
        }
        if (!file.exists()) {
            return "";
        }
        StringBuffer str = new StringBuffer();
        FileInputStream fs = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fs = new FileInputStream(file);
            if (encoding == null || encoding.trim().equals("")) {
                isr = new InputStreamReader(fs);
            } else {
                isr = new InputStreamReader(fs, encoding.trim());
            }
            br = new BufferedReader(isr, bufferLength);

            String data;
            while ((data = br.readLine()) != null) {
                if (data.startsWith("#")) continue;
                str.append(data).append(separator);
            }
        } catch (IOException e) {
            Log.w(Tag, "read file content failure: "
                    + NEW_LINE + Log.getStackTraceString(e));
        } finally {
            close(br);
            close(isr);
            close(fs);
        }
        return str.substring(0, str.lastIndexOf(separator));
    }

    // 写(覆盖)
    public static boolean writeCover(String content, String path) {
        if (TextUtils.isEmpty(path)) {
            Log.w(Tag, "write file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return false;
        }
        return writeCover(content, new File(path));
    }

    public static boolean writeCover(String content, File target) {
        return write(content, target, false);
    }

    public static boolean writeCover(List<String> contents, String path) {
        if (TextUtils.isEmpty(path)) {
            Log.w(Tag, "write file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return false;
        }
        return writeCover(contents, new File(path));
    }

    public static boolean writeCover(List<String> contents, File target) {
        return write(contents, target, false);
    }

    public static boolean writeCover(InputStream is, String path) {
        if (TextUtils.isEmpty(path)) {
            Log.w(Tag, "write file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return false;
        }
        return writeCover(is, new File(path));
    }

    public static boolean writeCover(InputStream is, File target) {
        return write(is, target, false);
    }

    // 写(追加)
    public static boolean writeAppend(String content, String path) {
        if (TextUtils.isEmpty(path)) {
            Log.w(Tag, "write file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return false;
        }
        return writeAppend(content, new File(path));
    }

    public static boolean writeAppend(String content, File target) {
        return write(content, target, true);
    }

    public static boolean writeAppend(List<String> contents, String path) {
        if (TextUtils.isEmpty(path)) {
            Log.w(Tag, "write file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return false;
        }
        return writeAppend(contents, new File(path));
    }

    public static boolean writeAppend(List<String> contents, File target) {
        return write(contents, target, true);
    }

    public static boolean writeAppend(InputStream is, String path) {
        if (TextUtils.isEmpty(path)) {
            Log.w(Tag, "write file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return false;
        }
        return writeAppend(is, new File(path));
    }

    public static boolean writeAppend(InputStream is, File target) {
        return write(is, target, true);
    }

    // 写
    public static boolean write(String content, File target, boolean append) {
        if (TextUtils.isEmpty(content)) {
            Log.w(Tag, "write file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return false;
        }
        if (target.exists() && target.isDirectory()) {
            Log.w(Tag, "write file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return false;
        }
        if (parentNotExists(target)) {
            Log.w(Tag, "write file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return false;
        }

        FileWriter fw = null;
        try {
            fw = new FileWriter(target, append);
            fw.write(content);
            return true;
        } catch (IOException e) {
            Log.e(Tag, "write file failure: "
                    + NEW_LINE + Log.getStackTraceString(e)
            );
        } finally {
            close(fw);
        }
        return false;
    }

    private static boolean parentNotExists(File target) {
        if (target != null) {
            return !target.exists() && !target.getParentFile().exists() && !target.getParentFile().mkdirs();
        }
        return true;
    }

    public static boolean write(List<String> contents, File target, boolean append) {
        if (contents == null || contents.isEmpty()) {
            Log.w(Tag, "write file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return false;
        }
        if (target.exists() && target.isDirectory()) {
            Log.w(Tag, "write file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return false;
        }
        if (parentNotExists(target)) {
            Log.w(Tag, "write file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return false;
        }

        FileWriter fw = null;
        try {
            fw = new FileWriter(target, append);
            for (int i = 0; i < contents.size(); i++) {
                if (i > 0) {
                    fw.write(NEW_LINE);
                }
                fw.write(contents.get(i));
            }
            return true;
        } catch (IOException e) {
            Log.e(Tag, "write file failure: "
                    + NEW_LINE + Log.getStackTraceString(e)
            );
        } finally {
            close(fw);
        }
        return false;
    }

    public static boolean write(InputStream is, File target, boolean append) {
        if (is == null) {
            Log.w(Tag, "write file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return false;
        }
        if (target.exists() && target.isDirectory()) {
            Log.w(Tag, "write file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return false;
        }
        if (parentNotExists(target)) {
            Log.w(Tag, "write file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return false;
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(target, append);
            byte data[] = new byte[BUFFER_SIZE_DEFAULT];
            int length;
            while ((length = is.read(data)) != -1) {
                fos.write(data, 0, length);
            }
            fos.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(is);
            close(fos);
        }
        return false;
    }

    // 复制
    public static boolean copy(File source, File dest) {
        if (source == null || dest == null) {
            Log.w(Tag, "copy file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return false;
        }
        if (!source.exists() && source.isDirectory()) {
            Log.w(Tag, "copy file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return false;
        }
        if (dest.exists()) {
            Log.w(Tag, "copy file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return false;
        }
        dest.getParentFile().mkdirs();

        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel ifc = null;
        FileChannel ofc = null;
        try {
            fis = new FileInputStream(source);
            fos = new FileOutputStream(dest);
            ifc = fis.getChannel();
            ofc = fos.getChannel();
            MappedByteBuffer mbb = ifc.map(
                    FileChannel.MapMode.READ_ONLY,
                    0,
                    ifc.size()
            );
            ofc.write(mbb);
            return true;
        } catch (IOException e) {
            Log.w(Tag, "copy file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return false;
        } finally {
            close(ifc);
            close(ofc);
            close(fis);
            close(fos);
        }
    }

    // 删除
    public static boolean delete(String path) {
        if (TextUtils.isEmpty(path)) {
            Log.w(Tag, "delete file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return false;
        }
        return delete(new File(path));
    }

    public static boolean delete(File file) {
        if (file == null) {
            Log.w(Tag, "delete file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return false;
        }
        if (!file.exists()) {
            Log.w(Tag, "delete file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return false;
        }
        if (file.isDirectory()) {
            String[] children = file.list();
            for (int i = 0; i < children.length; i++) {
                File child = new File(file, children[i]);
                boolean success = delete(child);
                if (!success) {
                    return false;
                }
            }
        }
        boolean success = false;
        try {
            success = file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!success) {
            Log.w(Tag, "delete file failure: "
                    + NEW_LINE + file.getAbsolutePath()
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
        }
        return success;
    }

    // 移动
    public static boolean move(File source, File dest) {
        if (source == null || dest == null) {
            Log.w(Tag, "move file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return false;
        }
        if (!source.exists() && source.isDirectory()) {
            Log.w(Tag, "move file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return false;
        }
        if (dest.exists()) {
            Log.w(Tag, "move file failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
            return false;
        }
        return source.renameTo(dest) || (copy(source, dest) && delete(source));
    }

    // 关闭
    public static void close(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            Log.w(Tag, "stream close failure: "
                    + NEW_LINE + Log.getStackTraceString(new Throwable())
            );
        }
    }

    public static void close(Channel channel) {
        if (channel == null) {
            return;
        }
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean adCardExit = Environment.getExternalStorageState()
                .endsWith(Environment.MEDIA_MOUNTED);//判断SD卡是否挂载
        if (adCardExit) {
            sdDir = Environment.getExternalStorageDirectory();//获取根目录
        }
        if (sdDir != null) {
            return sdDir.toString();
        } else {
            return "";
        }
    }
}
