package com.xiaoma.bluetooth.phone.common.manager;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.MacAddressBean;
import com.xiaoma.db.DBManager;
import com.xiaoma.db.IDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiuboxiang on 2018/12/5 16:07
 */
public class BluetoothPhoneDbManager {

    private MacAddressBean macAddressBean;

    public static BluetoothPhoneDbManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final BluetoothPhoneDbManager instance = new BluetoothPhoneDbManager();
    }

    public static IDatabase getDBManager() {
        return DBManager.getInstance().getDBManager();
    }

    public List<ContactBean> queryAllCollection() {
        if (macAddressBean == null || macAddressBean.getContactBeans() == null) {
            return new ArrayList<>();
        }
        return macAddressBean.getContactBeans();
    }

    public void addCollection(ContactBean bean) {
        if (macAddressBean == null) return;
        if( macAddressBean.getContactBeans() == null) {
            macAddressBean.setContactBeans(new ArrayList<ContactBean>());
        }
        if (!macAddressBean.getContactBeans().contains(bean)) {
            macAddressBean.getContactBeans().add(bean);
        }
        saveMacAddress(macAddressBean);
    }

    public void deleteCollection(ContactBean bean) {
        if (macAddressBean == null) return;
        if( macAddressBean.getContactBeans() == null) {
            macAddressBean.setContactBeans(new ArrayList<ContactBean>());
        }
        if (macAddressBean.getContactBeans().contains(bean)) {
            macAddressBean.getContactBeans().remove(bean);
        }
        saveMacAddress(macAddressBean);
    }

    public void setCollectionList(ArrayList<ContactBean> list) {
        if (macAddressBean == null) return;
        macAddressBean.setContactBeans(list);
        saveMacAddress(macAddressBean);
    }

    public MacAddressBean queryMacAddress(String macAddress) {
        MacAddressBean bean = null;
        try {
            bean = getDBManager().queryById(macAddress, MacAddressBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    public void saveMacAddress(MacAddressBean macAddress) {
        try {
            getDBManager().save(macAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteMacAddress(MacAddressBean macAddress) {
        try {
            getDBManager().delete(macAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMacAddressBean(MacAddressBean macAddressBean) {
        this.macAddressBean = macAddressBean;
    }

    public String getMacAddress() {
        return macAddressBean != null ? macAddressBean.getAddress() : null;
    }
}
