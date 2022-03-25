package com.xiaoma.instructiondistribute;

import android.app.Application;

import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.instructiondistribute.xkan.common.manager.UsbMediaDataManager;
import com.xiaoma.skin.manager.XmSkinManager;

public class InstructionDistribute extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initUsb();
        initSkin();
        initCar();
    }

    private void initCar() {
        XmCarFactory.init(this);
    }

    private void initSkin() {
        XmSkinManager.getInstance().initSkin(this);
    }

    private void initUsb() {
        UsbMediaDataManager.getInstance().init(this);
    }

}
