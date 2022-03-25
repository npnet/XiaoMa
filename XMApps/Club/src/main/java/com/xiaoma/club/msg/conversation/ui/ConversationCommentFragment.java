package com.xiaoma.club.msg.conversation.ui;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoma.club.R;
import com.xiaoma.club.msg.conversation.controller.ConversationCommentAdapter;
import com.xiaoma.club.msg.conversation.model.CommentMsgInfo;
import com.xiaoma.component.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: loren
 * Date: 2018/10/10 0010
 */

public class ConversationCommentFragment extends BaseFragment {

    RecyclerView conversationCommentRv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fmt_conversation_comment, container, false);

        initRv(view);

        return view;
    }

    private void initRv(View view) {
        conversationCommentRv = view.findViewById(R.id.conversation_comment_rv);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        conversationCommentRv.setLayoutManager(layoutManager);

        List<CommentMsgInfo> commentMsgInfos = new ArrayList<>();

        conversationCommentRv.setAdapter(new ConversationCommentAdapter(getActivity(), commentMsgInfos));
    }
}
