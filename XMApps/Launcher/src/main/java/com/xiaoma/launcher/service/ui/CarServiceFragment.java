package com.xiaoma.launcher.service.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.service.adapter.CarAdapter;
import com.xiaoma.launcher.service.model.ServiceBean;
import com.xiaoma.launcher.service.vm.CarServiceFragmentVM;
import com.xiaoma.model.XmResource;
import com.xiaoma.ui.view.XmDividerDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author taojin
 * @date 2019/1/10
 */
public class CarServiceFragment extends BaseFragment {

    private RecyclerView mRvCar;
    private CarServiceFragmentVM mCarServiceFragmentVM;
    private CarAdapter mCarAdapter;
    private List<ServiceBean> mServiceBeans = new ArrayList<>();


    public static CarServiceFragment newInstance() {
        return new CarServiceFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateWrapView(inflater.inflate(R.layout.fragment_service_car, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        initView();
        initData();
    }


    private void bindView(View view) {
        mRvCar = view.findViewById(R.id.car_rv);
    }

    private void initView() {

        mCarAdapter = new CarAdapter(R.layout.item_car, mServiceBeans);
        mRvCar.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        XmDividerDecoration decor = new XmDividerDecoration(mContext, DividerItemDecoration.HORIZONTAL);
        int horizontal = mContext.getResources().getDimensionPixelOffset(R.dimen.rv_car_divider_horizontal);
        int extra = mContext.getResources().getDimensionPixelOffset(R.dimen.rv_car_divider_extra);
        decor.setRect(horizontal, 0, horizontal, 0);
        decor.setExtraMargin(extra);
        mRvCar.addItemDecoration(decor);

        mRvCar.setAdapter(mCarAdapter);
    }

    private void initData() {

        mCarServiceFragmentVM = ViewModelProviders.of(this).get(CarServiceFragmentVM.class);
        mCarServiceFragmentVM.getmTravels().observe(this, new Observer<XmResource<List<ServiceBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<ServiceBean>> listXmResource) {
                listXmResource.handle(new OnCallback<List<ServiceBean>>() {
                    @Override
                    public void onSuccess(List<ServiceBean> data) {
                        mServiceBeans.clear();
                        mServiceBeans.addAll(data);
                        mCarAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        mCarServiceFragmentVM.fetchCars();
    }
}
