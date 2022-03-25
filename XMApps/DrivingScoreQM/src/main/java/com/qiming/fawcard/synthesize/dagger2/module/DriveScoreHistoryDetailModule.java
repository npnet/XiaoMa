package com.qiming.fawcard.synthesize.dagger2.module;

import android.content.Context;

import com.qiming.fawcard.synthesize.base.DriveScoreLineChartManager;
import com.qiming.fawcard.synthesize.core.drivescore.DriveScoreHistoryDetailActivity;
import com.qiming.fawcard.synthesize.core.drivescore.contract.DriveScoreHistoryDetailContract;
import com.qiming.fawcard.synthesize.core.drivescore.presenter.DriveScoreHistoryDetailPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class DriveScoreHistoryDetailModule {
    private Context mContext;
    private DriveScoreHistoryDetailContract.View mView;

    public DriveScoreHistoryDetailModule(Context context,
                                         DriveScoreHistoryDetailContract.View view){
        mContext = context;
        mView = view;
    }

    @Provides
    public Context provideContext(){
        return mContext;
    }

    @Provides
    public  DriveScoreHistoryDetailContract.View provideView(){
        return mView;
    }

    @Provides
    public DriveScoreHistoryDetailContract.Presenter provideDriveScoreHistoryDetailPresenter(){
        return new DriveScoreHistoryDetailPresenter(mContext, mView);
    }

    @Provides
    public DriveScoreLineChartManager provideDriveScoreLineChartManager(){
        DriveScoreHistoryDetailActivity activity = (DriveScoreHistoryDetailActivity)mView;

        return new DriveScoreLineChartManager(mContext, activity.getLineChart());
    }
}
