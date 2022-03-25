package com.xiaoma.motorcade.main.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.motorcade.MainActivity;
import com.xiaoma.motorcade.R;
import com.xiaoma.motorcade.common.constants.MotorcadeConstants;
import com.xiaoma.motorcade.common.model.GroupCardInfo;
import com.xiaoma.motorcade.common.model.MeetingInfo;
import com.xiaoma.motorcade.common.ui.MotorcadeBaseFragment;
import com.xiaoma.motorcade.common.utils.MotorcadeDialogUtils;
import com.xiaoma.motorcade.common.utils.UserUtil;
import com.xiaoma.motorcade.common.view.RecyclerDividerItem;
import com.xiaoma.motorcade.main.adapter.MotorcadeListAdapter;
import com.xiaoma.motorcade.main.vm.MainVM;
import com.xiaoma.motorcade.map.ui.MotorcadeConferenceActivity;
import com.xiaoma.motorcade.setting.ui.SettingActivity;
import com.xiaoma.ui.StateControl.OnRetryClickListener;
import com.xiaoma.ui.StateControl.StateView;
import com.xiaoma.ui.StateControl.Type;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.ShareUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.share.ShareCallBack;
import com.xiaoma.utils.share.ShareClubBean;
import com.xiaoma.utils.share.ShareConstants;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by ZYao.
 * Date ：2019/1/16 0016
 */
@PageDescComponent(MotorcadeConstants.PageDesc.mainFragment)
public class MainFragment extends MotorcadeBaseFragment implements View.OnClickListener {

    private static final int REFRESH_LIST_DATA_TIME = 30000;
    private static final String TAG = "MainFragment_join";
    private TextView tvJoinMotorcadeNum;
    private LinearLayout llJoinMotorcade;
    private LinearLayout llCreateMotorcade;
    private RecyclerView rvMotorcade;
    private XmScrollBar scrollBar;
    private StateView stateView;
    private MotorcadeListAdapter mAdapter;
    private MainVM mainVM;
    private boolean firstLoading = true;
    private boolean hasJumped = false;
    private boolean createAgain = false;
    private int refreshCount = 0;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public static MainFragment newInstance(Bundle bundle) {
        MainFragment fragment = new MainFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initEvent();
        initVM();
    }

    @Override
    public void operateAfterCreate(GroupCardInfo info) {
        fetchData();
        createAgain = true;
    }

    @Override
    public void operateAfterJoin() {
        fetchData();
        createAgain = true;
    }

    @Override
    public void operateAfterQuit() {
        fetchData();
    }

    private void fetchData() {
        mainVM.fetchMotorcadeData();
    }

