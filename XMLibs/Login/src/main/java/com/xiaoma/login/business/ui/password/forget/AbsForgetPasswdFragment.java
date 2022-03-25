package com.xiaoma.login.business.ui.password.forget;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xiaoma.component.base.BaseFragment;

/**
 * Created by kaka
 * on 19-5-24 下午5:19
 * <p>
 * desc: #a
 * </p>
 */
public abstract class AbsForgetPasswdFragment extends BaseFragment {

    protected ForgetPasswdVM mForgetPasswdVM;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mForgetPasswdVM = ViewModelProviders.of(getActivity()).get(ForgetPasswdVM.class);
    }
}
