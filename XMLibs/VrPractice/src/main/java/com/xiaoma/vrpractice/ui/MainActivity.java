package com.xiaoma.vrpractice.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.reflect.TypeToken;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.config.ConfigConstants;
import com.xiaoma.login.LoginManager;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.pratice.SkillBean;
import com.xiaoma.model.pratice.VrPracticeConstants;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.XmProperties;
import com.xiaoma.vrpractice.R;
import com.xiaoma.vrpractice.adapter.SkillAdapter;
import com.xiaoma.vrpractice.vm.MainSkillVM;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 2019/6/3 0003
 * 语音训练
 */

public class MainActivity extends BaseActivity {

    private RecyclerView rvSkill;
    private XmScrollBar scrollBar;
    private Button btnAdd;
    private SkillAdapter mAdapter;
    private MainSkillVM mMainSkillVM;
    private BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        initView();
        initData();
        registerExit();
    }

    private void registerExit() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("close_app_VR_PRACTICE");
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                exit();
            }
        };
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void exit() {
        finish();
    }

    private void initView() {
        rvSkill = findViewById(R.id.rv_skill);
        scrollBar = findViewById(R.id.scroll_bar);
        btnAdd = findViewById(R.id.btn_add);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvSkill.setLayoutManager(linearLayoutManager);
        rvSkill.setHasFixedSize(true);
        mAdapter = new SkillAdapter(new ArrayList<>());
        mAdapter.setEmptyView(R.layout.view_empty_skill, rvSkill);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SkillBean skillBean = mAdapter.getData().get(position);
                SkillDemoActivity.skipDemo(MainActivity.this, skillBean, position);
            }
        });
        rvSkill.setAdapter(mAdapter);
        scrollBar.setRecyclerView(rvSkill);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<SkillBean> data = mAdapter.getData();
                if (!ListUtils.isEmpty(data) && data.size() >= VrPracticeConstants.MAX_SKILL_SIZE) {
                    showToast(R.string.add_10_at_most);
                } else {
                    startActivity(AddSkillActivity.class);
                }
            }
        });
    }


    private void initData() {
        mMainSkillVM = ViewModelProviders.of(this).get(MainSkillVM.class);
        mMainSkillVM.getSkillsData().observe(this, new Observer<XmResource<List<SkillBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<SkillBean>> listXmResource) {
                listXmResource.handle(new OnCallback<List<SkillBean>>() {
                    @Override
                    public void onSuccess(List<SkillBean> data) {
                        rvSkill.setVisibility(View.VISIBLE);
                        btnAdd.setVisibility(View.VISIBLE);
                        mAdapter.setNewData(data);
                        //保存skilllist到本地
                        XmProperties.build(LoginManager.getInstance().getLoginUserId()).put(ConfigConstants.VR_PRACTICE_CONTENT, GsonHelper.toJson(data));
                        notifyVrPracticeDataChange();
                    }

                    @Override
                    public void onFailure(String msg) {
                        rvSkill.setVisibility(View.VISIBLE);
                        btnAdd.setVisibility(View.VISIBLE);
                        String vrPracticeContent = XmProperties.build(LoginManager.getInstance().getLoginUserId()).get(ConfigConstants.VR_PRACTICE_CONTENT, "");
                        if (!TextUtils.isEmpty(vrPracticeContent)) {
                            List<SkillBean> skillBeans = GsonHelper.fromJson(vrPracticeContent, new TypeToken<List<SkillBean>>() {
                            }.getType());
                            mAdapter.setNewData(skillBeans);
                        }
                    }
                });
            }
        });
        mMainSkillVM.fetchSkills();
    }

    /**
     * 发送广播通知数据已变化
     */
    private void notifyVrPracticeDataChange() {
        Intent intent = new Intent();
        intent.setAction(ConfigConstants.VR_PRACTICE_ACTION);
        sendBroadcast(intent);
    }


    @Subscriber(tag = VrPracticeConstants.EVENT_REFRESH_SKILL)
    public void refreshSkill(String msg) {
        mMainSkillVM.fetchSkills();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public String getThisNode() {
        return NodeConst.LAUNCHER.VOICE_TRAINING;
    }
}
