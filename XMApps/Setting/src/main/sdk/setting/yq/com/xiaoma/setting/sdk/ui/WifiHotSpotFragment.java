package com.xiaoma.setting.sdk.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.fsl.android.tbox.bean.TBoxEndPointInfo;
import com.fsl.android.tbox.bean.TBoxHotSpot;
import com.fsl.android.tbox.bean.TBoxWiFiConnStatus;
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
import com.xiaoma.setting.sdk.manager.WifiStateCallback;
import com.xiaoma.setting.sdk.manager.XmWifiManager;
import com.xiaoma.setting.wifi.model.WifiScanResultBean;
import com.xiaoma.ui.dialog.InputDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;

import java.util.List;

/**
 * @Author ZiXu Huang
 * @Data 2018/10/15
 */
@PageDescComponent(EventConstants.PageDescribe.wifiHotSpotFragmentPagePathDesc)
public class WifiHotSpotFragment extends BaseFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, WifiStateCallback {
    private static final String WIFI_AP_NAME = "wifi_ap_name";
    private static final String WIFI_AP_PSW = "wifi_ap_psw";
    private static int MAX_HOTSPOT_NAME_LENGHT = 15;
    private static int MAX_HOTSPOT_PSW_LENGHT = 20;
    private TextView hotspotName;
    private TextView hotspotPsw;
    private ViewGroup nameParent;
    private ViewGroup pswParent;
    private CheckBox hotspotSwitch;
    private TextView connectedAmount;
    private int connectedNum = 0;
    private View hotspotCloseBg;
    private ProgressDialog wifiAPOpenDialog;
    private CountDownTimer countDownTimer;
    private boolean isEnable;
    private boolean isWifiApEnable;
    private TBoxHotSpot hotspot;
    private String wifiApName;
    private String wifiApPsw;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_wifi_hotspot, container, false);
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        initEvent();
        XmWifiManager.getInstance().requestWifiStatus();
        XmWifiManager.getInstance().registerWifiStateCallBack(this);
    }

    private void initEvent() {
        nameParent.setOnClickListener(this);
        pswParent.setOnClickListener(this);
        hotspotSwitch.setOnCheckedChangeListener(this);
    }

    private void bindView(View view) {
        hotspotName = view.findViewById(R.id.hotspot_name);
//        hotspotName.setText(R.string.default_hotspot_name);
        hotspotPsw = view.findViewById(R.id.hotspot_psw);
//        hotspotPsw.setText(R.string.default_hotspot_psw);
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
            connectedAmount.setText(String.format(getResources().getString(R.string.connected_amount), connectedNum));
            hotspotCloseBg.setVisibility(View.GONE);
        } else {
            pswParent.setVisibility(View.GONE);
            nameParent.setVisibility(View.GONE);
            connectedAmount.setVisibility(View.GONE);
            hotspotCloseBg.setVisibility(View.VISIBLE);
        }
    }


    private void setWifiAPEnable(boolean isChecked) {
        XmWifiManager.getInstance().setWifiApEnable(isChecked);

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
//        bltNameEt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
//        bltNameEt.setKeyListener(DigitsKeyListener.getInstance("0123456789abcdefghijklmnopqrstuvwxyz\n" +
//                "ABCDEFGHIJKLMNOPQRSTUVWXYZ`¬!\"£$%^*()~=#{}[];':,./?/*-_+&#060;&#062;&#064;&#038;"));
//        bltNameEt.setFilters(new InputFilter[]{new NoChinecFilter()});
        bltNameEt.setKeyListener(DigitsKeyListener.getInstance("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.@~-,:*?!_#/=+﹉&^;%…$()\\..<>|·¥[]\"\"{}–'€"));
        bltNameEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        final TextView confirm = dialog.getConfirmView();
        String trim = hotspotPsw.getText().toString().trim();
        if (!TextUtils.isEmpty(trim)) {
            bltNameEt.setText(trim);
        }
        bltNameEt.setSelection(bltNameEt.getText().toString().length());
        bltNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.length() == 0) {
                    confirm.setEnabled(false);
                } else {
                    confirm.setEnabled(true);
                }
            }
        });
        dialog.setTitle(getString(R.string.please_input_hotspot_psw))
                .setPositiveButton(getString(R.string.confirm), new InputDialog.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(String editext) {
                        String inputText = bltNameEt.getText().toString().trim();
                        if (inputText.length() < 8) {
                            showToast(getContext().getResources().getString(R.string.need_length_8));
                        } else {
                            wifiApPsw = inputText;
                            hotspotPsw.setText(StringUtils.subString(MAX_HOTSPOT_PSW_LENGHT, inputText));
                            if (hotspot != null) {
                                setAp(hotspot.getSsid(), inputText);
                            } else {
                                if (!TextUtils.isEmpty(wifiApName)) {
                                    setAp(wifiApName, inputText);
                                }
                            }
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
        confirm.setEnabled(bltNameEt.getText().length() > 0);
        dialog.show();

    }

    private void setAp(String apName, String apPsw) {
        if (!TextUtils.isEmpty(apName) && !TextUtils.isEmpty(apPsw)) {
            int code = XmWifiManager.getInstance().apSetting(apName, apPsw);
            if (code == 0) {
                XMToast.toastSuccess(getContext(), R.string.edit_success, false);
            }
        }
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
        final TextView confirm = dialog.getConfirmView();
        String trim = hotspotName.getText().toString().trim();
        if (!TextUtils.isEmpty(trim)) {
            bltNameEt.setText(trim);
        }
        bltNameEt.setSelection(bltNameEt.getText().toString().length());
        bltNameEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        bltNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                confirm.setEnabled(s.length() > 0);
            }
        });
        dialog.setTitle(getString(R.string.please_input_hotspot_name))
                .setPositiveButton(getString(R.string.confirm), new InputDialog.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(String editext) {
                        String inputText = bltNameEt.getText().toString().trim();
                        if (!TextUtils.isEmpty(inputText)) {
                            hotspotName.setText(StringUtils.subString(MAX_HOTSPOT_NAME_LENGHT, inputText));
                            wifiApName = inputText;
                            if (hotspot != null) {
                                setAp(inputText, hotspot.getPassword());
                            } else {
                                if (!TextUtils.isEmpty(wifiApPsw)) {
                                    setAp(inputText, wifiApPsw);
                                }
                            }
                            dialog.dismiss();
                        } else {
                            showToast(R.string.wifi_ap_name_void);
                        }

                    }
                })
                .setNegativeButton(getString(R.string.cancel), new InputDialog.OnCancelClickListener() {
                    @Override
                    public void onCancelClick() {
                        dialog.dismiss();
                    }
                });
        confirm.setEnabled(bltNameEt.getText().length() > 0);
        dialog.show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        hotspotSwitch.setOnCheckedChangeListener(null);
        hotspotSwitch.setChecked(!isChecked);
        hotspotSwitch.setOnCheckedChangeListener(WifiHotSpotFragment.this);
        if (isChecked) {
            showOpenWifiAPDialog();
        }
