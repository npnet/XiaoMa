package com.xiaoma.assistant.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.xiaoma.assistant.scenarios.ScenarioDispatcher;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LatLng;
import com.xiaoma.mapadapter.model.LocationInfo;

/**
 * Created by qiuboxiang on 2019/8/22 0:24
 * Desc:
 */
public class NaviServiceManager {

    private static NaviServiceManager mInstance;
    private Context context;

    public static NaviServiceManager getInstance() {
        if (mInstance == null) {
            synchronized (NaviServiceManager.class) {
                if (mInstance == null) {
                    mInstance = new NaviServiceManager();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        this.context = context;
        registerReceiver();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CenterConstants.NAVIGATE_TO_GAS_STATION);
        filter.addAction(CenterConstants.NAVIGATE_TO_COFFEE_HOUSE);
        filter.addAction(CenterConstants.NAVIGATE_TO_RESTING_AREA);
        filter.addAction(CenterConstants.NAVIGATE_TO_SERVICE_AREA);
        context.registerReceiver(receiver, filter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;
            switch (action) {
                case CenterConstants.NAVIGATE_TO_GAS_STATION:
                    startNavi(CenterConstants.GAS_STATION);
                    break;
                case CenterConstants.NAVIGATE_TO_COFFEE_HOUSE:
                    startNavi(CenterConstants.COFFEE_HOUSE);
                    break;
                case CenterConstants.NAVIGATE_TO_RESTING_AREA:
                    startNavi(CenterConstants.RESTING_AREA);
                    break;
                case CenterConstants.NAVIGATE_TO_SERVICE_AREA:
                    startNavi(CenterConstants.SERVICE_AREA);
                    break;
            }
        }
    };

    private void startNavi(String poi) {
        LatLng latLng = LocationManager.getInstance().getCurrentPosition();
        if (latLng == null) {
            latLng = new LatLng(LocationInfo.getDefault().getLatitude(), LocationInfo.getDefault().getLongitude());
        }
        AssistantManager.getInstance().showWithoutListening();
        ScenarioDispatcher.getInstance().getIatNaviScenario(context).searchNearByKey(poi, latLng.getLatitude(), latLng.getLongitude());
    }

}
