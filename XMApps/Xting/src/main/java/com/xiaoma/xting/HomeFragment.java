package com.xiaoma.xting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.vr.dispatch.annotation.Command;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.VisibilityFragment;
import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.contract.PlayerStatus;
import com.xiaoma.xting.common.playerSource.control.IPlayerControl;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.info.model.RecordInfo;
import com.xiaoma.xting.common.playerSource.info.sharedPref.SharedPrefUtils;
import com.xiaoma.xting.common.view.MiniPlayerView;
import com.xiaoma.xting.common.view.TabMenuGroup;
import com.xiaoma.xting.launcher.XtingAudioClient;
import com.xiaoma.xting.local.ui.LocalFMFragment;
import com.xiaoma.xting.mine.ui.MineFragment;
import com.xiaoma.xting.online.ui.NetFMFragment;
import com.xiaoma.xting.search.ui.SearchActivity;

import java.util.List;

/**
 * @author KY
 * @date 2018/10/9
 */
@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_HOME)
public class HomeFragment extends VisibilityFragment implements View.OnClickListener, TabMenuGroup.OnCheckedChangeListener {

    private static final String TAG = HomeFragment.class.getSimpleName();

    public static final String FRAGMENT_TAG_NET_FM = "Fragment_TAG_NET_FM";
    public static final String FRAGMENT_TAG_LOCAL_FM = "Fragment_TAG_LOCAL_FM";
    public static final String FRAGMENT_TAG_MY = "Fragment_TAG_MY";
    /**
     * 搜索
     */
    private ImageView mTvSearch;
    private TabMenuGroup rgMenu;
    private MiniPlayerView miniPlayer;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindView(view);
        initView();
        initData();
    }

    private void bindView(View view) {
        mTvSearch = view.findViewById(R.id.tv_search);
        rgMenu = view.findViewById(R.id.rg_menu);
        miniPlayer = view.findViewById(R.id.mini_player);
    }

    private void initView() {
        mTvSearch.setOnClickListener(this);
        rgMenu.setOnCheckedChangeListener(this);
        replaceContent(FRAGMENT_TAG_NET_FM);
    }

    private void initData() {
        PlayerInfo cachedPlayerInfo = SharedPrefUtils.getCachedPlayerInfo(getContext());
        if (cachedPlayerInfo != null) {
            int type = cachedPlayerInfo.getType();
            PlayerSourceFacade.newSingleton().setSourceType(type);
            XtingAudioClient.newSingleton(getContext()).setLauncherCategoryId(cachedPlayerInfo.getCategoryId());
            if (type == PlayerSourceType.RADIO_YQ) {
                PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.NONE);
            } else {
                RecordInfo recordInfo = XtingUtils.getRecordDao().selectBy(cachedPlayerInfo.getType(), cachedPlayerInfo.getProgramId());
                if (recordInfo != null) {
                    cachedPlayerInfo.setProgress(recordInfo.getProgress());
                    cachedPlayerInfo.setDuration(recordInfo.getDuration());
                }

                IPlayerControl playerControl = PlayerSourceFacade.newSingleton().getPlayerControl();
                if (playerControl == null) {
                    if (type != PlayerSourceType.DEFAULT) {
                        PlayerSourceFacade.newSingleton().setSourceType(type);
                        playerControl = PlayerSourceFacade.newSingleton().getPlayerControl(type);
                    }
                }
                if (playerControl != null) {
                    if (playerControl.isPlaying()) {
                        PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.PLAYING);
                    } else {
                        PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.PAUSE);
                    }
                } else {
                    XtingAudioClient.newSingleton(getContext()).restoreLauncherCategoryId();
                    PlayerInfoImpl.newSingleton().onPlayerInfoChanged(null);
                    return;
                }
            }
            cachedPlayerInfo.setFromRecordF(true);
            PlayerInfoImpl.newSingleton().onPlayerInfoChanged(cachedPlayerInfo);
//            AudioFocusHelper.newSingleton().requestAudioFocus(cachedPlayerInfo.getType());
        } else {
//            AudioFocusHelper.newSingleton().requestAudioFocus(PlayerSourceType.DEFAULT);
            XtingAudioClient.newSingleton(getContext()).restoreLauncherCategoryId();
            PlayerInfoImpl.newSingleton().onPlayerInfoChanged(null);
        }
    }

    public void replaceContent(String tag) {
        List<Fragment> fragments = FragmentUtils.getFragments(getChildFragmentManager());
        Fragment fragmentByTag = FragmentUtils.findFragment(getChildFragmentManager(), tag);
        if (fragmentByTag != null) {
            fragments.remove(fragmentByTag);
            FragmentUtils.showHide(fragmentByTag, fragments);
        } else {
            for (Fragment fg : fragments) {
                FragmentUtils.hide(fg);
            }
            Fragment dstFragment = getFragmentByTag(tag);
            FragmentUtils.add(getChildFragmentManager(), dstFragment, R.id.fragment, tag);
        }
    }

    private Fragment getFragmentByTag(String tag) {
        switch (tag) {
            default:
            case FRAGMENT_TAG_NET_FM:
                return NetFMFragment.newInstance();
            case FRAGMENT_TAG_LOCAL_FM:
                return LocalFMFragment.newInstance();
            case FRAGMENT_TAG_MY:
                return MineFragment.newInstance();
        }
    }

    @NormalOnClick({EventConstants.NormalClick.ACTION_MENU_SEARCH})
    @ResId({R.id.tv_search})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_search:
                SearchActivity.launcher(getContext());
                break;
        }
    }

    @Override
    public void onCheckedChanged(TabMenuGroup group, int checkedId) {
        switch (checkedId) {
            default:
            case R.id.rb_net_fm:
                manualUpdateTrack(EventConstants.NormalClick.ACTION_MENU_NET_RADIO);
                replaceContent(FRAGMENT_TAG_NET_FM);
                break;
            case R.id.rb_local_fm:
                manualUpdateTrack(EventConstants.NormalClick.ACTION_MENU_LOCAL_RADIO);
                replaceContent(FRAGMENT_TAG_LOCAL_FM);
                break;
            case R.id.rb_my:
                manualUpdateTrack(EventConstants.NormalClick.ACTION_MENU_MINE);
                replaceContent(FRAGMENT_TAG_MY);
                break;
        }
    }

    @Override
    public boolean handleJump(String nextNode) {
        super.handleJump(nextNode);
        switch (nextNode) {
            case NodeConst.Xting.FGT_MY:
                //这样替换避免了点击直接调用performClick出现的按钮点击声效
                rgMenu.check(R.id.rb_my);
                return true;
            case NodeConst.Xting.FGT_NET:
                rgMenu.check(R.id.rb_net_fm);
                return true;
            case NodeConst.Xting.FGT_LOCAL:
                rgMenu.check(R.id.rb_local_fm);
                return true;
            default:
                return false;
        }
    }

    @Override
    public String getThisNode() {
        return NodeConst.Xting.FGT_HOME;
    }

    @Command("测试测试")
    public void testtest() {
        XMToast.showToast(getContext(), "测试测试");
    }

    @Command("哈哈哈")
    public void testtest(String command) {
        XMToast.showToast(getContext(), "哈哈哈");
    }

    public MiniPlayerView getMiniPlayer() {
        return miniPlayer;
    }

    private void manualUpdateTrack(String action) {
        XmAutoTracker.getInstance().onEvent(action,
                HomeFragment.this.getClass().getName(),
                EventConstants.PageDescribe.FRAGMENT_HOME);
    }

}
