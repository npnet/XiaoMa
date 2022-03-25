package com.xiaoma.assistant.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.CarEventWithAssistantCallback;
import com.xiaoma.assistant.callback.IAssistantViewListener;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.ui.adapter.AssistantAdapter;
import com.xiaoma.assistant.ui.adapter.BaseMultiPageAdapter;
import com.xiaoma.assistant.utils.AssistantUtils;
import com.xiaoma.assistant.view.ContactView;
import com.xiaoma.assistant.view.HelpView;
import com.xiaoma.assistant.view.MultiPageView;
import com.xiaoma.assistant.view.MusicRecognitionView;
import com.xiaoma.assistant.view.VoiceTextView;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.manager.XmCarEventDispatcher;
import com.xiaoma.carlib.wheelcontrol.OnWheelKeyListener;
import com.xiaoma.carlib.wheelcontrol.WheelKeyEvent;
import com.xiaoma.carlib.wheelcontrol.XmWheelManager;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.systemuilib.SystemUIConstant;
import com.xiaoma.ui.UIUtils;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.FrameAnimView;
import com.xiaoma.utils.ScreenUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.model.ConversationItem;
import com.xiaoma.vr.model.SeoptType;
import com.xiaoma.vr.utils.VrConstants;

import java.util.ArrayList;
import java.util.List;

import static com.xiaoma.assistant.utils.Constants.Launcher.SYSTEM_UI;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/9/26
 * Desc：语音助手界面的UI
 */
