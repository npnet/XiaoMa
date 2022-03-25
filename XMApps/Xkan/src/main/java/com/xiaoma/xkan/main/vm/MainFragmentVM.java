package com.xiaoma.xkan.main.vm;

import android.app.Application;
import android.support.annotation.NonNull;

import com.xiaoma.xkan.common.manager.UsbMediaDataManager;
import com.xiaoma.xkan.common.model.UsbMediaInfo;
import com.xiaoma.xkan.common.vm.BaseMediaVM;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Thomas on 2018/11/9 0009
 */

public class MainFragmentVM extends BaseMediaVM {

    public MainFragmentVM(@NonNull Application application) {
        super(application);
    }

    @Override
    protected List<UsbMediaInfo> initDataList() {
        return UsbMediaDataManager.getInstance().getCurrPathFileList();
    }

    /**
     * 根据筛选条件给集合排序
     *
     * @param comparator
     */
    public void filterListV2(Comparator comparator,List<UsbMediaInfo> datas) {
        if (datas != null && !datas.isEmpty()) {
            Collections.sort(datas, comparator);
        }
        getDataList().setValue(datas);
    }

}
