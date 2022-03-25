package com.xiaoma.club.msg.redpacket.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hyphenate.chat.EMMessage;
import com.xiaoma.club.msg.chat.constant.MessageKey;
import com.xiaoma.club.msg.chat.constant.MessageType;

/**
 * Created by LKF on 2019-4-12 0012.
 */
public class RedPacketInfo implements Parcelable {
    @SerializedName("id")
    private long packetId;

    @SerializedName("message")
    private String greeting;

    @SerializedName("pointNum")
    private int money;

    @SerializedName("totalNum")
    private int count;

    @SerializedName("poiName")
    private String poiName;

    @SerializedName("location")
    private String poiAddress;

    @SerializedName("lat")
    private double latitude;

    @SerializedName("lon")
    private double longitude;

    @SerializedName("redEnvelopeStatus")
    private int openStatus;

    private boolean isLocation;

    public static RedPacketInfo fromMessage(EMMessage message) {
        RedPacketInfo info = new RedPacketInfo();
        info.setPacketId(message.getLongAttribute(MessageKey.RedPacket.PACKET_ID, 0));
        info.setGreeting(message.getStringAttribute(MessageKey.RedPacket.GREETING, ""));
        info.setMoney(message.getIntAttribute(MessageKey.RedPacket.MONEY, 0));
        info.setCount(message.getIntAttribute(MessageKey.RedPacket.COUNT, 0));
        info.setOpenStatus(message.getIntAttribute(MessageKey.RedPacket.OPEN_STATUS, 0));
        info.setLocation(message.getBooleanAttribute(MessageKey.RedPacket.IS_LOCATION, false));
        if (info.isLocation()) {
            info.setPoiName(message.getStringAttribute(MessageKey.RedPacket.POI_NAME, ""));
            info.setPoiAddress(message.getStringAttribute(MessageKey.RedPacket.POI_ADDRESS, ""));
            try {
                info.setLatitude(Double.parseDouble(message.getStringAttribute(MessageKey.RedPacket.LATITUDE, "")));
            } catch (NumberFormatException ignored) {
            }
            try {
                info.setLongitude(Double.parseDouble(message.getStringAttribute(MessageKey.RedPacket.LONGITUDE, "")));
            } catch (NumberFormatException ignored) {
            }
        }
        return info;
    }

    public static EMMessage toMessage(RedPacketInfo redPacketInfo, String chatId, boolean isGroupChat) {
        final EMMessage message = EMMessage.createTxtSendMessage("[ RedPacketInfo ]", chatId);
        // 红包基本信息
        message.setAttribute(MessageKey.MSG_TYPE, MessageType.RED_PACKET);
        message.setAttribute(MessageKey.RedPacket.PACKET_ID, redPacketInfo.getPacketId());
        message.setAttribute(MessageKey.RedPacket.GREETING, redPacketInfo.getGreeting());
        message.setAttribute(MessageKey.RedPacket.MONEY, redPacketInfo.getMoney());
        message.setAttribute(MessageKey.RedPacket.COUNT, redPacketInfo.getCount());
        message.setAttribute(MessageKey.RedPacket.OPEN_STATUS, redPacketInfo.getOpenStatus());
        message.setAttribute(MessageKey.RedPacket.IS_LOCATION, redPacketInfo.isLocation());
        if (redPacketInfo.isLocation()) {
            // 位置信息
            message.setAttribute(MessageKey.RedPacket.POI_NAME, redPacketInfo.getPoiName());
            message.setAttribute(MessageKey.RedPacket.POI_ADDRESS, redPacketInfo.getPoiAddress());
            message.setAttribute(MessageKey.RedPacket.LATITUDE, String.valueOf(redPacketInfo.getLatitude()));
            message.setAttribute(MessageKey.RedPacket.LONGITUDE, String.valueOf(redPacketInfo.getLongitude()));
        }
        // 会话类型
        message.setChatType(isGroupChat ? EMMessage.ChatType.GroupChat : EMMessage.ChatType.Chat);
        return message;
    }

    public long getPacketId() {
        return packetId;
    }

    public void setPacketId(long packetId) {
        this.packetId = packetId;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPoiName() {
        return poiName;
    }

    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }

    public String getPoiAddress() {
        return poiAddress;
    }

    public void setPoiAddress(String poiAddress) {
        this.poiAddress = poiAddress;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(int openStatus) {
        this.openStatus = openStatus;
    }

    public boolean isLocation() {
        return isLocation;
    }

    public void setLocation(boolean location) {
        isLocation = location;
    }

    public RedPacketInfo() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.packetId);
        dest.writeString(this.greeting);
        dest.writeInt(this.money);
        dest.writeInt(this.count);
        dest.writeString(this.poiName);
        dest.writeString(this.poiAddress);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeInt(this.openStatus);
        dest.writeByte(this.isLocation ? (byte) 1 : (byte) 0);
    }

    protected RedPacketInfo(Parcel in) {
        this.packetId = in.readLong();
        this.greeting = in.readString();
        this.money = in.readInt();
        this.count = in.readInt();
        this.poiName = in.readString();
        this.poiAddress = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.openStatus = in.readInt();
        this.isLocation = in.readByte() != 0;
    }

    public static final Creator<RedPacketInfo> CREATOR = new Creator<RedPacketInfo>() {
        @Override
        public RedPacketInfo createFromParcel(Parcel source) {return new RedPacketInfo(source);}

        @Override
        public RedPacketInfo[] newArray(int size) {return new RedPacketInfo[size];}
    };
}
