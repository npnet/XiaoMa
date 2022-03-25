package com.xiaoma.club.msg.chat.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.amap.api.location.AMapLocation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.xiaoma.club.R;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.club.common.view.ClubSearchVoiceView;
import com.xiaoma.club.common.viewmodel.ViewState;
import com.xiaoma.club.msg.chat.controller.SearchLocationTipAdapter;
import com.xiaoma.club.msg.chat.vm.SearchLocationVM;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.NetworkUtils;

import java.util.List;

/**
 * Created by LKF on 2019-1-9 0009.
 */
public class SearchLocationFragment extends BaseFragment implements View.OnClickListener {
    public static final String EXTRA_LATLONPOINT = "latLonPoint";

    private static final String TAG = SearchLocationFragment.class.getSimpleName();
    private ClubSearchVoiceView searchVoiceView;
    private RecyclerView mRvPoiList;
    private XmScrollBar mScrollBar;
    private SearchLocationVM mVM;
    private Inputtips.InputtipsListener mCurrSearchListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.act_search_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchVoiceView = view.findViewById(R.id.search_poi_voice_view);
        searchVoiceView.setOnSearchClickListener(this);
        searchVoiceView.setHint(getString(R.string.input_address));
        searchVoiceView.requestEtFocus();

        mRvPoiList = view.findViewById(R.id.rv_poi_list);
        mRvPoiList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvPoiList.setHasFixedSize(true);
        mRvPoiList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 15, 0, 15);
            }
        });
        mScrollBar = view.findViewById(R.id.scroll_bar);

        initVM();
    }

    private void initVM() {
        mVM = ViewModelProviders.of(this).get(SearchLocationVM.class);
        mVM.getKeyword().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String keyword) {
                LogUtil.logI(TAG, "onChanged( keyword: %s )", keyword);
                try {
                    ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).
                            hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception e) {
                }
                // 先清除当前列表
                mRvPoiList.swapAdapter(null, true);
                if (!NetworkUtils.isConnected(getContext())) {
                    mVM.getViewState().setValue(ViewState.NETWORK_ERROR);
                    return;
                }
                if (!TextUtils.isEmpty(keyword)) {
                    mVM.getViewState().setValue(ViewState.LOADING_INITIAL);

                    LatLonPoint location = null;
                    Bundle args;
                    if ((args = getArguments()) != null) {
                        AMapLocation aMapLocation = args.getParcelable(EXTRA_LATLONPOINT);
                        if (aMapLocation != null) {
                            location = new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                        }
                    }
                    //第二个参数传入null或者""代表在全国进行检索，否则按照传入的city进行检索
                    final InputtipsQuery query = new InputtipsQuery(keyword, "");
                    if (location != null) {
                        query.setLocation(location);
                    }
                    query.setCityLimit(false);//是否限制在当前城市
                    final Inputtips inputTips = new Inputtips(getContext(), query);
                    inputTips.setInputtipsListener(mCurrSearchListener = new Inputtips.InputtipsListener() {
                        @Override
                        public void onGetInputtips(List<Tip> inputTips, int resultID) {
                            LogUtil.logI(TAG, "onGetInputtips( inputTips: %s, resultID: %s )", inputTips, resultID);
                            // 销毁或者搜索监听变了之后,不更新Tips
                            if (isDestroy() || this != mCurrSearchListener)
                                return;
                            mRvPoiList.swapAdapter(new SearchLocationTipAdapter(inputTips, new SearchLocationTipAdapter.Callback() {
                                @Override
                                public void onTipSelect(Tip tip) {
                                    if (mCallback != null) {
                                        mCallback.onTipSelect(tip);
                                    }
                                }
                            }), false);
                            mScrollBar.setRecyclerView(mRvPoiList);
                            if (inputTips == null || inputTips.isEmpty()) {
                                mVM.getViewState().setValue(ViewState.EMPTY_DATA);
                            } else {
                                mVM.getViewState().setValue(ViewState.NORMAL);
                            }
                        }
                    });
                    inputTips.requestInputtipsAsyn();
                } else {
                    mVM.getViewState().setValue(ViewState.NORMAL);
                }
            }
        });
        mVM.getViewState().observe(this, new Observer<ViewState>() {
            @Override
            public void onChanged(@Nullable ViewState state) {
                if (state == null)
                    return;
                Fragment fmt = null;
                switch (state) {
                    case LOADING_INITIAL:
                        fmt = new StateLoadingFragment();
                        break;
                    case EMPTY_DATA:
                        fmt = new StateDataEmptyFragment();
                        break;
                    case NETWORK_ERROR:
                        fmt = new StateNoNetworkFragment();
                        ((StateNoNetworkFragment) fmt).setCallback(new StateNoNetworkFragment.Callback() {
                            @Override
                            public void onReload() {
                                mVM.getKeyword().setValue(searchVoiceView.getText().trim());
                            }
                        });
                        break;
                }
                if (fmt != null) {
                    mRvPoiList.setVisibility(View.GONE);
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.fmt_state, fmt)
                            .commitNow();
                } else {
                    mRvPoiList.setVisibility(View.VISIBLE);
                    final Fragment exist = getChildFragmentManager().findFragmentById(R.id.fmt_state);
                    if (exist != null) {
                        getChildFragmentManager().beginTransaction()
                                .remove(exist)
                                .commitNow();
                    }
                }
            }
        });
        mVM.getViewState().setValue(ViewState.NORMAL);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.discovery_search_now_btn:
                mVM.getKeyword().setValue(searchVoiceView.getText().trim());
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (searchVoiceView != null) {
            searchVoiceView.initVoiceEngine(getContext());
            searchVoiceView.registerIatListener();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (searchVoiceView != null) {
            searchVoiceView.unregisterIatListener();
            searchVoiceView.release(false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            searchVoiceView.setNormal();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            if (searchVoiceView != null) {
                searchVoiceView.registerIatListener();
            }
        } else {
            if (searchVoiceView != null) {
                searchVoiceView.unregisterIatListener();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (searchVoiceView != null) {
            searchVoiceView.release(true);
        }
    }

    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onTipSelect(Tip tip);
    }
}