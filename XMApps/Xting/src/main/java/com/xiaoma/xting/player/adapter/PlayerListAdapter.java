package com.xiaoma.xting.player.adapter;

import android.animation.AnimatorListenerAdapter;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.utils.TimeUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.contract.PlayerStatus;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.sdk.OnlineFMPlayerFactory;
import com.xiaoma.xting.sdk.bean.XMPlayableModel;
import com.xiaoma.xting.sdk.bean.XMSchedule;
import com.xiaoma.xting.sdk.utils.HimalayanPlayerUtils;
import com.xiaoma.xting.welcome.view.PropertyAnimImageView;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/30
 */
public class PlayerListAdapter extends XMBaseAbstractBQAdapter<PlayerInfo, BaseViewHolder> {

    private int mPlayingPos = -1;
    private @PlayerStatus
    int mPlayStatus;
    private long mProgress;
    private int mRecordLivingIndex = 0;

    public PlayerListAdapter() {
        super(R.layout.item_online_player_list);
        Log.d(TAG, "{PlayerListAdapter}-[clinit] : ");
        mPlayStatus = PlayerStatus.PAUSE;
    }

    public void notifyPlayerIndex(int position) {
        Log.d(TAG, "{notifyPlayerIndex}-[index] : " + position + " / " + mPlayingPos + " / " + mRecordLivingIndex);
        if (position < 0 || getItemCount() <= 0) return;
        if (getItemCount() == 1) {
            if (mPlayingPos == position) return;
            mPlayingPos = position;
            notifyItemChanged(position);
            return;
        }
//因为 本地电台使用的播放列表其实是来自于喜马拉雅,所以 不能用adapter里面的数据
        PlayerInfo playerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
        if (playerInfo == null) {
            return;
        }
        if (playerInfo.getSourceSubType() == PlayerSourceSubType.RADIO) {
            List<? extends XMPlayableModel> listPlayer = OnlineFMPlayerFactory.getPlayer().getListInPlayer();
            int nowLivingIndex = HimalayanPlayerUtils.checkIndexWithTime((List<XMSchedule>) listPlayer);
            if (nowLivingIndex != mRecordLivingIndex) {
                mPlayingPos = position;
                mRecordLivingIndex = nowLivingIndex;
                notifyItemRangeChanged(0, getItemCount());
                return;
            }
        } else if (playerInfo.getType() == PlayerSourceType.RADIO_YQ) {
            if (mPlayingPos == position) return;
            if (mPlayingPos > 0) {
                int refreshFrom = Math.min(position, mPlayingPos);
                mPlayingPos = position;
                notifyItemRangeChanged(refreshFrom, getItemCount() - refreshFrom + 1);
            } else {
                mPlayingPos = position;
                notifyItemChanged(position);
            }
            return;
        }

        if (mPlayingPos == position) return;
        int tempPos = mPlayingPos;
        mPlayingPos = position;

        if (tempPos != -1) {
            notifyItemChanged(tempPos);
        }
        notifyItemChanged(position);

    }

    public void notifyPlayerProgress(long progress) {
        mProgress = progress;
        notifyItemChanged(mPlayingPos, TimeUtils.timeMsToMMSS(progress));
    }

