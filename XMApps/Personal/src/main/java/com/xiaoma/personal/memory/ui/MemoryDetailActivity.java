package com.xiaoma.personal.memory.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.XmResource;
import com.xiaoma.personal.R;
import com.xiaoma.personal.memory.model.MemoryBean;
import com.xiaoma.personal.memory.vm.MemoryDetailVM;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

public class MemoryDetailActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private InnerAdapter mAdapter;
    private MemoryDetailVM mVM;
    private TextView mTvTitle;
    private TextView mTvTips;
    private LinearLayout mLlRecording;
    private Button mBtnSubmit;
    private List<String> mCheckList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memroy_detail);
        initView();
        initData();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new InnerAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mTvTitle = findViewById(R.id.tvTitle);
        mTvTips = findViewById(R.id.tv_tips);
        mLlRecording = findViewById(R.id.ll_recording);
        mBtnSubmit = findViewById(R.id.btn_submit);
    }

    private void initData() {
        mVM = ViewModelProviders.of(this).get(MemoryDetailVM.class);
        mVM.getMemoryList().observe(this, new Observer<XmResource<List<MemoryBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<MemoryBean>> listXmResource) {
                mAdapter.setNewData(listXmResource.data);
            }
        });
        mVM.getCheckMemoryList().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> list) {
                if (ListUtils.isEmpty(list)) {
                    return;
                }
                mLlRecording.removeAllViews();
                for (String s : list) {
                    TextView view = new TextView(MemoryDetailActivity.this);
                    view.setText(s);
                    mLlRecording.addView(view);
                }
            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MemoryBean bean = updateItem(position);
                updateCheckList(bean);
                updateSubmitButton();
            }
        });
    }

    @NonNull
    private MemoryBean updateItem(int position) {
        MemoryBean bean = mAdapter.getData().get(position);
        bean.setSelected(!bean.isSelected());
        mAdapter.notifyItemChanged(position);
        return bean;
    }

    private void updateCheckList(MemoryBean bean) {
        if (bean.isSelected()) {
            mCheckList.add(bean.getName());
        } else {
            mCheckList.remove(bean.getName());

        }
        mVM.getCheckMemoryList().postValue(mCheckList);
    }

    private void updateSubmitButton() {
        for (MemoryBean item : mAdapter.getData()) {
            if (item.isSelected()) {
                mBtnSubmit.setClickable(true);
                mBtnSubmit.setBackgroundColor(Color.YELLOW);
                break;
            }
            mBtnSubmit.setClickable(false);
            mBtnSubmit.setBackgroundColor(Color.GRAY);
        }
    }

    static class InnerAdapter extends BaseQuickAdapter<MemoryBean, BaseViewHolder> {

        InnerAdapter() {
            super(R.layout.item_memory_detail);
        }

        @Override
        protected void convert(BaseViewHolder helper, MemoryBean item) {
            helper.setText(R.id.tv_name, item.getName());
            helper.setBackgroundColor(R.id.item_root, item.isSelected() ? Color.GREEN : Color.WHITE);
        }
    }
}
