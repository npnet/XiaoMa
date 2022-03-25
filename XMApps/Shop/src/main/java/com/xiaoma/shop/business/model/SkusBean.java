package com.xiaoma.shop.business.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.xiaoma.shop.common.track.model.GoodsTrackInfo;
import com.xiaoma.utils.GsonHelper;

/**
 * 语音音色
 */
public class SkusBean implements Parcelable {

    /**
     * id : 236
     * createDate : 1539586589000
     * modifyDate : 1539586589000
     * category : 音频
     * price : 0.01
     * icon : http://www.carbuyin.net/sl/filePath/58b646fc-4083-47d7-81a4-6f5ca4a1f131.png
     * typeIcon :
     * themeName : 童声
     * voiceParam : vinn
     * xfParameter : 0
     * orderList : 120
     * logoUrl : http://www.carbuyin.net/sl/filePath/58b646fc-4083-47d7-81a4-6f5ca4a1f131.png
     * isBuy : false
     * channelId : AA1090
     * status : 0
     * isDefault : false
     * scorePrice : 0
     * source : 0
     * usedNum : 53
     */

    private int id;
    private long createDate;
    private long modifyDate;
    private String category;
    private double price;
    private String icon;
    private String typeIcon;
    private String themeName;
    private String voiceParam;
    private int xfParameter;
    private int orderList;
    private String logoUrl;
    private boolean isBuy;
    private String channelId;
    private int status;
    private boolean isDefault;
    private int scorePrice;
    private int source;
    private double discountPrice;
    private int discountScorePrice;
    private int usedNum;
    @SerializedName("auditionMusicUrl")
    private String auditionUrl;
    @SerializedName("musicUrl")
    private String ttsResDownloadUrl;

    private boolean isSelect;

    private boolean isPlay;

    public String getTtsResDownloadUrl() {
        return ttsResDownloadUrl;
    }

    public void setTtsResDownloadUrl(String ttsResDownloadUrl) {
        this.ttsResDownloadUrl = ttsResDownloadUrl;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public String getAuditionUrl() {
        return auditionUrl;
    }

    public void setAuditionUrl(String auditionUrl) {
        this.auditionUrl = auditionUrl;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(long modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTypeIcon() {
        return typeIcon;
    }

    public void setTypeIcon(String typeIcon) {
        this.typeIcon = typeIcon;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public String getVoiceParam() {
        return voiceParam;
    }

    public void setVoiceParam(String voiceParam) {
        this.voiceParam = voiceParam;
    }

    public int getXfParameter() {
        return xfParameter;
    }

    public void setXfParameter(int xfParameter) {
        this.xfParameter = xfParameter;
    }

    public int getOrderList() {
        return orderList;
    }

    public void setOrderList(int orderList) {
        this.orderList = orderList;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public void setBuy(boolean buy) {
        isBuy = buy;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public int getScorePrice() {
        return scorePrice;
    }

    public void setScorePrice(int scorePrice) {
        this.scorePrice = scorePrice;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
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

    public int getUsedNum() {
        return usedNum;
    }

    public void setUsedNum(int usedNum) {
        this.usedNum = usedNum;
    }

    private int defaultShowNum;
    public SkusBean() {
    }

    public int getDefaultShowNum() {
        return defaultShowNum;
    }

    public void setDefaultShowNum(int defaultShowNum) {
        this.defaultShowNum = defaultShowNum;
    }

    public static final Creator<SkusBean> CREATOR = new Creator<SkusBean>() {
        @Override
        public SkusBean createFromParcel(Parcel source) {
            return new SkusBean(source);
        }

        @Override
        public SkusBean[] newArray(int size) {
            return new SkusBean[size];
        }
    };

    public String toTrackString() {
        GoodsTrackInfo goodsTrackInfo = new GoodsTrackInfo();
        goodsTrackInfo.setId(id);
        goodsTrackInfo.setName(themeName);
        goodsTrackInfo.setRmbPrice(String.valueOf(price));
        goodsTrackInfo.setRmbPriceDiscount(String.valueOf(discountPrice));
        goodsTrackInfo.setCoinPrice(String.valueOf(scorePrice));
        goodsTrackInfo.setCoinPriceDiscount(String.valueOf(discountScorePrice));
        goodsTrackInfo.setCount(String.valueOf(defaultShowNum + usedNum));
//        goodsTrackInfo.setTag(String.valueOf(status));
        return GsonHelper.toJson(goodsTrackInfo);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeLong(this.createDate);
        dest.writeLong(this.modifyDate);
        dest.writeString(this.category);
        dest.writeDouble(this.price);
        dest.writeString(this.icon);
        dest.writeString(this.typeIcon);
        dest.writeString(this.themeName);
        dest.writeString(this.voiceParam);
        dest.writeInt(this.xfParameter);
        dest.writeInt(this.orderList);
        dest.writeString(this.logoUrl);
        dest.writeByte(this.isBuy ? (byte) 1 : (byte) 0);
        dest.writeString(this.channelId);
        dest.writeInt(this.status);
        dest.writeByte(this.isDefault ? (byte) 1 : (byte) 0);
        dest.writeInt(this.scorePrice);
        dest.writeInt(this.source);
        dest.writeDouble(this.discountPrice);
        dest.writeInt(this.discountScorePrice);
        dest.writeInt(this.usedNum);
        dest.writeString(this.auditionUrl);
        dest.writeString(this.ttsResDownloadUrl);
        dest.writeByte(this.isSelect ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isPlay ? (byte) 1 : (byte) 0);
        dest.writeInt(this.defaultShowNum);
    }

    protected SkusBean(Parcel in) {
        this.id = in.readInt();
        this.createDate = in.readLong();
        this.modifyDate = in.readLong();
        this.category = in.readString();
        this.price = in.readDouble();
        this.icon = in.readString();
        this.typeIcon = in.readString();
        this.themeName = in.readString();
        this.voiceParam = in.readString();
        this.xfParameter = in.readInt();
        this.orderList = in.readInt();
        this.logoUrl = in.readString();
        this.isBuy = in.readByte() != 0;
        this.channelId = in.readString();
        this.status = in.readInt();
        this.isDefault = in.readByte() != 0;
        this.scorePrice = in.readInt();
        this.source = in.readInt();
        this.discountPrice = in.readDouble();
        this.discountScorePrice = in.readInt();
        this.usedNum = in.readInt();
        this.auditionUrl = in.readString();
        this.ttsResDownloadUrl = in.readString();
        this.isSelect = in.readByte() != 0;
        this.isPlay = in.readByte() != 0;
        this.defaultShowNum = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }
}