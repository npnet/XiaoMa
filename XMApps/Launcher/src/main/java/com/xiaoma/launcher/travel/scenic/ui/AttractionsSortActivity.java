package com.xiaoma.launcher.travel.scenic.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.travel.scenic.adapter.AttractionsSortAdapter;
import com.xiaoma.launcher.travel.scenic.vm.AttractionsVM;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.trip.category.response.CategoryBean;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmDividerDecoration;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;

import org.simple.eventbus.EventBus;

import java.util.List;
@PageDescComponent(EventConstants.PageDescribe.attractionsActivitySortPagePathDesc)
public class AttractionsSortActivity extends BaseActivity {

    private RecyclerView mItemAttraction;
    private AttractionsVM mAttractionsVM;
    private AttractionsSortAdapter mAttractionsAdapter;
    private XmScrollBar xmScrollBar;
    public static final String NEARBY_PLAY = "周边游";
    private String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attraction_sort_activity);
        bindView();
        initView();
        initData();
        EventBus.getDefault().register(this);
    }

    private void bindView() {
        mItemAttraction = findViewById(R.id.attraction_sort_rv);
        xmScrollBar = findViewById(R.id.scroll_bar);
    }

    private void initView() {
        mAttractionsAdapter = new AttractionsSortAdapter();
        mItemAttraction.setLayoutManager(new GridLayoutManager(this, 2, LinearLayoutManager.HORIZONTAL, false));
        XmDividerDecoration decor = new XmDividerDecoration(this, DividerItemDecoration.HORIZONTAL);
        int horizontal = 94;
        int vertical = 80;
        int extra = 55;
        decor.setRect(0, vertical, horizontal, 0);
        decor.setExtraMargin(extra, 2);
        mItemAttraction.addItemDecoration(decor);

        mItemAttraction.setAdapter(mAttractionsAdapter);

        xmScrollBar.setRecyclerView(mItemAttraction);

    }

    private void initData() {
        type=getIntent().getStringExtra("type");
        mAttractionsVM = ViewModelProviders.of(this).get(AttractionsVM.class);
        mAttractionsVM.getAttractionsSort().observe(this, new Observer<XmResource<List<CategoryBean.SubcateBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<CategoryBean.SubcateBean>> listXmResource) {
                if (listXmResource == null) {
                    return;
                }
                listXmResource.handle(new OnCallback<List<CategoryBean.SubcateBean>>() {
                    @Override
                    public void onSuccess(List<CategoryBean.SubcateBean> data) {
                        if (!ListUtils.isEmpty(data)) {
                            mAttractionsAdapter.setNewData(data);
                        } else {
                            showEmptyView();
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        super.onFailure(msg);
                    }

                    @Override
                    public void onError(int code, String message) {
                        super.onError(code, message);
                        if (code == -1) {
                            showToastException(R.string.net_work_error);
                        } else {
                            XMToast.showToast(AttractionsSortActivity.this, message);
                        }
                    }
                });
            }
        });

        if (!NetworkUtils.isConnected(this)) {
            showNoNetView();
            return;
        }

        mAttractionsVM.queryAttractiousSort(type,NEARBY_PLAY);
    }
    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        if (NetworkUtils.isConnected(this)) {
            mAttractionsAdapter.getData().clear();
            mAttractionsVM.queryAttractiousSort(type,NEARBY_PLAY);
        }
    }
}
