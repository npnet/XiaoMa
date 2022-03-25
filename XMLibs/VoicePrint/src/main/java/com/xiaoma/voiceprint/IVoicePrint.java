package com.xiaoma.voiceprint;

import android.content.Context;
import android.support.annotation.Nullable;

import com.xiaoma.voiceprint.model.ICallback;

import java.io.File;
import java.util.List;

/**
 * @author KY
 * @date 11/22/2018
 */
interface IVoicePrint {
    /**
     * 初始化
     */
    void init(Context context);

    /**
     * SDK授权接口
     *
     * @param callback 回调
     */
    void oauth(ICallback.OauthCallback callback);

    /**
     * 添加组
     *
     * @param groupName groupName
     * @param describe  describe可为空
     * @param callback  回调
     */
    void addGroup(String groupName, @Nullable String describe, ICallback.AddGroupCallback callback);

    /**
     * 获取组
     *
     * @param groupid  groupid
     * @param callback 回调
     */
    void getGroup(String groupid, ICallback.GetGroupCallback callback);

    /**
     * 批量移除组
     *
     * @param groupIds groupIds
     * @param callback 回调
     */
    void removeGroup(List<String> groupIds, ICallback.CommonCallback callback);

    /**
     * 添加用户
     *
     * @param userName userName
     * @param describe describe可为空
     * @param callback 回调返回userId
     */
    void addUser(String userName, @Nullable String describe, ICallback.AddUserCallback callback);

    /**
     * 批量移用户
     *
     * @param clientIds clientIds
     * @param callback 回调
     */
    void removeUser(List<String> clientIds, String groupId, ICallback.CommonCallback callback);

    /**
     * 声纹上传
     *
     * @param file     声纹文件
     * @param callback 回调返回远端的声纹文件ID，用于以后比对声纹
     */
    void uploadVoicePrint(File file, ICallback.UploadCallback callback);

    /**
     * 获取声纹的读取文本
     *
     * @param modeltype      声纹模型Type
     * @param modeltextcount 文本数目
     * @param callback       回调返回文本列表
     */
    void getTrainingText(String modeltype, int modeltextcount, ICallback.GetTextCallback callback);

    /**
     * 注册声纹
     *
     * @param clientId          clientId
     * @param src_sample_rate 采样率
     * @param modeltype       声纹模型Type
     * @param fileidlist      声纹文件id list
     * @param callback        回调返回用户声的纹特征
     */
    void registerVoicePrint(String clientId, int src_sample_rate, String modeltype, List<String> fileidlist, ICallback.RegisterVoicePrintCallback callback);

    /**
     * 验证声纹
     *
     * @param clientId          clientId
     * @param src_sample_rate 采样率
     * @param modeltype       声纹模型Type
     * @param fileidlist      声纹文件id list
     * @param limitCount      结果集的数目上限
     * @param callback        回调返回所验证结果
     */
    void verifyVoicePrint(String clientId, int src_sample_rate, String modeltype, List<String> fileidlist, int limitCount, ICallback.VoicePrintVerifyCallback callback);
}
