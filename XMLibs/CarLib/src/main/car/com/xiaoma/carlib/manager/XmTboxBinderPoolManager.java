package com.xiaoma.carlib.manager;

import android.content.Context;

import com.fsl.android.TboxBinderPool;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: ZiXu Huang
 * @Data: 2019/6/18
 * @Desc:
 */
public class XmTboxBinderPoolManager {
    public static XmTboxBinderPoolManager instance;
    public  Context context;
    public List<OnTboxBinderPoolConnectionListener> listeners = new ArrayList<>();
    private TboxBinderPool tboxBinderPool;

    public static XmTboxBinderPoolManager getInstance() {
        if (instance == null) {
            instance = new XmTboxBinderPoolManager();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;
        tboxBinderPool = TboxBinderPool.getInstance(context);

    }
    public void prepare(){
        tboxBinderPool.setConnListener(new TboxBinderPool.ServiceConnListener() {
            @Override
            public void onResult(int i) {
                if (i == TboxBinderPool.BINDER_SUCCEED) {
                    notifyConnected(tboxBinderPool);
                } else {
                    notifyDisconnected(tboxBinderPool);
                }
            }
        });
        tboxBinderPool.prepare();
    }

    private void notifyDisconnected(TboxBinderPool tboxBinderPool) {
        if (listeners.isEmpty()) return;
        for (OnTboxBinderPoolConnectionListener listener : listeners) {
            listener.onDisconnected(tboxBinderPool);
        }
    }

    private void notifyConnected(TboxBinderPool tboxBinderPool) {
        if (listeners.isEmpty()) return;
        for (OnTboxBinderPoolConnectionListener listener : listeners) {
            listener.onConnected(tboxBinderPool);
        }
    }

    public void registerTboxbinderPoolConnectedListener(OnTboxBinderPoolConnectionListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void unregisterTboxbinderPoolConnectedListener(OnTboxBinderPoolConnectionListener listener){
        if (listener != null && listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    public interface OnTboxBinderPoolConnectionListener {

        void onConnected(TboxBinderPool tboxBinderPool);

        void onDisconnected(TboxBinderPool tboxBinderPool);
    }
}
