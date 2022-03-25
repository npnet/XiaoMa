package com.xiaoma.assistant.Interceptor.manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.assistant.Interceptor.interceptor.BaseInterceptor;
import com.xiaoma.assistant.Interceptor.interceptor.CloseAssistantInterceptor;
import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.vrfactory.iat.XmIatManager;
import com.xiaoma.vrfactory.tts.XmTtsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiuboxiang on 2019/4/12 11:42
 * Desc:
 */
public class InterceptorManager {

    private static InterceptorManager mInstance;
    private BaseInterceptor currentInterceptor;
    private BaseInterceptor savedInterceptor;
    private Context context;
    private boolean addFeedback = true;
    private List<BaseInterceptor> defaultInterceptorList = new ArrayList<>();

    public static InterceptorManager getInstance() {
        if (mInstance == null) {
            synchronized (InterceptorManager.class) {
                if (mInstance == null) {
                    mInstance = new InterceptorManager();
                }
            }
        }
        return mInstance;
    }

    public boolean intercept(LxParseResult parserResult) {
        for (BaseInterceptor interceptor : defaultInterceptorList) {
            if (interceptor.intercept(parserResult)){
                return true;
            }
        }
        if (currentInterceptor != null) {
            boolean intercept = currentInterceptor.intercept(parserResult);
            clearInterceptorAndStopListening();
            return intercept;
        }
        return false;
    }

    public void init(Context context) {
        this.context = context;
        initDefaultInterceptorList();
    }

    private void initDefaultInterceptorList() {
        addDefaultInterceptor(new CloseAssistantInterceptor());
    }

    public Context getContext() {
        return context;
    }

    public void addDefaultInterceptor(BaseInterceptor interceptor) {
        defaultInterceptorList.add(interceptor);
    }

    public void setCurrentInterceptor(BaseInterceptor currentInterceptor) {
        setCurrentInterceptor(currentInterceptor, null);
    }

    public void setCurrentInterceptor(BaseInterceptor currentInterceptor, String text) {
        Log.d("QBX", "setCurrentInterceptor: " + currentInterceptor.getClass().getSimpleName());
        this.currentInterceptor = currentInterceptor;
        startSpeakingAndListening(text);
    }

    public void setCurrentInterceptorWithoutAddFeedback(BaseInterceptor currentInterceptor, String text) {
        addFeedback = false;
        setCurrentInterceptor(currentInterceptor, text);
    }

    public void clearInterceptor() {
//        Log.d("QBX", "clearInterceptor");
        currentInterceptor = null;
    }

    public void clearInterceptorAndStopListening() {
        clearInterceptor();
        stopSpeakingAndListening();
    }

    public void clearSavedInterceptor() {
        Log.d("QBX", "clearSavedInterceptor");
        savedInterceptor = null;
    }

    public void saveInterceptor() {
        Log.d("QBX", "saveInterceptor");
        savedInterceptor = currentInterceptor;
    }

    public void restoreInterceptor() {
        Log.d("QBX", "restoreInterceptor");
        currentInterceptor = savedInterceptor;
        stopSpeakingAndListening();
    }

    public BaseInterceptor getCurrentInterceptor() {
        return currentInterceptor;
    }

    private void startSpeakingAndListening(String text) {
        if (addFeedback) {
            AssistantManager.getInstance().addFeedBackConversation(text);
        } else {
            addFeedback = true;
        }
        if (TextUtils.isEmpty(text)) {
            startListening();
        } else {
            XmTtsManager.getInstance().startSpeakingByAssistant(text, new WrapperSynthesizerListener() {
                @Override
                public void onCompleted() {
                    startListening();
                }
            });
        }
    }

    private void startListening() {
        if (!AssistantManager.getInstance().isShowing()) {
            XmTtsManager.getInstance().removeListeners();
            XmTtsManager.getInstance().stopSpeaking();
            XmIatManager.getInstance().startListeningNormal();
        } else {
            AssistantManager.getInstance().startListening(false);
        }
    }

    private void stopSpeakingAndListening() {
        XmTtsManager.getInstance().removeListeners();
        XmTtsManager.getInstance().stopSpeaking();
        XmIatManager.getInstance().stopListening();
    }

}
