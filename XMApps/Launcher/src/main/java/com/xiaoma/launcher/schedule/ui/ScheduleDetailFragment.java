package com.xiaoma.launcher.schedule.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.aidl.model.ScheduleInfo;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.schedule.adapter.ScheduleDetailAdapter;
import com.xiaoma.launcher.schedule.manager.ScheduleDataManager;
import com.xiaoma.launcher.schedule.manager.ScheduleRemindManager;
import com.xiaoma.launcher.schedule.utils.DateUtil;
import com.xiaoma.launcher.schedule.utils.LunarUtil;
import com.xiaoma.mapadapter.model.SearchAddressInfo;
import com.xiaoma.mapadapter.ui.MapActivity;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.ui.view.XmDividerDecoration;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

@PageDescComponent(EventConstants.PageDescribe.ScheduleDetailActivityPagePathDesc)
public class ScheduleDetailFragment extends BaseFragment implements View.OnClickListener {
    public static final int REQUEST_CODE_LOCATION = 11;
    private TextView tvDate, tvLunar, tvWeek, tvCompile;
    private RecyclerView recyclerView;
    private ScheduleDetailAdapter scheduleDetailAdapter;

    private ScheduleInfo mScheduleInfo;

    private static final String LUNAR_ARRAY = "lunar";
    private static final String DATE_STR = "date";
    private View mNoNetView;
    private String mDate;

