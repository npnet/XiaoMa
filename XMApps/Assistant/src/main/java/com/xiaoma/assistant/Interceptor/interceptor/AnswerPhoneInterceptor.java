package com.xiaoma.assistant.Interceptor.interceptor;

import android.util.Log;

import com.xiaoma.assistant.Interceptor.matcher.ConfirmOrCancelTextMatcher;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.manager.BluetoothPhoneManager;
import com.xiaoma.assistant.manager.api.BluetoothPhoneApiManager;
import com.xiaoma.assistant.model.parser.LxParseResult;

/**
 * Created by qiuboxiang on 2019/4/15 17:25
 * Desc:
 */
public class AnswerPhoneInterceptor extends BaseInterceptor<ConfirmOrCancelTextMatcher> {

    @Override
    protected ConfirmOrCancelTextMatcher getTextMatcher() {
        Log.d("QBX", context.getString(R.string.incoming_phone));
        ConfirmOrCancelTextMatcher confirmMatcher = new ConfirmOrCancelTextMatcher();
        confirmMatcher.addKeyword("不接", false)
                .addKeyword("挂断", false)
                .addKeyword("挂掉", false)
                .addKeyword("接听", true)
                .addKeyword("接通", true);
        return confirmMatcher;
    }

    @Override
    protected void action(LxParseResult parserResult, String text) {
        BluetoothPhoneManager.getInstance().clearIncomingPhoneState();
        if (textMatcher.isConfirm()) {
            BluetoothPhoneApiManager.getInstance().acceptCall();
        } else {
            BluetoothPhoneApiManager.getInstance().rejectCall();
        }
    }
}
