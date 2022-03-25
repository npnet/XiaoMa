package com.xiaoma.assistant.Interceptor.interceptor;

import android.content.Context;

import com.xiaoma.assistant.Interceptor.manager.InterceptorManager;
import com.xiaoma.assistant.Interceptor.matcher.TextMatcher;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.model.parser.LxParseResult;

/**
 * Created by qiuboxiang on 2019/4/12 16:35
 * Desc:
 */
public abstract class BaseInterceptor<T extends TextMatcher> {

    Context context;
    T textMatcher;

    BaseInterceptor() {
        context = InterceptorManager.getInstance().getContext();
        textMatcher = getTextMatcher();
    }

    public boolean intercept(LxParseResult parserResult) {
        boolean match = textMatcher.match(parserResult.getText());
        if (match) {
            action(parserResult, parserResult.getText());
        }
        return match;
    }

    protected abstract T getTextMatcher();

    protected abstract void action(LxParseResult parserResult, String text);

    protected void closeAssistant() {
        AssistantManager.getInstance().closeAssistant();
    }
}
