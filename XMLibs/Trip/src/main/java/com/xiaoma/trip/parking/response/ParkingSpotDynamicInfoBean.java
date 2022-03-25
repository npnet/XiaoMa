package com.xiaoma.trip.parking.response;

public class ParkingSpotDynamicInfoBean {
    /**
     * id : t5NG71nV
     * availablePlaces : null
     * difficulty : 1
     * feeLevel : null
     * feeLevelEx : null
     * feePredict : 0.0
     * findTime : 2
     * recommendation : 1
     * status : 忙
     */

    private String id;
    private Object availablePlaces;
    private String difficulty;
    private Object feeLevel;
    private Object feeLevelEx;
    private String feePredict;
    private String findTime;
    private String recommendation;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getAvailablePlaces() {
        return availablePlaces;
    }

    public void setAvailablePlaces(Object availablePlaces) {
        this.availablePlaces = availablePlaces;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Object getFeeLevel() {
        return feeLevel;
    }

    public void setFeeLevel(Object feeLevel) {
        this.feeLevel = feeLevel;
    }

    public Object getFeeLevelEx() {
        return feeLevelEx;
    }

    public void setFeeLevelEx(Object feeLevelEx) {
        this.feeLevelEx = feeLevelEx;
    }

    public String getFeePredict() {
        return feePredict;
    }

    public void setFeePredict(String feePredict) {
        this.feePredict = feePredict;
    }

    public String getFindTime() {
        return findTime;
    }

    public void setFindTime(String findTime) {
        this.findTime = findTime;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
