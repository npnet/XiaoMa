package com.xiaoma.assistant.practice;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.vrpractice.ui
 *  @file_name:      RecordActivity
 *  @author:         Rookie
 *  @create_time:    2019/6/4 16:46
 *  @description：   TODO             */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.practice.manager.SkillAudioFocusManager;
import com.xiaoma.assistant.practice.util.MediaRecordingUtils;
import com.xiaoma.assistant.practice.util.RecordUtil;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.annotation.SingleClick;
import com.xiaoma.model.pratice.VrPracticeConstants;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.log.KLog;

import java.io.File;
import java.io.FileInputStream;

public class RecordActivity extends BaseActivity implements View.OnClickListener {

    private Button btnSure;
    private Button btnRecord;
    private Button btnRetry;
    private Button btnDel;
    private Group groupRecordFinish;


    private boolean isRecording = false;
    private boolean isPlaying;
    private boolean needReset = false;
    private MediaPlayer mMediaPlayer;
    private String mAbsolutePath;
    private int recordTime;
    private Handler mHandler = new Handler();
    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            recordTime++;
            if (recordTime >= 60) {
                stopRecord();
            } else {
                btnRecord.setText(getString(R.string.finish_record_with_second, recordTime));
                mHandler.postDelayed(this, 1000);
            }

        }
    };
    private ConfirmDialog mDelRecordDialog;
    private ConfirmDialog mRetryDialog;
    private int mRequestCode;
    private int mActionPosition;
    //
