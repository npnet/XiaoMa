package com.xiaoma.songname.api;

import com.xiaoma.config.ConfigManager;

/**
 * Created by Thomas on 2018/10/13 0013
 * SongNameApi API
 */

public interface SongNameApi {

    String BASE_URL = ConfigManager.EnvConfig.getEnv().getBusiness();

    /**
     * 获取题目
     */
    String GET_SUBJECT = BASE_URL + "guessSong/getSubjectByUid";

    /**
     * 回答正确后上报
     */
    String REPORT_RESULT = BASE_URL + "guessSong/reportResult";

    /**
     * 获取当前答题积分排行榜
     */
    String RANKING_LIST = BASE_URL + "guessSong/getRankingList";

    /**
     * 退出游戏获取当前积分结算情况
     */
    String GET_TOTAL_POINTS = BASE_URL + "guessSong/secedeGame";

    /**
     * 获取用户个性签名
     */
    String GET_USER_SIGN = BASE_URL + "guessSong/getPersonalSignature";

    /**
     * 申请加好友
     */
    String REQUEST_ADD_FRIEND = BASE_URL + "user/applyFriend";
}
