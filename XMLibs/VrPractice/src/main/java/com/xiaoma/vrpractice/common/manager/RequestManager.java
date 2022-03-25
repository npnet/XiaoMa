package com.xiaoma.vrpractice.common.manager;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.vrpractice.api.VrPracticeAPI;
import com.xiaoma.model.pratice.NewsBean;
import com.xiaoma.model.pratice.PlayMusicBean;
import com.xiaoma.model.pratice.ProvinceBean;
import com.xiaoma.model.pratice.RadioBean;
import com.xiaoma.model.pratice.SkillBean;
import com.xiaoma.model.pratice.SkillItemBean;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Thomas on 2018/11/9 0009
 */

public class RequestManager {
    private RequestManager() {
    }

    private static class InstanceHolder {
        static final RequestManager sInstance = new RequestManager();
    }

    public static RequestManager getInstance() {
        return InstanceHolder.sInstance;
    }


    /**
     * 删除技能
     *
     * @param callback
     */
    public void delSkill(String skillId, ResultCallback<XMResult<String>> callback) {
        Map<String, String> map = new HashMap<>();
        map.put("skillId", skillId);
        postRequest(VrPracticeAPI.DEL_SKILL, map, callback);
    }

    /**
     * 检查语句是否重复
     *
     * @param callback
     */
    public void checkSkillWord(String word, ResultCallback<XMResult<Boolean>> callback) {
        Map<String, String> map = new HashMap<>();
        map.put("word", word);
        postRequest(VrPracticeAPI.CHECK_SKILL_WORD, map, callback);
    }

    /**
     * 编辑已创建的技能
     *
     * @param callback
     */
    public void editSkills(String skillId, String oldWord,String newWord, String itemList, ResultCallback<XMResult<String>> callback) {
        Map<String, String> map = new HashMap<>();
        map.put("skillId", skillId);
        map.put("oldWord", oldWord);
        map.put("newWord", newWord);
        map.put("itemList", itemList);
        postRequest(VrPracticeAPI.EDIT_SKILL, map, callback);
    }

    /**
     * 保存新创建的技能
     *
     * @param callback
     */
    public void saveSkills(String word, String itemList, ResultCallback<XMResult<String>> callback) {
        Map<String, String> map = new HashMap<>();
        map.put("word", word);
        map.put("itemList", itemList);
        postRequest(VrPracticeAPI.SAVE_NEW_SKILL, map, callback);
    }


    /**
     * 获取所有技能列表
     *
     * @param callback
     */
    public void fetchSkillList(ResultCallback<XMResult<List<SkillBean>>> callback) {
        postRequest(VrPracticeAPI.GET_SKILL_LIST, null, callback);
    }

    /**
     * 获取所有技能点
     *
     * @param callback
     */
    public void fetchSkillItemList(ResultCallback<XMResult<List<SkillItemBean>>> callback) {
        postRequest(VrPracticeAPI.GET_SKILL_ITEM_LIST, null, callback);
    }


    /**
     * 获取城市列表
     *
     * @param callback
     */
    public void getProvinceAndCity(ResultCallback<XMResult<List<ProvinceBean>>> callback) {
        postRequest(VrPracticeAPI.GET_PROVINCE_AND_CITY, null, callback);
    }


    /**
     * 获取新闻频道列表
     *
     * @param callback
     */
    public void getNewsChannel(ResultCallback<XMResult<NewsBean>> callback) {
        postRequest(VrPracticeAPI.GET_NEWS_CHANNEL, null, callback);
    }

    /**
     * 获取音乐搜索列表
     *
     * @param name
     * @param callback
     */
    public void getMusicList(String name,ResultCallback<XMResult<List<PlayMusicBean>>> callback){
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        postRequest(VrPracticeAPI.GET_MUSIC_LIST,map,callback);
    }

    /**
     * 获取电台搜索列表
     *
     * @param title
     * @param callback
     */
    public void getRadioList(String title,ResultCallback<XMResult<List<RadioBean>>> callback){
        Map<String, String> map = new HashMap<>();
        map.put("title", title);
        map.put("page", "0");
        map.put("size", "1");
        postRequest(VrPracticeAPI.GET_RADIO_LIST,map,callback);
    }

    private static <Bean> void request(String url, Map params, final ResultCallback<XMResult<Bean>> callback) {
        if (callback == null) {
            return;
        }

        XmHttp.getDefault().getString(url, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Type type = ((ParameterizedType) callback.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
                String data = response.body();
                XMResult<Bean> result = GsonHelper.fromJson(data, type);
                if (result == null) {
                    callback.onFailure(response.code(), response.message());
                    return;
                }
                if (!result.isSuccess()) {
                    callback.onFailure(result.getResultCode(), result.getResultMessage());
                    return;
                }
                callback.onSuccess(result);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                callback.onFailure(response.code(), response.getException().getMessage());
            }
        });
    }

    private static <Bean> void postRequest(String url, Map params, final ResultCallback<XMResult<Bean>> callback) {
        if (callback == null) {
            return;
        }

        XmHttp.getDefault().postString(url, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Type type = ((ParameterizedType) callback.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
                String data = response.body();
                XMResult<Bean> result = GsonHelper.fromJson(data, type);
                if (result == null) {
                    callback.onFailure(response.code(), response.message());
                    return;
                }
                if (!result.isSuccess()) {
                    callback.onFailure(result.getResultCode(), result.getResultMessage());
                    return;
                }
                callback.onSuccess(result);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                callback.onFailure(response.code(), response.getException().getMessage());
            }
        });
    }
}
