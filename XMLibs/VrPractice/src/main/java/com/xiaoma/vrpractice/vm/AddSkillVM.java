package com.xiaoma.vrpractice.vm;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.vrpractice.vm
 *  @file_name:      AddSkillVM
 *  @author:         Rookie
 *  @create_time:    2019/6/12 16:11
 *  @description：   TODO             */

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.vrpractice.common.manager.RequestManager;
import com.xiaoma.model.pratice.SkillItemBean;

import java.util.List;

public class AddSkillVM extends BaseViewModel {

    private MutableLiveData<XmResource<String>> addSkill;
    private MutableLiveData<XmResource<String>> editSkill;
    private MutableLiveData<XmResource<Boolean>> checkWord;
    private MutableLiveData<XmResource<List<SkillItemBean>>> skillItems;

    public AddSkillVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<String>> getAddSkill() {
        if (addSkill == null) {
            addSkill = new MutableLiveData<>();
        }
        return addSkill;
    }

    public MutableLiveData<XmResource<String>> getEditSkill() {
        if (editSkill == null) {
            editSkill = new MutableLiveData<>();
        }
        return editSkill;
    }

    public MutableLiveData<XmResource<Boolean>> getCheckWord() {
        if (checkWord == null) {
            checkWord = new MutableLiveData<>();
        }
        return checkWord;
    }

    public MutableLiveData<XmResource<List<SkillItemBean>>> getSkillItems() {
        if (skillItems == null) {
            skillItems = new MutableLiveData<>();
        }
        return skillItems;
    }

    public void saveSkills(String word, String itemList) {
        getAddSkill().setValue(XmResource.loading());
        RequestManager.getInstance().saveSkills(word, itemList, new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                getAddSkill().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getAddSkill().setValue(XmResource.error(msg));
            }
        });
    }

    public void editSkills(String skillId, String oldWord, String newWord, String itemList) {
        getEditSkill().setValue(XmResource.loading());
        RequestManager.getInstance().editSkills(skillId, oldWord, newWord, itemList, new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                getEditSkill().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getEditSkill().setValue(XmResource.error(msg));
            }
        });
    }

    public void fetchSkillItems() {
        getSkillItems().setValue(XmResource.loading());
        RequestManager.getInstance().fetchSkillItemList(new ResultCallback<XMResult<List<SkillItemBean>>>() {
            @Override
            public void onSuccess(XMResult<List<SkillItemBean>> result) {
                getSkillItems().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getSkillItems().setValue(XmResource.error(msg));
            }
        });
    }


}
