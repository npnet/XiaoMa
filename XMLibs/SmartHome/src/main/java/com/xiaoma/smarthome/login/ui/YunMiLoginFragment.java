package com.xiaoma.smarthome.login.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.xiaoma.model.XmResource;
import com.xiaoma.smarthome.R;
import com.xiaoma.smarthome.common.constants.SmartConstants;
import com.xiaoma.smarthome.common.manager.CMDeviceManager;
import com.xiaoma.smarthome.common.manager.CMSceneDataManager;
import com.xiaoma.smarthome.login.model.CMUserInfo;
import com.xiaoma.smarthome.login.model.CloudMIBean;
import com.xiaoma.smarthome.login.model.XiaoMiBean;
import com.xiaoma.smarthome.login.vm.LoginVM;
import com.xiaoma.smarthome.scene.ui.SelectSceneFragment;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.utils.log.KLog;

public class YunMiLoginFragment extends BaseFragment {

    private ImageView ivLoginIv;
    private View lableView;
    private TextView tvTitle;
    private TextView tvLable;

    private LoginVM mLoginVM;

    private static final int CHECK_INTERVAL = 1500;
    private static final int MAX_CHECK_COUNT_NUM = 80;
    private int checkCount;

    private Runnable checkStateRunnable = new Runnable() {
        @Override
        public void run() {
            if (checkCount < MAX_CHECK_COUNT_NUM) {
                checkCount++;
                mLoginVM.fetchCMLoginBean();

            } else {
                ThreadDispatcher.getDispatcher().remove(checkStateRunnable);
//                showToast(getString(R.string.refresh_code));
                mLoginVM.fetchCMLoginCode();
                KLog.d("state checkCount finish");
            }
            KLog.d("state checkCount: " + checkCount);
        }
    };
    private TextView mTvError;
    private ImageView mIvErrorCode;

