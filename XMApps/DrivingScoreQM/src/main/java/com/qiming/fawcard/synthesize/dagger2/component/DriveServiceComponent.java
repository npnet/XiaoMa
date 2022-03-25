package com.qiming.fawcard.synthesize.dagger2.component;


import com.qiming.fawcard.synthesize.base.system.service.DriverService;
import com.qiming.fawcard.synthesize.dagger2.module.DriveServiceModule;

import dagger.Component;

@Component(modules = {DriveServiceModule.class})
public interface DriveServiceComponent {
    void  inject(DriverService service);
}
