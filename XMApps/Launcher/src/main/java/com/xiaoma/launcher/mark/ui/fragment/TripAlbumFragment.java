package com.xiaoma.launcher.mark.ui.fragment;

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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.views.VisibilityFragment;
import com.xiaoma.launcher.mark.adapter.TripAlbumAdapter;
import com.xiaoma.launcher.mark.model.MarkPhotoBean;
import com.xiaoma.launcher.mark.ui.activity.MarkMainActivity;
import com.xiaoma.launcher.mark.vm.TripAlblumVM;
import com.xiaoma.launcher.schedule.utils.DateUtil;
import com.xiaoma.launcher.schedule.view.NumberPickerView;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.ui.view.XmDividerDecoration;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
@PageDescComponent(EventConstants.PageDescribe.TripAlbumActivityPagePathDesc)
public class TripAlbumFragment  extends VisibilityFragment implements View.OnClickListener{

    private static final String TAG = "TripAlbumFragment";
    private static String[] MOUTH = {"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
    private static String[] SUMMOUTH;
    //true为mark有存储，false为无存储
    private static boolean MARK_TYPE = true;
    private NumberPickerView mMonthPicker;

    private int mCurrentYear;

    private int mCurrentMonth;

    private TextView mYears;
    private TextView mRecordingNumber;
    private Button mTripEdit;
    private RecyclerView mTripRv;
    private XmScrollBar mScrollBar;
    private TripAlbumAdapter mTripAlbumAdapter;
    private TripAlblumVM mTripAlbumVM;
    public boolean editType = false;
    HashSet<String> hashSet = new HashSet<>();
    private LinearLayout mRecordingLinear;
    private int mFistYears;
    private int mLastMonth;
    private int mLastYears;
    private int mYearDifference;
    private List<String> mList = new ArrayList<>();
    private List<String> mMouthList = new ArrayList<>();
    private static MarkMainActivity mMarkMainActivity;

    public static TripAlbumFragment newInstance(MarkMainActivity markMainActivity) {
        mMarkMainActivity = markMainActivity;
        return new TripAlbumFragment();
    }
    public String getTAG() {
        return TAG;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_ablum, container, false);
        return super.onCreateWrapView(view);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        initView();
        //因为glide会添加一个空白的fragment，导致页面直接退出
        ImageLoader.with(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mTripAlbumVM != null) {
            editType = false;
            mTripAlbumVM.tripAlbum(String.valueOf(mCurrentYear), String.valueOf(mCurrentMonth));
        }
    }

    private void bindView(View view) {
        mMonthPicker = view.findViewById(R.id.month_picker);
        mYears = view.findViewById(R.id.years);
        mRecordingLinear = view.findViewById(R.id.recording_linear);
        mRecordingNumber = view.findViewById(R.id.recording_number);
        mTripEdit = view.findViewById(R.id.trip_edit);
        mTripRv = view.findViewById(R.id.trip_rv);
        mScrollBar = view.findViewById(R.id.scroll_bar);
    }

