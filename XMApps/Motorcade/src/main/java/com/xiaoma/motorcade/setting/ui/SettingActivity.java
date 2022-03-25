package com.xiaoma.motorcade.setting.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.motorcade.MainActivity;
import com.xiaoma.motorcade.R;
import com.xiaoma.motorcade.common.constants.MotorcadeConstants;
import com.xiaoma.motorcade.common.manager.RequestManager;
import com.xiaoma.motorcade.common.model.BaseResult;
import com.xiaoma.motorcade.common.model.GroupCardInfo;
import com.xiaoma.motorcade.common.model.GroupMemberInfo;
import com.xiaoma.motorcade.common.model.MeetingInfo;
import com.xiaoma.motorcade.common.utils.MotorcadeDialogUtils;
import com.xiaoma.motorcade.common.utils.MotorcadeSetting;
import com.xiaoma.motorcade.common.utils.UserUtil;
import com.xiaoma.motorcade.main.vm.MainVM;
import com.xiaoma.motorcade.map.ui.MotorcadeConferenceActivity;
import com.xiaoma.motorcade.setting.view.ShareCommandFragment;
import com.xiaoma.network.callback.CallbackWrapper;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.ShareUtils;
import com.xiaoma.utils.share.ShareCallBack;
import com.xiaoma.utils.share.ShareClubBean;
import com.xiaoma.utils.share.ShareConstants;

/**
 * @author zs
 * @date 2019/1/16 0016.
 */
