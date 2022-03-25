package com.xiaoma.drivingscore.historyRecord.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.drivingscore.R;
import com.xiaoma.drivingscore.historyRecord.adapter.RecordAdapter;
import com.xiaoma.drivingscore.historyRecord.model.DriveInfo;
import com.xiaoma.drivingscore.historyRecord.vm.RecordVM;
import com.xiaoma.model.XmResource;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/1/7
 */
public class HistoryRecordFragment extends BaseFragment {

    private RecyclerView mRecordsRV;
    private RecordAdapter mRecordAdapter;

    public static HistoryRecordFragment newInstance() {
        return new HistoryRecordFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_history_record, container, false);
        return super.onCreateWrapView(inflate);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindView(view);
        bindRV();
        bindVM();
    }

    private void bindRV() {
        mRecordAdapter = new RecordAdapter();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecordsRV.setLayoutManager(linearLayoutManager);
        mRecordsRV.setAdapter(mRecordAdapter);
    }

    private void bindView(@NonNull View view) {
        ImageView recordIV = view.findViewById(R.id.ivRecord);
        mRecordsRV = view.findViewById(R.id.rvRecords);

    }

    private void bindVM() {
        RecordVM recordVM = ViewModelProviders.of(this).get(RecordVM.class);
        recordVM.getStateDriveInfoLiveData().observe(this, new Observer<XmResource<List<DriveInfo>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<DriveInfo>> listXmResource) {
                listXmResource.handle(new OnCallback<List<DriveInfo>>() {
                    @Override
                    public void onSuccess(List<DriveInfo> data) {
                        mRecordAdapter.setNewData(data);
                    }
                });
            }
        });

        recordVM.fetchDriveInfo();

    }
}
