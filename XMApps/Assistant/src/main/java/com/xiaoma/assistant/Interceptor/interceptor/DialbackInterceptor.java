package com.xiaoma.assistant.Interceptor.interceptor;

import com.xiaoma.assistant.Interceptor.matcher.ConfirmOrCancelTextMatcher;
import com.xiaoma.assistant.manager.api.BluetoothPhoneApiManager;
import com.xiaoma.assistant.model.parser.LxParseResult;

/**
 * Created by qiuboxiang on 2019/4/12 17:34
 * Desc:
 */
public class DialbackInterceptor extends BaseInterceptor<ConfirmOrCancelTextMatcher> {

    @Override
    protected ConfirmOrCancelTextMatcher getTextMatcher() {
        return new ConfirmOrCancelTextMatcher();
    }

    @Override
    protected void action(LxParseResult parserResult, String text) {
        if (textMatcher.isConfirm()) {
            BluetoothPhoneApiManager.getInstance().dialBack();
        } else {
            closeAssistant();
        }
    }

}
