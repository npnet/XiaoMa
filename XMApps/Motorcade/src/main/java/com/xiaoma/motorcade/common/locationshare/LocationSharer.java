package com.xiaoma.motorcade.common.locationshare;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.xiaoma.model.User;
import com.xiaoma.motorcade.common.constants.MotorcadeConstants;
import com.xiaoma.motorcade.common.im.IMUtils;
import com.xiaoma.motorcade.common.model.ShareLocationParam;
import com.xiaoma.motorcade.common.utils.UserUtil;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by LKF on 2017/5/9 0009.
 */

class LocationSharer implements ILocationSharer {
    public static final String TAG = "LocationSharer";
    public static final int DEFAULT_GPS_UPDATE_INTERVAL = 3 * 1000;
    private String chatToId;
    private int gpsUpdateInterval = DEFAULT_GPS_UPDATE_INTERVAL;
    private AMapLocationClient aMapLocationClient;
    private SimpleMessageListener messageReceiver;
    private Set<LocationSharerCallback> locationReceiveListeners = new HashSet<>();
    private boolean started;

    LocationSharer(Context context, String chatToId) {
        this.chatToId = chatToId;
        init(context);
    }

    private void init(Context context) {
        AMapLocationClient client = new AMapLocationClient(context);

        AMapLocationClientOption locationOption = new AMapLocationClientOption();
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationOption.setInterval(gpsUpdateInterval);
        locationOption.setNeedAddress(false);
        client.setLocationOption(locationOption);
        this.aMapLocationClient = client;
        client.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                final User currentUser = UserUtil.getCurrentUser();
                if (currentUser == null) {
                    return;
                }
                if (aMapLocation == null || aMapLocation.getErrorCode() != 0) {
                    KLog.e(TAG, "未获取到经纬度，设置默认北京地区经纬度");
                    aMapLocation.setLatitude(39.994337);
                    aMapLocation.setLongitude(116.43323);
                    if (aMapLocationClient != null) {
                        aMapLocationClient.startLocation();
                    }
                }
                ShareLocationParam param = new ShareLocationParam(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                param.setHxAccount(currentUser.getHxAccountService());
                param.setPicPath(currentUser.getPicPath());
                param.setUserNick(currentUser.getName());
                IMUtils.sendLocationCMDMessage(chatToId, param);
                KLog.d("MrMine", "onLocationChanged: " + "sendLocationCMDMessage");
                dispatchOnSendLocation(param);
            }
        });
        EMClient.getInstance().chatManager().addMessageListener(messageReceiver = new SimpleMessageListener() {
            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                super.onCmdMessageReceived(messages);
                for (EMMessage cmdMessage : messages) {
                    if (cmdMessage.getTo().equals(chatToId)) {
                        final EMMessageBody body = cmdMessage.getBody();
                        if (body instanceof EMCmdMessageBody) {
                            final EMCmdMessageBody cmdMessageBody = (EMCmdMessageBody) body;
                            final String action = cmdMessageBody.action();
                            if (action.equals(MotorcadeConstants.IMMessageType.LOCATION_SHARE)) {
                                String paramStr = null;
                                ShareLocationParam param = null;
                                try {
                                    paramStr = cmdMessage.getStringAttribute(MotorcadeConstants.LOCATION_SHARE_PARAM);
                                    param = GsonHelper.fromJson(paramStr, ShareLocationParam.class);
                                    for (LocationSharerCallback l : locationReceiveListeners) {
                                        l.onReceiveLocation(LocationSharer.this, cmdMessage.getFrom(), param);
                                    }
                                } catch (HyphenateException e) {
                                    e.printStackTrace();
                                }
                            } else if (action.equals(MotorcadeConstants.IMMessageType.LOCATION_OUT)) {
                                for (LocationSharerCallback l : locationReceiveListeners) {
                                    l.onReceiveLocationOut(LocationSharer.this, cmdMessage.getFrom());
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void dispatchOnSendLocation(ShareLocationParam param) {
        for (LocationSharerCallback l : locationReceiveListeners) {
            l.onSendLocation(this, param);
        }
    }

    @Override
    public String getChatToId() {
        return chatToId;
    }

    @Override
    public void start() {
        if (started)
            return;
        started = true;
        aMapLocationClient.startLocation();
    }

    @Override
    public void stop() {
        if (!started)
            return;
        started = false;
        EMClient.getInstance().chatManager().removeMessageListener(messageReceiver);
        aMapLocationClient.stopLocation();
        IMUtils.sendOutLocationCMDMessage(chatToId);
    }

    @Override
    public void addCallback(LocationSharerCallback listener) {
        if (listener != null)
            locationReceiveListeners.add(listener);
    }

    @Override
    public void removeCallback(LocationSharerCallback listener) {
        if (listener != null)
            locationReceiveListeners.remove(listener);
    }
}
