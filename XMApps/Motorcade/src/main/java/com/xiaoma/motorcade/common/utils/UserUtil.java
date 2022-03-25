package com.xiaoma.motorcade.common.utils;

import com.xiaoma.login.LoginManager;
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
            return UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
