package com.xiaoma.vr.model;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.utils.GsonHelper;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/4
 * Desc:
 */

public class Contacts implements Serializable {
    private static final long serialVersionUID = 646863438754L;

    private int id;

    private String phoneNumber;

    private long contactId;

    private String category;

    private String userName;

    private boolean isUploaded = false;

    public Contacts(){

    }

    public Contacts(String userName, String phoneNumber, long contactId, String category) {
        this.userName = userName;
        this.contactId = contactId;
        this.phoneNumber = phoneNumber;
        this.category = category;
    }

    public static List<Contacts> parseFromMailList(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        Type type = new TypeToken<List<Contacts>>() {
        }.getType();
        return GsonHelper.fromJson(json, type);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }
}
