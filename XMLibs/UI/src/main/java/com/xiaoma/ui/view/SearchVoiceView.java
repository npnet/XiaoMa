package com.xiaoma.ui.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoma.ui.R;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/10/08
 *     desc   :
 * </pre>
 */
public class SearchVoiceView extends FrameLayout {

    public static final int MAX_SEARCH_TEXT_LENGTH = 32;
    private LinearLayout mLlRecording;
    private TextView mTvRecordingCancel;
    private ImageView mIvRecordingLeft;
    private TextView mTvRecordingContent;
    private ImageView mIvRecordingRight;
    private LinearLayout mLlNormal;
    private EditText mEtNormalContent;
    private TextView mTvStartVoiceSearch;
    private AnimationDrawable mLeftDrawable;
    private AnimationDrawable mRightDrawable;


    public SearchVoiceView(Context context) {
        this(context, null);
    }

    public SearchVoiceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchVoiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_voice_view, this);
        mLlRecording = (LinearLayout) view.findViewById(R.id.ll_recording);
        mTvRecordingCancel = (TextView) view.findViewById(R.id.tv_recording_cancel);
        mIvRecordingLeft = (ImageView) view.findViewById(R.id.iv_recording_left);
        mTvRecordingContent = (TextView) view.findViewById(R.id.tv_recording_content);
        mIvRecordingRight = (ImageView) view.findViewById(R.id.iv_recording_right);
        mLlNormal = (LinearLayout) view.findViewById(R.id.ll_normal);
        mEtNormalContent = (EditText) view.findViewById(R.id.et_normal_content);
        mTvStartVoiceSearch = (TextView) view.findViewById(R.id.tv_start_voice_search);
        mLeftDrawable = (AnimationDrawable) mIvRecordingLeft.getDrawable();
        mRightDrawable = (AnimationDrawable) mIvRecordingRight.getDrawable();

        mEtNormalContent.setLongClickable(false);
        mEtNormalContent.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                int dindex = 0;
                int count = 0;
                while (count <= MAX_SEARCH_TEXT_LENGTH && dindex < dest.length()) {
                    char c = dest.charAt(dindex++);
                    if (c < 128) {
                        count = count + 1;
                    } else {
                        count = count + 2;
                    }
                }
                if (count > MAX_SEARCH_TEXT_LENGTH) {
                    return dest.subSequence(0, dindex - 1);
                }

                int sindex = 0;
                while (count <= MAX_SEARCH_TEXT_LENGTH && sindex < source.length()) {
                    char c = source.charAt(sindex++);
                    if (c < 128) {
                        count = count + 1;
                    } else {
                        count = count + 2;
                    }
                }
                if (count > MAX_SEARCH_TEXT_LENGTH) {
                    sindex--;
                }
                return source.subSequence(0, sindex);
            }
        }});

        mEtNormalContent.setCustomSelectionActionModeCallback(new ActionModeCallbackInterceptor());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // call that method
            mEtNormalContent.setCustomInsertionActionModeCallback(new ActionModeCallbackInterceptor());
        }

    }

    private class ActionModeCallbackInterceptor implements ActionMode.Callback {
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return false;
        }


        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }


        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }


        public void onDestroyActionMode(ActionMode mode) {
        }
    }

    public void startVoiceAnim() {
        if (!mLeftDrawable.isRunning()) {
            mLeftDrawable.start();
        }
        if (!mRightDrawable.isRunning()) {
            mRightDrawable.start();
        }
    }

    public void stopVoiceAnim() {
        mLeftDrawable.stop();
        mRightDrawable.stop();
    }

    public void setHintText(String hintText) {
        mEtNormalContent.setHint(hintText);
    }

    public void clearEditText() {
        mEtNormalContent.getText().clear();
    }

    public void setNormal() {
        mLlNormal.setVisibility(VISIBLE);
        mEtNormalContent.setVisibility(VISIBLE);
        mLlRecording.setVisibility(GONE);
        stopVoiceAnim();
    }

    public void setHint(String text) {
        if (mEtNormalContent != null) {
            mEtNormalContent.setHint(text);
        }
    }

    public void setNormalContentText(CharSequence text) {
        mLlNormal.setVisibility(VISIBLE);
        mLlRecording.setVisibility(GONE);
        mEtNormalContent.setVisibility(VISIBLE);
        mEtNormalContent.setText(text);
        stopVoiceAnim();
    }

    public void setVoiceContent(CharSequence text) {
        if (mLlNormal.getVisibility() != GONE) {
            mLlNormal.setVisibility(GONE);
            mLlRecording.setVisibility(VISIBLE);
            mIvRecordingLeft.setVisibility(VISIBLE);
            mIvRecordingRight.setVisibility(VISIBLE);
            mTvRecordingContent.setVisibility(VISIBLE);
        }
            mTvRecordingContent.setText(text);
            startVoiceAnim();
    }


    public void setOnVoiceClickListener(@Nullable OnClickListener listener) {
        mTvStartVoiceSearch.setOnClickListener(listener);
    }

    public void setOnRecordingCancelClickListener(@Nullable OnClickListener listener) {
        mTvRecordingCancel.setOnClickListener(listener);
    }

}
