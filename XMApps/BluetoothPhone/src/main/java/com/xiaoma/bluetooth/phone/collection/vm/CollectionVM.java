package com.xiaoma.bluetooth.phone.collection.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.xiaoma.bluetooth.phone.common.manager.BluetoothPhoneDbManager;
import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.component.base.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiuboxiang on 2018/12/4 20:11
 */
public class CollectionVM extends BaseViewModel {

    private MutableLiveData<List<ContactBean>> mCollections;

    public CollectionVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<ContactBean>> getCollections() {
        if (mCollections == null) {
            getCollectionsFromDb();
        }
        return mCollections;
    }

    public void getCollectionsFromDb() {
        if (mCollections == null) {
            mCollections = new MutableLiveData<>();
        }
        List<ContactBean> list = BluetoothPhoneDbManager.getInstance().queryAllCollection();
        mCollections.setValue(list != null ? list : new ArrayList<ContactBean>());
    }

}
