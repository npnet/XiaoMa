package com.xiaoma.setting.other.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by Administrator on 2018/10/26 0026.
 */

public class SystemInfoVM extends AndroidViewModel{

    private static final String TAG = "SystemInfoVM";
    private Context mContext;
    private MutableLiveData<Integer> mDescribeContents;
    private MutableLiveData<String> mContent;
    private MutableLiveData<String> mDownloadLink;
    private MutableLiveData<String> mVersion;

    public SystemInfoVM(@NonNull Application application) {
        super(application);
        mContext = application;
    }

    public MutableLiveData<Integer> getDescribeContents(){
        if (mDescribeContents == null){
            mDescribeContents = new MutableLiveData<>();
        }
        return mDescribeContents;
    }

    public MutableLiveData<String> getContent(){
        if (mContent == null){
            mContent = new MutableLiveData<>();
        }
        return mContent;
    }

    public MutableLiveData<String> getDownloadLink(){
        if (mDownloadLink == null){
            mDownloadLink = new MutableLiveData<>();
        }
        return mDownloadLink;
    }

    public MutableLiveData<String> getVersion(){
        if (mVersion == null){
            mVersion = new MutableLiveData<>();
        }
        return mVersion;
    }

}
