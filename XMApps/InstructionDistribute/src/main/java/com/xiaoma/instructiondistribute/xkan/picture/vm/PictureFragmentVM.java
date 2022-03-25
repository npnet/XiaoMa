package com.xiaoma.instructiondistribute.xkan.picture.vm;

import android.app.Application;
import android.support.annotation.NonNull;

import com.xiaoma.instructiondistribute.xkan.common.manager.UsbMediaDataManager;
import com.xiaoma.instructiondistribute.xkan.common.model.UsbMediaInfo;
import com.xiaoma.instructiondistribute.xkan.common.vm.BaseMediaVM;

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
