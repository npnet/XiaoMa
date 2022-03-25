package com.xiaoma.autotracker.upload;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.xiaoma.autotracker.model.AutoTrackInfo;

/**
 * @author taojin
 * @date 2018/12/24
 */
public interface CollectBasisInfo {

    /**
     * 收集APP页面前台事件
     *
     * @param activity
     * @param trackInfo
     */
    void saveAppViewScreenEvent(Activity activity, AutoTrackInfo trackInfo);

    /**
     * 收集应用前后台事件
     *
     * @param appViewType
     */
    void saveAppEvent(int appViewType);

    /**
     * 收集Fragment链路跳转事件
     *
     * @param fragment
     * @param trackInfo
     */
    void saveFragmentViewScreenEvent(Fragment fragment, AutoTrackInfo trackInfo);

    /**
     * 收集点击事件到数据库
     *
     * @param event    控件名称
     * @param id       控件点击对应的业务id
     * @param pagePath 页面路径
     * @param pageName 页面中文意思
     */
    void saveOnClickEvent(String event, String id, String pagePath, String pageName);

}
