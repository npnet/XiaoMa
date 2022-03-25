package com.xiaoma.ad.utils;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.xiaoma.ad.AdManager;
import com.xiaoma.ad.models.Ad;
import com.xiaoma.ad.models.LinkType;
import com.xiaoma.ad.provider.SharedPreferenceProxy;
import com.xiaoma.utils.StringUtil;

import java.util.List;

/**
 * @author KY
 * @date 2018/9/13
 */
public class AdSpUtil {

    private static final String SP_NAME = "xiaomaAd";
    private static final String AD_REQUEST_DATE_KEY = "ad_request_date_key";
    private static final String AD_SHOW_DATE_KEY = "ad_show_date_key";
    private static final String AD_DATA_KEY = "ad_data_key";
    private static SharedPreferences preferences = null;


    /**
     * 在 PreferenceUtil中判断当前进程是否为provider所在进程，进而通过不同的方式获得相同的 preferences
     *
     * @return SharedPreferences
     */
    private static SharedPreferences getSpInstance() {
        if (preferences == null) {
            synchronized (AdSpUtil.class) {
                if (preferences == null) {
                    preferences = SharedPreferenceProxy.getSharedPreferences(AdManager.getContext(), SP_NAME);
                }
            }
        }

        return preferences;
    }

    /**
     * 保存广告列表
     *
     * @param ads ads
     */
    @SuppressLint("ApplySharedPref")
    public static void saveAd(List<Ad> ads) {
        getSpInstance().edit().putString(AD_DATA_KEY, GsonUtil.toJson(ads)).commit();
    }

    /**
     * 读取广告列表
     *
     * @return ads
     */
    public static List<Ad> getCachedAds() {
        String json = getSpInstance().getString(AD_DATA_KEY, null);
        List<Ad> ads = GsonUtil.fromJsonToList(json, Ad[].class);
        return ads;

//        if (ads.isEmpty()) {
//            return Collections.singletonList(getDefaultAd());
//
//        } else {
//            return ads;
//        }
    }

    /**
     * 记录广告的同步时间
     */
    @SuppressLint("ApplySharedPref")
    public static void markSync() {
        getSpInstance().edit().putString(AD_REQUEST_DATE_KEY, StringUtil.getDateByYMD()).commit();
    }

    /**
     * 获取上一次广告的同步时间
     *
     * @return 上一次同步时间
     */
    public static String getSyncMark() {
        return getSpInstance().getString(AD_REQUEST_DATE_KEY, "");
    }

    /**
     * 记录广告显示的时间
     */
    @SuppressLint("ApplySharedPref")
    public static void markAdShowSync() {
        getSpInstance().edit().putString(AD_SHOW_DATE_KEY, StringUtil.getDateByYMD()).commit();
    }

    /**
     * 获取上一次广告显示的时间
     *
     * @return 上一次同步时间
     */
    public static String getAdShowSyncMark() {
        return getSpInstance().getString(AD_SHOW_DATE_KEY, "");
    }

    @NonNull
    private static Ad getDefaultAd() {
        return new Ad(-1, "file:///android_asset/notice/default_loading_notice.jpg",
                LinkType.NONE, "", 1536811417732L,
                1914702035000L, 5, true);
    }

}
