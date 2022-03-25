package com.xiaoma.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

@Entity(indices = {@Index("id"), @Index("hxAccount"), @Index("hxAccountService")})
@Table("CacheUser")
public class User implements Serializable, Parcelable {

    @PrimaryKey
    @com.litesuits.orm.db.annotation.PrimaryKey(AssignType.BY_MYSELF)
    private long id;                       // userId
    private int isOnLine;                  // 是否在线
    private String voicePrintClientId;     // 声纹ClinetId
    private int masterUser;                // 是否为主账户 为1是为true
    private long masterAccountId;          // 为子账户时，其所附属的主账户id
    private String bluetoothKey;           // 绑定的蓝牙钥匙Id
    private String commonKey;              // 绑定的普通钥匙Id
    private int faceId;                    // 绑定的人脸识别Id !!!后台没这个字段!!!
    private String password;               // 经过加盐hash后的用户密码，用于校验 !!!后台没这个字段!!!

    private int type;                      // 用户类型(参考UserType)1.普通类型6.产线测试7.路试账户
    private String openId;                 // 对外展示用的UserID

    private long createDate;               // 服务器数据创建时间
    private long modifyDate;               // 服务器数据修改时间

    private int gender;                    // 性别
    private String age;                    // 年龄
    private String name;                   // 昵称
    private String phone;                  // 手机号
    private String picPath;                // 头像地址
    private String headerIndex;            // 默认头像列表下标
    private String birthDay;               // 生日
    private long birthDayLong;             // 生日时间戳
    private String personalSignature;      // 个人签名（车信专用）

    private String imei;                   // 注册的imei
    private String channelId;              // 注册的channelId
    private String tboxSN;                 // T-Box串码


    private String carType;                // 车辆型号
    private String distance;               // 里程
    private String plateNumber;            // 车牌号
    private String vin;          // 车架号
    private String engineNumber;           // 发动机号

    private String carTypeId;              // 车辆型号ID
    private String carTypeLogo;            // 车辆型号Logo

    private boolean isFirstCar;            // 位置共享中的头车

    private String hxAccount;              // Cl ub环信账号
    private String hxPassword;             // Club环信密码
    private String hxAccountService;       // Service环信账号
    private String hxPasswordService;      // Service环信密码

    private String voipAccount;            // Club语音账户
    private String voipPassword;           // Club语音密码
    private String voipAccountService;     // Service语音账户
    private String voipPasswordService;    // Service语音密码

    private String wxOpenId;               // 微信OpenID
    private String bmUserId;               // 北京现代蓝缤会员id

    private String subToken;               // 历史遗留,弃用
    private String subAccount;             // 历史遗留,弃用
    private String mkUser;                 // 历史遗留,弃用
    private String mkPassword;             // 历史遗留,弃用

    private int isRead;                    // 未知参数
    private String enableStatus;           // 未知参数
    private String privateStatus;          // 未知参数
    private int beanNum;                   // 未知参数
    private String userShortNum;           // 未知参数
    private String riskScore;              // 未知参数
    private int score;                     // 未知参数
    private int h5Score;                   // 未知参数
    private String extra1;                 // 额外预留字段
    private String extra2;                 // 额外预留字段
    private String extra3;                 // 额外预留字段

    public User() {

    }

    public long getBirthDayLong() {
        return birthDayLong;
    }

