package com.xiaoma.music.online.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.music.R;
import com.xiaoma.music.common.constant.EventBusTags;
import com.xiaoma.music.common.constant.MusicConstants;
import com.xiaoma.music.online.model.Category;
import com.xiaoma.utils.BackHandlerHelper;
import com.xiaoma.utils.FragmentUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

/**
 * @author zs
 * @date 2018/10/9 0009.
 */
public class OnlineMusicFragment extends BaseFragment implements BackHandlerHelper.FragmentBackHandler {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_online_music, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (MusicConstants.SHOW_ONLINE_TABLE) {
            FragmentUtils.add(getChildFragmentManager(), OnlineHomeFragment.newInstance(), R.id.child_online_fragment_content);
        }
    }


    @Subscriber(tag = EventBusTags.SKIP_TO_CATEGORY_FRAGMENT)
    public void onSkipToCategoryDetail(Category category) {
        skipToNextFragment(CategoryDetailFragment.newInstance(category));
    }

    public void skipToNextFragment(Fragment fragment) {
        FragmentUtils.replace(getChildFragmentManager(), fragment, R.id.child_online_fragment_content, true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onBackPressed() {
        int count = getChildFragmentManager().getBackStackEntryCount();
        if (count > 0) {
            FragmentUtils.pop(getChildFragmentManager());
            return true;
        } else {
            return BackHandlerHelper.handleBackPress(this);
        }
    }
}
