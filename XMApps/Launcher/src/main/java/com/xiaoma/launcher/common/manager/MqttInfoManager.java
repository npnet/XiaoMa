package com.xiaoma.launcher.common.manager;

import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.mqtt.constant.MqttConstants;
import com.xiaoma.mqtt.model.MqttInfo;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

/**
 * Created by Thomas on 2019/6/10 0010
 */

public class MqttInfoManager {

    private static MqttInfoManager instance;
    private Context context;

    private MqttInfoManager() {
    }

    public static MqttInfoManager getInstance() {
        if (instance == null) {
            synchronized (MqttInfoManager.class) {
                if (instance == null) {
                    instance = new MqttInfoManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        RequestManager.getInstance().getMqttInfo(new ResultCallback<XMResult<MqttInfo>>() {

            @Override
            public void onSuccess(XMResult<MqttInfo> result) {
                saveMqttInfo(result.getData());
            }

            @Override
            public void onFailure(int code, String msg) {
                KLog.e("MqttManager getMqttInfo onFailure() code = " + code + " msg = " + msg);
            }
        });
    }

    private void saveMqttInfo(MqttInfo data) {
        if (data != null && !TextUtils.isEmpty(data.broker)
                && !TextUtils.isEmpty(data.username)
                && !TextUtils.isEmpty(data.password)) {
            String mqttInfo = GsonHelper.toJson(data);
            TPUtils.put(this.context, MqttConstants.Mqtt_Info, mqttInfo);
            KLog.d("MqttManager saveMqttInfo is " + mqttInfo);
        }
    }

}
