package com.qiming.fawcard.synthesize.dagger2.module;

import android.content.Context;

import com.qiming.fawcard.synthesize.base.DriveScoreLineChartManager;
import com.qiming.fawcard.synthesize.base.dialog.LoadingFailDialog;
import com.qiming.fawcard.synthesize.base.dialog.LoadingNormalDialog;
import com.qiming.fawcard.synthesize.core.drivescore.DriveScoreHomeActivity;
import com.qiming.fawcard.synthesize.core.drivescore.contract.DriveScoreHomeContract;
import com.qiming.fawcard.synthesize.core.drivescore.presenter.DriveScoreHomePresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class DriveScoreHomeModule {
    private Context mContext;
    private DriveScoreHomeContract.View mView;
    private LoadingFailDialog.PriorityListener mListener;

    public DriveScoreHomeModule(Context context,
                                DriveScoreHomeContract.View view,
                                LoadingFailDialog.PriorityListener listener){
        mContext = context;
        mView = view;
        mListener = listener;
    }

    @Provides
    public Context provideContext(){
        return mContext;
    }

    @Provides
    public  DriveScoreHomeContract.View provideView(){
        return mView;
    }

    @Provides
    public DriveScoreHomePresenter provideDriveScoreHomePresenter(){
        return new DriveScoreHomePresenter(mContext, mView);
    }

    @Provides
    public DriveScoreLineChartManager provideDriveScoreLineChartManager(){
        DriveScoreHomeActivity homeActivity = (DriveScoreHomeActivity)mView;
        return new DriveScoreLineChartManager(mContext, homeActivity.getLineChart());
    }

    @Provides
    public LoadingNormalDialog provideLoadingNormalDialog(){
        return new LoadingNormalDialog(mContext);
    }

    @Provides
    public LoadingFailDialog provideLoadingFailDialog(){
        return new LoadingFailDialog(mContext, mListener);
    }
}
