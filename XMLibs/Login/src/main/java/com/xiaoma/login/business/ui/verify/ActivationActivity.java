package com.xiaoma.login.business.ui.verify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.login.R;
import com.xiaoma.ui.toast.XMToast;

import static com.xiaoma.login.common.LoginConstants.RESULT_OFFLINE_LOGIN;

/**
 * Created by kaka
 * on 19-6-14 下午5:51
 * <p>
 * desc: #a
 * </p>
 */
public class ActivationActivity extends BaseActivity {

    public static final int OFFLINE_REQUEST_CODE = 1001;

    private TextView mIccid;
    private TextView mImei;
    private Button mActive;

    public static void startForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ActivationActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);
        getNaviBar().hideNavi();
        mIccid = findViewById(R.id.iccid);
        mImei = findViewById(R.id.imei);
        mActive = findViewById(R.id.active);

        mIccid.setText(getString(R.string.iccid, ConfigManager.DeviceConfig.getICCID(this)));
        mImei.setText(getString(R.string.imei, ConfigManager.DeviceConfig.getIMEI(this)));
        mActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genActiveFile();
            }
        });
    }

    private void genActiveFile() {
        boolean active = ConfigManager.FileConfig.Active();
        if (active) {
            setResult(RESULT_OK);
            finish();
        } else {
            XMToast.toastException(ActivationActivity.this, R.string.activation_error);
//            OfflineActivationActivity.startForResult(ActivationActivity.this, OFFLINE_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OFFLINE_REQUEST_CODE && resultCode == RESULT_OK) {
            setResult(RESULT_OFFLINE_LOGIN);
            finish();
        }
    }
}