    private void initEvent() {
        EventBus.getDefault().register(this);
        llJoinMotorcade.setOnClickListener(this);
        llCreateMotorcade.setOnClickListener(this);
        rvMotorcade.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        int offset = mContext.getResources().getDimensionPixelOffset(R.dimen.size_item_margin);
        RecyclerDividerItem dividerItem = new RecyclerDividerItem(mContext, DividerItemDecoration.HORIZONTAL);
        dividerItem.setRect(0, 0, offset, 0);
        rvMotorcade.addItemDecoration(dividerItem);
        mAdapter = new MotorcadeListAdapter(null);
        rvMotorcade.setAdapter(mAdapter);
        scrollBar.setRecyclerView(rvMotorcade);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                final GroupCardInfo groupCardInfo = (GroupCardInfo) adapter.getItem(position);
                switch (view.getId()) {
                    case R.id.btn_exit_motorcade:
                        if (!NetworkUtils.isConnected(getContext())) {
                            showToastException(R.string.net_work);
                            return;
                        }
                        MotorcadeDialogUtils.showExitDialog(getActivity(), getChildFragmentManager(),
                                new MotorcadeDialogUtils.DialogClickListener() {
                                    @Override
                                    public void onSure(String text) {
                                        quitCurrentTeam(groupCardInfo);
                                    }

                                    @Override
                                    public void onCancel() {

                                    }
                                });
                        break;
                    case R.id.motorcade_info_ll:
                        if (!NetworkUtils.isConnected(getContext())) {
                            showToastException(R.string.net_work);
                            return;
                        }
                        if (groupCardInfo != null) {
                            mainVM.jumpToMeeting(groupCardInfo.getId());
                        } else {
                            showToastException(R.string.join_failed_error);
                        }
                        break;
                    case R.id.btn_motorcade_setting:
                        SettingActivity.launch(mContext, groupCardInfo);
                        break;
                }
            }
        });
        rvMotorcade.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                        if (layoutManager instanceof LinearLayoutManager) {
                            LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                            //获取最后一个可见view的位置
                            int lastItemPosition = linearManager.findLastVisibleItemPosition() + 1;
                            int hiddenCount = mainVM.getJoinNum().getValue() - lastItemPosition;
                            XmAutoTracker.getInstance().onEventMotorcade(MotorcadeConstants.MOTORCADE_LIST,
                                    hiddenCount, lastItemPosition, TAG, MotorcadeConstants.PageDesc.mainActivity);
                        }
                        break;
                    default:
                        break;
                }

            }

        });
    }

    private void runTimerTask() {
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                fetchData();
            }
        }, REFRESH_LIST_DATA_TIME, REFRESH_LIST_DATA_TIME, TimeUnit.MILLISECONDS);
    }

    private void initVM() {
        mainVM = ViewModelProviders.of(this).get(MainVM.class);
        runTimerTask();
        // 监听车队详细信息
        mainVM.getMotorcadeInfoList().observe(this, new Observer<XmResource<List<GroupCardInfo>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<GroupCardInfo>> listXmResource) {
                if (listXmResource != null) {
                    listXmResource.handle(new OnCallback<List<GroupCardInfo>>() {
                        @Override
                        public void onSuccess(final List<GroupCardInfo> data) {
                            if (!ListUtils.isEmpty(data)) {
                                stateView.showContent();
                                mAdapter.setNewData(data);
                                // 第一次加载成功后先存储一次
                                storeDatas(data);
                                handleArgument(data);
                            }
                        }

                        @Override
                        public void onLoading() {
                            KLog.d("onLoading: ");
                            if (firstLoading) {
                                showProgressDialog(com.xiaoma.component.R.string.base_loading);
                                firstLoading = false;
                            }
                        }

                        @Override
                        public void onFailure(String msg) {
                            KLog.e("onFailure: ");
                            dismissProgress();
                            if (!StringUtil.isEmpty(msg)) {
                                KLog.e(msg);
                            }
                            List<GroupCardInfo> infos = new ArrayList<>();
                            mAdapter.setNewData(infos);
                            stateView.showEmpty();
                            tvJoinMotorcadeNum.setText(getString(R.string.number_zero));
                            if (callBack != null) {
                                callBack.replaceInitial();
                            }
                            //如果车队列表空了，及时更新数据库
                            mainVM.saveMotorcadeDatas(infos);
                        }

                        @Override
                        public void onError(int code, String message) {
                            handleError();
                        }

                    });
                }
            }
        });
        // 监听加入车队的数量
        mainVM.getJoinNum().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer != null) {
                    tvJoinMotorcadeNum.setText(integer + getString(R.string.motorcade_unit));
                }
            }
        });
        mainVM.getMeetingInfo().observe(this, new Observer<XmResource<MeetingInfo>>() {
            @Override
            public void onChanged(@Nullable XmResource<MeetingInfo> infoXmResource) {
                if (infoXmResource != null) {
                    infoXmResource.handle(new OnCallback<MeetingInfo>() {
                        @Override
                        public void onSuccess(MeetingInfo data) {
                            if (data != null) {
                                joinSuccess(data);
                            } else {
                                joinFailed();
                            }
                        }

                        @Override
                        public void onError(int code, String message) {
                            joinFailed();
                        }

                        @Override
                        public void onFailure(String msg) {
                            joinFailed();
                        }

                        @Override
                        public void onLoading() {
                            dismissProgress();
                            showProgressDialog(R.string.join_loading);
                        }
                    });
                } else {
                    joinFailed();
                }
            }
        });
    }

    private void handleArgument(List<GroupCardInfo> data) {
        if (createAgain) {
            openShare(data.get(0));
            createAgain = false;
            return;
        }
        if (getArguments().getBoolean("isOpenShare")) {
            if (hasJumped) {
                return;
            }
            openShare(data.get(0));
            hasJumped = true;
        }
    }

    private void initView(View view) {
        stateView = view.findViewById(R.id.main_state_view);
        tvJoinMotorcadeNum = view.findViewById(R.id.tv_join_motorcade_num);
        llJoinMotorcade = view.findViewById(R.id.ll_join_motorcade);
        llCreateMotorcade = view.findViewById(R.id.ll_create_motorcade);
        rvMotorcade = view.findViewById(R.id.rv_motorcade_list);
        scrollBar = view.findViewById(R.id.motorcade_scroll_bar);
        stateView.setOnRetryClickListener(new OnRetryClickListener() {
            @Override
            public void onRetryClick(View view, Type type) {
                fetchData();
            }
        });
    }

    @Override
    @NormalOnClick({MotorcadeConstants.NormalClick.join, MotorcadeConstants.NormalClick.create})
    @ResId({R.id.ll_join_motorcade, R.id.ll_create_motorcade})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_join_motorcade:
                if (!NetworkUtils.isConnected(getContext())) {
                    showToastException(R.string.net_work);
                    return;
                }
                MotorcadeDialogUtils.showJoinDialog(getActivity(), getChildFragmentManager(),
                        new MotorcadeDialogUtils.DialogClickListener() {
                            @Override
                            public void onSure(String text) {
                                joinMotorcade(text);
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                break;
            case R.id.ll_create_motorcade:
                if (!NetworkUtils.isConnected(getContext())) {
                    showToastException(R.string.net_work);
                    return;
                }
                MotorcadeDialogUtils.showCreateDialog(getActivity(), getChildFragmentManager(),
                        new MotorcadeDialogUtils.DialogClickListener() {
                            @Override
                            public void onSure(String text) {
                                createMotorcade(text);
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                break;
        }
    }


    @Subscriber(tag = MotorcadeConstants.EventTag.OPEN_MAP)
    public void openMap(GroupCardInfo model) {
        showProgressDialog(R.string.join_loading);
        mainVM.jumpToMeeting(model.getId());
    }

    @Subscriber(tag = MotorcadeConstants.EventTag.JUMP_SHARE)
    public void jumpShare(GroupCardInfo model) {
        ShareClubBean bean = new ShareClubBean();
        bean.setFromPackage(getContext().getPackageName());
        bean.setBackAction(ShareConstants.MOTORCADE_HANDLE_SHARE_ACTION);
        bean.setCoreKey(model.getHxGroupId());
        bean.setShareImage(model.getPicPath());
        bean.setShareTitle(UserUtil.getCurrentUser().getName() + getString(R.string.motor_name, model.getHxGroupId()));
        bean.setCarTeamDetail(model.getCount() + "," + model.getNick() + "," + model.getHxGroupId());
        bean.setCarTeamId(model.getId());
        bean.setShareContent(getString(R.string.motor_command));
        ShareUtils.shareToClub(getContext(), bean, new ShareCallBack() {
            @Override
            public void shareError(String errorMsg) {
                if (!TextUtils.isEmpty(errorMsg)) {
                    showToastException(errorMsg);
                }
            }

            @Override
            public void shareSuccess() {

            }
        });
    }

    private void handleError() {
        dismissProgress();
        List<GroupCardInfo> list = mainVM.getInfosFromRepo();
        if (list == null) {
            stateView.showNoNetwork();
            tvJoinMotorcadeNum.setText(getString(R.string.number_zero));
        } else if (list.isEmpty()) {
            showEmptyView();
            tvJoinMotorcadeNum.setText(getString(R.string.number_zero));
        } else {
            stateView.showContent();
            mAdapter.setNewData(list);
            tvJoinMotorcadeNum.setText(list.size() + "个");
        }
    }

    private void joinFailed() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                showToastException(R.string.join_failed);
                dismissProgress();
            }
        });
    }

    private void joinSuccess(final MeetingInfo info) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                dismissProgress();
                MotorcadeConferenceActivity.launcherMapActivity(mContext, info, false);
            }
        });
    }

    private void storeDatas(List<GroupCardInfo> infos) {
        if (refreshCount % 11 == 0) {
            mainVM.saveMotorcadeDatas(infos);
        }
        refreshCount++;
    }

    private MainActivity.ReplaceMainCallBack callBack;

    public void setCallBack(MainActivity.ReplaceMainCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchData();
    }
}
