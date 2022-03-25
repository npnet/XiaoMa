package com.xiaoma.assistant.practice.manager;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.assistant.practice.manager
 *  @file_name:      AssistantSkillManager
 *  @author:         Rookie
 *  @create_time:    2019/6/20 17:28
 *  @description：   TODO             */

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserDate;
import com.xiaoma.assistant.model.parser.ParserLocation;
import com.xiaoma.assistant.model.parser.RadioSlots;
import com.xiaoma.assistant.processor.AssistantProcessorChain;
import com.xiaoma.assistant.processor.SemanticByNetworkProcessor;
import com.xiaoma.assistant.scenarios.IatNewsScenario;
import com.xiaoma.assistant.scenarios.IatWeatherScenario;
import com.xiaoma.assistant.scenarios.ScenarioDispatcher;
import com.xiaoma.assistant.utils.CommonUtils;
import com.xiaoma.model.pratice.NewsChannelBean;
import com.xiaoma.model.pratice.VrPracticeConstants;
import com.xiaoma.ui.UIUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.skill.client.SkillCallback;
import com.xiaoma.vr.skill.client.SkillDispatcher;
import com.xiaoma.vr.skill.client.SkillHandler;
import com.xiaoma.vr.skill.logic.ExecResult;
import com.xiaoma.vr.skill.model.Skill;
import com.xiaoma.vr.tts.EventTtsManager;

public class AssistantSkillManager {

    private MediaPlayer mediaPlayer;

    private AssistantSkillManager() {

    }

    public static AssistantSkillManager getInstance() {
        return AssistantSkillManagerHolder.instance;
    }

    private static class AssistantSkillManagerHolder {
        static final AssistantSkillManager instance = new AssistantSkillManager();
    }

    public void init(Context context) {
        SkillDispatcher.getInstance().addSkillHandler(new SkillHandler() {
            @Override
            public boolean onSkill(String command, Skill skill, SkillCallback callback) {
                String skillDesc = skill.getSkillDesc();
                ExecResult execResult = new ExecResult("", "");
                String content = skill.getExtra();
                if (VrPracticeConstants.SKILL_TTS.equals(skillDesc)) {
                    //tts播报
                    UIUtils.runOnMain(new Runnable() {
                        @Override
                        public void run() {
                            ttsSome(callback, execResult, content, context);
                        }
                    });
                    return true;
                } else if (VrPracticeConstants.SKILL_WEATHER.equals(skillDesc)) {
                    //播报天气 content为城市名
                    UIUtils.runOnMain(new Runnable() {
                        @Override
                        public void run() {
                            AssistantProcessorChain.getInstance().getSemanticByNetworkProcessor().setCurrentScenario(ScenarioDispatcher.getInstance().getIatWeatherScenario(context));
                            queryWeather(content, execResult, callback, context);
                        }
                    });
                    return true;
                } else if (VrPracticeConstants.SKILL_RECORD.equals(skillDesc)) {
                    UIUtils.runOnMain(new Runnable() {
                        @Override
                        public void run() {
                            playRecord(content, callback, execResult, context);
                        }
                    });
                    return true;
                } else if (VrPracticeConstants.SKILL_NEWS.equals(skillDesc)) {
                    //播放新闻 content为新闻类型
                    UIUtils.runOnMain(new Runnable() {
                        @Override
                        public void run() {
                            AssistantProcessorChain.getInstance().getSemanticByNetworkProcessor().setCurrentScenario(ScenarioDispatcher.getInstance().getIatNewsScenario(context));
                            NewsChannelBean channelBean = GsonHelper.fromJson(content, NewsChannelBean.class);
                            queryNews(channelBean.getName(), execResult, callback, context);
                        }
                    });
                    return true;
                }
                return false;
            }
        });
    }

    private void queryWeather(String city, ExecResult execResult, SkillCallback callback, Context context) {
        IatWeatherScenario.Slots slots = new IatWeatherScenario.Slots();
        ParserDate date = new ParserDate();
        date.setDate(IatWeatherScenario.CURRENT_DAY);
        slots.setDatetime(date);
        ParserLocation location = new ParserLocation();
        location.setCityAddr(city);
        slots.setLocation(location);
        String slotsString = GsonHelper.toJson(slots);
        LxParseResult parseResult = new LxParseResult(slotsString);

        showAssistantDialog();
        IatWeatherScenario iatWeatherScenario = ScenarioDispatcher.getInstance().getIatWeatherScenario(context);
        iatWeatherScenario.setSkillData(execResult, callback);
        iatWeatherScenario.onParser("", parseResult, CommonUtils.getLocation(), 0);
    }