//    private XmMicManager.OnMicFocusChangeListener mOnMicFocusChangeListener = new XmMicManager.OnMicFocusChangeListener() {
//        @Override
//        public void onMicFocusChange(int var1) {
//            if (var1 == XmMicManager.MICFOCUS_LOSS) {
//                //焦点被抢占
//                if (isRecording) {
//                    stopRecord();
//                }
//            }
//        }
//    };
    private ImageView mIvStartRecord;
    private ImageView mIvRecording;
    private ImageView mIvPlay;
    private boolean mIsEdit;
    private boolean isPause;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        initView();
        initData();
        registerExit();
    }

    private void registerExit() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("close_app_VR_PRACTICE");
        intentFilter.addAction(CenterConstants.CLICK_BLUE_PHONE);
        intentFilter.addAction(CenterConstants.SHOW_VOICE_ASSISTANT_DIALOG);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("close_app_VR_PRACTICE".equals(action)) {
                finish();
            } else if (CenterConstants.CLICK_BLUE_PHONE.equals(action)) {
                //停止录音
                if (isRecording) {
                    stopRecord();
                }
            } else if (CenterConstants.SHOW_VOICE_ASSISTANT_DIALOG.equals(action)) {
                if (isPlaying) {
                    playRecord();
                }
                if (isRecording) {
                    stopRecord();
                }
            }
        }
    };

    private void initView() {
        btnSure = findViewById(R.id.btn_sure);
        btnRecord = findViewById(R.id.btn_record);
        btnRetry = findViewById(R.id.btn_retry);
        btnDel = findViewById(R.id.btn_del);

        mIvStartRecord = findViewById(R.id.iv_start_record);
        mIvRecording = findViewById(R.id.iv_recording);
        mIvPlay = findViewById(R.id.iv_play_record);

        groupRecordFinish = findViewById(R.id.group_record_finish);
        btnSure.setOnClickListener(this);
        btnDel.setOnClickListener(this);
        btnRecord.setOnClickListener(this);
        btnRetry.setOnClickListener(this);
        mIvPlay.setOnClickListener(this);

    }

    private void initData() {

        Intent intent = getIntent();
        Bundle bundleExtra = intent.getBundleExtra(LaunchUtils.EXTRA_BUNDLE);
        if (bundleExtra == null) {
            return;
        }
        mActionPosition = bundleExtra.getInt(VrPracticeConstants.ACTION_POSITION, 0);
        mRequestCode = bundleExtra.getInt(VrPracticeConstants.SKILL_REQUEST_CODE, 0);
        String path = bundleExtra.getString(VrPracticeConstants.ACTION_JSON);
        if (!TextUtils.isEmpty(path)) {
            mIsEdit = true;
            //进入编辑状态
            mAbsolutePath = path;
            groupRecordFinish.setVisibility(View.VISIBLE);
            btnRecord.setText(R.string.start_record);
            btnRecord.setVisibility(View.GONE);
            mIvPlay.setVisibility(View.VISIBLE);
            mIvPlay.setImageResource(R.drawable.img_record_finish);
            mIvStartRecord.setVisibility(View.GONE);
            mIvRecording.setVisibility(View.GONE);
            recordTime = 0;
            isRecording = false;
        }
    }

    @Override
    @SingleClick
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure:
                finishRecord();
                break;
            case R.id.btn_del:
                showDelDialog();
                break;
            case R.id.btn_record:
                isRecording = !isRecording;
                if (isRecording) {
                    //开始录音
                    startRecord();
                } else {
                    //结束录音
                    stopRecord();
                }
                break;
            case R.id.btn_retry:
                //重新录音
                showRetryDialog();
                break;
            case R.id.iv_play_record:
                //播放/暂停
                playRecord();
                break;
            default:
                break;
        }
    }

    private void finishRecord() {
        if (TextUtils.isEmpty(mAbsolutePath)) {
            showToast(R.string.not_start_record);
        } else {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                showToast(R.string.is_playing_record);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(VrPracticeConstants.ACTION_JSON, mAbsolutePath);
                bundle.putInt(VrPracticeConstants.ACTION_POSITION, mActionPosition);
                bundle.putInt(VrPracticeConstants.SKILL_REQUEST_CODE, mRequestCode);
                LaunchUtils.launchAppWithData(this, VrPracticeConstants.PACKAGE_NAME, VrPracticeConstants.SKILL_CLASS_NAME, bundle);
                finish();
            }
        }
    }

    private void showRetryDialog() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPlaying = false;
            mIvPlay.setVisibility(View.VISIBLE);
            mIvPlay.setImageResource(R.drawable.img_record_finish);
            mIvRecording.setVisibility(View.GONE);
            mIvStartRecord.setVisibility(View.GONE);
        }
        mRetryDialog = new ConfirmDialog(this).setTitle(getString(R.string.tip)).setContent(getString(R.string.wheather_record_again));
        mRetryDialog.setPositiveButton(getString(R.string.sure), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新录音
                mRetryDialog.dismiss();
                needReset = true;
                groupRecordFinish.setVisibility(View.GONE);
                btnRecord.setVisibility(View.VISIBLE);
                mIvStartRecord.setVisibility(View.GONE);
                mIvPlay.setVisibility(View.GONE);
                mIvRecording.setVisibility(View.VISIBLE);
                RecordUtil.deleteSingleFile(mAbsolutePath);
                mAbsolutePath = null;
                isRecording = true;
                startRecord();
            }
        });
        mRetryDialog.setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRetryDialog.dismiss();
            }
        });
        mRetryDialog.show();

    }

    private void showDelDialog() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPlaying = false;
            //暂停
            mIvPlay.setVisibility(View.VISIBLE);
            mIvPlay.setImageResource(R.drawable.img_record_finish);
            mIvRecording.setVisibility(View.GONE);
            mIvStartRecord.setVisibility(View.GONE);
        }
        mDelRecordDialog = new ConfirmDialog(this).setTitle(getString(R.string.tip)).setContent(getString(R.string.wheather_del_record));
        mDelRecordDialog.setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDelRecordDialog.dismiss();
            }
        });
        mDelRecordDialog.setPositiveButton(getString(R.string.sure), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDelRecordDialog.dismiss();
                isRecording = false;
                needReset = true;
                groupRecordFinish.setVisibility(View.GONE);
                btnRecord.setVisibility(View.VISIBLE);
                mIvStartRecord.setVisibility(View.VISIBLE);
                mIvRecording.setVisibility(View.GONE);
                mIvPlay.setVisibility(View.GONE);
                RecordUtil.deleteSingleFile(mAbsolutePath);
                mAbsolutePath = null;
                if (mIsEdit) {
                    Bundle bundle = new Bundle();
                    bundle.putString(VrPracticeConstants.ACTION_JSON, mAbsolutePath);
                    bundle.putInt(VrPracticeConstants.ACTION_POSITION, mActionPosition);
                    bundle.putInt(VrPracticeConstants.SKILL_REQUEST_CODE, mRequestCode);
                    LaunchUtils.launchAppWithData(RecordActivity.this, VrPracticeConstants.PACKAGE_NAME, VrPracticeConstants.SKILL_CLASS_NAME, bundle);
                    finish();
                }
            }
        });
        mDelRecordDialog.show();

    }

    private void playRecord() {
        isPlaying = !isPlaying;
        mIvPlay.setVisibility(View.VISIBLE);
        mIvPlay.setImageResource(isPlaying ? R.drawable.img_play_pause :
                R.drawable.img_record_finish);
        mIvStartRecord.setVisibility(View.GONE);
        mIvRecording.setVisibility(View.GONE);
        if (isPlaying) {
            if (mMediaPlayer == null) {
                needReset = false;
                mMediaPlayer = new MediaPlayer();
                SkillAudioFocusManager.getInstance().init(RecordActivity.this);
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        KLog.d("MediaPlayer onCompletion  ");
                        resetPlayer();
                    }
                });
                mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        KLog.d("MediaPlayer onError what: " + what + " extra: " + extra);
                        showToast(R.string.play_record_error);
                        resetPlayer();
                        return true;
                    }
                });
            }
            try {
                if (needReset) {
                    needReset = false;
                    mMediaPlayer.reset();
                }
                if (!isPause) {
                    File tempFile = new File(mAbsolutePath);
                    FileInputStream fis = new FileInputStream(tempFile);
                    mMediaPlayer.setDataSource(fis.getFD());
                    mMediaPlayer.prepare();
                }
                SkillAudioFocusManager.getInstance().requestAudioFocus();
                if (SkillAudioFocusManager.getInstance().hasAudioFocus()) {
                    mMediaPlayer.start();
                } else {
                    showToast(R.string.play_error);
                }
                isPause = false;
            } catch (Exception e) {
                showToast(R.string.play_error);
                e.printStackTrace();
                resetPlayer();

            }
        } else {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                try {
                    mMediaPlayer.pause();
                    isPause = true;
                    SkillAudioFocusManager.getInstance().abandonAudioFocus();
                } catch (Exception e) {
                    e.printStackTrace();
                    resetPlayer();
                }
            }
        }
    }

    private void resetPlayer() {
        SkillAudioFocusManager.getInstance().abandonAudioFocus();
        isPlaying = false;
        needReset = true;
        isPause = false;
        mIvPlay.setVisibility(View.VISIBLE);
        mIvPlay.setImageResource(R.drawable.img_record_finish);
        mIvStartRecord.setVisibility(View.GONE);
        mIvRecording.setVisibility(View.GONE);
    }

    private void stopRecord() {
        MediaRecordingUtils.getInstance().stopRecord();
        groupRecordFinish.setVisibility(View.VISIBLE);
        btnRecord.setText(R.string.start_record);
        btnRecord.setVisibility(View.GONE);
        mIvPlay.setVisibility(View.VISIBLE);
        mIvPlay.setImageResource(R.drawable.img_record_finish);
        mIvStartRecord.setVisibility(View.GONE);
        mIvRecording.setVisibility(View.GONE);
        mHandler.removeCallbacks(timeRunnable);  //停止Timer
        recordTime = 0;
        isRecording = false;
        isPlaying = false;
    }

    private void startRecord() {
        btnRecord.setText(R.string.finish_record);

        mIvPlay.setVisibility(View.GONE);
        mIvRecording.setVisibility(View.VISIBLE);
        mIvStartRecord.setVisibility(View.GONE);
        recordTime = 0;
        MediaRecordingUtils.getInstance().startRecord(this, mOnRecordComplete);
        mHandler.postDelayed(timeRunnable, 1000); // 开始Timer

    }

    MediaRecordingUtils.OnRecordComplete mOnRecordComplete = new MediaRecordingUtils.OnRecordComplete() {
        @Override
        public void recordComplete(String path) {
            mAbsolutePath = path;
        }
    };

    @Override
    protected void onDestroy() {
        if (isRecording) {
            MediaRecordingUtils.getInstance().stopRecord();
        }
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            //暂停播放音频文件
            mMediaPlayer.stop();
        }
        MediaRecordingUtils.getInstance().releaseRecord();
        SkillAudioFocusManager.getInstance().abandonAudioFocus();
        mHandler.removeCallbacksAndMessages(null);
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }


}
