package com.xiaoma.assistant.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.GasPriceBean;
import com.xiaoma.assistant.ui.adapter.GasPriceDetailAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lai on 2019/1/15 0016.
 */

public class GasView extends FrameLayout {

    private TextView mTitle;
    private RecyclerView rv_price;
    private List<GasPriceBean.GasPrice> list = new ArrayList<>();
    private GasPriceDetailAdapter gasPriceDetailAdapter;

    public GasView(@NonNull Context context) {
        super(context);
        initView();
    }

    public GasView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public GasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_assistant_gas, this);
        mTitle = findViewById(R.id.tv_title);
        /*mGas97 = findViewById(R.id.tv_gas_97);
        mGas93 = findViewById(R.id.tv_gas_93);
        mGas90 = findViewById(R.id.tv_gas_90);
        mGas0 = findViewById(R.id.tv_gas_0);*/
        rv_price = findViewById(R.id.rv_price);

        initAdapter();
    }

    private void initAdapter() {
        gasPriceDetailAdapter = new GasPriceDetailAdapter(getContext(), list);
        rv_price.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false));
        rv_price.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getLayoutManager().getPosition(view);
                if(position%2==1){
                    outRect.set(110, 0, 0, 15);
                }else{
                    outRect.set(110, 0, 10, 15);
                }

            }
        });
        rv_price.setAdapter(gasPriceDetailAdapter);
    }

    public void setData(List<GasPriceBean.GasPrice> list) {
        this.list.clear();
        this.list.addAll(list);
        gasPriceDetailAdapter.notifyDataSetChanged();
    }

}
