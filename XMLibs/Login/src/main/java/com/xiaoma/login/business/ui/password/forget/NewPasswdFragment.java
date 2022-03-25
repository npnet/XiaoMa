package com.xiaoma.login.business.ui.password.forget;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.login.R;
import com.xiaoma.login.business.ui.verify.view.NumberKeyboard;
import com.xiaoma.model.annotation.SingleClick;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.UnderLineTextView;
import com.xiaoma.utils.BackHandlerHelper;

import static com.xiaoma.login.business.ui.password.forget.ForgetPasswdActivity.TAG_RESET;


/**
 * Created by kaka
 * on 19-5-24 下午4:56
 * <p>
 * desc: #a
 * </p>
 */
public class NewPasswdFragment extends AbsForgetPasswdFragment implements View.OnClickListener, NumberKeyboard.onKeyEventListener, BackHandlerHelper.FragmentBackHandler {
    private Status mStatus = Status.first_input;
    private String passwd;
    private UnderLineTextView underLineTextView;
    private NumberKeyboard numberKeyboard;
    private ImageView deletePasswordImage;
    private ImageView descImage;
    private TextView descText;
    private TextView verifyTitle;
    private Button mConfirmButton;
    protected boolean isNext = false;

    public static NewPasswdFragment newInstance() {
        return new NewPasswdFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_input_passwd, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        refreshTips();
    }

    protected void initView(View view) {
        verifyTitle = view.findViewById(R.id.verify_title);
        underLineTextView = view.findViewById(R.id.password_input_area);
        numberKeyboard = view.findViewById(R.id.number_key_board);
        deletePasswordImage = view.findViewById(R.id.iv_delete_password);
        descImage = view.findViewById(R.id.iv_desc_image);
        descText = view.findViewById(R.id.tv_desc_text);
        mConfirmButton = view.findViewById(R.id.password_confirm_bt);

        numberKeyboard.setOnKeyEventListener(this);
        deletePasswordImage.setOnClickListener(this);
        mConfirmButton.setOnClickListener(this);

        underLineTextView.addContentLengthChangeCallback(new UnderLineTextView.OnContentLengthChangeCallback() {
            @Override
            public void contentLengthChange(int length) {
                if (length == 4) {
                    mConfirmButton.setEnabled(true);
                } else {
                    mConfirmButton.setEnabled(false);
                }
            }
        });

        verifyTitle.setText(R.string.input_new_passwd);
    }

    @Override
    @SingleClick(200)
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_delete_password) {
            deletePassword();
        } else if (i == R.id.password_confirm_bt) {
            finishInputThenConfirm();
        }
    }

    @SuppressLint("MissingPermission")
    protected void finishInputThenConfirm() {

        if (mStatus == Status.first_input) {
            mStatus = Status.second_input;
            passwd = getPasswordText();
            refreshTips();
        } else if (mStatus == Status.second_input
                || mStatus == Status.created) {
            if (!TextUtils.isEmpty(passwd) && passwd.equals(getPasswordText())) {
                mForgetPasswdVM.setPasswd(passwd);
                mForgetPasswdVM.setPageTag(TAG_RESET);
                mStatus = Status.created;
            } else {
                mStatus = Status.first_input;
                passwd = null;
                cleanPasswordText();
                refreshTips();
                XMToast.toastException(mContext, R.string.passwd_not_same_pls_retry);
            }
        }
    }

    private void refreshTips() {
        if (mStatus == Status.first_input) {
            setButtonText(R.string.confirm_password);
            setDescText(R.string.please_enter_your_new_password);
            if (!TextUtils.isEmpty(passwd) && passwd.length() == 4) {
                underLineTextView.setString(passwd);
                mConfirmButton.setEnabled(true);
            }
        } else if (mStatus == Status.second_input) {
            cleanPasswordText();
            setButtonText(R.string.create_account);
            setDescText(R.string.please_enter_your_sub_account_password_again);
        } else if (mStatus == Status.created) {
            setButtonText(R.string.create_account);
            setDescText(R.string.please_enter_your_sub_account_password_again);
            if (!TextUtils.isEmpty(passwd) && passwd.length() == 4) {
                underLineTextView.setString(passwd);
                mConfirmButton.setEnabled(true);
            }
        }
    }

    protected boolean isNext() {
        return false;
    }

    protected void setNext(boolean next) {
        this.isNext = next;
    }

    protected String getPasswordText() {
        return underLineTextView.getString();
    }

    protected void setPasswordText(String passwd) {
        underLineTextView.setString(passwd);
    }

    protected void cleanPasswordText() {
        underLineTextView.empty();
    }

    protected void setDescText(int resId) {
        descText.setText(resId);
    }

    protected void setButtonText(int resId) {
        mConfirmButton.setText(resId);
    }

    private void deletePassword() {
        underLineTextView.backspace();
    }


    @Override
    public void onKey(int number) {
        underLineTextView.put(String.valueOf(number).charAt(0));
    }

    @Override
    public void onBackspace() {
        //Nothing to do
    }

    @Override
    public boolean onBackPressed() {
        if (mStatus == Status.second_input
                || mStatus == Status.created) {
            mStatus = Status.first_input;
            refreshTips();
            return true;
        } else {
            return false;
        }
    }

    enum Status {
        first_input,
        second_input,
        created
    }
}
