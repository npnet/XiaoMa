package com.xiaoma.xting.online.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.VisibilityFragment;

import static com.xiaoma.xting.common.XtingConstants.SUPPORT_ONLINE_FM;

/**
 * @author KY
 * @date 2018/10/10
 */
@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_FM_NET)
public class NetFMFragment extends VisibilityFragment {
    public static final String FRAGMENT_TAG_NET_FM_INDEX = "FRAGMENT_TAG_NET_FM_INDEX";
    public static final String FRAGMENT_TAG_CHILD_BOARD = "FRAGMENT_TAG_CHILD_BOARD";
    public static final String FRAGMENT_TAG_CHILD_ALBUM_CLASS = "FRAGMENT_TAG_CHILD_ALBUM_CLASS";
    public static final String FRAGMENT_TAG_CHILD_RADIO_CLASS = "FRAGMENT_TAG_CHILD_RADIO_CLASS";
    public static final String FRAGMENT_TAG_CHILD_MULTI_RADIO_CLASS = "FRAGMENT_TAG_CHILD_MULTI_RADIO_CLASS";

    private FrameLayout content;

    public static NetFMFragment newInstance() {
        return new NetFMFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (SUPPORT_ONLINE_FM) {
            return inflater.inflate(R.layout.fragment_net_container, container, false);
        } else {
            return inflater.inflate(R.layout.state_empty_view, container, false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        content = view.findViewById(R.id.content);
        if (SUPPORT_ONLINE_FM) {
            initView();
        }
    }

    private void initView() {
        content.setVisibility(View.VISIBLE);
        replaceFragment(IndexFragment.newInstance(), FRAGMENT_TAG_NET_FM_INDEX);
    }

    public void replaceFragment(BaseFragment fragment, String tag) {
        FragmentUtils.replace(getChildFragmentManager(), fragment, R.id.content, tag, false);
    }

    public void addFragment(BaseFragment fragment, String tag) {
        //使用add fragment 会检测不出 fragment 之间的层叠关系
        FragmentUtils.replace(getChildFragmentManager(), fragment, R.id.content, tag, true,
                R.anim.transitions_enter_from_bottom, 0, 0, R.anim.transitions_out_to_bottom);
    }

    @Override
    public String getThisNode() {
        return NodeConst.Xting.FGT_NET;
    }
}
