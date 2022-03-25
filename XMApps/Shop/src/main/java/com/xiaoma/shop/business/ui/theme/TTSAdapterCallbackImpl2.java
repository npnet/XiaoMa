package com.xiaoma.shop.business.ui.theme;

import android.text.TextUtils;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.model.SkusBean;
import com.xiaoma.ui.toast.XMToast;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/07/26
 * @Describe:
 */
public abstract class TTSAdapterCallbackImpl2 extends TTSAdapterCallbackImpl {
    private TTSHelper mTTSHelper;

    public TTSAdapterCallbackImpl2(BaseFragment fragment) {
        super(fragment);
    }

    @Override
    public boolean isAuditionPlaying(SkusBean item) {
        if (!super.isAuditionPlaying(item)) {
            return mTTSHelper != null && item.getId() == mTTSHelper.getCurrentPlayId();
        }
        return true;
    }

    @Override
    public void startAudition(SkusBean item) {
        if (mTTSHelper != null && mTTSHelper.getCurrentPlayId() != -1) {
            // 停止tts播报
            mTTSHelper.stop();
        }
        releaseAudition();
        if (!TextUtils.isEmpty(item.getAuditionUrl())) {
            super.startAudition(item);
            return;
        }
        if (null == mTTSHelper) {
            mTTSHelper = new TTSHelper();
            mTTSHelper.init(mContext, new TTSHelper.PlayCallback() {
                @Override
                public void onStart() {
                    onNotifyDataSetChanged();
                }

                @Override
                public void onStop() {
                    onNotifyDataSetChanged();
                }

                @Override
                public void onError() {
                    onNotifyDataSetChanged();
                    XMToast.toastException(mContext, R.string.play_audio_error);
                }
            });
        }
        mTTSHelper.start(item.getVoiceParam(), "你好，我是你的语音助理友幂", item.getId());
        onNotifyDataSetChanged();
    }

    @Override
    public void stopAudition(SkusBean item) {
        super.stopAudition(item);
        if(mTTSHelper!=null){
            mTTSHelper.stop();
        }
    }


}
