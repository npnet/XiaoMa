package com.xiaoma.xting.welcome.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.ui.StateControl.OnRetryClickListener;
import com.xiaoma.ui.StateControl.Type;
import com.xiaoma.ui.constract.ToastLevel;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.dialog.impl.XMCompatDialog;
import com.xiaoma.ui.dialog.impl.XMSmallTextDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.ResUtils;
import com.xiaoma.utils.tputils.TPUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.welcome.GridSpaceItemDecoration;
import com.xiaoma.xting.welcome.PreferenceAdapter;
import com.xiaoma.xting.welcome.consract.FirstInAppStatus;
import com.xiaoma.xting.welcome.model.PreferenceBean;
import com.xiaoma.xting.welcome.ui.SplashActivity;
import com.xiaoma.xting.welcome.vm.PreferenceVM;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author Jir
 * @date 2018/10/8
 */
@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_PREFERENCE_SELECT)
public class PreferenceSelectFragment extends BaseFragment implements View.OnClickListener, OnRetryClickListener {

    public static final String TAG = PreferenceSelectFragment.class.getSimpleName();
    public static final int SPAN_PREFERENCE = 2;

    private PreferenceVM mPreferenceVM;

    private TextView mSkipTV;
    private RecyclerView mPreferenceTypesRV;
    private PreferenceAdapter mPreferenceAdapter;
    private XmScrollBar mScrollBar;
    private TextView mStartTV;
    private XMSmallTextDialog mSmallDialog;

    public static PreferenceSelectFragment newInstance() {
        return new PreferenceSelectFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preference, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        initEvent();
        afterBindView();
        checkNetWork();
    }

