package com.xiaoma.login.business.ui.subaccount.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xiaoma.login.R;
import com.xiaoma.login.common.RequestManager;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.annotation.Ignore;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.BackHandlerHelper;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.SpanUtils;
import com.xiaoma.utils.StringUtil;

import static com.xiaoma.login.business.ui.subaccount.CreateSubAccountActivity.TAG_USER_INFO;

/**
 * Created by kaka
 * on 19-5-24 下午5:39
 * <p>
 * desc: #a
 * </p>
 */
public class VerifyPhoneFragment extends AbsCreateSubAccountFragment implements View.OnClickListener, BackHandlerHelper.FragmentBackHandler {

    public static final int VERIFY_LENGTH = 6;
    private TextView mLoginTitle;
    private EditText mPhone;
    private EditText mVerifyCode;
    private Button mLogin;
    private Button mGetVerify;
    private ImageButton mSetting;
    private CountDownTimer mVerifyCountDown;
    private TextView mCountDown;

    public static VerifyPhoneFragment newInstance() {
        return new VerifyPhoneFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_verify_phone, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initListener();
    }

    private void initListener() {
        mLoginTitle.setOnClickListener(this);
        mLogin.setOnClickListener(this);
        mGetVerify.setOnClickListener(this);
    }

    private void initView(View view) {
        mLoginTitle = view.findViewById(R.id.login_title);
        mPhone = view.findViewById(R.id.phone);
        mVerifyCode = view.findViewById(R.id.verify_code);
        mLogin = view.findViewById(R.id.login);
        mGetVerify = view.findViewById(R.id.get_verify);
        mCountDown = view.findViewById(R.id.count_down);
        mSetting = view.findViewById(R.id.setting);
    }

    @Override
    @Ignore
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.login) {
            if (!checkPhone()) {
                XMToast.toastException(mContext, R.string.pls_input_right_phon_num);
            } else if (!checkVerify()) {
                XMToast.toastException(mContext, R.string.pls_input_right_verify_code);
            } else {
                verify(mPhone.getText().toString(), mVerifyCode.getText().toString());
            }
        } else if (id == R.id.get_verify) {
            if (checkPhone()) {
                getVerifyCode(mPhone.getText().toString());
            } else {
                XMToast.toastException(mContext, R.string.pls_input_right_phon_num);
            }
        } else if (id == R.id.setting) {
            //TODO:
            XMToast.showToast(mContext, "go to setting!");
        }
    }

    private void getVerifyCode(String phone) {
        showProgressDialog(R.string.base_loading);
        RequestManager.getVerifyCode(phone, new ResultCallback<XMResult<Object>>() {
            @Override
            public void onSuccess(XMResult result) {
                dismissProgress();
                if (result.isSuccess()) {
                    mGetVerify.setEnabled(false);
                    mVerifyCountDown = new VerifyCountDown().start();
                } else {
                    XMToast.toastException(mContext, result.getResultMessage());
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                dismissProgress();
                if (TextUtils.isEmpty(msg)) {
                    msg = getContext().getString(R.string.net_work_error);
                }
                XMToast.toastException(mContext, msg);
            }
        });
    }

    private void verify(final String phone, String vin) {
        showProgressDialog(R.string.base_loading);
        RequestManager.checkPhoneCode(phone, vin, new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                if (result.isSuccess()) {
                    mCreateSubAccountVM.setPhone(phone);
                    mCreateSubAccountVM.setPageTag(TAG_USER_INFO);
                } else {
                    XMToast.toastException(mContext, result.getResultMessage());
                }
                dismissProgress();
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onFailure(int code, String msg) {
                dismissProgress();
                if (TextUtils.isEmpty(msg) || code == -1 || !NetworkUtils.isConnected(mContext)) {
                    msg = getString(R.string.no_network);
                }
                XMToast.toastException(getContext(), msg);
            }
        });
    }

    @Override
    public void onDestroy() {
        if (mVerifyCountDown != null) {
            mVerifyCountDown.cancel();
        }
        super.onDestroy();
    }

    private boolean checkPhone() {
        String phone = mPhone.getText().toString();
        return StringUtil.isMobileValid(phone);
    }

    private boolean checkVerify() {
        String verify = mVerifyCode.getText().toString();
        return !TextUtils.isEmpty(verify) && verify.length() == VERIFY_LENGTH;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    class VerifyCountDown extends CountDownTimer {

        VerifyCountDown() {
            super(60 * 1000, 1000);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (mCountDown.getVisibility() != View.VISIBLE) {
                mCountDown.setVisibility(View.VISIBLE);
            }
            int lastSecond = Math.round(millisUntilFinished / 1000f);
            String text = getString(R.string.verify_count_text);
            SpannableStringBuilder spannableStringBuilder = SpanUtils.with(getContext()).append(String.valueOf(lastSecond))
                    .setForegroundColor(getResources().getColor(R.color.get_verify_text_color))
                    .append(text).create();
            mCountDown.setText(spannableStringBuilder);
        }

        @Override
        public void onFinish() {
            mGetVerify.setEnabled(true);
            mCountDown.setVisibility(View.GONE);
        }
    }
}
