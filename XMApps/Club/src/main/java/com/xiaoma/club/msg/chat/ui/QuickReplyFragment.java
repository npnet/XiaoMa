package com.xiaoma.club.msg.chat.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.club.R;
import com.xiaoma.club.common.ui.SlideInFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by LKF on 2018/10/10 0010.
 */
public class QuickReplyFragment extends SlideInFragment {
    // TODO: 测试数据
    public static final String[] REPLY_TEXT_ARR = new String[]{
            "Hi你好,可以交个朋友吗?", "你电话多少,可以认识一下吗?", "不要走,决战到天亮", "你的牌打得太烂了", "你是GG还是MM?",
            "测试回复数据001", "测试回复数据002", "测试回复数据003", "测试回复数据004", "测试回复数据005"};

    private RecyclerView mRvReplyList;
    private List<String> mReplyList = new ArrayList<>();

    @Override
    protected int getSlideViewId() {
        return R.id.quick_reply_parent;
    }

    @Override
    protected View onCreateWrapView(View childView) {
        return LayoutInflater.from(childView.getContext()).inflate(R.layout.fmt_quick_reply, (ViewGroup) childView, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvReplyList = view.findViewById(R.id.rv_reply_list);
        mRvReplyList.setHasFixedSize(true);
        mRvReplyList.setLayoutManager(new LinearLayoutManager(getContext()));
        // TODO: 测试数据
        mReplyList.addAll(Arrays.asList(REPLY_TEXT_ARR));
        mRvReplyList.setAdapter(new ReplyItemAdapter());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });
    }

    private class ReplyItemAdapter extends RecyclerView.Adapter<ReplyItemHolder> {
        @Override
        public ReplyItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ReplyItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quick_reply, parent, false));
        }

        @Override
        public void onBindViewHolder(ReplyItemHolder holder, int position) {
            final String replyContent = mReplyList.get(position);
            holder.mTvReplyContent.setText(replyContent);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onReplyContentClick(QuickReplyFragment.this, v, replyContent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mReplyList.size();
        }
    }

    private static class ReplyItemHolder extends RecyclerView.ViewHolder {
        TextView mTvReplyContent;

        ReplyItemHolder(View itemView) {
            super(itemView);
            mTvReplyContent = itemView.findViewById(R.id.tv_reply_content);

        }
    }

    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onReplyContentClick(Fragment f, View v, String content);
    }
}