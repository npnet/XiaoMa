package com.xiaoma.club.msg.chat.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.club.R;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;

/**
 * Created by LKF on 2019-2-13 0013.
 */
public class StateNoNetworkFragment extends BaseFragment implements View.OnClickListener {
    private TextView mBtnReload;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.include_no_network_view, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtnReload = view.findViewById(R.id.tv_retry);
        mBtnReload.setOnClickListener(this);
    }

    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_retry:
                if (!NetworkUtils.isConnected(v.getContext())) {
                    XMToast.toastException(getContext(), R.string.net_work_error);
                    return;
                }
                if (mCallback != null) {
                    mCallback.onReload();
                }
                break;
        }
    }

    public interface Callback {
        void onReload();
    }
}
