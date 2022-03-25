package com.xiaoma.smarthome.login.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.smarthome.R;
import com.xiaoma.smarthome.common.model.MiUserInfo;
import com.xiaoma.smarthome.common.utils.QrCodeUtil;
import com.xiaoma.smarthome.common.vm.XiaoMiVM;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.utils.log.KLog;

public class XiaoMiLoginFragment extends BaseFragment {

    private static final int CHECK_INTERVAL = 1500;
    private static final int MAX_CHECK_COUNT_NUM = 100;
    private int checkCount;

    private ImageView ivLoginIv;
    private View lableView;
    private TextView tvTitle;
    private TextView tvLable;

    private XiaoMiVM xiaoMiVM;

    private TextView mTvError;
    private ImageView mIvErrorCode;

    private Runnable checkBindStateRunnable = new Runnable() {
        @Override
        public void run() {
            if (checkCount < MAX_CHECK_COUNT_NUM) {
                checkCount++;
                xiaoMiVM.checkUserBindMi();

            } else {
                ThreadDispatcher.getDispatcher().remove(this);
            }
        }
    };

    public static XiaoMiLoginFragment newInstance() {
        return new XiaoMiLoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_xiao_mi_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);
    }

    private void initView(View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        tvLable = view.findViewById(R.id.tv_lable);
        ivLoginIv = view.findViewById(R.id.iv_code);
        lableView = view.findViewById(R.id.lable_view);
        mIvErrorCode = view.findViewById(R.id.iv_error_code);
        mTvError = view.findViewById(R.id.tv_error);

        lableView.setVisibility(View.GONE);
        tvTitle.setVisibility(View.GONE);
        tvLable.setVisibility(View.GONE);
        ivLoginIv.setVisibility(View.INVISIBLE);

        xiaoMiVM = ViewModelProviders.of(this).get(XiaoMiVM.class);

        mIvErrorCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xiaoMiVM.fetchMiAuthInfo();
            }
        });

        //检查绑定状态
        xiaoMiVM.getXmBindStatus().observe(this, new Observer<XmResource<XMResult<String>>>() {
            @Override
            public void onChanged(@Nullable XmResource<XMResult<String>> xmResultXmResource) {
                if (xmResultXmResource == null) {
                    return;
                }
                xmResultXmResource.handle(new OnCallback<XMResult<String>>() {
                    @Override
                    public void onSuccess(XMResult<String> data) {
                        KLog.d("onSuccess", getString(R.string.bind_success_fetch_data));
                        ThreadDispatcher.getDispatcher().remove(checkBindStateRunnable);
                        //绑定成功，获取信息
                        xiaoMiVM.fetchMiUserInfo();
                        XmTracker.getInstance().uploadEvent(-1, TrackerCountType.LOGINSMARTACCOUNT.getType());
                    }

                    @Override
                    public void onFailure(String msg) {
                        KLog.d("onFailure", "getXmBindStatus msg:" + msg);
                        lableView.setVisibility(View.VISIBLE);
                        tvLable.setVisibility(View.VISIBLE);
                        tvTitle.setVisibility(View.VISIBLE);

                        if (ivLoginIv.getVisibility()==View.VISIBLE){
                            //如果已经显示过二维码
                            ThreadDispatcher.getDispatcher().postDelayed(checkBindStateRunnable, CHECK_INTERVAL);
                        }else {
                            //未绑定，去拉取登陆二维码
                            xiaoMiVM.fetchMiAuthInfo();
                        }
                    }
                });
            }
        });

        //获取用户信息
        xiaoMiVM.getMiUserInfo().observe(this, new Observer<XmResource<MiUserInfo>>() {
            @Override
            public void onChanged(@Nullable XmResource<MiUserInfo> miUserInfoXmResource) {
                if (miUserInfoXmResource == null) {
                    return;
                }
                miUserInfoXmResource.handle(new OnCallback<MiUserInfo>() {
                    @Override
                    public void onSuccess(MiUserInfo data) {
                        if (data != null) {
                            //显示登录成功页面
                            startLoginSuccessFragment(data);
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        Log.d("onFailure", "getMiUserInfo msg:" + msg);
                        showToastException(R.string.fetch_user_data_fail);
                    }
                });
            }
        });

        //授权
        xiaoMiVM.getMiAuthInfo().observe(this, new Observer<XmResource<XMResult<String>>>() {
            @Override
            public void onChanged(@Nullable XmResource<XMResult<String>> xmResultXmResource) {
                if (xmResultXmResource == null) {
                    return;
                }
                xmResultXmResource.handle(new OnCallback<XMResult<String>>() {
                    @Override
                    public void onSuccess(XMResult<String> data) {
                        String loginUrl = data.getData();
//                        ivLoginIv.setImageBitmap(QrCodeUtil.generateBitmap(loginUrl, 382, 382));
                        ImageLoader.with(mContext)
                                .load(QrCodeUtil.generateBitmap(loginUrl, 382, 382))
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .dontAnimate()
                                .listener(glideListener)
                                .into(ivLoginIv);
                        ThreadDispatcher.getDispatcher().postDelayed(checkBindStateRunnable, CHECK_INTERVAL);
                    }

                    @Override
                    public void onFailure(String msg) {
                        Log.d("onFailure", "getMiAuthInfo msg:" + msg);
                        showToastException(getString(R.string.auth_fail_msg, msg));
                        ivLoginIv.setVisibility(View.INVISIBLE);
                        mIvErrorCode.setVisibility(View.VISIBLE);
                        mTvError.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        //查询用户绑定状态
        xiaoMiVM.checkUserBindMi();
    }

    private RequestListener glideListener = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            if (mIvErrorCode != null && ivLoginIv != null && mTvError != null) {
                ivLoginIv.setVisibility(View.INVISIBLE);
                mIvErrorCode.setVisibility(View.VISIBLE);
                mTvError.setVisibility(View.VISIBLE);
                ThreadDispatcher.getDispatcher().remove(checkBindStateRunnable);
            }
            KLog.d("ym GlideException: " + (e != null ? e.getMessage() : ""));
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            if (mIvErrorCode != null && ivLoginIv != null && mTvError != null) {
                ivLoginIv.setVisibility(View.VISIBLE);
                mIvErrorCode.setVisibility(View.GONE);
                mTvError.setVisibility(View.GONE);
            }
            return false;
        }
    };

    private void startLoginSuccessFragment(MiUserInfo data) {
        if (getActivity() != null) {
            //将登录fragment出栈
            getActivity().getSupportFragmentManager().popBackStack();
            //登录成功页面
            FragmentUtils.replace(getActivity().getSupportFragmentManager(), XiaoMiLoginSuccessFragment.newInstance(data),
                    R.id.container_frame, MainActivity.FRAGMENT_TAG_XIAOMAI_LOGIN_SUCESS, true);
        }
    }
}
