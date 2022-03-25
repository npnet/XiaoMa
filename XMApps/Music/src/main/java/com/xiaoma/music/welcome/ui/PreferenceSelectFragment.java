package com.xiaoma.music.welcome.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.music.MainActivity;
import com.xiaoma.music.R;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.constant.MusicConstants;
import com.xiaoma.music.common.ui.MainFragment;
import com.xiaoma.music.welcome.adapter.PreferenceAdapter;
import com.xiaoma.music.welcome.model.PreferenceBean;
import com.xiaoma.music.welcome.view.GridSpaceItemDecoration;
import com.xiaoma.music.welcome.vm.PreferenceVM;
import com.xiaoma.ui.constract.ToastLevel;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.dialog.impl.XMCompatDialog;
import com.xiaoma.ui.dialog.impl.XMSmallTextDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author Jir
 * @date 2018/10/8
 */
@PageDescComponent(EventConstants.PageDescribe.preferenceSelectFragment)
public class PreferenceSelectFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = PreferenceSelectFragment.class.getSimpleName();
    public static final int SPAN_PREFERENCE = 8;

    private MainActivity mMainActivity;
    private PreferenceVM mPreferenceVM;

    private TextView mTitleTV;
    private TextView mSkipTV;
    private Button mSaveBtn;
    private RecyclerView mPreferenceTypesRV;
    private PreferenceAdapter mPreferenceAdapter;
    private XmScrollBar scrollBar;
    private ConstraintLayout mNetWorkView;
    private RelativeLayout mEmptyView;
    private XMSmallTextDialog mSmallDialog;

    public static PreferenceSelectFragment newInstance() {
        return new PreferenceSelectFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMainActivity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return super.onCreateWrapView(inflater.inflate(R.layout.fragment_preference, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
        bindView(view);
        initEvent();
        initVM();
    }

    private void bindView(View view) {
        mNetWorkView = view.findViewById(R.id.rl_preference_no_network);
        mEmptyView = view.findViewById(R.id.rl_preference_empty);
        mTitleTV = view.findViewById(R.id.tvTitle);
        mSkipTV = view.findViewById(R.id.tvSkip);
        mSaveBtn = view.findViewById(R.id.btnSave);
        mPreferenceTypesRV = view.findViewById(R.id.rvPreferenceTypes);
        scrollBar = view.findViewById(R.id.scroll_bar);

        view.findViewById(R.id.tv_retry).setVisibility(View.GONE);
    }

    private void initEvent() {
        mSaveBtn.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(((TextView) view).getText().toString(), "0");
            }

            @BusinessOnClick
            @Override
            public void onClick(View v) {
                setSaveBtn();
            }
        });
        mSkipTV.setOnClickListener(this);

        mPreferenceAdapter = new PreferenceAdapter();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, MusicConstants.MUSIC_HISTORY_GRID_NUM,
                LinearLayoutManager.HORIZONTAL, false);
        mPreferenceTypesRV.setLayoutManager(gridLayoutManager);
        GridSpaceItemDecoration gridSpaceItemDecoration = new GridSpaceItemDecoration(getActivity().getResources().getDimensionPixelSize(R.dimen.size_preference_top_margin), getActivity().getResources().getDimensionPixelSize(R.dimen.size_preference_right_margin));
        mPreferenceTypesRV.addItemDecoration(gridSpaceItemDecoration);

        mPreferenceTypesRV.setAdapter(mPreferenceAdapter);
        scrollBar.setRecyclerView(mPreferenceTypesRV);
        mPreferenceAdapter.setOnSelectIndexListener(new PreferenceAdapter.OnSelectIndexListener() {
            @Override
            public void onSelectChange(boolean enable) {
                mSaveBtn.setSelected(enable);
            }
        });
        mSaveBtn.setSelected(false);
