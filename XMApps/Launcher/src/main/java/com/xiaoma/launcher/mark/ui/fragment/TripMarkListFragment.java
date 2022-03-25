package com.xiaoma.launcher.mark.ui.fragment;

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

import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.views.VisibilityFragment;
import com.xiaoma.launcher.mark.adapter.TripMarkListAdapter;
import com.xiaoma.launcher.mark.cluster.ClusterItem;
import com.xiaoma.launcher.mark.model.MarkPhotoBean;
import com.xiaoma.launcher.mark.ui.activity.MarkMainActivity;
import com.xiaoma.launcher.schedule.view.NumberPickerView;
import com.xiaoma.ui.view.XmDividerDecoration;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TripMarkListFragment extends VisibilityFragment implements View.OnClickListener {

    private NumberPickerView mCityView;
    private TextView mTripNumber;
    private Button mTripEdit;
    private RecyclerView mTripRv;
    private XmScrollBar mScrollBar;
    private TripMarkListAdapter mTripAlbumAdapter;
    public boolean editType = false;

    private LinearLayout mRecordingLinear;
    private List<String> mCityList = new ArrayList<>();
    private static final String TAG = "TripMarkListFragment";
    private static MarkMainActivity mMarkMainActivity;
    private List<ClusterItem> mClusterItem;
    private List<String> mCityString = new ArrayList<>();
    private List<MarkPhotoBean> mMarkPhotoBeans = new ArrayList<>();
    private String[] mCITY;
    private String mSelectCity;


    public static TripMarkListFragment newInstance( MarkMainActivity markMainActivity) {
        mMarkMainActivity = markMainActivity;
        return new TripMarkListFragment();
    }

    public String getTAG() {
        return TAG;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mark_list, container, false);
        return super.onCreateWrapView(view);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        initView();
    }

    private void bindView(View view) {
        mCityView = view.findViewById(R.id.city_list);
        mRecordingLinear = view.findViewById(R.id.recording_linear);
        mTripNumber = view.findViewById(R.id.trip_number);
        mTripEdit = view.findViewById(R.id.trip_edit);
        mTripRv = view.findViewById(R.id.trip_rv);
        mScrollBar = view.findViewById(R.id.scroll_bar);
    }

    private void initView() {
        mRecordingLinear.setOnClickListener(this);
        mTripEdit.setOnClickListener(this);
        initRecycleView();
        mClusterItem = mMarkMainActivity.getClusterItem();
        setCityList();
        mCityView.setMinValue(0);
        if (!ListUtils.isEmpty(mCityString)) {
            mCITY = new String[mCityString.size()];
            mCityString.toArray(mCITY);
            mCityView.setDisplayedValues(mCITY);
            mCityView.setMaxValue(mCITY.length - 1);
            mSelectCity = mCITY[0];
            if (StringUtil.isNotEmpty(mSelectCity)) {
                setRecycleData(mSelectCity);
            }
        }
        mCityView.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal, int clickDay) {
                mSelectCity = mCITY[newVal];
                if (StringUtil.isNotEmpty(mSelectCity)) {
                    setRecycleData(mSelectCity);
                }
            }
        });
    }

    private void setRecycleData(String city) {

        mTripAlbumAdapter.setEdit(editType);
        mTripEdit.setText(editType ? getString(R.string.trip_complete) : getString(R.string.trip_edit));

        if (mTripAlbumAdapter != null) {
            setDataList(city);
            if (mMarkPhotoBeans.size() == 0) {
                mTripEdit.setVisibility(View.INVISIBLE);
                mTripNumber.setVisibility(View.INVISIBLE);
            } else {
                mTripEdit.setVisibility(View.VISIBLE);
                mTripNumber.setVisibility(View.VISIBLE);
            }
            mTripNumber.setText(StringUtil.format(getString(R.string.mark_list_number), mMarkPhotoBeans.size()));
            mTripAlbumAdapter.setNewData(mMarkPhotoBeans);
        }
        mTripAlbumAdapter.setEmptyView(R.layout.trip_album_not_data, (ViewGroup) mTripRv.getParent());

    }

    private void setDataList(String city) {
        mMarkPhotoBeans.clear();
        for (ClusterItem item : mClusterItem) {
            MarkPhotoBean markPhoto = item.getMarkPhoto();
            if (markPhoto != null) {
                if (StringUtil.isNotEmpty(markPhoto.getLocation())) {
                    String[] strings = markPhoto.getLocation().split(" ");
                    if (StringUtil.isNotEmpty(strings[1])) {
                        if (city.equals(strings[1])) {
                            mMarkPhotoBeans.add(markPhoto);
                        }
                    }
                }
            }
        }
        if (mMarkPhotoBeans.size() == 0) {
            mTripEdit.setVisibility(View.INVISIBLE);
            mTripNumber.setVisibility(View.INVISIBLE);
        } else {
            mTripEdit.setVisibility(View.VISIBLE);
            mTripNumber.setVisibility(View.VISIBLE);
        }
        mTripNumber.setText(StringUtil.format(getString(R.string.mark_list_number), mMarkPhotoBeans.size()));
        mTripAlbumAdapter.notifyDataSetChanged();
    }

    private void setCityList() {
        for (ClusterItem item : mClusterItem) {
            MarkPhotoBean markPhoto = item.getMarkPhoto();
            if (markPhoto != null) {
                if (StringUtil.isNotEmpty(markPhoto.getLocation())) {
                    String[] strings = markPhoto.getLocation().split(" ");
                    if (StringUtil.isNotEmpty(strings[1])) {
                        if (!mCityString.contains(strings[1])) {
                            mCityString.add(strings[1]);
                        }
                    }
                }
            }
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        editType = false;
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
        mTripRv.setAdapter(mTripAlbumAdapter = new TripMarkListAdapter(this, mMarkMainActivity));
        mScrollBar.setRecyclerView(mTripRv);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!ListUtils.isEmpty(mCityList)) {
            mCityList.clear();
        }
    }

    public void adapterDeleteListener(MarkPhotoBean markPhotoBean) {
        updateDeleteData(markPhotoBean);
        if (StringUtil.isNotEmpty(mSelectCity)) {
            setDataList(mSelectCity);
        }
    }

    private void updateDeleteData(MarkPhotoBean markPhotoBean) {
        Iterator<ClusterItem> iterator = mClusterItem.iterator();
        while (iterator.hasNext()) {
            ClusterItem integer = iterator.next();
            if (markPhotoBean != null) {
                if (markPhotoBean.equals(integer.getMarkPhoto())) {
                    iterator.remove();
                }
            }
        }

    }

    @Override
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
}