public class AssistantDialog extends BaseAssistantDialog implements View.OnClickListener,
        View.OnLongClickListener, AdapterView.OnItemClickListener, View.OnTouchListener {

    private RelativeLayout rlMicAndText;
    private RelativeLayout rlExample;
    private RelativeLayout rlBack;
    private FrameAnimView micAnimView;
    private ImageView ivBack;
    private ListView conversationListView;
    private TextView searchResultOperate;
    private VoiceTextView tvShowText;
    private MultiPageView multipageView;
    private ContactView contactView;
    private MusicRecognitionView musicRecognitionView;
    private HelpView helpView;
    private ProgressBar loadingView;
    private View awakenView;
    private ImageView headMainIv;
    private ImageView headNotMainIv;

    private int[] micImagesNormal;
    private int[] micImagesRecord;
    private int[] micImagesWait;
    private boolean isInMultiPage;
    private boolean isInContact;
    private boolean isInHelp;
    private boolean isSrDialog;
    private View contentView;
    private OnWheelKeyListener.Stub mKeyListener;
    private MicIconState micIconState = MicIconState.NORMAL;
    private int lastVolume = 0;
    private boolean settingAnim;
    private boolean surfaceCreated;
    private static final boolean NORMAL_ANIM_ONESHOT = false;
    private static final boolean RECORD_ANIM_ONESHOT = true;
    private static final boolean WAIT_ANIM_ONESHOT = false;
    private static final int NORMAL_ANIM_FRAME_INTERVAL = 5;
    private static final int RECORD_ANIM_FRAME_INTERVAL = 2;
    private static final int WAIT_ANIM_FRAME_INTERVAL = 50;
    private boolean stopListening;

    @Override
    public boolean createView(final Context context) {
        this.context = context;
        mIsShowHalfScreen = false;
        if (context == null || (mVoiceDialog != null && mVoiceDialog.isShowing())) {
            hideHelpView();
            hideContactView();
            hideMultiPageView();
            if (isSrDialog) {
                showSrDialogView(false);
            }
            return false;
        }
        if (mVoiceDialog == null) {
            mVoiceDialog = new Dialog(context, R.style.transparent_dialog_v2) {
                @Override
                protected void onStop() {
                    setDialogSession(0);
                }
            };
            mVoiceDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    context.sendBroadcast(new Intent(SystemUIConstant.ACTION_NOTIFICATION_PANEL_COLLAPSE)
                            .setPackage(SYSTEM_UI));
                }
            });
            mVoiceDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    resetState();
                    if(multipageView != null) multipageView.unregisterWheelListener();
                    context.sendBroadcast(new Intent(CenterConstants.DISMISS_VOICE_ASSISTANT_DIALOG));
                    if (listener != null) {
                        listener.onDialogDismiss(dialogInterface);
                    }
                }
            });
            Window window = mVoiceDialog.getWindow();
            if (window == null) {
                if (listener != null) {
                    listener.onViewClose();
                }
                return false;
            }
            window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !ConfigManager.ApkConfig.isCarPlatform()) {
                window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else {
                window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            //window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            mVoiceDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                    if (listener != null) {
                        return listener.onDialogKeyEvent(dialogInterface, keyCode, keyEvent);
                    }
                    return false;
                }
            });
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);

            // 创建视图
            mVoiceDialog.addContentView(contentView = LayoutInflater.from(context).inflate(
                    R.layout.view_assistant_session, null), params);
            mRlRootView = mVoiceDialog.findViewById(R.id.rl_voice_dialog);
            if (!showFullScreenDialog()) {
                if (listener != null) {
                    listener.onViewClose();
                }
            }
            // 找到视图
            rlMicAndText = mVoiceDialog.findViewById(R.id.rl_mic_and_text);
            rlBack = mVoiceDialog.findViewById(R.id.assistant_rl_back);
            ivBack = mVoiceDialog.findViewById(R.id.assistant_iv_back);
            tvShowText = mVoiceDialog.findViewById(R.id.tv_show_text);
            micAnimView = mVoiceDialog.findViewById(R.id.btn_mic);
            //tvMicStatus = mVoiceDialog.findViewById(R.id.tv_voice_status);
            conversationListView = mVoiceDialog.findViewById(R.id.lv_chat);
            searchResultOperate = mVoiceDialog.findViewById(R.id.tv_search_result_operate);
            multipageView = mVoiceDialog.findViewById(R.id.mpv_assistant);
            contactView = mVoiceDialog.findViewById(R.id.cv_assistant);
            musicRecognitionView = mVoiceDialog.findViewById(R.id.view_music_recognition);
            loadingView = mVoiceDialog.findViewById(R.id.loading);
            //主副驾唤醒显示
            awakenView = mVoiceDialog.findViewById(R.id.awaken_view);
            headMainIv = mVoiceDialog.findViewById(R.id.head_main);
            headNotMainIv = mVoiceDialog.findViewById(R.id.head_not_main);
            //左下角帮助示例
            rlExample = mVoiceDialog.findViewById(R.id.rl_example);
            //帮助页面
            helpView = mVoiceDialog.findViewById(R.id.hv_assistant);
            //聊天框
            conversationListView.setOnItemClickListener(this);
            conversationListView.setOnTouchListener(this);
            //录音框
            micAnimView.setOnClickListener(this);
            micAnimView.setOnLongClickListener(this);
