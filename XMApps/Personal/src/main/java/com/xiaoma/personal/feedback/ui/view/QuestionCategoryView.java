package com.xiaoma.personal.feedback.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.xiaoma.personal.R;
import com.xiaoma.skin.views.XmSkinTextView;

/**
 * @author Gillben
 * date: 2018/12/04
 * <p>
 * 反馈问题类型选择View
 */
public class QuestionCategoryView extends XmSkinTextView {


    private boolean selected = false;
    private OnSelectListener onSelectListener;

    public QuestionCategoryView(Context context) {
        this(context, null);
    }


    public QuestionCategoryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public QuestionCategoryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            changeSelectStatus();
        }
        return super.onTouchEvent(event);
    }


    @Override
    public boolean isSelected() {
        return selected;
    }


    private void changeSelectStatus() {
        if (selected) {
            selected = false;
            setBackgroundResource(R.drawable.icon_question_category_unselect);
            if (onSelectListener != null) {
                onSelectListener.notSelected();
            }
        } else {
            selected = true;
            setBackgroundResource(R.drawable.icon_question_category_select);
            if (onSelectListener != null) {
                onSelectListener.selected();
            }
        }
    }


    public void setOnSelectListener(OnSelectListener listener) {
        this.onSelectListener = listener;
    }

    public interface OnSelectListener {
        void selected();

        void notSelected();
    }
}
