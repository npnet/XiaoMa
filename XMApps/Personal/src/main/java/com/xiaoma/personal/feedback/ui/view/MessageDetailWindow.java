package com.xiaoma.personal.feedback.ui.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.personal.R;
import com.xiaoma.personal.common.OnlyCode;
import com.xiaoma.personal.common.RequestManager;
import com.xiaoma.personal.common.util.SatisfactionType;
import com.xiaoma.personal.feedback.model.MessageInfo;
import com.xiaoma.ui.navi.NavigationBar;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Gillben
 * date: 2018/12/05
 * <p>
 * 消息详情页窗口
 */
public class MessageDetailWindow extends PopupWindow implements PopupWindow.OnDismissListener {

    private Context mContext;
    private NavigationBar mNavigationBar;
    private RelativeLayout mDetailPage;
    private ImageView mAvatarImg;
    private TextView mStatusText;
    private TextView mDateText;
    private TextView mContentText;
    private SatisfactionView mSatisfactionView;
    private EditText mEditText;
    private Button mPostBt;
    private LinearLayout editLinearLayout;

    private ConstraintLayout mExceptionPage;        //异常页

    private TextView mExceptionBt;

    private int satisfactionStatus = -1;
    private MessageInfo.MessageList messageInfo;
    private OnPopupWindowCallback onPopupWindowCallback;
    private OnCommentCallback onCommentCallback;


    public MessageDetailWindow(Context context, int width, int height) {
        this.mContext = context;
        View content = View.inflate(context, R.layout.pop_message_detail, null);
        setContentView(content);
        setParams(width, height);
        initView(content);
//        checkException();
    }

    private void setParams(int width, int height) {
        setWidth(width);
        setHeight(height);
        setTouchable(true);
        setFocusable(true);
        setClippingEnabled(false);
        setOnDismissListener(this);
        setAnimationStyle(R.style.PersonalInfoAnimation);
    }

    private void initView(View content) {
        mNavigationBar = content.findViewById(R.id.message_detail_nav);
        mNavigationBar.showBackNavi();
        mDetailPage = content.findViewById(R.id.relative_detail_page);
        mAvatarImg = content.findViewById(R.id.iv_message_detail_icon);
        mStatusText = content.findViewById(R.id.tv_message_detail_title);
        mDateText = content.findViewById(R.id.tv_message_detail_date);
        mContentText = content.findViewById(R.id.tv_message_detail_content);
        mSatisfactionView = content.findViewById(R.id.satisfaction_view);
        mEditText = content.findViewById(R.id.et_write_advice);
        mPostBt = content.findViewById(R.id.bt_post_message);
        editLinearLayout = content.findViewById(R.id.ll_message_detail_edit);

        mExceptionPage = content.findViewById(R.id.exception_page_layout);
        mExceptionBt = content.findViewById(R.id.tv_retry);

        //提交信息
        mPostBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postSatisfaction();
            }
        });

        //异常页重新加载
        mExceptionBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkException()) {
                    setContent(messageInfo);
                }
            }
        });


        //修改输入建议编辑框状态
        mSatisfactionView.setOnCommentCallback(new SatisfactionView.OnCommentCallback() {
            @Override
            public void good() {
                satisfactionStatus = SatisfactionType.GOOD.value;
                editLinearLayout.setVisibility(View.GONE);
            }

            @Override
            public void bad() {
                satisfactionStatus = SatisfactionType.BAD.value;
                editLinearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void reset() {
                satisfactionStatus = -1;
                mEditText.setText("");
                editLinearLayout.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onDismiss() {
        if (onPopupWindowCallback != null) {
            onPopupWindowCallback.close();
        }
    }


    private boolean checkException() {
        boolean isException;
        //            mExceptionPage.setVisibility(View.VISIBLE);
//            mDetailPage.setVisibility(View.GONE);
//            mExceptionPage.setVisibility(View.GONE);
//            mDetailPage.setVisibility(View.VISIBLE);
        isException = !NetworkUtils.isConnected(mContext);
        return isException;
    }


    public void setContent(MessageInfo.MessageList messageList) {
        this.messageInfo = messageList;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date date = new Date(Long.parseLong(messageList.getCreateDate()));
        mDateText.setText(dateFormat.format(date));
        mContentText.setText(messageList.getComments());
    }


    private void postSatisfaction() {
        if (!NetworkUtils.isConnected(mContext)) {
            XMToast.toastException(mContext, R.string.network_error_retry);
            return;
        }
        if (satisfactionStatus == -1) {
            XMToast.toastException(mContext, R.string.hint_not_select_comments);
            return;
        }

        String advice = mEditText.getText().toString();

        //评价为满意：评价内容置为空
        if (satisfactionStatus == SatisfactionType.GOOD.value) {
            advice = "";
        }

        RequestManager.postCommentSatisfactionEvaluation(
                messageInfo.getId(),
                satisfactionStatus,
                advice,
                new ResultCallback<XMResult<OnlyCode>>() {
                    @Override
                    public void onSuccess(XMResult<OnlyCode> model) {
                        XMToast.showToast(mContext, R.string.hint_submit_success);
                        if (onCommentCallback != null){
                            onCommentCallback.success(messageInfo);
                        }
                        dismiss();
                    }

                    @Override
                    public void onFailure(int code, String msg) {
//                        checkException();
                        XMToast.toastException(mContext, R.string.network_error_retry);
                        if (onCommentCallback != null){
                            onCommentCallback.failed();
                        }
                    }
                });
    }


    public void show(View parent, int gravity, int x, int y, OnPopupWindowCallback callback) {
        showAtLocation(parent, gravity, x, y);
        this.onPopupWindowCallback = callback;
        onPopupWindowCallback.open();
    }



    public void setOnCommentCallback(OnCommentCallback callback) {
        this.onCommentCallback = callback;
    }


    public interface OnPopupWindowCallback {
        void open();

        void close();
    }


    public interface OnCommentCallback {
        void success(MessageInfo.MessageList messageInfo);

        void failed();
    }

}
