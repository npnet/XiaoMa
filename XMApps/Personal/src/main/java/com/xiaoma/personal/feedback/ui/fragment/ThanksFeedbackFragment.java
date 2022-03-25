package com.xiaoma.personal.feedback.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.personal.R;
import com.xiaoma.personal.common.util.EventConstants;
import com.xiaoma.personal.feedback.ui.FeedbackActivity;

import java.util.Objects;

/**
 * @author  Gillben
 * date: 2018/12/04
 *
 * 感谢反馈
 */
@PageDescComponent(EventConstants.PageDescribe.thanksFeedbackFragment)
public class ThanksFeedbackFragment extends BaseFragment {


    private Button mBackFeedbackHomeBt;


    public static BaseFragment newInstance(){
        return new ThanksFeedbackFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_thank_feedback,container,false);
        initView(contentView);
        return contentView;
    }

    private void initView(View contentView) {
        mBackFeedbackHomeBt = contentView.findViewById(R.id.back_feedback_home);
        mBackFeedbackHomeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FeedbackActivity) Objects.requireNonNull(getActivity())).popFragmentFromStack();
            }
        });
    }

}
