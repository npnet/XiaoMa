package com.xiaoma.motorcade.main.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoma.model.XmResource;
import com.xiaoma.motorcade.MainActivity;
import com.xiaoma.motorcade.R;
import com.xiaoma.motorcade.common.constants.MotorcadeConstants;
import com.xiaoma.motorcade.common.model.GroupCardInfo;
import com.xiaoma.motorcade.common.model.MeetingInfo;
import com.xiaoma.motorcade.common.ui.MotorcadeBaseFragment;
import com.xiaoma.motorcade.common.utils.MotorcadeDialogUtils;
import com.xiaoma.motorcade.main.vm.MainVM;
import com.xiaoma.motorcade.map.ui.MotorcadeConferenceActivity;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.tputils.TPUtils;

/**
 * Created by ZYao.
 * Date ：2019/1/21 0021
 */
public class InitialMotorcadeFragment extends MotorcadeBaseFragment implements View.OnClickListener {

    private MainVM mainVM;
    private Handler handler = new Handler(Looper.getMainLooper());

    public static InitialMotorcadeFragment newInstance() {
        return new InitialMotorcadeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_initial_motorcade, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initVM();
    }

    private void initView(View view) {
        view.findViewById(R.id.btn_join_motorcade).setOnClickListener(this);
        view.findViewById(R.id.btn_create_motorcade).setOnClickListener(this);
        TPUtils.put(mContext, MotorcadeConstants.TP_FIRST_START_APP, false);
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

    private void joinFailed() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                showToastException(R.string.join_failed);
                dismissProgress();
            }
        });
    }

    private void joinSuccess(final MeetingInfo info) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                dismissProgress();
                MotorcadeConferenceActivity.launcherMapActivity(mContext, info, false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_join_motorcade:
                if (!NetworkUtils.isConnected(getContext())) {
                    showToastException(R.string.net_work);
                    return;
                }
                MotorcadeDialogUtils.showJoinDialog(getActivity(), getChildFragmentManager(),
                        new MotorcadeDialogUtils.DialogClickListener() {
                            @Override
                            public void onSure(String text) {
                                joinMotorcade(text);
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                break;
            case R.id.btn_create_motorcade:
                if (!NetworkUtils.isConnected(getContext())) {
                    showToastException(R.string.net_work);
                    return;
                }
                MotorcadeDialogUtils.showCreateDialog(getActivity(), getChildFragmentManager(),
                        new MotorcadeDialogUtils.DialogClickListener() {
                            @Override
                            public void onSure(String text) {
                                createMotorcade(text);
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                break;
        }
    }


    @Override
    public void operateAfterCreate(GroupCardInfo info) {
        if (callBack != null) {
            callBack.replaceMain(true);
        }
    }

    @Override
    public void operateAfterQuit() {

    }

    @Override
    public void operateAfterJoin() {
        if (callBack != null) {
            callBack.replaceMain(true);
        }
    }

    public void openMap(GroupCardInfo model) {
        if (isDestroy()) {
            return;
        }
        dismissProgress();
        showProgressDialog(R.string.join_loading);
        mainVM.jumpToMeeting(model.getId());
    }

    private MainActivity.ReplaceMainCallBack callBack;

    public void setCallBack(MainActivity.ReplaceMainCallBack callBack) {
        this.callBack = callBack;
    }

}
