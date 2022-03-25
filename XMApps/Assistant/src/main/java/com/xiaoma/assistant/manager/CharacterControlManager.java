package com.xiaoma.assistant.manager;//package com.xiaoma.assistant.manager;
//
///**
// * User:Created by Terence
// * IDE: Android Studio
// * Date:2018/10/9
// * Desc:全息角色设置
// */
//public class CharacterControlManager {
//
//    private static volatile CharacterControlManager sInstance;
//    private CharacterSelectorListener mListener;
//
//    private CharacterControlManager(){}
//
//    public static CharacterControlManager getInstance(){
//        if(sInstance == null){
//            synchronized (CharacterControlManager.class){
//                if(sInstance == null){
//                    sInstance = new CharacterControlManager();
//                }
//            }
//        }
//        return sInstance;
//    }
//
//    public void selector(int index){
//        if(mListener != null){
//            mListener.voiceSelector(index);
//        }
//    }
//
//    public void setCharacterSelectorListener(CharacterSelectorListener listener){
//        mListener = listener;
//    }
//
//    public boolean isSelectorEnable(){
//        return mListener != null;
//    }
//
//    public interface CharacterSelectorListener{
//        void voiceSelector(int index);
//    }
//
//    private void releaseRec(){
//        mListener = null;
//    }
//
//    public static void release(){
//        if(sInstance != null){
//            sInstance.releaseRec();
//        }
//        sInstance = null;
//    }
//
//}
