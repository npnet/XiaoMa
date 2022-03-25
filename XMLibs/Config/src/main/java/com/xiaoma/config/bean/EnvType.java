package com.xiaoma.config.bean;

/**
 * Created by youthyj on 2018/9/17.
 */
public enum EnvType {
    CUSTOM,
    DEV,
    TEST,
    OFFICE,
    EXP;

    public static final EnvType getEnvType(String desc) {
        if ("DEV".equalsIgnoreCase(desc)) {
            return DEV;
        }
        if ("TEST".equalsIgnoreCase(desc)) {
            return TEST;
        }
        if ("OFFICE".equalsIgnoreCase(desc)) {
            return OFFICE;
        }
        if ("EXP".equalsIgnoreCase(desc)) {
            return EXP;
        }
        return TEST;
    }

    public static final String getEnvDesc(EnvType type) {
        switch (type) {
            case CUSTOM:
                return "CUSTOM";
            case DEV:
                return "DEV";
            case TEST:
                return "TEST";
            case OFFICE:
                return "OFFICE";
            case EXP:
                return "EXP";
        }
        return "UNKNOWN";
    }
}
