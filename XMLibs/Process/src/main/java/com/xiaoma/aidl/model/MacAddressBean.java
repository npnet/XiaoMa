package com.xiaoma.aidl.model;

import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiuboxiang on 2019/3/29 16:32
 * Desc:
 */
@Table("BluetoothPhone_MacAddress")
public class MacAddressBean {

    @PrimaryKey(AssignType.BY_MYSELF)
    private String address;

    @Mapping(Relation.OneToMany)
    private ArrayList<ContactBean> contactBeans;

    public MacAddressBean(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<ContactBean> getContactBeans() {
        return contactBeans;
    }

    public void setContactBeans(ArrayList<ContactBean> contactBeans) {
        this.contactBeans = contactBeans;
    }
}
