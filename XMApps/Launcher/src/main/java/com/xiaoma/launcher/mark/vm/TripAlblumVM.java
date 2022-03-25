package com.xiaoma.launcher.mark.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.launcher.common.LauncherUtils;
import com.xiaoma.launcher.common.api.LauncherAPI;
import com.xiaoma.launcher.common.manager.RequestManager;
import com.xiaoma.launcher.mark.model.MarkPhotoBean;
import com.xiaoma.login.LoginManager;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ConvertUtils;
import com.xiaoma.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import id.zelory.compressor.Compressor;

public class TripAlblumVM extends AndroidViewModel {
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private MutableLiveData<XmResource<List<MarkPhotoBean>>> mTripAlbumList;
    private MutableLiveData<XmResource<Boolean>> mPhotoUpload;
    private MutableLiveData<XmResource<String>> mUploadPhoto;



    public TripAlblumVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<MarkPhotoBean>>> getTripAlbumList() {
        if (mTripAlbumList == null) {
            mTripAlbumList = new MutableLiveData<>();
        }
        return mTripAlbumList;
    }

    public MutableLiveData<XmResource<Boolean>> getPhotoUpload() {
        if (mPhotoUpload == null) {
            mPhotoUpload = new MutableLiveData<>();
        }
        return mPhotoUpload;
    }

    public MutableLiveData<XmResource<String>> getUploadPhoto() {
        if (mUploadPhoto == null) {
            mUploadPhoto = new MutableLiveData<>();
        }
        return mUploadPhoto;
    }

    public void tripAlbum(String year, String month) {

        List<MarkPhotoBean> markPhotoBeans = new ArrayList<>();
        markPhotoBeans.clear();
        String keys[] = {YEAR, MONTH};
        String[][] values = {{year}, {month}};
        markPhotoBeans = LauncherUtils.getDBManager().queryByWhere(MarkPhotoBean.class, keys, values);
        Collections.reverse(markPhotoBeans);
        getTripAlbumList().setValue(XmResource.success(markPhotoBeans));
    }

    /**
     * 获得所有旅行信息
     */
    public void getAllTrip() {
        List<MarkPhotoBean> markPhotoBeans = new ArrayList<>();
        markPhotoBeans.clear();
        markPhotoBeans = LauncherUtils.getDBManager().queryAll(MarkPhotoBean.class);

        getTripAlbumList().setValue(XmResource.success(markPhotoBeans));
    }


    public void upPhoto(MarkPhotoBean markPhotoBean) {
        String mPath;
        String mAddress;
        String mLocation;
        String mUid;
        String mLon = markPhotoBean.getLongitude() + "";
        String mLat = markPhotoBean.getLatitude() + "";
        File compressedImageFile = null;
        if (StringUtil.isNotEmpty(markPhotoBean.getPhotoPath())) {
            mPath = markPhotoBean.getPhotoPath();
        } else {
            XMToast.showToast(getApplication(), "照片不存在");
            return;
        }
        if (StringUtil.isNotEmpty(markPhotoBean.getPhotoAddress())) {
            mAddress = markPhotoBean.getPhotoAddress();
        } else {
            mAddress = "没有定位信息";
        }
        if (StringUtil.isNotEmpty(markPhotoBean.getLocation())) {
            mLocation = markPhotoBean.getLocation();
        } else {
            mLocation = "没有定位信息";
        }
        File file = new File(mPath);
        try {
            compressedImageFile = new Compressor(getApplication()).compressToFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (LoginManager.getInstance() != null) {
            mUid = LoginManager.getInstance().getLoginUserId();
        } else {
            mUid = "0";
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("title", ConvertUtils.intToString(markPhotoBean.getFacialType()));
        params.put("city", mLocation);
        params.put("address", mAddress);
        params.put("lon", mLon);
        params.put("lat", mLat);
        params.put("uid", mUid);
        params.put("photoId", markPhotoBean.getId());
        if (StringUtil.isNotEmpty(compressedImageFile.getName())) {
            XmHttp.getDefault().upFile(LauncherAPI.MARK_UPLOAD_PHOTO, params, compressedImageFile, new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    String data = response.body();
                    JSONObject jsonObject = null;
                    String resultCode = null;
                    if (StringUtil.isNotEmpty(data)) {
                        try {
                            jsonObject = new JSONObject(data);
                            if (jsonObject.has("resultCode")) {
                                resultCode = jsonObject.getString("resultCode");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    markPhotoBean.setSaveType(true);
                    LauncherUtils.getDBManager().save(markPhotoBean);
                    if (StringUtil.isNotEmpty(resultCode)) {
                        getUploadPhoto().setValue(XmResource.success(resultCode));
                    }
                }

                @Override
                public void onError(Response<String> response) {
                    super.onError(response);
                    String data = response.body();
                    JSONObject jsonObject = null;
                    String resultCode = null;
                    if (StringUtil.isNotEmpty(data)) {
                        try {
                            jsonObject = new JSONObject(data);
                            if (jsonObject.has("resultCode")) {
                                resultCode = jsonObject.getString("resultCode");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (StringUtil.isNotEmpty(resultCode)) {
                        getUploadPhoto().setValue(XmResource.error(resultCode));
                    }
                }
            });
        } else {
            getUploadPhoto().setValue(XmResource.error("-1"));
        }
    }

    public void getPhoto(MarkPhotoBean markPhotoBean) {
        RequestManager.getInstance().getPhotoType(markPhotoBean.getId()+"", new ResultCallback<XMResult<Boolean>>() {
            @Override
            public void onSuccess(XMResult<Boolean> result) {
                if (!result.getData()){
                    markPhotoBean.setSaveType(false);
                    LauncherUtils.getDBManager().save(markPhotoBean);
                }
                getPhotoUpload().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getPhotoUpload().setValue(XmResource.error(msg));
            }
        });
    }
}
