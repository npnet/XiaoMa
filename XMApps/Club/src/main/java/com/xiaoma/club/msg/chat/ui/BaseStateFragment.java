package com.xiaoma.club.msg.chat.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.club.R;
import com.xiaoma.component.base.BaseFragment;

/**
 * Created by LKF on 2019-2-13 0013.
 */
public class BaseStateFragment extends BaseFragment {
    protected ImageView ivState;
    protected TextView tvStateDesc;
    protected TextView tvStateDesc2;
    protected View stateContainer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fmt_base_state, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivState = view.findViewById(R.id.iv_state);
        tvStateDesc = view.findViewById(R.id.tv_state_desc);
        tvStateDesc2 = view.findViewById(R.id.tv_state_desc_2);
        stateContainer = view.findViewById(R.id.state_container);
    }
}
