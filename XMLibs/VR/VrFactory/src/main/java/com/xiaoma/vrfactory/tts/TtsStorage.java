//package com.xiaoma.vrfactory.tts;
//
//import com.xiaoma.utils.ListUtils;
//import com.xiaoma.utils.log.KLog;
//import com.xiaoma.vr.tts.OnSequenceTtsListener;
//import com.xiaoma.vr.tts.OnTtsListener;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
///**
// * Created by ZYao.
// * Date ：2019/6/6 0006
// */
//public class TtsStorage {
//    private static final String TAG = TtsStorage.class.getSimpleName();
//    private List<TtsContent> mTtsContentList = new ArrayList<>();//存储集合
//    private OnSequenceTtsListener listener;
//    private OnTtsEndListener mEndListener;
//
//    public synchronized void addSpeakContentList(String speakContent, OnSequenceTtsListener listener) {
//        this.mTtsContentList.add(new TtsContent(speakContent));
//        this.listener = listener;
//        KLog.d(TAG, "addSpeakContentList: " + mTtsContentList.size());
//    }
//
//    public synchronized void startSpeak(OnTtsEndListener endListener) {
//        this.mEndListener = endListener;
//        while (mTtsContentList.size() > 0) {
//            try {
//                KLog.d(TAG, "startSpeak: " + "");
//                final String content = mTtsContentList.get(0).content;
//                XmTtsManager.getInstance().startSpeakingByAssistant(content, new OnTtsListener() {
//                    @Override
//                    public void onCompleted() {
//                        synchronized (TtsStorage.this) {
//                            removeSpeakContent(mTtsContentList.get(0));
//                            if (listener != null) {
//                                listener.onCurrentTtsCompleted(content);
//                            }
//                            TtsStorage.this.notify();
//                            KLog.d(TAG, "onCompleted: " + "");
//                        }
//                    }
//
//                    @Override
//                    public void onBegin() {
//                        KLog.d(TAG, "onBegin: ");
//                        if (listener != null) {
//                            listener.onCurrentTtsBegin(content);
//                        }
//                    }
//
//                    @Override
//                    public void onError(int code) {
//                        synchronized (TtsStorage.this) {
//                            removeSpeakContent(mTtsContentList.get(0));
//                            mEndListener.onTtsEnd();
//                            KLog.d(TAG, "onError: ");
//                            if (listener != null) {
//                                listener.onCurrentTtsError(content);
//                            }
//                            TtsStorage.this.notify();
//                        }
//                    }
//                });
//                this.wait();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void removeSpeakContent(TtsContent content) {
//        Iterator<TtsContent> iterator = mTtsContentList.iterator();
//        while (iterator.hasNext()) {
//            if (iterator.next() == content) {
//                iterator.remove();
//                KLog.d(TAG, "removeSpeakContent: " + "");
//            }
//        }
//        if (ListUtils.isEmpty(mTtsContentList) && mEndListener != null) {
//            mEndListener.onTtsEnd();
//            KLog.d(TAG, "removeSpeakContent: " + "onTtsEnd");
//        }
//        if (ListUtils.isEmpty(mTtsContentList) && listener != null){
//            listener.onTtsEnd();
//        }
//        KLog.d(TAG, "removeSpeakContent: " + "notify");
//    }
//
//    public interface OnTtsEndListener {
//        void onTtsEnd();
//    }
//}
