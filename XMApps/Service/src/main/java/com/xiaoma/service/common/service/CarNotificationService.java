package com.xiaoma.service.common.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.service.R;
import com.xiaoma.service.common.manager.CarDataManager;
import com.xiaoma.service.common.manager.CarServiceNotificationHelper;
import com.xiaoma.service.common.manager.RequestManager;
import com.xiaoma.service.main.model.MaintenancePeriodBean;
import com.xiaoma.utils.log.KLog;

/**
 * @author taojin
 * @date 2019/4/3
 */
public class CarNotificationService extends Service {
    private static final String TAG = "CarNotificationService";
    private int NEED_MAINTENANCE_STATUS = 0;
    private final String ACTION = "com.xiaoma.service.period.action";
    private final String EXTRA_PERIOD_DATA = "period_data";

    @Override
    public void onCreate() {
        super.onCreate();
        getPeriodData();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void getPeriodData() {
        RequestManager.getInstance().getFirstPageShow(CarDataManager.getInstance().getVinInfo(), new ResultCallback<XMResult<MaintenancePeriodBean>>() {
            @Override
            public void onSuccess(XMResult<MaintenancePeriodBean> result) {
                KLog.d(TAG, "onSuccess");
                if (result == null) {
                    return;
                }
                MaintenancePeriodBean data = result.getData();
                if (data.getRemainingKM() == NEED_MAINTENANCE_STATUS || data.getRemainingTime() == NEED_MAINTENANCE_STATUS) {
                    //发现有n项保养项
                    CarServiceNotificationHelper.getInstance().handleCarServiceNotification(getApplicationContext(), getString(R.string.you_need_maintain), String.format(getString(R.string.maintain_count_text), data.getUpkeepSize()));
                    sendBroadCastToLauncher(true);
                } else {
                    sendBroadCastToLauncher(false);
                }

            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }

    private void sendBroadCastToLauncher(boolean isNeedPeriod) {
        Intent intent = new Intent();
        intent.setAction(ACTION);
        intent.putExtra(EXTRA_PERIOD_DATA, isNeedPeriod);
        sendBroadcast(intent);
    }
}
