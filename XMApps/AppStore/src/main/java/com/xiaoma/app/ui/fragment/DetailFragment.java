package com.xiaoma.app.ui.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.app.R;
import com.xiaoma.app.SilentInstallManager;
import com.xiaoma.app.adapter.AppDetailAdapter;
import com.xiaoma.app.common.constant.EventConstants;
import com.xiaoma.app.listener.ISwitchFragmentListener;
import com.xiaoma.app.model.DownLoadAppInfo;
import com.xiaoma.app.ui.activity.AppDetailsActivity;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.view.XmScrollBar;


import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * Created by zhushi.
 * Date: 2018/11/30
 */
@PageDescComponent(EventConstants.PageDescribe.detailFragmentPagePathDesc)
public class DetailFragment extends BaseFragment implements AppDetailsActivity.PackageRemoveListener {

    public static final String DOWNLOADAPPINFO = "downloadappinfo";

    private DownLoadAppInfo downLoadAppInfo;
    private RecyclerView recyclerView;
    private AppDetailAdapter appDetailAdapter;
    private XmScrollBar xmScrollBar;
    //fragment切换监听
    private ISwitchFragmentListener mSwitchFragmentListener;
    private NewGuide newGuide;

    public static DetailFragment newInstance(DownLoadAppInfo downLoadAppInfo) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DOWNLOADAPPINFO, downLoadAppInfo);
        DetailFragment appDetailFragment = new DetailFragment();
        appDetailFragment.setArguments(bundle);

        return appDetailFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_app_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        downLoadAppInfo = (DownLoadAppInfo) getArguments().getSerializable(DOWNLOADAPPINFO);
        recyclerView = view.findViewById(R.id.app_detail_recycler_view);
        xmScrollBar = view.findViewById(R.id.scroll_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        appDetailAdapter = new AppDetailAdapter(getActivity());
        appDetailAdapter.setDownloadAppInfo(downLoadAppInfo);
        appDetailAdapter.setClickUninstallDialog(new AppDetailAdapter.UninstallListener() {
            @Override
            public void onClickUninstallDialog() {
                showUnInstallDialog();
            }
        });
        appDetailAdapter.setSwitchFragmentListener(new ISwitchFragmentListener() {
            @Override
            public void switchFragment(String tag) {
                if (mSwitchFragmentListener != null) {
                    mSwitchFragmentListener.switchFragment(tag);
                }
            }
        });
        recyclerView.setAdapter(appDetailAdapter);
        xmScrollBar.setRecyclerView(recyclerView);
        showGuideWindow();
    }

    public void setSwitchFragmentListener(ISwitchFragmentListener switchFragmentListener) {
        this.mSwitchFragmentListener = switchFragmentListener;
    }

    /**
     * 确认卸载弹框
     */
    private void showUnInstallDialog() {
        final ConfirmDialog unInstallDialog = new ConfirmDialog(getActivity());
        unInstallDialog.setContent(getString(R.string.tips_unintall_app, downLoadAppInfo.getAppInfo().getAppName()))
                .setPositiveButton(getString(R.string.sure), new View.OnClickListener() {
                    @Override
                    @NormalOnClick({EventConstants.NormalClick.uninstallSure})
                    @ResId({R.id.tv_sure})
                    public void onClick(View v) {
                        unInstallDialog.dismiss();
                        //静默卸载
                        List<String> path = new ArrayList<>();
                        path.add(downLoadAppInfo.getAppInfo().getPackageName());
                        SilentInstallManager.getInstance().unInstallApkFile(path);
                        //卸载中弹框
                        showProgressDialog(R.string.unintalling_app);
                        if (getActivity() != null) {
                            ((AppDetailsActivity) getActivity()).setPackageRemoveListener(DetailFragment.this);
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    @NormalOnClick({EventConstants.NormalClick.uninstallCancel})
                    @ResId({R.id.tv_cancel})
                    public void onClick(View v) {
                        unInstallDialog.dismiss();
                    }
                });
        unInstallDialog.show();
    }

    @Override
    public void packageRemove(boolean result) {
        dismissProgress();
    }

    private void showGuideWindow() {
        if (!GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.APPSTORE_SHOWED, GuideConstants.APPSTORE_GUIDE_FIRST, false))
            return;
        addRecycleViewScrollListener();
        Rect targetRect = new Rect(410, 0, 1920, 720);
        newGuide = NewGuide.with(getActivity())
                .setLebal(GuideConstants.APPSTORE_SHOWED)
                .setGuideLayoutId(R.layout.guide_view_app_detail)
                .setSupportScroll(true)
                .setViewSkipId(R.id.tv_guide_skip)
                .setTargetRect(targetRect)
                .build();
        newGuide.showGuide();
    }

    private void addRecycleViewScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE) {
                    dismissGuideWindow();
                    showLastGuide();
                }
            }
        });
    }

    private void showLastGuide() {
        if (!GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.APPSTORE_SHOWED, GuideConstants.APPSTORE_GUIDE_FIRST, false))
            return;
        NewGuide.with(getActivity())
                .setLebal(GuideConstants.APPSTORE_SHOWED)
                .build()
                .showLastGuide();
    }

    private void dismissGuideWindow() {
        if (newGuide != null) {
            newGuide.dismissGuideWindow();
            newGuide = null;
        }
    }
}
