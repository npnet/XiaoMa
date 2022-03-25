package com.xiaoma.component.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * @author taojin
 * @date 2019/5/7
 */
public class LauncherActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
    }
}
