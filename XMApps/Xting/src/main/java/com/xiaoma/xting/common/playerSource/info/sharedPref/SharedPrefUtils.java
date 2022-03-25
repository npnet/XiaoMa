package com.xiaoma.xting.common.playerSource.info.sharedPref;

import android.content.Context;

import com.xiaoma.component.AppHolder;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.tputils.TPUtils;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.utils.PrintInfo;
import com.xiaoma.xting.launcher.XtingAudioClient;
import com.xiaoma.xting.local.model.BaseChannelBean;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/28
 */
public class SharedPrefUtils {

    private static final String TAG = "Cache_SharedPref";

    private static final String KEY_LIST_RECOMMEND = "list_recommend.key";
    private static final String KEY_PLAY_MODE = "playMode.key";
    private static final String KEY_PLAY_INFO = "playInfo.key";
    private static final String LIST_RANK = "list_rank";
    private static final String YQ_RECORD = "yq_player_record";
    private static final String YQ_FM_RECORD = "yq_fm_player_record";
    private static final String YQ_AM_RECORD = "yq_am_player_record";

    private SharedPrefUtils() {
    }

    public static void cachePlayerInfo(Context context, PlayerInfo playerInfo) {
        playerInfo.setCategoryId(XtingAudioClient.newSingleton(context).getCurCategoryId());
        PrintInfo.print(TAG, "cachePlayerInfo", String.format("PlayerInfo = %1$s", playerInfo));
        TPUtils.putObject(context, KEY_PLAY_INFO, playerInfo);
    }

    public static PlayerInfo getCachedPlayerInfo(Context context) {
        PlayerInfo playerInfo = TPUtils.getObject(context, KEY_PLAY_INFO, PlayerInfo.class);
        PrintInfo.print(TAG, "getCachedPlayerInfo", String.valueOf(playerInfo));
        return playerInfo;
    }

    public static void clearCachedPlayerInfo(Context context) {
        TPUtils.remove(context, KEY_PLAY_INFO);
        PrintInfo.print(TAG, "clearCachedPlayerInfo");
    }

    public static void cacheRecommendList(Context context, List<PlayerInfo> list) {
        TPUtils.putList(context, KEY_LIST_RECOMMEND, list);
        String s = GsonHelper.toJson(list);
        PrintInfo.print(TAG, "cacheRecommendList", "JSON Info" + s);
    }

    public static List<PlayerInfo> getCachedRecommendList(Context context) {
        return TPUtils.getList(context, KEY_LIST_RECOMMEND, PlayerInfo[].class);
    }

    public static void cacheRankList(Context context, long rankListId, List<PlayerInfo> list) {
        TPUtils.putList(context, LIST_RANK + "_" + rankListId, list);
    }

    public static List<PlayerInfo> getCachedRankList(Context context, long rankListId) {
        return TPUtils.getList(context, LIST_RANK + "_" + rankListId, PlayerInfo[].class);
    }

    public static void cachePlayMode(Context context, int playMode) {
        TPUtils.put(context, KEY_PLAY_MODE, playMode);
    }

    public static int getPlayMode(Context context) {
        return TPUtils.get(context, KEY_PLAY_MODE, -1);
    }

    public static void cacheYQRadioLast(int channelValue) {
        TPUtils.put(AppHolder.getInstance().getAppContext(), YQ_RECORD, channelValue);
    }

    public static BaseChannelBean getLastYQPlayerInfo(boolean isFM) {
        Integer channelValue = TPUtils.get(AppHolder.getInstance().getAppContext(), YQ_RECORD, isFM ? XtingConstants.FMAM.getFMStart() : XtingConstants.FMAM.getAMStart());
        return XtingUtils.getChannelByValue(channelValue);
    }

    public static void cacheLastFM(int channelValue) {
        TPUtils.put(AppHolder.getInstance().getAppContext(), YQ_FM_RECORD, channelValue);
    }

    public static BaseChannelBean getCacheLastFM() {
        Integer channelValue = TPUtils.get(AppHolder.getInstance().getAppContext(), YQ_FM_RECORD, XtingConstants.FMAM.getFMStart());
        return XtingUtils.getChannelByValue(channelValue);
    }

    public static void cacheLastAM(int channelValue) {
        TPUtils.put(AppHolder.getInstance().getAppContext(), YQ_AM_RECORD, channelValue);
    }

    public static BaseChannelBean getCacheLastAM() {
        Integer channelValue = TPUtils.get(AppHolder.getInstance().getAppContext(), YQ_AM_RECORD, XtingConstants.FMAM.getAMStart());
        return XtingUtils.getChannelByValue(channelValue);
    }

    public static void clearCachedYQRadioLast() {
        TPUtils.remove(AppHolder.getInstance().getAppContext(), YQ_RECORD);
    }

    public static void clearCachedPlayerInfo() {
        TPUtils.remove(AppHolder.getInstance().getAppContext(), KEY_PLAY_INFO);
    }
}
