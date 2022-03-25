package com.xiaoma.assistant.manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.understand.IUnderstandListener;
import com.xiaoma.vr.understand.UnderstandResult;

import java.util.regex.Pattern;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/19
 * Desc：语义理解
 */
public class UnderStandManager {
    private static UnderStandManager instance;

    public static UnderStandManager getInstance() {
        if (instance == null) {
            instance = new UnderStandManager();
        }
        return instance;
    }


    /**
     * 语义理解分发（套件、后台）
     *
     * @param context            上文文
     * @param voiceText          语音文本
     * @param voiceJson          套件语音结果
     * @param parseResult        套件解析结果
     * @param understandListener 回调
     */
    public void underStandText(Context context, String voiceText, String voiceJson, LxParseResult parseResult, IUnderstandListener understandListener) {
        if (parseResult == null) {
            //结果回调为null，说明使用的是套件的解析结果，不然会涉及到二次解析分发场景的问题
            notifyUnderstandSuccess(understandListener, null);
            return;
        }
        if (parseResult.isOpenSemantic()) {
            notifyUnderstandSuccess(understandListener, new UnderstandResult(voiceJson));
        } else if (isLxParserSuccess(context, parseResult)) {
            notifyUnderstandSuccess(understandListener, null);
        } else {
            xmUnderStandText(voiceText, understandListener);
        }
    }


    private void notifyUnderstandSuccess(IUnderstandListener understanderListener, UnderstandResult result) {
        if (understanderListener != null) {
            understanderListener.onResult(result);
        }
    }

    private void notifyUnderstandError(IUnderstandListener understanderListener) {
        if (understanderListener != null) {
            understanderListener.onError();
        }
    }


    /**
     * 讯飞离线结果解析判断
     */
    private boolean isLxParserSuccess(Context context, LxParseResult lxParseResult) {
        if (lxParseResult == null) return false;
        /*try {
         *//***** XXX 附近的火锅店兼容 *****//*
            if (!TextUtils.isEmpty(lxParseResult.getSlots())) {
                JSONObject slots = new JSONObject(lxParseResult.getSlots());
                if (slots.has("category") && !TextUtils.isEmpty(slots.getString("category"))) {
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        return (lxParseResult.getRc() != 4 && lxParseResult.isUserOfflineParserResult() && !isNearByCar(context, lxParseResult.getText())) || (lxParseResult.getRc() == 4 && !TextUtils.isEmpty(lxParseResult.getService()));
    }


    /**
     * 是否是POI周边搜索
     */
    private boolean isPoiLbsQuery(LxParseResult lxParseResult) {
        if (lxParseResult == null) return false;
        return lxParseResult.getRc() == 0 && lxParseResult.isPoiLbsQuery();
    }

    private boolean isNearByCar(Context context, String content) {
        if (!TextUtils.isEmpty(content)) {
            if (Pattern.matches(context.getString(R.string.nearby_people), content))
                return true;
        }
        return false;
    }


    private void xmUnderStandText(String voiceText, final IUnderstandListener understandListener) {
        if (TextUtils.isEmpty(voiceText)) {
            understandListener.onResult(null);
            return;
        }
        KLog.d("zhs xiaoma understand start  " + System.currentTimeMillis());
        AssistantManager.getInstance().showLoadingView(true);
        RequestManager.newSingleton().xmNlu(voiceText, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                AssistantManager.getInstance().showLoadingView(false);
                KLog.d("zhs xiaoma understand  end  " + System.currentTimeMillis());
                String textResult = response.body();
                Log.d("QBX", "xmUnderStandText: " + textResult);
                KLog.json(response.body());
                UnderstandResult result = new UnderstandResult();
                result.setResultString(textResult);
                notifyUnderstandSuccess(understandListener, result);
            }

            @Override
            public void onError(Response<String> response) {
                AssistantManager.getInstance().showLoadingView(false);
                super.onError(response);
                KLog.d("zhs xiaoma understand  error  " + System.currentTimeMillis());
                KLog.e(String.format("iatPro -> use XM understand:[fail: code=%s,msg=%s]", response.code(), response.message()));
                notifyUnderstandError(understandListener);
            }
        });

    }

}
