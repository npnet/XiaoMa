package com.xiaoma.bluetooth.phone.history.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.bluetooth.phone.R;
import com.xiaoma.bluetooth.phone.common.constants.EventBusTags;
import com.xiaoma.bluetooth.phone.common.constants.EventConstants;
import com.xiaoma.bluetooth.phone.common.sdk.BluetoothPhoneNforeAndOriginHybridSdk;
import com.xiaoma.bluetooth.phone.common.utils.BluetoothAdapterUtils;
import com.xiaoma.bluetooth.phone.common.utils.BluetoothUtils;
import com.xiaoma.bluetooth.phone.common.utils.OperateUtils;
import com.xiaoma.bluetooth.phone.common.views.VerticalScrollBar;
import com.xiaoma.bluetooth.phone.history.adapter.ContactHistoryAdapter;
import com.xiaoma.bluetooth.phone.main.ui.BaseBluetoothFragment;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.utils.GsonHelper;

import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.xiaoma.bluetooth.phone.common.constants.EventConstants.PageDescribe.callLogFragmentPagePathDesc;

/**
 * @author: iSun
 * @date: 2018/11/16 0016
 */
@PageDescComponent(callLogFragmentPagePathDesc)
public class HistoryFragment extends BaseBluetoothFragment implements ContactHistoryAdapter.ItemClickedListener, View.OnClickListener {

    public static final String SETTINGS_PACKAGE_NAME = "com.xiaoma.setting";
    public static final String SETTINGS_PACKAGE_NAME_ENTRANCE = "com.xiaoma.setting.main.ui.MainActivity";
    private String TAG = "HistoryFragment";
    private RecyclerView recyclerView;
    private List<ContactBean> items = new ArrayList<>();
    private ContactHistoryAdapter contactHistoryAdapter;
    private View disconnectBtView;
    private TextView toConnectBluetooth;
    private View noContactView;
    private View content;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
    }

    @Subscriber(tag = EventBusTags.CALL_HISTORY_LIST_REFRESH)
    private void onCallHistoryListRefresh(List<ContactBean> contactBeans) {
        items.clear();
        items.addAll(collectData(contactBeans));
        contactHistoryAdapter.notifyDataSetChanged();
        if (contactBeans == null || contactBeans.size() == 0) {
//            if (BluetoothUtils.isBTConnectDevice()) {
            if (BluetoothPhoneNforeAndOriginHybridSdk.getInstance().isHfpDisconnected()) {
                displayDisconnectLayout(true);
            } else {
                displayNoContactLayout(true);
            }
           /* } else {
                displayDisconnectLayout(true);
            }*/
        } else {
            displayNoContactLayout(false);
            displayDisconnectLayout(false);
        }
        /*--------测试代码----------*/
       /* items.clear();
        items.addAll(collectData(contactBeans));
        contactHistoryAdapter.notifyDataSetChanged();
        displayDisconnectLayout(false);
        displayNoContactLayout(false);*/
    }

    @Subscriber(tag = EventBusTags.PULL_STATE)
    private void pullState(boolean pullState) {
       /* noContactView.setVisibility(pullState ? View.GONE : View.VISIBLE);
        if (pullState) {
            disconnectBtView.setVisibility(View.GONE);
        } else {
            items.clear();
            contactHistoryAdapter.notifyDataSetChanged();
            noContactView.setVisibility(View.VISIBLE);
        }*/
        BluetoothAdapter bluetoothAdapter = BluetoothAdapterUtils.getBluetoothAdapter();
        int profileConnectionState = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET_CLIENT);
        if (profileConnectionState == BluetoothAdapter.STATE_CONNECTED) {
            items.clear();
            contactHistoryAdapter.notifyDataSetChanged();
            if (!pullState) {
                noContactView.setVisibility(View.VISIBLE);
                disconnectBtView.setVisibility(View.GONE);
            }
        } else {
            disconnectBtView.setVisibility(View.VISIBLE);
            noContactView.setVisibility(View.GONE);
        }
    }

    private List<ContactBean> collectData(List<ContactBean> items) {
        List<ContactBean> contactBeans = new ArrayList<>(items);
        if (contactBeans.size() == 0) {
            return contactBeans;
        }
        Calendar instance = Calendar.getInstance();
        String curYear = instance.get(Calendar.YEAR) + "";
        for (ContactBean contactBean : contactBeans) {
            String temp = contactBean.getDate().substring(0, 4);
            String tempData = contactBean.getDate().substring(5);
            if (TextUtils.equals(curYear, temp)) {
                contactBean.setDate(tempData);
            }
        }
        return contactBeans;
    }

    @Override
    public void onBlueToothDisabled() {
        displayDisconnectLayout(true);
    }

    public void bindView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        content = view.findViewById(R.id.content);
        VerticalScrollBar mScrollBar = view.findViewById(R.id.scroll_bar);
        mScrollBar.setRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));

        contactHistoryAdapter = new ContactHistoryAdapter(getContext(), items, R.layout.item_history_contact);
        contactHistoryAdapter.setOnItemClickedListener(this);
        recyclerView.setAdapter(contactHistoryAdapter);
        noContactView = view.findViewById(R.id.no_contact_layout);
        disconnectBtView = view.findViewById(R.id.layout_disconnect_bluetooth);
        toConnectBluetooth = disconnectBtView.findViewById(R.id.to_connect_bluetooth);
