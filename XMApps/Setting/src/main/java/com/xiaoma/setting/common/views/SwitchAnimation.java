package com.xiaoma.setting.common.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.setting.R;
import com.xiaoma.setting.common.constant.EventConstants;
import com.xiaoma.setting.common.utils.AnimUtils;
import com.xiaoma.skin.views.XmSkinLinearLayout;
import com.xiaoma.utils.log.KLog;

import skin.support.content.res.SkinCompatResources;
import skin.support.widget.SkinCompatBackgroundHelper;
import skin.support.widget.SkinCompatHelper;

import static skin.support.widget.SkinCompatHelper.INVALID_ID;

/**
 * @author: iSun
 * @date: 2018/11/13 0013
 */
public class SwitchAnimation extends XmSkinLinearLayout {
    private Context context;
    private AnimUtils animUtils;
    private View hideView;
    private SwitchListener listener;
    private String switchText;
    private TextView tvSwitch;
    private boolean isAnim = true;
    private CheckBox checkbox;
    private int textColor;
    private int textSize;
    private boolean clickable = true;
    private OnTouchListener onTouchListener;
    private boolean needReverse = false;
    private boolean isDelayCheckbox = true;
    private ImageView noticeIcon;
    private Drawable checkBoxBg;

    private int mCheckBoxBg = INVALID_ID;
    private SkinCompatBackgroundHelper mBackgroundTintHelper;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (checkbox != null) {
                checkbox.setClickable(true);
            }
        }
    };
    private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (checkbox != null) {
                if (isDelayCheckbox) {
                    KLog.d("hzx", "点击checkbox, 当前时间: " + System.currentTimeMillis());
                    checkbox.setOnCheckedChangeListener(null);
                    checkbox.setChecked(!b);
                    checkbox.setOnCheckedChangeListener(checkedChangeListener);
                    checkbox.setClickable(false);
                    animControl(compoundButton, b, true, false);
                    handler.postDelayed(runnable, 5000);
                } else {
                    animControl(compoundButton, b, true, true);
                }
            }
        }
    };


    public SwitchAnimation(@NonNull Context context) {
        super(context);
        initView(context, null, 0);
    }

    public SwitchAnimation(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public SwitchAnimation(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    public SwitchAnimation(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    @Override
    public void setClickable(boolean clickable) {
        this.clickable = clickable;
        if (checkbox != null) {
            checkbox.setChecked(clickable);
        }
        if (!clickable) {
            animControl(null, false, false, false);
        }
    }

    public void setNeedReverse(boolean needReverse) {
        this.needReverse = needReverse;
    }

    public void check(boolean state) {
        if (isDelayCheckbox) {
            if (checkbox != null) {
                checkbox.setOnCheckedChangeListener(null);
                checkbox.setChecked(state);
                checkbox.setOnCheckedChangeListener(checkedChangeListener);
//                checkbox.setClickable(true);
                // 移除5s没有回调设置可点击runnable
                handler.removeCallbacks(runnable);
                // 值回调回来后, 1s后才能点击,防止多次点击导致动画异常,从而造成视图不能显示
                handler.postDelayed(runnable, 1000);
            }
            animControl(null, state, false, true);
        } else {
            checkbox.setChecked(state);
        }
    }

    public boolean isCheck() {
        if (checkbox != null) {
            return checkbox.isChecked();
        }
        return false;
    }

    private synchronized void initView(final Context context, AttributeSet attrs, int defStyleAttr) {
        this.context = context;
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SwitchAnimation);
            switchText = ta.getString(R.styleable.SwitchAnimation_switchText);
            textSize = ta.getDimensionPixelSize(R.styleable.SwitchAnimation_textSize, (int) getResources().getDimension(R.dimen.def_text_size));
            textColor = ta.getColor(R.styleable.SwitchAnimation_textColor, Color.WHITE);
            isAnim = ta.getBoolean(R.styleable.SwitchAnimation_anim, true);
            mCheckBoxBg = ta.getResourceId(R.styleable.SwitchAnimation_checkBoxBg, INVALID_ID);
            ta.recycle();

        }
        View view = inflate(getContext(), R.layout.view_switch_animation, this);
        tvSwitch = view.findViewById(R.id.tv_switch);
        checkbox = view.findViewById(R.id.checkbox);
        noticeIcon = view.findViewById(R.id.notice_icon);

        setSwitchText(switchText);
        checkbox.setChecked(true);
        checkbox.setOnCheckedChangeListener(checkedChangeListener);
        onTouchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if (clickable) {
                            return false;
                        } else {
                            SettingToast.build(getContext()).setMessage(getContext().getString(R.string.security_tips)).setImage(R.drawable.icon_error).show(2000);
                            return true;
                        }
                    default:
                        return false;
                }
            }
        };
        checkbox.setOnTouchListener(onTouchListener);
