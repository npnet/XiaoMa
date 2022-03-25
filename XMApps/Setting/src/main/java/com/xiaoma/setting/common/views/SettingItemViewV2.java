package com.xiaoma.setting.common.views;

import android.content.Context;
import android.content.res.TypedArray;
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
public class SettingItemViewV2 extends LinearLayout {
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
    private volatile boolean hasCallBack = true;


    public SettingItemViewV2(Context context) {
        super(context);
        initView(context, null);
    }

    public SettingItemViewV2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public SettingItemViewV2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public SettingItemViewV2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
            ta.recycle();
        }

        initView();
    }


    public void initView() {
        tvTitle.setText(titleText);
        tvTitle.setTextSize(titleSize);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
        //KLog.d("ljb", "titleWidth=" + titleWidth);
        tvTitle.getLayoutParams().width = titleWidth;
        radioButtonLayout.setButtonNames(textArray);
        initControl();
    }

    private void initControl() {
        radioButtonLayout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (listener != null && hasCallBack) {
                    listener.onSelect(getId(), checkedId);
                }
            }
        });
    }

    public void setCheck(int index) {
        hasCallBack = true;
        if (radioButtonLayout != null) {
            radioButtonLayout.check(index);
        }
    }

    //此check没有回调
    public void setCheckNoCallBack(int index) {
        hasCallBack = false;
        if (radioButtonLayout != null) {
            radioButtonLayout.check(index);
        }
    }

    @Override
    public void setClickable(boolean clickable) {
        if (radioButtonLayout != null) {
            radioButtonLayout.setClickable(clickable);
        }
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
        int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
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
