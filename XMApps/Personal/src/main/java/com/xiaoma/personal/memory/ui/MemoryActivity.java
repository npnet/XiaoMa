package com.xiaoma.personal.memory.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.XmResource;
import com.xiaoma.personal.R;
import com.xiaoma.personal.memory.vm.MemoryVM;

import java.util.List;

public class MemoryActivity extends BaseActivity {

    private TextView mTvTitle;
    private ImageView mIvAvatar;
    private TextView mTvTel;
    private Button mBtnSubmit;
    private boolean hsaSet = true;
    private LinearLayout mLlHasSet;
    private LinearLayout mLlNotHasSet;
    private MemoryVM mVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);
        getNaviBar().showBackNavi();
        initView();
        initData();
    }

    private void initView() {
        mTvTitle = findViewById(R.id.tvTitle);
        mIvAvatar = findViewById(R.id.iv_avatar);
        mTvTel = findViewById(R.id.tv_tel);
        mBtnSubmit = findViewById(R.id.btn_submit);
        mLlHasSet = findViewById(R.id.ll_has_set);
        mLlNotHasSet = findViewById(R.id.ll_not_has_set);
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MemoryActivity.this, MemoryDetailActivity.class));
            }
        });
    }

    private void initData() {
        mTvTitle.setText(hsaSet ? getString(R.string.memory_not_first_time_title) : getString(R.string.memory_first_time_title));
        mBtnSubmit.setText(hsaSet ? getString(R.string.memory_rentry) : getString(R.string.memory_begain));
        mLlHasSet.setVisibility(hsaSet ? View.VISIBLE : View.GONE);
        mLlNotHasSet.setVisibility(hsaSet ? View.GONE : View.VISIBLE);
        mVM = ViewModelProviders.of(this).get(MemoryVM.class);
        mVM.getMemoryList().observe(this, new Observer<XmResource<List<String>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<String>> listXmResource) {
                for (String item : listXmResource.data) {
                    TextView view = new TextView(MemoryActivity.this);
                    view.setText(item);
                    mLlHasSet.addView(view);
                }
            }
        });


    }
}
