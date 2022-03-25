package com.xiaoma.personal.feedback.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.personal.BuildConfig;
import com.xiaoma.personal.R;
import com.xiaoma.personal.common.OnlyCode;
import com.xiaoma.personal.common.RequestManager;
import com.xiaoma.personal.common.util.EventConstants;
import com.xiaoma.personal.common.util.FragmentTagConstants;
import com.xiaoma.personal.feedback.callback.OnSwitchFragmentCallback;
import com.xiaoma.personal.feedback.model.MessageInfo;
import com.xiaoma.personal.feedback.ui.view.QuestionCategoryGroup;
import com.xiaoma.personal.feedback.ui.view.RecordVoiceDialog;
import com.xiaoma.personal.feedback.vm.MessageVM;
import com.xiaoma.ui.progress.loading.XMProgress;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Gillben
 * date: 2018/12/04
 * <p>
 * 反馈首页
 */
@PageDescComponent(EventConstants.PageDescribe.feedbackHomeFragment)
public class FeedbackHomeFragment extends BaseFragment implements View.OnClickListener {


    private static final String TAG = FeedbackHomeFragment.class.getSimpleName();
    private TextView mMineMessageText;
    private ImageView mInputVoiceImg;
    private EditText mInputContentEdit;
    private TextView mCounterText;
    private ImageView mRedPointImageView;
    private QuestionCategoryGroup mQuestionCategoryGroup;
    private Button mPostBt;
    private OnSwitchFragmentCallback mOnSwitchFragmentCallback;
    private List<String> saveCategories = new ArrayList<>();
    private boolean recordContentFlag = false;


