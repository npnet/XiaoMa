package com.xiaoma.vrpractice.ui;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.vrpractice.ui
 *  @file_name:      SkillDemoActivity
 *  @author:         Rookie
 *  @create_time:    2019/6/5 11:08
 *  @description：   TODO             */

import android.app.Dialog;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.pratice.SkillBean;
import com.xiaoma.model.pratice.VrPracticeConstants;
import com.xiaoma.vrpractice.R;
import com.xiaoma.vrpractice.adapter.SkillDemoAdapter;
import com.xiaoma.vrpractice.vm.SkillDemoVM;

import org.simple.eventbus.EventBus;

public class SkillDemoActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvTitle;

    private Button btnEdit;
    private Button btnDel;
    private ImageView ivHead;
    private TextView tvVoice;
    private RecyclerView rvActions;

    private static final String SKILL_BEAN = "skill_bean";
    private static final String SKILL_POSITION = "skill_position";
    private SkillBean mSkillBean;
    private Dialog mDialog;
    private int mSkillPosition;
    private SkillDemoVM mSkillDemoVM;
    private BroadcastReceiver mBroadcastReceiver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill_demo);
        initView();
        registerExit();
    }

    @Override
    protected void onDestroy() {
        unRegisterExit();
        super.onDestroy();
    }

    private void registerExit() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("close_app_VR_PRACTICE");
        registerReceiver(mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                exit();
            }
        }, intentFilter);
    }

    private void exit() {
        finish();
    }
    public static void skipDemo(Context context, SkillBean skillBean, int position) {
        Intent intent = new Intent(context, SkillDemoActivity.class);
        intent.putExtra(SKILL_BEAN, skillBean);
        intent.putExtra(SKILL_POSITION, position);
        context.startActivity(intent);
    }
    private void unRegisterExit() {
        if (mBroadcastReceiver == null) return;
        unregisterReceiver(mBroadcastReceiver);
    }
    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        btnEdit = findViewById(R.id.btn_edit);
        btnDel = findViewById(R.id.btn_del);
        ivHead = findViewById(R.id.iv_head);
        tvVoice = findViewById(R.id.tv_voice);
        rvActions = findViewById(R.id.rv_actions);
        btnEdit.setOnClickListener(this);
        btnDel.setOnClickListener(this);
        mSkillBean = getIntent().getParcelableExtra(SKILL_BEAN);
        mSkillPosition = getIntent().getIntExtra(SKILL_POSITION, 0);
        tvTitle.setText(getString(R.string.skill_title, mSkillPosition + 1));
        tvVoice.setText(mSkillBean.getWord());
        if (UserManager.getInstance().getCurrentUser() != null) {
            RequestOptions requestOptions = RequestOptions.circleCropTransform()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
                    .skipMemoryCache(true);//不做内存缓存
            ImageLoader.with(this).load(UserManager.getInstance().getCurrentUser().getPicPath()).apply(requestOptions).placeholder(R.drawable.icon_default_user).into(ivHead);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvActions.setLayoutManager(linearLayoutManager);
        SkillDemoAdapter skillDemoAdapter = new SkillDemoAdapter(mSkillBean.getUserSkillItems());
        rvActions.setAdapter(skillDemoAdapter);

        mSkillDemoVM = ViewModelProviders.of(this).get(SkillDemoVM.class);
        mSkillDemoVM.getDelSkill().observe(this, new Observer<XmResource<String>>() {
            @Override
            public void onChanged(@Nullable XmResource<String> stringXmResource) {
                stringXmResource.handle(new OnCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        showToast(R.string.del_success);
                        EventBus.getDefault().post("", VrPracticeConstants.EVENT_REFRESH_SKILL);
                        finish();
                    }

                    @Override
                    public void onError(int code, String message) {
                        showToast(getString(R.string.del_fail_msg, message));
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_edit) {
            AddSkillActivity.skipAddSkill(this, mSkillBean, true);
        } else if (i == R.id.btn_del) {
            showDelDialog();
        }
    }

    private void showDelDialog() {
        if (mDialog == null) {
            View view = View.inflate(this, R.layout.view_dialog_del, null);
            view.findViewById(R.id.tv_sure).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delSkill();
                }
            });
            view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissDialog();
                }
            });
            mDialog = new Dialog(this,R.style.custom_dialog2);
            mDialog.setContentView(view);
            WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
            lp.width = 500;
            lp.height = 300;
            mDialog.getWindow().setAttributes(lp);
        }
        mDialog.show();
    }

    private void delSkill() {
        dismissDialog();
        mSkillDemoVM.delSkill(String.valueOf(mSkillBean.getId()));
    }

    private void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}
