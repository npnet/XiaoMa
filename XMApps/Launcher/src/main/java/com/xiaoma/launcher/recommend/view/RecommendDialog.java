package com.xiaoma.launcher.recommend.view;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.ad.utils.GsonUtil;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.views.BaseRecommendDialog;
import com.xiaoma.launcher.recommend.model.RecommendMessager;
import com.xiaoma.launcher.recommend.model.RecommendType;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.utils.SoundPoolUtils;

/**
 * @author taojin
 * @date 2019/2/25
 */

public class RecommendDialog extends BaseRecommendDialog {

    private TextView dialogMessage;

    private TextView dialogSure;
    private TextView dialogCancel;

    private ImageView mIvRecommend;

    private OnRecommendClick mOnRecommendClick;
    private String mMessage = "";
    private String mType = "";
    private int placeHoderId;
    private ImageView mIvRecPlay;

    public void setOnRecommendClick(OnRecommendClick onRecommendClick) {
        mOnRecommendClick = onRecommendClick;
    }

    public interface OnRecommendClick {
        void sureClick();
    }

    public RecommendDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public int getContentViewId() {
        return R.layout.dialog_recommend;
    }

    private XMAutoTrackerEventOnClickListener sureClick = new XMAutoTrackerEventOnClickListener() {
        @Override
        public ItemEvent returnPositionEventMsg(View view) {
            RecommendMessager recommendMessager = new RecommendMessager();
            recommendMessager.id = mType;
            recommendMessager.value = mMessage;
            return new ItemEvent(EventConstants.NormalClick.RECOMMEND_DIALOG_SURE, GsonUtil.toJson(recommendMessager));
        }

        @Override
        @BusinessOnClick
        public void onClick(View v) {
            if (mOnRecommendClick != null) {
                mOnRecommendClick.sureClick();
            }
            cancelDismissTimer();
            dismiss();
        }
    };

    @Override
    public void initView() {
        dialogMessage = findViewById(R.id.dialog_message);
        mIvRecommend = findViewById(R.id.iv_recommend);
        dialogSure = findViewById(R.id.dialog_sure);
        dialogCancel = findViewById(R.id.dialog_cancel);
        mIvRecPlay = findViewById(R.id.iv_rec_play);
        mIvRecPlay.setOnClickListener(sureClick);
        dialogSure.setOnClickListener(sureClick);
        dialogCancel.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                RecommendMessager recommendMessager = new RecommendMessager();
                recommendMessager.id = mType;
                recommendMessager.value = mMessage;
                return new ItemEvent(EventConstants.NormalClick.RECOMMEND_DIALOG_CANCEL, GsonUtil.toJson(recommendMessager));
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                cancelDismissTimer();
                dismiss();
            }
        });

    }

    @Override
    protected void initWindow() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = 656;
        lp.height = 425;
        getWindow().setAttributes(lp);
    }

    public void setDialogMessage(String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        mMessage = message;
        dialogMessage.setText(message);
    }

    public void setDialogType(String type) {
        if (TextUtils.isEmpty(type)) {
            return;
        }
        mType = type;

        switch (RecommendType.matchRecommendType(type)) {
            case FILM:
                placeHoderId = R.drawable.pic_movie;
                break;
            case FOOD:
                placeHoderId = R.drawable.pic_food;
                break;
            case PARKING:
                placeHoderId = R.drawable.pic_parking;
                break;
            case RADIO:
                placeHoderId = R.drawable.pic_radio;
                dialogSure.setText("");
                mIvRecPlay.setVisibility(View.VISIBLE);
                break;
            case SCENERY:
                placeHoderId = R.drawable.pic_view;
                break;
            case HOTEL:
                placeHoderId = R.drawable.pic_hotel;
                break;
            case MUSIC:
                placeHoderId = R.drawable.pic_music;
                dialogSure.setText("");
                mIvRecPlay.setVisibility(View.VISIBLE);
                break;
            case GAS:
                placeHoderId = R.drawable.pic_parking;
                break;
            default:
                break;
        }
    }

    public void setMsgColor(String color) {
        if (TextUtils.isEmpty(color)) {
            return;
        }
        try {
            dialogMessage.setTextColor(Color.parseColor(color));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注：先设置type setDialogType
     *
     * @param recommendIv
     */
    public void setIvRecommend(String recommendIv) {
        ImageLoader.with(mContext).load(recommendIv).placeholder(placeHoderId == 0 ? R.drawable.icon_default_icon : placeHoderId).into(mIvRecommend);
    }

    @Override
    protected long getTotalTime() {
        return 10000;
    }

    @Override
    protected CountDownTimer getDismissTimer() {
        return new CountDownTimer(getTotalTime(), COUNT_TIME_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (dialogCancel != null) {
                    dialogCancel.setText(mContext.getString(R.string.not_need_now, millisUntilFinished / 1000 + 1));
                }

            }

            @Override
            public void onFinish() {
                if (isShowing()) {
                    dismiss();
                }
            }
        };
    }

    @Override
    public void show() {
        try {
            // 播放提示音
            SoundPoolUtils.getInstance(mContext).play(R.raw.recom_beep, true);
            super.show();
            startDismissTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
