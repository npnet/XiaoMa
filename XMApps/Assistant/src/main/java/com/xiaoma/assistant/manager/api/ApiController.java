package com.xiaoma.assistant.manager.api;

import android.content.Context;
import android.util.Log;
import com.xiaoma.assistant.manager.IBCallAndPhoneStateManager;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.local.Center;
import com.xiaoma.center.logic.local.StateManager;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.vr.skill.client.SkillClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by qiuboxiang on 2019/3/14 15:01
 * Desc:
 */
public class ApiController {

    private static ApiController mInstance;
    private static List<ApiManager> apiManagerList = new ArrayList<>();
    private static HashMap<String, ApiManager> remoteAppMap = new HashMap<>();
    private boolean inited = false;
    private Context context;

    private ApiController() {
    }

    public static ApiController getInstance() {
        if (mInstance == null) {
            synchronized (ApiController.class) {
                if (mInstance == null) {
                    mInstance = new ApiController();
                }
            }
        }
        return mInstance;
    }

    public void init(final Context context, int localPort) {
        Log.d("QBX", "ApiController init");
        this.context = context;
        apiManagerList.add(MusicApiManager.getInstance());
        apiManagerList.add(BluetoothPhoneApiManager.getInstance());
        apiManagerList.add(RadioApiManager.getInstance());
        apiManagerList.add(SettingApiManager.getInstance());
        apiManagerList.add(AirConditionerApiManager.getInstance());
        apiManagerList.add(VehicleConditionApiManager.getInstance());
        apiManagerList.add(CarControlApiManager.getInstance());
        apiManagerList.add(LauncherApiManager.getInstance());

        for (ApiManager apiManager : apiManagerList) {
            apiManager.init(context, localPort);
            remoteAppMap.put(apiManager.getRemoteApp(), apiManager);
        }

        StateManager.getInstance().addStateCallback(new StateManager.StateListener() {
            @Override
            public void onClientIn(SourceInfo source) {
                Log.d("QBX", "onClientIn: " + source.getLocation() + "  port=" + source.getPort());
                if (remoteAppMap.containsKey(source.getLocation())) {
                    remoteAppMap.get(source.getLocation()).onClientIn(source);
                }
            }

            @Override
            public void onClientOut(SourceInfo source) {
                if (CenterConstants.BLUETOOTH_PHONE.equals(source.getLocation())){
                    IBCallAndPhoneStateManager.getInstance().setPhoneBusyState(false);
                }
            }

            @Override
            public void onConnected() {
                Log.d("QBX", "ApiController onConnected");
                initApi();
            }

            @Override
            public void onPrepare(String depend) {
                Log.d("QBX", "Center => onConnected");
                Center.getInstance().init(context.getApplicationContext());
            }

            @Override
            public void onDisconnected() {
                Log.d("QBX", "Center => onDisconnected");
                inited = false;
                ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Center.getInstance().init(context.getApplicationContext());
                    }
                }, 5000);

            }

        });

        AudioManager.getInstance().initAudioType();
        if (Center.getInstance().isConnected()) {
            initApi();
        } else {
            Center.getInstance().init(context.getApplicationContext());
        }
    }

    private void initApi() {
        if (!inited) {
            inited = true;
            AudioManager.getInstance().init(context);
            for (ApiManager apiManager : apiManagerList) {
                apiManager.init();
            }

            Center.getInstance().register(new SkillClient(context));
        }
    }

}
