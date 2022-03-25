package com.xiaoma.xting.koala.contract.impl;

import android.util.Log;

import com.xiaoma.xting.common.playerSource.contract.PlayerStatus;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.utils.PrintInfo;
import com.xiaoma.xting.koala.KoalaPlayer;
import com.xiaoma.xting.koala.bean.XMPlayItem;
import com.xiaoma.xting.koala.contract.IKoalaPlayerCallback;
import com.xiaoma.xting.koala.utils.KoalaPlayerUtils;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/10
 */
public class KoalaPlayerCallbackImpl implements IKoalaPlayerCallback<Boolean> {

    private static final String TAG = "Koala_Callback";

    private long mProgress, mDuration=-1;

    private KoalaPlayerCallbackImpl() {
        Log.d(TAG, "{KoalaPlayerCallbackImpl}-[cinit] : ");
    }

    public static KoalaPlayerCallbackImpl newSingleton() {
        return Holder.sINSTANCE;
    }

    @Override
    public void onIdle(XMPlayItem var) {
//        PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.PAUSE);
    }

    @Override
    public void onPlayerPreparing(XMPlayItem var) {
        PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.LOADING);
        PlayerInfoImpl.newSingleton().onPlayerInfoChanged(KoalaPlayerUtils.toPlayerInfo(var));
        logInfo("onPlayerPreparing", var.toString());
    }

    @Override
    public void onPlayerPlaying(XMPlayItem var) {
        PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.PLAYING);
        logInfo("onPlayerPlaying", var.toString());
        PlayerInfoImpl.newSingleton().onPlayerInfoChanged(KoalaPlayerUtils.toPlayerInfo(var));
    }

    @Override
    public void onPlayerPaused(XMPlayItem var) {
        PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.PAUSE);
        logInfo("onPlayerPaused", var.toString());
    }

    @Override
    public void onProgress(String url, int position, int duration, boolean isPreDownloadComplete) {
        mProgress = position;
        mDuration = duration;
        PlayerInfoImpl.newSingleton().onPlayerProgress(position, duration);
        logInfo("onProgress", "url : " + url + "\n position= " + position + ", duration= " + duration + ", isPreDownloadComplete= " + isPreDownloadComplete);
    }

    @Override
    public void onPlayerFailed(XMPlayItem var, int var2, int var3) {
        PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.ERROR_BY_PLAYER);
        logInfo("onPlayerFailed", var.toString() + " \n var2= " + var2 + ", var3= " + var3);
    }

    @Override
    public void onPlayerEnd(XMPlayItem var) {
        PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.PAUSE);
        PlayerInfoImpl.newSingleton().onPlayerProgress(mDuration, mDuration);
        mProgress = 0;
        mDuration = -1;
        logInfo("onPlayerEnd", var.toString());
    }

    @Override
    public void onSeekStart(String url) {
        if (KoalaPlayer.newSingleton().isPaused()) {
            PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.PAUSE);
        } else {
            PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.LOADING);
        }
        logInfo("onSeekStart", url);
    }

    @Override
    public void onSeekComplete(String url) {
        logInfo("onSeekComplete", url);
    }

    @Override
    public void onBufferingStart(XMPlayItem var) {
        logInfo("onBufferingStart", var.toString());
    }

    @Override
    public void onBufferingEnd(XMPlayItem var) {
        logInfo("onBufferingEnd", var.toString());
    }

    @Override
    public void onGeneralResult(Boolean o) {
        logInfo("onGeneralResult", o.toString());
    }

    @Override
    public void onGeneralError(int var) {
        logInfo("onGeneralError", var + "");
    }

    @Override
    public void onGeneralException(Throwable var) {
        logInfo("onGeneralException", var.toString());
    }

    private void logInfo(String methodName, String appendInfo) {
        PrintInfo.print(TAG, methodName, String.format("appendInfo = %1$s", appendInfo));
    }

    interface Holder {
        KoalaPlayerCallbackImpl sINSTANCE = new KoalaPlayerCallbackImpl();
    }
}
