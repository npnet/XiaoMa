package com.xiaoma.vr.utils;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/4
 * Desc:Vr相关工具类
 */
public class VrUtils {

    private final static String[] sPunctuation = {",", ".", "?", ";", "!", "，", "。", "？", "；", "！"};

    public static class IFly {
        public static String parseIatResult(String json) {
            StringBuffer ret = new StringBuffer();
            try {
                JSONTokener jsonTokener = new JSONTokener(json);
                JSONObject joResult = new JSONObject(jsonTokener);

                JSONArray words = joResult.getJSONArray("ws");
                for (int i = 0; i < words.length(); i++) {
                    // 转写结果词，默认使用第一个结果
                    JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                    JSONObject obj = items.getJSONObject(0);
                    ret.append(obj.getString("w"));
//				如果需要多候选结果，解析数组其他字段
//				for(int j = 0; j < items.length(); j++)
//				{
//					JSONObject obj = items.getJSONObject(j);
//					ret.append(obj.getString("w"));
//				}
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ret.toString();
        }
    }



    public static String replaceFilter(String voiceContent){
        if (TextUtils.isEmpty(voiceContent)) {
            return "";
        }
        if (isLastStringContainsChar(voiceContent)) {
            voiceContent = voiceContent.substring(0, voiceContent.length() - 1);
        }
        return voiceContent;
    }

    private static boolean isLastStringContainsChar(String content) {
        content = content.substring(content.length() - 1, content.length());
        for (int i = 0; i < sPunctuation.length; i++) {
            if (sPunctuation[i].equals(content)) {
                return true;
            }
        }
        return false;
    }

}
