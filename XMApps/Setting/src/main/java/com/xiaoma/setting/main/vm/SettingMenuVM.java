package com.xiaoma.setting.main.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.xiaoma.setting.R;

import java.util.Arrays;
import java.util.List;

public class SettingMenuVM extends AndroidViewModel {
    private MutableLiveData<List<String>> menuText = new MutableLiveData();

    public SettingMenuVM(@NonNull Application application) {
        super(application);
    }

    public SettingMenuVM initData(Context context) {
        String[] menu = context.getResources().getStringArray(R.array.setting_menu);
        menuText.setValue(Arrays.asList(menu));
        return this;
    }


    public MutableLiveData<List<String>> getMenuTexts() {
        return menuText;
    }

    @Override
    protected void onCleared() {
        menuText = null;
        super.onCleared();
    }
}
