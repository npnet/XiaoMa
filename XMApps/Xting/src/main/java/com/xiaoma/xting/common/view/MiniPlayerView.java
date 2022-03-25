package com.xiaoma.xting.common.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.AutoScrollTextView;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerAction;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.contract.PlayerStatus;
import com.xiaoma.xting.common.playerSource.info.IPlayerInfo;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.loadmore.IFetchListener;
import com.xiaoma.xting.common.playerSource.utils.PrintInfo;
import com.xiaoma.xting.player.ui.FMPlayerActivity;
import com.xiaoma.xting.utils.Transformations;

/**
 * @author KY
 * @date 2018/10/12
 */
public class MiniPlayerView extends ConstraintLayout implements View.OnClickListener {

    public static final String TAG = MiniPlayerView.class.getSimpleName();

    private Context mContext;
    private ImageView ivCover;
    private ImageView ibControl;
    private AutoScrollTextView tvName;
    private Animation animation;
    private GuideClickCallBack guideClickCallBack;

    private @PlayerStatus
    int mPlayerStatus;
    private PlayerInfo mPlayerInfo;

    public MiniPlayerView(Context context) {
        this(context, null);
    }

    public MiniPlayerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private void bindView() {
        LayoutInflater.from(mContext).inflate(R.layout.view_mini_player, this, true);
        ivCover = findViewById(R.id.iv_cover);
        ibControl = findViewById(R.id.ib_control);
        tvName = findViewById(R.id.tv_name);
    }

