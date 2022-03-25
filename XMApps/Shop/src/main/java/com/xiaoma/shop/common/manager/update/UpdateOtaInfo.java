package com.xiaoma.shop.common.manager.update;

import com.litesuits.orm.db.annotation.Default;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.constant.UpdateResouceType;

/**
 * Author: Ljb
 * Time  : 2019/6/30
 * Description:
 */
@Table("UpdateOtaInfo")
public class UpdateOtaInfo {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private long _id;
    private int progress;

    private String fileUrl;
    /**
     * {@link com.fsl.android.uniqueota.UniqueOtaConstants.EcuId}
     */
    private int ecu;

    private int installState;

    private @ResourceType
    int resType;

    @Default(value = "-1")
    private @UpdateResouceType
    int updateResType;

    @Ignore
    public static final String ECU = "ecu";
    @Ignore
    public static final String FILE_URL = "fileUrl";
    @Ignore
    public static final String INSTALL_STATE = "installState";


    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }


    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }


    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public int getEcu() {
        return ecu;
    }

    public void setEcu(int ecu) {
        this.ecu = ecu;
    }

    public int getInstallState() {
        return installState;
    }

    public int getResType() {
        return resType;
    }

    public void setResType(int resType) {
        this.resType = resType;
    }

    public void setInstallState(int installState) {
        this.installState = installState;
    }

    public int getUpdateResType() {
        return updateResType;
    }

    public void setUpdateResType(int updateResType) {
        this.updateResType = updateResType;
    }

    @Override
    public String toString() {
        return "UpdateOtaInfo{" +
                "_id=" + _id +
                ", progress=" + progress +
                ", fileUrl='" + fileUrl + '\'' +
                ", ecu=" + ecu +
                ", installState=" + installState +
                ", resType=" + resType +
                ", updateResType=" + updateResType +
                '}';
    }

    public static class InstallState {
        public static final int NONE = 0;        // 闲置状态
        public static final int INSTALL_PRE = 1;  // 安装前准备状态
        public static final int INSTALLING = 2;  // 安装中
        public static final int INSTALL_SUCCESSFUL = 3;  // 安装成功
        public static final int INSTALL_FAILED = 4;      // 安装失败


        public static final int ROBOT_DELETE_S = 5;//robot 删除成功
        public static final int ROBOT_DELETE_F = 6;//robot 删除失败

        public static final int COPY_FILE= 7;      // 文件复制

    }
}
