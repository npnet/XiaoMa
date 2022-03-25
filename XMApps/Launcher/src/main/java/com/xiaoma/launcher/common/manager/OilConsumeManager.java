package com.xiaoma.launcher.common.manager;

import android.util.Log;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.launcher.main.manager.CarSettingManager;
import com.xiaoma.login.LoginManager;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.ThreadDispatcher;


/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/06/04
 *     desc   :
 * </pre>
 */

public class OilConsumeManager {

    public static final String TAG="[OilConsumeManager]";
    private final long CYCLE_GET_WEATHER_TIME = 60 * 60 * 1000;
    private static OilConsumeManager instance;

    private OilConsumeManager() {
    }

    public static OilConsumeManager getInstance() {
        if (instance == null) {
            synchronized (OilConsumeManager.class) {
                if (instance == null) {
                    instance = new OilConsumeManager();
                }
            }
        }
        return instance;
    }

    public void init() {
        uploadStart(5000);
    }

    private void uploadStart(long delayTime){
        Log.i(TAG,"uploadStart()");
        ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
            @Override
            public void run() {
                uploadOilConsume();
            }
        }, delayTime, Priority.NORMAL);
    }

    private void uploadOilConsume(){
        Log.i(TAG,"uploadOilConsume()");
        String userId = LoginManager.getInstance().getLoginUserId();
        long uid = (userId == null ? 0L : Long.parseLong(userId));
        String channelId = ConfigManager.ApkConfig.getChannelID();
        double oilConsumeValue= CarSettingManager.getInstance().getFuelConsumption();
        RequestManager.getInstance().pushOilConsume(uid, channelId, oilConsumeValue, new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                Log.i(TAG,"onSuccess() result="+result.getResultCode()+" ,"+result.getResultMessage()+" ,"+result.getData());
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.i(TAG,"onFailure() code="+code+" msg="+msg);
            }
        });
        uploadStart(CYCLE_GET_WEATHER_TIME);
    }

}
