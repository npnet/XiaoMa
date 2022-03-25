package com.xiaoma.bluetooth.phone.contacts.model;

import com.chad.library.adapter.base.entity.SectionEntity;
import com.xiaoma.aidl.model.ContactBean;

/**
 * Created by qiuboxiang on 2018/12/4 20:33
 */
public class ContactSection extends SectionEntity<ContactBean> {

    public ContactSection(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public ContactSection(ContactBean t) {
        super(t);
    }
}