    public static ScheduleDetailFragment newInstance(String date, String[] lunar) {
        ScheduleDetailFragment scheduleDetailFragment = new ScheduleDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DATE_STR, date);
        bundle.putStringArray(LUNAR_ARRAY, lunar);
        scheduleDetailFragment.setArguments(bundle);
        return scheduleDetailFragment;
    }

    public static ScheduleDetailFragment newInstance(String date) {
        ScheduleDetailFragment scheduleDetailFragment = new ScheduleDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DATE_STR, date);
        scheduleDetailFragment.setArguments(bundle);
        return scheduleDetailFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_record_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        initData();
    }

    private void initView(View view) {
        tvDate = view.findViewById(R.id.tv_date);
        tvLunar = view.findViewById(R.id.tv_lunar);
        tvWeek = view.findViewById(R.id.tv_week);
        tvCompile = view.findViewById(R.id.tv_compile);
        XmScrollBar xmScrollBar = view.findViewById(R.id.scroll_bar);
        recyclerView = view.findViewById(R.id.schedule_recycler);
        mNoNetView = view.findViewById(R.id.ll_net);
        TextView tvRetryNet = view.findViewById(R.id.tv_retry);
        tvRetryNet.setOnClickListener(this);
        tvCompile.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        XmDividerDecoration xmDividerDecoration = new XmDividerDecoration(mContext, DividerItemDecoration.HORIZONTAL);
        xmDividerDecoration.setRect(20, 0, 0, 0);
        xmDividerDecoration.setExtraMargin(40);
        recyclerView.addItemDecoration(xmDividerDecoration);

        scheduleDetailAdapter = new ScheduleDetailAdapter(new ArrayList<>());
        recyclerView.setAdapter(scheduleDetailAdapter);
        xmScrollBar.setRecyclerView(recyclerView);
        scheduleDetailAdapter.setEmptyView(R.layout.empty_sch_view, recyclerView);
        scheduleDetailAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int id = view.getId();
                ScheduleInfo scheduleInfo = scheduleDetailAdapter.getData().get(position);
                if (id == R.id.tv_location) {
                    //选择地址
                    jumpForLocation(scheduleInfo);

                    XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.SCHEDULE_DETAIL_SELECT_ADDRESS,
                            "",
                            ScheduleDetailFragment.class.getSimpleName(),
                            EventConstants.PageDescribe.ScheduleDetailActivityPagePathDesc);
                } else if (id == R.id.iv_delete) {
                    //删除日程
                    delSch(scheduleInfo, position);
                    XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.SCHEDULE_DETAIL_DELETE,
                            "",
                            ScheduleDetailFragment.class.getSimpleName(),
                            EventConstants.PageDescribe.ScheduleDetailActivityPagePathDesc);
                }
            }
        });
    }


    private void initData() {
        EventBus.getDefault().register(this);

        if (getArguments() == null) {
            return;
        }
        Bundle arguments = getArguments();
        mDate = arguments.getString(DATE_STR);
        try {
            tvDate.setText(DateUtil.getFormatDate2(mDate));
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            Date time = format.parse(mDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(time);
            tvWeek.setText(DateUtil.getWeek(calendar));
            //显示农历
            String[] lunar = arguments.getStringArray(LUNAR_ARRAY);
            if (lunar == null) {
                lunar = LunarUtil.solarToLunar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            }
            tvLunar.setText(getString(R.string.calendar_lunar_date, LunarUtil.getTraditionalYearName(calendar.get(Calendar.YEAR)), lunar[0], lunar[1]));

            scheduleDetailAdapter.setNewData(getScheduleDetailInfos(mDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //检查网络
        checkSchNet();
    }

    private void delSch(ScheduleInfo scheduleInfo, int position) {
        ScheduleRemindManager.getInstance().removeScheduleRemind(scheduleInfo);
        scheduleDetailAdapter.remove(position);
        EventBus.getDefault().post(scheduleInfo, LauncherConstants.UPDATE_STATUS);
        //删除后 监听数据是否为空
        if (ListUtils.isEmpty(scheduleDetailAdapter.getData())) {
            tvCompile.setVisibility(View.GONE);
        } else {
            tvCompile.setVisibility(View.VISIBLE);
        }
    }

    private void jumpForLocation(ScheduleInfo scheduleInfo) {
        Intent intent = new Intent(mContext, MapActivity.class);
        startActivityForResult(intent, REQUEST_CODE_LOCATION);
        mScheduleInfo = scheduleInfo;
    }

    private void checkSchNet() {
        if (NetworkUtils.isConnected(mContext)) {
            mNoNetView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            if (!ListUtils.isEmpty(scheduleDetailAdapter.getData())) {
                tvCompile.setVisibility(View.VISIBLE);
            } else {
                tvCompile.setVisibility(View.GONE);
            }
        } else {
            mNoNetView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvCompile.setVisibility(View.GONE);
        }
    }

    public List<ScheduleInfo> getScheduleDetailInfos(String date) {
        if (TextUtils.isEmpty(date)) {
            tvCompile.setVisibility(View.GONE);
            return null;
        }
        List<ScheduleInfo> scheduleInfos = ScheduleDataManager.getLocalScheduleInfosForDate(date);
        Collections.sort(scheduleInfos);
        return scheduleInfos;
    }


    @Override
    @NormalOnClick({EventConstants.NormalClick.SCHEDULE_DETAIL_EDIT, EventConstants.NormalClick.SCHEDULE_DETAIL_NETWORK_RECONNECTION})
//按钮对应的名称
    @ResId({R.id.tv_compile, R.id.tv_retry})//按钮对应的R文件id
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_compile:
                if (getString(R.string.compile).contentEquals(tvCompile.getText())) {
                    List<ScheduleInfo> scheduleInfoList = scheduleDetailAdapter.getData();
                    if (ListUtils.isEmpty(scheduleInfoList)) {
                        return;
                    }
                    ((TextView) v).setText(R.string.finish);
                    for (int i = 0; i < scheduleInfoList.size(); i++) {
                        scheduleInfoList.get(i).setShowDeleteView(true);
                    }
                    scheduleDetailAdapter.setLocationClickable(false);
                } else {
                    List<ScheduleInfo> scheduleInfoList = scheduleDetailAdapter.getData();
                    tvCompile.setText(R.string.compile);
                    for (int i = 0; i < scheduleInfoList.size(); i++) {
                        scheduleInfoList.get(i).setShowDeleteView(false);
                    }
                    scheduleDetailAdapter.setLocationClickable(true);
                }
                break;
            case R.id.tv_retry:
                checkSchNet();
                break;
            default:
                break;
        }
    }

    @Subscriber(tag = LauncherConstants.UPDATE_STATUS)
    private void updateSchDetail(ScheduleInfo scheduleInfo) {
        if (scheduleDetailAdapter != null) {
            scheduleDetailAdapter.setNewData(getScheduleDetailInfos(mDate));
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOCATION && resultCode == RESULT_OK) {
            SearchAddressInfo addressInfo = data.getParcelableExtra(MapActivity.EXTRA_LOCATION_DATA);
            mScheduleInfo.setLocation(addressInfo.title);
            scheduleDetailAdapter.notifyDataSetChanged();
            //更新地址
            ScheduleRemindManager.getInstance().addScheduleRemind(mScheduleInfo);
        }
    }

}
