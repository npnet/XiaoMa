package com.xiaoma.vrpractice.api;

import com.xiaoma.config.ConfigManager;

/**
 * Created by Thomas on 2018/10/13 0013
 * VrPracticeAPI API
 */

public interface VrPracticeAPI {

    String BASE_URL = ConfigManager.EnvConfig.getEnv().getBusiness();

    /**
     * 获取城市列表
     */
//    String GET_PROVINCE_AND_CITY = BASE_URL + "carserve/getProvinceAndCity";
    String GET_PROVINCE_AND_CITY = BASE_URL + "chinaRegion/getAllRegion";
    String GET_NEWS_CHANNEL = BASE_URL + "query/newsChannel.action";

    //获取所有技能项目
    String GET_SKILL_ITEM_LIST = BASE_URL + "skill/getSkillItemList.action";

    //获取所有技能列表
    String GET_SKILL_LIST = BASE_URL + "skill/getSkillList.action";

    //保存创建的新技能
    String SAVE_NEW_SKILL = BASE_URL + "skill/createSkills.action";

    //编辑已创建的新技能
    String EDIT_SKILL = BASE_URL + "skill/editSkills.action";

    //用于校验输入的话述文字，避免重复
    String CHECK_SKILL_WORD = BASE_URL + "skill/checkDuplicateWord.action";

    //删除技能
    String DEL_SKILL = BASE_URL + "skill/deleteSkillById.action";

    //获取音乐搜索列表
//    String GET_MUSIC_LIST=BASE_URL+"rest/music/searchByName.action";
    String GET_MUSIC_LIST = BASE_URL + "music/searchByName.action";
    //获取电台搜索列表
//    String GET_RADIO_LIST=BASE_URL+"rest/audio/getRadioListByTitle.action";
    String GET_RADIO_LIST = BASE_URL + "udio/getRadioListByTitle.action";
}
