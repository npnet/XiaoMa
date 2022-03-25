package com.xiaoma.login.common;

/**
 * Created by Gillben on 2019/1/10 0010
 * <p>
 * desc:
 */
public final class PasswordPageConstants {

    private PasswordPageConstants() throws Exception {
        throw new Exception();
    }

    public static final int VERIFY_PAGE_REQUEST_CODE = 0x01;
    public static final int MODIFY_PAGE_REQUEST_CODE = 0x02;
    public static final int UNBIND_VERIFY_PAGE_REQUEST_CODE = 0x03;
    public static final int BIND_VERIFY_PAGE_REQUEST_CODE = 0x04;
    public static final int RESET_PASSWD_PAGE_REQUEST_CODE = 0x05;
}
