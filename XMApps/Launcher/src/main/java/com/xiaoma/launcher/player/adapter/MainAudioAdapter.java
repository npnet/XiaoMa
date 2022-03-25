package com.xiaoma.launcher.player.adapter;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.manager.LanguageUtils;
import com.xiaoma.launcher.player.model.LauncherAudioInfo;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.ui.view.ReflectionImageView;
import com.xiaoma.utils.log.KLog;

import java.util.List;

/**
 * 音频首页adapter
 * Created by zhushi.
 * Date: 2019/1/30
 */
public class MainAudioAdapter extends BaseMultiItemQuickAdapter<LauncherAudioInfo, BaseViewHolder> {
    //电台标题
    public static final int ITEM_TYPE_XTING_TITLE = 0;
    //音乐标题
    public static final int ITEM_TYPE_MUSIC_TITLE = 6;
    //我的收藏
    public static final int ITEM_TYPE_MY_FAVORITE = 10;

    //初始情况下为无响应
    private int usbConnectState = AudioConstants.ConnectStatus.USB_NOT_MOUNTED;
    //蓝牙连接状态
    private boolean bTConnect;

    public MyFavoriteClickListener myFavoriteClickListener;
    private RequestManager mRequestManager;

    public MainAudioAdapter(List<LauncherAudioInfo> data, RequestManager requestManager) {
        super(data);
        //音乐标题
        addItemType(ITEM_TYPE_MUSIC_TITLE, R.layout.item_type_title);
        //在线音乐
        addItemType(AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO, R.layout.item_type_recommend);
        //蓝牙音乐
        addItemType(AudioConstants.AudioTypes.MUSIC_LOCAL_BT, R.layout.item_type_local);
        //usb音乐
        addItemType(AudioConstants.AudioTypes.MUSIC_LOCAL_USB, R.layout.item_type_local);

        //电台
        addItemType(ITEM_TYPE_XTING_TITLE, R.layout.item_type_title);
        //网络电台
        addItemType(AudioConstants.AudioTypes.XTING_NET_FM, R.layout.item_type_recommend);
        //在线电台
        addItemType(AudioConstants.AudioTypes.XTING_LOCAL_FM, R.layout.item_type_recommend);
        //奔腾FM
        addItemType(AudioConstants.AudioTypes.XTING_KOALA_ALBUM, R.layout.item_type_recommend);

        //收藏
        addItemType(ITEM_TYPE_MY_FAVORITE, R.layout.item_type_favorite);
        mRequestManager = requestManager;
    }

    private static final String TAG = "MainAudioAdapter";

