package com.xiaoma.personal.qrcode.ui.fragment;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.xiaoma.model.XmResource;
import com.xiaoma.personal.qrcode.callback.WrapOnHandlepCallback;
import com.xiaoma.personal.qrcode.model.KeyQRCode;
import com.xiaoma.ui.progress.loading.XMProgress;
import com.xiaoma.utils.log.KLog;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/21 0021 16:18
 *   desc:   远程控制
 * </pre>
 */
public class RemoteControllerFragment extends BaseQRCodeFragment {

    private static final String TAG = RemoteControllerFragment.class.getSimpleName();

    public static RemoteControllerFragment newInstance() {
        return new RemoteControllerFragment();
    }

    @Override
    protected void fetchData() {
        getQRCodeVM().getRemoteControllerQRCode().observe(this, new Observer<XmResource<KeyQRCode>>() {
            @Override
            public void onChanged(@Nullable XmResource<KeyQRCode> keyQRCodeXmResource) {
                XMProgress.dismissProgressDialog(RemoteControllerFragment.this);
                if (keyQRCodeXmResource == null) {
                    KLog.w(TAG, "RemoteController's keyQRCodeXmResource is null");
                    return;
                }

                keyQRCodeXmResource.handle(new WrapOnHandlepCallback<KeyQRCode>() {
                    @Override
                    public void onSuccess(KeyQRCode keyQRCode) {
                        if (keyQRCode == null) {
                            KLog.d(TAG, "KeyQRCode instance is null.");
                            showEmptyView();
                            return;
                        }
                        refreshData(keyQRCode);
                    }

                    @Override
                    public void onFailure(String message) {
                        showNoNetView();
                    }
                });
            }
        });
    }

    private void refreshData(KeyQRCode keyQRCode) {
        setCodeImageView(keyQRCode.getQrcode());
        setCodeDescText(keyQRCode.getDesc());
    }
}
