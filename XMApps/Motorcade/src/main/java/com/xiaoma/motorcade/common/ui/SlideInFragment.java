package com.xiaoma.motorcade.common.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.motorcade.R;
import com.xiaoma.ui.navi.NavigationBar;

import java.util.Objects;

/**
 * Created by LKF on 2018-12-28 0028.
 * 侧滑进场/退场效果的Fragment
 */
public abstract class SlideInFragment extends BaseFragment {
    abstract protected int getSlideViewId();

    private boolean mNaviIsShowOnActivity;

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Animation anim = AnimationUtils.loadAnimation(getContext(), enter ? R.anim.slide_in_left : R.anim.slide_out_left);
        if (listener != null) {
            anim.setAnimationListener(listener);
        }
        Objects.requireNonNull(getView()).findViewById(getSlideViewId()).startAnimation(anim);
        return FragmentTransitAnimHelper.onCreateAnimation(anim.getDuration());
    }

    @Nullable
    @Override
    final public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                   @Nullable Bundle savedInstanceState) {
        final NavigationBar naviFromActivity = getNaviBar();
        if (naviFromActivity == null || !naviFromActivity.isShown()) {
            final View v = inflater.inflate(R.layout.fmt_slide_in, container, false);

            final NavigationBar naviFromFmt = v.findViewById(R.id.navigationBar);
            naviFromFmt.showBackNavi();

            final View slideContainer = v.findViewById(R.id.slide_content);
            final View slideContent = onCreateWrapView(slideContainer);
            ((ViewGroup) slideContainer).addView(slideContent);
            return v;
        } else {
            mNaviIsShowOnActivity = true;
            naviFromActivity.showBackNavi();
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

    private AnimationStateListener listener;

    public void setListener(AnimationStateListener listener) {
        this.listener = listener;
    }

    public class AnimationStateListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

}
