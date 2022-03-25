package com.xiaoma.music.player.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.music.R;
import com.xiaoma.music.common.constant.EventConstants;

/**
 * Created by LKF on 2018-12-21 0021.
 */
@PageDescComponent(EventConstants.PageDescribe.emptyThumbPlayerFragment)
public class EmptyThumbPlayerFragment extends BaseFragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_thumb_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.fragment_thumb_fl_cover).setOnClickListener(this);
        view.findViewById(R.id.fragment_thumb_tv_music_info).setOnClickListener(this);
        ImageView mPlayStatusIv = view.findViewById(R.id.fragment_thumb_iv_status);
        mPlayStatusIv.setVisibility(View.INVISIBLE);
    }

    @NormalOnClick({EventConstants.NormalClick.emptyMusic, EventConstants.NormalClick.emptyMusic})
    @ResId({R.id.fragment_thumb_fl_cover, R.id.fragment_thumb_tv_music_info})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_thumb_fl_cover:
            case R.id.fragment_thumb_tv_music_info:
                showToast(mContext.getString(R.string.have_not_music));
                break;
        }
    }
}
