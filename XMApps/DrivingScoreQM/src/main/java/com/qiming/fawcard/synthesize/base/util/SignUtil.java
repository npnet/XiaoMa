package com.qiming.fawcard.synthesize.base.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by summit on 2/21/17.
 */

public class SignUtil {


    public static String sign(String path, Map<String, Object> params, String secretKey) {
        LinkedList parameters = new LinkedList(params.entrySet());//  监听集合
        Collections.sort(parameters, new Comparator<Map.Entry<String, Object>>() {
            @Override
            public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        StringBuilder sb = new StringBuilder();
        sb.append(path).append("_");
        Iterator baseString = parameters.iterator();

        while (baseString.hasNext()) {
            Map.Entry param = (Map.Entry) baseString.next();
            sb.append((String) param.getKey()).append("=").append(param.getValue().toString()).append("_");
        }

        sb.append(secretKey);
        try {
            String baseString1 = URLEncoder.encode(sb.toString(), "UTF-8");
            return encrypt(baseString1);
        } catch (Exception e) {
            return "";
        }
    }

    private static final String encrypt(String s) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        byte[] inputBytes = s.getBytes("UTF-8");
        MessageDigest mdInst = MessageDigest.getInstance("md5");
        mdInst.update(inputBytes);
        byte[] md = mdInst.digest();
        int j = md.length;
        char[] str = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; ++i) {
            byte byte0 = md[i];
            str[k++] = hexDigits[byte0 >>> 4 & 15];
            str[k++] = hexDigits[byte0 & 15];
        }
        return new String(str);
    }
}
