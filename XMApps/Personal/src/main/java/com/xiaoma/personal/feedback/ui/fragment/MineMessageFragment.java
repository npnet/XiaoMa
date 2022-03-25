package com.xiaoma.personal.feedback.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.personal.R;
import com.xiaoma.personal.common.OnlyCode;
import com.xiaoma.personal.common.RequestManager;
import com.xiaoma.personal.common.util.EventConstants;
import com.xiaoma.personal.feedback.adapter.MessageItemDecoration;
import com.xiaoma.personal.feedback.adapter.MineMessageAdapter;
import com.xiaoma.personal.feedback.model.MessageInfo;
import com.xiaoma.personal.feedback.ui.FeedbackActivity;
import com.xiaoma.personal.feedback.ui.view.MessageDetailWindow;
import com.xiaoma.personal.feedback.vm.MessageVM;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.progress.loading.XMProgress;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import java.util.List;
import java.util.Objects;

/**
 * @author Gillben
 * date: 2018/12/05
 * <p>
 * 反馈页 我的消息
 */
@PageDescComponent(EventConstants.PageDescribe.mineMessageFragment)
public class MineMessageFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = MineMessageFragment.class.getSimpleName();
    private View mTranslucentView;
    private ConstraintLayout mConstraintLayout;
    private TextView mTitleText;
    private RecyclerView mRecyclerView;
    private XmScrollBar mXmScrollBar;

    //异常页面控件
    private LinearLayout mExceptionLinearLayout;
    private ImageView mExceptionImg;
    private TextView mExceptionText;
    private Button mExceptionBt;
    private MessageDetailWindow detailWindow;

    private MineMessageAdapter mMineMessageAdapter;
    private MessageVM messageVM;


    private static final int REQUEST_SUCCESS = 0;           //请求成功
    private static final int REQUEST_SUCCESS_NON = 1;       //请求成功，无消息数据
    private static final int REQUEST_FAILED = 2;            //请求失败

    private int accessNumber = 10;     //每次访问消息数量
    private int currentPage = 1;      //当前访问页数
    private int totalPages;           //记录总页数
    private int unreadMessageNumbers;  //记录未读消息条数


    public static BaseFragment newInstance() {
        return new MineMessageFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_mine_message, container, false);
        return onCreateWrapView(contentView);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }


    @Override
    protected void showContentView() {
        super.showContentView();
        initData();
    }

    private void initView(View contentView) {
        mTranslucentView = contentView.findViewById(R.id.message_translucent_view);
        mConstraintLayout = contentView.findViewById(R.id.cl_message_home_page);
        mTitleText = contentView.findViewById(R.id.tv_mine_message_title);
        mRecyclerView = contentView.findViewById(R.id.rv_mine_message);
        mXmScrollBar = contentView.findViewById(R.id.mine_message_scroll_bar);

        mExceptionLinearLayout = contentView.findViewById(R.id.exception_page_layout);
        mExceptionImg = contentView.findViewById(R.id.iv_exception_image);
        mExceptionText = contentView.findViewById(R.id.tv_exception_desc);
        mExceptionBt = contentView.findViewById(R.id.bt_exception_reload);
        mExceptionBt.setOnClickListener(this);

        mMineMessageAdapter = new MineMessageAdapter();
        MessageItemDecoration itemDecoration = new MessageItemDecoration(40);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapter(mMineMessageAdapter);
        mXmScrollBar.setRecyclerView(mRecyclerView);

        addListener();
    }


    private void initData() {
        if (!NetworkUtils.isConnected(getContext())) {
            showNoNetView();
            return;
        }
        messageVM = ViewModelProviders.of(this).get(MessageVM.class);
        fetchData(false);
    }


    private void addListener() {
        mMineMessageAdapter.setOnItemClickListener((adapter, view, position) -> {
            List<MessageInfo.MessageList> messageLists = mMineMessageAdapter.getData();
            showMessageDetailWindow(messageLists.get(position));
            updateMessageStatus(messageLists, position);
        });

        //加载更多
        mMineMessageAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                fetchData(true);
            }
        }, mRecyclerView);
    }


    private void fetchData(boolean more) {
        if (!more) {
            XMProgress.showProgressDialog(this, mContext.getString(R.string.hard_to_loading));
        }
        messageVM.getMessageList(currentPage, accessNumber).observe(this, new Observer<XmResource<MessageInfo>>() {
            @Override
            public void onChanged(@Nullable XmResource<MessageInfo> messageInfoXmResource) {
                if (messageInfoXmResource == null) {
                    KLog.w(TAG, "messageInfoXmResource is null.");
                    return;
                }

                ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
                    @Override
                    public void run() {
                        XMProgress.dismissProgressDialog(MineMessageFragment.this);
                        handleData(messageInfoXmResource);
                    }
                }, more ? 0 : 500);
            }
        });
    }

    private void handleData(XmResource<MessageInfo> messageInfoXmResource) {
        messageInfoXmResource.handle(new XmResource.OnHandleCallback<MessageInfo>() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onSuccess(MessageInfo data) {
                if (data.getList() == null || data.getList().size() <= 0) {
                    changePageLayout(REQUEST_SUCCESS_NON);
                    return;
                }

                int totalPages = data.getTotalPages();
                if (currentPage <= totalPages) {
                    if (data.getCurrentPage() == 1) {
                        MineMessageFragment.this.unreadMessageNumbers = data.getNoRead();
                        updateUnreadMessageNumber(unreadMessageNumbers);
                        changePageLayout(REQUEST_SUCCESS);
                        mMineMessageAdapter.setNewData(data.getList());
                    } else {
                        mMineMessageAdapter.addData(data.getList());
                    }
                    mMineMessageAdapter.loadMoreComplete();

                    ++currentPage;

                    if (currentPage > totalPages) {
                        mMineMessageAdapter.loadMoreEnd(true);
                    }
                }

            }

            @Override
            public void onFailure(String message) {
                currentPage = 1;
                changePageLayout(REQUEST_FAILED);
            }

            @Override
            public void onError(int code, String message) {

            }

            @Override
            public void onCompleted() {

            }
        });
    }


    /**
     * 更新未读消息
     *
     * @param unread 未读消息数
     */
    private void updateUnreadMessageNumber(int unread) {
        if (unread < 0) {
            unread = 0;
        }
        String title = getString(R.string.welcome_to_my_msg, unread);
        mTitleText.setText(title);
    }


    /**
     * 显示消息详情窗口
     *
     * @param messageList 消息实体类
     */
    private void showMessageDetailWindow(MessageInfo.MessageList messageList) {
        if (detailWindow == null) {
            detailWindow = new MessageDetailWindow(getContext(), 1165, WindowManager.LayoutParams.MATCH_PARENT);
        }
        if (detailWindow.isShowing()) {
            return;
        }

        detailWindow.setOnCommentCallback(new MessageDetailWindow.OnCommentCallback() {
            @Override
            public void success(MessageInfo.MessageList messageInfo) {
                if (mMineMessageAdapter != null) {
                    messageInfo.setIsReview(1);
                    int position = mMineMessageAdapter.getData().indexOf(messageInfo);
                    mMineMessageAdapter.notifyItemChanged(position, messageInfo);
                }
            }

            @Override
            public void failed() {
                //TODO 评论失败  Nothing to do
            }
        });

        detailWindow.show(getView(), Gravity.TOP | Gravity.START, 0, 0, new MessageDetailWindow.OnPopupWindowCallback() {
            @Override
            public void open() {
                ((FeedbackActivity) Objects.requireNonNull(getActivity())).hideNavigation();
                mTranslucentView.setVisibility(View.VISIBLE);
            }

            @Override
            public void close() {
                ((FeedbackActivity) Objects.requireNonNull(getActivity())).showHomeAndBack();
                mTranslucentView.setVisibility(View.GONE);
            }
        });
        detailWindow.setContent(messageList);
    }


    /**
     * 更新消息状态
     */
    private void updateMessageStatus(final List<MessageInfo.MessageList> list, final int position) {
        final MessageInfo.MessageList temp = list.get(position);
        if (temp.isRead() == 0) {
            return;
        }

        temp.setRead(0);
        RequestManager.readMessageChangeStatus(temp.getId(), new ResultCallback<XMResult<OnlyCode>>() {
            @Override
            public void onSuccess(XMResult<OnlyCode> model) {
                mMineMessageAdapter.notifyItemChanged(position, temp);
                updateUnreadMessageNumber(--unreadMessageNumbers);
            }

            @Override
            public void onFailure(int code, String msg) {
                //nothing to do
            }
        });
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 随实际请求结果改变页面布局
     */
    private void changePageLayout(int requestStatus) {
        if (requestStatus == REQUEST_SUCCESS) {
            mExceptionLinearLayout.setVisibility(View.GONE);
            mConstraintLayout.setVisibility(View.VISIBLE);

        } else if (requestStatus == REQUEST_SUCCESS_NON) {
            mConstraintLayout.setVisibility(View.GONE);
            mExceptionBt.setVisibility(View.GONE);
            mExceptionLinearLayout.setVisibility(View.VISIBLE);

            mExceptionText.setText(R.string.non_message);
            Glide.with(this).load(R.drawable.not_mine_message).into(mExceptionImg);

        } else if (requestStatus == REQUEST_FAILED) {
            showNoNetView();
//            mExceptionLinearLayout.setVisibility(View.VISIBLE);
//            mExceptionBt.setVisibility(View.VISIBLE);
//
//            mExceptionText.setText(R.string.loading_error);
//            Glide.with(this).load(R.drawable.loading_error).into(mExceptionImg);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_exception_reload:
                initData();
                break;
        }
    }
}
