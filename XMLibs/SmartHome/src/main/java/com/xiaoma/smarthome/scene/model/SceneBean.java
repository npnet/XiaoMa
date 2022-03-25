package com.xiaoma.smarthome.scene.model;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.smarthome.scene.model
 *  @file_name:      SceneBean
 *  @author:         Rookie
 *  @create_time:    2019/4/23 16:10
 *  @description：   TODO             */

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class SceneBean implements Parcelable {
    /**
     * sceneId : 16386
     * sceneName : 离家模式
     * sceneType : EVENT_SCENE
     * effictTime : 08:00~23:00
     * week : 0,1,2,3,4,5,6
     * imgUrl : https://cnbj2.fds.api.xiaomi.com/viomi-device/viomi-home/leaveHome.png
     * status : 0
     * triggers : []
     * actions : []
     * autoCondition : {"uid":1104999319574163456,"sceneId":16386,"autoFlag":0,"ruleFlag":0,"radius":1000}
     */


    private String sceneId;
    private String sceneName;
    private String sceneType;//1. “EVENT_SCENE” 事件场景 2. “TIMER_SCENE” 定时场景
    private String effictTime;//场景生效时间段 默认为 “08:00~23:00”
    private String week;//每周的哪几天执行 0:周日 1:周一 2:周二 3:周三 4:周四 5:周五 6:周六
    private String imgUrl;
    private String status;//场景开关 0 表示 “开” 1 表示 “关”
    private List<TriggersBean> triggers;
    private List<ActionsBean> actions;
    private AutoConditionBean autoCondition;

    public AutoConditionBean getAutoCondition() {
        return autoCondition;
    }

    public void setAutoCondition(AutoConditionBean autoCondition) {
        this.autoCondition = autoCondition;
    }

    public static class AutoConditionBean implements Parcelable {

        /**
         * uid : 187
         * sceneId : 16150
         * lat : 113.953994
         * lon : 22.536317
         * address : 广东省深圳市南山区粤海街道高新南四道航盛科技大厦
         * autoFlag : 1
         * ruleFlag : 1
         * radius : 1000
         */

        private String uid;
        private String sceneId;
        private double lat;
        private double lon;
        private String address;
        private int autoFlag;   //触发开关，1表示开,0表示关
        private int ruleFlag;   //触发规则，1表示大于，0表示小于
        private int radius;     //触发半径

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getSceneId() {
            return sceneId;
        }

        public void setSceneId(String sceneId) {
            this.sceneId = sceneId;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getAutoFlag() {
            return autoFlag;
        }

        public void setAutoFlag(int autoFlag) {
            this.autoFlag = autoFlag;
        }

        public int getRuleFlag() {
            return ruleFlag;
        }

        public void setRuleFlag(int ruleFlag) {
            this.ruleFlag = ruleFlag;
        }

        public int getRadius() {
            return radius;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.uid);
            dest.writeString(this.sceneId);
            dest.writeDouble(this.lat);
            dest.writeDouble(this.lon);
            dest.writeString(this.address);
            dest.writeInt(this.autoFlag);
            dest.writeInt(this.ruleFlag);
            dest.writeInt(this.radius);
        }

        public AutoConditionBean() {
        }

        protected AutoConditionBean(Parcel in) {
            this.uid = in.readString();
            this.sceneId = in.readString();
            this.lat = in.readDouble();
            this.lon = in.readDouble();
            this.address = in.readString();
            this.autoFlag = in.readInt();
            this.ruleFlag = in.readInt();
            this.radius = in.readInt();
        }

        public static final Creator<AutoConditionBean> CREATOR = new Creator<AutoConditionBean>() {
            @Override
            public AutoConditionBean createFromParcel(Parcel source) {
                return new AutoConditionBean(source);
            }

            @Override
            public AutoConditionBean[] newArray(int size) {
                return new AutoConditionBean[size];
            }
        };
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getSceneType() {
        return sceneType;
    }

    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }

    public String getEffictTime() {
        return effictTime;
    }

    public void setEffictTime(String effictTime) {
        this.effictTime = effictTime;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<TriggersBean> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<TriggersBean> triggers) {
        this.triggers = triggers;
    }

    public List<ActionsBean> getActions() {
        return actions;
    }

    public void setActions(List<ActionsBean> actions) {
        this.actions = actions;
    }

    public static class TriggersBean implements Parcelable {
        /**
         * deviceId : 00124b001a584fcc_0e
         * deviceType : 514
         * deviceName : 云米互联网窗帘电机(开合帘版)
         * event : open
         * eventName : 窗帘开
         * imgUrl : https://cdn.cnbj2.fds.api.mi-img.com/viomi-fileupload/product_%20viomi.fb.curtain_514_dcc70bdb-a0cd-4379-b3ed-ffc01f0bb733.png
         * gatewayId : 5150859
         */

        private String deviceId;
        private String deviceType;
        private String deviceName;
        private String event;
        private String eventName;
        private String imgUrl;
        private String gatewayId;

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public String getEventName() {
            return eventName;
        }

        public void setEventName(String eventName) {
            this.eventName = eventName;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getGatewayId() {
            return gatewayId;
        }

        public void setGatewayId(String gatewayId) {
            this.gatewayId = gatewayId;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.deviceId);
            dest.writeString(this.deviceType);
            dest.writeString(this.deviceName);
            dest.writeString(this.event);
            dest.writeString(this.eventName);
            dest.writeString(this.imgUrl);
            dest.writeString(this.gatewayId);
        }

        public TriggersBean() {
        }

        protected TriggersBean(Parcel in) {
            this.deviceId = in.readString();
            this.deviceType = in.readString();
            this.deviceName = in.readString();
            this.event = in.readString();
            this.eventName = in.readString();
            this.imgUrl = in.readString();
            this.gatewayId = in.readString();
        }

        public static final Creator<TriggersBean> CREATOR = new Creator<TriggersBean>() {
            @Override
            public TriggersBean createFromParcel(Parcel source) {
                return new TriggersBean(source);
            }

            @Override
            public TriggersBean[] newArray(int size) {
                return new TriggersBean[size];
            }
        };
    }

    public static class ActionsBean implements Parcelable {
        /**
         * deviceId : 00124b001b60390e_08
         * deviceType : 516
         * deviceName : 云米互联网晾衣架
         * method : openLight
         * methodName : 打开照明
         * imgUrl : https://cdn.cnbj2.fds.api.mi-img.com/viomi-fileupload/product_viomi.fb.dryingrack_516_f8c4a494-f4c1-4e68-b512-1760b9a43cbd.png
         * gatewayId : 5150859
         * parameter : 1
         */

        private String deviceId;
        private String deviceType;
        private String deviceName;
        private String method;
        private String methodName;
        private String imgUrl;
        private String gatewayId;
        private String parameter;

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getGatewayId() {
            return gatewayId;
        }

        public void setGatewayId(String gatewayId) {
            this.gatewayId = gatewayId;
        }

        public String getParameter() {
            return parameter;
        }

        public void setParameter(String parameter) {
            this.parameter = parameter;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.deviceId);
            dest.writeString(this.deviceType);
            dest.writeString(this.deviceName);
            dest.writeString(this.method);
            dest.writeString(this.methodName);
            dest.writeString(this.imgUrl);
            dest.writeString(this.gatewayId);
            dest.writeString(this.parameter);
        }

        public ActionsBean() {
        }

        protected ActionsBean(Parcel in) {
            this.deviceId = in.readString();
            this.deviceType = in.readString();
            this.deviceName = in.readString();
            this.method = in.readString();
            this.methodName = in.readString();
            this.imgUrl = in.readString();
            this.gatewayId = in.readString();
            this.parameter = in.readString();
        }

        public static final Creator<ActionsBean> CREATOR = new Creator<ActionsBean>() {
            @Override
            public ActionsBean createFromParcel(Parcel source) {
                return new ActionsBean(source);
            }

            @Override
            public ActionsBean[] newArray(int size) {
                return new ActionsBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sceneId);
        dest.writeString(this.sceneName);
        dest.writeString(this.sceneType);
        dest.writeString(this.effictTime);
        dest.writeString(this.week);
        dest.writeString(this.imgUrl);
        dest.writeString(this.status);
        dest.writeList(this.triggers);
        dest.writeList(this.actions);
    }

    public SceneBean() {
    }

    protected SceneBean(Parcel in) {
        this.sceneId = in.readString();
        this.sceneName = in.readString();
        this.sceneType = in.readString();
        this.effictTime = in.readString();
        this.week = in.readString();
        this.imgUrl = in.readString();
        this.status = in.readString();
        this.triggers = new ArrayList<TriggersBean>();
        in.readList(this.triggers, TriggersBean.class.getClassLoader());
        this.actions = new ArrayList<ActionsBean>();
        in.readList(this.actions, ActionsBean.class.getClassLoader());
    }

    public static final Creator<SceneBean> CREATOR = new Creator<SceneBean>() {
        @Override
        public SceneBean createFromParcel(Parcel source) {
            return new SceneBean(source);
        }

        @Override
        public SceneBean[] newArray(int size) {
            return new SceneBean[size];
        }
    };


}