    private void afterBindView() {
        mPreferenceVM = ViewModelProviders.of(this).get(PreferenceVM.class);
        mPreferenceVM.getPreferencesContainer().observeForever(new Observer<XmResource<List<PreferenceBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<PreferenceBean>> listXmResource) {
                listXmResource.handle(new OnCallback<List<PreferenceBean>>() {
                    @Override
                    public void onSuccess(List<PreferenceBean> preferenceTags) {
                        dismissProgress();
                        mStateView.setVisibility(View.GONE);
                        mPreferenceTypesRV.setVisibility(View.VISIBLE);
                        mStartTV.setVisibility(View.VISIBLE);

                        mStartTV.setSelected(false);
                        mPreferenceAdapter.setNewData(preferenceTags);
                    }

                    @Override
                    public void onFailure(String msg) {
                        showView(Type.EEMPTY);
                        super.onFailure(msg);
                    }

                    @Override
                    public void onError(int code, String message) {
                        showView(Type.NONETWORK);
                        XMToast.toastException(getContext(), ResUtils.getString(getContext(), R.string.net_not_connect));
                        super.onError(code, message);
                    }
                });
            }
        });

        mPreferenceVM.getPreferenceSettingFeedback().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if ((getString(R.string.setting_success)).equals(s)) {
                    if (mSmallDialog != null) {
                        mSmallDialog.updateMsgAndState(ToastLevel.SUCCESS, R.string.setting_success);
                    }
                    getUIHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mSmallDialog != null && mSmallDialog.isVisible()) {
                                mSmallDialog.dismissAllowingStateLoss();
                            }
                            SplashActivity.goToMainPage(mActivity, FirstInAppStatus.FM_PAGE_FIRST);
                        }
                    }, 1000);
                } else {
                    if (mSmallDialog != null) {
                        mSmallDialog.updateMsgAndState(ToastLevel.EXCEPTION, R.string.settting_failed);
                    }
                    TPUtils.putList(getActivity(), XtingConstants.TP_XTING_SELECT_TAGS, mPreferenceAdapter.getSelected());
                    getUIHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mSmallDialog != null && mSmallDialog.isVisible()) {
                                mSmallDialog.dismissAllowingStateLoss();
                            }

                            SplashActivity.goToMainPage(mActivity, FirstInAppStatus.AUTO_UPDATE_PREFERENCE);
                        }
                    }, 1000);
                }
            }
        });
    }

    private void bindView(View view) {
        mSkipTV = view.findViewById(R.id.tvSkip);
        mStartTV = view.findViewById(R.id.tvStart);
        mScrollBar = view.findViewById(R.id.scroll_bar);
        mPreferenceTypesRV = view.findViewById(R.id.rvPreferenceTypes);
        mStateView = view.findViewById(R.id.stateView);
    }

    private void initEvent() {
        mSkipTV.setOnClickListener(this);
        mStartTV.setOnClickListener(this);
        mStateView.setOnRetryClickListener(this);
        mPreferenceAdapter = new PreferenceAdapter();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, SPAN_PREFERENCE, LinearLayoutManager.HORIZONTAL, false);
        mPreferenceTypesRV.setLayoutManager(gridLayoutManager);
        GridSpaceItemDecoration gridSpaceItemDecoration = new GridSpaceItemDecoration(SPAN_PREFERENCE);

        gridSpaceItemDecoration.setDivider(mContext.getResources().getDimensionPixelOffset(R.dimen.size_preference_margin_left),
                mContext.getResources().getDimensionPixelOffset(R.dimen.size_preference_margin_middle),
                mContext.getResources().getDimensionPixelOffset(R.dimen.size_preference_margin_top));
        mPreferenceTypesRV.addItemDecoration(gridSpaceItemDecoration);

        mPreferenceTypesRV.setAdapter(mPreferenceAdapter);
        mScrollBar.setRecyclerView(mPreferenceTypesRV);

        mPreferenceAdapter.setOnSelectIndexListener(new PreferenceAdapter.OnSelectIndexListener() {
            @Override
            public void onSelectChange(boolean enable) {
                mStartTV.setSelected(enable);
            }
        });
    }

    private void checkNetWork() {
        if (NetworkUtils.isConnected(getContext())) {
            initView();
        } else {
            initNoNetwork();
        }
    }

    private void initNoNetwork() {
        mPreferenceTypesRV.setVisibility(View.GONE);
        mStartTV.setVisibility(View.GONE);

        showView(Type.NONETWORK);
    }

    private void initView() {
        mStartTV.setVisibility(View.GONE);
        mPreferenceVM.fetchPreferenceTags();
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.ACTION_START_APP, EventConstants.NormalClick.ACTION_SKIP_SET})
    @ResId({R.id.tvStart, R.id.tvSkip})
    public void onClick(View v) {
        int vId = v.getId();
        ArrayList<PreferenceBean> selectedPreference = mPreferenceAdapter.getSelected();
        if (vId == R.id.tvStart) {
            saveSettingPreference(selectedPreference);
        } else if (vId == R.id.tvSkip) {
            skipSettingPreference(selectedPreference);
        }
    }

    @Override
    public void onRetryClick(View view, Type type) {
        manualUpdateTrack(EventConstants.NormalClick.ACTION_NET_NONE_RETRY);
        checkNetWork();
    }

    private void saveSettingPreference(final ArrayList<PreferenceBean> selectedPreference) {
        if (selectedPreference.isEmpty()) {
            showToast(getString(R.string.hint_for_start_xting_with_no_tag));
        } else {
            mSmallDialog = XMCompatDialog.createSmallDialog();
            mSmallDialog.updateMsgAndState(ToastLevel.LOADING, R.string.preference_setting);
            mSmallDialog.showDialog(getFragmentManager());
            mPreferenceVM.sendSelectedPreferences(selectedPreference);

        }
    }

    private void skipSettingPreference(ArrayList<PreferenceBean> selectedPreference) {
        if (selectedPreference.isEmpty()) {
            SplashActivity.goToMainPage(mActivity, FirstInAppStatus.FM_PAGE_FIRST);
        } else {
            ConfirmDialog confirmDialog = new ConfirmDialog(getActivity());
            confirmDialog.setContent(getString(R.string.hint_skip, selectedPreference.size()))
                    .setNegativeButton(getString(R.string.no), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            manualUpdateTrack(EventConstants.NormalClick.ACTION_CANCEL_SKIP_SET);
                            confirmDialog.dismiss();
                        }
                    })
                    .setPositiveButton(getString(R.string.yes), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmDialog.dismiss();
                            manualUpdateTrack(EventConstants.NormalClick.ACTION_CONFIRM_SKIP_SET);
                            SplashActivity.goToMainPage(mActivity, FirstInAppStatus.FM_PAGE_FIRST);
                        }
                    }).show();
        }
    }

    @Override
    public void onDestroyView() {
        mPreferenceVM.getPreferencesContainer().removeObservers(this);
        super.onDestroyView();

    }

    private void showView(Type type) {
        mStateView.setVisibility(View.VISIBLE);
//        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mStateView.getLayoutParams();
        switch (type) {
            case EEMPTY:
                //如果需要微调 就在这里设置
//                layoutParams.bottomMargin = 60;
                showEmptyView();
                break;
            case NONETWORK:
//                layoutParams.bottomMargin = 0;
                showNoNetView();
                break;
        }
    }

    private void manualUpdateTrack(String pagePathDesc) {
        XmAutoTracker.getInstance().onEvent(
                EventConstants.PageDescribe.FRAGMENT_PREFERENCE_SELECT,
                PreferenceSelectFragment.this.getClass().getName(),
                pagePathDesc);
    }
}
