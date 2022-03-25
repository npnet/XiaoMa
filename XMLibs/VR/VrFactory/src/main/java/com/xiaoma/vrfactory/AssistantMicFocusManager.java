package com.xiaoma.vrfactory;

import com.xiaoma.carlib.manager.XmMicManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: iSun
 * @date: 2019/6/18 0018
 */
public class AssistantMicFocusManager {
    private static AssistantMicFocusManager instance;
    public List<IAssistantFocusListener> focusListeners = new ArrayList<>();
    private XmMicManager.OnMicFocusChangeListener micListener = new XmMicManager.OnMicFocusChangeListener() {
        @Override
        public void onMicFocusChange(int var1) {
            notifyMicChange(var1);
        }
    };

    public static AssistantMicFocusManager getInstance() {
        if (instance == null) {
            synchronized (AssistantMicFocusManager.class) {
                if (instance == null) {
                    instance = new AssistantMicFocusManager();
                }
            }
        }
        return instance;
    }

    public boolean requestMicFocus() {
        return XmMicManager.getInstance().requestMicFocus(micListener, XmMicManager.MIC_LEVEL_VOICE, XmMicManager.FLAG_NONE);
    }

    public void addListener(IAssistantFocusListener listener) {
        if (listener!=null&&!focusListeners.contains(listener)) {
            focusListeners.add(listener);
        }
    }

    private void notifyMicChange(int i) {
        for (IAssistantFocusListener listener : focusListeners) {
            listener.onMicFocusChange(i);
        }
    }


}
