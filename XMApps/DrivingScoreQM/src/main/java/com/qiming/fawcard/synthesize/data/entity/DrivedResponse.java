package com.qiming.fawcard.synthesize.data.entity;

import com.qiming.fawcard.synthesize.base.BaseResponse;
import com.xiaoma.utils.TimeUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by My on 2018/7/2.
 */

public class DrivedResponse extends BaseResponse {
    public List<Bean> result;

    public List<Bean> getResult() {
        return result;
    }

    public void setResult(List<Bean> result) {
        this.result = result;
    }

    public static class Bean implements Serializable {

        public String vin; //车驾号
        public String vehSeries;//车系
        public Long startTime;//开始时间（时间戳
        public Long endTime;//结束时间（时间戳
        public Long travelTime;//行驶时间（秒 s
        public Double travelOdograph;//行驶里程（km
        public Double avgFuelConsumer;//平均油耗（L/100km
        public Double avgSpeed; //平均车速(km/h
        public Double outAvgTemp;//车外平均温度（
        public Double airAvgTemp;//空调平均温度（
        public Long airUserTime;//空调使用时长（秒 s
        public Long rapidAccelerateNum;//急加速次数
        public Double rapidAccelerateTime;//急加速持续时间（秒 s
        public Long rapidDecelerationNum;//急减速次数
        public Double rapidDecelerationTime;//急减速时间（秒 s
        public Long sharpTurnNum;//急转弯次数
        public Double sharpTurnTime;//急转弯持续时间（秒 s
        public Long exceedSpeedNum;//超速行驶次数
        public Double exceedSpeedTime;//超速行驶持续时间（秒 s
        public Long safetyBelt;//安全带事件
        public Double safetyBeltTime;//未系安全带持续时间（秒 s
        public Long handsOutNum;//双手离开方向盘次数
        public Double handsOutTime;//双手离开方向盘时间（s
        public Long faultDrive;//故障行驶次数
        public Double faultDriveTime;//故障行驶事件时间(s
        public Long travelNum;//驾驶次数
        public Long fatigueDrive;//疲劳驾驶
        public Double fatigueDriveTime;//疲劳驾驶持续时间(s
        public Double score;//得分

        private String _class;
        private long generateTime;
        private double fuelConsumerRate;
        private int totalFuelConsumer;
        private String startStatus;
        private String endStatus;
        private int totalOdograph;
        private int startlongitude;//开始位置
        private int startlatitude;
        private int endlongitude;//结束位置
        private int endlatitude;
        private double highestSpeed;//最高车速
        private double fuelConsumerStats;

        public double getFuelConsumerRate() {
            return fuelConsumerRate;
        }

        public void setFuelConsumerRate(double fuelConsumerRate) {
            this.fuelConsumerRate = fuelConsumerRate;
        }

        public double getFuelConsumerStats() {
            return fuelConsumerStats;
        }

        public void setFuelConsumerStats(double fuelConsumerStats) {
            this.fuelConsumerStats = fuelConsumerStats;
        }

        private int scoreRank;


        public String getVin() {
            return vin;
        }

        public void setVin(String vin) {
            this.vin = vin;
        }

        public String getVehSeries() {
            return vehSeries;
        }

        public void setVehSeries(String vehSeries) {
            this.vehSeries = vehSeries;
        }

        public Long getStartTime() {
            return startTime;
        }

        public void setStartTime(Long startTime) {
            this.startTime = startTime;
        }

        public Long getEndTime() {
            return endTime;
        }

        public void setEndTime(Long endTime) {
            this.endTime = endTime;
        }

        public Long getTravelTime() {
            if (travelTime == null) {
                return 0L;
            }
            return travelTime;
        }

        public void setTravelTime(Long travelTime) {
            this.travelTime = travelTime;
        }

        public Double getTravelOdograph() {
            return travelOdograph;
        }

        public void setTravelOdograph(Double travelOdograph) {
            this.travelOdograph = travelOdograph;
        }

        public Double getAvgFuelConsumer() {
            return avgFuelConsumer;
        }

        public void setAvgFuelConsumer(Double avgFuelConsumer) {
            this.avgFuelConsumer = avgFuelConsumer;
        }

        public Double getAvgSpeed() {
            return avgSpeed;
        }

        public void setAvgSpeed(Double avgSpeed) {
            this.avgSpeed = avgSpeed;
        }

        public Double getOutAvgTemp() {
            return outAvgTemp;
        }

        public void setOutAvgTemp(Double outAvgTemp) {
            this.outAvgTemp = outAvgTemp;
        }

        public Double getAirAvgTemp() {
            return airAvgTemp;
        }

        public void setAirAvgTemp(Double airAvgTemp) {
            this.airAvgTemp = airAvgTemp;
        }

        public Long getAirUserTime() {
            return airUserTime;
        }

        public void setAirUserTime(Long airUserTime) {
            this.airUserTime = airUserTime;
        }

        public Long getRapidAccelerateNum() {
            return rapidAccelerateNum;
        }

        public void setRapidAccelerateNum(Long rapidAccelerateNum) {
            this.rapidAccelerateNum = rapidAccelerateNum;
        }

        public Double getRapidAccelerateTime() {
            return rapidAccelerateTime;
        }

        public void setRapidAccelerateTime(Double rapidAccelerateTime) {
            this.rapidAccelerateTime = rapidAccelerateTime;
        }

        public Long getRapidDecelerationNum() {
            return rapidDecelerationNum;
        }

