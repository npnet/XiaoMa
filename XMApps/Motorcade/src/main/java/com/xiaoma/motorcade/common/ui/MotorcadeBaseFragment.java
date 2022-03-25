package com.xiaoma.motorcade.common.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.XMResult;
import com.xiaoma.motorcade.R;
import com.xiaoma.motorcade.common.constants.MotorcadeConstants;
import com.xiaoma.motorcade.common.manager.RequestManager;
import com.xiaoma.motorcade.common.model.BaseResult;
import com.xiaoma.motorcade.common.model.GroupCardInfo;
import com.xiaoma.motorcade.common.utils.PatternUtils;
import com.xiaoma.motorcade.common.utils.TextTypeUtil;
import com.xiaoma.motorcade.setting.view.ShareCommandFragment;
import com.xiaoma.network.callback.CallbackWrapper;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;

/**
 * 简介: 第一次推荐页和主页有共同的操作，所以建了基类
 *
 * @author lingyan
 */
public abstract class MotorcadeBaseFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    // 退出车队请求
    protected void quitCurrentTeam(GroupCardInfo motorcade) {
        String motorcadeId = String.valueOf(motorcade.getId());
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
                    XMToast.toastSuccess(getActivity(), R.string.quit_success);
                    operateAfterQuit();
                } else {
                    XMToast.toastException(getActivity(), R.string.quit_failed, false);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                XMToast.toastException(getContext(), getString(R.string.request_error));
            }
        });
    }

    // 创建车队请求
    protected void createMotorcade(String motorcadeName) {
        showProgressDialog(R.string.base_loading);
        // 请求创建车队
        RequestManager.createMotorcade(motorcadeName, new CallbackWrapper<GroupCardInfo>() {

            @Override
            public GroupCardInfo parse(String data) throws Exception {
                XMResult<GroupCardInfo> info = GsonHelper.fromJson(data, new TypeToken<XMResult<GroupCardInfo>>() {
                }.getType());
                if (info == null || !info.isSuccess()) {
                    return null;
                }
                return info.getData();
            }

            @Override
            public void onSuccess(GroupCardInfo model) {
                super.onSuccess(model);
                if (model != null) {
                    // 添加成功才能进入会话
                    XMToast.toastSuccess(getActivity(), R.string.create_team_success);
                    operateAfterCreate(model);
                }
                dismissProgress();
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                XMToast.toastException(getContext(), getString(R.string.request_error));
                dismissProgress();
            }
        });
    }

    // 加入车队请求
    protected void joinMotorcade(String command) {
        if (!PatternUtils.isNumberic(command)) {
            XMToast.toastException(getActivity(), getString(R.string.please_input_numbers));
            return;
        }
        RequestManager.requestAddGroup(command, new CallbackWrapper<BaseResult>() {

            @Override
            public BaseResult parse(String data) throws Exception {
                return GsonHelper.fromJson(data, BaseResult.class);
            }

            @Override
            public void onSuccess(BaseResult model) {
                super.onSuccess(model);
                if (model != null && (model.isSuccess() || model.isRepeat())) {
                    //添加成功才能进入会话
                    XMToast.toastSuccess(getActivity(), R.string.joined_success);
                    operateAfterJoin();
                    XmTracker.getInstance().uploadEvent(-1, TrackerCountType.JOINMOTORCADE.getType());
                } else if (model.getResultMessage() != null && TextTypeUtil.hasChinese(model.getResultMessage())) {
                    XMToast.toastException(getActivity(), model.getResultMessage());
                } else {
                    XMToast.toastException(getActivity(), R.string.join_motorcade_failed);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                XMToast.toastException(getContext(), getString(R.string.request_error));
            }
        });
    }

    public abstract void operateAfterCreate(GroupCardInfo info);

    public abstract void operateAfterQuit();

    public abstract void operateAfterJoin();

    public void openShare(final GroupCardInfo model) {
        if (model == null) {
            return;
        }
        final ShareCommandFragment fragment = ShareCommandFragment.newInstance(model.getHxGroupId());
        fragment.setCallBack(new ShareCommandFragment.MapCallBack() {
            @Override
            public void goToMap() {
                if (!NetworkUtils.isConnected(getContext())) {
                    showToastException(R.string.net_work);
                    return;
                }
                EventBus.getDefault().post(model, MotorcadeConstants.EventTag.OPEN_MAP);
                onBack();
            }

            @Override
            public void jumpToShare() {
                if (!NetworkUtils.isConnected(getContext())) {
                    showToastException(R.string.net_work);
                    return;
                }
                if (model == null) {
                    showToastException(R.string.join_failed_error);
                    return;
                }
                EventBus.getDefault().post(model, MotorcadeConstants.EventTag.JUMP_SHARE);
            }

            @Override
            public void onBack() {
                try {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .remove(fragment)
                            .commitAllowingStateLoss();
                } catch (Exception ignored) {
                    KLog.e(ignored.getMessage());
                }

            }
        });
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.slide_in_left, R.anim.slide_out_left,
                R.anim.slide_in_left, R.anim.slide_out_left);
        transaction.add(R.id.view_content, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

}
