package com.xiaoma.club.common.viewmodel;

/**
 * Created by LKF on 2019-1-24 0024.
 * View状态的枚举,可以在ViewModel中通用
 */
public enum ViewState {
    LOADING_INITIAL,// 初次加载
    LOADING_BEFORE,// 加载上一页
    LOADING_AFTER,// 加载下一页
    NORMAL,// 常态
    EMPTY_DATA,// 空数据
    NETWORK_ERROR,// 网络异常
    SERVICE_ERROR,// 后台异常
}
