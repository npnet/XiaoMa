package com.qiming.fawcard.synthesize.dagger2.component;

import com.qiming.fawcard.synthesize.core.drivescore.DriveScoreHistoryDetailActivity;
import com.qiming.fawcard.synthesize.dagger2.module.DriveScoreHistoryDetailModule;

import dagger.Component;

@Component(modules = {DriveScoreHistoryDetailModule.class})
public interface DriveScoreHistoryDetailComponent {
    void inject(DriveScoreHistoryDetailActivity activity);
}
