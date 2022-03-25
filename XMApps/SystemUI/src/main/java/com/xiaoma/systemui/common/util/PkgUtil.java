package com.xiaoma.systemui.common.util;

public class PkgUtil {
    // 是否为系统包名
    public static boolean isSystemPackage(String packageName) {
        return packageName != null &&
                (packageName.startsWith("com.android") || packageName.startsWith("android"));
    }

}
