package com.xiaoma.login.business.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.login.R;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.network.ErrorCodeConstants;

/**
 * Created by kaka
 * on 19-6-12 下午4:43
 * <p>
 * desc: #a
 * </p>
 */
public class InvalidActivity extends BaseActivity {

    private Button mChoseOtherUser;
    private int mErrorCode;
    private TextView mTips;

    public static void start(Activity activity, int errorCode) {
        Intent intent = new Intent(activity, InvalidActivity.class);
        intent.putExtra(LoginConstants.IntentKey.ERROR_CODE, errorCode);
        activity.startActivity(intent);
    }

    public static void startForResult(Activity activity, int requestCode, int errorCode) {
        Intent intent = new Intent(activity, InvalidActivity.class);
        intent.putExtra(LoginConstants.IntentKey.ERROR_CODE, errorCode);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invalid);
        getNaviBar().hideNavi();
        mErrorCode = getIntent().getIntExtra(LoginConstants.IntentKey.ERROR_CODE, -1);
        mChoseOtherUser = findViewById(R.id.chose_other_user);
        mTips = findViewById(R.id.tips);
        String tips;
        if (mErrorCode == ErrorCodeConstants.USER_NOT_EXIST_CODE) {
            tips = getString(R.string.user_id_not_exist);
        } else if (mErrorCode == ErrorCodeConstants.BIND_KEY_USER_NOT_FOUND) {
            tips = getString(R.string.user_id_not_exist);
        } else if (mErrorCode == ErrorCodeConstants.USER_DISABLE_CODE) {
            tips = getString(R.string.user_disabled);
        } else {
            tips = getString(R.string.user_exception);
        }
        mTips.setText(tips);
        mChoseOtherUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
