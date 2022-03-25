package com.xiaoma.assistant.processor;

import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.assistant.manager.IAssistantManager;
import com.xiaoma.assistant.model.parser.CmdSlotsBean;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.utils.GsonHelper;


/**
 * Created by blusehuang on 2017/6/11.
 */

class HandleAudioProcessor extends BaseLocalVoiceProcessor {

    public HandleAudioProcessor(Context context, IAssistantManager  assistantManager) {
        super(context, assistantManager);
    }

    @Override
    public boolean process(String voiceContent, String json, LxParseResult parseResult) {
        return handleCurrentAudio(voiceContent, parseResult);
    }

    private boolean handleCurrentAudio(String voiceContent, LxParseResult parseResult) {
        if (TextUtils.isEmpty(voiceContent)) {
            return false;
        }

//        if (parseResult.isStartMusicAction()) {
//            if (MusicUtils.isMusicAppInstalled(context)) {
//                MusicUtils.start(context);
//                closeVoicePopup();
//            } else {
//                addToUiAndSpeakAndClose(context.getString(R.string.app_is_not_install));
//            }
//            return true;
//        }

        if (parseResult.isStartMusicAction() &&("播放歌曲".equals(voiceContent) || "播放音乐".equals(voiceContent) || "放歌".equals(voiceContent))){
            //主机那边修改了提升语音助手音源等级的机制 需要添加延迟，以免酷我音乐无法使用开始播放指令
            /*closeVoicePopup(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    FunctionCtrlManager.getInstance().playerStart();
                }
            });
            LocalControlManager.getInstance().setSceneActionAndDuration(26,0);*/
            return true;
        }

        CmdSlotsBean cmdSlotsBean = null;
        if (parseResult.isCmdAciton() && !TextUtils.isEmpty(parseResult.getSlots())) {
            cmdSlotsBean = GsonHelper.fromJson(parseResult.getSlots(), CmdSlotsBean.class);
        }
        if (cmdSlotsBean == null || (!cmdSlotsBean.isPlayModeCmd() && !cmdSlotsBean.isPlayControlCmd()) || TextUtils.isEmpty(cmdSlotsBean.getName())) {
            return false;
        }

//        if (hitStartMusic(cmdSlotsBean)) {
//            MusicUtils.start(context);
//            closeVoicePopup();
//            return true;
//        }
//        else if (hitCloseToast(cmdSlotsBean)) {
//            final XmBaseAudio currentAudio = XmAudioManager.getInstance().getCurrentAudio();
//            if (AppUtils.isAppInForeground(context, com.xiaoma.base.constant.Constants.PACKAGENAME.MUSIC)) {
//                if (currentAudio != null && currentAudio instanceof XmKuwoAudio) {
//                    closeVoicePopup();
//                    Intent intent = new Intent(Intent.ACTION_MAIN, null);
//                    intent.addCategory(Intent.CATEGORY_HOME);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent);
//                } else {
//                    launcherIvwManager.speakUnderstand();
//                }
//            } else {
//                launcherIvwManager.speakUnderstand();
//            }
//
//            return true;
//        }
        else if (hitPlayAudio(cmdSlotsBean)) {//播放
            //主机那边修改了提升语音助手音源等级的机制 需要添加延迟，以免酷我音乐无法使用开始播放指令
//            closeVoicePopup(new DialogInterface.OnDismissListener() {
//                @Override
//                public void onDismiss(DialogInterface dialog) {
//                    FunctionCtrlManager.getInstance().playerStart();
//                }
//            });
//            LocalControlManager.getInstance().setSceneActionAndDuration(26,0);
            return true;
        } else if (hitPauseAudio(cmdSlotsBean)) { //暂停
//            FunctionCtrlManager.getInstance().playerStop();
//            addToUiAndSpeakAndClose(getString(R.string.already_pause_kuwo));
//            LocalControlManager.getInstance().setSceneActionAndDuration(26,getString(R.string.already_pause_kuwo).length());
            return true;
        } else if (hitAudioPrevious(cmdSlotsBean)) { //上一首
//            closeVoicePopupDelay(new DialogInterface.OnDismissListener() {
//                @Override
//                public void onDismiss(DialogInterface dialog) {
//                    FunctionCtrlManager.getInstance().playerPrevious();
//                }
//            });
//            LocalControlManager.getInstance().setSceneActionAndDuration(16,0);
            return true;
        } else if (hitAudioNext(cmdSlotsBean)) {//下一首
//            closeVoicePopupDelay(new DialogInterface.OnDismissListener() {
//                @Override
//                public void onDismiss(DialogInterface dialog) {
//                    FunctionCtrlManager.getInstance().playerNext();
//                }
//            });
//            LocalControlManager.getInstance().setSceneActionAndDuration(16,0);
            return true;
        } else if (hitAudioOrder(cmdSlotsBean)) { //顺序循环
//            FunctionCtrlManager.getInstance().setPlayerOrder();
//            speakContent(getString(R.string.is_order_play), new WrapperSynthesizerListener() {
//                @Override
//                public void onCompleted() {
//                    closeVoicePopup();
//                }
//            });
//            LocalControlManager.getInstance().setSceneActionAndDuration(26,0);
            return true;
        } else if (hitAudioSingle(cmdSlotsBean)) { //单曲循环
//            FunctionCtrlManager.getInstance().setPlayerSingle();
//            speakContent(getString(R.string.is_singer_play), new WrapperSynthesizerListener() {
//                @Override
//                public void onCompleted() {
//                    closeVoicePopup();
//                }
//            });
//            LocalControlManager.getInstance().setSceneActionAndDuration(26,0);
            return true;
        } else if (hitAudioRandom(cmdSlotsBean)) { //随机播放
//            FunctionCtrlManager.getInstance().setPlayerRandom();
//            speakContent(getString(R.string.is_random_play), new WrapperSynthesizerListener() {
//                @Override
//                public void onCompleted() {
//                    closeVoicePopup();
//                }
//            });
//            LocalControlManager.getInstance().setSceneActionAndDuration(26,0);
            return true;
        }
//        else if (hitAudioMode(cmdSlotsBean)) {
//            final XmBaseAudio currentAudio = XmAudioManager.getInstance().getCurrentAudio();
//            if (currentAudio != null && currentAudio instanceof XmKuwoAudio) {
//                Random rand = new Random();
//                int randNum = rand.nextInt(3);
//                MusicUtils.setPlayMode(context, randNum);
//                changeMode();
//            } else {
//                launcherIvwManager.speakUnderstand();
//            }
//
//            return true;
//        }
        return false;
    }

