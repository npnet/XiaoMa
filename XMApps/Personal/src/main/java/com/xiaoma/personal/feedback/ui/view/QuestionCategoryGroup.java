package com.xiaoma.personal.feedback.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.xiaoma.personal.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gillben on 2018/12/11 0011
 * <p>
 * desc: 问题类别容器
 */
public class QuestionCategoryGroup extends LinearLayout {


    private QuestionCategoryView mFeatureView, mSuggestionsView, mOtherView;
    //临时模拟问题类型List  1:功能使用  2:投诉建议  3:其它
    private List<String> categories = new ArrayList<>();

    private OnQuestionSelectCallback onQuestionSelectCallback;

    public QuestionCategoryGroup(Context context) {
        this(context,null);
    }

    public QuestionCategoryGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public QuestionCategoryGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initListener();
    }

    private void initView(Context context) {
        View contentView = View.inflate(context, R.layout.question_category_group,null);
        mFeatureView = contentView.findViewById(R.id.qcv_feature_category);
        mSuggestionsView = contentView.findViewById(R.id.qcv_suggestions);
        mOtherView = contentView.findViewById(R.id.qcv_other);

        addView(contentView);
    }

    private void initListener() {
        mFeatureView.setOnSelectListener(new QuestionCategoryView.OnSelectListener() {
            @Override
            public void selected() {
                categories.add(mFeatureView.getText().toString());
                handleCategory(categories);
            }

            @Override
            public void notSelected() {
                categories.remove(mFeatureView.getText().toString());
                handleCategory(categories);
            }
        });

        mSuggestionsView.setOnSelectListener(new QuestionCategoryView.OnSelectListener() {
            @Override
            public void selected() {
                categories.add(mSuggestionsView.getText().toString());
                handleCategory(categories);
            }

            @Override
            public void notSelected() {
                categories.remove(mSuggestionsView.getText().toString());
                handleCategory(categories);
            }
        });

        mOtherView.setOnSelectListener(new QuestionCategoryView.OnSelectListener() {
            @Override
            public void selected() {
                categories.add(mOtherView.getText().toString());
                handleCategory(categories);
            }

            @Override
            public void notSelected() {
                categories.remove(mOtherView.getText().toString());
                handleCategory(categories);
            }
        });
    }




    private void handleCategory(List<String> category){
        if (onQuestionSelectCallback != null){
            onQuestionSelectCallback.callback(category);
        }
    }

    public void setOnQuestionSelectCallback(OnQuestionSelectCallback callback){
       this.onQuestionSelectCallback = callback;
    }


    public interface  OnQuestionSelectCallback{
        void callback(List<String> category);
    }

}
