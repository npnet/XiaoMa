package com.xiaoma.motorcade.setting.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import com.xiaoma.motorcade.R;
import com.xiaoma.motorcade.common.ui.SlideInFragment;
import com.xiaoma.motorcade.common.utils.MotorcadeDialogUtils;

/**
 * @author zs
 * @date 2019/1/29 0029.
 */
public class ShareCommandFragment extends SlideInFragment implements View.OnClickListener {

    private static final String KEY_COMMAND = "key_command";
    private TextView commandTv;

    public static ShareCommandFragment newInstance(String command) {
        ShareCommandFragment fragment = new ShareCommandFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_COMMAND, command);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected View onCreateWrapView(View childView) {
        return LayoutInflater.from(childView.getContext()).inflate(R.layout.activity_share_command,
                (ViewGroup) childView, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        commandTv = view.findViewById(R.id.share_command_content_tv);
        view.findViewById(R.id.share_command_btn).setOnClickListener(this);
        view.findViewById(R.id.enter_home_btn).setOnClickListener(this);
        view.findViewById(R.id.command_bg_parent).setOnClickListener(this);
        if (getArguments() == null || (getArguments().getString(KEY_COMMAND)) == null) {
            return;
        }
        commandTv.setText(getArguments().getString(KEY_COMMAND));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_command_btn:
                showShareDialog();
                break;
            case R.id.enter_home_btn:
                if (callBack != null) {
                    callBack.goToMap();
                }
                break;
            case R.id.command_bg_parent:
                back();
                break;
            default:
                break;
        }
    }

    private void back() {
        if (callBack != null) {
            callBack.onBack();
        }
    }

    private void showShareDialog() {
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        MotorcadeDialogUtils.showShareCommandDialog(activity, activity.getSupportFragmentManager(),
                new MotorcadeDialogUtils.DialogClickListener() {
                    @Override
                    public void onSure(String text) {
                        setListener(new AnimationStateListener() {
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                super.onAnimationEnd(animation);
                                if (callBack != null) {
                                    callBack.jumpToShare();
                                }
                            }
                        });
                        back();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    @Override
    protected int getSlideViewId() {
        return R.id.content_ll;
    }

    private MapCallBack callBack;

    public void setCallBack(MapCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MapCallBack {
        void goToMap();

        void jumpToShare();

        void onBack();
    }
}
