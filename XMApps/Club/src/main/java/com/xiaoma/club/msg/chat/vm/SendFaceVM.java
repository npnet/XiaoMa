package com.xiaoma.club.msg.chat.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;

import java.util.List;

/**
 * Author: loren
 * Date: 2019/1/14 0014
 */

public class SendFaceVM extends BaseViewModel {

    private MutableLiveData<List<String>> faceUrls;

    public SendFaceVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<String>> getFaceUrls() {
        if (faceUrls == null) {
            faceUrls = new MutableLiveData<>();
        }
        return faceUrls;
    }
}
