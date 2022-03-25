package com.xiaoma.xting.welcome.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.tputils.TPUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.RequestManager;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.welcome.consract.FirstInAppStatus;
import com.xiaoma.xting.welcome.model.PreferenceBean;

import java.util.List;

/**
 * <des>
 *
 * @author Jir
 * @date 2018/10/8
 */
public class PreferenceVM extends AndroidViewModel {

    public static final String SIGN_COMMA = ",";

    private MutableLiveData<XmResource<List<PreferenceBean>>> mPreferenceTags;
    private MutableLiveData<String> mPreferenceSettingFeedback;

    public PreferenceVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<PreferenceBean>>> getPreferencesContainer() {
        if (mPreferenceTags == null) {
            mPreferenceTags = new MutableLiveData<>();
        }
        return mPreferenceTags;
    }

    public MutableLiveData<String> getPreferenceSettingFeedback() {
        if (mPreferenceSettingFeedback == null) {
            mPreferenceSettingFeedback = new MutableLiveData<>();
        }
        return mPreferenceSettingFeedback;
    }

    public void fetchPreferenceTags() {
        getPreferencesContainer().postValue(XmResource.<List<PreferenceBean>>loading());
        RequestManager.requestFMPreferenceTags(new ResultCallback<XMResult<List<PreferenceBean>>>() {
            @Override
            public void onSuccess(XMResult<List<PreferenceBean>> result) {
                if (result != null) {
                    if (!ListUtils.isEmpty(result.getData())) {
                        setPreferenceTypes(result.getData());
                        return;
                    }
                }
                getPreferencesContainer().setValue(XmResource.<List<PreferenceBean>>failure(XtingConstants.EMPTY_DATA));
            }

            @Override
            public void onFailure(int code, String msg) {
//                if (NetworkUtils.isConnected(getApplication())) {
//                    getPreferencesContainer().setValue(XmResource.<List<PreferenceBean>>failure(XtingConstants.EMPTY_DATA));
//                } else {
                    getPreferencesContainer().postValue(XmResource.<List<PreferenceBean>>error(msg));
//                }
            }
        });

    }

    private void setPreferenceTypes(List<PreferenceBean> list) {
//        if (!ListUtils.isEmpty(list)) {
//            compareTagsWithLocalSaved(list);
//        }
        getPreferencesContainer().setValue(XmResource.success(list));
    }

    private void compareTagsWithLocalSaved(List<PreferenceBean> list) {
        List<PreferenceBean> selectedTags = getSelectedTags();
        if (!ListUtils.isEmpty(selectedTags)) {
            for (PreferenceBean selectedTag : selectedTags) {
                int id = selectedTag.getId();
                for (PreferenceBean netTags : list) {
                    if (netTags.getId() == id) {
                        netTags.setEnableStatus(PreferenceBean.TAG_SELECTED);
                        break;
                    }
                }
            }
        }
    }

    public void sendSelectedPreferences(final List<PreferenceBean> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (PreferenceBean bean : list) {
            stringBuilder.append(bean.getId())
                    .append(SIGN_COMMA);
        }
        RequestManager.uploadFMPreferenceTags(stringBuilder.toString(), new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                TPUtils.remove(getApplication(), XtingConstants.TP_XTING_SELECT_TAGS);
                TPUtils.put(getApplication(), XtingConstants.TP_FIRST_START_APP, FirstInAppStatus.FM_PAGE_FIRST);
                getPreferenceSettingFeedback().postValue(getApplication().getString(R.string.setting_success));
            }

            @Override
            public void onFailure(int code, String msg) {
                getPreferenceSettingFeedback().postValue(getApplication().getString(R.string.settting_failed));
            }
        });
    }

    public List<PreferenceBean> getSelectedTags() {
        return TPUtils.getList(getApplication(), XtingConstants.TP_XTING_SELECT_TAGS, PreferenceBean[].class);
    }
}
