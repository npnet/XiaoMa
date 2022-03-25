package com.xiaoma.personal.feedback.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.xiaoma.personal.R;

/**
 * @author Gillben
 * date: 2018/12/05
 * <p>
 * 满意度选择视图
 */
public class SatisfactionView extends LinearLayout {

    private static final String TAG = SatisfactionView.class.getSimpleName();
    //满意
    private LinearLayout satisfactionImage;
    //不满意
    private LinearLayout noSatisfactionImage;

    public static final int SATISFACTION = 0;
    public static final int NO_SATISFACTION = 1;
    private int curStatus = -1;

    private OnCommentCallback onCommentCallback;

    public SatisfactionView(Context context) {
        this(context, null);
    }

    public SatisfactionView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SatisfactionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View content = View.inflate(context, R.layout.view_satisfaction, null);
        initView(content);
        addView(content);
    }


    private void initView(View view) {
        satisfactionImage = view.findViewById(R.id.satisfaction_linear);
        noSatisfactionImage = view.findViewById(R.id.non_satisfaction_linear);

        addListener();
    }

    private void addListener() {
        satisfactionImage.setOnClickListener(v -> changeStatus(SATISFACTION, v.getId()));

        noSatisfactionImage.setOnClickListener(v -> changeStatus(NO_SATISFACTION, v.getId()));
    }


    private void changeStatus(int status, int imageId) {
        //已经选择，直接返回
        if (status == curStatus) {
            if (imageId == R.id.satisfaction_linear) {
                satisfactionImage.setBackgroundResource(R.drawable.comment_normal);
            } else if (imageId == R.id.non_satisfaction_linear) {
                noSatisfactionImage.setBackgroundResource(R.drawable.comment_normal);
            }
            if (onCommentCallback != null) {
                onCommentCallback.reset();
            }
            curStatus = -1;
            return;
        }

        if (status == SATISFACTION) {
            curStatus = SATISFACTION;
            satisfactionImage.setBackgroundResource(R.drawable.comment_press);
            noSatisfactionImage.setBackgroundResource(R.drawable.comment_normal);
            if (onCommentCallback != null) {
                onCommentCallback.good();
            }

        } else if (status == NO_SATISFACTION) {
            curStatus = NO_SATISFACTION;
            satisfactionImage.setBackgroundResource(R.drawable.comment_normal);
            noSatisfactionImage.setBackgroundResource(R.drawable.comment_press);
            if (onCommentCallback != null) {
                onCommentCallback.bad();
            }
        }
    }


    public int getCurStatus() {
        return curStatus;
    }


    public void setOnCommentCallback(OnCommentCallback callback) {
        this.onCommentCallback = callback;
    }

    public interface OnCommentCallback {
        void good();

        void bad();

        void reset();
    }

}
