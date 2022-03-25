package com.xiaoma.setting.sdk.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.fsl.android.tbox.bean.TBoxEndPointInfo;
import com.fsl.android.tbox.bean.TBoxHotSpot;
import com.fsl.android.tbox.bean.TBoxWiFiConnStatus;
import com.fsl.android.tbox.bean.TBoxWifiInfo;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.setting.R;
import com.xiaoma.setting.common.constant.EventConstants;
import com.xiaoma.setting.common.utils.StringUtils;
import com.xiaoma.setting.common.views.ErrorDialog;
import com.xiaoma.setting.common.views.ProgressDialog;
import com.xiaoma.setting.sdk.adapter.WifiScanResultAdapter;
import com.xiaoma.setting.sdk.manager.WifiStateCallback;
import com.xiaoma.setting.sdk.manager.XmWifiManager;
import com.xiaoma.setting.wifi.model.WifiScanResultBean;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.Work;
import com.xiaoma.ui.dialog.InputDialog;
import com.xiaoma.ui.dialog.XmDialog;
import com.xiaoma.ui.view.VerticalScrollBar;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author ZiXu Huang
 * @Data 2018/10/15
 */
@PageDescComponent(EventConstants.PageDescribe.wifiConnectionFragmentPagePathDesc)
public class WifiConnectionFragment extends BaseFragment implements WifiScanResultAdapter.OnItemClickedListener,
        CompoundButton.OnCheckedChangeListener, WifiStateCallback {

    private CheckBox aSwitch;
    private RecyclerView recyclerView;
    private ErrorDialog errorDialog;
    private final List<WifiScanResultBean> items = new ArrayList<>();
    private WifiScanResultAdapter wifiScanResultAdapter;
    private ProgressDialog connectOrDisconnectedDialog;
    private View wifiUnableBg;
    private View scanDevice;
    private WifiScanResultBean connectBean = null;
    private boolean isWifiApEnable;
    private boolean isFragmentVisible = false;
    private boolean wifiConnectedStatus;
    private ProgressDialog searchDialog;
    public static final int MAX_BT_NAME_LENGTH = 20;
    private XmDialog builder;


    private void showWrongPswDialog(String selectSSID) {
        /*WrongPswDialog wrongPswDialog = new WrongPswDialog(getContext(), selectSSID);
        wrongPswDialog.show();*/
        ErrorDialog wrondPswDailog = new ErrorDialog(getContext());
        wrondPswDailog.show();
        wrondPswDailog.setErrorMsg(String.format(getContext().getResources().getString(R.string.name_wrong_psw), selectSSID));
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
        XmWifiManager.getInstance().registerWifiStateCallBack(this);
        bindView(view);
        initRecyclerView();
        XmWifiManager.getInstance().requestWifiStatus();
        XmWifiManager.getInstance().getWifiCOnnectedState();
    }


    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        wifiScanResultAdapter = new WifiScanResultAdapter(items);
        wifiScanResultAdapter.setOnItemClickedListener(this);
        recyclerView.setAdapter(wifiScanResultAdapter);
        wifiScanResultAdapter.notifyDataSetChanged();
    }

    private void bindView(View view) {
        aSwitch = view.findViewById(R.id.wifi_switch);
        aSwitch.setOnCheckedChangeListener(this);
        recyclerView = view.findViewById(R.id.recycler_view);
        scanDevice = view.findViewById(R.id.scan_devices);
        wifiUnableBg = view.findViewById(R.id.wifi_unable_bg);

        recyclerView.setHasFixedSize(true);
        VerticalScrollBar mScrollBar = view.findViewById(R.id.scroll_bar);
        mScrollBar.setRecyclerView(recyclerView);
       /* TextView test = view.findViewById(R.id.wifi_switch_tv);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XmWifiManager.getInstance().requestWifiStatus();
            }
        });*/
    }

    private void showDialog() {
        try {
            if (isFragmentVisible) {
                searchDialog = new ProgressDialog(getContext());
                searchDialog.show();
                searchDialog.setMessage(getString(R.string.searching_wifi));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isFragmentVisible = !hidden;
    }

    @Override
    public void OnItemClicked(int position) {
        TBoxWifiInfo tBoxWifiInfo = items.get(position).getInfo();
        if (wifiConnectedStatus && TextUtils.equals(connectBean.getInfo().getSsid(), tBoxWifiInfo.getSsid())) {
            showDisconnectedDialog();
            XmCarFactory.getSystemManager().operateConnectedWifi(SDKConstants.WifiOperation.DISCONNECT, tBoxWifiInfo.getSsid());
        } else {
            TBoxHotSpot tBoxHotSpot = new TBoxHotSpot();
            tBoxHotSpot.setEncryption(tBoxWifiInfo.getWifiEncryption());
            tBoxHotSpot.setSsid(tBoxWifiInfo.getSsid());
            if (tBoxWifiInfo.getWifiEncryption() == SDKConstants.WifiEncryption_NO) {
                connHotSpot(tBoxHotSpot, "");
            } else {
                showInputWifiPswDialog(tBoxHotSpot);
            }
        }
    }

    private void connHotSpot(TBoxHotSpot tBoxHotSpot, String s) {
        tBoxHotSpot.setPassword(s);
        XmCarFactory.getSystemManager().connectWifi(SDKConstants.WifiOperation.CONNECT, SDKConstants.WifiSavedState.SAVED, tBoxHotSpot);
        showConnectDialog();
    }

    private void showDisconnectedDialog() {
        showDialog(getString(R.string.disconnecting));
    }

    private void showConnectDialog() {
        showDialog(getString(R.string.is_connecting));
    }

    private void showDialog(String text) {
        if (connectOrDisconnectedDialog == null) {
            connectOrDisconnectedDialog = new ProgressDialog(getContext());
        }
        connectOrDisconnectedDialog.show();
        connectOrDisconnectedDialog.setMessage(text);
    }


    private void showInputWifiPswDialog(final TBoxHotSpot tBoxHotSpot) {
        //输入密码
        final InputDialog dialog = new InputDialog(getActivity());
        final EditText bltNameEt = dialog.getEditText();
//        bltNameEt.setKeyListener(DigitsKeyListener.getInstance("0123456789abcdefghijklmnopqrstuvwxyz\n" +
//                "ABCDEFGHIJKLMNOPQRSTUVWXYZ`¬!\"£$%^*()~=#{}[];':,./?/*-_+&#060;&#062;&#064;&#038;，。！·"));
//        bltNameEt.setFilters(new InputFilter[]{new NoChinecFilter()});
        InputFilter[] ifs = {new InputFilter.LengthFilter(16), DigitsKeyListener.getInstance("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.@~-,:*?!_#/=+﹉&^;%…$()\\..<>|·¥[]\"\"{}–'€")};
        bltNameEt.setFilters(ifs);
        bltNameEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//        bltNameEt.setKeyListener();
        String ssid = tBoxHotSpot.getSsid();
        String s = StringUtils.subString(4, ssid);
        dialog.setTitle(String.format(getString(R.string.please_input_psw), s))
                .setPositiveButton(getString(R.string.confirm), new InputDialog.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(String editext) {
                        String inputText = bltNameEt.getText().toString().trim();
                        if (!TextUtils.isEmpty(inputText) && inputText.length() >= 6) {
                            connHotSpot(tBoxHotSpot, inputText);
                        }
                        dialog.dismiss();
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
        aSwitch.setOnCheckedChangeListener(null);
        aSwitch.setChecked(!isChecked);
        aSwitch.setOnCheckedChangeListener(this);
//        changeBackground(isChecked);
        XmWifiManager.getInstance().setWifiEnable(isChecked);
        if (isChecked) {
            showDialog();
        } else {
            if (searchDialog != null && searchDialog.isShowing()) {
                searchDialog.dismiss();
            }
        }
        /*if (isChecked) {
            showDialog();
            XmWifiManager.getInstance().startScan();
        } else {
            items.clear();
            wifiScanResultAdapter.notifyDataSetChanged();
        }*/
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
        if (connectOrDisconnectedDialog != null && connectOrDisconnectedDialog.isShowing()) {
            connectOrDisconnectedDialog.dismiss();
        }
    }

    @Override
    public void onWifiEnable(boolean isEnable) {
        if (builder != null && builder.getDialog() != null && builder.getDialog().isShowing()) {
            builder.dismiss();
        }
        if (isEnable) {
            XmWifiManager.getInstance().startScan();
        } else {
            items.clear();
            wifiScanResultAdapter.notifyDataSetChanged();
        }
        changeBackground(isEnable);
        aSwitch.setOnCheckedChangeListener(null);
        aSwitch.setChecked(isEnable);
        aSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onWifiApEnable(boolean isWifiApEnable) {
        this.isWifiApEnable = isWifiApEnable;
    }

    @Override
    public void onWifiListChanged(final List<WifiScanResultBean> tBoxWifiInfo) {
        SeriesAsyncWorker.create().next(new Work(Priority.HIGH) {
            @Override
            public void doWork(Object lastResult) {
                if (isDestroy())
                    return;
                doNext(sortList(signList(tBoxWifiInfo)));
            }
        }).next(new Work<List<WifiScanResultBean>>() {
            @Override
            public void doWork(List<WifiScanResultBean> scanResultBeans) {
                if (isDestroy())
                    return;
                items.clear();
                items.addAll(scanResultBeans);
                wifiScanResultAdapter.notifyDataSetChanged();
                try {
                    if (searchDialog != null && searchDialog.isShowing()) {
                        searchDialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 标记状态发生更改的wifi
     */
    private List<WifiScanResultBean> signList(List<WifiScanResultBean> tBoxWifiInfo) {
        if (connectBean != null) {
            for (int i = 0; i < tBoxWifiInfo.size(); i++) {
                if (TextUtils.equals(tBoxWifiInfo.get(i).getInfo().getSsid(), connectBean.getInfo().getSsid())) {
                    tBoxWifiInfo.get(i).setConnected(connectBean.isConnected());
                }
            }
        }
        return tBoxWifiInfo;
    }


    private List<WifiScanResultBean> sortList(List<WifiScanResultBean> tBoxWifiInfos) {
        WifiScanResultBean connectedWifi = null;
        for (int i = 0; i < tBoxWifiInfos.size(); i++) {
            for (int j = 0; j <= i; j++) {
                if (tBoxWifiInfos.get(i).getInfo().getSigStrength() < tBoxWifiInfos.get(j).getInfo().getSigStrength()) {
                    WifiScanResultBean temp = tBoxWifiInfos.get(j);
                    tBoxWifiInfos.set(j, tBoxWifiInfos.get(i));
                    tBoxWifiInfos.set(i, temp);
                }
                if (tBoxWifiInfos.get(i).isConnected()) {
                    connectedWifi = tBoxWifiInfos.get(i);
                }
            }
        }
        if (connectedWifi != null && tBoxWifiInfos.contains(connectedWifi)) { //把已连接上的wifi放置在列表最前
            tBoxWifiInfos.remove(connectedWifi);
            tBoxWifiInfos.add(0, connectedWifi);
        }
        return tBoxWifiInfos;
    }

    @Override
    public void onWifiConnectStatusChange(TBoxWiFiConnStatus tBoxWiFiConnStatus) {
        int status = tBoxWiFiConnStatus.getStatus();
        KLog.d("hzx", "wifi连接状态: " + status);
        if (connectOrDisconnectedDialog != null && connectOrDisconnectedDialog.isShowing()) {
            connectOrDisconnectedDialog.dismiss();
        }
        if (status == SDKConstants.WifiConnectStatus.AUTHENTICATION_FAILED) {
            showWrongPswDialog(tBoxWiFiConnStatus.getSsid());
        } else if (status == SDKConstants.WifiConnectStatus.IP_ACQUISITION_FAILED) {
            showErrorDialog();
        } else if (status == SDKConstants.WifiConnectStatus.CONNECTED) {
            onWifiConnection(true, tBoxWiFiConnStatus);
        } else if (status == SDKConstants.WifiConnectStatus.DISCONNECTED) {
            onWifiConnection(false, tBoxWiFiConnStatus);
        }
    }

    @Override
    public void onHotSpotChange(TBoxHotSpot hotSpot) {

    }

    @Override
    public void onHotSpotConnectedStatusChange(TBoxEndPointInfo[] infos) {

    }

    private void onWifiConnection(boolean connectedStatus, TBoxWiFiConnStatus tBoxWiFiConnStatus) {
        if (tBoxWiFiConnStatus == null) return;
        connectBean = new WifiScanResultBean();
        connectBean.setConnected(connectedStatus);
        TBoxWifiInfo info = new TBoxWifiInfo();
        info.setSsid(tBoxWiFiConnStatus.getSsid());
        connectBean.setInfo(info);
        wifiConnectedStatus = connectedStatus;

        for (WifiScanResultBean bean : items) {
            String ssid = bean.getInfo().getSsid();
            if (TextUtils.equals(ssid, tBoxWiFiConnStatus.getSsid())) {
                bean.setConnected(connectedStatus);
            }
        }
        wifiScanResultAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        XmWifiManager.getInstance().unRegisterWifiStateCallBack(this);
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (searchDialog != null && searchDialog.isShowing()) {
            searchDialog.dismiss();
        }
    }

}
