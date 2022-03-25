package com.xiaoma.smarthome.login.ui;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.smarthome.R;
import com.xiaoma.smarthome.common.constants.SmartConstants;
import com.xiaoma.smarthome.common.manager.ISmartHomeManager;
import com.xiaoma.smarthome.common.manager.SmartHomeManager;
import com.xiaoma.smarthome.common.model.AccountRecord;
import com.xiaoma.smarthome.scene.ui.XiaoBaiSelectSceneFragment;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.tputils.TPUtils;

public class XiaoBaiLoginFragment extends BaseFragment implements ISmartHomeManager.OnSmartHomeLoginCallback {

    private EditText etAccount;
    private EditText etPassword;
    private Button btnLogin;

    private String userName;
    private String passWord;

    public static XiaoBaiLoginFragment newInstance() {
        return new XiaoBaiLoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_xiaobai_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);
    }

    private void initView(View view) {
        SmartHomeManager.getInstance().registerSmartHomeLoginCallback(this);

        etAccount = view.findViewById(R.id.et_account);
        etPassword = view.findViewById(R.id.et_password);
        btnLogin = view.findViewById(R.id.btn_login);

        AccountRecord accountRecord = TPUtils.getObject(getContext(), SmartConstants.KEY_LOGIN_ACCOUNT, AccountRecord.class);
        if (accountRecord != null) {
            if (!TextUtils.isEmpty(accountRecord.account)) {
                etAccount.setText(accountRecord.account);

            } else {
                btnLogin.setEnabled(false);
            }
            if (!TextUtils.isEmpty(accountRecord.passWord)) {
                etPassword.setText(accountRecord.passWord);

            } else {
                btnLogin.setEnabled(false);
            }

        } else {
            btnLogin.setEnabled(false);
        }

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtil.isEmpty(s.toString().trim()) && !StringUtil.isEmpty(etPassword.getText().toString().trim())) {
                    btnLogin.setEnabled(true);
                } else {
                    btnLogin.setEnabled(false);
                }
            }
        };

        etAccount.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etAccount.getText())) {
                    XMToast.toastException(getContext(), getString(R.string.account_not_null));
                    return;
                }
                if (TextUtils.isEmpty(etPassword.getText())) {
                    XMToast.toastException(getContext(), getString(R.string.password_not_null));
                    return;
                }
                if (!NetworkUtils.isConnected(getContext())) {
                    XMToast.toastException(getContext(), getString(R.string.no_net_work));
                    return;
                }
                userName = etAccount.getText().toString();
                passWord = etPassword.getText().toString();

                showProgressDialog(getString(R.string.login_ing));
                SmartHomeManager.getInstance().login(userName, passWord);
            }
        });

        //设置密码转换
        etPassword.setTransformationMethod(new TransformationMethod() {
            @Override
            public CharSequence getTransformation(CharSequence source, View view) {
                return new PasswordCharSequence(source);
            }

            @Override
            public void onFocusChanged(View view, CharSequence sourceText, boolean focused, int direction, Rect previouslyFocusedRect) {
            }
        });
    }

    @Override
    public void onLoginSuccess() {
        dismissProgress();
        TPUtils.putObject(getContext(), SmartConstants.KEY_LOGIN_ACCOUNT, new AccountRecord(userName, passWord));
        XmTracker.getInstance().uploadEvent(-1, TrackerCountType.LOGINSMARTACCOUNT.getType());
        if (getActivity() != null) {
            //移除所有fragment
            getActivity().getSupportFragmentManager().popBackStack();
            //打开场景列表
            FragmentUtils.replace(getActivity().getSupportFragmentManager(), XiaoBaiSelectSceneFragment.newInstance(),
                    R.id.container_frame, MainActivity.FRAGMENT_TAG_XIAOBAI_SELECTSCENE, true);
        }
    }

    @Override
    public void onLoginFail(String errorMsg) {
        dismissProgress();
        XMToast.toastException(getContext(), errorMsg);
    }

    /**
     * 将密码转换成*显示
     */
    private class PasswordCharSequence implements CharSequence {
        private CharSequence mSource;

        public PasswordCharSequence(CharSequence source) {
            mSource = source; // Store char sequence
        }

        @Override
        public char charAt(int index) {
            //这里返回的char，就是密码的样式，注意，是char类型的
            return '*';
        }

        @Override
        public int length() {
            return mSource.length();
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return mSource.subSequence(start, end); // Return default
        }
    }
}
