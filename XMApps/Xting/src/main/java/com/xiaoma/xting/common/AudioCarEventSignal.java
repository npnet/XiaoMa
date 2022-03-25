package com.xiaoma.xting.common;

import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.ICarEvent;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.carlib.model.CarEvent;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/8/21 0021 14:49
 *   desc:   接收倒车信号  关闭语音搜索
 * </pre>
 */
public class AudioCarEventSignal implements ICarEvent {


    private ReversingCallback reversingCallback;


    private AudioCarEventSignal() {

    }

    private static class Holder {
        private static final AudioCarEventSignal AUDIO_CAR_EVENT_SIGNAL = new AudioCarEventSignal();
    }


    public static AudioCarEventSignal getInstance() {
        return Holder.AUDIO_CAR_EVENT_SIGNAL;
    }


    public void setReversingCallback(ReversingCallback callback) {
        this.reversingCallback = callback;
    }


    @Override
    public void onCarEvent(CarEvent event) {
        if (event == null) {
            return;
        }

        if (SDKConstants.ID_CAMERA_STATUS == event.id && XmCarVendorExtensionManager.getInstance().getCameraStatus()) {
            if (reversingCallback != null) {
                reversingCallback.onReversing();
            }
        }
    }


    public interface ReversingCallback {
        void onReversing();
    }

}