    public void notifyPlayerStatus(@PlayerStatus int status) {
        Log.d(TAG, "{notifyPlayerStatus}-[status] : " + status + " / " + mPlayingPos + " > " + mRecordLivingIndex);
        mPlayStatus = status;
        notifyItemChanged(mPlayingPos, status);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position,
                                 @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            for (Object payload : payloads) {
                if (payload instanceof String) {
                    holder.setText(R.id.tvTime, String.format("%1$s/%2$s",
                            String.valueOf(payload),
                            TimeUtils.timeMsToMMSS(getItem(position).getDuration())));
                } else if (payload instanceof Integer) {
                    PropertyAnimImageView levelIV = holder.getView(R.id.ivStateLevel);
                    setPlayStatus(levelIV, (Integer) payload);
                    holder.itemView.setEnabled(isEnableClick());
                }
            }
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, PlayerInfo item) {
        int position = helper.getAdapterPosition();
        String programName = item.getProgramName();

        TextView tvTitle = helper.getView(R.id.tvTitle);

        tvTitle.setText(TextUtils.isEmpty(programName) ? item.getAlbumName() : programName);
        tvTitle.setSelected(position == mPlayingPos);
        int type = item.getType();
        if (type == PlayerSourceType.HIMALAYAN || type == PlayerSourceType.RADIO_XM) {
            handleHimalayan(helper, item, position);
        } else if (type == PlayerSourceType.RADIO_YQ) {
            handleYQRadio(helper, item, position);
        } else if (type == PlayerSourceType.KOALA) {
            handleKoala(helper, item, position);
        }

    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getData().get(position).getProgramName(), String.valueOf(position));
    }

    private void setPlayStatus(final PropertyAnimImageView stateLevelIV,
                               @PlayerStatus final int status) {
        if (status == PlayerStatus.PLAYING) {
            stateLevelIV.setImageLevelBound(R.drawable.rhythm_list, 1, 20)
                    .startRepeatLevelAnim(800, new AnimatorListenerAdapter() {
                    });
        } else {
            if (status == PlayerStatus.LOADING) {
                stateLevelIV.startRotate();
            } else {
                stateLevelIV.pauseLevelAnim();
            }
            stateLevelIV.setImageLevel(R.drawable.fm_player_list_state_level, status);
        }
    }

    private void handleHimalayan(BaseViewHolder helper, PlayerInfo item, int position) {
        TextView tvTime = helper.getView(R.id.tvTime);
        PropertyAnimImageView levelIV = helper.getView(R.id.ivStateLevel);
        if (item.getSourceSubType() == PlayerSourceSubType.RADIO) {
            @StringRes int playState;
            if (position < mRecordLivingIndex) {
                levelIV.setVisibility(View.VISIBLE);
                if (position == mPlayingPos) {
                    helper.itemView.setEnabled(isEnableClick());
                    playState = R.string.radio_state_listen_record;
                    setPlayStatus(levelIV, mPlayStatus);
                } else {
                    helper.itemView.setEnabled(true);
                    setPlayStatus(levelIV, 0);
                    playState = R.string.radio_state_end;
                }
            } else if (position == mRecordLivingIndex) {
                playState = R.string.radio_state_living;
                if (position != mPlayingPos) {
                    setPlayStatus(levelIV, PlayerStatus.PAUSE);
                    levelIV.setVisibility(View.INVISIBLE);
                    helper.itemView.setEnabled(true);
                } else {
                    levelIV.setVisibility(View.VISIBLE);
                    helper.itemView.setEnabled(isEnableClick());
                    setPlayStatus(levelIV, mPlayStatus);
                }
            } else {
                levelIV.setVisibility(View.INVISIBLE);
                helper.itemView.setEnabled(false);
                playState = R.string.radio_state_coming;
            }
            tvTime.setText(mContext.getString(R.string.str_radio_time_state, item.getExtra1(), item.getExtra2(), mContext.getString(playState)));
        } else {
            if (position == mPlayingPos) {
                levelIV.setVisibility(View.VISIBLE);
                helper.itemView.setEnabled(isEnableClick());
                setPlayStatus(levelIV, mPlayStatus);
                tvTime.setText(String.format("%1$s/%2$s",
                        TimeUtils.timeMsToMMSS(mProgress),
                        TimeUtils.timeMsToMMSS(item.getDuration())));
            } else {
                setPlayStatus(levelIV, PlayerStatus.PAUSE);
                levelIV.setVisibility(View.INVISIBLE);
                helper.itemView.setEnabled(true);
                tvTime.setText(TimeUtils.timeMsToMMSS(item.getDuration()));
            }
        }
    }

    private void handleYQRadio(BaseViewHolder helper, PlayerInfo item, int position) {
        TextView tvTime = helper.getView(R.id.tvTime);
        PropertyAnimImageView levelIV = helper.getView(R.id.ivStateLevel);
        @StringRes int playState;
        if (position < mPlayingPos) {
            setPlayStatus(levelIV, PlayerStatus.PAUSE);
            levelIV.setVisibility(View.INVISIBLE);
            helper.itemView.setEnabled(false);
            playState = R.string.radio_state_end;
        } else if (position == mPlayingPos) {
            setPlayStatus(levelIV, PlayerStatus.PLAYING);
            levelIV.setVisibility(View.VISIBLE);
            helper.itemView.setEnabled(false);
            playState = R.string.radio_state_living;
        } else {
            setPlayStatus(levelIV, 6);
            levelIV.setVisibility(View.VISIBLE);
            helper.itemView.setEnabled(true);
            playState = R.string.radio_state_coming;
        }

        tvTime.setText(mContext.getString(R.string.str_radio_time_state, item.getExtra1(), item.getExtra2(), mContext.getString(playState)));
    }

    private void handleKoala(BaseViewHolder helper, PlayerInfo item, int position) {
        TextView tvTime = helper.getView(R.id.tvTime);
        PropertyAnimImageView levelIV = helper.getView(R.id.ivStateLevel);
        if (position == mPlayingPos) {
            levelIV.setVisibility(View.VISIBLE);
            helper.itemView.setEnabled(isEnableClick());
            setPlayStatus(levelIV, mPlayStatus);
            tvTime.setText(String.format("%1$s/%2$s",
                    TimeUtils.timeMsToMMSS(mProgress),
                    TimeUtils.timeMsToMMSS(item.getDuration())));
        } else {
            setPlayStatus(levelIV, PlayerStatus.PAUSE);
            levelIV.setVisibility(View.INVISIBLE);
            helper.itemView.setEnabled(true);
            tvTime.setText(TimeUtils.timeMsToMMSS(item.getDuration()));
        }
    }

    private boolean isEnableClick() {
        return mPlayStatus == PlayerStatus.PAUSE
                || mPlayStatus == PlayerStatus.ERROR
                || mPlayStatus == PlayerStatus.ERROR_BY_PLAYER
                || mPlayStatus == PlayerStatus.ERROR_BY_DATA_SOURCE;
    }
}
