package com.xiaoma.music.kuwo.model;

import com.xiaoma.music.common.constant.MusicConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: loren
 * Date: 2019/6/25 0025
 */
public class VIPOrderStateModel {

    private String token;//订单token，由KWApi.getPayToken获得
    private String appId;//由酷我商务生成，前端固定常量
    private String isBind;//车机账号是否绑定酷我账号，1是，0否；暂时不用，默认为0
    private long id;//订单id,创建订单后，后台会返回订单id

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appid) {
        this.appId = appid;
    }

    public String getIsBind() {
        return isBind;
    }

    public void setIsBind(String isBind) {
        this.isBind = isBind;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Map<String, Object> getRequestParams() {
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("appId", MusicConstants.KwConstants.KW_APP_ID);
        map.put("isBind", "0");
        map.put("id", id);
        return map;
    }
}
