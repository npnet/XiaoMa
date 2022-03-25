package com.xiaoma.music.common.util;

import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.ICarEvent;
import com.xiaoma.carlib.model.CarEvent;
import com.xiaoma.music.UsbMusicFactory;
import com.xiaoma.utils.log.KLog;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/8/5 0005 21:16
 *   desc:   ACC off/on  事件处理
 * </pre>
 */
public class AccOffOnSignal implements ICarEvent {

    private static final String TAG = AccOffOnSignal.class.getSimpleName();

    public static AccOffOnSignal getInstance() {
        return Holder.ACC_OFF_ON_SIGNAL;
    }

    @Override
    public void onCarEvent(CarEvent event) {
        if (event == null) {
            KLog.d(TAG, "Event is null");
            return;
        }

        KLog.d(TAG, "Event id --> " + event.id);

        if (event.id == SDKConstants.ID_WORK_MODE_STATUS) {
            int value = (int) event.value;
            KLog.d(TAG, "Event value --> " + event.value);

            if (SDKConstants.WorkMode.NORMAL == value) {
                UsbMusicFactory.getUsbPlayerProxy().stop();
            } else if (SDKConstants.WorkMode.STANDBY == value) {
                UsbMusicFactory.getUsbPlayerProxy().continuePlayOrPlayFirst();
            }
        }
    }

    private static class Holder {
        private static final AccOffOnSignal ACC_OFF_ON_SIGNAL = new AccOffOnSignal();
    }
}
