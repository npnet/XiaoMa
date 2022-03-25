package com.xiaoma.shop.business.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.xiaoma.shop.common.constant.ShopContract;
import com.xiaoma.shop.common.track.model.GoodsTrackInfo;

import java.util.ArrayList;
import java.util.List;

public class SkinVersionsBean implements Parcelable {

    /**
     * id : 257
     * createDate : 1539586589000
     * modifyDate : 1556419526000
     * versionName : 1.0
     * versionCode : 1
     * channelId : AA1090
     * appName : 红色阴影Aa12dfsdfsffsdfsadfsdafsfsfsdfsfsdfsdfsfsagdfgsdfsdfsafsdfsafsdfsdfsdfsd
     * versionDesc : 一款年轻个性的皮肤
     * packageName : com.xiaoma.launcher
     * size : 216255
     * sizeFomat : 211 KB
     * md5String : 8d7eb0c4bb21c03b0f4e055749c90441
     * url : http://www.carbuyin.net/by2/skin/launcher_151eea2aa7a646b389681c0b195f754b_1.0.skin
     * apkFilename : launcher_151eea2aa7a646b389681c0b195f754b_1.0.skin
     * apkFilePath : http://www.carbuyin.net/by2/skin/launcher_151eea2aa7a646b389681c0b195f754b_1.0.skin
     * isForceUpdate : false
     * isAddUpdate : false
     * isNeedUpdate : false
     * bigImg1 : http://www.carbuyin.net/sl/filePath/64b5e98b-c8f6-4230-99f3-85d01dbc4d56.png
     * bigImg2 : http://www.carbuyin.net/sl/filePath/68bf3eb0-93f8-4f4a-b63a-5b52ec343609.png
     * bigImg3 : http://www.carbuyin.net/by2/skinImg/ea4cbedc-fea4-48d1-90fa-3e2e3a869365.png
     * bigImgUrl1 : http://www.carbuyin.net/sl/filePath/64b5e98b-c8f6-4230-99f3-85d01dbc4d56.png
     * bigImgUrl2 : http://www.carbuyin.net/sl/filePath/68bf3eb0-93f8-4f4a-b63a-5b52ec343609.png
     * bigImgUrl3 : http://www.carbuyin.net/by2/skinImg/ea4cbedc-fea4-48d1-90fa-3e2e3a869365.png
     * isRecommend : 0
     * iconPath : http://www.carbuyin.net/by2/skinImg/bba5c0b1-12ec-43b3-a446-27ccbcf9fab4.png
     * iconPathUrl : http://www.carbuyin.net/by2/skinImg/bba5c0b1-12ec-43b3-a446-27ccbcf9fab4.png
     * price : 1.1
     * scorePrice : 123
     * isBuy : true
     * tagPath :
     * logoUrl : http://www.carbuyin.net/by2/skinImg/bba5c0b1-12ec-43b3-a446-27ccbcf9fab4.png
     * status : 0
     * discountPrice : 0.5
     * isDefault : false
     * usedNum : 0
     * defaultShowNum : 200
     * showNum : 0
     * canTry : 0
     * trialTime : 5
     * orderNumber : 3
     * likeNum : 0
     * isThumbs : 0
     * tag : 限免
     * hotSkin : 0
     * discountScorePrice : 555
     * newSkin : 0
     * hot : false
     * used : false
     */

    private int id;
    private String bigImg3;
    private String tag;
    private int discountScorePrice;
    private int newSkin;
    private boolean used;
    private long createDate;
    private long modifyDate;
    private String versionName;
    private String versionCode;
    private String channelId;
    private String appName;
    private String versionDesc;
    private String packageName;
    private int size;
    private String sizeFomat;
    private String md5String;
    private String url;
    private String apkFilename;
    private String apkFilePath;
    private boolean isForceUpdate;
    private boolean isAddUpdate;
    private boolean isNeedUpdate;
    private String bigImg1;
    private String bigImg2;
    private String bigImgUrl1;
    private String bigImgUrl2;
    private String bigImgUrl3;
    private String isRecommend;
    private String iconPath;
    private String iconPathUrl;
    private double price;
    private int scorePrice;
    private boolean isBuy;
    private String tagPath;
    private String logoUrl;
    private int status;
    private double discountPrice;
    private boolean isDefault;
    private int usedNum;
    private int defaultShowNum;
    private int showNum;
    private int canTry;
    private int trialTime;
    private int orderNumber;
    private int likeNum;
    private int isThumbs;
    private int hotSkin;
    private boolean hot;
    // TODO: 字段名称待后台确认
    private int skinStyle;