    private void queryNews(String newsType, ExecResult execResult, SkillCallback callback, Context context) {
        RadioSlots slots = new RadioSlots();
        slots.category = newsType;
        String slotsString = GsonHelper.toJson(slots);
        LxParseResult parseResult = new LxParseResult(slotsString);

        showAssistantDialog();
        IatNewsScenario iatNewsScenario = ScenarioDispatcher.getInstance().getIatNewsScenario(context);
        iatNewsScenario.setSkillData(execResult, callback);
        iatNewsScenario.onParser("", parseResult, CommonUtils.getLocation(), 0);
    }

    private void showAssistantDialog() {
        if (!AssistantManager.getInstance().isShowing()) {
            AssistantManager.getInstance().show(false);
        }
    }

    private void playRecord(String path, SkillCallback callback, ExecResult execResult, Context context) {

        AssistantManager.getInstance().addFeedBackConversation("为你播报一段录音");
        AssistantManager.getInstance().speakContent("为你播报一段录音", new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                super.onCompleted();
                //播放录音
//                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer = MediaPlayer.create(context, Uri.parse(path));
                SkillAudioFocusManager.getInstance().init(context);
                if (mediaPlayer == null) {
                    KLog.d("assistant_skill mediaPlayer==null");
                    execResult.setResult(VrPracticeConstants.SKILL_SUCCESS);
                    callback.onExec(execResult);
                    SkillAudioFocusManager.getInstance().abandonAudioFocus();
                    return;
                }
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        KLog.d("assistant_skill onCompletion ");
                        releaseMediaPlayer();
                        execResult.setResult(VrPracticeConstants.SKILL_SUCCESS);
                        callback.onExec(execResult);
                        EventTtsManager.getInstance().stopSpeaking();
                        SkillAudioFocusManager.getInstance().abandonAudioFocus();
                    }
                });
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        KLog.d("assistant_skill playrecord what: " + what + " extra:" + extra);
                        releaseMediaPlayer();
                        execResult.setResult(VrPracticeConstants.SKILL_SUCCESS);
                        callback.onExec(execResult);
                        EventTtsManager.getInstance().stopSpeaking();
                        SkillAudioFocusManager.getInstance().abandonAudioFocus();
                        return true;
                    }
                });
                try {
//                    File tempFile = new File(path);
//                    FileInputStream fis = new FileInputStream(tempFile);
//                    mediaPlayer.setDataSource(fis.getFD());
//                    mediaPlayer.prepare();
                    SkillAudioFocusManager.getInstance().requestAudioFocus();
                    if (SkillAudioFocusManager.getInstance().hasAudioFocus()) {
                        KLog.d("assistant_skill playrecord hasAudioFocus true start ");
                        mediaPlayer.start();
                    } else {
                        KLog.d("assistant_skill playrecord hasAudioFocus false ");
                        execResult.setResult(VrPracticeConstants.SKILL_SUCCESS);
                        callback.onExec(execResult);
                    }
                } catch (Exception e) {
                    KLog.d("assistant_skill playrecord Exception" + e.getMessage());
                    e.printStackTrace();
                    execResult.setResult(VrPracticeConstants.SKILL_SUCCESS);
                    callback.onExec(execResult);
                    EventTtsManager.getInstance().stopSpeaking();
                    SkillAudioFocusManager.getInstance().abandonAudioFocus();
                }
            }

            @Override
            public void onError(int code) {
                KLog.d("assistant_skill playrecord onError: " + code);
                execResult.setResult(VrPracticeConstants.SKILL_SUCCESS);
                callback.onExec(execResult);
                EventTtsManager.getInstance().stopSpeaking();
                SkillAudioFocusManager.getInstance().abandonAudioFocus();
            }
        });
    }

    private void ttsSome(SkillCallback callback, ExecResult execResult, String content, Context context) {

        AssistantManager.getInstance().addFeedBackConversation(content);
        AssistantManager.getInstance().speakContent(content, new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                super.onCompleted();
                execResult.setResult(VrPracticeConstants.SKILL_SUCCESS);
                callback.onExec(execResult);
            }

            @Override
            public void onError(int code) {
                super.onError(code);
                execResult.setResult(VrPracticeConstants.SKILL_SUCCESS);
                callback.onExec(execResult);
            }
        });
    }


    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
