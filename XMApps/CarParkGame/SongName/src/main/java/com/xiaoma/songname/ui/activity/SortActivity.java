package com.xiaoma.songname.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.songname.R;
import com.xiaoma.songname.adapter.SongNameSortAdapter;
import com.xiaoma.songname.common.constant.SongNameConstants;
import com.xiaoma.songname.common.manager.RequestManager;
import com.xiaoma.songname.common.utils.TextUtil;
import com.xiaoma.songname.model.SortListBean;
import com.xiaoma.songname.model.UserSignBean;
import com.xiaoma.ui.adapter.XMBaseAbstractRyAdapter;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 排行页面
 */
public class SortActivity extends BaseActivity implements View.OnClickListener {
    private ImageView mIvBack;
    private RecyclerView mSortRv;
    private XmScrollBar mScrollBar;
    private List<SortListBean.RankUserInfo> mRankUserList;
    private SongNameSortAdapter sortAdapter;
    private String uid;
    private PopupWindow mRankFriendPop;
    private View mContainer;

    //pop 控件
    private ImageView ivHead;
    private ImageView ivSex;
    private TextView tvName;
    private TextView tvAge;
    private TextView tvSign;
    private Button btnAdd;
    private Button btnChat;
    private UserSignBean userSignBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);
        initView();
        initData();
    }

    private void initView() {
        mContainer = findViewById(R.id.container);
        mIvBack = findViewById(R.id.sn_back);
        mSortRv = findViewById(R.id.sort_rv);
        mScrollBar = findViewById(R.id.sn_scroll_bar);
        mIvBack.setOnClickListener(this);
        mSortRv.setLayoutManager(new GridLayoutManager(this, 3, LinearLayoutManager.HORIZONTAL, false));
    }

    private void initData() {
        uid = getIntent().getStringExtra(SongNameConstants.UID);
//        uid = "1125591760727453696";
        mRankUserList = new ArrayList<>();
        sortAdapter = new SongNameSortAdapter(this, mRankUserList, R.layout.item_song_sort);
        sortAdapter.setOnItemClickListener(new XMBaseAbstractRyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.Adapter adapter, View view, RecyclerView.ViewHolder holder, int position) {
                //第一项为用户自身数据,不需要显示弹框
                if (position == 0) {
                    return;
                }
                //弹出好友信息
                SortListBean.RankUserInfo rankUserInfo = sortAdapter.getDatas().get(position);
                showFriendPop(rankUserInfo);
            }
        });
        mSortRv.setAdapter(sortAdapter);
        mScrollBar.setRecyclerView(mSortRv);
        getHttpSortList();
    }

    public void getHttpSortList() {
        RequestManager.getInstance().getRankingList(uid, new ResultCallback<XMResult<SortListBean>>() {
            @Override
            public void onSuccess(XMResult<SortListBean> result) {
                List<SortListBean.RankUserInfo> infoList = result.getData().getRankingList();
                //数据为空处理
                if (ListUtils.isEmpty(infoList)) {
                    showEmptyView();
                    return;
                }
                mRankUserList.addAll(infoList);
                sortAdapter.setDatas(mRankUserList);
            }

            @Override
            public void onFailure(int code, String msg) {
                showNoNetView();
            }
        });
    }

    private void showFriendPop(final SortListBean.RankUserInfo rankUserInfo) {
        if (mRankFriendPop == null) {
            initRankUserPop(rankUserInfo);
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestManager.getInstance().requestAddFriend(uid, String.valueOf(rankUserInfo.getUid()),
                        new ResultCallback<XMResult<String>>() {
                            @Override
                            public void onSuccess(XMResult<String> result) {
                                showToast(getString(R.string.add_friend_ok));
                            }

                            @Override
                            public void onFailure(int code, String msg) {
                                if (TextUtil.hasChinese(msg)) {
                                    showToast(msg);

                                } else {
                                    showToast(getString(R.string.add_friend_fail, ""));
                                }
                            }
                        });
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userSignBean != null && !StringUtil.isEmpty(userSignBean.getHxAccount())) {
                    chatFriend(userSignBean.getHxAccount());

                } else {
                    showToastException("hx_id is null");
                }
            }
        });

        RequestManager.getInstance().getUserSign(uid, String.valueOf(rankUserInfo.getUid()),
                new ResultCallback<XMResult<UserSignBean>>() {
                    @Override
                    public void onSuccess(XMResult<UserSignBean> result) {
                        userSignBean = result.getData();
                        if (StringUtil.isEmpty(userSignBean.getPersonalSignature())) {
                            tvSign.setText(getString(R.string.no_data));

                        } else {
                            tvSign.setText(userSignBean.getPersonalSignature());
                        }
                        btnAdd.setVisibility(userSignBean.isIsFriend() ? View.GONE : View.VISIBLE);
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        showToastException(getString(R.string.get_user_sign_error, msg));
                    }
                });
        ImageLoader.with(this).load(rankUserInfo.getUserPic())
                .placeholder(getDrawable(R.drawable.icon_default_head))
                .transform(new CircleCrop())
                .into(ivHead);
        ivSex.setImageResource(rankUserInfo.getUserSex() == 1 ? R.drawable.sn_man : R.drawable.sn_woman);
        tvName.setText(rankUserInfo.getUsername());
        tvAge.setText(getString(R.string.user_age, rankUserInfo.getUserAge()));
        mRankFriendPop.showAtLocation(mContainer, Gravity.START, 0, 0);
    }

    private void initRankUserPop(final SortListBean.RankUserInfo rankUserInfo) {
        View view = getLayoutInflater().inflate(R.layout.view_rank_firend_pop, null);
        mRankFriendPop = new PopupWindow(view, 800, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mRankFriendPop.setOutsideTouchable(true);
        mRankFriendPop.setAnimationStyle(R.style.PopAnimTranslate);
        mRankFriendPop.setClippingEnabled(false);
        view.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRankFriendPop != null && mRankFriendPop.isShowing()) {
                    mRankFriendPop.dismiss();
                }
            }
        });
        ivHead = view.findViewById(R.id.iv_head);
        ivSex = view.findViewById(R.id.iv_sex);
        tvName = view.findViewById(R.id.tv_name);
        tvAge = view.findViewById(R.id.tv_age);
        tvSign = view.findViewById(R.id.tv_sign);
        btnAdd = view.findViewById(R.id.btn_add);
        btnChat = view.findViewById(R.id.btn_chat);
    }

    /**
     * 私聊
     *
     * @param userId
     */
    private void chatFriend(String userId) {
        Intent intent = new Intent("com.xiaoma.club.CHAT");
        intent.putExtra("chatId", userId);
        intent.putExtra("isGroup", false);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        getHttpSortList();
    }

    @Override
    protected boolean isAppNeedShowNaviBar() {
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sn_back) {
            finish();
        }
    }
}
