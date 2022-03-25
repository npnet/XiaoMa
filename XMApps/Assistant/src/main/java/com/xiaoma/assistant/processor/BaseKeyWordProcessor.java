package com.xiaoma.assistant.processor;

import android.content.Context;
import android.support.annotation.StringRes;

import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.manager.IAssistantManager;
import com.xiaoma.vr.tts.OnTtsListener;
import com.xiaoma.vrfactory.tts.XmTtsManager;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/12
 * Desc：
 */
public abstract class BaseKeyWordProcessor {
    protected IAssistantManager mAssistantManager;
    protected Context context;

    public BaseKeyWordProcessor(Context context, IAssistantManager mAssistantManager) {
        this.mAssistantManager = mAssistantManager;
        this.context = context;
    }

    public IAssistantManager getmAssistantManager() {
        return mAssistantManager;
    }

    public abstract boolean process(String keyword);

    protected void closeVoicePopup() {
        //todo 对外提供关闭
        //mAssistantManager.closeVoicePopup();
    }

    protected void addToLeftTextConversation(String text) {
        //todo 加入对话列表中
        //mAssistantManager.addFeedBackConversation(text);
    }

    protected void speakContent(String text, final boolean needStartListen) {
        mAssistantManager.speakContent(text, new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                super.onCompleted();
                if (needStartListen) {
                    mAssistantManager.startListening(false);
                }
            }
        });
    }

    protected void speakContent(String text) {
        //todo 请求焦点 低精度？？？ 请求和释放成对出现
        //Utils.requestAudioManagerLowerVoice(context, null);
        XmTtsManager.getInstance().stopSpeaking();
        XmTtsManager.getInstance().startSpeakingByAssistant(text, new OnTtsListener() {

            @Override
            public void onCompleted() {
                //Utils.releaseAudioFocus(context, null);
            }

            @Override
            public void onBegin() {

            }

            @Override
            public void onError(int code) {

            }
        });
    }

    protected void speakContent(String text, WrapperSynthesizerListener listener) {
        mAssistantManager.speakContent(text, listener);
    }

    protected void uploadOpenAppEvent(String appName) {
        //EventAgent.getInstance().onEvent(Constants.XMEventKey.Assistant.SEARCH_OPEN_APP, appName);
    }


    protected String getString(int resId) {
        return context.getString(resId);
    }

    protected boolean isContains(String voiceContent, int resId) {
        String texts = getString(resId);
        String[] splits = texts.split("\\|");
        if (splits.length == 0) {
            return false;
        }
        for (String split : splits) {
            if (split.isEmpty()) {
                continue;
            }
            if (voiceContent.contains(split)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isEquals(String voiceContent, int resId) {
        String texts = getString(resId);
        StringTokenizer tokenizer = new StringTokenizer(texts, "|");
        while (tokenizer.hasMoreElements()) {
            String splits = tokenizer.nextToken();
            if (splits.equals(voiceContent)) {
                return true;
            }
        }
        return false;
    }

    protected boolean matches(@StringRes int strRes, String voiceContent) {
        try {
            return Pattern.matches(getString(strRes), voiceContent);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
