package com.xiaoma.xting.mine.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.playerSource.info.model.RecordInfo;
import com.xiaoma.xting.common.playerSource.info.model.SubscribeInfo;
import com.xiaoma.xting.common.playerSource.utils.PrintInfo;

import java.util.List;

/**
 * @author KY
 * @date 2018/10/11
 */
public class MineVM extends AndroidViewModel {

    public MineVM(@NonNull Application application) {
        super(application);

    }

    private MutableLiveData<List<RecordInfo>> mRecordsLiveData;

    public MutableLiveData<List<RecordInfo>> getRecordsLiveData() {
        if (mRecordsLiveData == null) {
            mRecordsLiveData = new MutableLiveData<>();
        }
        return mRecordsLiveData;
    }

    private void setRecordsLiveData(List<RecordInfo> value) {
        getRecordsLiveData().setValue(value);
    }

    public void getRecordFromDB() {
        setRecordsLiveData(XtingUtils.getRecordDao().selectAll());
    }

    public void clearAllRecord() {
        PrintInfo.print("PlayerInfoImpl", "Clear Record 4");
        XtingUtils.getRecordDao().clear();
        setRecordsLiveData(null);
    }

    public void delete(RecordInfo data) {
        XtingUtils.getRecordDao().delete(data);
    }

    private MutableLiveData<List<SubscribeInfo>> mSubscribesLiveData;

    public MutableLiveData<List<SubscribeInfo>> getSubscribesLiveData() {
        if (mSubscribesLiveData == null) {
            mSubscribesLiveData = new MutableLiveData<>();
        }
        return mSubscribesLiveData;
    }

    private void setSubscribesLiveData(List<SubscribeInfo> value) {
        getSubscribesLiveData().setValue(value);
    }

    public void getSubscribeFromDB() {
        setSubscribesLiveData(XtingUtils.getSubscribeDao().selectAll());
    }

    public void delete(SubscribeInfo subscribeInfo) {
        XtingUtils.getSubscribeDao().delete(subscribeInfo);
//        getSubscribeFromDB();
    }

    public void clearAllSubscribe() {
        XtingUtils.getSubscribeDao().clear();
        setSubscribesLiveData(null);
    }

    public boolean isSubscribesEmpty() {
        return XtingUtils.getSubscribeDao().count() == 0;
    }

    public boolean isRecordsEmpty() {
        return XtingUtils.getRecordDao().count() == 0;
    }

}
