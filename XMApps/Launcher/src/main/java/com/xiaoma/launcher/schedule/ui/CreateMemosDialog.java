package com.xiaoma.launcher.schedule.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.constraint.Group;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xiaoma.aidl.model.ScheduleInfo;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.views.BaseRecommendDialog;
import com.xiaoma.mapadapter.model.SearchAddressInfo;
import com.xiaoma.mapadapter.ui.MapActivity;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.model.annotation.NormalOnClick;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;


public class CreateMemosDialog extends BaseRecommendDialog {

    private TextView mTv_date;
    private TextView mTv_location;
    private TextView mTv_message;
    private Button mBtn_create;
    private Button mBtn_cancle;
    private CountDownTimer mCountDownTimer;
    private ScheduleInfo mScheduleInfo;
    private OnMemosListener mOnMemosListener;
    public static final int REQUEST_MAP_CODE = 20;
    private Group mMsgGroup;
    private Group mDoneGroup;

    public interface OnMemosListener {

        void clickSure(ScheduleInfo scheduleInfo);

        void cancle();

        void clickLocation(ScheduleInfo scheduleInfo);

    }

    public void registerCreateMemosListener(OnMemosListener listener) {
        mOnMemosListener = listener;
    }

    public CreateMemosDialog(Context context, ScheduleInfo scheduleInfo) {
        super(context);
        EventBus.getDefault().register(this);
        mScheduleInfo = scheduleInfo;
    }

    @Override
    public int getContentViewId() {
        return R.layout.layout_dialog_memos;
    }

    @Override
    protected void initWindow() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.gravity = Gravity.START | Gravity.TOP;
        lp.width = 700;
        lp.height = 500;
        lp.x = 183;
        lp.y = 188;
        getWindow().setAttributes(lp);
    }

    @Override
    public void initView() {

        mTv_date = findViewById(R.id.tv_time);
        mTv_location = findViewById(R.id.tv_location);
        mTv_message = findViewById(R.id.tv_message);
        mBtn_create = findViewById(R.id.btn_create);
        mBtn_cancle = findViewById(R.id.btn_recreate);
        mMsgGroup = findViewById(R.id.group_msg);
        mDoneGroup = findViewById(R.id.group_done);

        mTv_message.setText(mScheduleInfo.getMessage());
        mTv_date.setText(String.format(mContext.getString(R.string.schedule_time), mScheduleInfo.getDate(), mScheduleInfo.getTime()));

        initEvent();

    }

    private void initEvent() {

        setCanceledOnTouchOutside(true);

        mBtn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick(EventConstants.NormalClick.CALENDAR_ADD_SCHEDULE_SURE)
            public void onClick(View v) {
                cancleCountDownTimer();
                if (mOnMemosListener != null) {
                    mOnMemosListener.clickSure(mScheduleInfo);
                }

            }
        });
        mBtn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick(EventConstants.NormalClick.CALENDAR_ADD_SCHEDULE_CANCEL)
            public void onClick(View v) {
                cancleCountDownTimer();
                if (mOnMemosListener != null) {
                    mOnMemosListener.cancle();
                }
                dismiss();
            }
        });

        mTv_location.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(EventConstants.NormalClick.CALENDAR_ADD_SCHEDULE_INFO,"");
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                if (mOnMemosListener != null) {
                    mOnMemosListener.clickLocation(mScheduleInfo);
                }
                mCountDownTimer.cancel();
                mBtn_create.setText(mContext.getString(R.string.confirm_to_create2));
            }
        });

        setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                cancleCountDownTimer();
                EventBus.getDefault().unregister(this);
            }
        });
    }

    public void done() {
        mDoneGroup.setVisibility(View.VISIBLE);
        mMsgGroup.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 1000);
    }


    @Subscriber(tag = MapActivity.EXTRA_LOCATION_DATA)
    private void updateLocation(SearchAddressInfo searchAddressInfo) {
        if (!TextUtils.isEmpty(searchAddressInfo.title)) {
            mTv_location.setText(searchAddressInfo.title);
            mTv_location.setBackgroundColor(Color.TRANSPARENT);
            mTv_location.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            mScheduleInfo.setLocation(searchAddressInfo.title);
        }
    }

    @Override
    public void show() {
        super.show();
        startCountDownTimer();
    }

    private void startCountDownTimer() {
        cancleCountDownTimer();
        mCountDownTimer = new CountDownTimer(5100, 1000) {
            @Override
            public void onTick(long l) {
                mBtn_create.setText(String.format(mContext.getString(R.string.confirm_to_create), (int) l / 1000));
            }

            @Override
            public void onFinish() {
                if (mOnMemosListener != null) {
                    mOnMemosListener.clickSure(mScheduleInfo);
                }
            }
        };
        mCountDownTimer.start();
    }

    private void cancleCountDownTimer() {
        if (mCountDownTimer!=null){
            mCountDownTimer.cancel();
        }
    }


}