    public String getImei() {
        return imei;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getTboxSN() {
        return tboxSN;
    }

    public String getVoipAccount() {
        return voipAccount;
    }

    public String getVoipPassword() {
        return voipPassword;
    }

    public String getVoipAccountService() {
        return voipAccountService;
    }

    public String getVoipPasswordService() {
        return voipPasswordService;
    }

    public String getWxOpenId() {
        return wxOpenId;
    }

    public String getBmUserId() {
        return bmUserId;
    }

    public String getSubToken() {
        return subToken;
    }

    public String getSubAccount() {
        return subAccount;
    }

    public String getMkUser() {
        return mkUser;
    }

    public String getMkPassword() {
        return mkPassword;
    }

    public int getIsRead() {
        return isRead;
    }

    public String getEnableStatus() {
        return enableStatus;
    }

    public String getPrivateStatus() {
        return privateStatus;
    }

    public int getBeanNum() {
        return beanNum;
    }

    public String getUserShortNum() {
        return userShortNum;
    }

    public String getRiskScore() {
        return riskScore;
    }

    public int getScore() {
        return score;
    }

    public int getH5Score() {
        return h5Score;
    }

    public long getId() {
        return id;
    }

    public int getIsMasterAccount() {
        return masterUser;
    }

    public long getMasterAccountId() {
        return masterAccountId;
    }

    public String getBluetoothKeyId() {
        return bluetoothKey;
    }

    public String getNormalKeyId() {
        return commonKey;
    }

    public String getVoicePrintClientId() {
        return voicePrintClientId;
    }

    public int getIsOnLine() {
        return isOnLine;
    }

    public int getType() {
        return type;
    }

    // 判断用户是否是系统用户
    public boolean isSystemAccount() {
        return getType() == ModelConstants.UserType.TYPE_SYSTEM_USER;
    }

    public String getOpenId() {
        return openId;
    }

    public long getCreateDate() {
        return createDate;
    }

    public long getModifyDate() {
        return modifyDate;
    }

    public int getGender() {
        return gender;
    }

    public boolean isMan() {
        return getGender() == ModelConstants.GenderType.TYPE_MAN;
    }

    public String getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getPicPath() {
        // 后台为了节省CDN流量删除了原图
        // 只有Middle和Small被保留了
        // 为了防止后面有人使用原图Url
        // 这里默认转换为Middle
//        return StringUtil.getImagePathByType(picPath, StringUtil.ImageType.MIDDLE_IMG);
        // 最新的后台不支持使用后缀获取不同规格的图片了
        return picPath;
    }

    public String getPersonalSignature() {
        return personalSignature;
    }

    public String getHeaderIndex() {
        return headerIndex;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public String getCarType() {
        return carType;
    }

    public String getDistance() {
        return distance;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getVin() {
        return vin;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public void setMasterUser(int masterUser) {
        this.masterUser = masterUser;
    }

    public void setBluetoothKey(String bluetoothKey) {
        this.bluetoothKey = bluetoothKey;
    }

    public void setCommonKey(String commonKey) {
        this.commonKey = commonKey;
    }

    public int getFaceId() {
        return faceId;
    }

    public void setFaceId(int faceId) {
        this.faceId = faceId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCarTypeId() {
        return carTypeId;
    }

    public String getCarTypeLogo() {
        return carTypeLogo;
    }

    public boolean isFirstCar() {
        return isFirstCar;
    }

    public String getHxAccount() {
        return hxAccount;
    }

    public String getHxPassword() {
        return hxPassword;
    }

    public String getHxAccountService() {
        return hxAccountService;
    }

    public String getHxPasswordService() {
        return hxPasswordService;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setIsMasterAccount(int isMasterAccount) {
        this.masterUser = isMasterAccount;
    }

    public int getMasterUser() {
        return masterUser;
    }

    public String getBluetoothKey() {
        return bluetoothKey;
    }

    public String getCommonKey() {
        return commonKey;
    }

    public void setMasterAccountId(long masterAccountId) {
        this.masterAccountId = masterAccountId;
    }

    public void setBluetoothKeyId(String bluetoothKeyId) {
        this.bluetoothKey = bluetoothKeyId;
    }

    public void setNormalKeyId(String normalKeyId) {
        this.commonKey = normalKeyId;
    }

    public void setVoicePrintClientId(String voicePrintClientId) {
        this.voicePrintClientId = voicePrintClientId;
    }

    public void setIsOnLine(int isOnLine) {
        this.isOnLine = isOnLine;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public void setModifyDate(long modifyDate) {
        this.modifyDate = modifyDate;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public void setPersonalSignature(String personalSignature) {
        this.personalSignature = personalSignature;
    }

    public void setHeaderIndex(String headerIndex) {
        this.headerIndex = headerIndex;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public void setBirthDayLong(long birthDayLong) {
        this.birthDayLong = birthDayLong;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setTboxSN(String tboxSN) {
        this.tboxSN = tboxSN;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }

    public void setCarTypeId(String carTypeId) {
        this.carTypeId = carTypeId;
    }

    public void setCarTypeLogo(String carTypeLogo) {
        this.carTypeLogo = carTypeLogo;
    }

    public void setFirstCar(boolean firstCar) {
        isFirstCar = firstCar;
    }

    public void setHxAccount(String hxAccount) {
        this.hxAccount = hxAccount;
    }

    public void setHxPassword(String hxPassword) {
        this.hxPassword = hxPassword;
    }

    public void setHxAccountService(String hxAccountService) {
        this.hxAccountService = hxAccountService;
    }

    public void setHxPasswordService(String hxPasswordService) {
        this.hxPasswordService = hxPasswordService;
    }

    public void setVoipAccount(String voipAccount) {
        this.voipAccount = voipAccount;
    }

    public void setVoipPassword(String voipPassword) {
        this.voipPassword = voipPassword;
    }

    public void setVoipAccountService(String voipAccountService) {
        this.voipAccountService = voipAccountService;
    }

    public void setVoipPasswordService(String voipPasswordService) {
        this.voipPasswordService = voipPasswordService;
    }

    public void setWxOpenId(String wxOpenId) {
        this.wxOpenId = wxOpenId;
    }

    public void setBmUserId(String bmUserId) {
        this.bmUserId = bmUserId;
    }

    public void setSubToken(String subToken) {
        this.subToken = subToken;
    }

    public void setSubAccount(String subAccount) {
        this.subAccount = subAccount;
    }

    public void setMkUser(String mkUser) {
        this.mkUser = mkUser;
    }

    public void setMkPassword(String mkPassword) {
        this.mkPassword = mkPassword;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public void setEnableStatus(String enableStatus) {
        this.enableStatus = enableStatus;
    }

    public void setPrivateStatus(String privateStatus) {
        this.privateStatus = privateStatus;
    }

    public void setBeanNum(int beanNum) {
        this.beanNum = beanNum;
    }

    public void setUserShortNum(String userShortNum) {
        this.userShortNum = userShortNum;
    }

    public void setRiskScore(String riskScore) {
        this.riskScore = riskScore;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setH5Score(int h5Score) {
        this.h5Score = h5Score;
    }

    public String getExtra1() {
        return extra1;
    }

    public void setExtra1(String extra1) {
        this.extra1 = extra1;
    }

    public String getExtra2() {
        return extra2;
    }

    public void setExtra2(String extra2) {
        this.extra2 = extra2;
    }

    public String getExtra3() {
        return extra3;
    }

    public void setExtra3(String extra3) {
        this.extra3 = extra3;
    }

    // 判断用户是否发生改变
    // public boolean isUserChange(User compare) {
    //     return !(picPath.equals(compare.getPicPath())
    //             && age == compare.getAge()
    //             && carType.equals(compare.getCarType())
    //             && plateNumber.equals(compare.getPlateNumber()));
    // }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.isOnLine);
        dest.writeString(this.voicePrintClientId);
        dest.writeInt(this.masterUser);
        dest.writeLong(this.masterAccountId);
        dest.writeString(this.bluetoothKey);
        dest.writeString(this.commonKey);
        dest.writeInt(this.faceId);
        dest.writeString(this.password);
        dest.writeInt(this.type);
        dest.writeString(this.openId);
        dest.writeLong(this.createDate);
        dest.writeLong(this.modifyDate);
        dest.writeInt(this.gender);
        dest.writeString(this.age);
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeString(this.picPath);
        dest.writeString(this.headerIndex);
        dest.writeString(this.birthDay);
        dest.writeLong(this.birthDayLong);
        dest.writeString(this.personalSignature);
        dest.writeString(this.imei);
        dest.writeString(this.channelId);
        dest.writeString(this.tboxSN);
        dest.writeString(this.carType);
        dest.writeString(this.distance);
        dest.writeString(this.plateNumber);
        dest.writeString(this.vin);
        dest.writeString(this.engineNumber);
        dest.writeString(this.carTypeId);
        dest.writeString(this.carTypeLogo);
        dest.writeByte(this.isFirstCar ? (byte) 1 : (byte) 0);
        dest.writeString(this.hxAccount);
        dest.writeString(this.hxPassword);
        dest.writeString(this.hxAccountService);
        dest.writeString(this.hxPasswordService);
        dest.writeString(this.voipAccount);
        dest.writeString(this.voipPassword);
        dest.writeString(this.voipAccountService);
        dest.writeString(this.voipPasswordService);
        dest.writeString(this.wxOpenId);
        dest.writeString(this.bmUserId);
        dest.writeString(this.subToken);
        dest.writeString(this.subAccount);
        dest.writeString(this.mkUser);
        dest.writeString(this.mkPassword);
        dest.writeInt(this.isRead);
        dest.writeString(this.enableStatus);
        dest.writeString(this.privateStatus);
        dest.writeInt(this.beanNum);
        dest.writeString(this.userShortNum);
        dest.writeString(this.riskScore);
        dest.writeInt(this.score);
        dest.writeInt(this.h5Score);
        dest.writeString(this.extra1);
        dest.writeString(this.extra2);
        dest.writeString(this.extra3);
    }

    protected User(Parcel in) {
        this.id = in.readLong();
        this.isOnLine = in.readInt();
        this.voicePrintClientId = in.readString();
        this.masterUser = in.readInt();
        this.masterAccountId = in.readLong();
        this.bluetoothKey = in.readString();
        this.commonKey = in.readString();
        this.faceId = in.readInt();
        this.password = in.readString();
        this.type = in.readInt();
        this.openId = in.readString();
        this.createDate = in.readLong();
        this.modifyDate = in.readLong();
        this.gender = in.readInt();
        this.age = in.readString();
        this.name = in.readString();
        this.phone = in.readString();
        this.picPath = in.readString();
        this.headerIndex = in.readString();
        this.birthDay = in.readString();
        this.birthDayLong = in.readLong();
        this.personalSignature = in.readString();
        this.imei = in.readString();
        this.channelId = in.readString();
        this.tboxSN = in.readString();
        this.carType = in.readString();
        this.distance = in.readString();
        this.plateNumber = in.readString();
        this.vin = in.readString();
        this.engineNumber = in.readString();
        this.carTypeId = in.readString();
        this.carTypeLogo = in.readString();
        this.isFirstCar = in.readByte() != 0;
        this.hxAccount = in.readString();
        this.hxPassword = in.readString();
        this.hxAccountService = in.readString();
        this.hxPasswordService = in.readString();
        this.voipAccount = in.readString();
        this.voipPassword = in.readString();
        this.voipAccountService = in.readString();
        this.voipPasswordService = in.readString();
        this.wxOpenId = in.readString();
        this.bmUserId = in.readString();
        this.subToken = in.readString();
        this.subAccount = in.readString();
        this.mkUser = in.readString();
        this.mkPassword = in.readString();
        this.isRead = in.readInt();
        this.enableStatus = in.readString();
        this.privateStatus = in.readString();
        this.beanNum = in.readInt();
        this.userShortNum = in.readString();
        this.riskScore = in.readString();
        this.score = in.readInt();
        this.h5Score = in.readInt();
        this.extra1 = in.readString();
        this.extra2 = in.readString();
        this.extra3 = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}