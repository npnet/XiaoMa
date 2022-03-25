package com.xiaoma.motorcade.common.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.motorcade.R;
import com.xiaoma.motorcade.common.manager.RequestManager;
import com.xiaoma.motorcade.common.model.GroupCardInfo;
import com.xiaoma.motorcade.common.model.MeetingInfo;
import com.xiaoma.motorcade.common.utils.PatternUtils;
import com.xiaoma.motorcade.main.vm.MainVM;
import com.xiaoma.motorcade.map.ui.MotorcadeConferenceActivity;
import com.xiaoma.network.callback.CallbackWrapper;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ShareUtils;

/**
 * Author: loren
 * Date: 2019/5/6 0006
 */

public class HandleShareActivity extends BaseActivity {
    private static final int MOTORCADE_NOT_EXSISTED_CODE = 40059;
    private MainVM mainVM;
    private String command;
    private int resultCode = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVM();
        handleIntent();
    }

    private void handleIntent() {
        if (getIntent() == null) {
            joinFailed();
            return;
        }
        command = getIntent().getStringExtra(ShareUtils.SHARE_KEY);
        if (TextUtils.isEmpty(command)) {
            joinFailed();
            return;
        }
        boolean hasBeenMember = getIntent().getBooleanExtra(ShareUtils.HAS_BEEN_MEMBER, false);
        if (hasBeenMember) {
            mainVM.jumpToMeeting(getIntent().getLongExtra(ShareUtils.CAR_TEAM_ID, 0));
            return;
        }
        dismissProgress();
        showProgressDialog(getString(R.string.add_loading));
        joinMotorcade(command);
    }

    // 加入车队请求
    protected void joinMotorcade(String command) {
        if (!PatternUtils.isNumberic(command)) {
            joinFailed();
            return;
        }
        RequestManager.requestAddGroup(command, new CallbackWrapper<GroupCardInfo>() {

            @Override
            public GroupCardInfo parse(String data) {
                XMResult<GroupCardInfo> info = GsonHelper.fromJson(data, new TypeToken<XMResult<GroupCardInfo>>() {
                }.getType());
                if (info != null) {
                    resultCode = info.getResultCode();
                }
                if (info == null || !(info.isSuccess() || info.getResultCode() == 40058)) {
                    return null;
                }
                return info.getData();
            }

            @Override
            public void onSuccess(GroupCardInfo model) {
                super.onSuccess(model);
                if (model != null) {
                    //添加成功进入设置页
                    XMToast.toastSuccess(HandleShareActivity.this, R.string.joined_success);
                    mainVM.jumpToMeeting(model.getId());
                } else {
                    joinFailed();
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (resultCode == MOTORCADE_NOT_EXSISTED_CODE) {
                    showTeamDismissDialog();
                } else {
                    joinFailed();
                }
            }
        });
    }

    private void showTeamDismissDialog() {
        ConfirmDialog dialog = new ConfirmDialog(HandleShareActivity.this);
        dialog.setTitle(getString(R.string.setting_motorcade_exit_tips))
                .setContent(getString(R.string.team_dismissed))
                .setPositiveButton(getString(R.string.sure), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                })
                .setNegativeButtonVisibility(false)
                .show();
    }

    private void initVM() {
        mainVM = ViewModelProviders.of(this).get(MainVM.class);
        mainVM.getMeetingInfo().observe(this, new Observer<XmResource<MeetingInfo>>() {
            @Override
            public void onChanged(@Nullable XmResource<MeetingInfo> infoXmResource) {
                if (infoXmResource != null) {
                    infoXmResource.handle(new OnCallback<MeetingInfo>() {
                        @Override
                        public void onSuccess(MeetingInfo data) {
                            if (data != null) {
                                joinSuccess(data);
                            } else {
                                enterFailed();
                            }
                        }

                        @Override
                        public void onError(int code, String message) {
                            enterFailed();
                        }

                        @Override
                        public void onFailure(String msg) {
                            enterFailed();
                        }

                        @Override
                        public void onLoading() {
                            dismissProgress();
                            showProgressDialog(R.string.join_loading);
                        }
                    });
                } else {
                    joinFailed();
                }
            }
        });
    }

    private void joinSuccess(final MeetingInfo info) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissProgress();
                MotorcadeConferenceActivity.launcherMapActivity(HandleShareActivity.this, info, true);
                finish();
            }
        });
    }

    private void enterFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToastException(R.string.join_failed);
                dismissProgress();
                finish();
            }
        });
    }

    private void joinFailed() {
        showToastException(getString(R.string.join_motorcade_failed));
        finish();
    }

    @Override
    public void finish() {
        dismissProgress();
        super.finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
