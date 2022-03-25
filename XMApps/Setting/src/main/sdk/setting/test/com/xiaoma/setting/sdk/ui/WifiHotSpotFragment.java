package com.xiaoma.setting.sdk.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.setting.R;
import com.xiaoma.setting.common.constant.EventConstants;
import com.xiaoma.setting.common.utils.StringUtils;
import com.xiaoma.setting.common.views.ErrorDialog;
import com.xiaoma.setting.common.views.ProgressDialog;
import com.xiaoma.ui.dialog.InputDialog;
import com.xiaoma.ui.dialog.XmDialog;
import com.xiaoma.ui.toast.XMToast;

/**
 * @Author ZiXu Huang
 * @Data 2018/10/15
 */
@PageDescComponent(EventConstants.PageDescribe.wifiHotSpotFragmentPagePathDesc)
public class WifiHotSpotFragment extends BaseFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String WIFI_AP_NAME = "wifi_ap_name";
    private static final String WIFI_AP_PSW = "wifi_ap_psw";
    private static int MAX_HOTSPOT_NAME_LENGHT = 15;
    private static int MAX_HOTSPOT_PSW_LENGHT = 20;
    private TextView hotspotName;
    private TextView hotspotPsw;
    private ViewGroup nameParent;
    private ViewGroup pswParent;
    private CheckBox hotspotSwitch;
    private SharedPreferences sp;
    private TextView connectedAmount;
    private int connectedNum = 0;
    private View hotspotCloseBg;
    private ProgressDialog wifiAPOpenDialog;
    private CountDownTimer countDownTimer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_wifi_hotspot, container, false);
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        bindView(view);
        initEvent();
    }

    private void initEvent() {
        nameParent.setOnClickListener(this);
        pswParent.setOnClickListener(this);
        hotspotSwitch.setOnCheckedChangeListener(this);
    }

    private void bindView(View view) {
        String savedName = sp.getString(WIFI_AP_NAME, null);
        String savedPsw = sp.getString(WIFI_AP_PSW, null);
        hotspotName = view.findViewById(R.id.hotspot_name);
        hotspotPsw = view.findViewById(R.id.hotspot_psw);
        if (!TextUtils.isEmpty(savedName)) {
            hotspotName.setText(StringUtils.subString(MAX_HOTSPOT_NAME_LENGHT, savedName));
        }
        if (!TextUtils.isEmpty(savedPsw)) {
            hotspotPsw.setText(StringUtils.subString(MAX_HOTSPOT_PSW_LENGHT, savedPsw));
        }
        nameParent = view.findViewById(R.id.name_parent);
        pswParent = view.findViewById(R.id.psw_parent);
        connectedAmount = view.findViewById(R.id.connected_amount);
        connectedAmount.setText(String.format(getResources().getString(R.string.connected_amount), connectedNum));
        hotspotSwitch = view.findViewById(R.id.wifi_hotspot_switch);
        hotspotCloseBg = view.findViewById(R.id.hotspot_close_bg_parent);
    }

    private void changeView(boolean wifiAPEnable) {
        if (wifiAPEnable) {
            pswParent.setVisibility(View.VISIBLE);
            nameParent.setVisibility(View.VISIBLE);
            connectedAmount.setVisibility(View.VISIBLE);
            hotspotCloseBg.setVisibility(View.GONE);
        } else {
            pswParent.setVisibility(View.GONE);
            nameParent.setVisibility(View.GONE);
            connectedAmount.setVisibility(View.GONE);
            hotspotCloseBg.setVisibility(View.VISIBLE);
        }
    }


    private void setWifiAPEnable(boolean isChecked) {

    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.editHotspotName, EventConstants.NormalClick.editHotspotPsw})
    @ResId({R.id.name_parent, R.id.psw_parent})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.name_parent:
                editHotspotName();
                break;
            case R.id.psw_parent:
                editHotspotPsw();
                break;
        }
    }

    private void editHotspotPsw() {
        final InputDialog dialog = new InputDialog(getActivity());
        final EditText bltNameEt = dialog.getEditText();
        String savedPsw = sp.getString(WIFI_AP_PSW, null);
        bltNameEt.setText(savedPsw);
        bltNameEt.setSelection(bltNameEt.getText().toString().length());
        dialog.setTitle(getString(R.string.please_input_hotspot_psw))
                .setPositiveButton(getString(R.string.confirm), new InputDialog.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(String editext) {
                        String inputText = bltNameEt.getText().toString().trim();
                        if (inputText.length() < 8) {
                            showToast(getContext().getResources().getString(R.string.need_length_8));
                        } else {
                            sp.edit().putString(WIFI_AP_PSW, inputText).apply();
                            hotspotPsw.setText(StringUtils.subString(MAX_HOTSPOT_PSW_LENGHT, inputText));
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new InputDialog.OnCancelClickListener() {
                    @Override
                    public void onCancelClick() {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void showEditSuccessDialog() {
        ErrorDialog errorDialog = new ErrorDialog(getContext());
        errorDialog.show();
        errorDialog.setErrorImg(R.drawable.icon_success);
        errorDialog.setErrorMsg(R.string.edit_success);
    }

    private void editHotspotName() {
        final InputDialog dialog = new InputDialog(getActivity());
        final EditText bltNameEt = dialog.getEditText();
        final String savedName = sp.getString(WIFI_AP_NAME, null);
        bltNameEt.setText(savedName);
        bltNameEt.setSelection(bltNameEt.getText().toString().length());
        bltNameEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        dialog.setTitle(getString(R.string.please_input_hotspot_name))
                .setPositiveButton(getString(R.string.confirm), new InputDialog.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(String editext) {
                        String inputText = bltNameEt.getText().toString().trim();
                        if (!TextUtils.isEmpty(inputText)) {
                            hotspotName.setText(StringUtils.subString(MAX_HOTSPOT_NAME_LENGHT, inputText));
                            sp.edit().putString(WIFI_AP_NAME, inputText).apply();
                            XMToast.toastSuccess(getContext(), R.string.edit_success, false);
                            String savedPsw = sp.getString(WIFI_AP_PSW, null);
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new InputDialog.OnCancelClickListener() {
                    @Override
                    public void onCancelClick() {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            showOpenWifiAPDialog();
        }
        dismissDialogByTime();
        changeView(isChecked);
        setWifiAPEnable(isChecked);
        String status = isChecked ? "打开" : "关闭";
        XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.hotSpotSetting, status,
                "WifiHotSpotFragment", EventConstants.PageDescribe.wifiHotSpotFragmentPagePathDesc);
    }

    private void dismissDialogByTime() {
        countDownTimer = new CountDownTimer(2 * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (wifiAPOpenDialog != null && wifiAPOpenDialog.isShowing()) {
                    wifiAPOpenDialog.dismiss();
                }
            }
        };
        countDownTimer.start();
    }

    private void showOpenWifiAPDialog() {
        wifiAPOpenDialog = new ProgressDialog(getContext());
        wifiAPOpenDialog.show();
        wifiAPOpenDialog.setMessage(getResources().getString(R.string.open_wifi_ap));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null)
            countDownTimer.onFinish();
    }

}
