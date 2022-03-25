package com.xiaoma.assistant.model;

import android.content.Context;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.utils.CommonUtils;
import com.xiaoma.mapadapter.model.LatLng;
import com.xiaoma.mapadapter.utils.MapUtil;
import com.xiaoma.utils.ConvertUtils;

import java.io.Serializable;

/**
 * Created by ZhangYao.
 * Date ：2017/6/8 0008
 */

public class ParkInfo implements Serializable {


    /**
     * address : 红岭中路1028号
     * building : 国信南山温泉酒店
     * cityName : 深圳市
     * closeTime : 00:00
     * coordinateAmap : 125.5286,43.684769
     * districtName : 罗湖区
     * entrance : 红岭中路
     * entranceCoordinatesAmap : 125.5286,43.684769
     * exitus : 红岭中路
     * exitusCoordinatesAmap : 125.5286,43.684769
     * farSide : 红宝路
     * feeExtText :
     * feeRefText : 选择性对外顾客免费停放
     * feeText : 选择性对外顾客免费停放
     * feeTextHoliday : 选择性对外顾客免费停放
     * feeTextWeekend : 选择性对外顾客免费停放
     * id : t5NG71nV
     * mainRoad : 红岭中路
     * mapFile :
     * name : 国信南山温泉酒店停车场
     * nearSide : 红桂路
     * openName : 选择性对外顾客
     * openReason :
     * openTime : 00:00
     * otherTime :
     * payment : 00000000000001
     * paymentName : 其他
     * photo : entrance_3CDnGlEL.jpg
     * photoBenifitplate : null
     * photoBuilding : entrance_3CDnGlEL.jpg
     * photoEntrance : entrance_3CDnGlEL.jpg
     * photoExit : exit_EBb7FVgV.jpg
     * photoNameplate1 : null
     * photoNameplate2 : null
     * photoStreetplate : null
     * photoView : null
     * provinceName : 广东省
     * serveId : null
     * serveType : null
     * serviceBrand :
     * shapeName : 平面
     * speciesName : 地面停车场
     * totalPlaces : 54
     * typeExName : 星级宾馆
     * typeName : 宾馆
     * updateTime : 2018-08-16 11:00:57
     * parkingSpotDynamicInfo : {"id":"t5NG71nV","availablePlaces":null,"difficulty":"1","feeLevel":null,"feeLevelEx":null,"feePredict":"0.0","findTime":"2","recommendation":"1","status":"忙"}
     */

    private String address;
    private String building;
    private String cityName;
    private String closeTime;
    private String coordinateAmap;
    private String districtName;
    private String entrance;
    private String entranceCoordinatesAmap;
    private String exitus;
    private String exitusCoordinatesAmap;
    private String farSide;
    private String feeExtText;
    private String feeRefText;
    private String feeText;
    private String feeTextHoliday;
    private String feeTextWeekend;
    private String id;
    private String mainRoad;
    private String mapFile;
    private String name;
    private String nearSide;
    private String openName;
    private String openReason;
    private String openTime;
    private String otherTime;
    private String payment;
    private String paymentName;
    private String photo;
    private Object photoBenifitplate;
    private String photoBuilding;
    private String photoEntrance;
    private String photoExit;
    private String photoNameplate1;
    private String photoNameplate2;
    private Object photoStreetplate;
    private Object photoView;
    private String provinceName;
    private Object serveId;
    private Object serveType;
    private String serviceBrand;
    private String shapeName;
    private String speciesName;
    private String totalPlaces;
    private String typeExName;
    private String typeName;
    private String updateTime;
    private ParkingSpotDynamicInfoBean parkingSpotDynamicInfo;
    private String distance;
    private double longitude;
    private double latitude;

