package com.xiaoma.app.util;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 工具类
 *
 * @author zhus
 */
public class SecurityUtil {

    private static String base64_random = "httpstd";

    /**
     * 将字符串编码为md5格式
     *
     * @param value
     * @return
     */
    public static String md5Encode(String value) {
        String tmp = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(value.getBytes("utf8"));
            byte[] md = md5.digest();
            tmp = binToHex(md);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return tmp;
    }

    public static String binToHex(byte[] md) {
        StringBuffer sb = new StringBuffer("");
        int read = 0;
        for (int i = 0; i < md.length; i++) {
            read = md[i];
            if (read < 0)
                read += 256;
            if (read < 16)
                sb.append("0");
            sb.append(Integer.toHexString(read));
        }

        return sb.toString();
    }

    /**
     * 计算给定路径的文件的md5值
     *
     * @param path APK文件路径
     * @return
     */
    public static String getMessageMd5(String path) {
        return getMessageDigest(path, "md5");
    }

    /**
     * 计算给定路径的文件的SHA-1值
     *
     * @param path APK文件路径
     * @return
     */
    public static String getMessageSha1(String path) {
        return getMessageDigest(path, "sha-1");
    }

    /**
     * 计算给定路径的文件的SHA-1值
     *
     * @param path      APK文件路径
     * @param algorithm 算法
     * @return
     */
    public static String getMessageDigest(String path, String algorithm) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }

        // 该对象通过使用 update() 方法处理数据
        BufferedInputStream in = null;
        MessageDigest messagedigest = null;
        try {
            messagedigest = MessageDigest.getInstance(algorithm);
            in = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[8192];
            int len = 0;

            while ((len = in.read(buffer)) != -1) {
                messagedigest.update(buffer, 0, len);
            }

            // 对于给定数量的更新数据，digest 方法只能被调用一次。在调用 digest 之后，MessageDigest
            // 对象被重新设置成其初始状态。
            return binToHex(messagedigest.digest());

        } catch (Throwable e) {
            e.printStackTrace();

        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
