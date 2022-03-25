package com.xiaoma.launcher.recommend.model;

import com.xiaoma.mqtt.model.DatasBean;

/**
 * @date: 17:10 2018/12/17 0017.
 * @author: huanghai
 * @Description: 喜泊客停车地点model
 */
public class ParkingSpot extends DatasBean {
    //停车场地址
    private String address;
    //标志性建筑物名称
    private String building;
    //所在城市
    private String cityName;
    //关闭时间
    private String closeTime;
    //主坐标 经度,维度(高德)
    private String coordinateAmap;
    //区县名称
    private String districtName;
    //入口路名
    private String entrance;
    //入口坐标 经度1,纬度1;经度2,纬度2(高德)
    private String entranceCoordinatesAmap;
    //出口路名
    private String exitus;
    //出口坐标 经度1,纬度1;经度2,纬度2(高德)
    private String exitusCoordinatesAmap;
    //支路远侧
    private String farSide;
    //扩充收费
    private String feeExtText;
    //参考收费
    private String feeRefText;
    //标准收费
    private String feeText;
    //节假日标准收费
    private String feeTextHoliday;
    //双休日标准收费
    private String feeTextWeekend;
    //停车场id
    private String id;
    //所在主干道
    private String mainRoad;
    //停车场地图文件名
    private String mapFile;
    //停车场名称
    private String name;
    //支路近侧
    private String nearSide;
    /**
     * 开放方式名称
     *  类型：
     *      完全对外
     *      选择性对外访客
     *      选择性对外顾客
     *      选择性对外住客
     *      选择性对外游客
     *      选择性对外病人
     *      选择性对外其他
     *
     * */
    private String openName;
    //开放原因
    private String openReason;
    //开放时间
    private String openTime;
    //其他时间说明
    private String otherTime;
    //暂无意义
    private String payment;
    /**
     * 收费方式名称
     * 类型：
     *      PSO机支付
     *      银联卡支付
     *      支付宝二维码
     *      微信二维码
     *      闪付二维码
     *      手机应用
     *      缴费机
     *      现金
     */
    private String paymentName;
    //照片文件名
    private String photo;
    //优惠π照片文件名称
    private String photoBenifitplate;
    //标志性建筑物照片文件名称
    private String photoBuilding;
    //入口照片文件名称
    private String photoEntrance;
    //出口照片文件名称
    private String photoExit;
    //铭牌1照片文件名
    private String photoNameplate1;
    //铭牌2照片文件名
    private String photoNameplate2;
    //路牌照片文件名
    private String photoStreetplate;
    //全景图片文件名
    private String photoView;
    //省份或者直辖市名称
    private String provinceName;
    //协议停车场的预约停车服务编号
    private String serveId;
    /**
     * 协议停车场的预约停车服务类型
     *
     * 1:临时停车服务
     * 2:包月停车服务
     * 3:机场停车服务
     *
     */
    private String serveType;
    //停车场服务提供商
    private String serviceBrand;
    /**
     * 形状名称
     *  占道型
     *      "H"型
     *      "T"型
     *      "C"型
     *      "O"型
     *       其他
     *  非占道型
     *      平面
     *      立体
     *      机械
     *      其他
     */
    private String shapeName;
    /**
     * 种类名称
     *  地面停车场
     *  地下停车库
     *  沿街停车场
     *  占道停车场
     *  立体停车楼
     *  综合型停车场
     *  其他
     */
    private String speciesName;
    // 总停车位
    private String totalPlaces;
    //子类别名称
    private String typeExName;
    /**
     * 主类别名称
     *    类型：
     *      小区
     *      写字楼
     *      医院
     *      公共事业
     *      政府机构
     *      交通枢纽
     *      购物中心
     *      专业停车场
     *      餐饮娱乐
     *      宾馆
     *      景点
     *      综合
     */
    private String typeName;
    /**
     * 更新时间
     */
    private String updateTime;


    private ParkingSpotDynamicInfo parkingSpotDynamicInfo;

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

    public String getPhotoBenifitplate() {
        return photoBenifitplate;
    }

    public void setPhotoBenifitplate(String photoBenifitplate) {
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

    public String getPhotoStreetplate() {
        return photoStreetplate;
    }

    public void setPhotoStreetplate(String photoStreetplate) {
        this.photoStreetplate = photoStreetplate;
    }

    public String getPhotoView() {
        return photoView;
    }

    public void setPhotoView(String photoView) {
        this.photoView = photoView;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getServeId() {
        return serveId;
    }

    public void setServeId(String serveId) {
        this.serveId = serveId;
    }

    public String getServeType() {
        return serveType;
    }

    public void setServeType(String serveType) {
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

    public ParkingSpotDynamicInfo getParkingSpotDynamicInfo() {
        return parkingSpotDynamicInfo;
    }

    public void setParkingSpotDynamicInfo(ParkingSpotDynamicInfo parkingSpotDynamicInfo) {
        this.parkingSpotDynamicInfo = parkingSpotDynamicInfo;
    }
}
