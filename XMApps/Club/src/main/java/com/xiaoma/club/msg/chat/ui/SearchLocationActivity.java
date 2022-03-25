//package com.xiaoma.club.msg.chat.ui;
//
//import android.arch.lifecycle.Observer;
//import android.arch.lifecycle.ViewModelProviders;
//import android.content.Intent;
//import android.graphics.Rect;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.Editable;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.amap.api.services.help.Inputtips;
//import com.amap.api.services.help.InputtipsQuery;
//import com.amap.api.services.help.Tip;
//import com.xiaoma.club.R;
//import com.xiaoma.club.common.util.LogUtil;
//import com.xiaoma.club.common.viewmodel.ViewState;
//import com.xiaoma.club.msg.chat.controller.SearchLocationTipAdapter;
//import com.xiaoma.club.msg.chat.vm.SearchLocationVM;
//import com.xiaoma.component.base.BaseActivity;
//import com.xiaoma.utils.NetworkUtils;
//
//import java.util.List;
//
///**
// * Created by LKF on 2019-1-9 0009.
// */
//public class SearchLocationActivity extends BaseActivity implements View.OnClickListener, TextWatcher {
//    private static final String TAG = "SearchLocationActivity";
//    public static final String RESULT_LOCATION_TIP = "locationTip";
//    private EditText mEtKeyword;
//    private Button mBtnSearch;
//    private RecyclerView mRvPoiList;
//    private TextView mTvState;
//
//    private SearchLocationVM mVM;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.act_search_location);
//        mEtKeyword = findViewById(R.id.et_keyword);
//        mEtKeyword.addTextChangedListener(this);
//
//        mBtnSearch = findViewById(R.id.btn_search);
//        mBtnSearch.setOnClickListener(this);
//
//        mRvPoiList = findViewById(R.id.rv_poi_list);
//        mRvPoiList.setLayoutManager(new LinearLayoutManager(this));
//        mRvPoiList.setHasFixedSize(true);
//        mRvPoiList.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//                outRect.set(0, 15, 0, 15);
//            }
//        });
//        mTvState = findViewById(R.id.tv_state);
//
//        initVM();
//    }
//
//    private void initVM() {
//        mVM = ViewModelProviders.of(this).get(SearchLocationVM.class);
//        mVM.getKeyword().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable final String keyword) {
//                LogUtil.logI(TAG, "onChanged( keyword: %s )", keyword);
//                // 先清除当前列表
//                mRvPoiList.swapAdapter(null, true);
//                if (!NetworkUtils.isConnected(SearchLocationActivity.this)) {
//                    mVM.getViewState().setValue(ViewState.NETWORK_ERROR);
//                    return;
//                }
//                if (!TextUtils.isEmpty(keyword)) {
//                    mVM.getViewState().setValue(ViewState.LOADING_INITIAL);
//                    //第二个参数传入null或者""代表在全国进行检索，否则按照传入的city进行检索
//                    final InputtipsQuery query = new InputtipsQuery(keyword, "");
//                    query.setCityLimit(true);//是否限制在当前城市
//                    final Inputtips inputTips = new Inputtips(SearchLocationActivity.this, query);
//                    inputTips.setInputtipsListener(new Inputtips.InputtipsListener() {
//                        @Override
//                        public void onGetInputtips(List<Tip> inputTips, int resultID) {
//                            LogUtil.logI(TAG, "onGetInputtips( inputTips: %s, resultID: %s )", inputTips, resultID);
//                            // Activity销毁或者关键字变了之后,不更新Tips
//                            if (isDestroy() ||
//                                    !keyword.equals(mEtKeyword.getText().toString()))
//                                return;
//                            mRvPoiList.swapAdapter(new SearchLocationTipAdapter(inputTips, new SearchLocationTipAdapter.Callback() {
//                                @Override
//                                public void onTipSelect(Tip tip) {
//                                    setResult(RESULT_OK, new Intent().putExtra(RESULT_LOCATION_TIP, tip));
//                                    finish();
//                                }
//                            }), false);
//                            if (inputTips == null || inputTips.isEmpty()) {
//                                mVM.getViewState().setValue(ViewState.EMPTY_DATA);
//                            } else {
//                                mVM.getViewState().setValue(ViewState.NORMAL);
//                            }
//                        }
//                    });
//                    inputTips.requestInputtipsAsyn();
//                } else {
//                    mVM.getViewState().setValue(ViewState.NORMAL);
//                }
//            }
//        });
//        mVM.getSearchEnable().observe(this, new Observer<Boolean>() {
//            @Override
//            public void onChanged(@Nullable Boolean enable) {
//                mBtnSearch.setEnabled(enable != null && enable);
//            }
//        });
//        mVM.getViewState().observe(this, new Observer<ViewState>() {
//            @Override
//            public void onChanged(@Nullable ViewState state) {
//                if (state == null)
//                    return;
//                // TODO: 临时显示
//                switch (state) {
//                    case LOADING_INITIAL:
//                        mRvPoiList.setVisibility(View.GONE);
//                        mTvState.setVisibility(View.VISIBLE);
//                        mTvState.setText("Loading...");
//                        break;
//                    case NORMAL:
//                        mRvPoiList.setVisibility(View.VISIBLE);
//                        mTvState.setVisibility(View.GONE);
//                        break;
//                    case EMPTY_DATA:
//                        mRvPoiList.setVisibility(View.GONE);
//                        mTvState.setVisibility(View.VISIBLE);
//                        mTvState.setText("Empty data !");
//                        break;
//                    case NETWORK_ERROR:
//                        mRvPoiList.setVisibility(View.GONE);
//                        mTvState.setVisibility(View.VISIBLE);
//                        mTvState.setText(R.string.net_work_error);
//                        break;
//                }
//            }
//        });
//        mVM.getViewState().setValue(ViewState.NORMAL);
//        // 设置初始化值
//        mVM.getKeyword().setValue("");
//        mVM.getSearchEnable().setValue(false);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_search:
//                mVM.getKeyword().setValue(mEtKeyword.getText().toString());
//                break;
//        }
//    }
//
//    @Override
//    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//    }
//
//    @Override
//    public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//    }
//
//    @Override
//    public void afterTextChanged(Editable s) {
//        // 2019-1-9 17:22:14: 产品(路杨)确定不需要自动联想
//        mVM.getSearchEnable().setValue(s.length() > 0);
//    }
//}