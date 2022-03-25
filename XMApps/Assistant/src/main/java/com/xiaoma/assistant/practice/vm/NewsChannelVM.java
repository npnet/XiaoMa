package com.xiaoma.assistant.practice.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.assistant.manager.RequestManager;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.pratice.NewsBean;
import com.xiaoma.model.pratice.NewsChannelBean;
import com.xiaoma.utils.ListUtils;

import java.util.List;

/**
 * @author taojin
 * @date 2019/6/6
 */
public class NewsChannelVM extends BaseViewModel {

    private MutableLiveData<XmResource<List<NewsChannelBean>>> newsChannles;

    public NewsChannelVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<NewsChannelBean>>> getNewsChannles() {
        if (newsChannles == null) {
            newsChannles = new MutableLiveData<>();
        }
        return newsChannles;
    }

    public void fetchNewsChannel() {
        getNewsChannles().setValue(XmResource.<List<NewsChannelBean>>loading());
        RequestManager.newSingleton().getNewsChannel(new ResultCallback<XMResult<NewsBean>>() {
            @Override
            public void onSuccess(XMResult<NewsBean> result) {
                if (result != null && !ListUtils.isEmpty(result.getData().getChannelList())) {
                    getNewsChannles().setValue(XmResource.success(result.getData().getChannelList()));
                } else {
                    getNewsChannles().setValue(XmResource.<List<NewsChannelBean>>error(0, null));
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                getNewsChannles().setValue(XmResource.<List<NewsChannelBean>>error(code, msg));
            }
        });
    }
}
