package com.xiaoma.music.common.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.mine.model.XMLoginResult;
import com.xiaoma.music.mine.model.XMQrCodeResult;
import com.xiaoma.thread.ThreadDispatcher;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.kuwo.mod.userinfo.login.LoginResult;
import cn.kuwo.mod.userinfo.login.QrCodeResult;

/**
 * Author: loren
 * Date: 2019/7/3 0003
 */
public class ScanLoginVM extends BaseViewModel {

    public ScanLoginVM(@NonNull Application application) {
        super(application);
    }

    private MutableLiveData<XMQrCodeResult> qrCodeResult;
    private MutableLiveData<XMLoginResult> loginResult;
    private static final long CHECK_PERIOD = 2000;
    private ScheduledExecutorService executorService;

    public MutableLiveData<XMQrCodeResult> getQrCodeResult() {
        if (qrCodeResult == null) {
            qrCodeResult = new MutableLiveData<>();
        }
        return qrCodeResult;
    }

    public MutableLiveData<XMLoginResult> getLoginResult() {
        if (loginResult == null) {
            loginResult = new MutableLiveData<>();
        }
        return loginResult;
    }

    public void requestQrCode() {
        ThreadDispatcher.getDispatcher().postNormalPriority(() -> {
            QrCodeResult codeResult = OnlineMusicFactory.getKWLogin().getQrCodeImage();
            getQrCodeResult().postValue(new XMQrCodeResult(codeResult));
        });
    }

    public void startCheckQrCode() {
        if (executorService == null || executorService.isShutdown()) {
            executorService = Executors.newSingleThreadScheduledExecutor();
        }
        executorService.scheduleAtFixedRate(() -> {
            LoginResult result = OnlineMusicFactory.getKWLogin().checkResult();
            getLoginResult().postValue(new XMLoginResult(result));
        }, 0, CHECK_PERIOD, TimeUnit.MILLISECONDS);
    }

    public void stopCheckQrCode() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
            executorService = null;
        }
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }

}
