package com.xiaoma.login.business.ui;

import com.xiaoma.login.common.LoginMethod;

/**
 * Created by kaka
 * on 19-1-11 下午4:54
 * <p>
 * desc: 车机初始化的Activity，会在这里初始化主账号数据，以及一些其他需要在真正进入桌面之前初始化的东西
 * </p>
 */
public class CarInitActivity extends AbsInitActivity {
    private static final String TAG = CarInitActivity.class.getSimpleName();

    @Override
    protected void fetchBindUser() {
        onFetchResult(keyBoundUser, mCarKey == null ? null : mCarKey.isBle() ? LoginMethod.KEY_BLE : LoginMethod.KEY_NORMAL);
    }
}
