package com.xiaoma.mqtt.model;


public class SensorBean {
    /**
     * type : gps
     * datas : [{"lon":114.065718,"lat":32.986567,"p":"河南省","c":"驻马店市","dis":"驿城区","s":"0.0","ac":"64.41126","be":"-1.0","gac":"0","sa":"-1"},{"lon":114.065718,"lat":32.986567,"p":"河南省","c":"驻马店市","dis":"驿城区","s":"0.0","ac":"64.41126","be":"-1.0","gac":"0","sa":"-1"}]
     */

    private String type;
    private DatasBean data;

    public SensorBean(DatasBean data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DatasBean getData() {
        return data;
    }

    public void setData(DatasBean data) {
        this.data = data;
    }
}