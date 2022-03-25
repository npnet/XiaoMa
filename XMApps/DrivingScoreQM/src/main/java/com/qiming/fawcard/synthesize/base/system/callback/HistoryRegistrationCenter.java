package com.qiming.fawcard.synthesize.base.system.callback;

import java.util.ArrayList;

public class HistoryRegistrationCenter {
    private static final ArrayList<IDataUpdateListener> mDataUpdateListenerList = new ArrayList<>();
    /**
     * 注册监听
     * @param listener 监听对象
     */
    public static void register(IDataUpdateListener listener){
        mDataUpdateListenerList.add(listener);
    }

    /**
     * 取消监听
     * @param listener 监听对象
     */
    public static void unRegister(IDataUpdateListener listener){
        mDataUpdateListenerList.remove(listener);
    }

    /**
     * 清除列表
     */
    public static void clear(){
        mDataUpdateListenerList.clear();
    }

    /**
     * 数据变化通知
     */
    public static void notifyUpdate(){
        for(IDataUpdateListener listener : mDataUpdateListenerList){
            listener.update();
        }
    }

}