@PageDescComponent(MotorcadeConstants.PageDesc.settingActivity)
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private static final String MOTORCADE_INFO = "MotorcadeInfo";
    private GroupCardInfo currentMotorcade;
    private TextView nickNameTv;
    private TextView nameTv;
    private StatusFragment statusFragment;
    private MainVM mainVM;

    public static void launch(Context context, GroupCardInfo info) {
        Intent intent = new Intent(context, SettingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(MOTORCADE_INFO, info);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initVM();
    }

    private void initView() {
        currentMotorcade = getIntent().getExtras().getParcelable(MOTORCADE_INFO);
        findViewById(R.id.motorcade_command_cl).setOnClickListener(this);
        findViewById(R.id.motorcade_name_cl).setOnClickListener(this);
        findViewById(R.id.motorcade_nickname_cl).setOnClickListener(this);
        findViewById(R.id.exit_motorcade).setOnClickListener(this);
        TextView commandTv = findViewById(R.id.command_tv);
        commandTv.setText(currentMotorcade.getHxGroupId());
        nameTv = findViewById(R.id.name_tv);
        nameTv.setText(currentMotorcade.getNick());

        nickNameTv = findViewById(R.id.nickname_tv);
        statusFragment = StatusFragment.newInstance(currentMotorcade.getId());
        statusFragment.setCallback(callBack);
        FragmentUtils.add(getSupportFragmentManager(), statusFragment,
                R.id.status_fl, "StatusFragment");
        Switch voiceSwitch = findViewById(R.id.listen_voice_switch);
        voiceSwitch.setChecked(MotorcadeSetting.isReceive(String.valueOf(currentMotorcade.getId())));
        voiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MotorcadeSetting.setReceiveSwitch(String.valueOf(currentMotorcade.getId()), isChecked);
                String opening = isChecked ? "打开" : "关闭";
                XmAutoTracker.getInstance().onEvent(MotorcadeConstants.RECEIVE_OTHERS_STATUS, opening,
                        "SettingActivity", MotorcadeConstants.PageDesc.settingActivity);
            }
        });
    }


    private void initVM() {
        mainVM = ViewModelProviders.of(this).get(MainVM.class);
        mainVM.setUICallback(new MainVM.UICallback() {
            @Override
            public void onShowLoading() {
                showProgressDialog(R.string.base_loading);
                Log.i("LKF", "onShowLoading: ");
            }

            @Override
            public void onDismissLoading() {
                dismissProgress();
                Log.i("LKF", "onDismissLoading: ");
            }
        });
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
                                joinFailed();
                            }
                        }

                        @Override
                        public void onError(int code, String message) {
                            joinFailed();
                        }

                        @Override
                        public void onFailure(String msg) {
                            joinFailed();
                        }

                        @Override
                        public void onLoading() {
                            showProgressDialog(R.string.join_loading);
                        }
                    });
                } else {
                    joinFailed();
                }
            }
        });
    }

    private void joinFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToastException(R.string.join_failed);
                dismissProgress();
            }
        });
    }

    private void joinSuccess(final MeetingInfo info) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissProgress();
                MotorcadeConferenceActivity.launcherMapActivity(SettingActivity.this, info, false);
            }
        });
    }

    @Override
    @NormalOnClick({MotorcadeConstants.NormalClick.shareCommand, MotorcadeConstants.NormalClick.teamName, MotorcadeConstants.NormalClick.nick, MotorcadeConstants.NormalClick.exit})
    @ResId({R.id.motorcade_command_cl, R.id.motorcade_name_cl, R.id.motorcade_nickname_cl, R.id.exit_motorcade})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.motorcade_command_cl:
                showShareCmdWindow();
                break;
            case R.id.motorcade_name_cl:
                if (!isAdmain()) {
                    XMToast.toastException(this, getString(R.string.no_permission));
                    return;
                }
                setDialog(R.string.setting_motorcade_name, nameTv.getText(),
                        MotorcadeDialogUtils.DIALOG_TYPE_MOTORCADE);
                break;
            case R.id.motorcade_nickname_cl:
                setDialog(R.string.setting_motorcade_nickname, nickNameTv.getText(),
                        MotorcadeDialogUtils.DIALOG_TYPE_NICKNAME);
                break;
            case R.id.exit_motorcade:
                setExitDialog();
                break;
            default:
                break;
        }
    }

    private boolean isAdmain() {
        if (currentMotorcade != null) {
            return currentMotorcade.getAdminId() == UserUtil.getCurrentUser().getId();
        }
        return false;
    }

    private void showShareCmdWindow() {
        if (currentMotorcade == null) {
            return;
        }
        ShareCommandFragment fragment = ShareCommandFragment.newInstance(currentMotorcade.getHxGroupId());
        fragment.setCallBack(new ShareCommandFragment.MapCallBack() {
            @Override
            public void goToMap() {
                if (!NetworkUtils.isConnected(getApplicationContext())) {
                    showToastException(R.string.net_work);
                    return;
                }
                if (currentMotorcade != null) {
                    mainVM.jumpToMeeting(currentMotorcade.getId());
                } else {
                    showToastException(R.string.join_failed_error);
                }
            }

            @Override
            public void jumpToShare() {
                if (currentMotorcade == null) {
                    showToastException(R.string.join_failed_error);
                    return;
                }
                ShareClubBean bean = new ShareClubBean();
                bean.setFromPackage(getPackageName());
                bean.setBackAction(ShareConstants.MOTORCADE_HANDLE_SHARE_ACTION);
                bean.setCoreKey(currentMotorcade.getHxGroupId());
                bean.setShareImage(currentMotorcade.getPicPath());
                bean.setShareTitle(UserUtil.getCurrentUser().getName() + getString(R.string.motor_name, currentMotorcade.getHxGroupId()));
                bean.setShareContent(getString(R.string.motor_command));
                bean.setCarTeamDetail(currentMotorcade.getCount() + "," + currentMotorcade.getNick() + "," + currentMotorcade.getHxGroupId());
                bean.setCarTeamId(currentMotorcade.getId());
                ShareUtils.shareToClub(SettingActivity.this, bean, new ShareCallBack() {
                    @Override
                    public void shareError(String errorMsg) {
                        if (!TextUtils.isEmpty(errorMsg)) {
                            showToastException(errorMsg);
                        }
                    }

                    @Override
                    public void shareSuccess() {

                    }
                });
            }

            @Override
            public void onBack() {
                onBackPressed();
            }
        });
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.slide_in_left, R.anim.slide_out_left,
                R.anim.slide_in_left, R.anim.slide_out_left);
        transaction.add(R.id.view_content, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    private void setDialog(@StringRes final int resId, CharSequence name, int dialogType) {
        MotorcadeDialogUtils.showMotorcadeNameDialog(this, getSupportFragmentManager(),
                new MotorcadeDialogUtils.DialogClickListener() {
                    @Override
                    public void onSure(String text) {
                        if (resId == R.string.setting_motorcade_name) {
                            changeMotorcadeName(text);
                        } else if (resId == R.string.setting_motorcade_nickname) {
                            changeNickName(text);
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                }, name, dialogType);
    }

    private void setExitDialog() {
        MotorcadeDialogUtils.showExitDialog(this, getSupportFragmentManager(),
                new MotorcadeDialogUtils.DialogClickListener() {
                    @Override
                    public void onSure(String text) {
                        quitCurrentTeam();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    // 退出车队请求
    protected void quitCurrentTeam() {
        if (currentMotorcade == null) {
            return;
        }
        String motorcadeId = String.valueOf(currentMotorcade.getId());
        RequestManager.quitMotorcade(motorcadeId, new CallbackWrapper<BaseResult>() {

            @Override
            public BaseResult parse(String data) throws Exception {
                return GsonHelper.fromJson(data, BaseResult.class);
            }

            @Override
            public void onSuccess(BaseResult model) {
                super.onSuccess(model);
                if (model != null && (model.isSuccess() || model.isRepeat())) {
                    //添加成功才能进入会话
                    XMToast.toastSuccess(SettingActivity.this, R.string.quit_success);
                    MainActivity.launch(SettingActivity.this);
                } else {
                    XMToast.toastException(SettingActivity.this, R.string.quit_failed);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                XMToast.toastException(SettingActivity.this, getString(R.string.request_error));
            }
        });
    }

    // 请求修改车队名称
    private void changeMotorcadeName(final String newName) {
        RequestManager.ChangeMotorcadeName(newName, currentMotorcade.getId(), new CallbackWrapper<BaseResult>() {

            @Override
            public BaseResult parse(String data) throws Exception {
                return GsonHelper.fromJson(data, BaseResult.class);
            }

            @Override
            public void onSuccess(BaseResult model) {
                super.onSuccess(model);
                if (model != null && (model.isSuccess() || model.isRepeat())) {
                    //添加成功才能进入会话
                    XMToast.toastSuccess(SettingActivity.this, R.string.change_success);
                    updateTextInMainThread(nameTv, newName);
                } else {
                    XMToast.toastException(SettingActivity.this, R.string.change_failed);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                XMToast.toastException(SettingActivity.this, R.string.change_failed);
            }
        });
    }

    // 请求修改用户昵称
    private void changeNickName(final String newName) {
        RequestManager.changeNickName(newName, currentMotorcade.getId(), new CallbackWrapper<BaseResult>() {

            @Override
            public BaseResult parse(String data) throws Exception {
                return GsonHelper.fromJson(data, BaseResult.class);
            }

            @Override
            public void onSuccess(BaseResult model) {
                super.onSuccess(model);
                if (model != null && (model.isSuccess() || model.isRepeat())) {
                    //添加成功才能进入会话
                    XMToast.toastSuccess(SettingActivity.this, R.string.change_success);
                    updateTextInMainThread(nickNameTv, newName);
                    statusFragment.fetchMembers();
                } else {
                    XMToast.toastException(SettingActivity.this, R.string.change_failed);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                XMToast.toastException(SettingActivity.this, R.string.change_failed);
            }
        });
    }

    private StatusFragment.CallBack callBack = new StatusFragment.CallBack() {
        @Override
        public void getNickCompleted(String nickName) {
            nickNameTv.setText(nickName);
        }

        @Override
        public void onItemClick(GroupMemberInfo memberInfo) {
            if (memberInfo.getId() == UserUtil.getCurrentUser().getId()) return;
            showFragment(FriendDetailsFragment.newInstance(memberInfo.getHxAccount(),
                    memberInfo.getId(), memberInfo.getNickName()), true);
        }
    };

    private void updateTextInMainThread(final TextView view, final String text) {
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                view.setText(text);
            }
        });
    }

    public void showFragment(Fragment fragment, boolean slideIn) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (slideIn) {
            transaction.setCustomAnimations(
                    R.anim.slide_in_left, R.anim.slide_out_left,
                    R.anim.slide_in_left, R.anim.slide_out_left);
        }
        transaction.add(R.id.view_content, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }


}
