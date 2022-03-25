package com.xiaoma.assistant.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.manager.IAssistantManager;
import com.xiaoma.assistant.model.StationInfo;
import com.xiaoma.assistant.model.TrainBean;
import com.xiaoma.assistant.model.TrainDetailBean;
import com.xiaoma.assistant.ui.adapter.TrainDetailAdapter;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/30
 * Desc：
 */
public class TrainDetailView extends RelativeLayout {

    private static final int PAGE_SIZE = 5;
    private TextView tvTrainNumber;
    private TextView tvTrainStartStation;
    private TextView tvTrainEndStation;
    private RecyclerView rlStation;
    private GridLayout mLayoutPrice;
    private TrainDetailAdapter adapter;
    private List<StationInfo> infoList;
    private Context mContext;
    private TextView advancedSoftSleeper;
    private TextView advancedSoftSleeperPrice;
    private TextView softSleeper;
    private TextView softSleeperPrice;
    private TextView hardSeat;
    private TextView hardSeatPrice;
    private TextView noSeat;
    private TextView noSeatPirce;
    private TextView page;
    private int lastPosition = PAGE_SIZE;
    private int firstPosition = 0;
    private LinearLayoutManager linearLayoutManager;

    public TrainDetailView(Context context) {
        this(context, null);
    }

    public TrainDetailView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrainDetailView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_assistant_train_detail, this);
        tvTrainNumber = findViewById(R.id.tv_train_number);
        tvTrainStartStation = findViewById(R.id.tv_train_start_acton);
        tvTrainEndStation = findViewById(R.id.tv_train_end_acton);
        rlStation = findViewById(R.id.rlv_train_detail);
        mLayoutPrice = findViewById(R.id.layout_seat_price);
        page = findViewById(R.id.page);
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rlStation.setLayoutManager(linearLayoutManager);
        /*advancedSoftSleeper = findViewById(R.id.advanced_soft_sleeper);
        advancedSoftSleeperPrice = findViewById(R.id.advanced_soft_sleeper_price);
        softSleeper = findViewById(R.id.soft_sleeper);
        softSleeperPrice = findViewById(R.id.soft_sleeper_price);
        hardSeat = findViewById(R.id.hard_seat);
        hardSeatPrice = findViewById(R.id.hard_seat_price);
        noSeat = findViewById(R.id.no_seat);
        noSeatPirce = findViewById(R.id.no_seat_price);*/
        rlStation.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                lastPosition = layoutManager.findLastVisibleItemPosition();
                firstPosition = layoutManager.findFirstVisibleItemPosition();
                KLog.d("lastPosition: " + lastPosition + ", firstPosition" + firstPosition);
                setPage();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }


    @SuppressLint("SetTextI18n")
    public void setData(TrainDetailBean trainDetailBean) {

        if (trainDetailBean == null) {
            return;
        }
        if (!ListUtils.isEmpty(infoList)) {
            infoList.clear();
        }
        lastPosition = PAGE_SIZE;
        firstPosition = 0;
        tvTrainNumber.setText(trainDetailBean.info);
        tvTrainStartStation.setText(trainDetailBean.start);
        tvTrainEndStation.setText(trainDetailBean.end);
        infoList = trainDetailBean.station_list;
        if (adapter == null) {
            adapter = new TrainDetailAdapter(getContext(), infoList);
            rlStation.setAdapter(adapter);
        } else {
            //  adapter.setPage(1);
            adapter.setData(infoList);
        }
        if (infoList.size() > 5) {
            setSearchResultOperate(true);
        } else {
            setSearchResultOperate(false);
        }
        setPage();
        adapter.notifyDataSetChanged();
        rlStation.scrollToPosition(0);
        linearLayoutManager.scrollToPositionWithOffset(0, 0);
        mLayoutPrice.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (!ListUtils.isEmpty(trainDetailBean.price_list)) {
            for (int i = 0; i < trainDetailBean.price_list.size(); i++) {
                TrainBean.PriceListBean priceBean = trainDetailBean.price_list.get(i);
                View layout = inflater.inflate(R.layout.view_train_seat_price, null);
                TextView tvSeatType = layout.findViewById(R.id.tv_seat_type);
                TextView tvPrice = layout.findViewById(R.id.tv_price);
                tvSeatType.setText(priceBean.price_type);

                String price = priceBean.price;
                if (price.equals("0") || price.equals("无")) {
                    tvPrice.setText(R.string.no);
                } else {
                    tvPrice.setText(String.format(getContext().getString(R.string.price_yuan), priceBean.price));
                }

                GridLayout.Spec rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1.0f);
                GridLayout.Spec columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1.0f);
                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
                if (i > 2) {
                    layoutParams.topMargin = 40;
                }
                mLayoutPrice.addView(layout, layoutParams);
            }
        }
    }

    private void setPage() {
        int itemCount = adapter.getItemCount();
        int curPage;
        View lastVisibleItem = linearLayoutManager.findViewByPosition(lastPosition);
        if (lastVisibleItem != null && lastVisibleItem.getBottom() > rlStation.getHeight()) {
            lastPosition--;
        }
        int totalPage = itemCount % PAGE_SIZE == 0 ? itemCount / PAGE_SIZE : (itemCount / PAGE_SIZE) + 1;
        if (lastPosition == adapter.getItemCount() - 1) {
            curPage = totalPage;
        } else {
            curPage = (lastPosition + 1) / PAGE_SIZE;
        }
        page.setText(curPage + "/" + totalPage);
    }

    public void setCurPage(int page) {
        TopSmoothScroller topSmoothScroller = new TopSmoothScroller(getContext());

        int itemCount = adapter.getItemCount();
        int totalPage = itemCount % PAGE_SIZE == 0 ? itemCount / PAGE_SIZE : (itemCount / PAGE_SIZE) + 1;
        int curPage = (lastPosition + 1) / PAGE_SIZE;
        if (lastPosition + 1 == itemCount) {
            curPage = totalPage;
        }
        if (page > 0) {
            curPage++;
            if (curPage < totalPage) {
                topSmoothScroller.setTargetPosition((curPage - 1) * PAGE_SIZE);
            } else if (curPage >= totalPage) {
                topSmoothScroller.setTargetPosition(itemCount - 1);
            }
        } else {
            curPage--;
            if (curPage <= 1) {
                topSmoothScroller.setTargetPosition(0);
            } else {
                int aimPosition = (curPage - 1) * PAGE_SIZE;
                topSmoothScroller.setTargetPosition(aimPosition);
            }
        }
        linearLayoutManager.startSmoothScroll(topSmoothScroller);
    }

    protected IAssistantManager assistantManager = AssistantManager.getInstance();

    private void setSearchResultOperate(boolean size) {
        TextView searchResultOperate = assistantManager.getSearchResultOperate();
        String prefix = mContext.getString(R.string.please_choose_or_cancel);
        String content = "";
        if (size) {
            content = mContext.getString(R.string.search_result_page_choose);
        } else {
            content = mContext.getString(R.string.search_result_page_cancel);
        }
        SpannableString spannableString = new SpannableString(prefix + content);
        spannableString.setSpan(new ForegroundColorSpan(mContext.getColor(R.color.white)), 0, prefix.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(mContext.getColor(R.color.gray)), prefix.length() + 1, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        searchResultOperate.setText(spannableString);
    }

}
