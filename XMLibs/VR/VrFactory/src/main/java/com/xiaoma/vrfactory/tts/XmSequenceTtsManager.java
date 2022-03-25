//package com.xiaoma.vrfactory.tts;
//
//import com.xiaoma.cariflytek.iat.VrAidlServiceManager;
//import com.xiaoma.thread.ThreadDispatcher;
//import com.xiaoma.vr.tts.OnSequenceTtsListener;
//
///**
// * Created by ZYao.
// * Date ：2019/6/5 0005
// */
//public class XmSequenceTtsManager {
//
//    private SequenceTtsTask ttsTask;
//
//    public static XmSequenceTtsManager getInstance() {
//        return InstanceHolder.instance;
//    }
//
//    private static class InstanceHolder {
//        static final XmSequenceTtsManager instance = new XmSequenceTtsManager();
//    }
//
//    public void startSpeak(String content) {
//        startSpeak(content, null);
//    }
//
//    public synchronized void startSpeak(final String content, final OnSequenceTtsListener listener) {
//        if (XmTtsManager.getInstance().isTtsSpeaking() && VrAidlServiceManager.getInstance().isVrShowing()) {
//            return;
//        }
//        if (ttsTask == null) {
//            ttsTask = new SequenceTtsTask(new TtsStorage());
//        }
//        ttsTask.addSpeakContentList(content, listener);
//        if (!ttsTask.isRun()) {
//            ThreadDispatcher.getDispatcher().post(ttsTask);
//        }
//    }
//
//    public boolean isSequenceSpeaking() {
//        if (ttsTask != null) {
//            return ttsTask.isRun();
//        }
//        return false;
//    }
//
//}