    @Override
    protected void convert(BaseViewHolder helper, LauncherAudioInfo item) {
        //title
        TextView tvTitle = helper.getView(R.id.audio_tv_title);
        if (LanguageUtils.isChinese(mContext)) {
            tvTitle.setText(item.getName());

        } else {
            tvTitle.setText(item.getNameEn());
        }
        if (ITEM_TYPE_MUSIC_TITLE == item.getAudioType()
                || ITEM_TYPE_MY_FAVORITE == item.getAudioType()
                || ITEM_TYPE_XTING_TITLE == item.getAudioType()) {
//            mRequestManager
//                    .load((Integer) item.getLogo())
//                    .placeholder(R.drawable.audio_defulut_item)
//                    .into((ImageView) helper.getView(R.id.audio_iv));
            ((ImageView) helper.getView(R.id.audio_iv)).setImageResource((Integer) item.getLogo());
        } else {
            //背景
            mRequestManager
                    .load(item.getLogo())
                    .placeholder(R.drawable.audio_defulut_item)
                    .into((ImageView) helper.getView(R.id.audio_iv));
        }
        switch (item.getItemType()) {
            case ITEM_TYPE_XTING_TITLE:
                View xtingTitle = helper.getView(R.id.item_view);
                RelativeLayout.LayoutParams xtingTitleLayoutParams = new RelativeLayout.LayoutParams(
                        mContext.getResources().getDimensionPixelSize(R.dimen.card_normal_width),
                        mContext.getResources().getDimensionPixelSize(R.dimen.card_normal_height));
                xtingTitleLayoutParams.leftMargin = 160;
                xtingTitleLayoutParams.topMargin = 60;
                xtingTitle.setLayoutParams(xtingTitleLayoutParams);
                helper.setText(R.id.audio_tv_sub_title, item.getSubTitle());
                break;

            case ITEM_TYPE_MUSIC_TITLE:
                View musicTitle = helper.getView(R.id.item_view);
                RelativeLayout.LayoutParams musicTitleLayoutParams = new RelativeLayout.LayoutParams(
                        mContext.getResources().getDimensionPixelSize(R.dimen.card_normal_width),
                        mContext.getResources().getDimensionPixelSize(R.dimen.card_normal_height));
                musicTitleLayoutParams.leftMargin = 43;
                musicTitleLayoutParams.topMargin = 60;
                musicTitle.setLayoutParams(musicTitleLayoutParams);
                helper.setText(R.id.audio_tv_sub_title, item.getSubTitle());
                break;

            case AudioConstants.AudioTypes.MUSIC_LOCAL_BT:
                setBtView(helper);

                setSelectedState(helper, item);
                setPlayAnimation(helper, item);
                break;

            case AudioConstants.AudioTypes.MUSIC_LOCAL_USB:
                setUsbView(helper);

                setSelectedState(helper, item);
                setPlayAnimation(helper, item);
                break;

            case AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO:
                View view = helper.getView(R.id.v_logo);
                view.setVisibility(View.VISIBLE);
                setSelectedState(helper, item);
                setPlayAnimation(helper, item);
                break;
            case AudioConstants.AudioTypes.XTING_NET_FM:
            case AudioConstants.AudioTypes.XTING_KOALA_ALBUM:
            case AudioConstants.AudioTypes.XTING_LOCAL_FM:
                setSelectedState(helper, item);
                setPlayAnimation(helper, item);
                break;

            case ITEM_TYPE_MY_FAVORITE:
                helper.setText(R.id.audio_tv_sub_title, item.getSubTitle());

                myFavoriteClick(helper);

                //音乐收藏背景
                ((ImageView) helper.getView(R.id.iv_music_favorite)).setImageResource(R.drawable.pic_collect_1_n);
                //电台收藏背景
                ((ImageView) helper.getView(R.id.iv_xting_favorite)).setImageResource(R.drawable.pic_collect_2_n);
                break;
        }
    }

