package com.xiaoma.service.main.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.service.R;
import com.xiaoma.service.common.constant.EventConstants;
import com.xiaoma.service.common.manager.CarDataManager;
import com.xiaoma.service.main.adapter.MaintainAdapter;
import com.xiaoma.service.main.model.MaintainBean;
import com.xiaoma.service.main.vm.MaintainDetailVM;
import com.xiaoma.service.order.ui.OrderActivity;
import com.xiaoma.ui.view.XmDividerDecoration;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZouShao on 2018/11/16 0016.
 */
@PageDescComponent(EventConstants.PageDescribe.maintaninDetailPagePathDesc)
public class MaintainListActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private XmScrollBar xmScrollBar;
    private MaintainAdapter adapter;
    private MaintainDetailVM maintainDetailVM;
    private List<MaintainBean> maintainBeanList;
    private TextView tvMaintainNumber;
    private Button btnMaintain;
    public static final int spanCount = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain);
        initView();
        initData();
    }

    private void initData() {
        maintainDetailVM = ViewModelProviders.of(this).get(MaintainDetailVM.class);
        maintainDetailVM.getMaintainList().observe(this, new Observer<XmResource<List<MaintainBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<MaintainBean>> listXmResource) {
                if (listXmResource == null) {
                    return;
                }
                listXmResource.handle(new OnCallback<List<MaintainBean>>() {
                    @Override
                    public void onSuccess(List<MaintainBean> data) {
                        //数据为空处理
                        if (ListUtils.isEmpty(data)) {
                            tvMaintainNumber.setVisibility(View.GONE);
                            btnMaintain.setVisibility(View.GONE);
                            showEmptyView();
                            return;
                        }
                        tvMaintainNumber.setVisibility(View.VISIBLE);
                        btnMaintain.setVisibility(View.VISIBLE);
                        maintainBeanList.addAll(data);
                        adapter.setDatas(maintainBeanList);
                        //设置部分字帖加粗
                        SpannableString spanString = new SpannableString(String.format(getString(R.string.maintain_number), data.size()));
                        //设置颜色
                        StyleSpan span = new StyleSpan(Typeface.BOLD);
                        if (data.size() >= 10) {
                            spanString.setSpan(span, 3, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spanString.setSpan(new ForegroundColorSpan(Color.parseColor("#ffcd91")), 3, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } else {
                            spanString.setSpan(span, 3, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spanString.setSpan(new ForegroundColorSpan(Color.parseColor("#ffcd91")), 3, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }

                        tvMaintainNumber.setText(spanString);
                    }
                });
            }
        });

        maintainDetailVM.fetchMaintainList(CarDataManager.getInstance().getVinInfo());
    }

    private void initView() {
        maintainBeanList = new ArrayList<>();
        tvMaintainNumber = findViewById(R.id.tv_maintain_number);
        recyclerView = findViewById(R.id.recycler_view);
        xmScrollBar = findViewById(R.id.maintain_scroll_bar);
        btnMaintain = findViewById(R.id.btn_to_maintain);
        btnMaintain.setOnClickListener(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2, LinearLayoutManager.HORIZONTAL, false));
        XmDividerDecoration decoration = new XmDividerDecoration(this, DividerItemDecoration.HORIZONTAL);
        int horizontal = getResources().getDimensionPixelOffset(R.dimen.maintain_list_horizontal);
        int left = getResources().getDimensionPixelOffset(R.dimen.maintain_list_left);
        int right = getResources().getDimensionPixelOffset(R.dimen.maintain_list_right);
        int extra = getResources().getDimensionPixelOffset(R.dimen.maintain_list_extra);
        decoration.setRect(left, 0, right, horizontal);
        decoration.setExtraMargin(extra, spanCount);
        recyclerView.addItemDecoration(decoration);
        adapter = new MaintainAdapter(MaintainListActivity.this, maintainBeanList, R.layout.item_maintain);
        recyclerView.setAdapter(adapter);
        xmScrollBar.setRecyclerView(recyclerView);
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        maintainDetailVM.fetchMaintainList(CarDataManager.getInstance().getVinInfo());
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.carReservation})
    @ResId({R.id.btn_to_maintain})
    public void onClick(View view) {
        startActivity(OrderActivity.class);
    }
}
