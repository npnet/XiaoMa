package com.xiaoma.club.msg.chat.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xiaoma.club.R;
import com.xiaoma.club.common.ui.SlideInFragment;
import com.xiaoma.club.common.view.UserHeadView;
import com.xiaoma.club.msg.chat.vm.FriendDetailVM;
import com.xiaoma.model.User;
import com.xiaoma.network.callback.SimpleCallback;

/**
 * Author: loren
 * Date: 2018/10/16 0017
 */

public class FriendDetailsFragment extends SlideInFragment implements View.OnClickListener {

    private UserHeadView friendHead;
    private TextView friendName, friendAge, friendSign;
    private Button addFriend;
    private FriendDetailVM detailsVM;
    private static final String KEY_HXID = "key_hxid";
    private static final String KEY_IS_FROM_GROUP_CHAT = "key_is_group_chat";
    private String hxId;

    public static FriendDetailsFragment newInstance(String hxId, Callback callback) {
        FriendDetailsFragment fragment = new FriendDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_HXID, hxId);
        fragment.setArguments(bundle);
        fragment.setCallback(callback);
        return fragment;
    }

    @Override
    protected int getSlideViewId() {
        return R.id.friend_details_parent;
    }

    @Override
    protected View onCreateWrapView(View childView) {
        return LayoutInflater.from(childView.getContext()).inflate(R.layout.fmt_friend_details, (ViewGroup) childView, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    private void initView(View view) {
        friendHead = view.findViewById(R.id.friend_details_icon);
        friendHead.setOnClickListener(this);
        friendName = view.findViewById(R.id.friend_user_name_tv);
        friendAge = view.findViewById(R.id.friend_user_age_tv);
        friendSign = view.findViewById(R.id.friend_user_sign_tv);
        addFriend = view.findViewById(R.id.friend_details_add_friend_btn);
        addFriend.setOnClickListener(this);
        view.findViewById(R.id.friend_details_outside).setOnClickListener(this);
        view.findViewById(R.id.friend_details_private_chat_btn).setOnClickListener(this);
    }

    private void initData() {
        if (getArguments() == null || (hxId = getArguments().getString(KEY_HXID)) == null) {
            return;
        }
        detailsVM = ViewModelProviders.of(this).get(FriendDetailVM.class);
        detailsVM.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    setData(user);
                }
            }
        });
        detailsVM.getUserDetails(hxId);
        //判断对方是否是好友关系
        if (detailsVM != null) {
            detailsVM.isMyFriend(getActivity(), hxId, new SimpleCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    if (result) {
                        addFriend.setVisibility(View.GONE);
                    } else {
                        addFriend.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onError(int errorCode, String errorMessage) {
                    addFriend.setVisibility(View.GONE);
                }
            });
        }
    }

    private void setData(User user) {
        friendHead.setUserHeadMsg(user);
        friendName.setText(user.getName());
        friendAge.setText(user.getAge() + getContext().getString(R.string.personal_sui));
        friendSign.setText(user.getPersonalSignature());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.friend_details_icon:
                break;
            case R.id.friend_details_outside:
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
                break;
            case R.id.friend_details_add_friend_btn:
                if (detailsVM != null && !TextUtils.isEmpty(hxId)) {
                    detailsVM.requestToAddFriend(getActivity(), hxId);
                }
                break;
            case R.id.friend_details_private_chat_btn:
                if (mCallback != null) {
                    mCallback.onChatToUser(hxId);
                }
                break;
        }
    }

    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onChatToUser(String hxAccount);
    }
}
