package com.xiaoma.assistant.processor;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.LruCache;

import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.manager.IAssistantManager;
import com.xiaoma.assistant.model.parser.LxParseResult;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/12
 * Desc：本地语音拦截处理基类
 */
public abstract class BaseLocalVoiceProcessor {
    protected IAssistantManager mAssistantManager;
    protected Context mContext;
    private static LruCache<String, Pattern> sPatternLruCache = new LruCache<>(128);

    public BaseLocalVoiceProcessor(Context context,IAssistantManager assistantManager) {
        this.mAssistantManager = assistantManager;
        this.mContext = context;
    }


    public IAssistantManager getmAssistantManager() {
         return mAssistantManager;
    }


    public abstract boolean process(String voiceContent, String json, LxParseResult parseResult);


    protected void speakContent(String text) {
        mAssistantManager.speakContent(text);
    }

    protected void speakContent(String text, WrapperSynthesizerListener listener) {
        mAssistantManager.speakContent(text, listener);
    }

    protected void add2LeftConversation(String text) {
        mAssistantManager.addFeedBackConversation(text);
    }


    protected String getString(int resId) {
        return mContext.getString(resId);
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
        final String regex = getString(strRes);
        if (regex == null || regex.isEmpty())
            return false;
        final LruCache<String, Pattern> patternCache = sPatternLruCache;
        Pattern p = patternCache.get(regex);
        if (p == null) {
            p = Pattern.compile(regex);
            patternCache.put(regex, p);
        }
        return p.matcher(voiceContent).matches();
    }


    protected void uploadOpenAppEvent(String appName) {
        //EventAgent.getInstance().onEvent(Constants.XMEventKey.Assistant.SEARCH_OPEN_APP, appName);
    }

}
