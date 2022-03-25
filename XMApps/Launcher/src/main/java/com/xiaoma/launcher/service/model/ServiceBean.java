package com.xiaoma.launcher.service.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * @author taojin
 * @date 2019/1/10
 */
public class ServiceBean implements MultiItemEntity {

    private String serviceName;
    private int serviceBgImg;
    private String serviceEnsName;

    private int imgOneRes;
    private int imgTwoRes;
    private int imgThreeRes;
    private int imgFourRes;

    private String itemOne;
    private String itemTwo;
    private String itemThree;
    private String itemFour;
    private int itemType;
    private int itemServiceType;


    public ServiceBean(String serviceName, int serviceBgImg, String serviceEnsName, int imgOneRes, int imgTwoRes, int imgThreeRes, int imgFourRes, String itemOne, String itemTwo, String itemThree, String itemFour, int itemType, int itemServiceType) {
        this.serviceName = serviceName;
        this.serviceBgImg = serviceBgImg;
        this.serviceEnsName = serviceEnsName;
        this.imgOneRes = imgOneRes;
        this.imgTwoRes = imgTwoRes;
        this.imgThreeRes = imgThreeRes;
        this.imgFourRes = imgFourRes;
        this.itemOne = itemOne;
        this.itemTwo = itemTwo;
        this.itemThree = itemThree;
        this.itemFour = itemFour;
        this.itemType = itemType;
        this.itemServiceType = itemServiceType;
    }


    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getServiceBgImg() {
        return serviceBgImg;
    }

    public void setServiceBgImg(int serviceBgImg) {
        this.serviceBgImg = serviceBgImg;
    }

    public String getServiceEnsName() {
        return serviceEnsName;
    }

    public void setServiceEnsName(String serviceEnsName) {
        this.serviceEnsName = serviceEnsName;
    }

    public int getImgOneRes() {
        return imgOneRes;
    }

    public void setImgOneRes(int imgOneRes) {
        this.imgOneRes = imgOneRes;
    }

    public int getImgTwoRes() {
        return imgTwoRes;
    }

    public void setImgTwoRes(int imgTwoRes) {
        this.imgTwoRes = imgTwoRes;
    }

    public int getImgThreeRes() {
        return imgThreeRes;
    }

    public void setImgThreeRes(int imgThreeRes) {
        this.imgThreeRes = imgThreeRes;
    }

    public int getImgFourRes() {
        return imgFourRes;
    }

    public void setImgFourRes(int imgFourRes) {
        this.imgFourRes = imgFourRes;
    }

    public String getItemOne() {
        return itemOne;
    }

    public void setItemOne(String itemOne) {
        this.itemOne = itemOne;
    }

    public String getItemTwo() {
        return itemTwo;
    }

    public void setItemTwo(String itemTwo) {
        this.itemTwo = itemTwo;
    }

    public String getItemThree() {
        return itemThree;
    }

    public void setItemThree(String itemThree) {
        this.itemThree = itemThree;
    }

    public String getItemFour() {
        return itemFour;
    }

    public void setItemFour(String itemFour) {
        this.itemFour = itemFour;
    }

    public int getItemType() {
        return itemType;
    }

    public int getItemServiceType() {
        return itemServiceType;
    }
}
