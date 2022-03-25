package com.xiaoma.assistant.model;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.StringTokenizer;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/12
 * Desc：
 */
public class RecognitionWord {
    private String word;
    private String packageName;
    private String className;
    private String tabName;
    private String replyWord;
    private String realPackageName;
    private boolean isLauncher;
    private String ttsContent;
    private int sceneActionCode;

    public RecognitionWord() {
    }

    public RecognitionWord(String word, String packageName, String className, String tabName,
                           String replyWord, String realPackageName,
                           boolean isLauncher, String ttsContent, int sceneActionCode) {
        this.word = word;
        this.packageName = packageName;
        this.className = className;
        this.tabName = tabName;
        this.replyWord = replyWord;
        this.realPackageName = realPackageName;
        this.isLauncher = isLauncher;
        this.ttsContent = ttsContent;
        this.sceneActionCode = sceneActionCode;
    }

    public String getTtsContent() {
        return ttsContent;
    }

    public void setTtsContent(String ttsContent) {
        this.ttsContent = ttsContent;
    }

    public int getSceneActionCode() {
        return sceneActionCode;
    }

    public void setSceneActionCode(int sceneActionCode) {
        this.sceneActionCode = sceneActionCode;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPackageName(Context context) {
        if (!packageName.contains("|")) {
            return packageName;
        }
        if (!TextUtils.isEmpty(realPackageName)) {
            return realPackageName;
        }

        StringTokenizer tokenizer = new StringTokenizer(packageName, "|");
        while (tokenizer.hasMoreElements()) {
            String pn = tokenizer.nextToken();
            //todo 判断是否安装
            /*if (Utils.isAppInstalled(context, pn)) {
                realPackageName = pn;
                break;
            }*/
        }
        return realPackageName;
    }

    public boolean isLauncher() {
        return isLauncher;
    }

    public void setLauncher(boolean launcher) {
        isLauncher = launcher;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getReplyWord() {
        return replyWord;
    }

    public void setReplyWord(String replyWord) {
        this.replyWord = replyWord;
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();

        if (!TextUtils.isEmpty(className)) {
            bundle.putString("forward", className);
        }
        if (!TextUtils.isEmpty(tabName)) {
            bundle.putString("tab", tabName);
        }
        return bundle;
    }
}
