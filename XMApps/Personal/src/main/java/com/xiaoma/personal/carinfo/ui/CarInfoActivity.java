package com.xiaoma.personal.carinfo.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.User;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.model.annotation.SingleClick;
import com.xiaoma.personal.R;
import com.xiaoma.personal.account.ui.view.BasePersonalInfoDialog;
import com.xiaoma.personal.common.util.EventConstants;
import com.xiaoma.ui.toast.XMToast;

import static com.xiaoma.network.ErrorCodeConstants.CAR_NUMBER_EXISTED_ERROR;

/**
 * @author KY
 * @date 12/3/2018
 */
@PageDescComponent(EventConstants.PageDescribe.personalCenterCarInfo)
public class CarInfoActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mCarPic;
    private LinearLayout mLlCarPlateNumber;
    private TextView mPlateNumber;
    private TextView mChassisNumber;
    private TextView mEngineNumber;
    private TextView mICCIDNumber;
    private ChangePlateNumberDialog changePlateNumberDialog;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, CarInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_info);
        initView();
        initData();
    }

    private void initView() {
        mCarPic = findViewById(R.id.car_pic);
        mLlCarPlateNumber = findViewById(R.id.ll_car_plate_number);
        mPlateNumber = findViewById(R.id.plate_number);
        mChassisNumber = findViewById(R.id.chassis_number);
        mEngineNumber = findViewById(R.id.engine_number);
        mICCIDNumber = findViewById(R.id.iccid_number);
    }

    private void initData() {

        User user = UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId());


        if (user != null) {
            mChassisNumber.setText(user.getVin());
            mEngineNumber.setText(user.getEngineNumber());
            mPlateNumber.setText(user.getPlateNumber());
            mICCIDNumber.setText(ConfigManager.DeviceConfig.getICCID(this));
        }

        mLlCarPlateNumber.setOnClickListener(this);
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.carinfoPlate})
    @ResId({R.id.ll_car_plate_number})
    @SingleClick(800)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_car_plate_number:
                showChangePlateNumberDialog();
                break;
            default:
        }
    }

    private void showChangePlateNumberDialog() {
        if (changePlateNumberDialog == null) {
            changePlateNumberDialog = new ChangePlateNumberDialog();
            changePlateNumberDialog.setOnDialogCallback(new BasePersonalInfoDialog.OnDialogCallback() {
                @Override
                public void onLoading() {
                    showProgressDialog(R.string.modifying);
                }

                @Override
                public void success(String content) {
                    mPlateNumber.setText(content);
                    XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.changeCarInfo,
                            content, "CarInfoActivity", EventConstants.PageDescribe.personalCenterCarInfo);
                }

                @Override
                public void fail(int code, String msg) {
                    if (code == CAR_NUMBER_EXISTED_ERROR) {
                        XMToast.toastException(CarInfoActivity.this, R.string.car_plate_exist);
                    } else {
                        XMToast.toastException(CarInfoActivity.this, R.string.modify_failed);
                    }
                }

                @Override
                public void onComplete() {
                    dismissProgress();
                }
            });
        }
        changePlateNumberDialog.setPlateNumber(mPlateNumber.getText().toString());
        if (changePlateNumberDialog.isVisible()) {
            changePlateNumberDialog.dismiss();
        }
        changePlateNumberDialog.show(getSupportFragmentManager(), null);
    }
}
