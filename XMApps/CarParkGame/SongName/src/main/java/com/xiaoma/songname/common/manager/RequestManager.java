package com.xiaoma.songname.common.manager;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.songname.api.SongNameApi;
import com.xiaoma.songname.model.GameResult;
import com.xiaoma.songname.model.SongNameBean;
import com.xiaoma.songname.model.SortListBean;
import com.xiaoma.songname.model.UserSignBean;
import com.xiaoma.utils.GsonHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thomas on 2018/11/7 0007
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
     * 获取题目
     *
     * @param callback
     */
    public void getSubject(String uid, ResultCallback<XMResult<SongNameBean>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        request(SongNameApi.GET_SUBJECT, params, callback);
    }

    /**
     * 回答正确后上报
     *
     * @param subjectId
     * @param callback
     */
    public void reportResult(String uid, int subjectId, ResultCallback<XMResult<Object>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        params.put("subjectId", subjectId);
        request(SongNameApi.REPORT_RESULT, params, callback);
    }

    /**
     * 获取当前答题积分排行榜
     *
     * @param callback
     */
    public void getRankingList(String uid, ResultCallback<XMResult<SortListBean>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        request(SongNameApi.RANKING_LIST, params, callback);
    }

    /**
     * 获取当前用户签名
     *
     * @param callback
     */
    public void getUserSign(String uid, String otherId, ResultCallback<XMResult<UserSignBean>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        params.put("otherId", otherId);
        request(SongNameApi.GET_USER_SIGN, params, callback);
    }

    /**
     * 获取当前积分结算情况
     *
     * @param callback
     */
    public void getTotalPoints(String uid, ResultCallback<XMResult<GameResult>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        request(SongNameApi.GET_TOTAL_POINTS, params, callback);
    }

    /**
     * 申请加好友
     */
    public void requestAddFriend(String fromId, String toId, ResultCallback<XMResult<String>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("fromId", fromId);
        params.put("toId", toId);
        request(SongNameApi.REQUEST_ADD_FRIEND, params, callback);
    }

    private static <Bean> void request(String url, Map params, final ResultCallback<XMResult<Bean>> callback) {
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
