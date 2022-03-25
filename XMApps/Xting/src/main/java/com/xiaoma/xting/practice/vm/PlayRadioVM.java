package com.xiaoma.xting.practice.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.pratice.PlayRadioBean;
import com.xiaoma.model.pratice.RadioBean;
import com.xiaoma.xting.practice.manager.RequestManager;

import java.util.List;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/06/11
 *     desc   :
 * </pre>
 */
public class PlayRadioVM extends BaseViewModel {

    private MutableLiveData<XmResource<List<PlayRadioBean>>> playRadios;

    public PlayRadioVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<PlayRadioBean>>> getPlayRadios() {
        if (playRadios == null) {
            playRadios = new MutableLiveData<>();
        }
        return playRadios;
    }

    public void fetchPlayRadioList(String title) {
        getPlayRadios().setValue(XmResource.loading());
        RequestManager.getInstance().getRadioList(title, new ResultCallback<XMResult<RadioBean>>() {
            @Override
            public void onSuccess(XMResult<RadioBean> result) {
                RadioBean data = result.getData();
                if (data == null) {
                    getPlayRadios().setValue(XmResource.error(result.getResultCode(),
                            result.getResultMessage()));
                    return;
                }
                getPlayRadios().setValue(XmResource.success(data.getList()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getPlayRadios().setValue(XmResource.failure(msg));
            }
        });
    }
}
