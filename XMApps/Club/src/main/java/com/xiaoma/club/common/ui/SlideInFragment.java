package com.xiaoma.club.common.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.xiaoma.club.R;
import com.xiaoma.club.common.util.FragmentTransitAnimHelper;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.ui.navi.NavigationBar;

import java.util.Objects;

/**
 * Created by LKF on 2018-12-28 0028.
 * 侧滑进场/退场效果的Fragment
 */
public abstract class SlideInFragment extends BaseFragment {

    private NavigationBar naviBar;

    abstract protected int getSlideViewId();

    private boolean mNaviIsShowOnActivity;

    @Nullable
    @Override
    final public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        naviBar = getNaviBar();
        if (naviBar == null || !naviBar.isShown()) {
            final View v = inflater.inflate(R.layout.fmt_slide_in, container, false);

            naviBar = v.findViewById(R.id.navigationBar);
            naviBar.showBackNavi();

            final View slideContainer = v.findViewById(R.id.slide_content);
            final View slideContent = onCreateWrapView(slideContainer);
            ((ViewGroup) slideContainer).addView(slideContent);
            return v;
        } else {
            mNaviIsShowOnActivity = true;
            naviBar.showBackNavi();
            return onCreateWrapView(container);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mNaviIsShowOnActivity) {
            getNaviBar().showBackAndHomeNavi();
        }
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        final Animation anim = AnimationUtils.loadAnimation(getContext(), enter ? R.anim.slide_in_left : R.anim.slide_out_left);
        Objects.requireNonNull(getView()).findViewById(getSlideViewId()).startAnimation(anim);
        return FragmentTransitAnimHelper.onCreateAnimation(anim.getDuration());
    }

    public NavigationBar getSlideNaviBar(){
        return naviBar;
    }
}
