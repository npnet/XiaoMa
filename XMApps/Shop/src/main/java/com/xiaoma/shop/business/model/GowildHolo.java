package com.xiaoma.shop.business.model;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/22
 */
public class GowildHolo {

    /**
     * id : 2
     * createDate : 1556074936000
     * channelId : AA1090
     * openId : xm_VCUaiZFrnqjWGq73EkV
     * downloadCode : 123456
     * downloadUrl : 23456
     * fieldCode : 5
     * imageId : 123456
     */

    private int id;
    private long createDate;
    private String channelId;
    private String openId;
    private String downloadCode;
    private String downloadUrl;
    private int fieldCode;
    private String imageId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getDownloadCode() {
        return downloadCode;
    }

    public void setDownloadCode(String downloadCode) {
        this.downloadCode = downloadCode;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(int fieldCode) {
        this.fieldCode = fieldCode;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}
