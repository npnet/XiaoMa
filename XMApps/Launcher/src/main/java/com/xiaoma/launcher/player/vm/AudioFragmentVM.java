package com.xiaoma.launcher.player.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.launcher.common.manager.RequestManager;
import com.xiaoma.launcher.player.model.AudioInfoBean;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.tputils.TPUtils;

/**
 * Created by zhushi.
 * Date: 2019/1/14
 */
public class AudioFragmentVM extends AndroidViewModel {
    public static final String CACHE_AUDIO_MAIN_KEY = "cache_audio_main_key";
    private MutableLiveData<XmResource<AudioInfoBean>> mAudioList;

    public AudioFragmentVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<AudioInfoBean>> getAudioList() {
        if (mAudioList == null) {
            mAudioList = new MutableLiveData<>();
        }
        return mAudioList;
    }

    /**
     * 获取桌面音频数据
     */
    public void fetchLauncherAudioList(boolean showLoading) {
        RequestManager.getInstance().getLauncherAudioList(new ResultCallback<XMResult<AudioInfoBean>>() {
            @Override
            public void onSuccess(XMResult<AudioInfoBean> result) {
                AudioInfoBean data = result.getData();
                getAudioList().setValue(XmResource.success(data));
                ThreadDispatcher.getDispatcher().post(new Runnable() {
                    @Override
                    public void run() {
                        TPUtils.put(getApplication(), CACHE_AUDIO_MAIN_KEY, GsonHelper.toJson(data));
                    }
                });
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mAudioList = null;
    }
}
