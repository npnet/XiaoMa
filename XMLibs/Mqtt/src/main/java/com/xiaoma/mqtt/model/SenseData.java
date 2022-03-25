package com.xiaoma.mqtt.model;


/**
 * @Auther: huojie
 * @Date: 2019/1/2 0002 11:05
 * @Description:
 */
public class SenseData {

    /**
     * mid : 85d61c48-ba65-4b41-9fbc-5e6c9e15c2d6
     * tid : 12312312313
     * cd : 448971636
     * uid : 1073758947469074432
     * iccid : 89860918710000022349
     * imei : 867012039872830
     * cid : AA1080
     * ov : 4.4.3
     * dm : BestuneFAW-D077
     * vc : 1123
     * sensor : {"type":"gps","datas":[{"lon":114.065718,"lat":32.986567,"p":"河南省","c":"驻马店市","dis":"驿城区","s":"0.0","ac":"64.41126","be":"-1.0","gac":"0","sa":"-1"},{"lon":114.065718,"lat":32.986567,"p":"河南省","c":"驻马店市","dis":"驿城区","s":"0.0","ac":"64.41126","be":"-1.0","gac":"0","sa":"-1"}]}
     */

    private Device device;
    private SensorBean sensor;

    public SenseData(Device device, SensorBean sensor) {
        this.device = device;
        this.sensor = sensor;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public SensorBean getSensor() {
        return sensor;
    }

    public void setSensor(SensorBean sensor) {
        this.sensor = sensor;
    }


}
