package com.xiaoma.dualscreen.eol;

import android.os.Bundle;
import android.util.Log;

import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.remote.Client;
import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.component.AppHolder;
import com.xiaoma.dualscreen.views.ViewCache;
import com.xiaoma.thread.ThreadDispatcher;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/7/19
 */
public class EOLScreenClient extends Client {

    private boolean mIsOnEOlModeF;
    private IDualScreenEOLEventListener mEOLEventListener;

    private EOLScreenClient() {
        super(AppHolder.getInstance().getAppContext(), CenterConstants.IPCPort.DualScreen.EOL);
        mIsOnEOlModeF = false;
    }

    public static EOLScreenClient newSingleton() {
        return Holder.sINSTANCE;
    }

    @Override
    protected void onReceive(int action, Bundle data) {

    }

    @Override
    protected void onRequest(int action, Bundle data, ClientCallback callback) {
        Log.d("DualScreen", "onRequest: " + action);
        mIsOnEOlModeF = true;
        if (action == CenterConstants.EOLContract.Action.ENV_TEST) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(CenterConstants.EOLContract.Key.EXTRA, true);
            dispatchFeedback(bundle, callback);
        } else if (action == CenterConstants.EOLContract.Action.LVDS_SHOW) {
            dispatchActionShow(callback);
        } else if (action == CenterConstants.EOLContract.Action.LVDS_HIDE) {
            dispatchActionHide(callback);
        } else if (action == CenterConstants.EOLContract.Action.LVDS_STATE) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(CenterConstants.EOLContract.Key.EXTRA, ViewCache.getInstance().getEOLView().isShown());
            dispatchFeedback(bundle, callback);
        }
    }

    @Override
    protected void onConnect(int action, Bundle data, ClientCallback callback) {

    }

    public boolean isOnEOLMode() {
        return mIsOnEOlModeF;
    }

    public void setOnEOLEventListener(IDualScreenEOLEventListener listener) {
        if (listener == null) return;
        mEOLEventListener = listener;
    }

    public void dispatchFeedback(Bundle bundle, ClientCallback callback) {
        if (callback != null) {
            callback.setData(bundle);
            callback.callback();
        }
    }

    private void dispatchActionShow(final ClientCallback callback) {
        if (mEOLEventListener != null) {
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    mEOLEventListener.showImage(callback);

                }
            });
        }
    }

    private void dispatchActionHide(final ClientCallback callback) {
        if (mEOLEventListener != null) {
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    mEOLEventListener.hideImage(callback);
                }
            });

        }
    }

    interface Holder {
        EOLScreenClient sINSTANCE = new EOLScreenClient();
    }
}
