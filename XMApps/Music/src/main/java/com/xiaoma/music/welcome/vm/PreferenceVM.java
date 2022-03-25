package com.xiaoma.music.welcome.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.music.R;
import com.xiaoma.music.common.constant.MusicConstants;
import com.xiaoma.music.common.manager.RequestManager;
import com.xiaoma.music.welcome.model.PreferenceBean;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.List;

/**
 * <des>
 *
 * @author Jir
 * @date 2018/10/8
 */
public class PreferenceVM extends AndroidViewModel {
    public static final String SIGN_COMMA = ",";
    private MutableLiveData<XmResource<List<PreferenceBean>>> mPreferenceTypes;
    private MutableLiveData<String> mPreferenceSettingFeedback;

    public PreferenceVM(@NonNull Application application) {
        super(application);
    }

    public void getPreferenceTypes() {
        getPreferencesContainer().setValue(XmResource.<List<PreferenceBean>>loading());
        RequestManager.getInstance().getAllMusicPreference(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                KLog.d("onSuccess: " + response.body());
                XMResult<List<PreferenceBean>> result = GsonHelper.fromJson(response.body(), new TypeToken<XMResult<List<PreferenceBean>>>() {
                }.getType());
                if (result == null || !result.isSuccess()) {
                    getPreferencesContainer().setValue(XmResource.<List<PreferenceBean>>error(getApplication().getString(R.string.data_empty_music)));
                    KLog.e("onFailed: Preference result is empty");
                    return;
                }
                List<PreferenceBean> preferences = result.getData();
                if (preferences == null || preferences.isEmpty()) {
                    getPreferencesContainer().setValue(XmResource.<List<PreferenceBean>>failure(getApplication().getString(R.string.data_empty_music)));
                    KLog.e("onFailed: Preference result is empty");
                    return;
                }
                setPreferenceTypes(preferences);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                getPreferencesContainer().setValue(XmResource.<List<PreferenceBean>>error(response.message()));
            }
        });
    }

    private void setPreferenceTypes(List<PreferenceBean> list) {
        getPreferencesContainer().setValue(XmResource.response(list));
    }

    public MutableLiveData<XmResource<List<PreferenceBean>>> getPreferencesContainer() {
        if (mPreferenceTypes == null) {
            mPreferenceTypes = new MutableLiveData<>();
        }
        return mPreferenceTypes;
    }

    public void settingPreference(List<PreferenceBean> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (PreferenceBean bean : list) {
            stringBuilder.append(bean.getThridId())
                    .append(SIGN_COMMA);
        }
        if (stringBuilder.length() == 0) {
            mPreferenceSettingFeedback.postValue(getApplication().getString(R.string.settting_failed));
            return;
        }
        String tags = stringBuilder.substring(0, stringBuilder.length() - 1);
        KLog.d("addUserMusicPreference: " + tags);
        RequestManager.getInstance().addUserMusicPreference(tags, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                KLog.d("onSuccess: " + response.body());
                XMResult result = GsonHelper.fromJson(response.body(), new TypeToken<XMResult>() {
                }.getType());
                if (result == null || !result.isSuccess()) {
                    KLog.e("onFailed: Preference commit failed");
                    mPreferenceSettingFeedback.postValue(getApplication().getString(R.string.settting_failed));
                    TPUtils.putList(getApplication(), MusicConstants.TP_MUSIC_SELECT_TAGS, list);
                    return;
                }
                TPUtils.remove(getApplication(), MusicConstants.TP_MUSIC_SELECT_TAGS);
                mPreferenceSettingFeedback.postValue(getApplication().getString(R.string.setting_success));
            }
        });
    }

    public MutableLiveData<String> getPreferenceSettingFeedback() {
        if (mPreferenceSettingFeedback == null) {
            mPreferenceSettingFeedback = new MutableLiveData<>();
        }
        return mPreferenceSettingFeedback;
    }

    public List<PreferenceBean> getSelectedTags() {
        return TPUtils.getList(getApplication(), MusicConstants.TP_MUSIC_SELECT_TAGS, PreferenceBean[].class);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mPreferenceTypes = null;
    }
}
