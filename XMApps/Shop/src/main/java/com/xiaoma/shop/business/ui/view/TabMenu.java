package com.xiaoma.shop.business.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.shop.R;


/**
 * @author KY
 * @date 2018/11/7
 */
public class TabMenu extends FrameLayout implements Checkable {

    private int dimensionPixelOffset;
    private boolean checked;
    private String title;
    private TextView tabMenuTitle;
    private ImageView tabMenuBg;
    private OnCheckedChangeListener mOnCheckedChangeWidgetListener;

    public TabMenu(@NonNull Context context) {
        this(context, null);
    }

    public TabMenu(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabMenu(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.TabMenu, 0, 0);
        title = attributes.getString(R.styleable.TabMenu_title);
        checked = attributes.getBoolean(R.styleable.TabMenu_checked, false);
        attributes.recycle();

        initView();
        refreshView();
    }

    private void initView() {
        dimensionPixelOffset = getContext().getResources().getDimensionPixelOffset(R.dimen.height_home_menus);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_tab_menu, this, true);

        tabMenuTitle = view.findViewById(R.id.tab_menu_title);
        tabMenuBg = view.findViewById(R.id.tab_menu_bg);
        setClickable(true);
    }

    void setOnCheckedChangeWidgetListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeWidgetListener = listener;
    }

    public TextView getText() {
        return tabMenuTitle;
    }


    @Override
    public boolean performClick() {
        ((TabMenuGroup) getParent()).check(getId());

        final boolean handled = super.performClick();
        if (!handled) {
            // View only makes a sound effect if the onClickListener was
            // called, so we'll need to make one here instead.
            playSoundEffect(SoundEffectConstants.CLICK);
        }

        return handled;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(dimensionPixelOffset, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    public void setChecked(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            if (mOnCheckedChangeWidgetListener != null) {
                mOnCheckedChangeWidgetListener.onCheckedChanged(this, checked);
            }
            refreshView();
        }
    }

    private void refreshView() {
        tabMenuBg.setVisibility(checked ? VISIBLE : INVISIBLE);
        tabMenuTitle.setSelected(checked);
        tabMenuTitle.setText(title);
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        if (!isChecked()) {
            setChecked(!checked);
        }
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(TabMenu buttonView, boolean isChecked);
    }
}
