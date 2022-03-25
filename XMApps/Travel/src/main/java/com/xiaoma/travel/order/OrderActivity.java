package com.xiaoma.travel.order;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.travel.R;
import com.xiaoma.travel.main.model.OrderBean;

public class OrderActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private OrderAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initView();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mAdapter = new OrderAdapter();
        mRecyclerView.setAdapter(mAdapter);

    }

    class OrderAdapter extends BaseQuickAdapter<OrderBean, BaseViewHolder> {

        public OrderAdapter() {
            super(R.layout.item_order);
        }

        @Override
        protected void convert(BaseViewHolder helper, OrderBean item) {

        }
    }
}
