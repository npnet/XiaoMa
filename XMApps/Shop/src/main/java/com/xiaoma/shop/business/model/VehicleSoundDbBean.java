package com.xiaoma.shop.business.model;

import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/06/12
 * @Describe:
 */
@Table("VehicleSoundDb")
public class VehicleSoundDbBean {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private long _id;
    private long id;            //产品ID
    private String filePath;    // 文件路径
    private int resourceType;   // 资源类型
    private String userId;      // 用户Id

    private long downloadTime; //下载的时间
    @Ignore
    public static final String ID ="id";
    @Ignore
    public static final String FILE_Path ="filePath";
    @Ignore
    public static final String RESOURCE_TYPE ="resourceType";
    @Ignore
    public static final String USER_ID ="userId";
    public static final String DOWNLOAD_TIME="downloadTime";
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getResourceType() {
        return resourceType;
    }

    public void setResourceType(int resourceType) {
        this.resourceType = resourceType;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getDownloadTime() {
        return downloadTime;
    }

    public void setDownloadTime(long downloadTime) {
        this.downloadTime = downloadTime;
    }

    @Override
    public String toString() {
        return "VehicleSoundDbBean{" +
                "_id=" + _id +
                ", id=" + id +
                ", filePath='" + filePath + '\'' +
                ", resourceType=" + resourceType +
                ", userId='" + userId + '\'' +
                ", downloadTime=" + downloadTime +
                '}';
    }
}