    public static BaseFragment newFragment() {
        return new FeedbackHomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOnSwitchFragmentCallback = (OnSwitchFragmentCallback) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_home_feedback, container, false);
        initView(contentView);
        return contentView;
    }


    @Override
    public void onResume() {
        super.onResume();
        refreshRedPoint();
    }

    private void initView(View contentView) {
        mMineMessageText = contentView.findViewById(R.id.tv_mine_message);
        mMineMessageText.setOnClickListener(this);
        mInputVoiceImg = contentView.findViewById(R.id.iv_voice_input);
        mInputVoiceImg.setOnClickListener(this);
        mRedPointImageView = contentView.findViewById(R.id.iv_mine_message_prompt);
        mInputContentEdit = contentView.findViewById(R.id.et_feedback_content);
        mCounterText = contentView.findViewById(R.id.tv_edit_counter);
        mQuestionCategoryGroup = contentView.findViewById(R.id.question_category_group);
        mPostBt = contentView.findViewById(R.id.bt_feedback_post);
        mPostBt.setOnClickListener(this);
        mInputContentEdit.requestFocus();
        initListener();
    }


    private void initListener() {
        mInputContentEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dealCounterText(s);
                handlePostButtonClickEvent();
                if (!TextUtils.isEmpty(s) && recordContentFlag) {
                    recordContentFlag = false;
                    mInputContentEdit.setSelection(s.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mQuestionCategoryGroup.setOnQuestionSelectCallback(category -> {
            saveCategories = category;
            handlePostButtonClickEvent();
        });
    }


    private void handlePostButtonClickEvent() {
        String tempEditString = mInputContentEdit.getText().toString();
        if (!TextUtils.isEmpty(tempEditString) && tempEditString.length() >= 5 && saveCategories != null && saveCategories.size() > 0) {
            mPostBt.setEnabled(true);
            mPostBt.setTextColor(Color.WHITE);
        } else {
            //不可提交
            mPostBt.setEnabled(false);
            mPostBt.setTextColor(mContext.getColor(R.color.bt_unable));
        }
    }


    private void cleanPostArea() {
        mInputContentEdit.setText("");
        saveCategories.clear();
    }


    /**
     * 处理编辑框计数器
     *
     * @param sequence 编辑框内容
     */
    private void dealCounterText(CharSequence sequence) {
        if (!TextUtils.isEmpty(sequence)) {
            String content = sequence.toString();
            int len = content.length();
            int remainder = 200 - len;
            if (remainder < 0) {
                remainder = 0;
            }
            mCounterText.setText(String.valueOf(remainder));
        } else {
            mCounterText.setText(String.valueOf(200));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_feedback_post:
                postFeedbackMessage();
                break;
            case R.id.tv_mine_message:
                cleanPostArea();
                if (mOnSwitchFragmentCallback != null) {
                    mOnSwitchFragmentCallback.onSwitchFragment(MineMessageFragment.newInstance(),
                            FragmentTagConstants.MINE_MESSAGE_FRAGMENT, true);
                }
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.mineMessage,
                        TAG, EventConstants.PageDescribe.feedbackHomeFragment);
                break;

            case R.id.iv_voice_input:
                RecordVoiceDialog.newInstance().show(getChildFragmentManager(),
                        null, content -> {
                            if (TextUtils.isEmpty(content)) {
                                return;
                            }
                            recordContentFlag = true;
                            mInputContentEdit.setText(content);
                        });
                break;

        }
    }

    /**
     * 提交问题反馈
     */
    private void postFeedbackMessage() {

        if (!NetworkUtils.isConnected(getContext())) {
            XMToast.toastException(getContext(), getString(R.string.network_error_retry));
            return;
        }


        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < saveCategories.size(); index++) {
            builder.append(saveCategories.get(index));
            if (index != saveCategories.size() - 1) {
                builder.append(",");
            }
        }

        String contentDesc = mInputContentEdit.getText().toString();
        XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.postFeedbackMessage,
                contentDesc, TAG, EventConstants.PageDescribe.feedbackHomeFragment);

        XMProgress.showProgressDialog(this, getString(R.string.loading));
        RequestManager.publishFeedbackQuestion(
                BuildConfig.CAR_CHANNEL_ID,
                contentDesc,
                builder.toString(),
                new ResultCallback<XMResult<OnlyCode>>() {
                    @Override
                    public void onSuccess(XMResult<OnlyCode> model) {
                        XMProgress.dismissProgressDialog(FeedbackHomeFragment.this);
                        if (model.isSuccess()) {
                            cleanPostArea();
                            if (mOnSwitchFragmentCallback != null) {
                                mOnSwitchFragmentCallback.onSwitchFragment(ThanksFeedbackFragment.newInstance(),
                                        FragmentTagConstants.THANKS_FEEDBACK_FRAGMENT, true);
                            }
                        } else {
                            XMToast.toastException(FeedbackHomeFragment.this.getContext(), getString(R.string.network_error_retry));
                        }
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        XMProgress.dismissProgressDialog(FeedbackHomeFragment.this);
                        if (code == 1097) {
                            XMToast.toastException(FeedbackHomeFragment.this.getContext(), getString(R.string.content_format_error));
                            return;
                        }
                        XMToast.toastException(FeedbackHomeFragment.this.getContext(), getString(R.string.network_error_retry));
                    }
                });
    }


    private void refreshRedPoint() {
        MessageVM messageVM = ViewModelProviders.of(this).get(MessageVM.class);
        messageVM.getMessageList(1, 1).observe(this, messageInfoXmResource -> {
            if (messageInfoXmResource == null) {
                return;
            }

            messageInfoXmResource.handle(new XmResource.OnHandleCallback<MessageInfo>() {
                @Override
                public void onLoading() {

                }

                @Override
                public void onSuccess(MessageInfo data) {
                    if (data == null) {
                        return;
                    }
                    int hasNoRead = data.getNoRead();
                    mRedPointImageView.setVisibility(hasNoRead > 0 ? View.VISIBLE : View.GONE);
                }

                @Override
                public void onFailure(String message) {

                }

                @Override
                public void onError(int code, String message) {

                }

                @Override
                public void onCompleted() {

                }
            });
        });
    }

}
