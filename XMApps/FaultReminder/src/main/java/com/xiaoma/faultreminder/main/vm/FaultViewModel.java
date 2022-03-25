package com.xiaoma.faultreminder.main.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.faultreminder.sdk.FaultFactory;
import com.xiaoma.faultreminder.sdk.model.CarFault;
import com.xiaoma.utils.CollectionUtil;

import java.util.List;

/**
 * @author KY
 * @date 12/27/2018
 */
public class FaultViewModel extends BaseViewModel {
    private MutableLiveData<List<CarFault>> mFaults;

    public FaultViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<CarFault>> getFaults() {
        if (mFaults == null) {
            mFaults = new MutableLiveData<>();
        }
        return mFaults;
    }

    public void fetchFaults() {
        List<CarFault> currentFaults = FaultFactory.getSDK().getCurrentFaults();
        if (!CollectionUtil.isListEmpty(currentFaults)) {
            mFaults.setValue(currentFaults);
        }
    }
}
