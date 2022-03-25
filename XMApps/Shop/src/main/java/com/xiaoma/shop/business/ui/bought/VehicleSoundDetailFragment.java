package com.xiaoma.shop.business.ui.bought;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.adapter.bought.BoughtVehicleSoundDetailAdapter;
import com.xiaoma.shop.business.ui.view.ProgressButton;
import com.xiaoma.shop.business.vm.ChildThemeVm;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/05/28
 * @Describe:
 */

public class VehicleSoundDetailFragment extends BaseFragment {

    private TextView mTvTitle;
    private RecyclerView mRv;
    private BoughtVehicleSoundDetailAdapter mAdapter;
    private ProgressButton mPb;

    public static VehicleSoundDetailFragment newInstance() {
        VehicleSoundDetailFragment detailFragment = new VehicleSoundDetailFragment();
        return detailFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vehicle_sound_detail, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initEvent();
    }


    private void initView(View view) {
        mTvTitle = view.findViewById(R.id.tv_title);
        mPb = view.findViewById(R.id.bt_center);

        mRv = view.findViewById(R.id.rv);
        mAdapter = new BoughtVehicleSoundDetailAdapter(ImageLoader.with(this));
        mRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 90, 0);
            }
        });
        mRv.setAdapter(mAdapter);
        mAdapter.setNewData(ViewModelProviders.of(this).get(ChildThemeVm.class).getMockData());
    }

    private void initEvent() {
        mPb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
