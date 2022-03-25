package com.xiaoma.instructiondistribute.xkan.picture.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.instructiondistribute.xkan.common.constant.XkanConstants;
import com.xiaoma.instructiondistribute.xkan.common.manager.UsbMediaDataManager;
import com.xiaoma.instructiondistribute.xkan.common.model.UsbMediaInfo;
import com.xiaoma.model.XmResource;

import java.util.List;

/**
 * @author taojin
 * @date 2018/11/12
 */
public class XmPhotoVM extends AndroidViewModel {

    private MutableLiveData<XmResource<List<UsbMediaInfo>>> mImages;

    public XmPhotoVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<UsbMediaInfo>>> getmImages() {
        if (mImages == null) {
            mImages = new MutableLiveData<>();
        }
        return mImages;
    }

    public void fetchImages(String type) {

        mImages.setValue(XmResource.<List<UsbMediaInfo>>loading());

        //代表从全部页面跳转过来
        if (type.equals(XkanConstants.FROM_ALL)) {
            getmImages().setValue(XmResource.success(UsbMediaDataManager.getInstance().getCurrPictList()));
        } else if (type.equals(XkanConstants.FROM_PHOTO)) {
            getmImages().setValue(XmResource.success(UsbMediaDataManager.getInstance().getPictureList()));
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mImages = null;
    }
}
