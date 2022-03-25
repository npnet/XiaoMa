package com.xiaoma.assistant.model;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/14
 * Desc:
 */
public class OptionWord {
    private String keyWord;
    private int number;
    private String type;
    private String numberCh = "";

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumberCh() {
        return numberCh;
    }

    @Override
    public String toString() {
        return "OptionWord{" +
                "keyWord='" + keyWord + '\'' +
                ", number=" + number +
                ", type='" + type + '\'' +
                '}';
    }


    public boolean isNavi() {
        return type.equals("all");
    }


    public boolean isMedia() {
        return type.equals("media") || type.equals("all");
    }

}
