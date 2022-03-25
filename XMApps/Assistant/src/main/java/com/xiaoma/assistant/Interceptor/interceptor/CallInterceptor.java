package com.xiaoma.assistant.Interceptor.interceptor;

import com.xiaoma.assistant.Interceptor.matcher.ConfirmOrCancelTextMatcher;
import com.xiaoma.assistant.manager.api.BluetoothPhoneApiManager;
import com.xiaoma.assistant.model.parser.LxParseResult;

/**
 * Created by qiuboxiang on 2019/4/12 17:35
 * Desc:
 */
public class CallInterceptor extends BaseInterceptor<ConfirmOrCancelTextMatcher> {

    private String phoneNumber;

    public CallInterceptor(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    protected ConfirmOrCancelTextMatcher getTextMatcher() {
        ConfirmOrCancelTextMatcher confirmMatcher = new ConfirmOrCancelTextMatcher();
        confirmMatcher.addKeyword("呼叫", true);
        return confirmMatcher;
    }

    @Override
    protected void action(LxParseResult parserResult, String text) {
        if (textMatcher.isConfirm()) {
            BluetoothPhoneApiManager.getInstance().dial(phoneNumber, "");
        } else {
            closeAssistant();
        }
    }

}