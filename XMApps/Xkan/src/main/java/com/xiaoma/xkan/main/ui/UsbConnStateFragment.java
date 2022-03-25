package com.xiaoma.xkan.main.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.xkan.R;
import com.xiaoma.xkan.common.constant.EventConstants;
import com.xiaoma.xkan.common.constant.XkanConstants;
import com.xiaoma.xkan.common.manager.UsbMediaDataManager;
import com.xiaoma.xkan.common.model.UsbStatus;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

/**
 * Created by Thomas on 2018/11/9 0009
 * usb state view
 */
@PageDescComponent(EventConstants.PageDescribe.USBCONNSTATEFRAGMENTPAGEPATHDESC)
public class UsbConnStateFragment extends BaseFragment {

    private TextView mUsbConnStateTv;
    private ImageView mIvEmpty;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.usb_empty_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        initView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initView(View view) {
        mUsbConnStateTv = view.findViewById(R.id.tv_empty);
        mIvEmpty = view.findViewById(R.id.iv_empty);
        mUsbConnStateTv.setText(R.string.usb_conn_state_remove);
        mIvEmpty.setVisibility(View.INVISIBLE);
        mUsbConnStateTv.setVisibility(View.INVISIBLE);
        setCurrentStateView("");
    }


    private void setCurrentStateView(String msg) {
        UsbStatus currentUsbStatus = UsbMediaDataManager.getInstance().getCurrentUsbStatus();
        if (currentUsbStatus == UsbStatus.MOUNT_ERROR) {
            mUsbConnStateTv.setText(R.string.usb_conn_state_mounted_error);
            if (!TextUtils.isEmpty(msg) && msg.toUpperCase().contains("HUB")) {
                mIvEmpty.setImageResource(R.drawable.img_hub_not_support);
            } else {
                mIvEmpty.setImageResource(R.drawable.img_usb_not_support);
            }
            mIvEmpty.setVisibility(View.VISIBLE);
            mUsbConnStateTv.setVisibility(View.VISIBLE);
        } else if (currentUsbStatus == UsbStatus.REMOVED || currentUsbStatus == UsbStatus.NO_USB_MOUNTED) {
            mUsbConnStateTv.setText(R.string.usb_conn_state_remove);
            mIvEmpty.setImageResource(R.drawable.icon_usb_no_connect);
            mIvEmpty.setVisibility(View.VISIBLE);
            mUsbConnStateTv.setVisibility(View.VISIBLE);
        } else if (currentUsbStatus == UsbStatus.INSERTED || currentUsbStatus == UsbStatus.MOUNTED) {
            mIvEmpty.setVisibility(View.INVISIBLE);
            mUsbConnStateTv.setVisibility(View.INVISIBLE);
        }
    }

    @Subscriber(tag = XkanConstants.REFRESH_USB_FRAG_VIEW)
    public void refreshState(String event) {
        setCurrentStateView(event);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
