package com.xiaoma.personal.common.util;

/**
 * Created by Gillben on 2019/1/17 0017
 * <p>
 * desc: 满意度类型 1：满意  0：不满意
 */
public enum SatisfactionType {

    GOOD(1),
    BAD(0);

    public int value;

    SatisfactionType(int value){
        this.value = value;
    }

}