//            micAnimView.setCachedFrameCount(200);
            micAnimView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    Log.d("QBX", "surfaceCreated: ");
                    surfaceCreated = true;
                    if (micAnimView.isPlaying()) {
                        micAnimView.stop();
                    }
                    setVoiceAnim(0);
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {

                }
            });
            //返回键
            ivBack.setOnClickListener(this);
            rlExample.setOnClickListener(this);

            initAnim(context);
        }
        return true;
    }

    private void resetState() {
        surfaceCreated = false;
        micIconState = MicIconState.NORMAL;
    }

    private void initAnim(Context context) {
        TypedArray micTypedArrayNormal = context.getResources().obtainTypedArray(R.array.mic_icon_normal);
        micImagesNormal = new int[micTypedArrayNormal.length()];
        initDrawableArray(micImagesNormal, micTypedArrayNormal);
        micTypedArrayNormal.recycle();

        TypedArray micTypedArrayRecord = context.getResources().obtainTypedArray(R.array.mic_icon_record);
        micImagesRecord = new int[micTypedArrayRecord.length()];
        initDrawableArray(micImagesRecord, micTypedArrayRecord);
        micTypedArrayRecord.recycle();

        TypedArray micTypedArrayWait = context.getResources().obtainTypedArray(R.array.mic_icon_wait);
        micImagesWait = new int[micTypedArrayWait.length()];
        initDrawableArray(micImagesWait, micTypedArrayWait);
        micTypedArrayWait.recycle();
    }

    private void initDrawableArray(int[] array, TypedArray typedArray) {
        for (int i = 0; i < typedArray.length(); i++) {
            array[i] = typedArray.getResourceId(i, 0);
        }
    }

    public void showSrDialog() {
        Window window = mVoiceDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.TOP;
            lp.width = ScreenUtils.getScreenWidth(context);
            lp.height = 100;
            window.setAttributes(lp);
            WindowManager.LayoutParams attr = window.getAttributes();
            attr.flags = attr.flags
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            showSrDialogView(true);
            isInMultiPage = isInContact = isInHelp = false;
            setDialogSession(System.currentTimeMillis());
            mVoiceDialog.show();
            startTime = System.currentTimeMillis();
        }
    }


    @Override
    public void show(SeoptType localSeoptType) {
        isInMultiPage = isInContact = isInHelp = false;
        try {
            showSeoptType(localSeoptType);
            setDialogSession(System.currentTimeMillis());
            mVoiceDialog.show();
            initKeyEvent();
        } catch (SecurityException e) {
            //没有悬浮窗权限时会抛此异常
            e.printStackTrace();
            String tips = getString(R.string.system_alert_window_without_permission);
            XMToast.showToast(context, tips);
            if (listener != null) {
                listener.onSpeak(tips);
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                ActivityCompat.startActivity(context,
                        new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK), null);
            }
            setDialogSession(0);
            return;
        }
        //context.sendBroadcast(new Intent(Constants.Actions.ON_VOICE_DIALOG_SHOW));
        startTime = System.currentTimeMillis();

        //注册倒车影像时间
        XmCarEventDispatcher.getInstance().registerEvent(CarEventWithAssistantCallback.getInstance());
    }

    /**
     * 主副驾唤醒显示
     *
     * @param localSeoptType
     */
    private void showSeoptType(SeoptType localSeoptType) {
        Log.d("LxWakeupMultipleHelper", "showSeoptType:" + localSeoptType);
        switch (localSeoptType) {
            case CLOSE:
                awakenView.setVisibility(View.GONE);
                break;

            case LEFT:
                awakenView.setVisibility(View.VISIBLE);
                headMainIv.setSelected(true);
                headNotMainIv.setSelected(false);
                break;

            case RIGHT:
                awakenView.setVisibility(View.VISIBLE);
                headMainIv.setSelected(false);
                headNotMainIv.setSelected(true);
                break;
        }
    }

    private void initKeyEvent() {
        //方控事件
        XmWheelManager.getInstance().register(mKeyListener = new OnWheelKeyListener.Stub() {
            @Override
            public String getPackageName() throws RemoteException {
                return "com.xiaoma.assistant";
            }

            @Override
            public boolean onKeyEvent(int keyAction, int keyCode) throws RemoteException {
                KLog.d("MrMine", "onKeyEvent: " + "keyAction");
                return true;
            }
        }, new int[]{WheelKeyEvent.KEYCODE_WHEEL_SEEK_SUB,
                WheelKeyEvent.KEYCODE_WHEEL_SEEK_ADD});
    }

    public void setVoiceAnim(int volume) {
        if (!surfaceCreated || settingAnim || (micAnimView.isPlaying() && isMicRecordState()) || (volume == 0 && isMicWaitState()) || (volume != 0 && volume == lastVolume)) {
            return;
        }
        settingAnim = true;
//        volume *= 4;
        if (volume >= micImagesRecord.length) {
            volume = micImagesRecord.length - 1;
        } else if (volume < 0) {
            volume = 0;
        }

        if (stopListening) {
            stopListening = false;
            playNormalStateAnim();
        } else if (volume == 0) {
            playWaitStateAnim();
        } else {
            playRecordStateAnim(volume);
        }
        lastVolume = volume;
    }

    public void playNormalStateAnim() {
        Log.d("micAnim", "playNormalStateAnim: ");
        micIconState = MicIconState.NORMAL;
        lastVolume = 0;
        playAnim(micImagesNormal, NORMAL_ANIM_ONESHOT, NORMAL_ANIM_FRAME_INTERVAL);
    }

    private void playRecordStateAnim(int volume) {
        Log.d("micAnim", "playRecordStateAnim: " + volume);
        if (volume != 0) {
            micIconState = MicIconState.RECORD;
            playAnim(generateRecordAnim(volume), RECORD_ANIM_ONESHOT, RECORD_ANIM_FRAME_INTERVAL);
        }
        settingAnim = false;
    }

    public void playWaitStateAnim() {
        Log.d("micAnim", "playWaitStateAnim: ");
        if (playAnim(micImagesWait, WAIT_ANIM_ONESHOT, WAIT_ANIM_FRAME_INTERVAL)) {
            micIconState = MicIconState.WAIT;
        }
        settingAnim = false;
    }

    private boolean playAnim(int[] array, boolean oneShot, int frameInterval) {
        if (!surfaceCreated) return false;
        micAnimView.reset();
        micAnimView.setFrames(array, frameInterval);
        micAnimView.setOneShot(oneShot);
        micAnimView.start();
        return true;
    }

    private int[] generateRecordAnim(int volume) {
        int[] array;
        if (volume > lastVolume) {
            array = new int[volume - lastVolume];
            for (int index = lastVolume + 1, i = 0; index <= volume; index++, i++) {
                array[i] = micImagesRecord[index];
            }
        } else if (volume < lastVolume) {
            array = new int[lastVolume - volume];
            for (int index = lastVolume - 1, i = 0; index >= volume; index--, i++) {
                array[i] = micImagesRecord[index];
            }
        } else {
            array = new int[1];
            array[0] = micImagesRecord[volume];
        }
        return array;
    }

    public void updateShowText(String statusText) {
        if (tvShowText != null) {
            tvShowText.setText(statusText);
            startInputAnim();
        }
    }

    public void stopInputAnim() {
        if (tvShowText != null) {
            tvShowText.stopEllipsisAnimation();
        }
        if (!isMicNormalState()) {
            playNormalStateAnim();
        }
    }

    public void startInputAnim() {
        if (tvShowText != null) {
            tvShowText.startEllipsisAnimation();
        }
    }

    public void updateFeedbackView(boolean show, String content) {
        if (isSrDialog) {
            showSrDialogView(false);
        }

        if (!isFullScreenDialog) {
            showFullScreenDialog();
        }

        if (show) {
            ConversationItem conversationItem = new ConversationItem();
            conversationItem.setAction(VrConstants.ConversationType.OTHER);
            conversationItem.setAttachment(content);
            AssistantManager.getInstance().addItemToConversationList(conversationItem);
        }
    }

    public TextView getShowTextView() {
        return tvShowText;
    }

    public TextView getSearchResultOperate() {
        return searchResultOperate;
    }

    public FrameAnimView getMicroPhoneButton() {
        return micAnimView;
    }

    public MultiPageView getMultiPageView() {
        return multipageView;
    }

    public ContactView getContactView() {
        return contactView;
    }

    public RelativeLayout getRlExample() {
        return rlExample;
    }

    public MusicRecognitionView getMusicRecognitionView() {
        return musicRecognitionView;
    }

    public boolean setMultiPageData(BaseMultiPageAdapter adapter) {
        if (adapter != null) {
            setSearchResultText(adapter);
            multipageView.setVisibility(View.VISIBLE);
            searchResultOperate.setVisibility(View.VISIBLE);
            conversationListView.setVisibility(View.GONE);
            rlExample.setVisibility(View.GONE);
        }

        if (isSrDialog) {
            showSrDialogView(false);
        }

        if (!isFullScreenDialog) {
            showFullScreenDialog();
        }

        isInMultiPage = multipageView.setData(adapter);
        return isInMultiPage;
    }

    public void showDetailView() {
        searchResultOperate.setVisibility(View.VISIBLE);
        conversationListView.setVisibility(View.GONE);
        rlExample.setVisibility(View.GONE);

        if (isSrDialog) {
            showSrDialogView(false);
        }

        if (!isFullScreenDialog) {
            showFullScreenDialog();
        }

        BaseMultiPageAdapter adapter;
        if (!isInMultiPage && (adapter = multipageView.getAdapter()) != null) {
            adapter.setData(new ArrayList());
        }

    }

    private void setSearchResultText(BaseMultiPageAdapter adapter) {
        int dataSize = adapter.getCurrentList().size();
        searchResultOperate.setText(getResultOperateString(dataSize));
    }

    private SpannableString getResultOperateString(int dataSize) {
        String prefix = context.getString(R.string.please_choose_or_cancel);
        String content;
        if (dataSize <= MultiPageView.PAGE_SIZE) {
            content = context.getString(R.string.search_result_one_page);
        } else {
            content = context.getString(R.string.search_result_more_page);
        }
        SpannableString spannableString = new SpannableString(prefix + content);
        spannableString.setSpan(new ForegroundColorSpan(context.getColor(R.color.white)), 0, prefix.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(context.getColor(R.color.gray)), prefix.length() + 1, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public void hideMultiPageView() {
        if (multipageView.getVisibility() == View.VISIBLE) {
            multipageView.setVisibility(View.GONE);
            searchResultOperate.setVisibility(View.GONE);
            conversationListView.setVisibility(View.VISIBLE);
            isInMultiPage = false;
        }
    }


    public void showHelpView() {
        rlExample.setVisibility(View.GONE);
        conversationListView.setVisibility(View.GONE);
        helpView.setVisibility(View.VISIBLE);
        helpView.setData();
        if (isSrDialog) {
            showSrDialogView(false);
        }
        if (!isFullScreenDialog) {
            showFullScreenDialog();
        }
        String exampleQuestion = context.getString(R.string.example_question);
        XmCarFactory.getCarVendorExtensionManager().setRobAction(AssistantConstants.RobActionKey.CHECK_HELP);
        stopInputAnim();
        tvShowText.setText(exampleQuestion);
        AssistantManager.getInstance().speakThenListening(exampleQuestion);
        isInHelp = true;
    }

    public void hideHelpView() {
        if (helpView.getVisibility() == View.VISIBLE) {
            if (!isFullScreenDialog) {
                showFullScreenDialog();
            }
            rlExample.setVisibility(View.VISIBLE);
            conversationListView.setVisibility(View.VISIBLE);
            helpView.setVisibility(View.GONE);
            tvShowText.setText(AssistantUtils.getWelcomeWord(context));
            AssistantManager.getInstance().startListening(false);
            isInHelp = false;
        }
    }


    public boolean inMultipleForChooseMode() {
        return isInMultiPage;
    }


    public boolean inChooseMode() {
        return inMultipleForChooseMode() || inContactChooseMode();
    }


    public boolean setContactData(BaseMultiPageAdapter adapter) {
        if (adapter != null) {
            rlMicAndText.setVisibility(View.VISIBLE);
            setSearchResultText(adapter);
            searchResultOperate.setVisibility(View.VISIBLE);
            multipageView.setVisibility(View.GONE);
            conversationListView.setVisibility(View.GONE);
            contactView.setVisibility(View.VISIBLE);
            rlExample.setVisibility(View.GONE);
        }
        if (!isFullScreenDialog) {
            showFullScreenDialog();
        }

        isInContact = contactView.setData(adapter);
        return isInContact;
    }


    public void hideContactView() {
        if (contactView.getVisibility() == View.VISIBLE) {
            contactView.setVisibility(View.GONE);
            rlMicAndText.setVisibility(View.VISIBLE);
            conversationListView.setVisibility(View.VISIBLE);
            rlExample.setVisibility(View.VISIBLE);
            searchResultOperate.setVisibility(View.GONE);
            isInContact = false;
        }
    }


    public boolean inContactChooseMode() {
        return isInContact;
    }


    public boolean isViewShowing() {
        return mVoiceDialog != null && mVoiceDialog.isShowing();
    }

    public boolean isHelpViewShowing() {
        return isInHelp;
    }

    public boolean isSrDialogShowing() {
        return isSrDialog;
    }

    @Override
    public void setAdapter(List<ConversationItem> list) {
        if (conversationListView != null) {
            if (mAdapter == null) {
                mAdapter = new AssistantAdapter(context, list);
                conversationListView.setAdapter(mAdapter);
            } else {
                mAdapter.clearData();
                mAdapter.setData(list);
                notifyDataSetChanged();
            }

        }
    }

    @Override
    public void notifyDataSetChanged() {
        KLog.d("zhs", "notify fresh the UI");
        if (isSrDialog) {
            showSrDialogView(false);
        }

        if (!isFullScreenDialog) {
            showFullScreenDialog();
        }

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
            rlExample.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setAssistantListener(IAssistantViewListener listener) {
        this.listener = listener;
    }


    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onViewClick(v.getId());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        //返回false时,onclick不会执行
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //列表点击事件
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (listener != null) {
            listener.onViewTouch(event);
        }
        return false;
    }

    @Override
    public void closeView() {
        if (mVoiceDialog != null && mVoiceDialog.isShowing()) {
            KLog.i("closeVoicePopup");
            setDialogSession(0);
            mVoiceDialog.dismiss();
            hideHelpView();
            XmWheelManager.getInstance().unregister(mKeyListener);
            XmCarEventDispatcher.getInstance().unregisterEvent(CarEventWithAssistantCallback.getInstance());
        }
    }

    public void clearContent() {
        if (mAdapter != null) {
            mAdapter.clearData();
            mAdapter.notifyDataSetChanged();
        }
    }

    public void showLoadingView(boolean show) {
        UIUtils.runOnMain(new Runnable() {
            @Override
            public void run() {
                if (show) {
                    hideHelpView();
                }
                loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    public void showMultiPageView(boolean show) {
        multipageView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 短期免唤醒时UI效果设置
     */
    private void showSrDialogView(boolean show) {
        if (show) {
            rlBack.setVisibility(View.GONE);
            rlExample.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlMicAndText.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            rlMicAndText.setLayoutParams(params);

//            ViewGroup.LayoutParams layoutParams = micAnimView.getLayoutParams();
//            layoutParams.height = 60;
//            layoutParams.width = 60;
            tvShowText.setBackgroundResource(R.drawable.bg_voice_dialing_box_transparent);
            tvShowText.setMaxLines(1);
            isSrDialog = true;
            isFullScreenDialog = false;
        } else {
            rlBack.setVisibility(View.VISIBLE);
            rlExample.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlMicAndText.getLayoutParams();
            params.setMargins(0, 77, 0, 0);
            rlMicAndText.setLayoutParams(params);

//            ViewGroup.LayoutParams layoutParams = micAnimView.getLayoutParams();
//            layoutParams.height = 101;
//            layoutParams.width = 101;
            tvShowText.setBackgroundResource(R.drawable.bg_voice_dialing_box);
            tvShowText.setMaxLines(2);
            isSrDialog = false;
        }
    }

    public ViewGroup getContentView() {
        return (ViewGroup) contentView;
    }

    public boolean getHelpViewDetail() {
        return helpView.getDetail();
    }

    public void setHelpViewDetail() {
        helpView.setDetail();
    }

    enum MicIconState {
        WAIT,
        RECORD,
        NORMAL
    }

    private boolean isMicNormalState() {
        return micIconState == MicIconState.NORMAL;
    }

    private boolean isMicRecordState() {
        return micIconState == MicIconState.RECORD;
    }

    private boolean isMicWaitState() {
        return micIconState == MicIconState.WAIT;
    }

    public void setStopListening(boolean stopListening) {
        this.stopListening = stopListening;
    }

}
