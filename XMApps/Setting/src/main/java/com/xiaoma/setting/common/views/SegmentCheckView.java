package com.xiaoma.setting.common.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.xiaoma.setting.R;
import com.xiaoma.setting.common.utils.AnimUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SegmentCheckView extends FrameLayout {
    private SettingCheckView settingCheckView;
    private View line;
    private SegmentControl segmentControl;
    private boolean powerState = false;
    private CharSequence titleText = "";
    private boolean isShowLine;
    private CharSequence egmentText;
    private AnimUtils animUtils;
    private StateChangListener stateListener;


    public SegmentCheckView(Context context) {
        super(context);
        initView(context, null);
    }

    public SegmentCheckView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public SegmentCheckView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public SegmentCheckView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    public void initView(final Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_setting_control, this);
        settingCheckView = findViewById(R.id.scv);
        line = findViewById(R.id.line);
        segmentControl = findViewById(R.id.segment_control);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SegmentCheckView);
            powerState = array.getBoolean(R.styleable.SegmentCheckView_defOnOff, false);
            isShowLine = array.getBoolean(R.styleable.SegmentCheckView_showLine, false);
            titleText = array.getText(R.styleable.SegmentCheckView_titleText);
//            textColor = array.getColor(R.styleable.SegmentCheckView_controlTextColor, defTextColor);
//            textSize = array.getDimensionPixelOffset(R.styleable.SegmentCheckView_controlTextSize, defTextSize);
            egmentText = array.getText(R.styleable.SegmentCheckView_segmentText);
            array.recycle();
            setData();
        }
        if (settingCheckView == null || segmentControl == null) {
            return;
        }
        settingCheckView.setListener(new SettingCheckView.CheckChangeListener() {
            @Override
            public void onCheckChanged(int viewId, boolean isCheck) {
                setPowerState(isCheck);
            }
        });
        segmentControl.setOnSegmentControlClickListener(new SegmentControl.OnSegmentControlClickListener() {
            @Override
            public void onSegmentControlClick(int viewId, int index) {
                if (stateListener != null) {
                    stateListener.onSegmentStateChange(getId(), index);
                }
            }
        });
    }

    private void setPowerState() {
        if (segmentControl != null) {
            setPowerState(segmentControl.getVisibility() == VISIBLE ? false : true);
        }
    }

    public void setSegmentIndex(int index) {
        if (segmentControl != null && settingCheckView != null) {
            if (index >= 0) {
                settingCheckView.setCheckState(true);
            } else {
                settingCheckView.setCheckState(false);
            }
            segmentControl.setSelectedIndex(index);
        }
    }

    private void setPowerState(boolean state) {
        if (state) {
            getAnimUtils().show();
        } else {
            getAnimUtils().hide();
        }
        notifyCheckState(state);
    }

    public void setStateListener(StateChangListener stateListener) {
        this.stateListener = stateListener;
    }

    public void notifyCheckState(boolean state) {
        if (segmentControl != null && stateListener != null && settingCheckView != null) {
            powerState = state;
            stateListener.onCheckStateChange(getId(), state);
        }
    }

    private AnimUtils getAnimUtils() {
        if (animUtils == null) {
            animUtils = AnimUtils.newInstance(getContext(), segmentControl, settingCheckView, getResources().getDimensionPixelOffset(R.dimen.setting_power_view_height));
        }
        return animUtils;
    }

    public void setData() {
        if (line != null) {
            line.setVisibility(isShowLine ? VISIBLE : GONE);
        }
        if (settingCheckView != null) {
            settingCheckView.setTitle(titleText);
        }
        if (segmentControl != null) {
            settingCheckView.setCheckState(powerState);
            segmentControl.setVisibility(powerState ? VISIBLE : GONE);
        }
        if (segmentControl != null && egmentText != null) {
            List<String> segmentList = new ArrayList<>();
            StringTokenizer tokens = new StringTokenizer(egmentText.toString(), "|");
            while (tokens.hasMoreTokens()) {
                segmentList.add(tokens.nextToken());
            }
            if (segmentList.size() > 0) {
                String[] segmentText = segmentList.toArray(new String[0]);
                segmentControl.setText(segmentText);
            }
        }
    }

    public interface StateChangListener {
        public void onCheckStateChange(int viewId, boolean off);

        public void onSegmentStateChange(int viewId, int index);
    }
}
