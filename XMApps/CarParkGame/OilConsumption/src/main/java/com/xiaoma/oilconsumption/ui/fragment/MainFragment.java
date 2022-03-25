package com.xiaoma.oilconsumption.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.discretescrollview.DSVOrientation;
import com.discretescrollview.DiscreteScrollView;
import com.discretescrollview.transform.ScaleTransformer;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.oilconsumption.R;
import com.xiaoma.oilconsumption.adapter.GamesAdapter;
import com.xiaoma.oilconsumption.data.CompetitionInformation;
import com.xiaoma.oilconsumption.ui.activity.ApplyPayActivity;
import com.xiaoma.oilconsumption.ui.activity.DetailsActivity;
import com.xiaoma.oilconsumption.ui.activity.MainActivity;
import com.xiaoma.oilconsumption.ui.dialog.ProtocolDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 2019/5/23 0023
 */

public class MainFragment extends BaseFragment implements DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>, DiscreteScrollView.ScrollStateChangeListener<RecyclerView.ViewHolder>, View.OnClickListener {

    List<CompetitionInformation> list = new ArrayList();
    Button cancle, record, rule;
    TextView date, money, number;
    LinearLayout mLinearLayout;
    private DiscreteScrollView mDiscreteScrollView;
    private GamesAdapter mGamesAdapter;

    public static MainFragment getInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateWrapView(inflater.inflate(R.layout.fragment_main, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        initView();
        initData();
    }

    public void bindView(View view) {
        mDiscreteScrollView = view.findViewById(R.id.games);
        cancle = view.findViewById(R.id.cancle);
        record = view.findViewById(R.id.record);
        rule = view.findViewById(R.id.rule);
        date = view.findViewById(R.id.date);
        money = view.findViewById(R.id.money);
        number = view.findViewById(R.id.number);
        mLinearLayout = view.findViewById(R.id.apply);
    }

    public void initView() {
        cancle.setOnClickListener(this);
        record.setOnClickListener(this);
        rule.setOnClickListener(this);
        mLinearLayout.setOnClickListener(this);

        mDiscreteScrollView.setClampTransformProgressAfter(2);
        mDiscreteScrollView.setOrientation(DSVOrientation.HORIZONTAL);
        mDiscreteScrollView.addOnItemChangedListener(this);
        mDiscreteScrollView.addScrollStateChangeListener(this);
        mDiscreteScrollView.setSlideOnFling(true);
        mDiscreteScrollView.setItemTransformer(new ScaleTransformer.Builder()
                .setMaxScale(1.2f)
                .build());

        mGamesAdapter = new GamesAdapter();
        mGamesAdapter.setEnableLoadMore(true);
        mGamesAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                //todo
                startActivity(new Intent(getActivity(), DetailsActivity.class));
            }
        },mDiscreteScrollView);

        mGamesAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.details:
                        //todo
                        break;
                }
            }
        });

        mGamesAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mDiscreteScrollView.smoothScrollToPosition(position);
            }
        });

        mDiscreteScrollView.setAdapter(mGamesAdapter);
    }

    public void initData() {
        list.add(new CompetitionInformation("1月1日", "1",1000, null));
        list.add(new CompetitionInformation("2月1日", "2",2000, null));
        list.add(new CompetitionInformation("3月1日", "3",3000, null));
        list.add(new CompetitionInformation("4月1日", "4",4000, null));
        mGamesAdapter.addData(list);
        if (list.size() >= 4) {
            mDiscreteScrollView.scrollToPosition(2);
        } else if (list.size() == 3) {
            mDiscreteScrollView.scrollToPosition(1);
        } else {
            mDiscreteScrollView.scrollToPosition(list.size() - 1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
        if (ListUtils.isEmpty(list)) {
            return;
        }
        if (adapterPosition < list.size()) {
            onItemChanged(list.get(adapterPosition));
        }
    }

    public void onItemChanged(CompetitionInformation competitionInformation){

    }

    @Override
    public void onScrollStart(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {

    }

    @Override
    public void onScrollEnd(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {
        if (adapterPosition == list.size()) {
            XMToast.showToast(getActivity(),"没有更多数据了");
            mDiscreteScrollView.smoothScrollToPosition(adapterPosition - 1);
        } else if (adapterPosition == list.size()) {
            mDiscreteScrollView.scrollToPosition(adapterPosition - 1);
        }
    }

    @Override
    public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable RecyclerView.ViewHolder currentHolder, @Nullable RecyclerView.ViewHolder newCurrent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancle:

                break;
            case R.id.record:

                break;
            case R.id.rule:
                new ProtocolDialog().showDialog(getActivity());
                break;
            case R.id.apply:
                startActivity(new Intent(getActivity(), ApplyPayActivity.class));
                break;
        }
    }
}
