package com.xiaoma.motorcade.main;

/**
 * 简介: 车队信息类
 *
 * @author lingyan
 */
public class MotorcadeInfo {
    // 在线人数
    private int onLineUserNumbers;
    // 车队总人数
    private int totalNumbers;
    // 车队口令
    private String motorcadeCommand;
    // 车队名
    private String motorcadeName;
    // 车队管理员id
    private long adminId;

    public int getOnLineUserNumbers() {
        return onLineUserNumbers;
    }

    public void setOnLineUserNumbers(int onLineUserNumbers) {
        this.onLineUserNumbers = onLineUserNumbers;
    }

    public int getTotalNumbers() {
        return totalNumbers;
    }

    public void setTotalNumbers(int totalNumbers) {
        this.totalNumbers = totalNumbers;
    }

    public String getMotorcadeCommand() {
        return motorcadeCommand;
    }

    public void setMotorcadeCommand(String motorcadeCommand) {
        this.motorcadeCommand = motorcadeCommand;
    }

    public String getMotorcadeName() {
        return motorcadeName;
    }

    public void setMotorcadeName(String motorcadeName) {
        this.motorcadeName = motorcadeName;
    }

    public long getAdminId() {
        return adminId;
    }

    public void setAdminId(long adminId) {
        this.adminId = adminId;
    }
}