//        dismissDialogByTime();
//        changeView(isChecked);
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
        XmWifiManager.getInstance().unRegisterWifiStateCallBack(this);
        super.onDestroy();
        if (countDownTimer != null)
            countDownTimer.onFinish();
    }

    @Override
    public void onWifiEnable(boolean isEnable) {
        this.isEnable = isEnable;
    }

    @Override
    public void onWifiApEnable(boolean isWifiApEnable) {
        this.isWifiApEnable = isWifiApEnable;
        if (wifiAPOpenDialog != null && wifiAPOpenDialog.isShowing()) {
            wifiAPOpenDialog.dismiss();
        }
        changeView(isWifiApEnable);
        hotspotSwitch.setOnCheckedChangeListener(null);
        hotspotSwitch.setChecked(isWifiApEnable);
        hotspotSwitch.setOnCheckedChangeListener(this);
        if (isWifiApEnable) {
            XmWifiManager.getInstance().requestApInfo();
        }
    }

    @Override
    public void onWifiListChanged(List<WifiScanResultBean> list) {

    }

    @Override
    public void onWifiConnectStatusChange(TBoxWiFiConnStatus status) {

    }

    @Override
    public void onHotSpotChange(TBoxHotSpot hotSpot) {
        this.hotspot = hotSpot;
        hotspotName.setText(hotSpot.getSsid());
        hotspotPsw.setText(hotSpot.getPassword());
        KLog.d("hzx", "收到的wifi信息,name: " + hotSpot.getSsid() + ", psw: " + hotSpot.getPassword());
        if (isWifiApEnable) {
            XmWifiManager.getInstance().apSetting(hotSpot.getSsid(), hotSpot.getPassword());
        }
    }

    @Override
    public void onHotSpotConnectedStatusChange(TBoxEndPointInfo[] infos) {
        KLog.d("hzx", "目前热点连接人数: " + infos.length);
        int connectedSize = infos.length;
        connectedAmount.setText(String.format(getResources().getString(R.string.connected_amount), connectedSize));
    }
}
