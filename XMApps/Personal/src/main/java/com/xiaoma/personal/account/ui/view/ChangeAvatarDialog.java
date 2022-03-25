package com.xiaoma.personal.account.ui.view;

import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.image.ImageLoader;
import com.xiaoma.login.LoginManager;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.personal.R;
import com.xiaoma.personal.account.model.AvatarQRCode;
import com.xiaoma.personal.common.RequestManager;
import com.xiaoma.ui.StateControl.OnRetryClickListener;
import com.xiaoma.ui.StateControl.StateView;
import com.xiaoma.ui.StateControl.Type;
import com.xiaoma.ui.dialog.impl.XMDialogImpl;

/**
 * Created by kaka
 * on 19-2-21 下午8:45
 * <p>
 * desc: #a
 * </p>
 */
public class ChangeAvatarDialog extends XMDialogImpl {

    private ImageView qrcode;
    private StateView stateView;
    private DialogInterface.OnCancelListener cancelListener;
    private TextView textview;

    @Override
    protected String getDialogTag() {
        return "ChangeAvatarDialog";
    }

    @Override
    public int getDialogHeight() {
        return getPxDimension(R.dimen.height_change_avatar_layout);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.dialog_change_avatar;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (cancelListener != null) {
            cancelListener.onCancel(dialog);
        }
    }

    @Override
    public void bindView(View view) {
        qrcode = view.findViewById(R.id.change_qr);
        stateView = view.findViewById(R.id.state_view);
        qrcode = view.findViewById(R.id.change_qr);
        textview = view.findViewById(R.id.textView);
        stateView.setOnRetryClickListener(new OnRetryClickListener() {
            @Override
            public void onRetryClick(View view, Type type) {
                fetchQrCode();
            }
        });
        fetchQrCode();
    }

    private void fetchQrCode() {
        stateView.showLoading();
        RequestManager.fetchChangeQR(LoginManager.getInstance().getLoginUserId(), new ResultCallback<XMResult<AvatarQRCode>>() {
            @Override
            public void onSuccess(XMResult<AvatarQRCode> result) {
                if (!ChangeAvatarDialog.this.isVisible()) return;
                if (result.isSuccess() && result.getData() != null) {
                    stateView.showContent();
                    ImageLoader.with(ChangeAvatarDialog.this)
                            .load(result.getData().getQrcode())
                            .into(qrcode);
                    textview.setVisibility(View.VISIBLE);
                } else {
                    stateView.showError();
                }



            }

            @Override
            public void onFailure(int code, String msg) {
                if (!ChangeAvatarDialog.this.isVisible()) return;
                textview.setVisibility(View.GONE);
                    stateView.showNoNetwork();


            }
        });
    }

    public ChangeAvatarDialog onCancelAction(DialogInterface.OnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        return this;
    }
}
