package com.xiaoma.club.discovery.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xiaoma.club.R;
import com.xiaoma.club.common.model.ClubEventConstants;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.repo.RepoObserver;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.club.contact.model.GroupCardInfo;
import com.xiaoma.club.discovery.controller.DiscoveryGroupCardAdapter;
import com.xiaoma.club.discovery.viewmodel.DiscoveryVM;
import com.xiaoma.club.msg.chat.ui.ChatActivity;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.tputils.TPUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: loren
 * Date: 2018/10/10 0010
 */
@PageDescComponent(ClubEventConstants.PageDescribe.discoveryFragment)
public class DiscoveryFragment extends BaseFragment implements View.OnClickListener, DiscoveryGroupCardAdapter.OnProgressStateListener, RepoObserver {
    private static final String TAG = DiscoveryFragment.class.getSimpleName();
    RecyclerView discoveryRv;
    private XmScrollBar scrollBar;
    private DiscoveryVM discoveryVM;
    private DiscoveryGroupCardAdapter adapter;
    private RelativeLayout content, loading, empty;
    private View error;
    private ImageView loadingView;
    private boolean isCreate = true;
    private Handler handler;
    private Runnable runnable;
    private NewGuide newGuide;
    private boolean hasShowedGuide;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fmt_discovery, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initVM();
        ClubRepo.getInstance().getGroupRepo().addObserver(this);
        ClubRepo.getInstance().getDiscoverRepo().addObserver(this);
        if (shouldShowGuide()) {
            if (NetworkUtils.isConnected(getContext())) {
                EventBus.getDefault().register(this);
            }
            showGuideWindow();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissGuideWindow();
        ClubRepo.getInstance().getGroupRepo().removeObserver(this);
        ClubRepo.getInstance().getDiscoverRepo().removeObserver(this);
    }

    private void initView(View view) {
        content = view.findViewById(R.id.discovery_content);
        loading = view.findViewById(R.id.discovery_loading);
        empty = view.findViewById(R.id.discovery_empty);
        error = view.findViewById(R.id.discovery_error);
        view.findViewById(R.id.tv_retry).setOnClickListener(this);
        loadingView = view.findViewById(R.id.discovery_loading_iv);


        view.findViewById(R.id.discovery_search_btn).setOnClickListener(this);
        discoveryRv = view.findViewById(R.id.discovery_rv);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        discoveryRv.setLayoutManager(layoutManager);

        List<GroupCardInfo> groupCardInfos = new ArrayList<>();
        discoveryRv.setAdapter(adapter = new DiscoveryGroupCardAdapter(ImageLoader.with(this), groupCardInfos));
        adapter.setOnProgressStateListener(this);

        scrollBar = view.findViewById(R.id.club_scroll_bar);
        scrollBar.setRecyclerView(discoveryRv);
    }


