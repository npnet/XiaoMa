/*
 * Filename	CharacterParser.java
 * Company	涓婃捣涔愰棶-娴︿笢鍒嗗叕鍙搞??
 * @author	LuRuihui
 * @version	0.1
 */
package com.xiaoma.utils;

import com.github.promeg.pinyinhelper.Pinyin;

/**
 * Java汉字转换为拼音
 */
public class CharacterParseUtil {

    private static CharacterParseUtil characterParseUtil = new CharacterParseUtil();

    public static CharacterParseUtil getInstance() {
        return characterParseUtil;
    }


    /**
     * 获取字符串首字符拼音
     */
    public String getSelling(String chinese) {
        if (chinese == null || chinese.isEmpty()) {
            return "";
        }
        char[] firstCodeChars = chinese.toCharArray();
        if(firstCodeChars == null || firstCodeChars.length == 0){
            return "";
        }
        return  Pinyin.toPinyin(firstCodeChars[0]);
    }


}
