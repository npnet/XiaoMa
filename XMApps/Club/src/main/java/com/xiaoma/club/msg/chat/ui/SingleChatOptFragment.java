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

import com.xiaoma.club.R;
import com.xiaoma.club.common.model.ClubEventConstants;
import com.xiaoma.club.common.viewmodel.BooleanState;
import com.xiaoma.club.msg.chat.vm.ChatOptVM;
import com.xiaoma.club.msg.chat.vm.ChatVM;
import com.xiaoma.model.User;
import com.xiaoma.model.annotation.PageDescComponent;

/**
 * Created by LKF on 2019-1-14 0014.
 * 单聊
 */
@PageDescComponent(ClubEventConstants.PageDescribe.singleChatOptFragment)
public class SingleChatOptFragment extends ChatOptFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fmt_chat_opt, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initChatVM();
        initSingleChatOptVM();
    }

    private void initChatVM() {
        final ChatVM chatVM = ViewModelProviders.of(getActivity()).get(ChatVM.class);
        chatVM.getIsFriend().observe(this, new Observer<BooleanState>() {
            @Override
            public void onChanged(@Nullable BooleanState isFriend) {
                bindIsFiend(isFriend);
            }
        });
        bindIsFiend(chatVM.getIsFriend().getValue());
    }

    private void bindIsFiend(@Nullable BooleanState isFriend) {
        if (isFriend == null)
            return;
        switch (isFriend) {
            case FETCHING:
            case FALSE:
                mBtnSendFace.setEnabled(false);
                mBtnSendRedPacket.setEnabled(false);
                mBtnSendLocation.setEnabled(false);
                mBtnSendFace.setAlpha(ALPHA_DISABLE);
                mBtnSendRedPacket.setAlpha(ALPHA_DISABLE);
                mBtnSendLocation.setAlpha(ALPHA_DISABLE);
                break;
            case TRUE:
                mBtnSendFace.setEnabled(true);
                mBtnSendRedPacket.setEnabled(true);
                mBtnSendLocation.setEnabled(true);
                mBtnSendFace.setAlpha(ALPHA_ENABLE);
                mBtnSendRedPacket.setAlpha(ALPHA_ENABLE);
                mBtnSendLocation.setAlpha(ALPHA_ENABLE);
                break;
        }
    }

    private void initSingleChatOptVM() {
        final ChatOptVM chatOptVM = ViewModelProviders.of(this).get(ChatOptVM.class);
        chatOptVM.init(getChatId());
        chatOptVM.getChatToUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user == null
                        || TextUtils.isEmpty(user.getName())
                        || isDestroy()) {
                    return;
                }
                setTitle(getString(R.string.chat_opt_title_format, user.getName()));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
