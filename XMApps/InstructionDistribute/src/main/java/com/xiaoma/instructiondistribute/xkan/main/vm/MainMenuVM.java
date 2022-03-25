package com.xiaoma.instructiondistribute.xkan.main.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.TreeMap;

/**
 * Created by Thomas on 2018/11/5 0005
 */

public class MainMenuVM extends AndroidViewModel {

    private MutableLiveData<TreeMap<Integer, String>> menuIndexMap;

    public MainMenuVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<TreeMap<Integer, String>> getMenuIndexMap() {
        if (menuIndexMap == null) {
            menuIndexMap = new MutableLiveData();
        }
        return menuIndexMap;
    }

    public void setMenuData(Integer integer, String fragmentTag) {
        TreeMap<Integer, String> integerStringTreeMap = new TreeMap<>();
        integerStringTreeMap.put(integer, fragmentTag);
        getMenuIndexMap().setValue(integerStringTreeMap);
    }

    @Override
    protected void onCleared() {
        menuIndexMap = null;
        super.onCleared();
    }

}
