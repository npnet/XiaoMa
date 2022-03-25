package com.xiaoma.club.common.util;

import android.content.Context;

import com.xiaoma.club.R;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;

/**
 * Author: loren
 * Date: 2019/1/14 0014
 */

public class ClubNetWorkUtils {
    public static boolean isConnected(Context context) {
        if (context == null) {
            return false;
        }
        if (!NetworkUtils.isConnected(context)) {
            XMToast.toastException(context, R.string.net_work_error);
            return false;
        }
        return true;
    }
}
