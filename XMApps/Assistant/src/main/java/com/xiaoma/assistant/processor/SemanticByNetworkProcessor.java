package com.xiaoma.assistant.processor;

import android.content.Context;
import android.util.Log;

import com.xiaoma.assistant.Interceptor.manager.InterceptorManager;
import com.xiaoma.assistant.manager.IAssistantManager;
import com.xiaoma.assistant.manager.UnderStandManager;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.scenarios.IatScenario;
import com.xiaoma.assistant.scenarios.ScenarioDispatcher;
import com.xiaoma.assistant.utils.CommonUtils;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.TimeConsumingUtils;
import com.xiaoma.utils.constant.ConsumingConstants;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.understand.IUnderstandListener;
import com.xiaoma.vr.understand.UnderstandResult;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/12
 * Desc：在线语义处理
 */
public class SemanticByNetworkProcessor extends BaseLocalVoiceProcessor {
    private IatScenario currentScenario;
    private String voiceJson;
    private LxParseResult parseResult;

    public SemanticByNetworkProcessor(Context context, IAssistantManager assistantManager) {
        super(context, assistantManager);
        ScenarioDispatcher.getInstance().init(assistantManager);
    }

    @Override
    public boolean process(String voiceText, String voiceJson, LxParseResult parseResult) {
        KLog.d("zhs", "SemanticByNetworkProcessor  process---" + System.currentTimeMillis());
        this.voiceJson = voiceJson;
        this.parseResult = parseResult;
        processVoiceText(voiceText);
        return true;
    }

    private void processVoiceText(String voiceText) {
        long session = mAssistantManager.getDialogSession();
        if (mAssistantManager.inMultipleForChooseMode()) {
            KLog.d("something to do");
        } else {
            handleRecognizeResult(voiceText, session);
        }
    }


    private void handleRecognizeResult(String voiceText, long session) {
        ParserLocationParam location = CommonUtils.getLocation();
        understandText(voiceText, location, session);
    }

    private void understandText(final String voiceText, final ParserLocationParam location, final long session) {
        KLog.d("zhs", "understand---" + System.currentTimeMillis());
        UnderStandManager.getInstance().underStandText(mContext, voiceText, voiceJson, parseResult, new IUnderstandListener() {
            @Override
            public void onResult(UnderstandResult result) {
                KLog.d("zhs", "understand result is----" + System.currentTimeMillis());
                if (null == result || !result.isSuccess()) {
                    if (AssistantProcessorChain.getInstance().getProcessType() == 2 || AssistantProcessorChain.getInstance().isTimeOut()) {
                        KLog.d("zhs", "use iflytek parser result----" + System.currentTimeMillis());
                        //采用套件的语义结果进行场景分发和解析
                        parse(voiceJson, location, session);
                    } else {
                        //delay执行
                        KLog.d("zhs", "waiting for local handle" + System.currentTimeMillis());
                        long time = AssistantProcessorChain.getInstance().getDelayTime();
                        if (time <= 0) {
                            //采用套件的语义结果进行场景分发和解析
                            parse(voiceJson, location, session);
                            return;
                        }
                        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //采用套件的语义结果进行场景分发和解析
                                parse(voiceJson, location, session);
                            }
                        }, time);
                    }
                } else {
                    //重新解析parserResult然后再进行分发
                    KLog.d("zhs", "understand start parser xiaoma nlu----" + System.currentTimeMillis());
                    parseResult = LxParseResult.fromJson(result.getResultString());
                    KLog.d("zhs", "understand end  parser xiaoma nlu----" + System.currentTimeMillis());
                    parse(result.getResultString(), location, session);
                }
                KLog.d("zhs", "scenario dispatcher end----" + System.currentTimeMillis());
            }

            @Override
            public void onError() {
                //直接使用套件的解析结果
                if (!InterceptorManager.getInstance().intercept(parseResult)) {
                    IatScenario scenario = ScenarioDispatcher.getInstance().dispatch(mContext, parseResult);
                    scenario.onParser(voiceJson, parseResult, location, session);
                    Log.d("QBX", "onError: onParser: " + scenario.getClass().getSimpleName());
                }
            }
        });

    }

    void parse(String json, ParserLocationParam location, long session) {
        if (!InterceptorManager.getInstance().intercept(parseResult)) {
            currentScenario = ScenarioDispatcher.getInstance().dispatch(mContext, parseResult);
            currentScenario.onParser(json, parseResult, location, session);
            Log.d("QBX", " onParser: " + currentScenario.getClass().getSimpleName());
        }
        TimeConsumingUtils.getInstance().end(ConsumingConstants.XF, parseResult.getText());
    }

    public IatScenario getCurrentScenario() {
        return currentScenario;
    }

    public void setCurrentScenario(IatScenario currentScenario) {
        this.currentScenario = currentScenario;
    }
}
