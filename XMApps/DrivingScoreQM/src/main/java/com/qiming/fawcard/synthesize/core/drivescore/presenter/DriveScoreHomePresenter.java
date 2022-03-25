package com.qiming.fawcard.synthesize.core.drivescore.presenter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.qiming.fawcard.synthesize.R;
import com.qiming.fawcard.synthesize.base.constant.QMConstant;
import com.qiming.fawcard.synthesize.base.system.callback.DriverInfoCallback;
import com.qiming.fawcard.synthesize.base.system.service.DriverService;
import com.qiming.fawcard.synthesize.core.drivescore.contract.DriveScoreHomeContract;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryDetailEntity;
import com.qiming.fawcard.synthesize.data.entity.DriveScoreHistoryEntity;

import java.util.List;

import javax.inject.Inject;

public class DriveScoreHomePresenter implements DriveScoreHomeContract.Presenter {
    private Context mContext;
    private DriverServiceConn mDriverServiceConn;
    private DriverService.DriverBinder mBinder = null;
    private DriveScoreHomeContract.View mView;

    @Inject
    public DriveScoreHomePresenter(Context context, DriveScoreHomeContract.View view){
        this.mView = view;
        this.mContext = context;
    }

    class DriverServiceConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if(service == null){
                return;
            }
            setBinder(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBinder = null;
        }
    }

    public void setBinder(IBinder service) {
        mBinder = (DriverService.DriverBinder) service;
        mBinder.getService().setDriverInfoCallback(new DriverInfoCallback() {
            @Override
            public void dataChange(DriveScoreHistoryDetailEntity driverInfo) { // 驾驶信息变化
                if (mView != null) {
                    mView.onDriverInfoUpdate(driverInfo);
                }
            }

            @Override
            public void onDriverInfosNotify(List<DriveScoreHistoryDetailEntity> driverInfoList) { // 驾驶信息变化 列表
                if (driverInfoList.size() == 0) {
                    return;
                }

                if (mView != null) {
                    mView.onDriverInfosUpdate(driverInfoList);
                }
            }

            @Override
            public void onDriverScore(DriveScoreHistoryEntity data) { // 驾驶评分变化
                if (mView != null){
                    mView.onDriveScoreUpdate(data);
                }
            }

            @Override
            public void onRequestFailed(QMConstant.RequestFailMessage msg) {
                String message = "";
                switch (msg) {
                    case SERVER_ERROR:
                        message = mContext.getResources().getString(R.string.system_error);
                        break;
                    case NETWORK_BADSIGNAL:
                        message = mContext.getResources().getString(R.string.network_error);
                        break;
                    case NETWORK_DISCONNECT:
                        message = mContext.getResources().getString(R.string.loading_fail);
                        break;
                    default:
                        break;
                }

                if (mBinder != null) {
//                    mBinder.stopGetDriverInfo();
                }

                if (mView != null) {
                    mView.onRequestFailed(message);
                }
            }

            @Override
            public void onDriveStart() {
                if (mView != null) {
                    mView.onDriveStart();
                }
            }

            @Override
            public void onBindSuccess() {
                mView.onBindSuccess();
            }

            @Override
            public void onDriveTime(long driveTime) {
                mView.onDriveTimeUpdate(driveTime);
            }

            @Override
            public void hideLoading() {
                mView.hideLoadingView();
            }
        });
    }

    @Override
    public void bindDriverService() {
        Intent intent = new Intent(mContext, DriverService.class);
        mDriverServiceConn = new DriverServiceConn();
        mContext.bindService(intent, mDriverServiceConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void unbindDriverService() {
        if (mBinder != null) {
            mContext.unbindService(mDriverServiceConn);
        }
    }

    @Override
    public void retryGetDriveInfo(int errorCode) {
        if (mBinder != null) {
            mBinder.restartGetDriverInfo();
//            mBinder.retryRequestData();
        }
    }

    @Override
    public boolean isTravelStarting() {
        if (mBinder == null) {
           return false;
        }

        return mBinder.isStarting();
    }
}
