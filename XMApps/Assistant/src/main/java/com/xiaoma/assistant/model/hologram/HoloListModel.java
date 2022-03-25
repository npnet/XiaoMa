package com.xiaoma.assistant.model.hologram;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.xiaoma.utils.GsonHelper;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/20
 */
public class HoloListModel implements Parcelable {

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
     * userBuyFlag : 0  =>0 未兑换, 1 已兑换
     * usedNum : 0
     * defaultUsedNum : 200
     * price : 10
     * discountPrice : 10
     * discountScorePrice : 0
     */
    public String toTrackString() {
        GoodsTrackInfo goodsTrackInfo = new GoodsTrackInfo();
        goodsTrackInfo.setId(id);
        goodsTrackInfo.setName(name);
        goodsTrackInfo.setRmbPrice(String.valueOf(price));
        goodsTrackInfo.setRmbPriceDiscount(String.valueOf(discountPrice));
        goodsTrackInfo.setCoinPrice(String.valueOf(needScore));
        goodsTrackInfo.setCoinPriceDiscount(String.valueOf(discountScorePrice));
        goodsTrackInfo.setCount(String.valueOf(defaultUsedNum + usedNum));
        goodsTrackInfo.setTag(String.valueOf(hotFlag));
        return GsonHelper.toJson(goodsTrackInfo);
    }


    public static final int TYPE_3D_SELF = 0;
    public static final int TYPE_3D_NORMAL = 1;
    public static final String PREFIX_ZIP = ".zip";
    private int type; //  0->自定义全息,1->非自定义

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

    public Boolean get3DState() {
        Boolean state = null;
        if (type == TYPE_3D_SELF) {
            state = Boolean.FALSE; //默认的3D
            if (!TextUtils.isEmpty(customImageResourceUrl)
                    && customImageResourceUrl.endsWith(PREFIX_ZIP)) {
                state = Boolean.TRUE; //有效的3D
            } else {
                picUrl = "";//保证自定义使用默认图标
            }
        }
        return state;
    }
    private int lockFlag;
    private int pid;
    private int hotFlag;
    private int orderLevel;
    private long createDate;
    private String channelId;
    private int userBuyFlag;
    private int usedNum;
    private int defaultUsedNum;
    private double price;
    private double discountPrice;
    private int discountScorePrice;
    private String customImageResourceUrl;

    private transient int state; //bean类的状态码

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    /**
     * 是否为自定义形象
     */
    public boolean isCustomRole() {
        return false;
    }

    public String getCustomImageResourceUrl() {
        return customImageResourceUrl;
    }

    public void setCustomImageResourceUrl(String customImageResourceUrl) {
        this.customImageResourceUrl = customImageResourceUrl;
    }

    public HoloListModel() {
    }

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

    public void setPrice(int price) {
        this.price = price;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(int discountPrice) {
        this.discountPrice = discountPrice;
    }

    public int getDiscountScorePrice() {
        return discountScorePrice;
    }

    public void setDiscountScorePrice(int discountScorePrice) {
        this.discountScorePrice = discountScorePrice;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.version);
        dest.writeString(this.code);
        dest.writeString(this.picUrl);
        dest.writeString(this.iconUrl);
        dest.writeInt(this.defaultFlag);
        dest.writeInt(this.needScore);
        dest.writeInt(this.enableStatus);
        dest.writeInt(this.type);
        dest.writeInt(this.lockFlag);
        dest.writeInt(this.pid);
        dest.writeInt(this.hotFlag);
        dest.writeInt(this.orderLevel);
        dest.writeLong(this.createDate);
        dest.writeString(this.channelId);
        dest.writeInt(this.userBuyFlag);
        dest.writeInt(this.usedNum);
        dest.writeInt(this.defaultUsedNum);
        dest.writeDouble(this.price);
        dest.writeDouble(this.discountPrice);
        dest.writeInt(this.discountScorePrice);
        dest.writeString(this.customImageResourceUrl);
    }

    protected HoloListModel(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        this.version = in.readString();
        this.code = in.readString();
        this.picUrl = in.readString();
        this.iconUrl = in.readString();
        this.defaultFlag = in.readInt();
        this.needScore = in.readInt();
        this.enableStatus = in.readInt();
        this.type = in.readInt();
        this.lockFlag = in.readInt();
        this.pid = in.readInt();
        this.hotFlag = in.readInt();
        this.orderLevel = in.readInt();
        this.createDate = in.readLong();
        this.channelId = in.readString();
        this.userBuyFlag = in.readInt();
        this.usedNum = in.readInt();
        this.defaultUsedNum = in.readInt();
        this.price = in.readDouble();
        this.discountPrice = in.readDouble();
        this.discountScorePrice = in.readInt();
        this.customImageResourceUrl = in.readString();
    }

    public static final Creator<HoloListModel> CREATOR = new Creator<HoloListModel>() {
        @Override
        public HoloListModel createFromParcel(Parcel source) {
            return new HoloListModel(source);
        }

        @Override
        public HoloListModel[] newArray(int size) {
            return new HoloListModel[size];
        }
    };
}
