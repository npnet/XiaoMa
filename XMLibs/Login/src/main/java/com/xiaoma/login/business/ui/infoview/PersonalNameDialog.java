package com.xiaoma.login.business.ui.infoview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.xiaoma.login.R;
import com.xiaoma.login.common.RequestManager;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.ui.toast.XMToast;

public class PersonalNameDialog extends BasePersonalInfoDialog implements View.OnClickListener {

    private EditText etName;
    private String lastName;
    protected OnExDialogCallback onExDialogCallback;

    @Override
    protected boolean isCancelableOutside() {
        return false;
    }

    @Override
    protected int contentLayoutId() {
        return R.layout.dialog_personal_name;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.personal_name);
    }

    @Override
    public void onBindView(View view) {
        etName = view.findViewById(R.id.et_input_name);
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString())) {
                    getSureButton().setEnabled(false);
                } else {
                    getSureButton().setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(lastName)) {
                    getSureButton().setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        etName.setText(lastName);
    }

    @Override
    protected void onSure() {
        final String innerName = etName.getText().toString().trim();
        if (TextUtils.isEmpty(innerName)) {
            XMToast.showToast(getContext(), R.string.nameEmpty);
            return;
        } else if (innerName.length() < 2) {
            XMToast.showToast(getContext(), R.string.name_length_error);
            return;
        }
        checkValid(innerName);
    }

    public void checkValid(final String name) {
        onExDialogCallback.onShowLoading();
        RequestManager.checkUserName(name, new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                onExDialogCallback.onDismissLoading();
                if (result.isSuccess()) {
                    lastName = name;
                    onExDialogCallback.success(name);
                } else {
                    XMToast.toastException(getContext(), R.string.network_error);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                onExDialogCallback.onDismissLoading();
                if (TextUtils.isEmpty(msg)) {
                    XMToast.toastException(getContext(), R.string.network_error);
                } else {
                    XMToast.toastException(getContext(), msg);
                }
            }
        });
    }

    public void setOnExDialogCallback(OnExDialogCallback callback) {
        this.onExDialogCallback = callback;
    }

    public interface OnExDialogCallback {

        public void success(String content);

        public void onShowLoading();

        public void onDismissLoading();
    }
}
