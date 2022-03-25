package com.xiaoma.personal.carinfo.ui;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.User;
import com.xiaoma.model.XMResult;
import com.xiaoma.personal.R;
import com.xiaoma.personal.account.ui.view.BasePersonalInfoDialog;
import com.xiaoma.personal.common.OnlyCode;
import com.xiaoma.personal.common.RequestManager;
import com.xiaoma.personal.common.util.EventConstants;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.WheelPickView;
import com.xiaoma.utils.NetworkUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static com.xiaoma.personal.common.util.EventConstants.NormalClick.changeCarInfo;

/**
 * @author KY
 * @date 12/3/2018
 */
public class ChangePlateNumberDialog extends BasePersonalInfoDialog {

    private static final String TAG = "ChangePlateNumberDialog";
    private WheelPickView mProvince;
    private WheelPickView mCity;
    private EditText mPlateNumber;
    private String mInitPlateNumber;
    private String mCurPlateNumber;
    // 车牌正则
    private Pattern pattern = Pattern.compile("[A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9挂学警港澳]{1}|(([0-9]{5}[DF])|([DF][A-HJ-NP-Z0-9][0-9]{4}))");
    private List<String> provinceList;
    private List<String> areaList;
    //    private Pattern pattern = Pattern.compile("^[0-9A-Za-z]{5,6}$");

    @Override
    protected int contentLayoutId() {
        return R.layout.dialog_plate_number;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.change_car_plate);
    }

    @Override
    protected void onSure() {
        String plateNumber = mPlateNumber.getText().toString();
        if (isPlateValid(plateNumber)) {
            modifyPlate();
        } else {
            XMToast.toastException(getContext(), R.string.plate_number_invalid);
        }
    }

    @Override
    protected WindowManager.LayoutParams changeWindowParams(WindowManager.LayoutParams lp) {
        lp.width = 650;
        lp.height = 420;
        return lp;
    }

    @Override
    protected boolean isCancelableOutside() {
        return false;
    }

    @Override
    public void onBindView(View view) {
        mProvince = view.findViewById(R.id.province);
        mCity = view.findViewById(R.id.city);
        mPlateNumber = view.findViewById(R.id.plate_number);

        provinceList = Arrays.asList(getContext().getResources().getString(R.string.car_number_province).split(","));
        areaList = Arrays.asList(getContext().getResources().getString(R.string.car_number_area).split(","));
        mProvince.setItems(provinceList);
        mCity.setItems(areaList);

        initPlate();

        mProvince.setOnWheelViewListener(new WheelPickView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                mCurPlateNumber = item + mCurPlateNumber.substring(1);
                checkModified();
            }
        });

        mCity.setOnWheelViewListener(new WheelPickView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                mCurPlateNumber = mCurPlateNumber.substring(0, 1) + item + mCurPlateNumber.substring(2);
                checkModified();
            }
        });

        mPlateNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mCurPlateNumber = mCurPlateNumber.substring(0, 2) + s.toString();
                checkModified();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initPlate();
    }

    private void modifyPlate() {
        onLoading();
        RequestManager.changeUserCarPlateNumber(mCurPlateNumber, new ResultCallback<XMResult<OnlyCode>>() {
            @Override
            public void onSuccess(XMResult<OnlyCode> xmResult) {
                if (xmResult.isSuccess()) {
                    User user = UserManager.getInstance().getCurrentUser();
                    user.setPlateNumber(mCurPlateNumber);
                    UserManager.getInstance().notifyUserUpdate(user);
                    onSuccessResult(mCurPlateNumber);
                    XMToast.toastSuccess(getContext(), R.string.modify_success);
                    dismiss();
                    XmAutoTracker.getInstance().onEvent(changeCarInfo, mCurPlateNumber,
                            "CarInfoActivity", EventConstants.PageDescribe.personalCenterCarInfo);
                } else {
                    onFailure(xmResult.getResultCode(), xmResult.getResultMessage());
                }
                onComplete();
            }

            @Override
            public void onFailure(int code, String msg) {
                if (!NetworkUtils.isConnected(getContext())) {
                    XMToast.toastException(getContext(), R.string.no_network);
                } else {
                    onFail(code, msg);
                }
                onComplete();
            }
        });
    }

    private void checkModified() {
        if (TextUtils.isEmpty(mPlateNumber.getText())) {
            getSureButton().setEnabled(false);
        } else {
            getSureButton().setEnabled(true);
        }
    }

    private void initPlate() {
        if (!TextUtils.isEmpty(mInitPlateNumber)) {
            String provice = mInitPlateNumber.substring(0, 1);
            String city = mInitPlateNumber.substring(1, 2);
            String number = mInitPlateNumber.substring(2);

            mProvince.setSelection(provinceList.indexOf(provice));
            mCity.setSelection(areaList.indexOf(city));
            mPlateNumber.setText(number);
            mPlateNumber.setSelection(mPlateNumber.length());
        } else {
            mCity.setSelection(1);
            mProvince.setSelection(1);
            mInitPlateNumber = mCurPlateNumber = provinceList.get(1) + areaList.get(1);
        }
    }

    private boolean isPlateValid(String plateNumber) {
        return pattern.matcher(plateNumber).find();
    }

    public void setPlateNumber(String plateNumber) {
        mCurPlateNumber = mInitPlateNumber = plateNumber;
    }
}