//        noAccessToContactView = view.findViewById(R.id.no_access_to_contact_layout);
        toConnectBluetooth.setOnClickListener(this);

        if (!BluetoothUtils.isBTConnectDevice()) {
            displayDisconnectLayout(true);
        }
    }

    @Override
    public void onItemClickedListener(ContactBean item) {
        OperateUtils.dial(item.getPhoneNum());
        XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.call, GsonHelper.toJson(item), TAG, callLogFragmentPagePathDesc);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void displayDisconnectLayout(boolean show) {
        if (show) {
            noContactView.setVisibility(View.GONE);
//            noAccessToContactView.setVisibility(View.GONE);
        }
        disconnectBtView.setVisibility(show ? View.VISIBLE : View.GONE);
        content.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void displayNoContactLayout(boolean show) {
        if (show) {
            disconnectBtView.setVisibility(View.GONE);
//            noAccessToContactView.setVisibility(View.GONE);
        }
        noContactView.setVisibility(show ? View.VISIBLE : View.GONE);
        content.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    /*private void displayNoAccessToContactLayout(boolean show) {
        if (show) {
            disconnectBtView.setVisibility(View.GONE);
            noContactView.setVisibility(View.GONE);
        }
        noAccessToContactView.setVisibility(show ? View.VISIBLE : View.GONE);
        content.setVisibility(show ? View.GONE : View.VISIBLE);
    }*/

    @Override
    public void onBlueToothDisConnected(BluetoothDevice device) {
        super.onBlueToothDisConnected(device);
//        displayDisconnectLayout(true);
    }

    @Override
    public void onBlueToothConnected() {
        super.onBlueToothConnected();
//        displayDisconnectLayout(false);
    }

    @Override
    public void onPbapConnected() {
        super.onPbapConnected();
//        displayNoAccessToContactLayout(false);
    }

    @Override
    public void onHfpConnected(BluetoothDevice device) {
        super.onHfpConnected(device);
//        if (isDeviceConnected(device)) {
            displayDisconnectLayout(false);
//        }
    }

    @Override
    public void onHfpDisConnected(BluetoothDevice device) {
        super.onHfpDisConnected(device);
//        if (isDeviceDisconnected(device)) {
        cleanData();
        displayDisconnectLayout(true);
//        }
    }

    @Override
    public void onA2dpConnected(BluetoothDevice device) {
        super.onA2dpConnected(device);
       /* if (isDeviceConnected(device)) {
            displayDisconnectLayout(false);
//            displayNoContactLayout(true);
        }*/
    }

    @Override
    public void onA2dpDisconnected(BluetoothDevice device) {
        super.onA2dpDisconnected(device);
       /* cleanData();
        if (isDeviceDisconnected(device)) {
            displayDisconnectLayout(true);
        }*/
    }

    private void cleanData() {
        items.clear();
        contactHistoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPbapDisconnected() {
        super.onPbapDisconnected();
        /*if (BluetoothUtils.isBTConnectDevice()) {
            displayNoAccessToContactLayout(true);
        } else {
            displayDisconnectLayout(true);
        }*/
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.toConnectBluetooth})
    @ResId({R.id.to_connect_bluetooth})
    public void onClick(View v) {
        if (v == toConnectBluetooth) {
            connectBluetooth();
        }
    }

    private void launchApp(String packageName, String className, String hintText) {
        PackageInfo packageinfo;
        try {
            packageinfo = mContext.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            showToast(hintText);
            return;
        }
        if (packageinfo == null) {
            showToast(hintText);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName(packageName, className);
        intent.setComponent(cn);
        startActivity(intent);
    }

    @Override
    public boolean needPhoneStateCallback() {
        return false;
    }

}
