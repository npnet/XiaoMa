package com.xiaoma.setting.bluetooth.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.setting.R;
import com.xiaoma.setting.bluetooth.Factory.BluetoothControllerFactory;
import com.xiaoma.setting.bluetooth.adapter.BoundDevicesAdapter;
import com.xiaoma.setting.bluetooth.btinterface.BluetoothConnectStateInterface;
import com.xiaoma.setting.bluetooth.btinterface.BluetoothStateInterface;
import com.xiaoma.setting.bluetooth.model.BluetoothDeviceStatusModule;
import com.xiaoma.setting.bluetooth.service.BluetoothService;
import com.xiaoma.setting.bluetooth.service.BluetoothServiceManager;
import com.xiaoma.setting.bluetooth.vm.BluetoothVM;
import com.xiaoma.setting.common.constant.EventConstants;
import com.xiaoma.setting.common.utils.StringUtils;
import com.xiaoma.setting.common.views.SwitchAnimation;
import com.xiaoma.ui.dialog.InputDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.VerticalScrollBar;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.tts.EventTtsManager;

import java.util.ArrayList;
import java.util.List;

@PageDescComponent(EventConstants.PageDescribe.blueToothSettingFragmentPagePathDesc)
public class BlueToothSettingFragment extends BaseFragment implements View.OnClickListener,
        BoundDevicesAdapter.OnIconClickedListener,
        BluetoothConnectStateInterface, BluetoothStateInterface, SwitchAnimation.SwitchListener, ServiceConnection, XmCarVendorExtensionManager.ValueChangeListener {

    private TextView bltNameTv;
    private SwitchAnimation aSwitch;
    private View editView;
    private BluetoothAdapter bltAdapter;
    private View bltDevices;
    private RelativeLayout disableBltBg;
    private BluetoothVM bluetoothVM;
    private BluetoothService service;
    private TextView bltStates;
    private Button addDevices;
    private RecyclerView bondDevices;
    private List<BluetoothDeviceStatusModule> items = new ArrayList<>();
    private BoundDevicesAdapter boundDevicesAdapter;
    private CountDownTimer countDownTimer;
    private boolean isCountdown;
    private BluetoothControllerAgent bluetoothController;
    private DialogDispatch dialogDispatch;
    private static int MAX_BT_NAME_LENGTH = 15;
    private boolean shouldShow = false;

    private Handler handler = new Handler();
    private BluetoothDevice needConnectedDevice;
    private boolean needConnectBt;
    private boolean connectSuccessTTS;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bluetooth, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bltAdapter = BluetoothServiceManager.getInstance().getBluetoothAdapter();
        bindView(view);
        initRecyclerView();
        initVM();
        BluetoothServiceManager.getInstance().addServiceConnection(this);
        dialogDispatch = new DialogDispatch(getContext());
        initService();
        XmCarVendorExtensionManager.getInstance().addValueChangeListener(this);
    }

    private void initService() {
       /* Intent intent = new Intent("com.nforetek.bt.START_UI_SERVICE");
        intent.setPackage("com.nforetek.bt.demo.service");
        getActivity().bindService(intent, btConnectService, BIND_AUTO_CREATE);*/
    }

    private void initVM() {
        bluetoothVM = ViewModelProviders.of(this).get(BluetoothVM.class);
        bluetoothVM.getMatchedDevices().observe(this, new Observer<List<BluetoothDeviceStatusModule>>() {
            @Override
            public void onChanged(@Nullable List<BluetoothDeviceStatusModule> bluetoothDevices) {
                items.clear();
                items.addAll(bluetoothDevices);
                boundDevicesAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initRecyclerView() {
        bondDevices.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        boundDevicesAdapter = new BoundDevicesAdapter(getContext(), items);
        boundDevicesAdapter.setOnIconClickedListener(this);
        bondDevices.setAdapter(boundDevicesAdapter);
    }


    private void bindView(View view) {
        bltDevices = view.findViewById(R.id.blt_devices);
        editView = view.findViewById(R.id.edit_blt_name_parent);
        editView.setOnClickListener(this);
        bltNameTv = view.findViewById(R.id.bluetooth_name);
        aSwitch = view.findViewById(R.id.blt_switch);
        disableBltBg = view.findViewById(R.id.blt_disable_bg);
        boolean enabled = bltAdapter.isEnabled();
        if (!enabled) {
            shouldShow = true;
        }
        bluetoothViewSetup(enabled);

        aSwitch.check(enabled);
        aSwitch.setListener(this);
        disableBltBg.setVisibility(enabled ? View.GONE : View.VISIBLE);
        bltDevices.setVisibility(enabled ? View.VISIBLE : View.GONE);

        String savedBltName = bltAdapter.getName();
        if (!TextUtils.isEmpty(savedBltName)) {
            bltNameTv.setText(StringUtils.subString(MAX_BT_NAME_LENGTH, savedBltName));
            KLog.d("hzx", "蓝牙名称: " + savedBltName);
        } else {
            KLog.d("hzx", "蓝牙名为空");
        }

        bltStates = view.findViewById(R.id.bluetooth_status);
        addDevices = view.findViewById(R.id.add_devices);
        bondDevices = view.findViewById(R.id.bond_devices);
        addDevices.setOnClickListener(this);


        VerticalScrollBar mScrollBar = view.findViewById(R.id.scroll_bar);
        mScrollBar.setRecyclerView(bondDevices);
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.editBltName})
    @ResId({R.id.edit_blt_name_parent})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_devices:
                addDevices();
                break;
            case R.id.edit_blt_name_parent:
                editBltName();
                break;
        }
    }

    private void addDevices() {
        if (isCountdown) {
            cancelMatch();
        } else {
            canBeDiscovered();
        }
//        bluetoothVM.refreshDevicesStatus();
    }

    private void canBeDiscovered() {
        if (bluetoothController != null) {
            bluetoothController.canBeDiscovered();
            countDown();
            bltStates.setText(R.string.matching_check_phone);
            isCountdown = true;
        }
    }

    private void cancelMatch() {
        if (countDownTimer != null)
            countDownTimer.cancel();
        addDevices.setText(R.string.add_device);
        bltStates.setText(R.string.click_and_add_device);
        closeDiscoverable();
        isCountdown = false;
    }

    private void countDown() {
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(60 * 1000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    addDevices.setText(String.format(getResources().getString(R.string.time_remain), millisUntilFinished / 1000));
                }

                @Override
                public void onFinish() {
                    isCountdown = false;
                    addDevices.setText(R.string.add_device);
                    bltStates.setText(R.string.click_and_add_device);
                    closeDiscoverable();
                }
            };
        }
        countDownTimer.start();
    }

    private void closeDiscoverable() {
        bluetoothController.unableDiscovered();
    }

    private void editBltName() {
        showEditNameDialog();
    }

    private void showEditNameDialog() {
        final InputDialog dialog = new InputDialog(getActivity());
        final EditText bltNameEt = dialog.getEditText();
        bltNameEt.setText(bltAdapter.getName());
        bltNameEt.setSelection(bltAdapter.getName().length());
        final View confirm = dialog.getConfirmView();
        bltNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString().trim();
                confirm.setEnabled(true);
                if (text.length() > MAX_BT_NAME_LENGTH) {
                    bltNameEt.setText(text.substring(0, MAX_BT_NAME_LENGTH));
                    bltNameEt.setSelection(MAX_BT_NAME_LENGTH);
                } else if (text.length() == 0) {
                    confirm.setEnabled(false);
                }
            }
        });
        dialog.setTitle(getString(R.string.please_input_blt_name))
                .setPositiveButton(getString(R.string.confirm), new InputDialog.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(String editext) {
                        String name = bltNameEt.getText().toString().trim();
                        if (name.length() > 15) {
                            Toast.makeText(getContext(), R.string.max_lenght_15, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        bltNameTv.setText(StringUtils.subString(MAX_BT_NAME_LENGTH, name));
                        bltAdapter.setName(name);
                        dialog.dismiss();
                        showEditSuccessToast();
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

    private void showEditSuccessToast() {
        XMToast.toastSuccess(getContext(), R.string.edit_success, false);
    }

    @Override
    public void onDestroy() {
        KLog.d("hzx", "onDestroy");
        BluetoothServiceManager.getInstance().removeServiceConnection(this, true);
        super.onDestroy();
//        getActivity().getApplication().unbindService(serviceConnection);
    }

    private void openBluetooth() {
        if (shouldShow) {
            dialogDispatch.showOpenBtDialog();
        }
        shouldShow = true;
        bltAdapter.enable();
    }

   /* @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        aSwitch.setClickable(false);
        if (isChecked) {
            openBluetooth();
        } else {
            bltAdapter.disable();
            bltDevices.setVisibility(View.INVISIBLE);
            cancelMatch();
        }
        disableBltBg.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        String status = isChecked ? "打开" : "关闭";
        XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.bluetoothSetting, status,
                "BlueToothSettingFragment", EventConstants.PageDescribe.blueToothSettingFragmentPagePathDesc);
    }*/

    @Override
    public void onDeleteClicked(int position) {
        if (items.get(position).getDeviceStatus() == BoundDevicesAdapter.CONNECTED) {
            bluetoothController.disconnect(items.get(position).getDevice());
        }
        if (bluetoothController.removeBond(items.get(position).getDevice())) {
            items.remove(position);
            boundDevicesAdapter.notifyDataSetChanged();
//        dialogDispatch.showAutoDismissDialog(R.string.have_been_deleted, R.drawable.icon_success);
            XMToast.toastSuccess(getContext(), R.string.have_been_deleted, false);
        } else {
            XMToast.toastSuccess(getContext(), R.string.delete_failed, false);
        }
    }

    @Override
    public void onConnectClicked(final int position) {
        int deviceStatus = items.get(position).getDeviceStatus();
        needConnectedDevice = items.get(position).getDevice();
        Log.d("hzx", "device name: " + items.get(position).getDevice().getName() + " , status:" + deviceStatus);
        if (deviceStatus == BoundDevicesAdapter.NOT_CONNECTED) {
            // 传说此处不用处理断开其他设备,只需要去连接所需设备,底层会自动把之前连接断开
            bluetoothController.connect(needConnectedDevice);
            dialogDispatch.showConnectOrDisconnectDialog(R.string.connect_blt);
            /*KLog.d("hzx", "断开其他链接蓝牙");
            bluetoothController.disconnectOtherDevice(needConnectedDevice);
            if (isBtConnectDevice()) {
                needConnectBt = true;
            } else {
                dialogDispatch.showConnectOrDisconnectDialog(R.string.connect_blt);
                bluetoothController.connect(needConnectedDevice);
            }*/
           /* handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    KLog.d("hzx", "断开完其他链接蓝牙后,链接蓝牙: " + device.getName());
                    dialogDispatch.showConnectOrDisconnectDialog(R.string.connect_blt);
                    bluetoothController.connect(device);
                }
            }, 2000);*/

        } else {
            dialogDispatch.showConnectOrDisconnectDialog(R.string.disconnect_blt);
            bluetoothController.disconnect(items.get(position).getDevice());
        }
    }

    private boolean isBtConnectDevice() {
        if (items == null || items.isEmpty()) {
            return false;
        }
        for (BluetoothDeviceStatusModule module : items) {
            if (module.getDeviceStatus() == BoundDevicesAdapter.CONNECTED) {
                return true;
            }
        }
        return false;
    }

    private void refreshDevices(final BluetoothDevice device, final int connectedStatus) {
        List<BluetoothDeviceStatusModule> connectedList = new ArrayList<>();
        List<BluetoothDeviceStatusModule> disconnectList = new ArrayList<>();
        items.clear();
        for (BluetoothDeviceStatusModule module : items) {
            if (TextUtils.equals(device.getAddress(), module.getDevice().getAddress())) {
                module.setDeviceStatus(connectedStatus);
            }
            if (module.getDeviceStatus() == BoundDevicesAdapter.CONNECTED) {
                connectedList.add(module);
                items.add(0, module);
            } else {
                disconnectList.add(module);
                items.add(module);
            }
        }
        /*items.clear();
        boundDevicesAdapter.notifyDataSetChanged();
        items.addAll(connectedList);
        items.addAll(disconnectList);*/
        boundDevicesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBtConnected(BluetoothDevice device, boolean isShowDialog) {
//        bluetoothController.filterConnection(device);
        if (connectSuccessTTS) {
            connectSuccessTTS = false;
            EventTtsManager.getInstance().startSpeaking(mContext.getString(R.string.blt_connect_success));
        }
        dialogDispatch.dismissConnectOrDisconnectDialog();
        refreshDevices(device, BoundDevicesAdapter.CONNECTED);
        bluetoothVM.refreshDevicesStatus();
    }

    @Override
    public void onBtDisconnected(BluetoothDevice device, boolean isShowDialog) {
        dialogDispatch.dismissConnectOrDisconnectDialog();
        refreshDevices(device, BoundDevicesAdapter.NOT_CONNECTED);
        bluetoothVM.refreshDevicesStatus();
        /*if (bluetoothController.isDeviceDisconnected(device)) {
            if (needConnectBt && needConnectedDevice != null) {
                KLog.d("hzx", "断开其他蓝牙后,连接蓝牙: " + needConnectedDevice.getName());
                dialogDispatch.showConnectOrDisconnectDialog(R.string.connect_blt);
                bluetoothController.connect(needConnectedDevice);
                needConnectBt = false;
            }
        }*/
    }

    @Override
    public void onDeleteBondedDevice() {
//        dialogDispatch.showAutoDismissDialog(R.string.have_been_deleted, R.drawable.icon_success);
    }

    @Override
    public void onPairingFailed() {
//        dialogDispatch.showPairFailedDialog();
        XMToast.toastException(getContext(), R.string.pair_failed, false);
    }

    @Override
    public void onHeadSetClientConnected(BluetoothDevice device) {
//        bluetoothController.filterConnection(device);
    }

    @Override
    public void onBtTurnOnSuccess() {
        disableBltBg.setVisibility(View.GONE);
        dialogDispatch.dismissOpenBtDialog();
        shouldShow = false;
        aSwitch.check(true);
        bltDevices.setVisibility(View.VISIBLE);
        if (bluetoothVM != null) {
            bluetoothVM.refreshDevicesStatus();
        }
        bluetoothViewSetup(true);
    }

    private void bluetoothViewSetup(boolean enabled) {
        if (editView == null) {
            return;
        }
        if (enabled) {
            editView.setClickable(true);
            bltNameTv.setClickable(true);
            bltNameTv.setTextColor(getResources().getColor(R.color.white));
        } else {
            editView.setClickable(false);
            bltNameTv.setClickable(false);
            bltNameTv.setTextColor(getResources().getColor(R.color.login_bg_gray));
        }
    }

    @Override
    public void onBtClose() {
        /*boolean checked = aSwitch.isCheck();
        if (checked) {
            aSwitch.check(false);
        }*/
        disableBltBg.setVisibility(View.VISIBLE);
        aSwitch.check(false);
        shouldShow = true;
        bluetoothViewSetup(false);
        bltDevices.setVisibility(View.INVISIBLE);
        cancelMatch();
    }

    @Override
    public void onBtTurnOnFailed() {
//        dialogDispatch.showAutoDismissDialog(ErrorDialog.DEFAULT_VALUE, ErrorDialog.DEFAULT_VALUE);
        XMToast.toastException(getContext(), R.string.error_operate_failed, false);
        bluetoothViewSetup(false);
    }

    @Override
    public void onBtBonded(final BluetoothDevice device) {
        cancelMatch();
        if (bluetoothController.disconnectOtherDevice(device)) {
            needConnectBt = true;
            needConnectedDevice = device;
        }
        bluetoothVM.refreshDevicesStatus();
       /* handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogDispatch.showConnectOrDisconnectDialog(R.string.connect_blt);
                bluetoothController.connect(device);
            }
        },1000);*/
    }

    @Override
    public void onPairingConfirm(int type, BluetoothDevice device, String pairingKey) {
        bluetoothController.onPair(type, device, pairingKey);
    }

    public void connectBluetooth() {
        connectSuccessTTS = true;
        EventTtsManager.getInstance().startSpeaking(StringUtil.format(mContext.getString(R.string.please_open_bluetooth), bltAdapter.getName()));
        BluetoothAdapter.getDefaultAdapter().enable();
        addDevices.performClick();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            aSwitch.check(bltAdapter.isEnabled());
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            aSwitch.check(bltAdapter.isEnabled());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        shouldShow = true;
        aSwitch.check(bltAdapter.isEnabled());
        String savedBltName = bltAdapter.getName();
        if (!TextUtils.isEmpty(savedBltName)) {
            bltNameTv.setText(StringUtils.subString(MAX_BT_NAME_LENGTH, savedBltName));
            KLog.d("hzx", "onResume, 蓝牙名称: " + savedBltName);
        } else {
            KLog.d("hzx", "onResume, 蓝牙名为空");
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        shouldShow = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        getActivity().getApplication().unbindService(serviceConnection);
    }

    @Override
    public void onSwitch(int viewId, boolean state) {
        if (state) {
            openBluetooth();
        } else {
            bltAdapter.disable();
        }
        String status = state ? "打开" : "关闭";
        XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.bluetoothSetting, status,
                "BlueToothSettingFragment", EventConstants.PageDescribe.blueToothSettingFragmentPagePathDesc);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        BlueToothSettingFragment.this.service = ((BluetoothService.BluetoothBinder) iBinder).getService();
        BlueToothSettingFragment.this.service.setOnBtConnectStateCallback(BlueToothSettingFragment.this);
        BlueToothSettingFragment.this.service.setOnBtStateCallback(BlueToothSettingFragment.this);
        BlueToothSettingFragment.this.service.setBtPairInterface(BluetoothControllerFactory.getInstance().getBluetoothPairController());
        BlueToothSettingFragment.this.bluetoothController = BlueToothSettingFragment.this.service.getBluetoothController();

        if (bluetoothVM != null) {
            bluetoothVM.refreshDevicesStatus(BlueToothSettingFragment.this.bluetoothController);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        BlueToothSettingFragment.this.service = null;
    }

    @Override
    public void onChange(int id, Object value) {
        if (id == SDKConstants.ID_WORK_MODE_STATUS) {
            Log.d("hzx","acc 信号： " + value);
            int result = (int) value;
            if (result == SDKConstants.WorkMode.STANDBY) {
                // Acc off时关闭可发现蓝牙
                cancelMatch();
            }
        }
    }
}
