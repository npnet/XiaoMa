package com.xiaoma.faultreminder.main.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.faultreminder.R;
import com.xiaoma.faultreminder.common.FaultReminderConstants;
import com.xiaoma.faultreminder.common.view.FaultFrameAnimView;
import com.xiaoma.faultreminder.main.vm.FaultViewModel;
import com.xiaoma.faultreminder.sdk.model.CarFault;
import com.xiaoma.update.manager.AppUpdateCheck;
import com.xiaoma.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author KY
 * @date 12/26/2018
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private FaultFrameAnimView animView;
    private RecyclerView rvFaults;
    private Button btnGoToFix;
    private FaultAdapter mAdapter;
    private FaultViewModel mFaultVM;
    private ImageButton shrink;

    public static void NewFault(Context context, ArrayList<CarFault> carFaults) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putParcelableArrayListExtra(FaultReminderConstants.IntentKey.FAULT_KEY, carFaults);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUpdateCheck.checkAppUpdate(getPackageName(), getApplication());
    }

    private void initView() {
        getNaviBar().hideNavi();
        animView = findViewById(R.id.anim);
        rvFaults = findViewById(R.id.rv_faults);
        btnGoToFix = findViewById(R.id.btn_go_to_fix);
        shrink = findViewById(R.id.shrink);

        rvFaults.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new FaultAdapter();
        mAdapter.bindToRecyclerView(rvFaults);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mAdapter.select(position);
                animView.startAnim(mAdapter.getCurrentFault());
            }
        });
    }

    private void initListener() {
        btnGoToFix.setOnClickListener(this);
        shrink.setOnClickListener(this);
        animView.setNextFaultCallback(new FaultFrameAnimView.NextFaultCallback() {
            @Override
            public void onNextFault() {
                animView.startAnim(mAdapter.next());
                rvFaults.scrollToPosition(mAdapter.getCurrentPosition());
            }
        });
    }

    private void initData() {
        mFaultVM = ViewModelProviders.of(this).get(FaultViewModel.class);
        mFaultVM.getFaults().observe(this, new Observer<List<CarFault>>() {
            @Override
            public void onChanged(@Nullable List<CarFault> carFaults) {
                if (!CollectionUtil.isListEmpty(carFaults)) {
                    btnGoToFix.setVisibility(View.VISIBLE);
                    Collections.sort(carFaults);
                    mAdapter.setNewData(carFaults);
                    animView.startAnim(mAdapter.getCurrentFault());
                }else {
                    btnGoToFix.setVisibility(View.INVISIBLE);
                }
            }
        });
        mFaultVM.fetchFaults();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ArrayList<CarFault> carFaults = intent.getParcelableArrayListExtra(FaultReminderConstants.IntentKey.FAULT_KEY);
        if (!CollectionUtil.isListEmpty(carFaults)) {
            mFaultVM.getFaults().setValue(carFaults);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_go_to_fix:
                //TODO: goto fixPage!
                showToast("go to fix!");
                break;
            case R.id.shrink:
                finish();
                break;
            default:
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        animView.release();
    }

    class FaultAdapter extends BaseQuickAdapter<CarFault, BaseViewHolder> {

        private int mCurrent;

        FaultAdapter() {
            super(R.layout.item_fault);
        }

        void select(int position) {
            mCurrent = position;
            notifyDataSetChanged();
        }

        CarFault next() {
            mCurrent++;
            if(mCurrent == getData().size()){
                mCurrent = 0;
            }
            notifyDataSetChanged();
            return getCurrentFault();
        }

        int getCurrentPosition() {
            return mCurrent;
        }

        CarFault getCurrentFault() {
            if (!CollectionUtil.isListEmpty(getData())) {
                return getData().get(mCurrent);
            }
            return null;
        }

        @Override
        public void setNewData(@Nullable List<CarFault> data) {
            super.setNewData(data);
        }

        @Override
        protected void convert(BaseViewHolder helper, CarFault item) {
            if (helper.getAdapterPosition() == mCurrent) {
                item.setSelected(true);
            } else {
                item.setSelected(false);
            }

            helper.setImageDrawable(R.id.iv_fault, mContext.getDrawable(item.getIconResId()));
            helper.setText(R.id.tv_fault, mContext.getString(item.getTipsId()));
            if (item.isSelected()) {
                helper.setVisible(R.id.iv_select_line, true);
                helper.setTextColor(R.id.tv_fault, ContextCompat.getColor(mContext, R.color.itemSelect));
            } else {
                helper.setVisible(R.id.iv_select_line, false);
                helper.setTextColor(R.id.tv_fault, ContextCompat.getColor(mContext, R.color.itemUnSelect));
            }
        }
    }

}
