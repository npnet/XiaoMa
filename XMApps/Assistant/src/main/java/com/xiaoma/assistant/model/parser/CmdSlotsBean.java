package com.xiaoma.assistant.model.parser;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/30
 * Desc:
 */
public class CmdSlotsBean implements Serializable {
    String category;
    String name;
    String code;
    String temperature;
    String mode;
    String airflowDirection;
    String fanSpeed;
    String target;
    String device;
    String nameValue;
    String source;
    String content;
    public String nameScn; // 蓝牙/音乐/蓝牙音乐
    @SerializedName("datetime")
    DateTimeBean dateTimeBean;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public DateTimeBean getDateTimeBean() {
        return dateTimeBean;
    }

    public void setDateTimeBean(DateTimeBean dateTimeBean) {
        this.dateTimeBean = dateTimeBean;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getFanSpeed() {
        return fanSpeed;
    }

    public void setFanSpeed(String fanSpeed) {
        this.fanSpeed = fanSpeed;
    }

    public String getAirflowDirection() {
        return airflowDirection;
    }

    public void setAirflowDirection(String airflowDirection) {
        this.airflowDirection = airflowDirection;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getNameValue() {
        return nameValue;
    }

    public void setNameValue(String nameValue) {
        this.nameValue = nameValue;
    }

    public boolean isVoiceTypeCmd(){
        return "语音设置".equalsIgnoreCase(category);
    }

    public boolean isPlayModeCmd(){
        return "播放模式".equalsIgnoreCase(category);
    }

    public boolean isPlayControlCmd(){
        return "曲目控制".equalsIgnoreCase(category);
    }

    public boolean isScreenControlCmd(){
        return "亮度调节".equalsIgnoreCase(category);
    }

    public boolean isMapSettingCmd(){
        return "地图设置".equalsIgnoreCase(category);
    }

    public boolean isNaviControlCmd(){
        return "导航控制".equalsIgnoreCase(category);
    }

    public boolean isWakeUpTypeCmd(){
        return "唤醒词".equalsIgnoreCase(category);
    }

    public boolean isVolumTypeCmd(){
        return "音量控制".equalsIgnoreCase(category);
    }
    public boolean isRadioModeCmd(){
        return "收音机控制".equalsIgnoreCase(category);
    }
}
