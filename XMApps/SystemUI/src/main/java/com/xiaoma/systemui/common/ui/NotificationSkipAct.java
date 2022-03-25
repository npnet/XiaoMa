package com.xiaoma.systemui.common.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by LKF on 2019-3-14 0014.
 */
public class NotificationSkipAct extends Activity {
    public static final String EXTRA_TEXT = "text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String text = getIntent().getStringExtra(EXTRA_TEXT);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        finish();
    }
}
