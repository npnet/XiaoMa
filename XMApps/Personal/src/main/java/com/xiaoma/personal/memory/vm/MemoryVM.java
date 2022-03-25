package com.xiaoma.personal.memory.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.XmResource;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/11/19
 *     desc   :
 * </pre>
 */
public class MemoryVM extends BaseViewModel {

    public MemoryVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<String>>> getMemoryList() {
        final MutableLiveData<XmResource<List<String>>> liveData = new MutableLiveData<>();
        // TODO: 2018/11/19  网络获取用户
        List<String> list = new ArrayList<>();
        list.add("座椅位置");
        list.add("空调");
        list.add("氛围灯");
        list.add("后视镜位置");
        liveData.setValue(XmResource.success(list));
        return liveData;
    }


}
