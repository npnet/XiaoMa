package com.xiaoma.carlib.manager;

/**
 * @author: iSun
 * @date: 2018/12/24 0024
 */
public interface ICarHvac {

    //打开/关闭空调
    void setHvacPowerOn(int state);
    //空调是否打开
    int getHvacPowerOn();

    //吹风模式切换，包括除霜模式，吹脚除霜模式，吹脚模式，吹脸和吹脚模式，吹脸模式
    void setFanDirectionAvailable(int mode);
    int getFanDirectionAvailable();

    //设置为具体温度
    void setLeftTempSetPoint(int area, float degrees);
    void setRightTempSetPoint(int area, float degrees);

    //获取当前温度值
    float getLeftTempSetPoint(int area);
    float getRightTempSetPoint(int area);

    //风量设置
    void setFanSpeedSetpoint(int speed);
    int getFanSpeedSetpoint();

    //打开/关闭压缩机
    void setAcON(int state);

    //切换循环模式，包括内循环和外循环
    void setAirRecirculationOn(int state);

    //打开/关闭自动模式
    void setAutomaticMode(int state);

    //打开/关闭后视镜加热
    void setMirrorDefroster(boolean state);
    boolean getMirrorDefroster();

    //打开/关闭后风窗电加热
    public void setWindowRearHeat(boolean state);

    //打开/关闭驾驶员座椅加热,关闭副驾驶座椅加热
    void setSeatTemp(int area, double temp);
    void setLeftSeatTemp(int state);
    void setRightSeatTemp(int state);

    //设置空调温度
    void setAcTemp(int temp);
    void setLeftAcTemp(int temp);
    void setRightAcTemp(int temp);
    //获取空调温度
    int getLeftAcTemp();
    int getRightAcTemp();

    void setTestValue(int constant, int param);

}
