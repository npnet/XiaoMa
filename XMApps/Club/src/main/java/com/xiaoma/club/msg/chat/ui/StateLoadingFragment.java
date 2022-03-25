package com.xiaoma.club.msg.chat.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.xiaoma.club.R;

/**
 * Created by LKF on 2019-2-13 0013.
 */
public class StateLoadingFragment extends BaseStateFragment {
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvStateDesc.setVisibility(View.GONE);
        ivState.setImageResource(R.drawable.state_loading_large);
        final Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        anim.setRepeatMode(Animation.RESTART);
        anim.setRepeatCount(Animation.INFINITE);
        ivState.startAnimation(anim);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ivState.clearAnimation();
    }
}
