package com.xiaoma.assistant.Interceptor.interceptor;

import com.xiaoma.assistant.Interceptor.matcher.AllContentTextMatcher;
import com.xiaoma.assistant.Interceptor.matcher.TextMatcher;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.scenarios.ScenarioDispatcher;

/**
 * Created by qiuboxiang on 2019/8/7 18:15
 * Desc:
 */
public class DestinationInterceptor extends BaseInterceptor{

    @Override
    protected TextMatcher getTextMatcher() {
        return new AllContentTextMatcher();
    }

    @Override
    protected void action(LxParseResult parserResult, String text) {
        ScenarioDispatcher.getInstance().getIatNaviScenario(context).navi(text);
    }

}
