package com.xiaoma.smarthome.login.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.smarthome.R;
import com.xiaoma.smarthome.common.model.MiUserInfo;
import com.xiaoma.smarthome.common.vm.XiaoMiVM;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.utils.FragmentUtils;

public class XiaoMiLoginSuccessFragment extends BaseFragment implements View.OnClickListener {

    public static final String MIUSERINFO = "MiUserInfo";

    private TextView tvAccount;
    private ImageView ivHead;
    private XiaoMiVM mXiaoMiVM;

    public static XiaoMiLoginSuccessFragment newInstance(MiUserInfo data) {
        XiaoMiLoginSuccessFragment fragment = new XiaoMiLoginSuccessFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(MIUSERINFO, data);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_xiao_mi_login_success, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);
    }

    private void initView(View view) {
        if (getArguments() == null) {
            return;
        }
        MiUserInfo info = (MiUserInfo) getArguments().getSerializable(MIUSERINFO);
        view.findViewById(R.id.btn_login_out).setOnClickListener(this);
        mXiaoMiVM = ViewModelProviders.of(this).get(XiaoMiVM.class);
        tvAccount = view.findViewById(R.id.xiaomi_accout);
        ivHead = view.findViewById(R.id.xiaomi_head);

        tvAccount.setText(getString(R.string.xiaomia_login_account, info.getMiliaoNick()));

        ImageLoader.with(XiaoMiLoginSuccessFragment.this)
                .load(info.getMiliaoIcon_320())
                .placeholder(R.drawable.image_head_default)
                .transform(new CircleCrop())
                .into(ivHead);

        mXiaoMiVM.getMiLogOut().observe(this, new Observer<XmResource<XMResult<String>>>() {
            @Override
            public void onChanged(@Nullable XmResource<XMResult<String>> xmResultXmResource) {
                if (xmResultXmResource == null) {
                    return;
                }
                xmResultXmResource.handle(new OnCallback<XMResult<String>>() {
                    @Override
                    public void onSuccess(XMResult<String> data) {
                        showToast(R.string.logout_success);
                        if (getActivity() != null) {
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        showToastException(R.string.logout_fail);
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login_out) {
            ConfirmDialog dialog = new ConfirmDialog(getActivity());
            dialog.setContent(getString(R.string.xiaomia_login_out_dialog_content))
                    .setPositiveButton(getString(R.string.sure), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            //退出登录
                            mXiaoMiVM.logOutMi();
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }
}
