package com.xiaoma.facerecognize.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.facerecognize.R;
import com.xiaoma.facerecognize.common.EventConstants;
import com.xiaoma.facerecognize.common.FaceRecognizeConstants;
import com.xiaoma.facerecognize.sdk.RecognizeType;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.update.manager.AppUpdateCheck;

/**
 * @author KY
 * @date 03/26/2019
 */
@PageDescComponent(EventConstants.PageDescClick.FatigueDriving)
public class TipsDialogActivity extends AbsRecognizeDialog implements View.OnClickListener {

    private static final String PLAY_MUSIC_ACTION = "com.xiaoma.PLAY_ANTI_FATIGUE_MUSIC";
    private TextView mTitle;
    private ImageView mIcon;
    private AppCompatTextView mMsg;
    private Button mConfirm;
    private Button mCancel;
    private RecognizeType mCurrentType;
    private boolean playing;

    public static void newRecognize(Context context, RecognizeType type) {
        Intent intent = new Intent(context, TipsDialogActivity.class);
        intent.putExtra(FaceRecognizeConstants.IntentKey.TYPE_KEY, type);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getNaviBar().hideNavi();
        playing = false;
        initView();
        initListener();
        handleIntent(getIntent());
    }

    private void initView() {
        mTitle = findViewById(R.id.title);
        mIcon = findViewById(R.id.icon);
        mMsg = findViewById(R.id.msg);
        mConfirm = findViewById(R.id.confirm);
        mCancel = findViewById(R.id.cancel);
    }

    private void initListener() {
        mConfirm.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }

    private void handleIntent(Intent intent) {
        mCurrentType = (RecognizeType) intent.getSerializableExtra(FaceRecognizeConstants.IntentKey.TYPE_KEY);
        if (mCurrentType == null) {
            finish();
            return;
        }

        String randomStr = getRandomStr(mCurrentType);
        switch (mCurrentType) {
            case LightFatigueDriving:
                mTitle.setText(R.string.fating_tip_title);
                mIcon.setImageResource(R.drawable.icon_light_fatigue_driving);
                mConfirm.setVisibility(View.GONE);
                break;
            case MiddleFatigueDriving:
                mTitle.setText(R.string.fating_tip_title);
                mIcon.setImageResource(R.drawable.icon_light_fatigue_driving);
                mConfirm.setVisibility(View.VISIBLE);
                mCancel.setText(R.string.cancel);
                break;
            case Inattention:
                mTitle.setText(R.string.behavior_tip_title);
                mIcon.setImageResource(R.drawable.icon_driver_inattention);
                mConfirm.setVisibility(View.GONE);
                break;
            case Smoking:
                mTitle.setText(R.string.behavior_tip_title);
                mIcon.setImageResource(R.drawable.icon_smoke);
                mConfirm.setVisibility(View.GONE);
                break;
            case PhoneCall:
                mTitle.setText(R.string.behavior_tip_title);
                mIcon.setImageResource(R.drawable.icon_use_phone);
                mConfirm.setVisibility(View.GONE);
                break;
            default:
                // fo nothing
        }
        if (!TextUtils.isEmpty(randomStr)) {
            String target = getString(R.string.yomi_chinese_char);
            String replacement = getString(R.string.yomi_en_display);
            String displayText = randomStr.replace(target, replacement);
            mMsg.setText(displayText);
            playTTs(randomStr);
        }
    }

    private String getRandomStr(RecognizeType mCurrentType) {
        switch (mCurrentType) {
            case LightFatigueDriving:
                String[] lightStr = getResources().getStringArray(R.array.light);
                return lightStr[mRandom.nextInt(lightStr.length)];
            case MiddleFatigueDriving:
                String[] middleStr = getResources().getStringArray(R.array.middles);
                return middleStr[mRandom.nextInt(middleStr.length)];
            case Inattention:
                String[] attentionStr = getResources().getStringArray(R.array.attention);
                return attentionStr[mRandom.nextInt(attentionStr.length)];
            case Smoking:
                String[] smokeStr = getResources().getStringArray(R.array.smoke);
                return smokeStr[mRandom.nextInt(smokeStr.length)];
            case PhoneCall:
                String[] phoneStr = getResources().getStringArray(R.array.phone);
                return phoneStr[mRandom.nextInt(phoneStr.length)];
            default:
                return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUpdateCheck.getInstance().checkAppUpdate(getPackageName(), getApplication());
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.cancel, EventConstants.NormalClick.ok})
    @ResId({R.id.cancel, R.id.confirm})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.confirm:
                playMusic();
                finish();
                break;
            default:
        }
    }

    @SuppressLint("WrongConstant")
    private void playMusic() {
        if (!playing) {
            Intent intent = new Intent(PLAY_MUSIC_ACTION);
            intent.addFlags(0x01000000);
            sendBroadcast(intent);
            playing = true;
        }
    }

    @Override
    void onTick(int seconds) {
        if (mCurrentType == RecognizeType.MiddleFatigueDriving) {
            mCancel.setText(getString(R.string.count_cancel, seconds));
        } else {
            mCancel.setText(getString(R.string.count_close, seconds));
        }
    }

    @Override
    protected void onFinish() {
        if (mCurrentType == RecognizeType.MiddleFatigueDriving
                || mCurrentType == RecognizeType.HeavyFatigueDriving) {
            playMusic();
        }
        super.onFinish();
    }

    @Override
    protected void onIatConfirm() {
        if (mCurrentType == RecognizeType.MiddleFatigueDriving
                || mCurrentType == RecognizeType.HeavyFatigueDriving) {
            playMusic();
        }
    }
}