    private void changeMode() {
//        addToLeftTextConversation(context.getString(R.string.the_mode_have_changed));
//        launcherIvwManager.speakContent(context.getString(R.string.the_mode_have_changed), new WrapperSynthesizerListener() {
//            @Override
//            public void onCompleted() {
//                closeVoicePopup();
//            }
//        });
    }


    private boolean hitAudioMode(CmdSlotsBean cmdSlotsBean) {
        return cmdSlotsBean.isPlayModeCmd() && "切换播放模式".equals(cmdSlotsBean.getName());
    }

    private boolean hitAudioRandom(CmdSlotsBean cmdSlotsBean) {
        return cmdSlotsBean.isPlayModeCmd() && "随机播放".equals(cmdSlotsBean.getName());
    }

    private boolean hitAudioSingle(CmdSlotsBean cmdSlotsBean) {
        return cmdSlotsBean.isPlayModeCmd() && "单曲循环".equals(cmdSlotsBean.getName());
    }

    private boolean hitAudioOrder(CmdSlotsBean cmdSlotsBean) {
        return cmdSlotsBean.isPlayModeCmd() && ("顺序循环".equals(cmdSlotsBean.getName()) ||
                "顺序播放".equals(cmdSlotsBean.getName()) ||
                "循环播放".equals(cmdSlotsBean.getName()));
    }

    private boolean hitPlayAudio(CmdSlotsBean cmdSlotsBean) {
        return cmdSlotsBean.isPlayControlCmd() && "播放".equals(cmdSlotsBean.getName());
    }

    private boolean hitPauseAudio(CmdSlotsBean cmdSlotsBean) {
        return cmdSlotsBean.isPlayControlCmd() && ("暂停".equals(cmdSlotsBean.getName()) || "停止".equals(cmdSlotsBean.getName()));
    }

    private boolean hitPauseHostAudio(CmdSlotsBean cmdSlotsBean) {
        return cmdSlotsBean.isPlayControlCmd() && ("暂停".equals(cmdSlotsBean.getName()));
    }

    private boolean hitStopHostAudio(CmdSlotsBean cmdSlotsBean) {
        return cmdSlotsBean.isPlayControlCmd() && "停止".equals(cmdSlotsBean.getName());
    }

    private boolean hitAudioPrevious(CmdSlotsBean cmdSlotsBean) {
        return cmdSlotsBean.isPlayControlCmd() && ("上一首".equals(cmdSlotsBean.getName()) || "上一频道".equals(cmdSlotsBean.getName()));
    }

    private boolean hitAudioNext(CmdSlotsBean cmdSlotsBean) {
        return cmdSlotsBean.isPlayControlCmd() && ("下一首".equals(cmdSlotsBean.getName()) || "下一频道".equals(cmdSlotsBean.getName()));
    }

    private boolean hitStartMusic(CmdSlotsBean cmdSlotsBean) {
        return cmdSlotsBean.isPlayControlCmd() && cmdSlotsBean.getName().contains("听歌");
    }

    private boolean hitCloseToast(CmdSlotsBean cmdSlotsBean) {
        return cmdSlotsBean.isPlayModeCmd() && "后台播放".equals(cmdSlotsBean.getName());
    }


}
