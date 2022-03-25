package com.xiaoma.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.xiaoma.utils.share.ShareCallBack;
import com.xiaoma.utils.share.ShareClubBean;
import com.xiaoma.utils.share.ShareConstants;

/**
 * Author: loren
 * Date: 2019/4/28 0028
 */

public class ShareUtils {

    public static final String CLUB_PACKAGE = "com.xiaoma.club";
    public static final String SHARE_KEY = "share_key";
    public static final String HAS_BEEN_MEMBER = "has_been_member";
    public static final String CAR_TEAM_ID = "car_team_id";

    public static void shareToClub(Context context, ShareClubBean bean, ShareCallBack callBack) {
        if (context == null) {
            return;
        }
        if (!AppUtils.isAppInstalled(context, CLUB_PACKAGE)) {
            if (callBack != null) {
                callBack.shareError(context.getString(R.string.club_is_not_installed));
            }
            return;
        }
        try {
            Intent intent = new Intent();
            intent.setAction(ShareConstants.CLUB_SHARE_ACTION);
            Bundle bundle = new Bundle();
            bundle.putParcelable(SHARE_KEY, bean);
            intent.putExtras(bundle);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            if (callBack != null) {
                callBack.shareError(context.getString(R.string.share_failed));
            }
        }
    }

    public static void handleShare(Context context, String pck, String action, String coreKey, ShareCallBack callBack) {
        if (context == null || TextUtils.isEmpty(pck) || TextUtils.isEmpty(action) || TextUtils.isEmpty(coreKey)) {
            callBack.shareError(context.getString(R.string.jump_failed));
            return;
        }
        if (!AppUtils.isAppInstalled(context, pck)) {
            callBack.shareError(context.getString(R.string.is_not_installed));
            return;
        }
        try {
            Intent intent = new Intent();
            intent.setAction(action);
            intent.putExtra(ShareUtils.SHARE_KEY, coreKey);
            context.startActivity(intent);
            callBack.shareSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            callBack.shareError(context.getString(R.string.jump_failed));
        }
    }
}
