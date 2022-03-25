package com.xiaoma.service.order.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.service.R;
import com.xiaoma.service.common.constant.EventConstants;
import com.xiaoma.service.order.adapter.ProgramAdapter;
import com.xiaoma.service.order.model.ProgramBean;
import com.xiaoma.service.order.vm.OrderVM;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmDividerDecoration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 选择保养项目弹框
 * Created by zhushi.
 * Date: 2018/12/27
 */
@PageDescComponent(EventConstants.PageDescribe.selectProgramActivityPagePathDesc)
public class ChooseProgramDialog extends BaseActivity implements View.OnClickListener {

    private List<ProgramBean> mSelectProgramList = new ArrayList<>();
    private Button mSureProgram;
    private RecyclerView recyclerView;
    private ProgramAdapter programAdapter;
    private OrderVM mOrderVM;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_program);
        getNaviBar().showBackNavi();
        getWindow().setLayout(getResources().getDimensionPixelOffset(R.dimen.choose_program_dialog_width), ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setGravity(Gravity.START);

        initView();
        initData();
    }

    private void initView() {
        if (getIntent().getSerializableExtra(OrderActivity.RESULT_PROGRAM) != null) {
            mSelectProgramList = (List<ProgramBean>) getIntent().getSerializableExtra(OrderActivity.RESULT_PROGRAM);
        }
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        XmDividerDecoration decoration = new XmDividerDecoration(this, DividerItemDecoration.HORIZONTAL);
        recyclerView.addItemDecoration(decoration);
        programAdapter = new ProgramAdapter(mSelectProgramList);
        recyclerView.setAdapter(programAdapter);
        programAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.selectProgram})
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ProgramBean programBean = (ProgramBean) adapter.getItem(position);
                if (programBean == null) {
                    return;
                }
                programBean.setSelected(!programBean.isSelected());
                programAdapter.notifyDataSetChanged();
                mSureProgram.setEnabled(!programAdapter.getSelectedList().isEmpty());
            }
        });
        mSureProgram = findViewById(R.id.sure_program);
        mSureProgram.setVisibility(View.INVISIBLE);
        mSureProgram.setOnClickListener(this);
        mSureProgram.setEnabled(!mSelectProgramList.isEmpty());
        if (!mSelectProgramList.isEmpty()){
            mSureProgram.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        mOrderVM = ViewModelProviders.of(this).get(OrderVM.class);
        mOrderVM.getProgramDates().observe(this, new Observer<XmResource<List<ProgramBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<ProgramBean>> listXmResource) {
                if (listXmResource == null) {
                    return;
                }
                listXmResource.handle(new OnCallback<List<ProgramBean>>() {
                    @Override
                    public void onSuccess(List<ProgramBean> data) {
                        if (data == null || data.isEmpty()) {
                            mSureProgram.setVisibility(View.GONE);
                            return;
                        }
                        mSureProgram.setVisibility(View.VISIBLE);
                        for (ProgramBean selectedPro : mSelectProgramList) {
                            for (int i = 0; i < data.size(); i++) {
                                if (selectedPro.getId() == data.get(i).getId()) {
                                    data.get(i).setSelected(true);
                                }
                            }
                        }
                        programAdapter.setNewData(data);
                    }
                });
            }
        });
        mOrderVM.fetchProgramList();
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        mOrderVM.fetchProgramList();
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.selectProgramSure})
    @ResId({R.id.sure_program})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sure_program:
                if (programAdapter.getSelectedList().isEmpty()) {
                    XMToast.toastException(this, getString(R.string.choose_program_tips), false);

                } else {
                    Intent intent = new Intent();
                    intent.putExtra(OrderActivity.RESULT_PROGRAM, (Serializable) programAdapter.getSelectedList());

                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        //注释掉activity本身的过渡动画
        overridePendingTransition(0, 0);
    }
}

