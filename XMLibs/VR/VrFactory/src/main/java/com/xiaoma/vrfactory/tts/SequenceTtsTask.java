//package com.xiaoma.vrfactory.tts;
//
//import com.xiaoma.utils.log.KLog;
//import com.xiaoma.vr.tts.OnSequenceTtsListener;
//
///**
// * Created by ZYao.
// * Date ：2019/6/5 0005
// */
//public class SequenceTtsTask implements Runnable {
//
//    private TtsStorage storage;
//    private boolean isRun;
//
//    public SequenceTtsTask(TtsStorage storage) {
//        this.storage = storage;
//    }
//
//    public void addSpeakContentList(String content, OnSequenceTtsListener listener) {
//        if (storage != null) {
//            storage.addSpeakContentList(content, listener);
//        }
//    }
//
//    public boolean isRun() {
//        return isRun;
//    }
//
//    @Override
//    public void run() {
//        isRun = true;
//        while (isRun) {
//            KLog.d("MrMine", "run: " + "startSpeak");
//            storage.startSpeak(new TtsStorage.OnTtsEndListener() {
//                @Override
//                public void onTtsEnd() {
//                    stop();
//                }
//            });
//        }
//    }
//
//    public void stop() {
//        isRun = false;
//    }
//}
