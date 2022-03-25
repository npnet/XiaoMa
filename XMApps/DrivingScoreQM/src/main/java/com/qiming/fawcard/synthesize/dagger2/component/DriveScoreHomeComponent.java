package com.qiming.fawcard.synthesize.dagger2.component;

import com.qiming.fawcard.synthesize.core.drivescore.DriveScoreHomeActivity;
import com.qiming.fawcard.synthesize.dagger2.module.DriveScoreHomeModule;

import dagger.Component;

@Component(modules = {DriveScoreHomeModule.class})
public interface DriveScoreHomeComponent {
    void inject(DriveScoreHomeActivity activity);
}
