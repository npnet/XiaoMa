package com.xiaoma.setting.sdk.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.component.permission.IPermissionCheck;
import com.xiaoma.component.permission.PermissionHelper;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.setting.R;
import com.xiaoma.setting.Setting;
import com.xiaoma.setting.common.constant.EventConstants;
import com.xiaoma.setting.common.views.ErrorDialog;
import com.xiaoma.setting.common.views.ProgressDialog;
import com.xiaoma.setting.common.views.WrongPswDialog;
import com.xiaoma.utils.log.KLog;

/**
 * @Author ZiXu Huang
 * @Data 2018/10/15
 */
@PageDescComponent(EventConstants.PageDescribe.wifiConnectionFragmentPagePathDesc)
public class WifiConnectionFragment extends BaseFragment implements
        CompoundButton.OnCheckedChangeListener {

    private CheckBox aSwitch;
    private RecyclerView recyclerView;
    private ErrorDialog errorDialog;
    private int GPS_REQUEST_CODE = 100;
    private boolean isPermissionGranted;
    private ProgressDialog progressDialog;
    private View wifiUnableBg;
    private View scanDevice;
    private boolean isConnected = false;


    private void showWrongPswDialog(String selectSSID) {
        WrongPswDialog wrongPswDialog = new WrongPswDialog(getContext(), selectSSID);
        wrongPswDialog.show();
    }

    //忘记网络
    private void deleteNetWork() {
    }

    private void startScan() {
        XmCarFactory.getSystemManager().scanWifiList();
    }

    private void showErrorDialog() {
        if (errorDialog == null) {
            errorDialog = new ErrorDialog(getContext());
        }
        errorDialog.show();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_wifi_connection, container, false);
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        initRecyclerView();
        requestPermission();
        dismissProgress();
    }

    private void requestPermission() {
        PermissionHelper permissionHelper = new PermissionHelper(new IPermissionCheck() {
            @Override
            public int getPermissionsRequestCode() {
                return 0;
            }

            @Override
            public String[] getPermissions() {
                return new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
            }

            @Override
            public void requestPermissionsSuccess() {
                isPermissionGranted = true;
            }

            @Override
            public void requestPermissionsFail() {
                isPermissionGranted = false;
            }
        });
        permissionHelper.requestPermissions(this);
    }

    private boolean isGPSOpen() {
        LocationManager locationManager = (LocationManager) Setting.getContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            return gpsEnabled || networkEnabled;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_REQUEST_CODE) {
            aSwitch.setChecked(false);
            if (isGPSOpen()) {
                aSwitch.setChecked(true);
            } else {
                showToast(getContext().getResources().getString(R.string.enable_gps_before_open_wifi));
            }
        }
    }

    private void openGps() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, GPS_REQUEST_CODE);
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
    }

    private void bindView(View view) {
        aSwitch = view.findViewById(R.id.wifi_switch);
        aSwitch.setOnCheckedChangeListener(this);
        recyclerView = view.findViewById(R.id.recycler_view);
        scanDevice = view.findViewById(R.id.scan_devices);
        wifiUnableBg = view.findViewById(R.id.wifi_unable_bg);
    }

    private void showDialog() {
        if (isVisible()) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.show();
            progressDialog.setMessage(getString(R.string.searching_wifi));
        }
    }

    private void showConnectDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
        }
        progressDialog.show();
        progressDialog.setMessage(getString(R.string.is_connecting));
    }


    private int getEncryption(String capabilities) {
        int type;
        if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
            type = 1;
        } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
            type = 2;
        } else {
            type = 0;
        }
        return type;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        changeBackground(isChecked);
        if (!isPermissionGranted) {
            requestPermission();
            return;
        }
        if (isChecked && !isGPSOpen()) {
            openGps();
            return;
        }
        if (isChecked) {
            showDialog();
        } else {
        }
        String status = isChecked ? "打开" : "关闭";
        XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.netWorkSetting, status,
                "WifiConnectionFragment", EventConstants.PageDescribe.wifiConnectionFragmentPagePathDesc);
    }

    private void changeBackground(boolean isChecked) {
        if (isChecked) {
            wifiUnableBg.setVisibility(View.GONE);
            scanDevice.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            wifiUnableBg.setVisibility(View.VISIBLE);
            scanDevice.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissProgress();
    }
}