    private void initVM() {
        isCreate = true;
        discoveryVM = ViewModelProviders.of(this).get(DiscoveryVM.class);
        discoveryVM.getGroupList().observe(this, new Observer<XmResource<List<GroupCardInfo>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<GroupCardInfo>> listXmResource) {
                if (listXmResource != null) {
                    listXmResource.handle(new OnCallback<List<GroupCardInfo>>() {
                        @Override
                        public void onSuccess(List<GroupCardInfo> data) {
                            if (data != null) {
                                adapter.refreshData(data);
                                checkEmptyView(data);
                            } else {
                                showEmptyView();
                            }
                        }

                        @Override
                        public void onLoading() {
                            showLoadingView();
                        }

                        @Override
                        public void onError(int code, String message) {
                            showErrorView();
                        }

                        @Override
                        public void onFailure(String msg) {
                            showEmptyView();
                        }
                    });
                } else {
                    showEmptyView();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (discoveryVM != null) {
            discoveryVM.requestHotGroup(isCreate);
            isCreate = false;
        }
    }

    private void checkEmptyView(List<GroupCardInfo> groupCardInfos) {
        if (groupCardInfos.isEmpty()) {
            showEmptyView();
            scrollBar.setVisibility(View.GONE);
        } else {
            showContentView();
        }
    }

    @Override
    protected void showLoadingView() {
        content.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        anim.setRepeatMode(Animation.RESTART);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        loadingView.startAnimation(anim);
    }

    @Override
    protected void showEmptyView() {
        loading.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
        discoveryRv.setVisibility(View.GONE);
        scrollBar.setVisibility(View.GONE);
        empty.setVisibility(View.VISIBLE);
        loadingView.clearAnimation();
    }

    @Override
    protected void showErrorView() {
        loading.setVisibility(View.GONE);
        content.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
        loadingView.clearAnimation();
    }

    @Override
    protected void showContentView() {
        loading.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
        discoveryRv.setVisibility(View.VISIBLE);
        empty.setVisibility(View.GONE);
        loadingView.clearAnimation();
    }

    @Override
    public void onClick(View v) {
        Context context = v.getContext();
        switch (v.getId()) {
            case R.id.discovery_search_btn:
                context.startActivity(new Intent(context, DiscoverySearchActivity.class));
                break;
            case R.id.tv_retry:
                if (!NetworkUtils.isConnected(context)) {
                    showToastException(R.string.net_work_error);
                    return;
                }
                if (discoveryVM != null) {
                    discoveryVM.requestHotGroup(true);
                    isCreate = false;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onProgressState(boolean isShow) {
        if (isShow) {
            showProgressDialog(R.string.loading_add_group);
        } else {
            dismissProgress();
            dismissGuideWindow();
        }
    }

    @Override
    public void showKickOutDialog(final String groupId) {
        final ConfirmDialog dialog = new ConfirmDialog(getActivity());
        dialog.setContent(getContext().getString(R.string.group_tips_removed))
                .setPositiveButton(getContext().getString(R.string.group_tips_chat_to_admin), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        final GroupCardInfo groupInfo = ClubRepo.getInstance().getGroupRepo().get(groupId);
                        if (groupInfo != null) {
                            final String adminHxId = groupInfo.getAdminHxId();
                            ChatActivity.start(getActivity(), adminHxId, false);
                        }
                    }
                })
                .setNegativeButton(getContext().getString(R.string.group_tips_quit), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onChanged(String table) {
        LogUtil.logI(TAG, "onChanged( table: %s )", table);
    }

    @Subscriber(tag = "club_data_update")
    public void dataUpdate(final Boolean forceDismiss) {
        if (hasShowedGuide) return;
        hasShowedGuide = true;
        if (newGuide != null) {
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (forceDismiss != null && forceDismiss) {
                            finishGuide();
                            return;
                        }
                        View itemView = discoveryRv.getLayoutManager().findViewByPosition(0);
                        if (itemView != null) {
                            Log.e(TAG, "startGuide()");
                            newGuide.setTargetViewAndRect(((ViewGroup) itemView).getChildAt(1));
                            newGuide.startGuide();
                            GuideDataHelper.setFirstGuideFalse(GuideConstants.CLUB_SHOWED);
                        } else {
                            dismissGuideWindow();
                            Log.e(TAG, "startGuide() Failed, itemView is null");
                        }
                    } catch (Exception e) {
                        dismissGuideWindow();
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void finishGuide() {
        dismissGuideWindow();
        GuideDataHelper.finishGuideData(GuideConstants.CLUB_SHOWED);
        TPUtils.put(getContext(), GuideConstants.CLUB_SHOWED, true);
        TPUtils.put(getContext(), GuideConstants.CLUB_GUIDE_FIRST, false);
    }

    private boolean shouldShowGuide() {
        Log.e(TAG, "newGuide = " + newGuide);
        boolean guideFlag = GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.CLUB_SHOWED, GuideConstants.CLUB_GUIDE_FIRST, true);
        Log.e(TAG, "guideFlag = " + guideFlag);
        return guideFlag && newGuide == null;
    }

    private void showGuideWindow(final View view) {
        if (!shouldShowGuide()) return;
        view.post(new Runnable() {
            @Override
            public void run() {
                newGuide = NewGuide.with(getActivity())
                        .setLebal(GuideConstants.CLUB_SHOWED)
                        .setTargetViewAndRect(view)
                        .setGuideLayoutId(R.layout.guide_view_group)
                        .setNeedHande(true)
                        .setNeedShake(true)
                        .setViewHandId(R.id.iv_gesture)
                        .setViewWaveIdOne(R.id.iv_wave_one)
                        .setViewWaveIdTwo(R.id.iv_wave_two)
                        .setViewWaveIdThree(R.id.iv_wave_three)
                        .setViewSkipId(R.id.tv_guide_skip)
                        .setHandLocation(NewGuide.RIGHT_AND_BOTTOM_TOP)
                        .build();
                newGuide.showGuide();
                GuideDataHelper.setFirstGuideFalse(GuideConstants.CLUB_SHOWED);
            }
        });
    }

    private void showGuideWindow() {
        Log.e(TAG, "showGuideWindow");
        if (newGuide != null) return;
        Log.e(TAG, "addGuideWindow");
        newGuide = NewGuide.with(getActivity())
                .setLebal(GuideConstants.CLUB_SHOWED)
                .setGuideLayoutId(R.layout.guide_view_group)
                .setNeedHande(true)
                .setNeedShake(true)
                .setViewHandId(R.id.iv_gesture)
                .setViewWaveIdOne(R.id.iv_wave_one)
                .setViewWaveIdTwo(R.id.iv_wave_two)
                .setViewWaveIdThree(R.id.iv_wave_three)
                .setViewSkipId(R.id.tv_guide_skip)
                .setHandLocation(NewGuide.RIGHT_AND_BOTTOM_TOP)
                .build();
        newGuide.addGuideWindow();
    }

    private void dismissGuideWindow() {
        Log.d(TAG, "dismissGuideWindow");
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            handler = null;
            runnable = null;
        }
        if (newGuide != null) {
            newGuide.dismissGuideWindow();
            newGuide = null;
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
