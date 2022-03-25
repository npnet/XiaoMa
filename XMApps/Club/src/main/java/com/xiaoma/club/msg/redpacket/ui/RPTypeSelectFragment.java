package com.xiaoma.club.msg.redpacket.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.club.BuildConfig;
import com.xiaoma.club.R;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.ui.navi.NavigationBar;

/**
 * Created by LKF on 2019-4-12 0012.
 * 红包类型选择页面(发红包)
 */
public class RPTypeSelectFragment extends BaseFragment implements View.OnClickListener {
    private View mBtnNormalRp;
    private View mBtnLocationRp;
    private View mBtnRpMap;
    private NavigationBar mNavigationBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.act_rp_type_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtnNormalRp = view.findViewById(R.id.btn_normal_rp);
        mBtnLocationRp = view.findViewById(R.id.btn_location_rp);
        mBtnRpMap = view.findViewById(R.id.btn_rp_map);

        mBtnNormalRp.setOnClickListener(this);
        mBtnLocationRp.setOnClickListener(this);
        mBtnRpMap.setOnClickListener(this);

        mNavigationBar = view.findViewById(R.id.rp_navi_bar);
        mNavigationBar.showBackNavi();
    }

    @Override
    public void onClick(View v) {
        RPType type = null;
        switch (v.getId()) {
            case R.id.btn_normal_rp:
                type = RPType.NORMAL;
                //startActivity(new Intent(this, RPSendActivity.class));
                break;
            case R.id.btn_location_rp:
                type = RPType.LOCATION;
                /*startActivity(new Intent(this, RPSendActivity.class)
                        .putExtra(RPSendActivity.EXTRA_IS_LOCATION, true));*/
                break;
            case R.id.btn_rp_map:
                type = RPType.RP_MAP;
                break;
        }
        if (mCallback != null)
            mCallback.onTypeSelect(type);
        // TODO test
        if (BuildConfig.DEBUG) {
            showToast(String.valueOf(((TextView) v).getText()));
        }
    }

    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onTypeSelect(RPType type);
    }

    public enum RPType {
        NORMAL,// 普通红包
        LOCATION,//位置红包
        RP_MAP//红包地图
    }
}