//        mPreferenceAdapter.setOnItemClickListener(new PreferenceAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                mPreferenceAdapter.notifyItemChanged(position);
//            }
//        });
    }

    private void setSaveBtn() {
        if (mContext.getString(R.string.loading_retry).equals(mSaveBtn.getText())) {
            //重新加载
            mPreferenceVM.getPreferenceTypes();
            return;
        }
        Log.d(TAG, "onClick: save");
        ArrayList<PreferenceBean> selectedPreference = mPreferenceAdapter.getSelected();
        if (!NetworkUtils.isConnected(mContext)) {
            XMToast.toastException(mContext, R.string.net_error);
            return;
        }
        if (selectedPreference.isEmpty()) {
            XMToast.showToast(getActivity(), R.string.please_select);
        } else {
            mSmallDialog = XMCompatDialog.createSmallDialog();
            mSmallDialog.updateMsgAndState(ToastLevel.LOADING, R.string.preference_setting);
            mSmallDialog.showDialog(getFragmentManager());
            mPreferenceVM.settingPreference(selectedPreference);
        }
    }

    private void initVM() {
        mPreferenceVM = ViewModelProviders.of(getActivity()).get(PreferenceVM.class);
        mPreferenceVM.getPreferencesContainer().observe(this, new Observer<XmResource<List<PreferenceBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<PreferenceBean>> strings) {
                if (strings != null) {
                    strings.handle(new OnCallback<List<PreferenceBean>>() {
                        @Override
                        public void onSuccess(List<PreferenceBean> data) {
                            dismissProgress();
                            setContent();
                            if (data != null) {
                                mPreferenceAdapter.inflateDates(data);
                                checkEmptyView(data);
                            }
                        }

                        @Override
                        public void onError(int code, String message) {
                            dismissProgress();
                            setNoNet();
                        }

                        @Override
                        public void onFailure(String msg) {
                            dismissProgress();
                            setEmpty();
                        }
                    });
                }
            }
        });
        mPreferenceVM.getPreferenceTypes();
        mPreferenceVM.getPreferenceSettingFeedback().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if ((getString(R.string.setting_success)).equals(s)) {
                    if (mSmallDialog != null) {
                        mSmallDialog.updateMsgAndState(ToastLevel.SUCCESS, R.string.setting_success);
                    }
                    goToMainActivity();
                } else {
                    if (mSmallDialog != null) {
                        mSmallDialog.updateMsgAndState(ToastLevel.EXCEPTION, R.string.settting_failed);
                    }
                    goToMainActivity();
                }
                TPUtils.put(getActivity(), MusicConstants.TP_FIRST_START_APP, false);
            }

            private void goToMainActivity() {
                getUIHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mSmallDialog != null && mSmallDialog.isVisible()) {
                            mSmallDialog.dismissAllowingStateLoss();
                        }
                        mMainActivity.replaceContent(MainFragment.newInstance());
                    }
                }, 1000);
            }
        });
    }

    private void setEmpty() {
        mEmptyView.setVisibility(View.VISIBLE);
        mPreferenceTypesRV.setVisibility(View.GONE);
        mNetWorkView.setVisibility(View.GONE);
        mSaveBtn.setText(R.string.loading_retry);
        scrollBar.setVisibility(View.GONE);
    }

    protected void setNoNet() {
        mEmptyView.setVisibility(View.GONE);
        mPreferenceTypesRV.setVisibility(View.GONE);
        mNetWorkView.setVisibility(View.VISIBLE);
        mSaveBtn.setText(R.string.loading_retry);
        scrollBar.setVisibility(View.GONE);
    }

    protected void setContent() {
        mEmptyView.setVisibility(View.GONE);
        mPreferenceTypesRV.setVisibility(View.VISIBLE);
        mNetWorkView.setVisibility(View.GONE);
        mSaveBtn.setText(R.string.start_xting);
    }

    private void checkEmptyView(List<PreferenceBean> musicInfos) {
        if (musicInfos.isEmpty()) {
            scrollBar.setVisibility(View.INVISIBLE);
            setEmpty();
        } else {
            scrollBar.setVisibility(View.VISIBLE);
            showContentView();
        }
    }

    @NormalOnClick(EventConstants.NormalClick.preferenceSkip)
    @ResId(R.id.tvSkip)
    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.tvSkip) {
            Log.d(TAG, "onClick: skip");
            if (mPreferenceAdapter == null) {
                return;
            }
            if (mPreferenceAdapter.getSelected().isEmpty()) {
                TPUtils.put(getActivity(), MusicConstants.TP_FIRST_START_APP, false);
                ((MainActivity) getActivity()).replaceContent(MainFragment.newInstance());
            } else {
                ConfirmDialog confirmDialog = new ConfirmDialog(getActivity());
                confirmDialog.setContent(getString(R.string.hint_skip_music, mPreferenceAdapter.getSelected().size()))
                        .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmDialog.dismiss();
                            }
                        })
                        .setPositiveButton(getString(R.string.sure), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmDialog.dismiss();
                                TPUtils.put(getActivity(), MusicConstants.TP_FIRST_START_APP, false);
                                ((MainActivity) getActivity()).replaceContent(MainFragment.newInstance());
                            }
                        }).show();
            }
        }
    }
}
