package com.xiaoma.assistant.model;

/**
 * Created by ZhangYao.
 * Date ：2017/5/9 0009
 */

public class ConfirmWord {
    private String keyWord;
    private int agree;
    private boolean startWith;

    public ConfirmWord(String keyWord, int agree) {
        this.keyWord = keyWord;
        this.agree = agree;
    }

    public ConfirmWord(String keyWord, boolean isConfirm) {
        this.keyWord = keyWord;
        this.agree = isConfirm ? 0 : 1;
    }

    public ConfirmWord(String keyWord, boolean isConfirm, boolean startWith) {
        this.keyWord = keyWord;
        this.agree = isConfirm ? 0 : 1;
        this.startWith = startWith;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public int getAgree() {
        return agree;
    }

    public void setAgree(int agree) {
        this.agree = agree;
    }

    public boolean isStartWith() {
        return startWith;
    }

    public void setStartWith(boolean startWith) {
        this.startWith = startWith;
    }

    @Override
    public String toString() {
        return "ConfirmWord{" +
                "keyWord='" + keyWord + '\'' +
                ", agree=" + agree +
                '}';
    }

    public boolean isConfirm() {
        return agree == 0;
    }
}
