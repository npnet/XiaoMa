package com.xiaoma.setting.car.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

public class CarSettingVM extends AndroidViewModel {

    public CarSettingVM(@NonNull Application application) {
        super(application);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public CarSettingVM initData() {
        return this;
    }
}
