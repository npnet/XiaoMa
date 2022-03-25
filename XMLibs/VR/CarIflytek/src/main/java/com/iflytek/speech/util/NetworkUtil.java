//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.iflytek.speech.util;

import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

public class NetworkUtil {
    private static final String tag = "NetworkUtil";
    public static final String NET_UNKNOWN = "none";
    public static final String NET_WIFI = "wifi";
    public static final String NET_CMWAP = "cmwap";
    public static final String NET_UNIWAP = "uniwap";
    public static final String NET_CTWAP = "ctwap";
    public static final String NET_CTNET = "ctnet";

    public NetworkUtil() {
    }

    public static String getNetType(NetworkInfo info) {
        if (info == null) {
            return "none";
        } else {
            try {
                if (info.getType() == 1) {
                    return "wifi";
                } else {
                    String extra = info.getExtraInfo().toLowerCase();
                    if (TextUtils.isEmpty(extra)) {
                        return "none";
                    } else if (!extra.startsWith("3gwap") && !extra.startsWith("uniwap")) {
                        if (extra.startsWith("cmwap")) {
                            return "cmwap";
                        } else if (extra.startsWith("ctwap")) {
                            return "ctwap";
                        } else {
                            return extra.startsWith("ctnet") ? "ctnet" : extra;
                        }
                    } else {
                        return "uniwap";
                    }
                }
            } catch (Exception var2) {
                Log.d("NetworkUtil", var2.toString());
                return "none";
            }
        }
    }

    public static String getNetSubType(NetworkInfo info) {
        if (info == null) {
            return "none";
        } else {
            try {
                if (info.getType() == 1) {
                    return "none";
                } else {
                    String subtype = "";
                    subtype = subtype + info.getSubtypeName();
                    subtype = subtype + ";" + info.getSubtype();
                    return subtype;
                }
            } catch (Exception var2) {
                Log.d("NetworkUtil", var2.toString());
                return "none";
            }
        }
    }
}
