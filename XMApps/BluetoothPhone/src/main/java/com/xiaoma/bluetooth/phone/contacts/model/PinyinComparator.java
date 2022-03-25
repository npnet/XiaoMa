package com.xiaoma.bluetooth.phone.contacts.model;

import com.xiaoma.aidl.model.ContactBean;
import java.util.Comparator;

/**
 * Created by qiuboxiang on 2018/12/13 17:15
 */
public class PinyinComparator implements Comparator<ContactBean> {

    @Override
    public int compare(ContactBean o1, ContactBean o2) {
        int lhs_ascii = o1.getFirstPinYin().toUpperCase().charAt(0);
        int rhs_ascii = o2.getFirstPinYin().toUpperCase().charAt(0);
        // 判断若不是字母，则排在字母之前
        if (lhs_ascii < 65 || lhs_ascii > 90)
            return -1;
        else if (rhs_ascii < 65 || rhs_ascii > 90)
            return 1;
        else
            return o1.getPinYin().toUpperCase().compareTo(o2.getPinYin().toUpperCase());
    }
}
