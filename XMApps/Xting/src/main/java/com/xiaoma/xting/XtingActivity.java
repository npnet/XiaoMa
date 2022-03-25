package com.xiaoma.xting;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xiaoma.component.base.BaseActivity;

/**
 * @author taojin
 * @date 2019/3/11
 */
public class XtingActivity extends BaseActivity {

    private static final String TAG = XtingActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
    }
}
