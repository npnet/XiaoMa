package com.xiaoma.personal.memory.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.XmResource;
import com.xiaoma.personal.memory.model.MemoryBean;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/11/19
 *     desc   :
 * </pre>
 */
public class MemoryDetailVM extends BaseViewModel {

    public MemoryDetailVM(@NonNull Application application) {
        super(application);
    }

    private MutableLiveData<List<String>> checkMemoryList;

    public MutableLiveData<List<String>> getCheckMemoryList() {
        if (checkMemoryList == null) {
            checkMemoryList = new MutableLiveData<>();
        }
        return checkMemoryList;
    }

    public MutableLiveData<XmResource<List<MemoryBean>>> getMemoryList() {
        final MutableLiveData<XmResource<List<MemoryBean>>> liveData = new MutableLiveData<>();
        // TODO: 2018/11/19  网络获取用户
        List<MemoryBean> list = new ArrayList<>();
        list.add(new MemoryBean("icon空调"));
        list.add(new MemoryBean("icon氛围灯"));
        list.add(new MemoryBean("icon座椅位置"));
        list.add(new MemoryBean("icon后视镜位置"));
        list.add(new MemoryBean("icon待定"));
        list.add(new MemoryBean("icon待定"));
        liveData.setValue(XmResource.success(list));
        return liveData;
    }


}
