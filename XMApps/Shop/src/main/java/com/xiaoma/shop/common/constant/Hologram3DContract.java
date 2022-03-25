package com.xiaoma.shop.common.constant;

/**
 * Author: loren
 * Date: 2019/8/14
 */
public interface Hologram3DContract {

    int STATE_DOWNLOAD = 1; //下载
    int STATE_DOWNLOAD_PROGRESS = 2; //下载中
    int STATE_DOWNLOAD_FAIL = 3; //下载失败
    int STATE_DOWNLOAD_SUCCESS = 4; //下载成功

    int STATE_INSTALL_PROGRESS = 5;
    int STATE_INSTALL_FAIL = 6;
    int STATE_INSTALL_SUCCESS = 6;

    int STATE_UPDATE = 8;
    int STATE_USING = 9; // 使用中

//    int STATE_USE = 3; //使用
//    int STATE_UPDATE = 4; // 更新 新的3d形象
//    int STATE_INSTALLING = 5; //升级中 推送到3d设备中
}