    public String getDistance(Context context, String location) {
        if (distance == null) {
            String[] locationArr = location.split(",");
            String[] parkArr = coordinateAmap.split(",");
            longitude = ConvertUtils.stringToDouble(parkArr[0]);
            latitude = ConvertUtils.stringToDouble(parkArr[1]);
            double distanceNum = MapUtil.calculateLineDistance(new LatLng(Double.parseDouble(locationArr[1]), Double.parseDouble(locationArr[0])),
                    new LatLng(latitude, longitude));
            if (distanceNum >= 1000) {
                distanceNum = distanceNum / 1000;
                distance = context.getString(R.string.thousand_address_distance, CommonUtils.getFormattedNumber(distanceNum));
            } else {
                distance = context.getString(R.string.address_distance, CommonUtils.getFormattedNumber(distanceNum));
            }
        }
        return distance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getCoordinateAmap() {
        return coordinateAmap;
    }

    public void setCoordinateAmap(String coordinateAmap) {
        this.coordinateAmap = coordinateAmap;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getEntrance() {
        return entrance;
    }

    public void setEntrance(String entrance) {
        this.entrance = entrance;
    }

    public String getEntranceCoordinatesAmap() {
        return entranceCoordinatesAmap;
    }

    public void setEntranceCoordinatesAmap(String entranceCoordinatesAmap) {
        this.entranceCoordinatesAmap = entranceCoordinatesAmap;
    }

    public String getExitus() {
        return exitus;
    }

    public void setExitus(String exitus) {
        this.exitus = exitus;
    }

    public String getExitusCoordinatesAmap() {
        return exitusCoordinatesAmap;
    }

    public void setExitusCoordinatesAmap(String exitusCoordinatesAmap) {
        this.exitusCoordinatesAmap = exitusCoordinatesAmap;
    }

    public String getFarSide() {
        return farSide;
    }

    public void setFarSide(String farSide) {
        this.farSide = farSide;
    }

    public String getFeeExtText() {
        return feeExtText;
    }

    public void setFeeExtText(String feeExtText) {
        this.feeExtText = feeExtText;
    }

    public String getFeeRefText() {
        return feeRefText;
    }

    public void setFeeRefText(String feeRefText) {
        this.feeRefText = feeRefText;
    }

    public String getFeeText() {
        return feeText;
    }

    public void setFeeText(String feeText) {
        this.feeText = feeText;
    }

    public String getFeeTextHoliday() {
        return feeTextHoliday;
    }

    public void setFeeTextHoliday(String feeTextHoliday) {
        this.feeTextHoliday = feeTextHoliday;
    }

    public String getFeeTextWeekend() {
        return feeTextWeekend;
    }

    public void setFeeTextWeekend(String feeTextWeekend) {
        this.feeTextWeekend = feeTextWeekend;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMainRoad() {
        return mainRoad;
    }

    public void setMainRoad(String mainRoad) {
        this.mainRoad = mainRoad;
    }

    public String getMapFile() {
        return mapFile;
    }

    public void setMapFile(String mapFile) {
        this.mapFile = mapFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNearSide() {
        return nearSide;
    }

    public void setNearSide(String nearSide) {
        this.nearSide = nearSide;
    }

    public String getOpenName() {
        return openName;
    }

    public void setOpenName(String openName) {
        this.openName = openName;
    }

    public String getOpenReason() {
        return openReason;
    }

    public void setOpenReason(String openReason) {
        this.openReason = openReason;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getOtherTime() {
        return otherTime;
    }

    public void setOtherTime(String otherTime) {
        this.otherTime = otherTime;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Object getPhotoBenifitplate() {
        return photoBenifitplate;
    }

    public void setPhotoBenifitplate(Object photoBenifitplate) {
        this.photoBenifitplate = photoBenifitplate;
    }

    public String getPhotoBuilding() {
        return photoBuilding;
    }

    public void setPhotoBuilding(String photoBuilding) {
        this.photoBuilding = photoBuilding;
    }

    public String getPhotoEntrance() {
        return photoEntrance;
    }

    public void setPhotoEntrance(String photoEntrance) {
        this.photoEntrance = photoEntrance;
    }

    public String getPhotoExit() {
        return photoExit;
    }

    public void setPhotoExit(String photoExit) {
        this.photoExit = photoExit;
    }

    public String getPhotoNameplate1() {
        return photoNameplate1;
    }

    public void setPhotoNameplate1(String photoNameplate1) {
        this.photoNameplate1 = photoNameplate1;
    }

    public String getPhotoNameplate2() {
        return photoNameplate2;
    }

    public void setPhotoNameplate2(String photoNameplate2) {
        this.photoNameplate2 = photoNameplate2;
    }

    public Object getPhotoStreetplate() {
        return photoStreetplate;
    }

    public void setPhotoStreetplate(Object photoStreetplate) {
        this.photoStreetplate = photoStreetplate;
    }

    public Object getPhotoView() {
        return photoView;
    }

    public void setPhotoView(Object photoView) {
        this.photoView = photoView;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public Object getServeId() {
        return serveId;
    }

    public void setServeId(Object serveId) {
        this.serveId = serveId;
    }

    public Object getServeType() {
        return serveType;
    }

    public void setServeType(Object serveType) {
        this.serveType = serveType;
    }

    public String getServiceBrand() {
        return serviceBrand;
    }

    public void setServiceBrand(String serviceBrand) {
        this.serviceBrand = serviceBrand;
    }

    public String getShapeName() {
        return shapeName;
    }

    public void setShapeName(String shapeName) {
        this.shapeName = shapeName;
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

    public String getTotalPlaces() {
        return totalPlaces;
    }

    public void setTotalPlaces(String totalPlaces) {
        this.totalPlaces = totalPlaces;
    }

    public String getTypeExName() {
        return typeExName;
    }

    public void setTypeExName(String typeExName) {
        this.typeExName = typeExName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public ParkingSpotDynamicInfoBean getParkingSpotDynamicInfo() {
        return parkingSpotDynamicInfo;
    }

    public void setParkingSpotDynamicInfo(ParkingSpotDynamicInfoBean parkingSpotDynamicInfo) {
        this.parkingSpotDynamicInfo = parkingSpotDynamicInfo;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public static class ParkingSpotDynamicInfoBean {
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
        private Integer availablePlaces;
        private String difficulty;
        private Integer feeLevel;
        private Integer feeLevelEx;
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

        public Integer getAvailablePlaces() {
            return availablePlaces;
        }

        public void setAvailablePlaces(Integer availablePlaces) {
            this.availablePlaces = availablePlaces;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(String difficulty) {
            this.difficulty = difficulty;
        }

        public Integer getFeeLevel() {
            return feeLevel;
        }

        public void setFeeLevel(Integer feeLevel) {
            this.feeLevel = feeLevel;
        }

        public Integer getFeeLevelEx() {
            return feeLevelEx;
        }

        public void setFeeLevelEx(Integer feeLevelEx) {
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
}

