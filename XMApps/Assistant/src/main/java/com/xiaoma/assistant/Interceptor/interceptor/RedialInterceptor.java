package com.xiaoma.assistant.Interceptor.interceptor;

import com.xiaoma.assistant.Interceptor.matcher.ConfirmOrCancelTextMatcher;
import com.xiaoma.assistant.manager.api.BluetoothPhoneApiManager;
import com.xiaoma.assistant.model.parser.LxParseResult;

/**
 * Created by qiuboxiang on 2019/4/12 16:51
 * Desc:
 */
public class RedialInterceptor extends BaseInterceptor<ConfirmOrCancelTextMatcher> {

    @Override
    protected ConfirmOrCancelTextMatcher getTextMatcher() {
        return new ConfirmOrCancelTextMatcher();
    }

    @Override
    protected void action(LxParseResult parserResult, String text) {
        if (textMatcher.isConfirm()) {
            BluetoothPhoneApiManager.getInstance().redial();
        } else {
            closeAssistant();
        }
    }

}
