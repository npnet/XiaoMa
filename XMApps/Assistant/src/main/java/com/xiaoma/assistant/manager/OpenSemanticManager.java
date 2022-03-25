package com.xiaoma.assistant.manager;

import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.semantic.OpenSemantic;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

/**
 * Created by qiuboxiang on 2019/7/29 19:48
 * Desc:
 */
public class OpenSemanticManager {

    private static final long DELAY_TIME = 1000 * 30;//重试间隔
    private static final String TAG = OpenSemanticManager.class.getSimpleName();
    private static OpenSemanticManager mInstance;
    private OpenSemantic openSemantic;
    private boolean isInitData = false;
    private int retryCount = 0;
    private static final int MAX_RETRY_COUNT = 5;//重试次数

    public static OpenSemanticManager getInstance() {
        if (mInstance == null) {
            synchronized (OpenSemanticManager.class) {
                if (mInstance == null) {
                    mInstance = new OpenSemanticManager();
                }
            }
        }
        return mInstance;
    }

    private OpenSemanticManager() {
        openSemantic = AssistantDbManager.getInstance().queryOpenSemantic();
        initSemantics();
    }

    private void initSemantics() {
        Log.d("TAG", "initSemantics: ");
        try {
            if (!isInitData) {
                retryCount++;
                RequestManager.newSingleton().getOpenSemanticsList(new ResultCallback<XMResult<OpenSemantic>>() {
                    @Override
                    public void onSuccess(XMResult<OpenSemantic> result) {
                        retryCount = 0;
                        isInitData = true;
                        OpenSemantic data = result.getData();
                        if (openSemantic == null || ListUtils.isEmpty(openSemantic.getList()) || !TextUtils.equals(data.getMd5String(), openSemantic.getMd5String())) {
                            if (openSemantic != null) {
                                AssistantDbManager.getInstance().deleteOpenSemantic(openSemantic);
                            }
                            openSemantic = data;
                            openSemantic.setSaveTime(System.currentTimeMillis());
                            AssistantDbManager.getInstance().saveOpenSemantic(openSemantic);
                            Log.d(TAG, "new md5");
                        } else {
                            Log.d(TAG, "old md5");
                        }
                        KLog.json(TAG, GsonHelper.toJson(openSemantic));

                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        if (retryCount < MAX_RETRY_COUNT && !isInitData) {
                            ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    initSemantics();
                                }
                            }, DELAY_TIME);
                        }

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {

    }


    private boolean isTimeOut() {
        long validTime = 0;
        boolean isTimeOut = false;
        if (openSemantic != null) {
            validTime = openSemantic.getSaveTime() + 1000 * 3600 * 24; //超时时间为一天
            isTimeOut = System.currentTimeMillis() > validTime;
        }
        Log.d("QBX", "isTimeOut: " + isTimeOut);
        return isTimeOut;
    }


    public void getOpenSemantic(String voiceText, IOpenSemanticCallBack callBack) {
        LxParseResult lxParseResult = null;
        if (openSemantic != null && !ListUtils.isEmpty(openSemantic.getList())) {
            for (OpenSemantic.ListBean bean : openSemantic.getList()) {
//                Log.d("QBX", "matchText: " + bean.getQuestion());
                if (TextUtils.equals(voiceText, bean.getQuestion())) {
                    String result = bean.getResult();
                    lxParseResult = LxParseResult.fromJson(result);
                    lxParseResult.setOpenSemantic(true);
                    break;
                }
            }
        }
        if (callBack != null) {
            if (lxParseResult != null) {
                callBack.onSuccess(voiceText, lxParseResult);
            } else {
                callBack.onFailure(-1);
            }
        }
        if (isTimeOut() && isInitData) {
            //超出一天 & 之前已经初始化成功
            isInitData = false;
            initSemantics();
        }
    }

    public interface IOpenSemanticCallBack {
        public void onSuccess(String voiceText, LxParseResult lxParseResult);

        public void onFailure(int erroCode);
    }

}
