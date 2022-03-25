package com.xiaoma.ad.provider;

import android.net.Uri;

/**
 * @author KY
 * @date 2018/9/13
 */
public class Constant {
    public static final String AUTHORITY = "com.xiaoma.preference";
    public static final Uri URI = Uri.parse("content://" + AUTHORITY);
    public static final String METHOD_CONTAIN_KEY = "method_contain_key";
    public static final String METHOD_QUERY_VALUE = "method_query_value";
    public static final String METHOD_EDIT_VALUE = "method_edit";
    public static final String METHOD_QUERY_PID = "method_query_pid";
    public static final String KEY_VALUES = "key_result";
}
