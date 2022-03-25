package com.xiaoma.assistant.Interceptor.interceptor;

import android.text.TextUtils;
import com.xiaoma.assistant.Interceptor.matcher.TextMatcher;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.carlib.XmCarFactory;

/**
 * Created by qiuboxiang on 2019/8/23 15:13
 * Desc:
 */
public class CloseAssistantInterceptor extends BaseInterceptor{

    @Override
    protected TextMatcher getTextMatcher() {
        return new TextMatcher() {
            @Override
            public boolean match(String text) {
                if (!TextUtils.isEmpty(text) && (
                        text.equals("关闭")
                        || text.equals("取消")
                        || text.equals("退出")
                        || text.equals("退下"))){
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    protected void action(LxParseResult parserResult, String text) {
        XmCarFactory.getCarVendorExtensionManager().setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_CLOSE_ASSISTANT);
        closeAssistant();
    }

}
