package com.qiming.fawcard.synthesize.dagger2.module;

import com.qiming.fawcard.synthesize.base.HttpManager;
import com.qiming.fawcard.synthesize.base.system.broadcast.DriveScoreBroadcastReceiver;
import com.qiming.fawcard.synthesize.core.drivescore.contract.DriveScoreContract;
import com.qiming.fawcard.synthesize.core.drivescore.presenter.DriveScorePresenter;
import com.qiming.fawcard.synthesize.data.source.remote.IDrivedApi;

import dagger.Module;
import dagger.Provides;

@Module
public class DriveServiceModule {
    private DriveScoreContract.Service mService;

    public DriveServiceModule(DriveScoreContract.Service service){
        mService = service;
    }

    @Provides
    public DriveScorePresenter provideDriveScorePresenter(){
        return new DriveScorePresenter(mService, HttpManager.getInstance().create(IDrivedApi.class));
    }

    @Provides
    public DriveScoreBroadcastReceiver provideDriveScoreBroadcastReceiver(){
        return new DriveScoreBroadcastReceiver();
    }
}
