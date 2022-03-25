package com.xiaoma.vrpractice.vm;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.vrpractice.vm
 *  @file_name:      MainSkillVM
 *  @author:         Rookie
 *  @create_time:    2019/6/12 15:28
 *  @description：   TODO             */

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.vrpractice.common.manager.RequestManager;
import com.xiaoma.model.pratice.SkillBean;

import java.util.List;

public class MainSkillVM extends BaseViewModel {

    private MutableLiveData<XmResource<List<SkillBean>>> skillsData;

    public MainSkillVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<SkillBean>>> getSkillsData() {
        if (skillsData == null) {
            skillsData = new MutableLiveData<>();
        }
        return skillsData;
    }

    public void fetchSkills() {
        getSkillsData().setValue(XmResource.loading());
        RequestManager.getInstance().fetchSkillList(new ResultCallback<XMResult<List<SkillBean>>>() {

            @Override
            public void onSuccess(XMResult<List<SkillBean>> result) {
                getSkillsData().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getSkillsData().setValue(XmResource.failure(msg));
            }
        });
    }

}
