package com.xiaoma.assistant.model.parser;

import java.io.Serializable;

/**
 * @author: iSun
 * @date: 2019/2/13 0013
 */
public class AirBean implements Serializable {
    public String device;
    public TemperatureBean temperature;
    public TemperatureBean fanSpeed;
    public String insType;
    public String mode;// "前后除霜";
    public String airflowDirection;//吹脚 脸
    public String operation;//CLOSE

    public static class TemperatureBean {
        public String direct;
        public String offset;
        public String ref;
        public String type;
    }
}
