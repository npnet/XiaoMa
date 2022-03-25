package com.xiaoma.setting.common.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xiaoma.setting.R;
import com.xiaoma.setting.common.utils.AnimUtils;

/**
 * @author: iSun
 * @date: 2018/10/25 0025
 */
public class SettingItemView extends LinearLayout {
    private boolean isAnimation = true;
    //    private View contentView;
    private Context context;
    private StateListener listener;
    private TextView tvTitle;
    private RadioButtonLayout radioButtonLayout;
    private String textArray;

    //config
    private int titleColor;
    private CharSequence titleText = "";
    private int titleSize;
    private int titleWidth;
    private String[] texts;
    private int segmentSize;
    private int segmentColor;
    private int segmentWidth;
    private int segmentHeight;
    private int segmentGravity;
    private boolean shwoLine;
    private boolean showCheckBox;
    private boolean showTitle;
    private boolean showSegmentControl = true;
    private int contentLayout;
    private View addContentView;
    private AnimUtils animUtils;
    private boolean isDelayItemSelector = false;  // 是否是点击后需等返回结果才更改UI
    private int last_select_item_position = 0;    //记录通过setCheck
    private Handler handler = new Handler();
    // 主要是区分车辆设置 和其他页面的布局显示 1.其他页面 2.车辆设置
    private int currentItemType = 1;
    private LinearLayout llContainer;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (radioButtonLayout != null) {
                radioButtonLayout.setClickable(true);
            }
        }
    };
    private RadioButtonLayout.CustomOnCheckedChangeListener itemSelectorListener = new RadioButtonLayout.CustomOnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (listener != null) {
                listener.onSelect(getId(), checkedId);
            }
            if (isDelayItemSelector){
                radioButtonLayout.setCustomOnCheckedChangeListener(null);
                radioButtonLayout.checkIndex(last_select_item_position);
                radioButtonLayout.setCustomOnCheckedChangeListener(this);
                radioButtonLayout.setClickable(false);
                handler.postDelayed(runnable, 5000);
            }
        }
    };


    public SettingItemView(Context context) {
        super(context);
        initView(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }


    private void initView(Context context, AttributeSet attrs) {
        this.context = context;
        View view = inflate(getContext(), R.layout.view_setting_item, this);
        radioButtonLayout = view.findViewById(R.id.radioButtonLaout);
//        content = view.findViewById(R.id.item_content);
//        llTitle = view.findViewById(R.id.ll_title);
        tvTitle = view.findViewById(R.id.tv_name);
        llContainer = view.findViewById(R.id.ll_container);
//        checkBox = view.findViewById(R.id.cb_check);
//        line = view.findViewById(R.id.line);

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView);
            titleColor = ta.getColor(R.styleable.SettingItemView_titleColor, context.getResources().getColor(R.color.default_font));
            titleText = ta.getText(R.styleable.SettingItemView_titleTexts);
            titleSize = ta.getDimensionPixelSize(R.styleable.SettingItemView_titleSize, (int) getResources().getDimension(R.dimen.def_text_size));
            textArray = ta.getString(R.styleable.SettingItemView_segmentTexts);
            titleWidth = ta.getDimensionPixelSize(R.styleable.SettingItemView_titleWidth, 460);
//            if (textArray != null) {
//                texts = textArray.split("\\|");
//            }
            segmentSize = ta.getDimensionPixelSize(R.styleable.SettingItemView_segmentSize, (int) getResources().getDimension(R.dimen.def_text_size));
            segmentColor = ta.getColor(R.styleable.SettingItemView_segmentColor, context.getResources().getColor(R.color.default_font));
            segmentWidth = ta.getDimensionPixelSize(R.styleable.SettingItemView_segmentWidth, LayoutParams.WRAP_CONTENT);
            segmentHeight = ta.getDimensionPixelSize(R.styleable.SettingItemView_segmentHeight, LayoutParams.WRAP_CONTENT);
            segmentGravity = ta.getInt(R.styleable.SettingItemView_segmentGravity, Gravity.CENTER);
            shwoLine = ta.getBoolean(R.styleable.SettingItemView_showLines, true);
            showCheckBox = ta.getBoolean(R.styleable.SettingItemView_checkBox, true);
            showTitle = ta.getBoolean(R.styleable.SettingItemView_showTitle, true);
            showSegmentControl = ta.getBoolean(R.styleable.SettingItemView_showSegmentControl, true);
            contentLayout = ta.getResourceId(R.styleable.SettingItemView_contentLayout, -1);
            String type = ta.getString(R.styleable.SettingItemView_itemType);
            if (!TextUtils.isEmpty(type)){
                currentItemType = Integer.parseInt(type);
            }
            ta.recycle();
        }

        initView();
    }


    public void initView() {
        tvTitle.setText(titleText);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
        tvTitle.setTextColor(titleColor);
        //KLog.d("ljb", "titleWidth=" + titleWidth);
        tvTitle.getLayoutParams().width=titleWidth;
        radioButtonLayout.setButtonNames(textArray);
        if (currentItemType == 2){ // 如果是车辆设置 修改布局为vertical显示
            llContainer.setOrientation(VERTICAL);
            LinearLayout.LayoutParams layoutParams = (LayoutParams) radioButtonLayout.getLayoutParams();
            layoutParams.topMargin = 30;
            radioButtonLayout.setLayoutParams(layoutParams);
            tvTitle.setPadding(0,0,0,0);
        }
        initControl();
    }

    private void initControl() {
        radioButtonLayout.setCustomOnCheckedChangeListener(itemSelectorListener);
    }

    public synchronized void setCheck(int index) {
        if (radioButtonLayout != null) {
            if (isDelayItemSelector) {
                radioButtonLayout.setCustomOnCheckedChangeListener(null);
                radioButtonLayout.checkIndex(index);
                radioButtonLayout.setCustomOnCheckedChangeListener(itemSelectorListener);
                last_select_item_position = index;
                radioButtonLayout.setClickable(true);
                handler.removeCallbacks(runnable);
            } else {
                radioButtonLayout.setCustomOnCheckedChangeListener(null);
                radioButtonLayout.checkIndex(index);
                radioButtonLayout.setCustomOnCheckedChangeListener(itemSelectorListener);
            }
        }
    }

    public void setDelayItemSelector(boolean isDelayItemSelector) {
        this.isDelayItemSelector = isDelayItemSelector;
        if (radioButtonLayout != null) {
            radioButtonLayout.isToastWhenUnclickable(!isDelayItemSelector);
        }
    }


    @Override
    public void setClickable(boolean clickable) {
        if (radioButtonLayout != null) {
            radioButtonLayout.setClickable(clickable);
        }
    }

    public int getIndex() {
        return radioButtonLayout.getCheckedIndex();
    }


//    private AnimUtils getAnimUtils() {
//        if (animUtils == null) {
//            int height = segmentHeight + 5;
//            if (addContentView != null) {
//                height = getHeight(addContentView);
//            }
//            animUtils = AnimUtils.newInstance(getContext(), content, findViewById(R.id.ll_title), height);
//        }
//        return animUtils;
//    }

    private int getHeight(View view) {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        return view.getMeasuredHeight();
    }

//    //是否显示展开动画
//    public void setAnimation(boolean animation) {
//        isAnimation = animation;
//    }
//
//    public void setTitleVisibility(boolean visibility) {
//        this.titleVisibility = visibility;
//    }
//
//    public void setCheckBoxVisibility(boolean visibility) {
//        this.checkBoxVisibility = visibility;
//    }

    public void setListener(StateListener listener) {
        this.listener = listener;
    }

    public interface StateListener {
        public void onSelect(int viewId, int index);
    }
}
