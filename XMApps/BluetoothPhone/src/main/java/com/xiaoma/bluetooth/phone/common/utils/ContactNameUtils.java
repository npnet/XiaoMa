package com.xiaoma.bluetooth.phone.common.utils;

import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.bluetooth.phone.BlueToothPhone;
import com.xiaoma.bluetooth.phone.R;

/**
 * @Author ZiXu Huang
 * @Data 2019/1/18
 */
public class ContactNameUtils {
    public static String getLimitedContactName(String contactName) {
        Context context = BlueToothPhone.getContext();
        if (TextUtils.isEmpty(contactName)) {
            return context.getString(R.string.unknown_contact);
        }
        if (contactName.length() <= 7) {
            return contactName;
        } else {
            return contactName.substring(0, 7) + "...";
        }
    }

    public static String getLimitedPhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) return "";
        if (phoneNumber.length() <= 11) {
            return phoneNumber;
        } else {
            return phoneNumber.substring(0, 11) + "...";
        }
    }
}
