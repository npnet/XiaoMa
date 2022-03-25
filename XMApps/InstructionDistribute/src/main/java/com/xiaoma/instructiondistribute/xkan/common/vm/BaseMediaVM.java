package com.xiaoma.instructiondistribute.xkan.common.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.instructiondistribute.xkan.common.model.UsbMediaInfo;
import com.xiaoma.thread.ThreadDispatcher;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 *  @项目名：  XMAgateOS
 *  @包名：    com.xiaoma.xkan.common
 *  @文件名:   BaseMediaVM
 *  @创建者:   Rookie
 *  @创建时间:  2018/11/15 15:08
 *  @描述：    TODO
 */
public abstract class BaseMediaVM extends AndroidViewModel {

    private MutableLiveData<List<UsbMediaInfo>> dataList;

    public MutableLiveData<List<UsbMediaInfo>> getDataList() {
        if (dataList == null) {
            dataList = new MutableLiveData<>();
        }
        return dataList;
    }

    public BaseMediaVM(@NonNull Application application) {
        super(application);
    }

    protected abstract List<UsbMediaInfo> initDataList();

    public void setDataList(List<UsbMediaInfo> dataList) {
        if (dataList != null) {
            getDataList().setValue(dataList);
        }
    }

    /**
     * 根据筛选条件给集合排序
     *
     * @param comparator
     */
    public void filterList(Comparator comparator) {
        final List<UsbMediaInfo> datas = initDataList();
        if (datas != null && !datas.isEmpty()) {
            Collections.sort(datas, comparator);
        }
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                getDataList().setValue(datas);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        dataList = null;
    }

}
