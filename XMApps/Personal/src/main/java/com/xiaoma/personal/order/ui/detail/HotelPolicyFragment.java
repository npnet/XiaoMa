package com.xiaoma.personal.order.ui.detail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.personal.R;
import com.xiaoma.personal.common.RequestManager;
import com.xiaoma.personal.common.util.EventConstants;
import com.xiaoma.personal.order.constants.Constants;
import com.xiaoma.personal.order.model.HotelPolicy;
import com.xiaoma.personal.order.model.OrderInfo;
import com.xiaoma.ui.progress.loading.XMProgress;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/3/21 0021 10:37
 *       desc：酒店政策
 * </pre>
 */
@PageDescComponent(EventConstants.PageDescribe.hotelPolicyFragment)
public class HotelPolicyFragment extends BaseFragment {


    private ConstraintLayout rootLayout;
    private TextView mInOut;
    private TextView mCancelPolicy;
    private TextView mHotelChild;
    private TextView mHotelPet;

    private OrderInfo.Order mOrderDetailInfo;


    public static HotelPolicyFragment newInstance(OrderInfo.Order order) {
        Bundle args = new Bundle();
        args.putParcelable(Constants.HOTEL_POLICY, order);
        HotelPolicyFragment fragment = new HotelPolicyFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hotel_policy, container, false);
        return super.onCreateWrapView(view);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);

        assert getArguments() != null;
        mOrderDetailInfo = getArguments().getParcelable(Constants.HOTEL_POLICY);
        initData();
    }

    private void initView(View view) {
        rootLayout = view.findViewById(R.id.hotel_policy_root);
        rootLayout.setVisibility(View.GONE);
        mInOut = view.findViewById(R.id.tv_in_out_content);
        mCancelPolicy = view.findViewById(R.id.tv_cancel_policy_content);
        mHotelChild = view.findViewById(R.id.tv_child_content);
        mHotelPet = view.findViewById(R.id.tv_pet_content);
    }


    private void initData() {
        if (!NetworkUtils.isConnected(mContext)) {
            showNoNetView();
            return;
        }

        if (mOrderDetailInfo == null) {
            KLog.w("mOrderDetailInfo is null.");
            showEmptyView();
            return;
        }

        XMProgress.showProgressDialog(this, getResources().getString(R.string.base_loading));
        RequestManager.getHotelPolicy(
                mOrderDetailInfo.getHotelJsonVo().getId(),
                mOrderDetailInfo.getChannelId(),
                new ResultCallback<XMResult<HotelPolicy>>() {
                    @Override
                    public void onSuccess(XMResult<HotelPolicy> result) {
                        XMProgress.dismissProgressDialog(HotelPolicyFragment.this);
                        setupContent(result.getData());
                        rootLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        KLog.w(msg);
                        XMProgress.dismissProgressDialog(HotelPolicyFragment.this);
                        showEmptyView();
                        rootLayout.setVisibility(View.VISIBLE);
                    }
                });
    }


    private void setupContent(HotelPolicy hotelPolicy) {
        mInOut.setText(hotelPolicy.getTime());
        mCancelPolicy.setText(hotelPolicy.getCancel());
        mHotelChild.setText(hotelPolicy.getChild());
        mHotelPet.setText(hotelPolicy.getPet());
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        initData();
    }
}
