package com.xiaoma.assistant.manager.api;

import android.os.Bundle;
import android.os.RemoteException;

import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.utils.log.KLog;

/**
 * @Author ZiXu Huang
 * @Data 2019/3/6
 */
public class CarControlApiManager extends ApiManager {
    private static CarControlApiManager instance;

    private CarControlApiManager(){}

    public static CarControlApiManager getInstance(){
        if (instance == null) {
            synchronized (CarControlApiManager.class) {
                if (instance == null) {
                    instance = new CarControlApiManager();
                }
            }
        }
        return instance;
    }

    @Override
    public void initRemote() {
        super.initRemote(CenterConstants.CAR_CONTROL, CenterConstants.CAR_CONTROL_PORT);
    }

    public void skyWindowControl(boolean isOpen){
        Bundle bundle = new Bundle();
        bundle.putBoolean(CenterConstants.CarControlThirdBundleKey.IS_OPEN_SKY_WINDOW, isOpen);
        KLog.d("skyWindowControl: " + isOpen);
        request(CenterConstants.CarControlThirdAction.SKY_WINDOW_CONTROL, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                boolean isSuccess = response.getExtra().getBoolean(CenterConstants.CarControlThirdBundleKey.IS_OPEN_SKY_WINDOW__RESULT);
                if (isSuccess) {

                }
            }
        });
    }

    public void carWindowControl(boolean isOpen){
        Bundle bundle = new Bundle();
        bundle.putBoolean(CenterConstants.CarControlThirdBundleKey.IS_OPEN_CAR_WINDOW, isOpen);
        KLog.d("carWindowControl: " + isOpen);
        request(CenterConstants.CarControlThirdAction.CAR_WINDOW_CONTROL, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                boolean isSuccess = response.getExtra().getBoolean(CenterConstants.CarControlThirdBundleKey.IS_OPEN_CAR_WINDOW_RESULT);
                if (isSuccess) {

                }
            }
        });
    }

    public  void sunShadeControl(boolean isOpen){
        Bundle bundle = new Bundle();
        bundle.putBoolean(CenterConstants.CarControlThirdBundleKey.IS_OPEN_SUN_SHADOW, isOpen);
        KLog.d("sunShadeControl: " + isOpen);
        request(CenterConstants.CarControlThirdAction.SUN_SHADOW_CONTROL, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                boolean isSuccess = response.getExtra().getBoolean(CenterConstants.CarControlThirdBundleKey.IS_OPEN_SUN_SHADOW_RESULT);
                if (isSuccess) {

                }
            }
        });
    }

    public void ambientLightControl(boolean isOpen){
        Bundle bundle = new Bundle();
        bundle.putBoolean(CenterConstants.CarControlThirdBundleKey.IS_OPEN_AMBIENT_LIGHT, isOpen);
        KLog.d("ambientLightControl: " + isOpen);
        request(CenterConstants.CarControlThirdAction.AMBIENT_LIGHT_CONTROL, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                boolean isSuccess = response.getExtra().getBoolean(CenterConstants.CarControlThirdBundleKey.IS_OPEN_AMBIENT_LIGHT_RESULT);
                if (isSuccess) {

                }
            }
        });
    }

    public void trunkControl(boolean isOpen){
        Bundle bundle = new Bundle();
        bundle.putBoolean(CenterConstants.CarControlThirdBundleKey.IS_OPEN_TRUNK, isOpen);
        KLog.d("trunkControl: " + isOpen);
        request(CenterConstants.CarControlThirdAction.TRUNK_CONTROL, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                boolean isSuccess = response.getExtra().getBoolean(CenterConstants.CarControlThirdBundleKey.IS_OPEN_TRUNK_RESULT);
                if (isSuccess) {

                }
            }
        });
    }

    public void reverseImageControl(boolean isOpen){
        Bundle bundle = new Bundle();
        bundle.putBoolean(CenterConstants.CarControlThirdBundleKey.IS_OPEN_REVERSE_IMAGE, isOpen);
        KLog.d("reverseImageControl: " + isOpen);
        request(CenterConstants.CarControlThirdAction.REVER_IMAGE_CONTROL, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                boolean isSuccess = response.getExtra().getBoolean(CenterConstants.CarControlThirdBundleKey.IS_OPEN_REVERSE_IMAGE_RESULT);
                if (isSuccess) {

                }
            }
        });
    }

    public void wiperControl(boolean isOpen){
        Bundle bundle = new Bundle();
        bundle.putBoolean(CenterConstants.CarControlThirdBundleKey.IS_OPEN_WIPER, isOpen);
        KLog.d("wiperControl: " + isOpen);
        request(CenterConstants.CarControlThirdAction.WIPER_CONTROL, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                boolean isSuccess = response.getExtra().getBoolean(CenterConstants.CarControlThirdBundleKey.IS_OPEN_WIPER_RESULT);
                if (isSuccess) {

                }
            }
        });
    }

    public void galleryLightControl(boolean isOpen){
        Bundle bundle = new Bundle();
        bundle.putBoolean(CenterConstants.CarControlThirdBundleKey.IS_OPEN_GALLERY_LIGHT, isOpen);
        KLog.d("galleryLightControl: " + isOpen);
        request(CenterConstants.CarControlThirdAction.GALLERY_LIGHT_CONTROL, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                boolean isSuccess = response.getExtra().getBoolean(CenterConstants.CarControlThirdBundleKey.IS_OPEN_GALLERY_LIGHT_RESULT);
                if (isSuccess) {

                }
            }
        });
    }

    public void panoramic360Control(boolean isOpen){
        Bundle bundle = new Bundle();
        bundle.putBoolean(CenterConstants.CarControlThirdBundleKey.IS_OPEN_PANORAMIC_360, isOpen);
        KLog.d("panoramic360Control: " + isOpen);
        request(CenterConstants.CarControlThirdAction.PANORANIC_360_CONTROL, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                boolean isSuccess = response.getExtra().getBoolean(CenterConstants.CarControlThirdBundleKey.IS_OPEN_PANORAMIC_360_RESULT);
                if (isSuccess) {

                }
            }
        });
    }

    public void switchCameraControl(CenterConstants.SwitchCamera operate){
        Bundle bundle = new Bundle();
        bundle.putSerializable(CenterConstants.CarControlThirdBundleKey.CAMERA_SWITCH, operate);
        KLog.d("switchCameraControl: " + operate.name());
        request(CenterConstants.CarControlThirdAction.SWITCH_CAMERAL_CONTROL, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                boolean isSuccess = response.getExtra().getBoolean(CenterConstants.CarControlThirdBundleKey.CAMERA_SWITCH_RESULT);
                if (isSuccess) {

                }
            }
        });
    }

    public void switchCameraModelControl(CenterConstants.SwitchCameraModel operate){
        Bundle bundle = new Bundle();
        bundle.putSerializable(CenterConstants.CarControlThirdBundleKey.CAMERA_MODEL_SWITCH, operate);
        KLog.d("switchCameraModelControl: " + operate.name());
        request(CenterConstants.CarControlThirdAction.CAR_WINDOW_CONTROL, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                boolean isSuccess = response.getExtra().getBoolean(CenterConstants.CarControlThirdBundleKey.CAMERA_MODEL_SWITCH_RESULT);
                if (isSuccess) {

                }
            }
        });
    }
}
