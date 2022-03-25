package com.xiaoma.personal.account.ui.view;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.User;
import com.xiaoma.model.XMResult;
import com.xiaoma.personal.R;
import com.xiaoma.personal.common.OnlyCode;
import com.xiaoma.personal.common.RequestManager;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;

public class PersonalNameDialog extends BasePersonalInfoDialog implements View.OnClickListener {

    private EditText etName;
    private String lastName;

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

        final User user = UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId());
        if (user != null) {
            etName.setText(user.getName());
            Editable text = etName.getText();
            etName.setSelection(text == null ? 0 : text.length());
            lastName = user.getName();
        }

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
    protected void onSure() {
        onLoading();
        final String innerName = etName.getText().toString().trim();
        if (TextUtils.isEmpty(innerName)) {
            XMToast.showToast(getContext(), R.string.nameEmpty);
            etName.setText("");
            onComplete();
            return;
        }
        RequestManager.changeUserName(innerName, new ResultCallback<XMResult<OnlyCode>>() {
            @Override
            public void onSuccess(XMResult<OnlyCode> result) {
                if (result.isSuccess()) {
                    User user = UserManager.getInstance().getCurrentUser();
                    user.setName(innerName);
                    UserManager.getInstance().notifyUserUpdate(user);
                    onSuccessResult(innerName);
                    XMToast.toastSuccess(getContext(), R.string.modify_success);
                    dismiss();
                } else {
                    onFail(result.getResultCode(), result.getResultMessage());
                }
                onComplete();
            }

            @Override
            public void onFailure(int code, String msg) {
                if (!NetworkUtils.isConnected(getContext())) {
                    XMToast.toastException(getContext(), R.string.no_network);
                } else {
                    onFail(code, msg);
                }
                onComplete();
            }
        });
    }
}
