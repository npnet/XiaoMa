package com.xiaoma.launcher.service.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.service.adapter.TravelAdapter;
import com.xiaoma.launcher.service.model.ServiceBean;
import com.xiaoma.launcher.service.vm.TravelServiceFragmentVM;
import com.xiaoma.launcher.travel.delicious.ui.DeliciousActivity;
import com.xiaoma.launcher.travel.delicious.ui.DeliciousSortActivity;
import com.xiaoma.launcher.travel.film.ui.FilmActivity;
import com.xiaoma.launcher.travel.hotel.ui.RecomHotelActivity;
import com.xiaoma.launcher.travel.parking.ui.ParkingActivity;
import com.xiaoma.launcher.travel.scenic.ui.AttractionsSortActivity;
import com.xiaoma.model.XmResource;
import com.xiaoma.ui.view.XmDividerDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author taojin
 * @date 2019/1/10
 */
public class TravelServiceFragment extends BaseFragment {

    private RecyclerView mRvTravel;
    private TravelServiceFragmentVM mTravelServiceFragmentVM;
    private TravelAdapter mTravelAdapter;
    private List<ServiceBean> mServiceBeans = new ArrayList<>();


    public static TravelServiceFragment newInstance() {
        return new TravelServiceFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateWrapView(inflater.inflate(R.layout.fragment_service_travel, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        initView();
        initData();
    }


    private void bindView(View view) {
        mRvTravel = view.findViewById(R.id.travel_rv);
    }

    private void initView() {

        mTravelAdapter = new TravelAdapter(R.layout.item_travel, mServiceBeans);
        mRvTravel.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        XmDividerDecoration decor = new XmDividerDecoration(mContext, DividerItemDecoration.HORIZONTAL);
        int horizontal = mContext.getResources().getDimensionPixelOffset(R.dimen.rv_travel_divider_horizontal);
        int extra = mContext.getResources().getDimensionPixelOffset(R.dimen.rv_travel_divider_extra);
        decor.setRect(horizontal, 0, horizontal, 0);
        decor.setExtraMargin(extra);
        mRvTravel.addItemDecoration(decor);

        mTravelAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if ("美食".equals(mServiceBeans.get(position).getServiceName())){
                    startActivity(new Intent(mContext, DeliciousSortActivity.class));
                }else  if ("酒店".equals(mServiceBeans.get(position).getServiceName())){
                    startActivity(new Intent(mContext, RecomHotelActivity.class));
                }else  if ("景点".equals(mServiceBeans.get(position).getServiceName())){
                    startActivity(new Intent(mContext, AttractionsSortActivity.class));
                }else  if ("电影".equals(mServiceBeans.get(position).getServiceName())){
                    startActivity(new Intent(mContext, FilmActivity.class));
                }else  if ("停车".equals(mServiceBeans.get(position).getServiceName())){
                    startActivity(new Intent(mContext, ParkingActivity.class));
                }

            }
        });
        mRvTravel.setAdapter(mTravelAdapter);

    }

    private void initData() {

        mTravelServiceFragmentVM = ViewModelProviders.of(this).get(TravelServiceFragmentVM.class);
        mTravelServiceFragmentVM.getmTravels().observe(this, new Observer<XmResource<List<ServiceBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<ServiceBean>> listXmResource) {
                listXmResource.handle(new OnCallback<List<ServiceBean>>() {
                    @Override
                    public void onSuccess(List<ServiceBean> data) {
                        mServiceBeans.clear();
                        mServiceBeans.addAll(data);
                        mTravelAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        mTravelServiceFragmentVM.fetchTravels();
    }

}
