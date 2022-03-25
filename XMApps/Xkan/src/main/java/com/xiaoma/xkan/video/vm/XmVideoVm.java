package com.xiaoma.xkan.video.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.model.XmResource;
import com.xiaoma.xkan.common.constant.XkanConstants;
import com.xiaoma.xkan.common.manager.UsbMediaDataManager;
import com.xiaoma.xkan.common.model.UsbMediaInfo;

import java.util.List;

/**
 * @author taojin
 * @date 2018/11/19
 */
public class XmVideoVm extends AndroidViewModel {

    private MutableLiveData<XmResource<List<UsbMediaInfo>>> mVideos;

    public XmVideoVm(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<UsbMediaInfo>>> getmVideos() {

        if (mVideos == null) {
            mVideos = new MutableLiveData<>();
        }
        return mVideos;
    }

    public void fetchmVideos(String type) {
        mVideos.setValue(XmResource.<List<UsbMediaInfo>>loading());
        if (type.equals(XkanConstants.FROM_ALL)) {
            mVideos.setValue(XmResource.success(UsbMediaDataManager.getInstance().getCurrVideoList()));
        } else if (type.equals(XkanConstants.FROM_VIDEO)) {
            mVideos.setValue(XmResource.success(UsbMediaDataManager.getInstance().getVideoList()));
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mVideos = null;
    }
}
