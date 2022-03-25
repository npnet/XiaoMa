package com.xiaoma.vrfactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.iflytek.speech.seopt.SeoptHelper;
import com.xiaoma.cariflytek.iat.VrAidlServiceManager;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.constant.VrConstants;
import com.xiaoma.vr.VrConfig;
import com.xiaoma.vr.tts.OnTtsListener;
import com.xiaoma.vrfactory.iat.XmIatManager;
import com.xiaoma.vrfactory.ivw.XmIvwManager;
import com.xiaoma.vrfactory.tts.XmTtsManager;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/1
 * Desc：语音相关广播接收者
 */
public class RecorderReceiver extends BroadcastReceiver {
    private static final String TAG = RecorderReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, final Intent intent) {
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        SeoptHelper.getInstance().setTempCloseSeopt(false);
        switch (action) {
            case VrConstants.Actions.START_IVW:
                //启动唤醒
                XmIvwManager.getInstance().startWakeup();
                break;
            case VrConstants.Actions.STOP_IVW:
                //停止唤醒
                XmIvwManager.getInstance().stopWakeup();
                break;
            case VrConstants.Actions.START_IAT:
                //启动识别
                boolean closeSeopt = intent.getBooleanExtra(VrConstants.ActionExtras.SEOPT_CLOSE, false);
                if (closeSeopt) {
                    SeoptHelper.getInstance().setTempCloseSeopt(closeSeopt);
                }
                XmIvwManager.getInstance().stopWakeup();
                XmIatManager.getInstance().startListeningNormal();
                break;
            case VrConstants.Actions.START_IAT_FOR_CHOOSE:
                //语音选择
                String stks = intent.getStringExtra(VrConstants.ActionExtras.STKS_FOR_CHOOSE);
                XmIvwManager.getInstance().stopWakeup();
                if (!TextUtils.isEmpty(stks)) {
                    XmIatManager.getInstance().startListeningForChoose(stks);
                } else {
                    XmIatManager.getInstance().startListeningForChoose();
                }
                break;
            case VrConstants.Actions.START_IAT_RECORD:
                //启动长语音识别
                XmIvwManager.getInstance().stopWakeup();
                int endTimeOut = intent.getIntExtra(VrConstants.ActionExtras.IAT_END_TIME_OUT, 0);
                int startTimeOut = intent.getIntExtra(VrConstants.ActionExtras.IAT_START_TIME_OUT, 0);
                if (startTimeOut > 0 || endTimeOut > 0) {
                    //根据前后端点超时自动结束
                    XmIatManager.getInstance().startListeningRecord(startTimeOut, endTimeOut);
                } else {
                    XmIatManager.getInstance().startListeningRecord();
                }
                break;
            case VrConstants.Actions.STOP_IAT:
                //停止识别
                XmIatManager.getInstance().stopListening();
                XmIvwManager.getInstance().startWakeup();
                break;
            case VrConstants.Actions.INIT_IVW:
                //初始化唤醒
                XmIvwManager.getInstance().init(context);
                break;
            case VrConstants.Actions.INIT_IAT:
                //初始化识别
                if (!XmIatManager.getInstance().getInitState())
                    XmIatManager.getInstance().init(context);
                break;
            case VrConstants.Actions.STOP_RECORDER:
                //停止录音
                XmIvwManager.getInstance().stopRecorder();
                break;
            case VrConstants.Actions.START_RECORDER:
                //启动录音
                XmIvwManager.getInstance().startWakeup();
                break;
            case VrConstants.Actions.UPLOAD_CONTACT_IAT:
                //联系人上传到字典
                ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
                    @Override
                    public void run() {
                        boolean isPhoneContact = intent.getBooleanExtra(VrConstants.ActionExtras.IAT_CONTACTS_TYPE_UPLOAD, true);
                        String contacts = intent.getStringExtra(VrConstants.ActionExtras.CONTACT_LIST);
                        XmIatManager.getInstance().upLoadContact(isPhoneContact, contacts);
                    }
                });
                break;
            case VrConstants.Actions.UPLOAD_APP_STATE: {
                //上传应用前后台状态
                ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
                    @Override
                    public void run() {
                        boolean isForeground = intent.getBooleanExtra(VrConstants.ActionExtras.IS_FOREGROUND, false);
                        String appType = intent.getStringExtra(VrConstants.ActionExtras.APP_TYPE);
                        XmIatManager.getInstance().uploadAppState(isForeground, appType);
                    }
                });
                break;
            }
            case VrConstants.Actions.UPLOAD_PLAY_STATE: {
                //上传播放状态
                ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
                    @Override
                    public void run() {
                        boolean isPlaying = intent.getBooleanExtra(VrConstants.ActionExtras.PLAY_STATE, false);
                        String appType = intent.getStringExtra(VrConstants.ActionExtras.APP_TYPE);
                        XmIatManager.getInstance().uploadPlayState(isPlaying, appType);
                    }
                });
                break;
            }
            case VrConstants.Actions.UPLOAD_NAVI_STATE:
                //上传导航状态
                ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
                    @Override
                    public void run() {
                        String naviState = intent.getStringExtra(VrConstants.ActionExtras.NAVI_STATE);
                        XmIatManager.getInstance().uploadNaviState(naviState);
                    }
                });
                break;
            case VrConstants.Actions.SET_IVW_THRESHOLD:
                //设置唤醒的阈值
                int curThresh = intent.getIntExtra(VrConstants.ActionExtras.IVW_THRESHOLD, VrConfig.DEFAULT_THRESH);
                XmIvwManager.getInstance().setIvwThreshold(curThresh);
                break;
            case VrConstants.Actions.START_SPEAKING:
                //三方应用启动TTS播报,如果当前在语音助手界面且有tts播报则三方应用不播报
                startSpeak(context, intent);
                break;
            case VrConstants.Actions.START_SPEAKING_BY_THIRD:
                startSpeakBThird(context, intent);
                break;
            case VrConstants.Actions.STOP_SPEAKING:
                //停止TTS播报
                XmTtsManager.getInstance().stopSpeaking();
                break;
            case VrConstants.Actions.REMOVE_TTS_SPEAKING_LISTENER:
                //移除TTS监听
                XmTtsManager.getInstance().removeListeners();
                break;
            case VrConstants.Actions.AUDITION_VOICE_TYPE: {
                //试听语音角色
                String param = intent.getStringExtra(VrConstants.ActionExtras.VOICE_PARAM);
                String speakContent = intent.getStringExtra(VrConstants.ActionExtras.SPEAK_CONTENT);
                final String pkgName = intent.getStringExtra(VrConstants.ActionExtras.PACKAGE_NAME);
                final String id = intent.getStringExtra(VrConstants.ActionExtras.TTS_ID);
                XmTtsManager.getInstance().auditionVoiceType(param, speakContent, new OnTtsListener() {
                    @Override
                    public void onCompleted() {
                        Intent replyIntent = new Intent(VrConstants.Actions.TTS_SPEAKING_COMPLETED);
                        replyIntent.putExtra(VrConstants.ActionExtras.PACKAGE_NAME, pkgName);
                        replyIntent.putExtra(VrConstants.ActionExtras.TTS_ID, id);
                        intent.addFlags(0x01000000);
                        context.sendBroadcast(replyIntent);
                    }

                    @Override
                    public void onBegin() {
                        Intent replyIntent = new Intent(VrConstants.Actions.TTS_SPEAKING_BEGIN);
                        replyIntent.putExtra(VrConstants.ActionExtras.PACKAGE_NAME, pkgName);
                        replyIntent.putExtra(VrConstants.ActionExtras.TTS_ID, id);
                        intent.addFlags(0x01000000);
                        context.sendBroadcast(replyIntent);
                    }

                    @Override
                    public void onError(int code) {
                        Intent replyIntent = new Intent(VrConstants.Actions.TTS_SPEAKING_ERROR);
                        replyIntent.putExtra(VrConstants.ActionExtras.PACKAGE_NAME, pkgName);
                        replyIntent.putExtra(VrConstants.ActionExtras.TTS_ID, id);
                        replyIntent.putExtra(VrConstants.ActionExtras.ERROR_CODE, code);
                        intent.addFlags(0x01000000);
                        context.sendBroadcast(replyIntent);
                    }
                });
                break;
            }
            case VrConstants.Actions.SET_VOICE_TYPE: {
                //设置语音角色
                String param = intent.getStringExtra(VrConstants.ActionExtras.VOICE_PARAM);
                String voiceName = intent.getStringExtra(VrConstants.ActionExtras.VOICE_NAME);
                XmTtsManager.getInstance().setVoiceType(param, voiceName);
                break;
            }
            default:
                break;
        }
    }

    private void startSpeakBThird(final Context context, final Intent intent) {
        String content = intent.getStringExtra(VrConstants.ActionExtras.TTS_SPEAKING_CONTENT);
        final String packageName = intent.getStringExtra(VrConstants.ActionExtras.PACKAGE_NAME);
        final String id = intent.getStringExtra(VrConstants.ActionExtras.TTS_ID);

        if (VrAidlServiceManager.getInstance().isVrShowing()) {
            //语音弹窗显示或其他权限不足时直接回调ERROR
            Intent replyIntent = new Intent(VrConstants.Actions.TTS_SPEAKING_ERROR);
            replyIntent.putExtra(VrConstants.ActionExtras.PACKAGE_NAME, packageName);
            replyIntent.putExtra(VrConstants.ActionExtras.TTS_ID, id);
            replyIntent.putExtra(VrConstants.ActionExtras.ERROR_CODE, -1);
            intent.addFlags(0x01000000);
            context.sendBroadcast(replyIntent);
            return;
        }
        XmTtsManager.getInstance().startSpeakingByThird(content, new OnTtsListener() {
            @Override
            public void onCompleted() {
                Intent replyIntent = new Intent(VrConstants.Actions.TTS_SPEAKING_COMPLETED);
                replyIntent.putExtra(VrConstants.ActionExtras.PACKAGE_NAME, packageName);
                replyIntent.putExtra(VrConstants.ActionExtras.TTS_ID, id);
                intent.addFlags(0x01000000);
                context.sendBroadcast(replyIntent);
            }

            @Override
            public void onBegin() {
                Intent replyIntent = new Intent(VrConstants.Actions.TTS_SPEAKING_BEGIN);
                replyIntent.putExtra(VrConstants.ActionExtras.PACKAGE_NAME, packageName);
                replyIntent.putExtra(VrConstants.ActionExtras.TTS_ID, id);
                intent.addFlags(0x01000000);
                context.sendBroadcast(replyIntent);
            }

            @Override
            public void onError(int code) {
                Intent replyIntent = new Intent(VrConstants.Actions.TTS_SPEAKING_ERROR);
                replyIntent.putExtra(VrConstants.ActionExtras.PACKAGE_NAME, packageName);
                replyIntent.putExtra(VrConstants.ActionExtras.TTS_ID, id);
                replyIntent.putExtra(VrConstants.ActionExtras.ERROR_CODE, code);
                intent.addFlags(0x01000000);
                context.sendBroadcast(replyIntent);
            }
        });
    }

    private void startSpeak(final Context context, final Intent intent) {
        String content = intent.getStringExtra(VrConstants.ActionExtras.TTS_SPEAKING_CONTENT);
        String id = intent.getStringExtra(VrConstants.ActionExtras.TTS_ID);
        int speed = intent.getIntExtra(VrConstants.ActionExtras.TTS_SPEAKING_SPEED, -1);
        final String packageName = intent.getStringExtra(VrConstants.ActionExtras.PACKAGE_NAME);
        if (speed != -1) {
            if (XmIatManager.getInstance().isIatTalk()) {
                return;
            }
            startSpeakWithSpeed(context, intent, content, speed, packageName, id);
        } else {
            if (VrAidlServiceManager.getInstance().isVrShowing() || XmIatManager.getInstance().isIatTalk()) {
                return;
            }
            startSpeakNormal(context, intent, content, packageName, id);
        }
    }

    private void startSpeakNormal(final Context context, final Intent intent, final String content, final String packageName, final String id) {
        XmTtsManager.getInstance().startSpeakingByXmApp(content, new OnTtsListener() {
            @Override
            public void onCompleted() {
                Intent replyIntent = new Intent(VrConstants.Actions.TTS_SPEAKING_COMPLETED);
                replyIntent.putExtra(VrConstants.ActionExtras.PACKAGE_NAME, packageName);
                replyIntent.putExtra(VrConstants.ActionExtras.TTS_ID, id);
                intent.addFlags(0x01000000);
                context.sendBroadcast(replyIntent);
            }

            @Override
            public void onBegin() {
                Intent replyIntent = new Intent(VrConstants.Actions.TTS_SPEAKING_BEGIN);
                replyIntent.putExtra(VrConstants.ActionExtras.PACKAGE_NAME, packageName);
                replyIntent.putExtra(VrConstants.ActionExtras.TTS_ID, id);
                intent.addFlags(0x01000000);
                context.sendBroadcast(replyIntent);
            }

            @Override
            public void onError(int code) {
                Intent replyIntent = new Intent(VrConstants.Actions.TTS_SPEAKING_ERROR);
                replyIntent.putExtra(VrConstants.ActionExtras.PACKAGE_NAME, packageName);
                replyIntent.putExtra(VrConstants.ActionExtras.TTS_ID, id);
                replyIntent.putExtra(VrConstants.ActionExtras.ERROR_CODE, code);
                intent.addFlags(0x01000000);
                context.sendBroadcast(replyIntent);
            }
        });
    }

    private void startSpeakWithSpeed(final Context context, final Intent intent, String content, int speed, final String packageName, final String id) {
        XmTtsManager.getInstance().startSpeakingByNavi(content, speed, new OnTtsListener() {
            @Override
            public void onCompleted() {
                Intent replyIntent = new Intent(VrConstants.Actions.TTS_SPEAKING_COMPLETED);
                replyIntent.putExtra(VrConstants.ActionExtras.PACKAGE_NAME, packageName);
                replyIntent.putExtra(VrConstants.ActionExtras.TTS_ID, id);
                intent.addFlags(0x01000000);
                context.sendBroadcast(replyIntent);
            }

            @Override
            public void onBegin() {
                Intent replyIntent = new Intent(VrConstants.Actions.TTS_SPEAKING_BEGIN);
                replyIntent.putExtra(VrConstants.ActionExtras.PACKAGE_NAME, packageName);
                replyIntent.putExtra(VrConstants.ActionExtras.TTS_ID, id);
                intent.addFlags(0x01000000);
                context.sendBroadcast(replyIntent);
            }

            @Override
            public void onError(int code) {
                Intent replyIntent = new Intent(VrConstants.Actions.TTS_SPEAKING_ERROR);
                replyIntent.putExtra(VrConstants.ActionExtras.PACKAGE_NAME, packageName);
                replyIntent.putExtra(VrConstants.ActionExtras.TTS_ID, id);
                replyIntent.putExtra(VrConstants.ActionExtras.ERROR_CODE, code);
                intent.addFlags(0x01000000);
                context.sendBroadcast(replyIntent);
            }
        });
    }

}