        public void setRapidDecelerationNum(Long rapidDecelerationNum) {
            this.rapidDecelerationNum = rapidDecelerationNum;
        }

        public Double getRapidDecelerationTime() {
            return rapidDecelerationTime;
        }

        public void setRapidDecelerationTime(Double rapidDecelerationTime) {
            this.rapidDecelerationTime = rapidDecelerationTime;
        }

        public Long getSharpTurnNum() {
            return sharpTurnNum;
        }

        public void setSharpTurnNum(Long sharpTurnNum) {
            this.sharpTurnNum = sharpTurnNum;
        }

        public Double getSharpTurnTime() {
            return sharpTurnTime;
        }

        public void setSharpTurnTime(Double sharpTurnTime) {
            this.sharpTurnTime = sharpTurnTime;
        }

        public Long getExceedSpeedNum() {
            return exceedSpeedNum;
        }

        public void setExceedSpeedNum(Long exceedSpeedNum) {
            this.exceedSpeedNum = exceedSpeedNum;
        }

        public Double getExceedSpeedTime() {
            return exceedSpeedTime;
        }

        public void setExceedSpeedTime(Double exceedSpeedTime) {
            this.exceedSpeedTime = exceedSpeedTime;
        }

        public Long getSafetyBelt() {
            return safetyBelt;
        }

        public void setSafetyBelt(Long safetyBelt) {
            this.safetyBelt = safetyBelt;
        }

        public Double getSafetyBeltTime() {
            return safetyBeltTime;
        }

        public void setSafetyBeltTime(Double safetyBeltTime) {
            this.safetyBeltTime = safetyBeltTime;
        }

        public Long getHandsOutNum() {
            return handsOutNum;
        }

        public void setHandsOutNum(Long handsOutNum) {
            this.handsOutNum = handsOutNum;
        }

        public Double getHandsOutTime() {
            return handsOutTime;
        }

        public void setHandsOutTime(Double handsOutTime) {
            this.handsOutTime = handsOutTime;
        }

        public Long getFaultDrive() {
            return faultDrive;
        }

        public void setFaultDrive(Long faultDrive) {
            this.faultDrive = faultDrive;
        }

        public Long getTravelNum() {
            return travelNum;
        }

        public void setTravelNum(Long travelNum) {
            this.travelNum = travelNum;
        }

        public Double getFaultDriveTime() {
            return faultDriveTime;
        }

        public void setFaultDriveTime(Double faultDriveTime) {
            this.faultDriveTime = faultDriveTime;
        }

        public Long getFatigueDrive() {
            return fatigueDrive;
        }

        public void setFatigueDrive(Long fatigueDrive) {
            this.fatigueDrive = fatigueDrive;
        }

        public Double getFatigueDriveTime() {
            return fatigueDriveTime;
        }

        public void setFatigueDriveTime(Double fatigueDriveTime) {
            this.fatigueDriveTime = fatigueDriveTime;
        }

        public double getHighestSpeed() {
            return highestSpeed;
        }

        public void setHighestSpeed(double highestSpeed) {
            this.highestSpeed = highestSpeed;
        }

        public Double getScore() {
            return score;
        }

        public void setScore(Double score) {
            this.score = score;
        }

        public String get_class() {
            return _class;
        }

        public void set_class(String _class) {
            this._class = _class;
        }

        public long getGenerateTime() {
            return generateTime;
        }

        public void setGenerateTime(long generateTime) {
            this.generateTime = generateTime;
        }


        public int getTotalFuelConsumer() {
            return totalFuelConsumer;
        }

        public void setTotalFuelConsumer(int totalFuelConsumer) {
            this.totalFuelConsumer = totalFuelConsumer;
        }

        public String getStartStatus() {
            return startStatus;
        }

        public void setStartStatus(String startStatus) {
            this.startStatus = startStatus;
        }

        public String getEndStatus() {
            return endStatus;
        }

        public void setEndStatus(String endStatus) {
            this.endStatus = endStatus;
        }

        public int getTotalOdograph() {
            return totalOdograph;
        }

        public void setTotalOdograph(int totalOdograph) {
            this.totalOdograph = totalOdograph;
        }

        public int getStartlongitude() {
            return startlongitude;
        }

        public void setStartlongitude(int startlongitude) {
            this.startlongitude = startlongitude;
        }

        public int getStartlatitude() {
            return startlatitude;
        }

        public void setStartlatitude(int startlatitude) {
            this.startlatitude = startlatitude;
        }

        public int getEndlongitude() {
            return endlongitude;
        }

        public void setEndlongitude(int endlongitude) {
            this.endlongitude = endlongitude;
        }

        public int getEndlatitude() {
            return endlatitude;
        }

        public void setEndlatitude(int endlatitude) {
            this.endlatitude = endlatitude;
        }

        public int getScoreRank() {
            return scoreRank;
        }

        public void setScoreRank(int scoreRank) {
            this.scoreRank = scoreRank;
        }

        @Override
        public String toString() {
            return "Bean{" +
                    "avgFuelConsumer=" + avgFuelConsumer +
                    ", avgSpeed=" + avgSpeed +
                    ", rapidAccelerateNum=" + rapidAccelerateNum +
                    ", rapidDecelerationNum=" + rapidDecelerationNum +
                    ", sharpTurnNum=" + sharpTurnNum +
                    ", score=" + score +
                    ", startTime=" + TimeUtils.date2String(new Date(startTime)) +
                    ", endTime=" + TimeUtils.date2String(new Date(endTime))  +
                    '}';
        }
    }
}
