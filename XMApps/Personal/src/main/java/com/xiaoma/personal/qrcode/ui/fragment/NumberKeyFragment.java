package com.xiaoma.personal.qrcode.ui.fragment;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.xiaoma.model.XmResource;
import com.xiaoma.personal.R;
import com.xiaoma.personal.qrcode.callback.WrapOnHandlepCallback;
import com.xiaoma.personal.qrcode.model.KeyQRCode;
import com.xiaoma.ui.progress.loading.XMProgress;
import com.xiaoma.utils.log.KLog;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/21 0021 16:17
 *   desc:   数字钥匙
 * </pre>
 */
public class NumberKeyFragment extends BaseQRCodeFragment {


    private static final String TAG = NumberKeyFragment.class.getSimpleName();

    public static NumberKeyFragment newInstance() {
        return new NumberKeyFragment();
    }


    @Override
    protected void fetchData() {
        XMProgress.showProgressDialog(this, mContext.getString(R.string.base_loading));
        getQRCodeVM().getNumberKeyQRCode().observe(this, new Observer<XmResource<KeyQRCode>>() {
            @Override
            public void onChanged(@Nullable XmResource<KeyQRCode> keyQRCodeXmResource) {
                XMProgress.dismissProgressDialog(NumberKeyFragment.this);
                if (keyQRCodeXmResource == null) {
                    KLog.w(TAG, "NumberKey's keyQRCodeXmResource is null");
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
