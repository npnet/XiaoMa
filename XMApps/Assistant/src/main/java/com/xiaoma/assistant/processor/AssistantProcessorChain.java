package com.xiaoma.assistant.processor;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.manager.IAssistantManager;
import com.xiaoma.assistant.manager.OpenSemanticManager;
import com.xiaoma.assistant.manager.VrPracticeManager;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.scenarios.IatScenario;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.TimeConsumingUtils;
import com.xiaoma.utils.constant.ConsumingConstants;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.dispatch.DispatchManager;
import com.xiaoma.vr.dispatch.model.Command;
import com.xiaoma.vr.dispatch.model.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/12
 * Desc：Voice processor拦截器
 */
public class AssistantProcessorChain {

    //拦截的超时机制，如果超过就直接执行在线的操作了，这个值根据项目来配置，理论上不应该出现超时
    //如果是超时的话说明本地拦截需要优化了，其次如果是线程执行异常的话那就更不用说
    private static final int MAX_TIME_OUT = 300;
    //拦截类型，0代表初始态，1代表的是拦截，2代表的是不拦截
    private static int processType = 0;
    private long processTime = 0;


    private static AssistantProcessorChain instance;
    private List<BaseLocalVoiceProcessor> processorList = new ArrayList<>();
    private List<BaseLocalVoiceProcessor> wakeupProcessorList = new ArrayList<>();
    private List<BaseKeyWordProcessor> wakeupKeyWordProcessorList = new ArrayList<>();
    private SemanticByNetworkProcessor semanticByNetworkProcessor;
    private ILocalBusinessVoiceProcess iLocalBusinessVoiceProcess;
    private WakeUpShowVoiceDialogProcessor mWakeUpShowVoiceDialogProcessor;

    private AssistantProcessorChain() {
        Log.d("AssistantProcessorChain", "AssistantProcessorChain constructor");
    }

    public static AssistantProcessorChain getInstance() {
        if (instance == null) {
            instance = new AssistantProcessorChain();
        }
        return instance;
    }


    /**
     * 本地拦截处理
     */
    public boolean processLocal(String voiceContent, String json, LxParseResult parserResult) {
        processTime = System.currentTimeMillis();
        processType = 0;
        for (BaseLocalVoiceProcessor processor : processorList) {
            if (processor.process(voiceContent, json, parserResult)) {
                KLog.d(StringUtil.format("iatPro -> process by < %s >", processor.getClass().getName()));
                processType = 1;
                return true;
            }
        }

        KLog.d("花费时间：" + (System.currentTimeMillis() - processTime));
        processType = 2;
        return false;
    }


    /**
     * 处理动态注册的关键字
     */
    public boolean processRegisterKeyWord(String voiceContent, String json, LxParseResult parserResult) {
        processTime = System.currentTimeMillis();
        processType = 0;
        Command command = new Command(voiceContent);
        Result result = DispatchManager.getInstance().notifyCommand(command);
        if (result.isHandled()) {
            KLog.d(StringUtil.format("process by register key word ") + voiceContent);
            processType = 1;
            return true;
        }

        KLog.d("花费时间：" + (System.currentTimeMillis() - processTime));
        processType = 2;
        return false;
    }


