package com.xiaoma.voiceprint.model;

import java.util.List;

/**
 * @author KY
 * @date 11/22/2018
 */
public interface ICallback {

    // 失败回调
    interface FailureCallback {
        void onFailure(String tag, String message);
    }

    // 用于SDK封装的回调
    interface CommonCallback extends FailureCallback {
        void onSuccess(String result);
    }

    /**
     * sdk授权
     */
    interface OauthCallback extends FailureCallback {
        void onOauthSuccess();
    }

    /**
     * 添加用户组
     */
    interface AddGroupCallback extends FailureCallback {
        void onGroupAddSuccess(String groupId);
    }

    /**
     * 获取用户组
     */
    interface GetGroupCallback extends FailureCallback {
        void onGroupGetSuccess(String groupId);
    }

    /**
     * 获取声纹验证文本
     */
    interface GetTextCallback extends FailureCallback {
        void onGetTextSuccess(List<String> texts);
    }

    /**
     * 声纹文件上传
     */
    interface UploadCallback extends FailureCallback {
        void onUploadSuccess(String fileId);
    }

    /**
     * 添加用户
     */
    interface AddUserCallback extends FailureCallback {
        void onUserAddSuccess(String clientId);
    }

    /**
     * 声纹注册
     */
    interface RegisterVoicePrintCallback extends FailureCallback {
        void onRegisterSuccess(String clientId);
    }

    /**
     * 声纹校验
     */
    interface VoicePrintVerifyCallback extends FailureCallback {
        void onVerifySuccess();
    }

    /**
     * 添加注册回调(包含添加用户和声纹注册)
     */
    interface AddRegisterCallback extends AddUserCallback, RegisterVoicePrintCallback {

    }

    /**
     * 初始化回调(包含oauth授权和添加默认组)
     */
    interface InitCallback extends OauthCallback, GetGroupCallback, AddGroupCallback {
    }
}
