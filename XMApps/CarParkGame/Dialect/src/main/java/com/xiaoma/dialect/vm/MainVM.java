package com.xiaoma.dialect.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.dialect.model.QuestionBean;
import com.xiaoma.model.XmResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 2018/11/7 0007
 */

public class MainVM extends BaseViewModel {

    private MutableLiveData<XmResource<List<QuestionBean>>> mQuestionList;

    public MainVM(@NonNull Application application) {
        super(application);
    }


    public MutableLiveData<XmResource<List<QuestionBean>>> getQuestionList() {
        if (mQuestionList == null) {
            mQuestionList = new MutableLiveData<>();
        }
        return mQuestionList;
    }

    public void fetchRecommendList() {
        List<String> demolist = new ArrayList<>();
        demolist.add("A.粤语");
        demolist.add("B.四川话");
        demolist.add("C.东北话");
        demolist.add("D.闽南语");
        final List<QuestionBean> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            list.add(new QuestionBean(demolist, i));
        }
        mQuestionList.setValue(XmResource.response(list));


    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mQuestionList = null;
    }
}
