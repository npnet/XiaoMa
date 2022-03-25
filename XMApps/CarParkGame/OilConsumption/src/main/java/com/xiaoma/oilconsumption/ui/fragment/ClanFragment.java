package com.xiaoma.oilconsumption.ui.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.oilconsumption.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClanFragment extends BaseFragment {

    RecyclerView mRecyclerView;

    public static ClanFragment getInstance() {
        return new ClanFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateWrapView(inflater.inflate(R.layout.fragment_clan, container, false));
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