//        setCheckboxBackground(checkBoxBg);
        applyDropDownBackgroundResource();
        mBackgroundTintHelper = new SkinCompatBackgroundHelper(this);
        mBackgroundTintHelper.loadFromAttributes(attrs, defStyleAttr);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                initData();
            }
        });
    }

    private void applyDropDownBackgroundResource() {
        mCheckBoxBg = SkinCompatHelper.checkResourceId(mCheckBoxBg);
        if (mCheckBoxBg != INVALID_ID) {
            Drawable drawable = SkinCompatResources.getDrawableCompat(getContext(), mCheckBoxBg);
            if (checkbox != null && drawable != null) {
                checkbox.setBackground(drawable);
            }
        }
    }

    public void setSwitchText(String text) {
        this.switchText = text;
        if (tvSwitch != null && !TextUtils.isEmpty(text)) {
            tvSwitch.setText(text);
            tvSwitch.setTextColor(textColor);
            tvSwitch.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
    }

    private void animControl(CompoundButton checkBox, boolean checked, boolean isCallback, boolean isShowAnim) {
        if (hideView == null) {
            initData();
        }
        if (hideView != null) {
            if (needReverse) {
                checked = !checked;
            }
            if (checked) {
                if (isShowAnim) {
                    if (isAnim) {
                        getAnimUtils().show();
                    } else {
                        hideView.setVisibility(VISIBLE);
                    }
                }
            } else {
                if (isShowAnim) {
                    if (isAnim) {
                        getAnimUtils().hide();
                    } else {
                        hideView.setVisibility(GONE);
                    }
                }
            }
        }
        if (needReverse) {
            checked = !checked;
        }
        if (isCallback) {
            if (listener != null) {
                listener.onSwitch(getId(), checked);
                String status = checked ? "打开" : "关闭";
                XmAutoTracker.getInstance().onEvent(switchText, status, "CarSettingFragment",
                        EventConstants.PageDescribe.carSettingFragmentPagePathDesc);
            }
        }
    }

    private AnimUtils getAnimUtils() {
        if (animUtils != null) {
            return animUtils;
        }
        int height = getHeight(hideView);
        animUtils = AnimUtils.newInstance(getContext(), hideView, checkbox, height);
        return animUtils;
    }

    private int getHeight(View view) {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        return view.getMeasuredHeight();
    }

    private void initData() {
        int childCount = getChildCount();
        if (childCount > 1) {
            hideView = getChildAt(1);
        }
    }

    public void setCheckboxBackground(Drawable drawable) {
        if (checkbox != null && drawable != null) {
            checkbox.setBackground(drawable);
        }
    }

    public void setNoticeVisibity(boolean isVisible) {
        if (isVisible) {
            noticeIcon.setVisibility(VISIBLE);
        } else {
            noticeIcon.setVisibility(GONE);
        }
    }

    public void setNoticeIconClickListener(OnClickListener listener) {
        noticeIcon.setOnClickListener(listener);
    }

    public void setDelayCheckbox(boolean isDelayChekBox) {
        this.isDelayCheckbox = isDelayChekBox;
    }

    public void setListener(SwitchListener listener) {
        this.listener = listener;
    }

    public interface SwitchListener {
        void onSwitch(int viewId, boolean state);
    }

    @Override
    public void applySkin() {
        super.applySkin();
        if (mBackgroundTintHelper != null) {
            mBackgroundTintHelper.applySkin();
        }
        applyDropDownBackgroundResource();
    }
}
