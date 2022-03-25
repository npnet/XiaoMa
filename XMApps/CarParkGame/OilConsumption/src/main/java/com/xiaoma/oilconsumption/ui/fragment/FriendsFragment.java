package com.xiaoma.oilconsumption.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.discretescrollview.DSVOrientation;
import com.discretescrollview.transform.ScaleTransformer;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.oilconsumption.R;
import com.xiaoma.oilconsumption.adapter.GamesAdapter;
import com.xiaoma.oilconsumption.data.CompetitionInformation;
import com.xiaoma.oilconsumption.ui.activity.DetailsActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends BaseFragment {

    RecyclerView mRecyclerView;

    public static FriendsFragment getInstance() {
        return new FriendsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateWrapView(inflater.inflate(R.layout.fragment_friends, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        initView();
        initData();
    }

    public void bindView(View view) {
        mRecyclerView=view.findViewById(R.id.recyclerView);
    }

    public void initView() {

    }

    public void initData() {

    }

}