    private void initView() {
        animation = AnimationUtils.loadAnimation(mContext, R.anim.rotate);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);
        ivCover.setOnClickListener(this);
        ibControl.setOnClickListener(this);
        tvName.setOnClickListener(this);
    }

    public MiniPlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        bindView();
        initView();
        init();
    }


    /**
     * 设置媒体信息
     *
     * @param cover cover url
     * @param name  name
     */
    public void setMediaInfo(final String cover, final String name) {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                try {
                    ImageLoader.with(mContext).load(cover)
                            .placeholder(R.drawable.fm_default_cover)
                            .transform(Transformations.getRoundedCorners())
                            .into(ivCover);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tvName.setText(name);
            }
        });
    }

    private IPlayerInfo mPlayerInfoListener = new IPlayerInfo() {
        @Override
        public void onPlayerInfoChanged(PlayerInfo playerInfo) {
            PrintInfo.print(TAG, "onPlayerInfoChanged", String.valueOf(playerInfo));
            mPlayerInfo = playerInfo;
            if (playerInfo == null) {
                ibControl.setVisibility(GONE);
                tvName.stopMarquee();
                setMediaInfo("", mContext.getString(R.string.mini_player_welcome));
            } else {
                String coverUrl = "";
                String showName = playerInfo.getProgramName();
                if (!playerInfo.isPreShowF()) {
                    coverUrl = playerInfo.getCoverUrl();
                }
                if (TextUtils.isEmpty(showName) || playerInfo.isFromRecordF()) {
                    //初次进入的时候，如果是 FM/AM电台，可能会走进这里面，进而导致判断错误，显示为空的情况
                    String albumName = playerInfo.getAlbumName();
                    if (!TextUtils.isEmpty(albumName)) {
                        showName = albumName;
                    }
                } else {
                    //产品需求 ： 要求本地电台节目显示，有具体台的显示具体，没有则显示频率
                    if (playerInfo.getSourceType() == PlayerSourceType.RADIO_YQ) {
                        String albumName = playerInfo.getAlbumName();
                        if (!TextUtils.isEmpty(albumName)) {
                            showName = albumName;
                        }
                    }
                }
                //以防止出现都是为空的情况，这里做这个处理
                if (TextUtils.isEmpty(showName)) {
                    showName = "UnKnown";
                }
                setMediaInfo(coverUrl, showName);
                if (playerInfo.getType() == PlayerSourceType.RADIO_YQ) {
                    onPlayerStatusChanged(PlayerStatus.NONE);
                } else {
                    ibControl.setVisibility(VISIBLE);
                    onPlayerStatusChanged(PlayerInfoImpl.newSingleton().getPlayStatus());
                }
            }
        }

        @Override
        public void onPlayerStatusChanged(int status) {
            PrintInfo.print(TAG, "onPlayerStatusChanged", String.format("state = %1$s , playerInfo = %2$s", status, mPlayerInfo));
            if (mPlayerInfo != null && mPlayerInfo.getType() == PlayerSourceType.RADIO_YQ) {
                animation.cancel();
                animation.reset();

                ibControl.setEnabled(false);
                ibControl.setImageLevel(status);
                ibControl.setVisibility(INVISIBLE);
            } else {
                mPlayerStatus = status;
                ibControl.setImageLevel(status);
                ibControl.setEnabled(status != PlayerStatus.LOADING && status != PlayerStatus.NONE);
                ivCover.setEnabled(status != PlayerStatus.LOADING);
                if (status == PlayerStatus.LOADING) {
                    animation.reset();
                    ibControl.startAnimation(animation);
                    tvName.stopMarquee();
                } else {
                    animation.cancel();
                    animation.reset();
                    if (status == PlayerStatus.PLAYING) {
                        tvName.startMarquee();
                    } else {
                        tvName.stopMarquee();
//                        if (status == PlayerStatus.ERROR_BY_DATA_SOURCE) {
//                            ibControl.setImageLevel(PlayerStatus.ERROR);
//                        }
                    }
                }
            }
        }

        @Override
        public void onPlayerProgress(long progress, long duration) {

        }

        @Override
        public void onProgramSubscribeChanged(boolean subscribe) {

        }
    };

    private void init() {
        mPlayerStatus = PlayerStatus.DEFAULT;
        PlayerInfoImpl.newSingleton().registerPlayerInfoListener(mPlayerInfoListener);
    }

    @Override
    public void onClick(View v) {
        int sourceType = PlayerSourceFacade.newSingleton().getSourceType();
        if (sourceType == PlayerSourceType.DEFAULT || mPlayerInfo == null) {
            XMToast.showToast(mContext, R.string.no_content_to_play);
        } else {
            PlayerInfo playerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
            if (playerInfo == null) {
                if (mPlayerInfo == null) {
                    XMToast.showToast(mContext, R.string.error_by_data_source);
                    return;
                }
            }
            int checkType = mPlayerInfo.getType();
            if (checkType != sourceType) {
                PlayerSourceFacade.newSingleton().setSourceType(checkType);
            }
            if (v.getId() == R.id.tv_name) {
                XmAutoTracker.getInstance().onEvent(EventConstants.PageDescribe.TAG_PLAY_AREA, EventConstants.NormalClick.ACTION_TO_PLAY_DETAILS, MiniPlayerView.this.getClass().getName(), EventConstants.PageDescribe.VIEW_MINI_PLAYER);
                FMPlayerActivity.launch(getContext());
//                if (PlayerSourceFacade.newSingleton().getPlayerControl().isCurPlayerInfoAlive()) {
//                    if (PlayerInfoImpl.newSingleton().getPlayerInfo() == null) {
//                        PlayerInfoImpl.newSingleton().onPlayerInfoChanged(PlayerSourceFacade.newSingleton().getPlayerControl().getCurPlayerInfo());
//                    }
//                    FMPlayerActivity.launch(getContext());
//                } else {
//                    XMToast.toastLoading(mContext, R.string.loading_data, true);
//                    if (PlayerSourceFacade.newSingleton().getSourceType() == PlayerSourceType.RADIO_YQ) {
//                        PlayerSourceFacade.newSingleton().getPlayerControl().play();
//                    } else {
//                        if (!PlayerSourceFacade.newSingleton().getPlayerFetch().isThisAlbumFetching(mPlayerInfo)) {
//                            mPlayerInfo.setAction(PlayerAction.PLAY_LIST);
//                            PlayerSourceFacade.newSingleton().getPlayerFetch().fetch(mPlayerInfo, getFetchListener());
//                        }
//                    }
//
//                }
                if (guideClickCallBack != null) {
                    guideClickCallBack.onClick();
                }
            } else {
                XmAutoTracker.getInstance().onEvent(EventConstants.PageDescribe.TAG_PLAY_AREA, EventConstants.NormalClick.ACTION_PLAYER_CONTROL_PLAY_OR_PAUSE, MiniPlayerView.this.getClass().getName(), EventConstants.PageDescribe.VIEW_MINI_PLAYER);
                handlePlayOrPause();
            }
        }
    }

    private void handlePlayOrPause() {
        if (mPlayerStatus == PlayerStatus.PLAYING) {
            PlayerSourceFacade.newSingleton().getPlayerControl().pause();
        } else {
            if (PlayerSourceFacade.newSingleton().getPlayerControl().isCurPlayerInfoAlive()) {
                PlayerSourceFacade.newSingleton().getPlayerControl().play();
            } else {
                mPlayerInfo.setAction(PlayerAction.PLAY_LIST);
                PlayerSourceFacade.newSingleton().getPlayerFetch().fetch(mPlayerInfo, getFetchListener());
            }
        }
    }

    private IFetchListener getFetchListener() {
        return new IFetchListener() {
            @Override
            public void onLoading() {
                PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.LOADING);
            }

            @Override
            public void onSuccess(Object t) {
                XMToast.cancelToast();
            }

            @Override
            public void onFail() {

            }

            @Override
            public void onError(int code, String msg) {
            }
        };
    }

    public interface GuideClickCallBack {
        void onClick();
    }

    public void setGuideClickCallBack(GuideClickCallBack guideClickCallBack) {
        this.guideClickCallBack = guideClickCallBack;
    }

}
