package com.xiaoma.music.kuwo.model;

import com.xiaoma.music.common.constant.MusicConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: loren
 * Date: 2019/6/25 0025
 */
public class VIPOrderModel {

    private String token;//订单token，由KWApi.getPayToken获得
    private String appId;//由酷我商务生成，前端固定常量
    private String isBind;//车机账号是否绑定酷我账号，1是，0否；暂时不用，默认为0
    private double price;//订单价格
    private int cnt;//开通的月数
    private String id;
    private String type;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getIsBind() {
        return isBind;
    }

    public void setIsBind(String isBind) {
        this.isBind = isBind;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getRequestParams() {
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("appId", MusicConstants.KwConstants.KW_APP_ID);
        map.put("isBind", "0");
        map.put("price", price);
        map.put("cnt", cnt);
        map.put("id", id);
        map.put("type", type);
        return map;
    }
}
