package com.xiaoma.xkan.picture.vm;

import android.app.Application;
import android.support.annotation.NonNull;

import com.xiaoma.xkan.common.manager.UsbMediaDataManager;
import com.xiaoma.xkan.common.model.UsbMediaInfo;
import com.xiaoma.xkan.common.vm.BaseMediaVM;

import java.util.List;

/**
 * Created by Thomas on 2018/11/9 0009
 */

public class PictureFragmentVM extends BaseMediaVM {

    public PictureFragmentVM(@NonNull Application application) {
        super(application);
    }

    @Override
    protected List<UsbMediaInfo> initDataList() {
        return UsbMediaDataManager.getInstance().getPictureList();
    }

}
