package com.xiaoma.login.business.ui.password;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.login.R;
import com.xiaoma.login.business.ui.verify.view.NumberKeyboard;
import com.xiaoma.model.annotation.SingleClick;
import com.xiaoma.ui.view.UnderLineTextView;
import com.xiaoma.utils.DoubleClickUtils;

/**
 * Created by Gillben on 2019/1/9 0009
 * <p>
 * desc: 密码输入基类
 */
public abstract class BasePasswordActivity extends BaseActivity implements View.OnClickListener, NumberKeyboard.onKeyEventListener {

    private UnderLineTextView underLineTextView;
    private NumberKeyboard numberKeyboard;
    private ImageView deletePasswordImage;
    private ImageView descImage;
    private TextView descText;
    private TextView verifyTitle;
    private Button mConfirmButton;
    protected boolean isNext = false;
    protected View mForgetPasswd;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNaviBar().showBackNavi();
        setContentView(R.layout.input_password_view);
        isNext = isNext();
        initView();
    }

    protected void initView() {
        verifyTitle = findViewById(R.id.verify_title);
        underLineTextView = findViewById(R.id.password_input_area);
        numberKeyboard = findViewById(R.id.number_key_board);
        deletePasswordImage = findViewById(R.id.iv_delete_password);
        descImage = findViewById(R.id.iv_desc_image);
        descText = findViewById(R.id.tv_desc_text);
        mConfirmButton = findViewById(R.id.password_confirm_bt);
        mForgetPasswd = findViewById(R.id.forget_passwd);

        numberKeyboard.setOnKeyEventListener(this);
        deletePasswordImage.setOnClickListener(this);
        mConfirmButton.setOnClickListener(this);
        mForgetPasswd.setOnClickListener(this);

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

        mForgetPasswd.setVisibility(showForget() ? View.VISIBLE : View.GONE);
    }

    @Override
    @SingleClick(0)
    public void onClick(View v) {
        if (DoubleClickUtils.isFastDoubleClick(200)) return;
        int i = v.getId();
        if (i == R.id.iv_delete_password) {
            deletePassword();
        } else if (i == R.id.password_confirm_bt) {
            finishInputThenConfirm();
        }
    }

    public void setTexts(PasswdTexts texts) {
        setTitleText(texts.getTitle());
        setButtonText(texts.getButton());
        setDescText(texts.getDesc());
    }

    protected abstract void finishInputThenConfirm();

    protected boolean showForget() {
        return false;
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

    protected void cleanPasswordText() {
        underLineTextView.empty();
    }


    protected void setDescImage(String url) {
        Glide.with(this)
                .load(url)
                .into(descImage);
    }

    protected void setDescImage(int resId) {
        descImage.setImageResource(resId);
    }


    protected void setDescText(String desc) {
        descText.setText(desc);
    }

    protected void setTitleText(@StringRes int resId) {
        verifyTitle.setText(resId);
    }

    protected void setTitleText(String title) {
        verifyTitle.setText(title);
    }

    protected void setDescText(int resId) {
        descText.setText(resId);
    }


    protected void setButtonText(String text) {
        mConfirmButton.setText(text);
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

}
