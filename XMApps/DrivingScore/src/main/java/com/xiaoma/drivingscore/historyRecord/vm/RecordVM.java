package com.xiaoma.drivingscore.historyRecord.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.drivingscore.historyRecord.model.DriveInfo;
import com.xiaoma.drivingscore.historyRecord.model.DriveRecordDetails;
import com.xiaoma.model.XmResource;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/1/7
 */
public class RecordVM extends AndroidViewModel {

    private MutableLiveData<XmResource<List<DriveInfo>>> mStateDriveInfoLiveData;

    public RecordVM(@NonNull Application application) {
        super(application);
    }

//    mock

    private List<DriveInfo> mockDriveInfos() {
        List<DriveInfo> driveInfoList = new ArrayList<>();
        driveInfoList.add(mockDrive("2018-10-1", "10:00"));
        driveInfoList.add(mockDrive("2018-10-1", "11:00"));
        driveInfoList.add(mockDrive("2018-10-1", "12:00"));
        driveInfoList.add(mockDrive("2018-10-1", "13:00"));
        driveInfoList.add(mockDrive("2018-10-1", "14:00"));

        driveInfoList.add(mockDrive("2018-10-2", "10:00"));
        driveInfoList.add(mockDrive("2018-10-2", "11:00"));

        driveInfoList.add(mockDrive("2018-10-3", "12:00"));
        driveInfoList.add(mockDrive("2018-10-3", "13:00"));

        driveInfoList.add(mockDrive("2018-10-4", "14:00"));

        return driveInfoList;
    }

    private DriveInfo mockDrive(String date, String time) {
        DriveInfo driveInfo = new DriveInfo();
        driveInfo.setDate(date);
        driveInfo.setTime(time);
        driveInfo.setRecordDetails(mockDetails());
        return driveInfo;
    }

    private DriveRecordDetails mockDetails() {
        DriveRecordDetails detailBean = new DriveRecordDetails();
        detailBean.setScore(20);
        detailBean.setSlowDownCount(1);
        detailBean.setSpeedUpCount(2);
        detailBean.setTurnCount(2);
        return detailBean;
    }

    public MutableLiveData<XmResource<List<DriveInfo>>> getStateDriveInfoLiveData() {
        if (mStateDriveInfoLiveData == null) {
            mStateDriveInfoLiveData = new MutableLiveData<>();
        }
        return mStateDriveInfoLiveData;
    }

    private void setDriveInfos(List<DriveInfo> driveInfoList) {
        getStateDriveInfoLiveData().setValue(XmResource.success(driveInfoList));
    }

    public void fetchDriveInfo() {
        setDriveInfos(mockDriveInfos());
    }

}
