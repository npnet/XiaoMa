package com.xiaoma.club.personal.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.club.R;
import com.xiaoma.club.common.ui.SlideInFragment;
import com.xiaoma.club.common.util.ClubNetWorkUtils;
import com.xiaoma.club.common.util.UserUtil;
import com.xiaoma.club.common.view.UserHeadView;
import com.xiaoma.club.common.viewmodel.MainViewModel;
import com.xiaoma.model.User;

/**
 * Author: loren
 * Date: 2018/10/11 0011
 */

public class PersonalDrawerFragment extends SlideInFragment implements View.OnClickListener {
    private MainViewModel mViewModel;
    private User mUser;
    private UserHeadView userHeadView;
    private TextView userName, userAge, usersign;

    @Override
    protected int getSlideViewId() {
        return R.id.panel_parent;
    }

    @Override
    protected View onCreateWrapView(View childView) {
        return LayoutInflater.from(childView.getContext()).inflate(R.layout.fmt_personal_drawer, (ViewGroup) childView, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initVM();
    }

    private void initView(View view) {
        userHeadView = view.findViewById(R.id.personal_head_view);
        userHeadView.setOnClickListener(this);
        view.findViewById(R.id.personal_edit_sign_btn).setOnClickListener(this);
        view.findViewById(R.id.personal_outside).setOnClickListener(this);
        userName = view.findViewById(R.id.personal_user_name_tv);
        userAge = view.findViewById(R.id.personal_user_age_tv);
        usersign = view.findViewById(R.id.personal_user_sign_tv);
    }

    private void initVM() {
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    mUser = user;
                    initData();
                }
            }
        });
    }


    private void initData() {
        if (mUser == null) {
            return;
        }
        userHeadView.setUserHeadMsg(mUser);
        userName.setText(mUser.getName());
        userAge.setText(mUser.getAge() + getString(R.string.personal_sui));
        usersign.setText(TextUtils.isEmpty(mUser.getPersonalSignature()) ? getString(R.string.user_sign_empty) : mUser.getPersonalSignature());
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        if (mViewModel != null) {
            //先获取缓存
            mViewModel.getUser().setValue(UserUtil.getCurrentUser());
            //再判断网络，有网就去接口请求并刷新
            if (!ClubNetWorkUtils.isConnected(getActivity())) {
                return;
            }
            mViewModel.requestCurrentUser(UserUtil.getCurrentUser().getHxAccount());
        }
    }

    @Override
    public void onClick(View v) {
        Callback callback = mCallback;
        if (callback != null) {
            switch (v.getId()) {
                case R.id.personal_head_view:
                    callback.onHeadImageClick();
                    break;
                case R.id.personal_edit_sign_btn:
                    if (mUser == null) {
                        showToast(getString(R.string.user_msg_error));
                        return;
                    }
                    callback.onEditSignClick(mUser.getPersonalSignature());
                    break;
                case R.id.personal_outside:
                    callback.onOutSideClick();
                    break;
            }
        }
    }

    private PersonalDrawerFragment.Callback mCallback;

    public void setCallback(PersonalDrawerFragment.Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onHeadImageClick();

        void onEditSignClick(String oldSign);

        void onOutSideClick();
    }
}
