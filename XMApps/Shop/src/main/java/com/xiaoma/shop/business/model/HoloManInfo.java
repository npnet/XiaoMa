package com.xiaoma.shop.business.model;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/20
 */
public class HoloManInfo {

    /**
     * id : 97
     * name : 琥珀
     * description : 琥珀
     * version : 1
     * code : 1
     * picUrl : http://www.carbuyin.net/by2/appImg/709b55cd-dd99-4572-bc39-848390b41549.png
     * iconUrl :
     * defaultFlag : 0
     * needScore : 0
     * enableStatus : 1
     * type : 1
     * lockFlag : 0
     * pid : 0
     * hotFlag : 0
     * orderLevel : 1
     * createDate : 1532330479000
     * channelId : AA1090
     * childItems : {"holo_clothes":[{"id":102,"name":"白色西装","description":"白色西装","version":"1","code":"3","picUrl":"http://www.carbuyin.net/by2/qunHeader/caf4eaee-f31e-4656-95de-b2c3ecc41fe2.jpg","iconUrl":"http://www.carbuyin.net/by2/qunHeader/8596386f-9e5d-416c-bd3a-4a61e2d25d62.png","defaultFlag":0,"needScore":10,"enableStatus":1,"type":2,"lockFlag":1,"pid":97,"hotFlag":1,"orderLevel":3,"createDate":1532330479000,"channelId":"AA1090","userBuyFlag":1,"usedNum":0,"defaultUsedNum":200}],"holo_motion":[{"id":103,"name":"出场","description":"出场","version":"1","code":"1","iconUrl":"","defaultFlag":0,"needScore":0,"enableStatus":1,"type":3,"lockFlag":0,"pid":97,"hotFlag":0,"orderLevel":4,"skillFlag":0,"createDate":1532330479000,"channelId":"AA1090","userBuyFlag":1,"usedNum":0,"defaultUsedNum":200}]}
     * userBuyFlag : 1
     * usedNum : 0
     * defaultUsedNum : 200
     * price : 10
     * discountPrice : 10
     * discountScorePrice : 0
     */

    private int id;
    private String name;
    private String description;
    private String version;
    private String code;
    private String picUrl;
    private String iconUrl;
    private int defaultFlag;
    private int needScore;
    private int enableStatus;
    private int type;
    private int lockFlag;
    private int pid;
    private int hotFlag;
    private int orderLevel;
    private long createDate;
    private String channelId;
    private ChildItemsBean childItems;
    private int userBuyFlag;
    private int usedNum;
    private int defaultUsedNum;
    private double price;
    private double discountPrice;
    private int discountScorePrice;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(int defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    public int getNeedScore() {
        return needScore;
    }

    public void setNeedScore(int needScore) {
        this.needScore = needScore;
    }

    public int getEnableStatus() {
        return enableStatus;
    }

    public void setEnableStatus(int enableStatus) {
        this.enableStatus = enableStatus;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLockFlag() {
        return lockFlag;
    }

    public void setLockFlag(int lockFlag) {
        this.lockFlag = lockFlag;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getHotFlag() {
        return hotFlag;
    }

    public void setHotFlag(int hotFlag) {
        this.hotFlag = hotFlag;
    }

    public int getOrderLevel() {
        return orderLevel;
    }

    public void setOrderLevel(int orderLevel) {
        this.orderLevel = orderLevel;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public ChildItemsBean getChildItems() {
        return childItems;
    }

    public void setChildItems(ChildItemsBean childItems) {
        this.childItems = childItems;
    }

    public int getUserBuyFlag() {
        return userBuyFlag;
    }

    public void setUserBuyFlag(int userBuyFlag) {
        this.userBuyFlag = userBuyFlag;
    }

    public int getUsedNum() {
        return usedNum;
    }

    public void setUsedNum(int usedNum) {
        this.usedNum = usedNum;
    }

    public int getDefaultUsedNum() {
        return defaultUsedNum;
    }

    public void setDefaultUsedNum(int defaultUsedNum) {
        this.defaultUsedNum = defaultUsedNum;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public int getDiscountScorePrice() {
        return discountScorePrice;
    }

    public void setDiscountScorePrice(int discountScorePrice) {
        this.discountScorePrice = discountScorePrice;
    }

    public static class ChildItemsBean {
        private List<HologramDress> holo_clothes;
        private List<HologramSkill> holo_motion;

        public List<HologramDress> getHolo_clothes() {
            return holo_clothes;
        }

        public void setHolo_clothes(List<HologramDress> holo_clothes) {
            this.holo_clothes = holo_clothes;
        }

        public List<HologramSkill> getHolo_motion() {
            return holo_motion;
        }

        public void setHolo_motion(List<HologramSkill> holo_motion) {
            this.holo_motion = holo_motion;
        }
    }
}
