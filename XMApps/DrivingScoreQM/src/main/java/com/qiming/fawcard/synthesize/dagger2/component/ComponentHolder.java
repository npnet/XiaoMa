package com.qiming.fawcard.synthesize.dagger2.component;

import com.qiming.fawcard.synthesize.dagger2.module.DriveScoreHistoryDetailModule;
import com.qiming.fawcard.synthesize.dagger2.module.DriveScoreHomeModule;
import com.qiming.fawcard.synthesize.dagger2.module.DriveServiceModule;

public class ComponentHolder {
    private static ComponentHolder INSTANCE = new ComponentHolder();
    private ComponentHolder(){}

    public static ComponentHolder getInstance(){
        return INSTANCE;
    }

    public DriveScoreHomeComponent getDriveScoreHomeComponent(DriveScoreHomeModule module) {
        return DaggerDriveScoreHomeComponent.builder()
                .driveScoreHomeModule(module)
                .build();
    }

    public DriveServiceComponent getDriveServiceComponent(DriveServiceModule module) {
        return DaggerDriveServiceComponent.builder()
                .driveServiceModule(module)
                .build();
    }

    public DriveScoreHistoryDetailComponent getDriveScoreHistoryDetailComponent(DriveScoreHistoryDetailModule module) {
        return DaggerDriveScoreHistoryDetailComponent.builder()
                .driveScoreHistoryDetailModule(module)
                .build();
    }
}
