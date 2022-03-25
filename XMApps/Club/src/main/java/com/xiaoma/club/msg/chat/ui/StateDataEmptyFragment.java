package com.xiaoma.club.msg.chat.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.xiaoma.club.R;

/**
 * Created by LKF on 2019-2-13 0013.
 */
public class StateDataEmptyFragment extends BaseStateFragment {
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivState.setImageResource(R.drawable.state_empty_large);
        tvStateDesc.setText(R.string.state_desc_empty_data);
    }
}
