package com.xiaoma.music.mine.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.autotracker.listener.XmTrackerOnTabSelectedListener;
import com.xiaoma.component.base.VisibleFragment;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.common.adapter.ViewPagerAdapter;
import com.xiaoma.music.common.constant.EventBusTags;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.manager.MusicDbManager;
import com.xiaoma.music.common.model.LoginOutEvent;
import com.xiaoma.music.common.util.Transformations;
import com.xiaoma.music.export.manager.AudioShareManager;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.mine.callback.OnRefreshKWUserStatusCallback;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.dialog.XmDialog;
import com.xiaoma.ui.view.ControllableViewPager;
import com.xiaoma.utils.BackHandlerHelper;
import com.xiaoma.utils.ListUtils;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2018/10/10 0010
 */
@PageDescComponent(EventConstants.PageDescribe.mineFragment)
public class MineFragment extends VisibleFragment implements View.OnClickListener,
        MusicDbManager.OnHistoryChangedListener,
        BackHandlerHelper.FragmentBackHandler,
        OnRefreshKWUserStatusCallback {

    private TabLayout mTabLayout;
    private ControllableViewPager mViewPager;
    private ViewPagerAdapter mAdapter;

    private int[] mTitles;
    private ViewHolder mHolder;
    private TextView clearAllHistory;
    private TextView tvClearCollection;
    private ConstraintLayout kwUserLl;
    private ImageView vipTv;
    private ImageView kwHead;
    public boolean isHide = false;
    private String mString = "";
    private MusicDbManager.OnCollectionStatusChangeListener collectionStatusChangeListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        initEvent();
    }

    private void bindView(@NonNull View view) {
        mTabLayout = view.findViewById(R.id.fragment_mine_tab);
        mViewPager = view.findViewById(R.id.fragment_mine_vp);
        clearAllHistory = view.findViewById(R.id.tv_clear_all_history);
        tvClearCollection = view.findViewById(R.id.tv_clear_collection);
        kwUserLl = view.findViewById(R.id.kw_user_ll);
        vipTv = view.findViewById(R.id.kw_user_vip_tv);
        kwHead = view.findViewById(R.id.kw_user_iv);
        view.findViewById(R.id.kw_user_btn).setOnClickListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        showKwUserHead(mViewPager.getCurrentItem() == mTitles.length - 1);
        OnlineMusicFactory.getKWLogin().fetchVipInfo();
    }

    //暂时取消已购音乐
    private void initEvent() {
        mTitles = new int[]{R.string.mine_collector,
                R.string.mine_history,
//                R.string.purchased_music,
                R.string.vip_center};
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(CollectionFragment.newInstance(mContext.getResources().getDimensionPixelOffset(R.dimen.size_item_first_margin),
                CollectionFragment.KEY_ENTRY_MAIN));
        fragments.add(HistoryFragment.newInstance());
//        fragments.add(PurchasedMusicFragment.newInstance());
        fragments.add(VipCenterFragment.newInstance());

        mAdapter = new ViewPagerAdapter(getChildFragmentManager(), fragments);
        mViewPager.setScrollable(true);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        setupTabView(mTabLayout);
        clearAllHistory.setVisibility(View.INVISIBLE);
        clearAllHistory.setOnClickListener(this);
        tvClearCollection.setVisibility(View.GONE);
        tvClearCollection.setOnClickListener(this);
        MusicDbManager.getInstance().addHistoryChangedListener(this);
        mTabLayout.addOnTabSelectedListener(new XmTrackerOnTabSelectedListener() {

            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(mString, "0");
            }

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                onTabChange(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                changeTab(tab, false);
            }
        });
        MusicDbManager.getInstance().addCollectionStatusChangeListener(collectionStatusChangeListener = new MusicDbManager.OnCollectionStatusChangeListener() {
            @Override
            public void onAddCollection() {
                doClearCollectVisibility();
            }

            @Override
            public void onRemoveCollection() {
                doClearCollectVisibility();
                AudioShareManager.getInstance().shareOnlineAudioFavorite(false);
            }
        });
    }

    private void onTabChange(TabLayout.Tab tab) {
        mString = changeTab(tab, true);
        int position = tab.getPosition();
        mViewPager.setCurrentItem(position, false);
        switch (position) {
            case 0:
                showClearHistory(false);
                showKwUserHead(false);
                break;
            case 1:
                showClearHistory(true);
                showKwUserHead(false);
                break;
            case 2:
            case 3:
                showClearHistory(false);
                showKwUserHead(true);
                break;
            default:
                showClearHistory(false);
                showKwUserHead(false);
                break;
        }
    }

    private void doClearCollectVisibility() {
        if (View.VISIBLE != kwUserLl.getVisibility()
                && View.VISIBLE != clearAllHistory.getVisibility()
                && mViewPager.getCurrentItem() == 0
                && MusicDbManager.getInstance().queryCollectionMusic().size() > 0) {
            tvClearCollection.setVisibility(View.VISIBLE);
        } else {
            tvClearCollection.setVisibility(View.GONE);
        }
    }

    private void showKwUserHead(boolean showLoginLayout) {
        if (OnlineMusicFactory.getKWLogin().isUserLogon()) {
            if (showLoginLayout) {
                kwUserLl.setVisibility(View.VISIBLE);
            } else {
                kwUserLl.setVisibility(View.INVISIBLE);
            }
            if (OnlineMusicFactory.getKWLogin().isCarVipUser()) {
                vipTv.setVisibility(View.VISIBLE);
            } else {
                vipTv.setVisibility(View.INVISIBLE);
            }
            Glide.with(this)
                    .load(OnlineMusicFactory.getKWLogin().getUserInfo().getHeadPic())
                    .placeholder(R.drawable.avator_icon_bg)
                    .transform(Transformations.getCircleCrop())
                    .into(kwHead);
        } else {
            kwUserLl.setVisibility(View.INVISIBLE);
        }
        doClearCollectVisibility();
    }

    private void showClearHistory(boolean b) {
        if (b) {
            List<XMMusic> xmMusicList = MusicDbManager.getInstance().queryHistoryMusic();
            if (ListUtils.isEmpty(xmMusicList)) {
                clearAllHistory.setVisibility(View.INVISIBLE);
            } else {
                clearAllHistory.setVisibility(View.VISIBLE);
            }
        } else {
            clearAllHistory.setVisibility(View.INVISIBLE);
        }
        doClearCollectVisibility();
    }

    private void setupTabView(TabLayout tabLayout) {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(R.layout.tab_layout_item);
                View view = tab.getCustomView();
                if (view != null) {
                    mHolder = new ViewHolder(view);
                    mHolder.tabTv.setText(mTitles[i]);
                }
            }
            if (i == 0) {
                mHolder.tabTv.setSelected(true);
                mHolder.tabTv.setTextAppearance(R.style.text_view_light_blue);
            }
        }
    }

    private String changeTab(TabLayout.Tab tab, boolean isSelected) {
        View customView = tab.getCustomView();
        if (customView == null) {
            return "";
        }
        mHolder = new ViewHolder(customView);
        mHolder.tabTv.setSelected(isSelected);
        if (isSelected) {
            mHolder.tabTv.setTextAppearance(mContext, R.style.text_view_light_blue);
        } else {
            mHolder.tabTv.setTextAppearance(mContext, R.style.text_view_normal);
        }
        return mHolder.tabTv.getText().toString();
    }

    @NormalOnClick({EventConstants.NormalClick.isDeleteHistory, EventConstants.NormalClick.kwLoginout})
    @ResId({R.id.tv_clear_all_history, R.id.kw_user_btn})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_clear_all_history:
                clearHistoryDialog();
                break;
            case R.id.tv_clear_collection:
                clearCollection();
                break;
            case R.id.kw_user_btn:
                showLoginoutDialog();
                break;
        }
    }

    private void clearCollection() {
        View view = View.inflate(mContext, R.layout.dialog_clear_all_view, null);

        TextView tvTips = view.findViewById(R.id.tv_tips);
        tvTips.setText(R.string.confirm_clear_collection);

        Button sureBtn = view.findViewById(R.id.btn_sure);
        Button cancelBtn = view.findViewById(R.id.btn_cancel);
        final XmDialog builder = new XmDialog.Builder(getFragmentManager())
                .setView(view)
                .setWidth(567)
                .setHeight(240)
                .create();
        sureBtn.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(((TextView) view).getText().toString(), "0");
            }

            @BusinessOnClick
            @Override
            public void onClick(View v) {
                MusicDbManager.getInstance().clearCollectionMusic();
                builder.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(((TextView) view).getText().toString(), "0");
            }

            @BusinessOnClick
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder.show();
    }

    private void showLoginoutDialog() {
        ConfirmDialog dialog = new ConfirmDialog(getActivity());
        dialog.setContent(getString(R.string.confirm_login_out))
                .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }).setPositiveButton(getString(R.string.sure), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                OnlineMusicFactory.getKWLogin().logout();
                showKwUserHead(false);
                EventBus.getDefault().post(new LoginOutEvent(), EventBusTags.LOGIN_OUT_TAG);
            }
        }).show();
    }

    private void clearHistoryDialog() {
        View view = View.inflate(mContext, R.layout.dialog_clear_all_view, null);
        Button sureBtn = view.findViewById(R.id.btn_sure);
        Button cancelBtn = view.findViewById(R.id.btn_cancel);
        final XmDialog builder = new XmDialog.Builder(getFragmentManager())
                .setView(view)
                .setWidth(567)
                .setHeight(240)
                .create();
        sureBtn.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(((TextView) view).getText().toString(), "0");
            }

            @BusinessOnClick
            @Override
            public void onClick(View v) {
                listener.clearAllHistory();
                builder.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(((TextView) view).getText().toString(), "0");
            }

            @BusinessOnClick
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public boolean handleJump(String nextNode) {
        super.handleJump(nextNode);
        switch (nextNode) {
            case NodeConst.MUSIC.OPEN_COLLECTION_LIST:
                TabLayout.Tab tabAt = mTabLayout.getTabAt(0);
                onTabChange(tabAt);
                return true;
            case NodeConst.MUSIC.OPEN_VIP_CENTER:
                TabLayout.Tab mTabLayoutTabAt = mTabLayout.getTabAt(2);
                onTabChange(mTabLayoutTabAt);
                return true;
            default:
                return false;
        }
    }

    @Override
    public String getThisNode() {
        return NodeConst.MUSIC.MINE_FRAGMENT;
    }


    @Override
    public void onClearAllHistorySuccess() {
        clearAllHistory.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAddHistory() {
        int visibility = clearAllHistory.getVisibility();
        final int currentItem = mViewPager.getCurrentItem();
        if (visibility != View.VISIBLE && currentItem == mTitles.length - 2) {
            clearAllHistory.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onBackPressed() {
        return BackHandlerHelper.handleBackPress(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isHide = hidden;
    }

    @Override
    public void onRefreshKWUserStatus() {
        showKwUserHead(true);
    }

    class ViewHolder {
        TextView tabTv;

        ViewHolder(View tabView) {
            tabTv = tabView.findViewById(R.id.view_tab_tv);
        }
    }

    private OnMusicHistoryChangeListener listener;

    public void setMusicHistoryChangeListener(OnMusicHistoryChangeListener listener) {
        this.listener = listener;
    }

    public interface OnMusicHistoryChangeListener {
        void clearAllHistory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MusicDbManager.getInstance().removeHistoryChangedListener(this);
        MusicDbManager.getInstance().removeCollectionStatusChangeListener(collectionStatusChangeListener);
    }
}
