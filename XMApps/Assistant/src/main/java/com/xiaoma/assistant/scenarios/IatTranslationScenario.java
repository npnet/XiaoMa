package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.manager.RequestManager;
import com.xiaoma.assistant.model.TranslateBean;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.vr.model.ConversationItem;
import com.xiaoma.vr.utils.VrConstants;
import com.xiaoma.vrfactory.tts.XmTtsManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/30
 * Desc:翻译场景
 */
public class IatTranslationScenario extends IatScenario {
    private final String CODE_OK = "1";//正确
    private final String CODE_SERVICE_ERROR = "1060";//服务不可用
    private final String CODE_NOT = "1059";//不支持

    public IatTranslationScenario(Context context) {
        super(context);
    }


    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        this.parseResult = parseResult;
        String target = "";
        String source = "";
        String content = "";
        try {
            JSONObject slots = new JSONObject(parseResult.getSlots());
            target = slots.getString("target");//目标语言
            source = slots.getString("source");//源语言
            if ("cn".equals(source)) {
                source = "zh";
            }
            content = slots.getString("content");//翻译内容
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(target) && !TextUtils.isEmpty(source) && !TextUtils.isEmpty(content)) {
            queryTranslate(target, source, content);
        } else {
            speakContent(context.getString(R.string.translation_alert));
        }
    }

    private void queryTranslate(final String target, String source, String content) {
        AssistantManager.getInstance().showLoadingView(true);
        RequestManager.newSingleton().getTranslate(source, target, content, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                AssistantManager.getInstance().showLoadingView(false);
                String result = response.body();
                //1060:服务不可用，1059:暂不支持该语言
                TranslateBean translateBean = GsonHelper.fromJson(result, TranslateBean.class);
                if (translateBean == null) {
                    return;
                }
                if (CODE_OK.equals(translateBean.getResultCode())) {
                    addFeedBackConversation(getString(R.string.translation_result_is));
                    XmTtsManager.getInstance().stopSpeaking();
                    speakContent(translateBean.getData());
                    //创建一个conversation并展示
                    ConversationItem conversationItem = parseResult.createReceiveConversation(
                            VrConstants.ConversationType.TRANSLATION, translateBean.getData());
                    addConversationToList(conversationItem);
                } else if (CODE_NOT.equals(translateBean.getResultCode())) {
                    XmTtsManager.getInstance().stopSpeaking();
                    speakContent(getString(R.string.translate_not));
                    addFeedBackConversation(getString(R.string.translate_not));
                } else {
                    speakUnderstand();
                }
            }

            @Override
            public void onError(Response<String> response) {
                AssistantManager.getInstance().showLoadingView(false);
                super.onError(response);
                speakUnderstand();
            }
        });
    }


    @Override
    public void onChoose(String voiceText) {

    }

    @Override
    public boolean isIntercept() {
        return false;
    }

    @Override
    public void onEnd() {

    }
}
