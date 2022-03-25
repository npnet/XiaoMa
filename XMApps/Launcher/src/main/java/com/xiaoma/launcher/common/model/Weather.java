package com.xiaoma.launcher.common.model;

/**
 * @author taojin
 * @date 2019/4/9
 */
public class Weather {

    /**
     * date : 2019-04-09
     * airQuality : 优质
     * city : 深圳
     * windLevel : 3
     * curTemp : 28℃
     * ziwaixian : 中等
     * pm25 : 15
     * weather : 多云
     * humidity : 69%
     * tempRange : 25℃~30℃
     * sourceName : 中国天气网
     * dateLong : 1554796427
     * wind : 西南风
     * lastUpdateTime : 2019-04-09 03:53:47
     * wash_car : {"title":"较适宜","desc":"无雨且风力较小，易保持清洁度"}
     */

    private String date;
    private String airQuality;
    private String city;
    private String windLevel;
    private String curTemp;
    private String ziwaixian;
    private String pm25;
    private String weather;
    private String humidity;
    private String tempRange;
    private String sourceName;
    private String dateLong;
    private String wind;
    private String lastUpdateTime;
    private WashCarBean wash_car;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAirQuality() {
        return airQuality;
    }

    public void setAirQuality(String airQuality) {
        this.airQuality = airQuality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWindLevel() {
        return windLevel;
    }

    public void setWindLevel(String windLevel) {
        this.windLevel = windLevel;
    }

    public String getCurTemp() {
        return curTemp;
    }

    public void setCurTemp(String curTemp) {
        this.curTemp = curTemp;
    }

    public String getZiwaixian() {
        return ziwaixian;
    }

    public void setZiwaixian(String ziwaixian) {
        this.ziwaixian = ziwaixian;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getTempRange() {
        return tempRange;
    }

    public void setTempRange(String tempRange) {
        this.tempRange = tempRange;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getDateLong() {
        return dateLong;
    }

    public void setDateLong(String dateLong) {
        this.dateLong = dateLong;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public WashCarBean getWash_car() {
        return wash_car;
    }

    public void setWash_car(WashCarBean wash_car) {
        this.wash_car = wash_car;
    }

    public static class WashCarBean {
        /**
         * title : 较适宜
         * desc : 无雨且风力较小，易保持清洁度
         */

        private String title;
        private String desc;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    @Override
    public String toString() {
        return "Weather{" +
                "date='" + date + '\'' +
                ", airQuality='" + airQuality + '\'' +
                ", city='" + city + '\'' +
                ", windLevel='" + windLevel + '\'' +
                ", curTemp='" + curTemp + '\'' +
                ", ziwaixian='" + ziwaixian + '\'' +
                ", pm25='" + pm25 + '\'' +
                ", weather='" + weather + '\'' +
                ", humidity='" + humidity + '\'' +
                ", tempRange='" + tempRange + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", dateLong='" + dateLong + '\'' +
                ", wind='" + wind + '\'' +
                ", lastUpdateTime='" + lastUpdateTime + '\'' +
                ", wash_car=" + wash_car +
                '}';
    }
}
