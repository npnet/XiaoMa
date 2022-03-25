package com.xiaoma.smarthome.common.model;

import java.io.Serializable;

/**
 * Created by zy 2018/8/20 22:59
 */
public class AccountRecord implements Serializable {
    public String account;
    public String passWord;

    public AccountRecord(String account, String passWord) {
        this.account = account;
        this.passWord = passWord;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