    /**
     * 我的收藏点击
     *
     * @param helper
     */
    private void myFavoriteClick(BaseViewHolder helper) {
        //音乐收藏
        helper.getView(R.id.music_favorite).setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.AUDIOMAIN_MUSIC_FAVORITE})
            public void onClick(View v) {
                if (myFavoriteClickListener != null) {
                    myFavoriteClickListener.onMusicFavorite();
                }
            }
        });
        //电台收藏
        helper.getView(R.id.xting_favorite).setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.AUDIOMAIN_XTING_FAVORITE})
            public void onClick(View v) {
                if (myFavoriteClickListener != null) {
                    myFavoriteClickListener.onXtingFavorite();
                }
            }
        });
    }

    public void setMyFavoriteClickListener(MyFavoriteClickListener myFavoriteClickListener) {
        this.myFavoriteClickListener = myFavoriteClickListener;
    }

    /**
     * 我的收藏点击Listener
     */
    public interface MyFavoriteClickListener {
        void onMusicFavorite();

        void onXtingFavorite();
    }

    /**
     * 设置栏目选中状态
     *
     * @param helper
     * @param item
     */
    private void setSelectedState(BaseViewHolder helper, LauncherAudioInfo item) {
        View itemView = helper.getView(R.id.item_view);
        ReflectionImageView audioIv = helper.getView(R.id.audio_iv);
        TextView titleView = helper.getView(R.id.audio_tv_title);
        View logoView = helper.getView(R.id.v_logo);

        TextView localConnectTv = null;
        if (item.getItemType() == AudioConstants.AudioTypes.MUSIC_LOCAL_BT ||
                item.getItemType() == AudioConstants.AudioTypes.MUSIC_LOCAL_USB) {
            localConnectTv = helper.getView(R.id.tv_connect_state);
        }

        RelativeLayout.LayoutParams itemLayoutParams = new RelativeLayout.LayoutParams(
                mContext.getResources().getDimensionPixelSize(R.dimen.card_normal_width),
                mContext.getResources().getDimensionPixelSize(R.dimen.card_normal_height));

        RelativeLayout.LayoutParams ivLayoutParams = new RelativeLayout.LayoutParams(
                mContext.getResources().getDimensionPixelSize(R.dimen.card_normal_width),
                mContext.getResources().getDimensionPixelSize(R.dimen.card_normal_height));
        ivLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        RelativeLayout.LayoutParams titleLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        titleLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        RelativeLayout.LayoutParams connectLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        connectLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        RelativeLayout.LayoutParams logoLayoutParams = null;
        if ((logoView != null) && (logoView.getVisibility() == View.VISIBLE)) {
            logoLayoutParams = new RelativeLayout.LayoutParams(98, 24);
        }

        //设置选中状态
        if (item.isSelected()) {
            itemLayoutParams.width = mContext.getResources().getDimensionPixelSize(R.dimen.card_bg_large_width);
            itemLayoutParams.height = mContext.getResources().getDimensionPixelSize(R.dimen.card_bg_large_height);
            itemLayoutParams.setMargins(0, 0, 0, 0);
            itemView.setLayoutParams(itemLayoutParams);
//            itemView.setBackground(mContext.getDrawable(R.drawable.bg_card_s));
            itemView.setBackgroundResource(R.drawable.bg_card_s);

            ivLayoutParams.width = mContext.getResources().getDimensionPixelSize(R.dimen.card_iv_large_width);
            ivLayoutParams.height = mContext.getResources().getDimensionPixelSize(R.dimen.card_iv_large_height);
            audioIv.setLayoutParams(ivLayoutParams);

            titleLayoutParams.setMargins(30, 0, 30, 80);
            titleView.setLayoutParams(titleLayoutParams);

            connectLayoutParams.setMargins(0, 60, 30, 0);
            if (localConnectTv != null) {
                localConnectTv.setLayoutParams(connectLayoutParams);
            }

            //选中的栏目图片没有倒影
            audioIv.setReflectionHeight(0);
            mRequestManager.load(item.getLogo())
                    .placeholder(R.drawable.audio_defulut_item)
                    .into(audioIv);

            if (logoLayoutParams != null) {
                logoLayoutParams.setMargins(50, 50, 0, 0);
                logoView.setLayoutParams(logoLayoutParams);
            }

        } else {
            audioIv.setReflectionHeight(40);
            itemLayoutParams.width = mContext.getResources().getDimensionPixelSize(R.dimen.card_normal_width);
            itemLayoutParams.height = mContext.getResources().getDimensionPixelSize(R.dimen.card_normal_height);
            itemLayoutParams.setMargins(0, mContext.getResources().getDimensionPixelSize(R.dimen.card_normal_margin_top), 0, 0);
            itemView.setLayoutParams(itemLayoutParams);
            itemView.setBackground(mContext.getDrawable(R.color.transparent));

            ivLayoutParams.width = mContext.getResources().getDimensionPixelSize(R.dimen.card_normal_width);
            ivLayoutParams.height = mContext.getResources().getDimensionPixelSize(R.dimen.card_normal_height);
            audioIv.setLayoutParams(ivLayoutParams);

            connectLayoutParams.setMargins(0, 20, 0, 0);
            if (localConnectTv != null) {
                localConnectTv.setLayoutParams(connectLayoutParams);
            }

            titleLayoutParams.setMargins(0, 0, 0, 70);
            titleView.setLayoutParams(titleLayoutParams);

            if (logoLayoutParams != null) {
                logoLayoutParams.setMargins(10, 10, 0, 0);
                logoView.setLayoutParams(logoLayoutParams);
            }
        }
    }

    /**
     * 设置栏目播放动画
     *
     * @param helper
     * @param item
     */
    private void setPlayAnimation(BaseViewHolder helper, LauncherAudioInfo item) {
        //播放动画
//        AnimationDrawable playingAnimation = (AnimationDrawable) mContext.getResources().getDrawable(R.drawable.play_wave_anim);
        AnimationDrawable playingAnimation = (AnimationDrawable) XmSkinManager.getInstance().getDrawable(R.drawable.play_wave_anim);

        ImageView ivPlayState = helper.getView(R.id.playing_state);
        ivPlayState.clearAnimation();

        if (item.getPlayState() == LauncherConstants.PLAY_STATE) {
            if (item.isSelected()) {
                ivPlayState.setVisibility(View.VISIBLE);
                ivPlayState.setImageDrawable(playingAnimation);
                playingAnimation.start();
            } else {
                ivPlayState.setVisibility(View.GONE);
            }
        } else if (item.getPlayState() == LauncherConstants.PAUSE_STATE) {
            if (item.isSelected()) {
                ivPlayState.setVisibility(View.VISIBLE);
                ivPlayState.setImageDrawable(playingAnimation);
                playingAnimation.stop();
            } else {
                ivPlayState.setVisibility(View.GONE);
            }
        } else if (item.getPlayState() == LauncherConstants.LOADING_STATE) {
            if (item.isSelected()) {
                ivPlayState.setVisibility(View.VISIBLE);
                ivPlayState.setImageDrawable(playingAnimation);
                playingAnimation.stop();
            } else {
                ivPlayState.setVisibility(View.GONE);
            }
        } else {
            ivPlayState.setVisibility(View.GONE);
        }
    }

    /**
     * 设置蓝牙栏目已连接和未连接状态
     *
     * @param helper
     */
    private void setBtView(BaseViewHolder helper) {
        TextView tvState = helper.getView(R.id.tv_connect_state);
        if (bTConnect) {
            tvState.setText(mContext.getString(R.string.audio_local_music_connect));
            tvState.setSelected(true);
            Drawable connectDrawable = mContext.getDrawable(R.drawable.connect_n);
            connectDrawable.setBounds(0, 0, 12, 12);
            tvState.setCompoundDrawables(connectDrawable, null, null, null);

        } else {
            tvState.setText(mContext.getString(R.string.audio_local_music_disconnect));
            tvState.setSelected(false);
            Drawable connectDrawable = mContext.getDrawable(R.drawable.unconnect_n);
            connectDrawable.setBounds(0, 0, 12, 12);
            tvState.setCompoundDrawables(connectDrawable, null, null, null);
        }
    }

    /**
     * 设置USB栏目已连接和未连接状态
     *
     * @param helper
     */
    private void setUsbView(BaseViewHolder helper) {
        TextView tvState = helper.getView(R.id.tv_connect_state);
        if (usbConnectState == AudioConstants.ConnectStatus.USB_SCAN_FINISH) {
            tvState.setText(mContext.getString(R.string.audio_local_music_connect));
            tvState.setSelected(true);
            Drawable connectDrawable = mContext.getDrawable(R.drawable.connect_n);
            connectDrawable.setBounds(0, 0, 12, 12);
            tvState.setCompoundDrawables(connectDrawable, null, null, null);

        } else {
            tvState.setText(mContext.getString(R.string.audio_local_music_disconnect));
            tvState.setSelected(false);
            Drawable unConnectDrawable = mContext.getDrawable(R.drawable.unconnect_n);
            unConnectDrawable.setBounds(0, 0, 12, 12);
            tvState.setCompoundDrawables(unConnectDrawable, null, null, null);
        }
    }

    /**
     * 设置USB状态
     *
     * @param usbConnectState
     */
    public void setUsbConnectState(int usbConnectState) {
        this.usbConnectState = usbConnectState;
        KLog.d("XM_LOG_" + "setUsbConnectState: " + "6");
        notifyDataSetChanged();
    }

    /**
     * 设置蓝牙状态
     *
     * @param btConnect
     */
    public void setBTConnectState(boolean btConnect) {
        this.bTConnect = btConnect;
        KLog.d("XM_LOG_" + "setBTConnectState: " + "5");
        notifyDataSetChanged();
    }
}
