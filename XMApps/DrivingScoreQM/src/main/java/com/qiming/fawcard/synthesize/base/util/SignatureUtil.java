package com.qiming.fawcard.synthesize.base.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class SignatureUtil {

    /**
     * 签名算法
     *
     * @param parameterMap 参数Map集合
     * @return
     */

    public static String getSign(Map<String, String> parameterMap, String key) {

        ArrayList<Map.Entry<String, String>> list = new ArrayList<>(parameterMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1,
                               Map.Entry<String, String> o2) {
                return o1.getKey().compareTo(o2.getKey());//顺序
            }
        });
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            buffer.append(list.get(i).getKey()).append("=").append(list.get(i).getValue()).append("&");
        }
        //"tspyiqi"
        buffer.append("key").append("=").append(key);
        return MD5Utils.MD5(buffer.toString());
    }
}
