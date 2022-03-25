package com.xiaoma.login.business.ui.subaccount.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.login.business.ui.subaccount.CreateSubAccountVM;

/**
 * Created by kaka
 * on 19-5-24 下午5:19
 * <p>
 * desc: #a
 * </p>
 */
public abstract class AbsCreateSubAccountFragment extends BaseFragment {

    protected CreateSubAccountVM mCreateSubAccountVM;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCreateSubAccountVM = ViewModelProviders.of(getActivity()).get(CreateSubAccountVM.class);
    }
}
