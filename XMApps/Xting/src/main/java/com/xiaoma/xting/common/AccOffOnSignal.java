package com.xiaoma.xting.common;

import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.ICarEvent;
import com.xiaoma.carlib.model.CarEvent;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.control.IPlayerControl;
import com.xiaoma.xting.common.playerSource.control.controlImpl.YQRadioControl;
import com.xiaoma.xting.common.playerSource.info.sharedPref.SharedPrefUtils;
import com.xiaoma.xting.local.model.BaseChannelBean;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/8/5 0005 21:16
 *   desc:   ACC off/on  事件处理
 * </pre>
 */
public class AccOffOnSignal implements ICarEvent {

    private static final String TAG = AccOffOnSignal.class.getSimpleName();
    private boolean isInterruptbyAcc;

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

            IPlayerControl playerControl = PlayerSourceFacade.newSingleton().getPlayerControl();
            if (playerControl instanceof YQRadioControl
                    && SDKConstants.WorkMode.STANDBY == value) {
                playerControl.exitPlayer();
                isInterruptbyAcc = true;
            } else if (SDKConstants.WorkMode.NORMAL == value) {
                if (isInterruptbyAcc) {
                    isInterruptbyAcc = false;
                    BaseChannelBean lastYQPlayerInfo = SharedPrefUtils.getLastYQPlayerInfo(true);
                    PlayerSourceFacade.newSingleton().getPlayerControl(PlayerSourceType.RADIO_YQ)
                            .playWithModel(lastYQPlayerInfo);
                }
            }
        }
    }

    private static class Holder {
        private static final AccOffOnSignal ACC_OFF_ON_SIGNAL = new AccOffOnSignal();
    }
}
