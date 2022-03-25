package com.xiaoma.personal.coin.model;

/**
 * @author Gillben
 * date: 2018/12/07
 * <p>
 * 车币记录实体类
 */
public class CoinRecordBean {

    private String desc;
    private String date;
    private int coin;
    private String iconUrl;

    public CoinRecordBean(String desc, String date, int coin, String iconUrl) {
        this.desc = desc;
        this.date = date;
        this.coin = coin;
        this.iconUrl = iconUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
