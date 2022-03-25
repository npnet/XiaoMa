package com.xiaoma.music.common.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.music.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2018/10/10 0010
 */
public class LeftTableVM extends AndroidViewModel {
    private MutableLiveData<List<String>> tableTitles;
    private MutableLiveData<Integer> tableIndex;


    public LeftTableVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<String>> getTableTitles() {
        if (tableTitles == null) {
            tableTitles = new MutableLiveData<>();
        }
        return tableTitles;
    }

    public void initTableTitleList() {
        final String[] stringArray = getApplication().getResources().getStringArray(R.array.tableTitle);
        getTableTitles().setValue(Arrays.asList(stringArray));
        getTableIndex().setValue(0);
    }

    public MutableLiveData<Integer> getTableIndex() {
        if (tableIndex == null) {
            tableIndex = new MutableLiveData<>();
        }
        return tableIndex;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        tableTitles = null;
        tableIndex = null;
    }
}