    public int getSkinStyle() {
        return skinStyle;
    }

    public void setSkinStyle(int skinStyle) {
        this.skinStyle = skinStyle;
    }

    private boolean select = false;
    @ShopContract.Pay
    private transient int pay;

    public SkinVersionsBean() {
    }

    public double getLatestPrice() {
        if (discountPrice * 100 > 0) {
            return discountPrice;
        }
        return price;
    }

    public int getLatestScorePrice() {
        if (discountScorePrice > 0) {
            return discountScorePrice;
        }
        return scorePrice;
    }

    public List<String> getThumbnails() {
        ArrayList<String> thumbnailLists = new ArrayList<>();
        if (!TextUtils.isEmpty(bigImgUrl1)) {
            thumbnailLists.add(bigImgUrl1);
        }

        if (!TextUtils.isEmpty(bigImgUrl2)) {
            thumbnailLists.add(bigImgUrl2);
        }

        if (!TextUtils.isEmpty(bigImgUrl3)) {
            thumbnailLists.add(bigImgUrl3);
        }

        return thumbnailLists;
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

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersionDesc() {
        return versionDesc;
    }

    public void setVersionDesc(String versionDesc) {
        this.versionDesc = versionDesc;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSizeFomat() {
        return sizeFomat;
    }

    public void setSizeFomat(String sizeFomat) {
        this.sizeFomat = sizeFomat;
    }

    public String getMd5String() {
        return md5String;
    }

    public void setMd5String(String md5String) {
        this.md5String = md5String;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getApkFilename() {
        return apkFilename;
    }

    public void setApkFilename(String apkFilename) {
        this.apkFilename = apkFilename;
    }

    public String getApkFilePath() {
        return apkFilePath;
    }

    public void setApkFilePath(String apkFilePath) {
        this.apkFilePath = apkFilePath;
    }

    public boolean isIsForceUpdate() {
        return isForceUpdate;
    }

    public void setIsForceUpdate(boolean isForceUpdate) {
        this.isForceUpdate = isForceUpdate;
    }

    public boolean isIsAddUpdate() {
        return isAddUpdate;
    }

    public void setIsAddUpdate(boolean isAddUpdate) {
        this.isAddUpdate = isAddUpdate;
    }

    public boolean isIsNeedUpdate() {
        return isNeedUpdate;
    }

    public void setIsNeedUpdate(boolean isNeedUpdate) {
        this.isNeedUpdate = isNeedUpdate;
    }

    public String getBigImg1() {
        return bigImg1;
    }

    public void setBigImg1(String bigImg1) {
        this.bigImg1 = bigImg1;
    }

    public String getBigImg2() {
        return bigImg2;
    }

    public void setBigImg2(String bigImg2) {
        this.bigImg2 = bigImg2;
    }


    public String getBigImgUrl1() {
        return bigImgUrl1;
    }

    public void setBigImgUrl1(String bigImgUrl1) {
        this.bigImgUrl1 = bigImgUrl1;
    }

    public String getBigImgUrl2() {
        return bigImgUrl2;
    }

    public void setBigImgUrl2(String bigImgUrl2) {
        this.bigImgUrl2 = bigImgUrl2;
    }

    public String getBigImgUrl3() {
        return bigImgUrl3;
    }

    public void setBigImgUrl3(String bigImgUrl3) {
        this.bigImgUrl3 = bigImgUrl3;
    }

    public String getIsRecommend() {
        return isRecommend;
    }

    public void setIsRecommend(String isRecommend) {
        this.isRecommend = isRecommend;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public String getIconPathUrl() {
        return iconPathUrl;
    }

    public void setIconPathUrl(String iconPathUrl) {
        this.iconPathUrl = iconPathUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getScorePrice() {
        return scorePrice;
    }

    public void setScorePrice(int scorePrice) {
        this.scorePrice = scorePrice;
    }

    public boolean isIsBuy() {
        return isBuy;
    }

    public void setIsBuy(boolean isBuy) {
        this.isBuy = isBuy;
    }

    public String getTagPath() {
        return tagPath;
    }

    public void setTagPath(String tagPath) {
        this.tagPath = tagPath;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public boolean isIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public int getUsedNum() {
        return usedNum;
    }

    public void setUsedNum(int usedNum) {
        this.usedNum = usedNum;
    }

    public int getDefaultShowNum() {
        return defaultShowNum;
    }

    public void setDefaultShowNum(int defaultShowNum) {
        this.defaultShowNum = defaultShowNum;
    }

    public int getShowNum() {
        return showNum;
    }

    public void setShowNum(int showNum) {
        this.showNum = showNum;
    }

    public int getCanTry() {
        return canTry;
    }

    public void setCanTry(int canTry) {
        this.canTry = canTry;
    }

    public int getTrialTime() {
        return trialTime;
    }

    public void setTrialTime(int trialTime) {
        this.trialTime = trialTime;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public int getIsThumbs() {
        return isThumbs;
    }

    public void setIsThumbs(int isThumbs) {
        this.isThumbs = isThumbs;
    }

    public String getBigImg3() {
        return bigImg3;
    }

    public int getHotSkin() {
        return hotSkin;
    }

    public void setHotSkin(int hotSkin) {
        this.hotSkin = hotSkin;
    }

    public void setBigImg3(String bigImg3) {
        this.bigImg3 = bigImg3;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getDiscountScorePrice() {
        return discountScorePrice;
    }

    public boolean isHot() {
        return hot;
    }

    public void setHot(boolean hot) {
        this.hot = hot;
    }

    public void setDiscountScorePrice(int discountScorePrice) {
        this.discountScorePrice = discountScorePrice;
    }

    public int getNewSkin() {
        return newSkin;
    }

    public void setNewSkin(int newSkin) {
        this.newSkin = newSkin;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public int getPay() {
        return pay;
    }

    public void setPay(int pay) {
        this.pay = pay;
    }

    public static final Creator<SkinVersionsBean> CREATOR = new Creator<SkinVersionsBean>() {
        @Override
        public SkinVersionsBean createFromParcel(Parcel source) {
            return new SkinVersionsBean(source);
        }

        @Override
        public SkinVersionsBean[] newArray(int size) {
            return new SkinVersionsBean[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.bigImg3);
        dest.writeString(this.tag);
        dest.writeInt(this.discountScorePrice);
        dest.writeInt(this.newSkin);
        dest.writeByte(this.used ? (byte) 1 : (byte) 0);
        dest.writeLong(this.createDate);
        dest.writeLong(this.modifyDate);
        dest.writeString(this.versionName);
        dest.writeString(this.versionCode);
        dest.writeString(this.channelId);
        dest.writeString(this.appName);
        dest.writeString(this.versionDesc);
        dest.writeString(this.packageName);
        dest.writeInt(this.size);
        dest.writeString(this.sizeFomat);
        dest.writeString(this.md5String);
        dest.writeString(this.url);
        dest.writeString(this.apkFilename);
        dest.writeString(this.apkFilePath);
        dest.writeByte(this.isForceUpdate ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAddUpdate ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isNeedUpdate ? (byte) 1 : (byte) 0);
        dest.writeString(this.bigImg1);
        dest.writeString(this.bigImg2);
        dest.writeString(this.bigImgUrl1);
        dest.writeString(this.bigImgUrl2);
        dest.writeString(this.bigImgUrl3);
        dest.writeString(this.isRecommend);
        dest.writeString(this.iconPath);
        dest.writeString(this.iconPathUrl);
        dest.writeDouble(this.price);
        dest.writeInt(this.scorePrice);
        dest.writeByte(this.isBuy ? (byte) 1 : (byte) 0);
        dest.writeString(this.tagPath);
        dest.writeString(this.logoUrl);
        dest.writeInt(this.status);
        dest.writeDouble(this.discountPrice);
        dest.writeByte(this.isDefault ? (byte) 1 : (byte) 0);
        dest.writeInt(this.usedNum);
        dest.writeInt(this.defaultShowNum);
        dest.writeInt(this.showNum);
        dest.writeInt(this.canTry);
        dest.writeInt(this.trialTime);
        dest.writeInt(this.orderNumber);
        dest.writeInt(this.likeNum);
        dest.writeInt(this.isThumbs);
        dest.writeInt(this.hotSkin);
        dest.writeByte(this.hot ? (byte) 1 : (byte) 0);
        dest.writeInt(this.skinStyle);
        dest.writeByte(this.select ? (byte) 1 : (byte) 0);
    }

    protected SkinVersionsBean(Parcel in) {
        this.id = in.readInt();
        this.bigImg3 = in.readString();
        this.tag = in.readString();
        this.discountScorePrice = in.readInt();
        this.newSkin = in.readInt();
        this.used = in.readByte() != 0;
        this.createDate = in.readLong();
        this.modifyDate = in.readLong();
        this.versionName = in.readString();
        this.versionCode = in.readString();
        this.channelId = in.readString();
        this.appName = in.readString();
        this.versionDesc = in.readString();
        this.packageName = in.readString();
        this.size = in.readInt();
        this.sizeFomat = in.readString();
        this.md5String = in.readString();
        this.url = in.readString();
        this.apkFilename = in.readString();
        this.apkFilePath = in.readString();
        this.isForceUpdate = in.readByte() != 0;
        this.isAddUpdate = in.readByte() != 0;
        this.isNeedUpdate = in.readByte() != 0;
        this.bigImg1 = in.readString();
        this.bigImg2 = in.readString();
        this.bigImgUrl1 = in.readString();
        this.bigImgUrl2 = in.readString();
        this.bigImgUrl3 = in.readString();
        this.isRecommend = in.readString();
        this.iconPath = in.readString();
        this.iconPathUrl = in.readString();
        this.price = in.readDouble();
        this.scorePrice = in.readInt();
        this.isBuy = in.readByte() != 0;
        this.tagPath = in.readString();
        this.logoUrl = in.readString();
        this.status = in.readInt();
        this.discountPrice = in.readDouble();
        this.isDefault = in.readByte() != 0;
        this.usedNum = in.readInt();
        this.defaultShowNum = in.readInt();
        this.showNum = in.readInt();
        this.canTry = in.readInt();
        this.trialTime = in.readInt();
        this.orderNumber = in.readInt();
        this.likeNum = in.readInt();
        this.isThumbs = in.readInt();
        this.hotSkin = in.readInt();
        this.hot = in.readByte() != 0;
        this.skinStyle = in.readInt();
        this.select = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toTrackString() {
        GoodsTrackInfo goodsTrackInfo = new GoodsTrackInfo();
        goodsTrackInfo.setId(id);
        goodsTrackInfo.setName(appName);
        goodsTrackInfo.setRmbPrice(String.valueOf(price));
        goodsTrackInfo.setRmbPriceDiscount(String.valueOf(discountPrice));
        goodsTrackInfo.setCoinPrice(String.valueOf(scorePrice));
        goodsTrackInfo.setCoinPriceDiscount(String.valueOf(discountScorePrice));
        goodsTrackInfo.setCount(String.valueOf(usedNum + defaultShowNum));
        goodsTrackInfo.setTag(tag);
        return goodsTrackInfo.toString();
    }
}