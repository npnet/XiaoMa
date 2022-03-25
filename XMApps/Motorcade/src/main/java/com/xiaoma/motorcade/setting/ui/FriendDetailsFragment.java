package com.xiaoma.motorcade.setting.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xiaoma.model.XmResource;
import com.xiaoma.motorcade.R;
import com.xiaoma.motorcade.common.ui.SlideInFragment;
import com.xiaoma.motorcade.common.utils.UserUtil;
import com.xiaoma.motorcade.common.view.UserHeadView;
import com.xiaoma.motorcade.setting.model.UserDetailInfo;
import com.xiaoma.motorcade.setting.vm.FriendDetailVM;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

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
    private static final String KEY_UID = "key_uid";
    private static final String KEY_NICK = "key_nick";
    private String hxId;

    public static FriendDetailsFragment newInstance(String hxId, long id, String nickName) {
        FriendDetailsFragment fragment = new FriendDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_HXID, hxId);
        bundle.putLong(KEY_UID, id);
        bundle.putString(KEY_NICK, nickName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getSlideViewId() {
        return R.id.friend_details_parent;
    }

    @Override
    protected View onCreateWrapView(View childView) {
        return LayoutInflater.from(childView.getContext()).inflate(R.layout.fmt_friend_details,
                (ViewGroup) childView, false);
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
        addFriend = view.findViewById(R.id.add_friend);
        addFriend.setOnClickListener(this);
        view.findViewById(R.id.friend_details_outside).setOnClickListener(this);
    }

    private void initData() {
        if (getArguments() == null || (hxId = getArguments().getString(KEY_HXID)) == null) {
            return;
        }
        detailsVM = ViewModelProviders.of(this).get(FriendDetailVM.class);
        detailsVM.getUser().observe(this, new Observer<XmResource<UserDetailInfo>>() {
            @Override
            public void onChanged(@Nullable final XmResource<UserDetailInfo> userXmResource) {
                userXmResource.handle(new OnCallback<UserDetailInfo>() {
                    @Override
                    public void onSuccess(UserDetailInfo data) {
                        setData(data);
                    }

                    @Override
                    public void onFailure(String msg) {
                        KLog.e("onFailure: ");
                        dismissProgress();
                    }

                    @Override
                    public void onError(int code, String message) {
                        KLog.e("onError: ");
                        dismissProgress();
                    }
                });
            }
        });
        detailsVM.getUserDetails(hxId);
    }

    private void setData(UserDetailInfo user) {
        if (user.getIsFriend() == 1 || isCurrentUser()) {
            addFriend.setEnabled(false);
            addFriend.setVisibility(View.GONE);
        } else {
            addFriend.setEnabled(true);
            addFriend.setVisibility(View.VISIBLE);
        }
        friendHead.setUserHeadMsg(user.getPicPath(), user.getGender());
        friendName.setText(getArguments().getString(KEY_NICK));
        friendAge.setText(user.getAge() + getContext().getString(R.string.personal_sui));
        friendSign.setText(user.getPersonalSignature());
    }

    private boolean isCurrentUser() {
        if (getArguments().getLong(KEY_UID) == UserUtil.getCurrentUser().getId()) {
            return true;
        }
        return false;
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
            case R.id.add_friend:
                if (!NetworkUtils.isConnected(getContext())) {
                    XMToast.toastException(getContext(), getContext().getString(R.string.net_work));
                    return;
                }
                long fromId = UserUtil.getCurrentUser().getId();
                requestAddFriend(fromId, getArguments().getLong(KEY_UID));
                break;
        }
    }

    private void requestAddFriend(long fromId, long toId) {
        detailsVM.requestAddFriend(getContext(), fromId, toId);
    }

}
