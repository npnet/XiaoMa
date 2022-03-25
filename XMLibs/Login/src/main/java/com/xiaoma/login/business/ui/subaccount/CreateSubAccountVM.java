package com.xiaoma.login.business.ui.subaccount;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.login.common.RequestManager;
import com.xiaoma.login.common.model.CurrentDate;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.User;
import com.xiaoma.model.XMResult;
import com.xiaoma.network.callback.SimpleCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kaka
 * on 19-5-24 下午5:12
 * <p>
 * desc: #a
 * </p>
 */
public class CreateSubAccountVM extends BaseViewModel {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    public CreateSubAccountVM(@NonNull Application application) {
        super(application);
    }

    private MutableLiveData<String> mPageTag;
    private MutableLiveData<String> mPasswd;
    private MutableLiveData<String> mPhone;
    private MutableLiveData<String> mName;
    private MutableLiveData<Integer> mGender;
    private MutableLiveData<Calendar> mCurrentTime;
    private MutableLiveData<Calendar> mBirthdayTime;
    private MutableLiveData<String> mFaceId;
    private MutableLiveData<String> mKeyId;
    private MutableLiveData<Boolean> mIsBlueTooth;
    private MutableLiveData<Integer> mRelation;

    public MutableLiveData<String> getPasswd() {
        if (mPasswd == null) {
            mPasswd = new MutableLiveData<>();
        }
        return mPasswd;
    }

    public void setPasswd(String passwd) {
        getPasswd().setValue(passwd);
    }

    public MutableLiveData<String> getPhone() {
        if (mPhone == null) {
            mPhone = new MutableLiveData<>();
        }
        return mPhone;
    }

    public void setPhone(String phone) {
        getPhone().setValue(phone);
    }

    public MutableLiveData<String> getPageTag() {
        if (mPageTag == null) {
            mPageTag = new MutableLiveData<>();
        }
        return mPageTag;
    }

    public void setPageTag(String pageTag) {
        getPageTag().setValue(pageTag);
    }

    public MutableLiveData<String> getName() {
        if (mName == null) {
            mName = new MutableLiveData<>();
        }
        return mName;
    }

    public void setName(String name) {
        getName().setValue(name);
    }

    public MutableLiveData<Integer> getGender() {
        if (mGender == null) {
            mGender = new MutableLiveData<>();
        }
        return mGender;
    }

    public void setGender(int gender) {
        getGender().setValue(gender);
    }

    public MutableLiveData<Calendar> getCurrentTime() {
        if (mCurrentTime == null) {
            mCurrentTime = new MutableLiveData<>();
        }
        return mCurrentTime;
    }

    public void setCurrentTime(Calendar birthdayTime) {
        getCurrentTime().setValue(birthdayTime);
    }

    public MutableLiveData<Calendar> getBirthdayTime() {
        if (mBirthdayTime == null) {
            mBirthdayTime = new MutableLiveData<>();
        }
        return mBirthdayTime;
    }

    public void setBirthdayTime(Calendar birthdayTime) {
        getBirthdayTime().setValue(birthdayTime);
    }

    public MutableLiveData<String> getFaceId() {
        if (mFaceId == null) {
            mFaceId = new MutableLiveData<>();
        }
        return mFaceId;
    }

    public void setFaceId(String faceId) {
        getFaceId().setValue(faceId);
    }

    public MutableLiveData<String> getKeyId() {
        if (mKeyId == null) {
            mKeyId = new MutableLiveData<>();
        }
        return mKeyId;
    }

    public void setKeyId(String keyId) {
        getKeyId().setValue(keyId);
    }

    public MutableLiveData<Boolean> getIsBlueTooth() {
        if (mIsBlueTooth == null) {
            mIsBlueTooth = new MutableLiveData<>();
        }
        return mIsBlueTooth;
    }

    public void setIsBlueTooth(boolean isBlueTooth) {
        getIsBlueTooth().setValue(isBlueTooth);
    }

    public MutableLiveData<Integer> getRelation() {
        if (mRelation == null) {
            mRelation = new MutableLiveData<>();
        }
        return mRelation;
    }

    public void setRelation(int relation) {
        getRelation().setValue(relation);
    }

    public void fetchCurrentTime(final SimpleCallback<Calendar> callback) {
        RequestManager.pullServerNewDate(new ResultCallback<XMResult<CurrentDate>>() {
            @Override
            public void onSuccess(XMResult<CurrentDate> result) {
                String date = result.getData().getDate();
                if (!TextUtils.isEmpty(date)) {
                    try {
                        Date dateTime = dateFormat.parse(date);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(dateTime);
                        setCurrentTime(calendar);
                        callback.onSuccess(calendar);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        callback.onError(-1, "time format error");
                    }
                } else {
                    callback.onError(-1, "data error");
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                callback.onError(code, msg);
            }

        });
    }

    public void createSubAccount(final SimpleCallback<User> callback) {
        Boolean aBoolean = getIsBlueTooth().getValue();
        final boolean blueTooth = aBoolean == null ? false : aBoolean;
        final String keyId = getKeyId().getValue();
        final String faceId = getFaceId().getValue();
        Integer relation = getRelation().getValue();
        Calendar birthDay = getBirthdayTime().getValue();
        Calendar current = getCurrentTime().getValue();
        String age = String.valueOf(current.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR));
        String birthday = dateFormat.format(birthDay.getTime());
        Integer gender = getGender().getValue();
        String name = getName().getValue();
        RequestManager.createSubAccount(getPasswd().getValue(),
                blueTooth ? null : keyId,
                blueTooth ? keyId : null,
                faceId,
                name,
                gender,
                age,
                birthday,
                relation,
                getPhone().getValue(),
                new ResultCallback<XMResult<User>>() {
                    @Override
                    public void onSuccess(XMResult<User> result) {
                        User data = result.getData();
                        if (result.isSuccess() && data != null) {
                            if (!TextUtils.isEmpty(faceId)) {
                                data.setFaceId(Integer.valueOf(faceId));
                            }
                            if (blueTooth) {
                                data.setBluetoothKey(keyId);
                            } else {
                                data.setCommonKey(keyId);
                            }
                            callback.onSuccess(data);
                        } else {
                            callback.onError(-1, "data error");
                        }
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        callback.onError(code, msg);
                    }
                });
    }
}
