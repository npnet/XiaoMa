package com.xiaoma.club.msg.redpacket.datasource;

import android.support.v4.util.ArrayMap;

import com.xiaoma.club.msg.redpacket.model.LocationRpResult;
import com.xiaoma.club.msg.redpacket.model.RPBaseResult;
import com.xiaoma.club.msg.redpacket.model.RPDetailResult;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.ModelCallback;
import com.xiaoma.network.callback.SimpleCallback;
import com.xiaoma.utils.GsonHelper;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by LKF on 2019-4-12 0012.
 * 红包相关的网络请求类
 */
public class RPRequest {
    private static final String PATH_SEND_SINGLE_PACKET = "redEnvelope/sendPrivate";
    private static final String PATH_SEND_GROUP_PACKET = "redEnvelope/sendGroup";

    private static final String PATH_OPEN_SINGLE_PACKET = "redEnvelope/receivePrivate";
    private static final String PATH_OPEN_GROUP_PACKET = "redEnvelope/receiveGroup";

    private static final String PATH_USER_WALLET = "redEnvelope/getUserScore";

    private static final String PATH_REQUEST_RP_DETAIL = "redEnvelope/receiveDetail";
    private static final String PATH_LOCATION_GROUP_PACKET = "redEnvelope/getGroupRedEnvelope";
    private static final String PATH_LOCATION_SINGLE_PACKET = "redEnvelope/getPrivateRedEnvelope";