    public boolean processKeyWord(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            return false;
        }
        for (BaseKeyWordProcessor processor : wakeupKeyWordProcessorList) {
            if (processor.process(keyword)) {
                KLog.d(StringUtil.format("iatPro -> process by < %s >", processor.getClass().getName()));
                return true;
            }
        }
        return false;
    }


    public void init(Context context, IAssistantManager assistantManager) {
        //语音识别处理
        processorList.clear();
        //本地语音处理
        processorList.add(new HandleAudioProcessor(context, assistantManager));

        //云端语义处理
        semanticByNetworkProcessor = new SemanticByNetworkProcessor(context, assistantManager);

        //唤醒命令词处理
        wakeupProcessorList.clear();
        wakeupProcessorList.add(new HandleAudioProcessor(context, assistantManager));

        //免唤醒词处理
        wakeupKeyWordProcessorList.clear();
    }


    public void handleWaitingForChoose(String voiceText) {
        IatScenario currentScenario = semanticByNetworkProcessor.getCurrentScenario();
        if (currentScenario != null) {
            currentScenario.onChoose(voiceText);
        }
    }

    public SemanticByNetworkProcessor getSemanticByNetworkProcessor() {
        return semanticByNetworkProcessor;
    }

    public WakeUpShowVoiceDialogProcessor getWakeUpShowVoiceDialogProcessor() {
        return mWakeUpShowVoiceDialogProcessor;
    }

    public void setiLocalBusinessVoiceProcess(ILocalBusinessVoiceProcess iLocalBusinessVoiceProcess) {
        this.iLocalBusinessVoiceProcess = iLocalBusinessVoiceProcess;
    }

    public void processByScenario(String voiceText, String voiceJson, LxParseResult parseResult) {
        //1、语音训练
        TimeConsumingUtils.getInstance().start(ConsumingConstants.DISPATCHER, voiceText);
        TimeConsumingUtils.getInstance().start(ConsumingConstants.USER, voiceText);
        boolean handleVrPracticeKeyWord = VrPracticeManager.getInstance().processVrPracticeKeyWord(voiceText);
        TimeConsumingUtils.getInstance().end(ConsumingConstants.USER, voiceText);
        if (!handleVrPracticeKeyWord) {
            TimeConsumingUtils.getInstance().start(ConsumingConstants.OPEN, voiceText);
            OpenSemanticManager.getInstance().getOpenSemantic(voiceText, new OpenSemanticManager.IOpenSemanticCallBack() {
                @Override
                public void onSuccess(String voiceText, LxParseResult lxParseResult) {
                    //2、奔腾开放平台语义
                    TimeConsumingUtils.getInstance().end(ConsumingConstants.OPEN, voiceText);
                    Log.e("SemanticDispatcher", String.format(" processByScenario : semantic for open, text:%s", voiceText));
                    KLog.json("SemanticDispatcher", GsonHelper.toJson(lxParseResult));
                    semanticByNetworkProcessor.process(voiceText, GsonHelper.toJson(lxParseResult), lxParseResult);
                    TimeConsumingUtils.getInstance().end(ConsumingConstants.DISPATCHER, voiceText);
                }

                @Override
                public void onFailure(int erroCode) {
                    TimeConsumingUtils.getInstance().end(ConsumingConstants.OPEN, voiceText);
                    TimeConsumingUtils.getInstance().start(ConsumingConstants.ACTIVITY, voiceText);
                    //3、界面注册指令
                    boolean handleRegisterKeyWord = AssistantProcessorChain.getInstance().processRegisterKeyWord(voiceText, voiceJson, parseResult);
                    TimeConsumingUtils.getInstance().end(ConsumingConstants.ACTIVITY, voiceText);
                    if (handleRegisterKeyWord) {
                        Log.e("SemanticDispatcher", String.format(" processByScenario : semantic for activity, text:%s", voiceText));
                        TimeConsumingUtils.getInstance().end(ConsumingConstants.DISPATCHER, voiceText);
                        AssistantManager.getInstance().closeAssistant();
                        return;
                    }
                    //4、讯飞语义
                    Log.e("SemanticDispatcher", String.format(" processByScenario : semantic for iflytek, text:%s", voiceText));
                    TimeConsumingUtils.getInstance().start(ConsumingConstants.XF, voiceText);
                    semanticByNetworkProcessor.process(voiceText, voiceJson, parseResult);
                    TimeConsumingUtils.getInstance().end(ConsumingConstants.DISPATCHER, voiceText);

                }
            });
        } else {
            Log.e("SemanticDispatcher", String.format(" processByScenario : semantic for user text:%s", voiceText));
            TimeConsumingUtils.getInstance().end(ConsumingConstants.DISPATCHER, voiceJson);
        }
    }

    public boolean processWakeUpCmd(String cmdText) {
        for (BaseLocalVoiceProcessor processor : wakeupProcessorList) {
            if (processor.process(cmdText, "", null)) {
                return true;
            }
        }
        return false;
    }

    public interface ILocalBusinessVoiceProcess {
        void addLocalBusinessVoiceProcess(List<BaseLocalVoiceProcessor> processors,
                                          Context context, IAssistantManager assistantManager);
    }


    public int getProcessType() {
        return processType;
    }

    public boolean isTimeOut() {
        if (System.currentTimeMillis() - processTime >= MAX_TIME_OUT) {
            return true;
        }

        return false;
    }

    public long getDelayTime() {
        return MAX_TIME_OUT - (System.currentTimeMillis() - processTime);
    }

}
