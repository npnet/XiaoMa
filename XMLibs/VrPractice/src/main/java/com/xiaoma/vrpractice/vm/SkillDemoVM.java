package com.xiaoma.vrpractice.vm;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.vrpractice.vm
 *  @file_name:      SkillDemoVM
 *  @author:         Rookie
 *  @create_time:    2019/6/12 17:17
 *  @description：   TODO             */

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.vrpractice.common.manager.RequestManager;

public class SkillDemoVM extends BaseViewModel {

    private MutableLiveData<XmResource<String>> delSkill;

    public SkillDemoVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<String>> getDelSkill() {
        if (delSkill == null) {
            delSkill = new MutableLiveData<>();
        }
        return delSkill;
    }

    public void delSkill(String skillId) {
        getDelSkill().setValue(XmResource.loading());
        RequestManager.getInstance().delSkill(skillId, new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                getDelSkill().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getDelSkill().setValue(XmResource.error(msg));
            }
        });
    }

}
