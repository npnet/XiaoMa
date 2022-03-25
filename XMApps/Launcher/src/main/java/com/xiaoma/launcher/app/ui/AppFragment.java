package com.xiaoma.launcher.app.ui;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EdgeEffect;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.app.AppVM;
import com.xiaoma.launcher.app.adapter.LauncherAppAdapter;
import com.xiaoma.launcher.app.model.LauncherApp;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.login.business.ui.ChooseUserActivity;
import com.xiaoma.model.User;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author taojin
 * @date 2019/1/2
 */
@SuppressLint("LogNotTimber")
@PageDescComponent(EventConstants.PageDescribe.AppFragmentPagePathDesc)
public class AppFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = "AppFragment";
    private final int DIVIDER_MARGIN = 65;
    private final int EDGEEFFECT_WIDTH = 100;
    private RecyclerView rvMain;
    private TextView tvUserName;
    private XmScrollBar mXmScrollBar;
    private ImageView mIvUserHead;
    private LauncherAppAdapter mLauncherAppAdapter;
    private final List<LauncherApp> mLauncherAppList = new ArrayList<>();
    private AppVM mAppVM;

    public static AppFragment newInstance() {
        return new AppFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateWrapView(inflater.inflate(R.layout.fragment_app, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindView(view);
        initRv();
        initVM();
    }

    private void initVM() {
        mAppVM = ViewModelProviders.of(this).get(AppVM.class);
        mAppVM.getAppList().observe(this, new Observer<List<LauncherApp>>() {
            @Override
            public void onChanged(@Nullable List<LauncherApp> launcherApps) {
                if (mLauncherAppAdapter != null && !ListUtils.isEmpty(launcherApps)) {
                    mLauncherAppList.clear();
                    mLauncherAppList.addAll(launcherApps);
                    rvMain.setAdapter(mLauncherAppAdapter);
                    mXmScrollBar.setRecyclerView(rvMain);
                }
            }
        });
        mAppVM.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user == null) {
                    return;
                }
                tvUserName.setText(LauncherConstants.tourist_uid.equals(String.valueOf(user.getId())) ?
                        mContext.getString(R.string.traveller) : user.getName());
                if (getContext() != null) {
                    final Context applicationContext = getContext().getApplicationContext();
                    ImageLoader.with(AppFragment.this)
                            .load(user.getPicPath())
                            .placeholder(applicationContext.getDrawable(R.drawable.icon_default_user))
                            .error(applicationContext.getDrawable(R.drawable.icon_default_user))
                            .circleCrop()
                            .into(mIvUserHead);
                }
            }
        });
        mAppVM.fetchAppList();
        UserManager.getInstance().addOnUserUpdateListener(mUserUpdateListener);
    }

    private UserManager.OnUserUpdateListener mUserUpdateListener = new UserManager.OnUserUpdateListener() {
        @Override
        public void onUpdate(@Nullable User user) {
            if (mAppVM != null && user != null) {
                mAppVM.setUser(user);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (LoginManager.getInstance().isUserLogin()) {
            User user = UserManager.getInstance().getCurrentUser();
            if (mAppVM != null && user != null) {
                mAppVM.setUser(user);
            }
        }
    }

    private void bindView(View view) {
        tvUserName = view.findViewById(R.id.tv_user_name);
        mIvUserHead = view.findViewById(R.id.iv_user_head);
        rvMain = view.findViewById(R.id.rv_main_app);
        rvMain.setEdgeEffectFactory(new RecyclerView.EdgeEffectFactory() {
            @NonNull
            @Override
            protected EdgeEffect createEdgeEffect(RecyclerView view, int direction) {
                EdgeEffect eff = super.createEdgeEffect(view, direction);
                eff.setColor(Color.GRAY);
                eff.setSize(EDGEEFFECT_WIDTH, EDGEEFFECT_WIDTH);
                return eff;
            }
        });
        rvMain.setHasFixedSize(true);
        rvMain.setItemAnimator(null);
        mXmScrollBar = view.findViewById(R.id.scroll_bar);
        tvUserName.setOnClickListener(this);
        mIvUserHead.setOnClickListener(this);
    }

    private void initRv() {
        rvMain.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        RecyclerView.ItemDecoration itemDecoration = new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int pos = parent.getChildLayoutPosition(view);
                if (pos == 0) {
                    outRect.set(0, 0, DIVIDER_MARGIN, 0);
                } else if (pos == parent.getAdapter().getItemCount() - 1) {
                    outRect.set(DIVIDER_MARGIN, 0, 0, 0);
                } else {
                    outRect.set(DIVIDER_MARGIN, 0, DIVIDER_MARGIN, 0);
                }
            }
        };
        rvMain.addItemDecoration(itemDecoration);
        mLauncherAppAdapter = new LauncherAppAdapter(R.layout.item_app_fragment,
                mLauncherAppList, ImageLoader.with(this));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        UserManager.getInstance().removeOnUserUpdateListener(mUserUpdateListener);
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.APP_USER_HEAD, EventConstants.NormalClick.APP_USER_HEAD})
    //按钮对应的名称
    @ResId({R.id.iv_user_head, R.id.tv_user_name})//按钮对应的R文件id
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_user_head:
            case R.id.tv_user_name:
                ChooseUserActivity.start(getContext());
                break;
            default:
                break;
        }
    }


}
