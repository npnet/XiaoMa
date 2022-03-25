package com.xiaoma.club.common.util;

import com.xiaoma.login.UserManager;
import com.xiaoma.model.User;

/**
 * Created by LKF on 2018/10/10 0010.
 */
public class UserUtil {
    /**
     * 获取当前用户
     */
    public static User getCurrentUser() {
        try {
            return UserManager.getInstance().getCurrentUser();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long getCurrentUid() {
        User u = getCurrentUser();
        if (u != null)
            return u.getId();
        return 0;
    }
}