    private void initView() {
        mFistYears = TPUtils.get(mContext, LauncherConstants.FIST_YEARS, 0);
        mLastMonth = TPUtils.get(mContext, LauncherConstants.LAST_MONTH, 0);
        mLastYears = TPUtils.get(mContext, LauncherConstants.LAST_YEARS, 0);
        mCurrentMonth = DateUtil.getCurrentMonth();
        mCurrentYear = DateUtil.getCurrentYear();

        getSumMouth();

        SUMMOUTH = new String[mMouthList.size()];
        mMouthList.toArray(SUMMOUTH);

        mMonthPicker.setDisplayedValues(SUMMOUTH);
        mMonthPicker.setMinValue(0);
        //设置当前时间
        if (mLastYears != 0){
            mCurrentYear = mLastYears;
            mCurrentMonth = mLastMonth;
            mYears.setText(String.valueOf(mCurrentYear) + "年");
        }else{
            mYears.setText(String.valueOf(mCurrentYear) + "年");
        }


        mTripEdit.setOnClickListener(this);
        mRecordingLinear.setOnClickListener(this);
        mMonthPicker.setMaxValue(SUMMOUTH.length - 1);
        mMonthPicker.setOnValueChangeListenerInScrolling(mOnDateChangedListener);

        if (mLastMonth!=0){
            //当前年份减去最后一次照片的年份，定位到最厚一次照片的月份
            if (SUMMOUTH.length - 1<(Math.abs(mLastYears-mFistYears))*12 + mLastMonth - 1){
                //为了防止手动更改日期会崩溃
                mMonthPicker.setValue(mCurrentMonth - 1);
            }else {
                mMonthPicker.setValue((Math.abs(mLastYears-mFistYears))*12 + mLastMonth - 1);
            }

        }else {
            mMonthPicker.setValue(SUMMOUTH.length - 1);
        }

        mMonthPicker.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal, int clickDay) {
                updateView();
            }
        });

        initRecycleView();
        initData();
    }

    /**
     * 获得所有月份
     */
    private void getSumMouth() {
        mList = Arrays.asList(MOUTH);
        mYearDifference = mCurrentYear - mFistYears;

        for (int i = 0; i < mYearDifference; i++) {
            mMouthList.addAll(mList);
        }

        for (int j = 0; j < mCurrentMonth ;j++){
            mMouthList.add(mList.get(j));
        }
    }

    private void initData() {
        mTripAlbumVM = ViewModelProviders.of(this).get(TripAlblumVM.class);
        mTripAlbumVM.getTripAlbumList().observe(this, new Observer<XmResource<List<MarkPhotoBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<MarkPhotoBean>> listXmResource) {
                if (listXmResource == null) {
                    return;
                }
                listXmResource.handle(new BaseFragment.OnCallback<List<MarkPhotoBean>>() {
                    @Override
                    public void onSuccess(List<MarkPhotoBean> data) {
                        hashSet.clear();
                        mTripAlbumAdapter.setEdit(editType);
                        mTripEdit.setText(editType ? getString(R.string.trip_complete) : getString(R.string.trip_edit));

                        if (mTripAlbumAdapter != null) {
                            for (MarkPhotoBean item : data) {
                                hashSet.add(item.getLocation());
                            }
                            if (hashSet.size() == 0) {
                                mTripEdit.setVisibility(View.INVISIBLE);
                                mRecordingNumber.setVisibility(View.INVISIBLE);
                            } else {
                                mTripEdit.setVisibility(View.VISIBLE);
                                mRecordingNumber.setVisibility(View.VISIBLE);
                            }
                            mRecordingNumber.setText(StringUtil.format(getString(R.string.recording_number), hashSet.size(), data.size()));
                            mTripAlbumAdapter.setNewData(data);
                        }
                        mTripAlbumAdapter.setEmptyView(R.layout.trip_album_not_data, (ViewGroup) mTripRv.getParent());
                    }
                });
            }
        });
    }

    /**
     * 初始化recycleview
     */
    private void initRecycleView() {
        mTripRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        XmDividerDecoration decor = new XmDividerDecoration(mContext, DividerItemDecoration.HORIZONTAL);
        int extra = 100;
        decor.setExtraMargin(extra);
        mTripRv.addItemDecoration(decor);
        mTripRv.setAdapter(mTripAlbumAdapter = new TripAlbumAdapter(this,mMarkMainActivity));
        mScrollBar.setRecyclerView(mTripRv);

    }

    private void updateView() {
        mYears.setText(String.valueOf(mCurrentYear) + "年");
        if (mTripAlbumVM != null) {
            editType = false;
            mTripAlbumVM.tripAlbum(String.valueOf(mCurrentYear), String.valueOf(mCurrentMonth));
        }
    }

    public void adapterDeleteListener() {
        if (mTripAlbumVM != null) {
            mTripAlbumVM.tripAlbum(String.valueOf(mCurrentYear), String.valueOf(mCurrentMonth));
        }
    }

    /**
     * Month Picker控件监听器
     */
    private NumberPickerView.OnValueChangeListenerInScrolling mOnDateChangedListener = new NumberPickerView.OnValueChangeListenerInScrolling() {
        @Override
        public void onValueChangeInScrolling(NumberPickerView picker, int oldVal, int newVal, int inScrollingPickedOldValue, int inScrollingPickedNewValue) {
            updateDate(oldVal, newVal, inScrollingPickedOldValue, inScrollingPickedNewValue);
            mYears.setText(String.valueOf(mCurrentYear) + "年");
        }
    };

    private void updateDate(int oldVal, int newVal, int inScrollingPickedOldValue,
                            int inScrollingPickedNewValue) {
        int newValRem = newVal % 12;
        int oldValRem = oldVal % 12;

        if (oldValRem == 11 && newValRem == 0) {
            //从12月到1月
            mCurrentYear++;

            mCurrentMonth = newValRem + 1;
            return;
        }

        if (oldValRem == 0 && newValRem == 11) {
            //从1月到12月
            mCurrentYear--;
            mCurrentMonth = newValRem + 1;
            return;
        }

        //数值，每次滑动的个数
        int value = newVal - oldVal;
        if (value == 0) {
            //滑动一位
        } else if (value < 0) {
            //向上滑动
            //滑动了多少轮
            int count = Math.abs(value) / 12;
            if (count < 0) {
                //一轮都没滑动到
                if (oldValRem - Math.abs(value) < 0) {
                    mCurrentYear--;
                }
            } else {
                //滑动了一轮以上
                mCurrentYear = mCurrentYear - count;
                int temp = Math.abs(value) % 12;
                if (oldValRem - temp < 0) {
                    mCurrentYear--;
                }
            }
        } else {
            //向下滑动
            int count = value / 12;
            if (count < 0) {
                //一轮都没滑动到
                if (oldValRem + count > 11) {
                    mCurrentYear++;

                }
            } else {
                //滑动了一轮以上
                mCurrentYear = mCurrentYear + count;
                int temp = value % 12;
                if (oldValRem + temp > 11) {
                    mCurrentYear++;
                }
            }
        }
        mCurrentMonth = newValRem + 1;
    }
    @Override
    @NormalOnClick({EventConstants.NormalClick.MARK_RECORD_EDIT})//按钮对应的名称
    @ResId({R.id.trip_edit})//按钮对应的R文件id
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trip_edit:
                if (mTripAlbumAdapter != null) {
                    mTripAlbumAdapter.setEdit(!editType);
                    mTripEdit.setText(editType ? getString(R.string.trip_edit) : getString(R.string.trip_complete));
                    mTripAlbumAdapter.notifyDataSetChanged();
                    editType = !editType;
                }
                break;
            case R.id.recording_linear:
                if (editType) {
                    mTripAlbumAdapter.setEdit(!editType);
                    mTripEdit.setText(getString(R.string.trip_edit));
                    mTripAlbumAdapter.notifyDataSetChanged();
                    editType = !editType;
                }
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!ListUtils.isEmpty(mMouthList)){
            mMouthList.clear();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