    /**
     * 查询红包信息
     */
    public static void queryPacket(long packetId, final SimpleCallback<Integer> callback) {
        Map<String, Object> params = getParams("redEnvelopeId", packetId);
        request("redEnvelope/queryRedEnvelopeStatus", params, new ModelCallback<Integer>() {
            @Override
            public void onSuccess(Integer status) {
                if (callback != null) {
                    if (status != null && status > 0) {
                        callback.onSuccess(status);
                    } else {
                        callback.onError(-1, "");
                    }
                }
            }

            @Override
            public void onError(int code, String msg) {
                if (callback != null)
                    callback.onError(code, msg);
            }

            @Override
            public Integer parse(String data) {
                Integer status = null;
                try {
                    status = new JSONObject(data)
                            .optJSONObject("data")
                            .optInt("status", -1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return status;
            }
        });
    }

    /**
     * 拆红包
     */
    public static void openPacket(long packetId, boolean isGroup, final SimpleCallback<RPBaseResult> callback) {
        String path;
        if (isGroup) {
            path = PATH_OPEN_GROUP_PACKET;
        } else {
            path = PATH_OPEN_SINGLE_PACKET;
        }
        Map<String, Object> params = getParams("redEnvelopeId", packetId);
        request(path, params, new ModelCallback<RPBaseResult>() {
            @Override
            public void onSuccess(RPBaseResult result) {
                if (callback != null)
                    callback.onSuccess(result);
            }

            @Override
            public void onError(int code, String msg) {
                if (callback != null)
                    callback.onError(code, msg);
            }

            @Override
            public RPBaseResult parse(String data) {
                return GsonHelper.fromJson(data, RPBaseResult.class);
            }
        });
    }

    /**
     * 发送普通红包
     *
     * @param toId    如果是群聊则为群id,私聊则为用户id
     * @param isGroup 是否群聊红包
     * @param count   如果是私聊红包,count字段被忽略
     * @param money   单个红包金额
     */
    public static void sendNormalPacket(long toId, boolean isGroup, int count,
                                        int money, String greeting,
                                        final SimpleCallback<RPBaseResult> callback) {
        ModelCallback<RPBaseResult> modelCallback = new ModelCallback<RPBaseResult>() {
            @Override
            public RPBaseResult parse(String data) {
                return GsonHelper.fromJson(data, RPBaseResult.class);
            }

            @Override
            public void onSuccess(RPBaseResult result) {
                if (callback != null)
                    callback.onSuccess(result);
            }

            @Override
            public void onError(int code, String msg) {
                if (callback != null)
                    callback.onError(code, msg);
            }
        };
        if (isGroup) {
            sendGroupPacket(toId, count, money, greeting, modelCallback);
        } else {
            sendSinglePacket(toId, money, greeting, modelCallback);
        }
    }

    public static void sendLocationPacket(long toId, boolean isGroup, int count,
                                          int money, String greeting,
                                          double lat, double lon,
                                          String poiName, String poiAddress,
                                          final SimpleCallback<RPBaseResult> callback) {
        ModelCallback<RPBaseResult> modelCallback = new ModelCallback<RPBaseResult>() {
            @Override
            public RPBaseResult parse(String data) {
                return GsonHelper.fromJson(data, RPBaseResult.class);
            }

            @Override
            public void onSuccess(RPBaseResult result) {
                if (callback != null)
                    callback.onSuccess(result);
            }

            @Override
            public void onError(int code, String msg) {
                if (callback != null)
                    callback.onError(code, msg);
            }
        };
        if (isGroup) {
            sendGroupLocationPacket(toId, count, money, greeting, lat, lon, poiName, poiAddress, modelCallback);
        } else {
            sendSingleLocationPacket(toId, money, greeting, lat, lon, poiName, poiAddress, modelCallback);
        }
    }

    /**
     * 查询用户的钱包(车币)
     */
    public static void queryUserWallet(final SimpleCallback<Integer> callback) {
        request(PATH_USER_WALLET, null, new ModelCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                if (callback != null) {
                    if (result != null) {
                        callback.onSuccess(result);
                    } else {
                        callback.onError(-1, "");
                    }
                }
            }

            @Override
            public void onError(int code, String msg) {
                if (callback != null)
                    callback.onError(code, msg);
            }

            @Override
            public Integer parse(String data) {
                Integer walletMoney = null;
                try {
                    walletMoney = new JSONObject(data)
                            .getJSONObject("data")
                            .getInt("userScore");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return walletMoney;
            }
        });
    }

    private static void sendSinglePacket(long toUid, int money, String greeting, final ModelCallback<RPBaseResult> callback) {
        Map<String, Object> params = getParams(
                "toUserId", toUid,
                "pointNum", money,
                "message", greeting
        );
        request(PATH_SEND_SINGLE_PACKET, params, callback);
    }

    private static void sendSingleLocationPacket(long toUid, int money, String greeting,
                                                 double lat, double lon,
                                                 String poiName, String poiAddress,
                                                 ModelCallback<RPBaseResult> callback) {
        Map<String, Object> params = getParams(
                "toUserId", toUid,
                "pointNum", money,
                "message", greeting,
                "lat", lat,
                "lon", lon,
                "poiName", poiName,
                "location", poiAddress
        );
        request(PATH_SEND_SINGLE_PACKET, params, callback);
    }

    private static void sendGroupPacket(long groupId, int count, int money, String greeting, final ModelCallback<RPBaseResult> callback) {
        Map<String, Object> params = getParams(
                "toGroupId", groupId,
                "number", count,
                "pointNum", money,
                "message", greeting
        );
        request(PATH_SEND_GROUP_PACKET, params, callback);
    }

    private static void sendGroupLocationPacket(long groupId, int count,
                                                int money, String greeting,
                                                double lat, double lon,
                                                String poiName, String poiAddress,
                                                ModelCallback<RPBaseResult> callback) {
        Map<String, Object> params = getParams(
                "toGroupId", groupId,
                "number", count,
                "pointNum", money,
                "message", greeting,
                "lat", lat,
                "lon", lon,
                "poiName", poiName,
                "location", poiAddress
        );
        request(PATH_SEND_GROUP_PACKET, params, callback);
    }

    /**
     * 获取群聊红包地图列表
     */
    public static void requestGroupRpLocationList(long groupId, ModelCallback<LocationRpResult> callback) {
        Map<String, Object> params = getParams(
                "groupId", groupId
        );
        request(PATH_LOCATION_GROUP_PACKET, params, callback);
    }

    /**
     * 获取单聊红包地图列表
     */
    public static void requestSingleRpLocationList(long sendUserId, ModelCallback<LocationRpResult> callback) {
        Map<String, Object> params = getParams(
                "toUserId", sendUserId
        );
        request(PATH_LOCATION_SINGLE_PACKET, params, callback);
    }

    /**
     * 红包详情
     */
    public static void requestRpDetail(long rpId, ModelCallback<RPDetailResult> callback) {
        Map<String, Object> params = getParams(
                "redEnvelopeId", rpId
        );
        request(PATH_REQUEST_RP_DETAIL, params, callback);
    }


    private static Map<String, Object> getParams(Object... params) {
        Map<String, Object> paramMap = new ArrayMap<>();
        for (int i = 0; i < params.length; i += 2) {
            paramMap.put(String.valueOf(params[i]), params[i + 1]);
        }
        return paramMap;
    }

    private static <T> void request(String path, Map<String, Object> params, ModelCallback<T> callback) {
        String url = ConfigManager.EnvConfig.getEnv().getBusiness() + path;
        if (params != null && !params.isEmpty()) {
            XmHttp.getDefault().getString(url, params, callback);
        } else {
            XmHttp.getDefault().getString(url, callback);
        }
    }
}
