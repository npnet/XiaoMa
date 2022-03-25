package com.xiaoma.recommend.manager;


import android.content.Context;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.network.XmHttp;
import com.xiaoma.push.handler.IPushHandler;
import com.xiaoma.push.impl.PushManager;
import com.xiaoma.push.model.PushMessage;
import com.xiaoma.recommend.common.api.RequestManager;
import com.xiaoma.recommend.listener.RecommendDataListener;
import com.xiaoma.recommend.model.RecommendCategory;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: iSun
 * @date: 2018/12/4 0004
 */
public class RecommendDataManager {
    private static final String TAG = RecommendDataManager.class.getSimpleName();
    private static RecommendDataManager instance;
    private final Context context;
    private List<RecommendDataListener> dataListener = new ArrayList<>();
    private ArrayDeque<RecommendCategory> mRecommendData = new ArrayDeque<>();//推送
    private ArrayList<RecommendCategory> mRecommendList = new ArrayList<>();//推荐列表
    private PushManager mPushManager = null;


    public static RecommendDataManager getInstance(Context context) {
        if (instance == null) {
            synchronized (RecommendDataManager.class) {
                if (instance == null) {
                    instance = new RecommendDataManager(context);
                }
            }
        }
        return instance;
    }

    public void init(PushManager pushManager) {
        if (pushManager != null) {
            this.mPushManager = pushManager;
            registerPushHandler();
        }
    }

    private RecommendDataManager(Context context) {
        this.context = context;
        XmHttp.getDefault().init(context.getApplicationContext());
    }


    private void registerPushHandler() {
        if (mPushManager != null) {
            mPushManager.registerHandler(new RecommendHandler() {
                @Override
                public void handle(PushMessage pm) {
                    pushHandler(pm);
                }
            });
        }
    }


    private void getLocalRecommend() {
        // TODO: 2018/12/11 0011 本地推荐 根据油量，故障推荐
    }

    private void pushHandler(PushMessage pm) {
        RecommendCategory model = pushToModel(pm);
        if (model != null) {
            mRecommendData.offerLast(model);
        }
        notifyPushDataChange();
    }

    public RecommendCategory pushToModel(PushMessage pushMessage) {
        if (pushMessage == null) {
            return null;
        }
        int action = pushMessage.getAction();
        RecommendCategory recommendCategory = null;
        if (action == IPushHandler.PUSH_ACTION_RECOMMEND) {
            String data = pushMessage.getData();
            recommendCategory = GsonHelper.fromJson(data, RecommendCategory.class);
        }
        return recommendCategory;
    }

    //注册推送监听
    public void addRecommendListener(RecommendDataListener recommendDataListener) {
        if (recommendDataListener != null) {
            dataListener.add(recommendDataListener);
        }
    }

    public void removeRecommendListener(RecommendDataListener recommendDataListener) {
        if (recommendDataListener != null) {
            dataListener.remove(recommendDataListener);
        }
    }

    private void notifyPushDataChange() {
        for (RecommendDataListener recommendDataListener : dataListener) {
            recommendDataListener.onPushDataChange();
        }
    }

    public RecommendCategory getFirstPushData() {
        RecommendCategory pushData = null;
        pushData = mRecommendData.pollFirst();
        return pushData;
    }


    public void getRecommendList(Long uid, String lat, String lon, final ResultCallback<XMResult<List<RecommendCategory>>> callback,String modeType) {
        RequestManager.getInstance().getRecommendList(new ResultCallback<XMResult<List<RecommendCategory>>>() {
            @Override
            public void onSuccess(XMResult<List<RecommendCategory>> result) {
                if (result != null && !ListUtils.isEmpty(result.getData())) {
                    setRecommendData(result.getData());
                }
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                if (callback != null) {
                    callback.onFailure(code, msg);
                }
            }
        }, uid, lat, lon,modeType);
    }

    private void setRecommendData(List<RecommendCategory> data) {
        mRecommendList.clear();
        mRecommendList.addAll(data);
    }


}