    public static YunMiLoginFragment newInstance() {
        return new YunMiLoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_yun_mi_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);
    }

    private void initView(View view) {
        ivLoginIv = view.findViewById(R.id.iv_code);
        lableView = view.findViewById(R.id.lable_view);
        tvTitle = view.findViewById(R.id.tv_title);
        tvLable = view.findViewById(R.id.tv_lable);
        mIvErrorCode = view.findViewById(R.id.iv_error_code);
        mTvError = view.findViewById(R.id.tv_error);

        mLoginVM = ViewModelProviders.of(this).get(LoginVM.class);

        mIvErrorCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginVM.fetchCMLoginCode();
            }
        });

        lableView.setVisibility(View.GONE);
        tvTitle.setVisibility(View.GONE);
        tvLable.setVisibility(View.GONE);

        //获取云米登录状态
        mLoginVM.getCmLoginState().observe(this, new Observer<XmResource<String>>() {
            @Override
            public void onChanged(@Nullable XmResource<String> stringXmResource) {
                if (stringXmResource == null) {
                    return;
                }
                stringXmResource.handle(new OnCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        //用户已授权则无需拉取二维码,直接查询用户信息
                        mLoginVM.queryCMUserInfo();
                    }

                    @Override
                    public void onError(int code, String message) {
                        //获取二维码图片
                        mLoginVM.fetchCMLoginCode();
                        lableView.setVisibility(View.VISIBLE);
                        tvTitle.setVisibility(View.VISIBLE);
                        tvLable.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        //云米登录成功后查询用户信息
        mLoginVM.getCMUserInfo().observe(this, new Observer<XmResource<CMUserInfo>>() {
            @Override
            public void onChanged(@Nullable XmResource<CMUserInfo> cmUserInfoXmResource) {
                if (cmUserInfoXmResource == null) {
                    return;
                }
                cmUserInfoXmResource.handle(new OnCallback<CMUserInfo>() {
                    @Override
                    public void onSuccess(CMUserInfo data) {
                        //更新云米token
                        CMSceneDataManager.getInstance().setCmToken(data.getToken());
                        //登陆云米sdk
                        XiaoMiBean xiaomi = data.getXiaomi();
                        if (xiaomi != null) {
                            CMDeviceManager.getInstance().loginCM(xiaomi);
                        }
                        startSelectSceneFragment(data.getHeadImg(), data.getNickName());
                    }

                    @Override
                    public void onError(int code, String message) {
                        XMToast.toastException(getContext(), getString(R.string.error_net_msg, message));
                        //登录已过期
                        if (code == SmartConstants.YUN_MI_LOGIN_EXPIRED) {
                            mLoginVM.logout();
                        }
                    }
                });
            }
        });

        //获取云米登录二维码
        mLoginVM.getLoginCodeData().observe(this, new Observer<XmResource<String>>() {
            @Override
            public void onChanged(@Nullable XmResource<String> cloudMILoginCodeBeanXmResource) {
                if (cloudMILoginCodeBeanXmResource == null) {
                    return;
                }
                cloudMILoginCodeBeanXmResource.handle(new OnCallback<String>() {
                    @Override
                    public void onSuccess(String code) {
                        ImageLoader.with(YunMiLoginFragment.this)
                                .load(code)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .dontAnimate()
                                .listener(glideListener)
                                .into(ivLoginIv);
                        //开始轮询是否登陆成功
                        ThreadDispatcher.getDispatcher().postDelayed(checkStateRunnable, CHECK_INTERVAL);
                    }

                    @Override
                    public void onError(int code, String message) {
                        XMToast.toastException(getContext(), R.string.error_net_fetch_code_fail);
                        ivLoginIv.setVisibility(View.INVISIBLE);
                        mIvErrorCode.setVisibility(View.VISIBLE);
                        mTvError.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        //轮询获取云米登录结果
        mLoginVM.getCloudMIBean().observe(this, new Observer<XmResource<CloudMIBean>>() {
            @Override
            public void onChanged(@Nullable XmResource<CloudMIBean> cloudMIBeanXmResource) {
                if (cloudMIBeanXmResource == null) {
                    return;
                }
                cloudMIBeanXmResource.handle(new OnCallback<CloudMIBean>() {
                    @Override
                    public void onSuccess(CloudMIBean data) {
                        //更新云米token
                        CMSceneDataManager.getInstance().setCmToken(data.getToken());
                        XiaoMiBean xiaomi = data.getXiaomi();
                        if (xiaomi != null) {
                            //登陆云米sdk
                            CMDeviceManager.getInstance().loginCM(xiaomi);
                            XmTracker.getInstance().uploadEvent(-1, TrackerCountType.LOGINSMARTACCOUNT.getType());
                        }
                        checkCount = 0;
                        ThreadDispatcher.getDispatcher().remove(checkStateRunnable);
                        startSelectSceneFragment(data.getHeadImg(), data.getNickName());
                    }

                    @Override
                    public void onError(int code, String message) {
                        KLog.d("fetch userinfo onError:" + message);
                        ThreadDispatcher.getDispatcher().postDelayed(checkStateRunnable, CHECK_INTERVAL);
                    }
                });
            }
        });

        mLoginVM.fetchCMLoginState();
    }

    private RequestListener glideListener = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            if (mIvErrorCode != null && ivLoginIv != null && mTvError != null) {
                ivLoginIv.setVisibility(View.INVISIBLE);
                mIvErrorCode.setVisibility(View.VISIBLE);
                mTvError.setVisibility(View.VISIBLE);
                ThreadDispatcher.getDispatcher().remove(checkStateRunnable);
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

    private void startSelectSceneFragment(String head, String nick) {
        if (getActivity() != null) {
            //将云米登录fragment出栈
            getActivity().getSupportFragmentManager().popBackStack();
            //打开场景列表
            FragmentUtils.replace(getActivity().getSupportFragmentManager(), SelectSceneFragment.newInstance(head, nick),
                    R.id.container_frame, MainActivity.FRAGMENT_TAG_YUN_MI_SELECTSCENE, true);
        }
    }
}
