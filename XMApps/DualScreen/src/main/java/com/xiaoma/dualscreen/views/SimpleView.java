package com.xiaoma.dualscreen.views;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.dualscreen.R;
import com.xiaoma.dualscreen.constant.TabState;
import com.xiaoma.dualscreen.manager.DualViewManager;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.view.MarqueeTextView;
import com.xiaoma.utils.ThreadUtils;
import com.xiaoma.utils.log.KLog;

/**
 * @author: iSun
 * @date: 2019/3/7 0007
 */
public class SimpleView extends BaseView {

    private MarqueeTextView mTvSimple;
    private LinearLayout mPhoneContainer;
    private TextView mTvStatus, mTvName, mTvTime;
    private ImageView musicIcon;
    private static String audioTitle;
    private String phoneTitle;
    private ISimpleViewShowCallback iSimpleViewShowCallback;
    private volatile boolean isSimpleMenuDisplayOn = false;

    public SimpleView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public SimpleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public SimpleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public SimpleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init();
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public int contentViewId() {
        return R.layout.view_simple;
    }

    @Override
    public void onViewCreated() {

    }

    @Override
    public void onDestory() {
        super.onDestory();
        setiSimpleViewShowCallback(null);
    }

    private void init() {
        mTvSimple = findViewById(R.id.tv_simple);
        mPhoneContainer = findViewById(R.id.rl_phone_container);
        mTvStatus = findViewById(R.id.tv_status);
        mTvName = findViewById(R.id.tv_name);
        mTvTime = findViewById(R.id.tv_time);
        musicIcon = findViewById(R.id.media_icon);
        isSimpleMenuDisplayOn = false;
    }

    public void onResume(){
        if (!DualViewManager.getInstance().isHigh()) {
            return;
        }
        if(DualViewManager.getInstance().getCurrentType() == TabState.PHONE){
            if(getPhoneState() != 0){
//                if(getPhoneState() != 4 && getPhoneState() != 3){
//                    setSimpleTvText(phoneTitle, false);
//                }
                setSimpleTvText("", false);
            }else{
                if(isMediaPlaying()){
                    setSimpleTvText(audioTitle, true);
                }else{
                    setSimpleTvText(phoneTitle, false);
                }
            }
        }else if(DualViewManager.getInstance().getCurrentType() == TabState.MEDIA){
            if(getPhoneState() != 0){
                if(getPhoneState() != 4 && getPhoneState() != 3){
                    setSimpleTvText(phoneTitle, false);
                }
            }else{
                dismissSimpleText();
            }
        }else if(DualViewManager.getInstance().getCurrentType() == TabState.NAVI){
            dismissSimpleText();
        }else{
            if(getPhoneState() != 0){
                if(getPhoneState() != 4 && getPhoneState() != 3){
                    setSimpleTvText(phoneTitle, false);
                }
            }else if(isMediaPlaying()){
                setSimpleTvText(audioTitle, true);
            }else {
                dismissSimpleText();
            }
        }
    }

    private boolean isMediaPlaying(){
        return iSimpleViewShowCallback == null?false:iSimpleViewShowCallback.isMediaPlaying();
    }

    private int getPhoneState(){
        return iSimpleViewShowCallback == null?0:iSimpleViewShowCallback.getCallState();
    }

    private void setSimpleTvText(final String text, final boolean isMedia){
        if (!DualViewManager.getInstance().isHigh()) {
            return;
        }
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                if(!TextUtils.isEmpty(text)){
                    if(isMedia) musicIcon.setVisibility(VISIBLE);
                    else musicIcon.setVisibility(GONE);
                    mTvSimple.setText(text);
                    mTvSimple.setVisibility(View.VISIBLE);
                    mPhoneContainer.setVisibility(View.GONE);
                    showSimpleText();
                }else{
                    musicIcon.setVisibility(GONE);
                    dismissSimpleText();
                }
            }
        });
    }

    public void setAudioTitle(String audioTitle) {
        this.audioTitle = audioTitle;
    }

    public void setPhoneTitle(String phoneTitle) {
        this.phoneTitle = phoneTitle;
    }

    public void setiSimpleViewShowCallback(ISimpleViewShowCallback iSimpleViewShowCallback) {
        this.iSimpleViewShowCallback = iSimpleViewShowCallback;
        isSimpleMenuDisplayOn = false;
    }

    public void setSimplePhoneText(final String status, final String name, final String time) {
        KLog.e("xm", "isun setSimplePhoneText : " + status + "  name:" + name + "   time:" + time);
        if (!DualViewManager.getInstance().isHigh()) {
            return;
        }
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(status) && TextUtils.isEmpty(name) && TextUtils.isEmpty(time)) {
                    dismissSimpleText();
                    return;
                }
                if (DualViewManager.getInstance().getCurrentType() == TabState.PHONE
                        || DualViewManager.getInstance().getCurrentType() == TabState.NAVI) {
                    return;
                }
                musicIcon.setVisibility(GONE);
                mTvSimple.setVisibility(View.GONE);
                mTvStatus.setText(status);
                mTvName.setText(name);
                mTvTime.setText(time);
                mPhoneContainer.setVisibility(View.VISIBLE);
                showSimpleText();
            }
        });
    }


    public void showSimpleText() {
        if (!DualViewManager.getInstance().isHigh()) {
            return;
        }
        if(!isSimpleMenuDisplayOn){
            XmCarFactory.getCarVendorExtensionManager().setSimpleMenuDisplay(SDKConstants.VALUE.CanCommon_ON);
        }
        isSimpleMenuDisplayOn = true;
    }

    public void dismissSimpleText() {
        if(ThreadUtils.isMainThread()){
            clearSimpleShow();
        }else{
            ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                @Override
                public void run() {
                    clearSimpleShow();
                }
            });
        }
    }

    private void clearSimpleShow(){
        musicIcon.setVisibility(GONE);
        mPhoneContainer.setVisibility(View.GONE);
        mTvSimple.setText("");
        mTvStatus.setText("");
        mTvName.setText("");
        mTvTime.setText("");
        XmCarFactory.getCarVendorExtensionManager().setSimpleMenuDisplay(SDKConstants.VALUE.CanCommon_OFF);
        isSimpleMenuDisplayOn = false;
    }

    public void changeSkin(int skin) {
        if (skin == 1) {
            mTvSimple.setTextColor(getResources().getColor(R.color.simple_tv_color_blue));
            mTvStatus.setTextColor(getResources().getColor(R.color.simple_tv_color_blue));
            mTvName.setTextColor(getResources().getColor(R.color.simple_tv_color_blue));
            mTvTime.setTextColor(getResources().getColor(R.color.simple_tv_color_blue));
            musicIcon.setImageResource(R.drawable.icon_music_blue);
        } else {
            mTvSimple.setTextColor(getResources().getColor(R.color.simple_tv_color_yellow));
            mTvStatus.setTextColor(getResources().getColor(R.color.simple_tv_color_yellow));
            mTvName.setTextColor(getResources().getColor(R.color.simple_tv_color_yellow));
            mTvTime.setTextColor(getResources().getColor(R.color.simple_tv_color_yellow));
            musicIcon.setImageResource(R.drawable.icon_music_yellow);
        }
        isSimpleMenuDisplayOn = false;
    }

    public interface ISimpleViewShowCallback{
        boolean isMediaPlaying();

        int getCallState();
    }
}
