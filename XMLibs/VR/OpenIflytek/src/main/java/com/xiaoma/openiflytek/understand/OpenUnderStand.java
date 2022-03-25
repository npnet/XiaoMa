package com.xiaoma.openiflytek.understand;

import android.content.Context;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.xiaoma.openiflytek.OpenVoiceManager;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.understand.IUnderstandListener;
import com.xiaoma.vr.understand.UnderstandResult;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/7
 * Desc：semantic by open iFlyTek speech server
 */

public class OpenUnderStand {
    private TextUnderstander mTextUnderstander;

    public void init(Context context) {
        OpenVoiceManager.getInstance().initXf(context);
        mTextUnderstander = TextUnderstander.createTextUnderstander(context, null);
        KLog.d("Init Xf understand " + mTextUnderstander);
    }

    public void understandText(String text, final IUnderstandListener understanderListener) {
        mTextUnderstander.setParameter(SpeechConstant.NLP_VERSION, getNlpVersion());
        mTextUnderstander.understandText(text, new TextUnderstanderListener() {
            @Override
            public void onResult(UnderstanderResult understanderResult) {
                UnderstandResult result = parse(understanderResult);
                understanderListener.onResult(result);
            }

            @Override
            public void onError(SpeechError speechError) {
                understanderListener.onError();
            }
        });
    }

    protected String getNlpVersion() {
        return "2.0";
    }


    /**
     * 数据转换
     */
    private UnderstandResult parse (UnderstanderResult understanderResult) {
        UnderstandResult result = new UnderstandResult();
        result.setResultString(understanderResult.getResultString());
        return result;
    }

}
