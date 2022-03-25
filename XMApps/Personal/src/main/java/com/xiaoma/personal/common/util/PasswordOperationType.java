package com.xiaoma.personal.common.util;

/**
 * Created by Gillben on 2019/1/15 0015
 * <p>
 * desc: 密码验证、或修改项
 */
public enum PasswordOperationType {

    VERIFY(0),
    MODIFY(1);


    int value;

    PasswordOperationType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
