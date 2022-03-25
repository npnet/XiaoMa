package com.xiaoma.shop.sdk;

import com.xiaoma.shop.sdk.sound.IVehicleSoundController;
import com.xiaoma.shop.sdk.sound.VehicleSoundControllerImpl;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/6 0006 11:27
 *   desc:   推送资源调度器
 * </pre>
 */
public class PublishDispatcher {

    private IVehicleSoundController vehicleSoundController = new VehicleSoundControllerImpl();


    private PublishDispatcher() {
    }

    public static PublishDispatcher getInstance() {
        return Holder.PUBLISH_DISPATCHER;
    }


    public void publishSound(String filePath, String tag, PublishResultCallback callback) {
        vehicleSoundController.publish(filePath, tag, callback);
    }


    public void upgrade() {
        vehicleSoundController.upgrade();
    }


    public String getSoundTag() {
        return vehicleSoundController.getCurrentTag();
    }


    public boolean checkAllowUpdate() {
        return vehicleSoundController.checkAllowUpdate();
    }


    private static class Holder {
        private static final PublishDispatcher PUBLISH_DISPATCHER = new PublishDispatcher();
    }


}
