package com.xiaoma.assistant.Interceptor.interceptor;

import com.xiaoma.assistant.Interceptor.matcher.AllContentTextMatcher;
import com.xiaoma.assistant.Interceptor.matcher.TextMatcher;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.processor.AssistantProcessorChain;
import com.xiaoma.assistant.scenarios.IatScenario;
import com.xiaoma.assistant.scenarios.IatWeChatScenario;

/**
 * Created by qiuboxiang on 2019/6/3 15:47
 * Desc:
 */
public class MessageContentInterceptor extends BaseInterceptor {

    @Override
    protected TextMatcher getTextMatcher() {
        return new AllContentTextMatcher();
    }

    @Override
    protected void action(LxParseResult parserResult, String text) {
        IatScenario currentScenario = AssistantProcessorChain.getInstance().getSemanticByNetworkProcessor().getCurrentScenario();
        if (currentScenario instanceof IatWeChatScenario) {
            IatWeChatScenario iatWeChatScenario = (IatWeChatScenario) currentScenario;
            iatWeChatScenario.setMessageContent(text);
        }
    }

}
