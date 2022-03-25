package com.xiaoma.login.business.ui.password;

import android.support.annotation.StringRes;

import com.xiaoma.login.R;

public enum PasswdTexts {
    NORMAL(R.string.passwd_verify_title, R.string.confirm, R.string.passwd_verify_tip),
    MANAGER(R.string.passwd_verify_title, R.string.confirm, R.string.need_input_passwd),
    BIND(R.string.passwd_verify_title, R.string.confirm_bind, R.string.confirm_bind_desc),
    UNBIND(R.string.passwd_verify_title, R.string.confirm_unbind, R.string.confirm_unbind_desc),
    LOGIN(R.string.passwd_verify_title, R.string.login, R.string.passwd_verify_tip);

    PasswdTexts(int title, int button, int desc) {
        this.title = title;
        this.button = button;
        this.desc = desc;
    }

    @StringRes
    private int title;
    @StringRes
    private int button;
    @StringRes
    private int desc;

    public int getTitle() {
        return title;
    }

    public int getButton() {
        return button;
    }

    public int getDesc() {
        return desc;
    }
}
