package com.xiaoma.recommend.ui;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.xiaoma.login.LoginManager;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.recommend.R;
import com.xiaoma.recommend.adapter.RecommendAdapter;
import com.xiaoma.recommend.listener.RecommendDataListener;
import com.xiaoma.recommend.model.ModeType;
import com.xiaoma.recommend.utils.RecommendUtils;
import com.xiaoma.recommend.listener.ItemClickListener;
import com.xiaoma.recommend.manager.RecommendDataManager;
import com.xiaoma.recommend.model.RecommendCategory;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;
import java.util.List;

/**
 * @author: iSun
 * @date: 2018/12/4 0004
 * 推荐模块
 */

public class RecommendFragment extends Fragment implements RecommendDataListener {

    private static final String TAG = RecommendFragment.class.getSimpleName();
    private RecyclerView rv;
    private RecommendAdapter adapter;
    //需要根据模式动态设置
    private ModeType modeType = ModeType.LEISURE;

    public static RecommendFragment newInstance() {
        return new RecommendFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recommend_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
    }

    private void bindView(View view) {
        rv = view.findViewById(R.id.rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.HORIZONTAL);
        adapter = new RecommendAdapter(getContext());
        rv.setAdapter(adapter);
        ItemClickListener listener = new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                KLog.e(TAG, " onItemClick : ");
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        };

        adapter.setItemClickListener(listener);
        Location location = RecommendUtils.getLocation(getContext());
        if (location != null) {
            String address = "lat：" + location.getLatitude() + "lon：" + location.getLongitude();
            KLog.d(TAG, " getLocation : " + address);
            getRecommendData(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), modeType.getValue());
        } else {
            KLog.d(TAG, " getLocation is null ");
            getRecommendData("", "", modeType.getValue());
        }
        RecommendDataManager.getInstance(getContext()).addRecommendListener(this);
    }

    public void getRecommendData(String lat, String lon, String modeType) {
        String loginUserId = LoginManager.getInstance().getLoginUserId();
        if (TextUtils.isEmpty(loginUserId)) {
            KLog.e(TAG, " getRecommendData : ");
            return;
        }
        RecommendDataManager.getInstance(getContext()).getRecommendList(Long.valueOf(loginUserId), lat, lon, new ResultCallback<XMResult<List<RecommendCategory>>>() {
            @Override
            public void onSuccess(XMResult<List<RecommendCategory>> result) {
                if (adapter != null && result != null) {
                    adapter.setData(result.getData());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                KLog.e(TAG, " onFailure : " + code + " msg:" + msg);
            }
        },modeType);
    }

    @Override
    public void onPushDataChange() {
        RecommendCategory firstPushData = RecommendDataManager.getInstance(getContext()).getFirstPushData();
        KLog.e(TAG, " received a push message : " + GsonHelper.toJson(firstPushData));
    }

    @Override
    public void onRecommendDataChange() {

    }

}
