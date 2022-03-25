package com.xiaoma.xting.practice.manager;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.xting.practice.manager
 *  @file_name:      XtingSkillManager
 *  @author:         Rookie
 *  @create_time:    2019/6/20 20:01
 *  @description：   TODO             */

import android.content.Context;

import com.xiaoma.model.pratice.PlayRadioBean;
import com.xiaoma.model.pratice.VrPracticeConstants;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.skill.SkillManager;
import com.xiaoma.vr.skill.client.SkillCallback;
import com.xiaoma.vr.skill.client.SkillDispatcher;
import com.xiaoma.vr.skill.client.SkillHandler;
import com.xiaoma.vr.skill.logic.ExecResult;
import com.xiaoma.vr.skill.model.Skill;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerAction;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.loadmore.IFetchListener;
import com.xiaoma.xting.launcher.LocalFMOperateManager;
import com.xiaoma.xting.sdk.bean.XMPlayableModel;

public class XtingSkillManager {

    private Context mContext;

    private XtingSkillManager() {

    }

    public static XtingSkillManager getInstance() {
        return XtingSkillManagerHolder.instance;
    }

    private static class XtingSkillManagerHolder {
        static final XtingSkillManager instance = new XtingSkillManager();
    }

    public void init(final Context context) {
        this.mContext = context;
        SkillManager.getInstance().init(context);
        SkillDispatcher.getInstance().addSkillHandler(new SkillHandler() {
            @Override
            public boolean onSkill(String command, Skill skill, SkillCallback callback) {

                if (VrPracticeConstants.SKILL_RADIO.equals(skill.getSkillDesc())) {
                    //播放在线或者本地电台
                    PlayRadioBean playRadioBean = GsonHelper.fromJson(skill.getExtra(), PlayRadioBean.class);
                    if (playRadioBean != null) {
                        if (playRadioBean.isOnline()) {
                            playHimalayan(playRadioBean.getKind(), playRadioBean.getAlbumOriginId(), playRadioBean.getOriginId());
                        } else {
                            //TODO:本地电台 id即频率值
                            playYQRadio((int) playRadioBean.getId());
                        }
                    }
                    ExecResult execResult = new ExecResult("play radio", VrPracticeConstants.SKILL_SUCCESS);
                    callback.onExec(execResult);
                    return true;
                }

                return false;
            }
        });
    }

    private void playYQRadio(int channelValue) {
        PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.RADIO_YQ);
        LocalFMOperateManager.newSingleton().playChannel(channelValue);
    }

    public void playHimalayan(String kind, long albumId, long programId) {
        PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.HIMALAYAN);
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setType(PlayerSourceType.HIMALAYAN);
        playerInfo.setAction(PlayerAction.PLAY_LIST);
        playerInfo.setAlbumId(albumId);
        playerInfo.setProgramId(programId);
        if (XMPlayableModel.KIND_TRACK.equals(kind)) {
            playerInfo.setSourceSubType(PlayerSourceSubType.TRACK);
        } else {
            playerInfo.setSourceSubType(PlayerSourceSubType.RADIO);
        }

        PlayerSourceFacade.newSingleton().getPlayerFetch().fetch(playerInfo, new IFetchListener() {
            @Override
            public void onLoading() {
                KLog.d("XtingSkillManager onLoading");
            }

            @Override
            public void onSuccess(Object t) {
                KLog.d("XtingSkillManager onSuccess");
            }

            @Override
            public void onFail() {
                KLog.d("XtingSkillManager onFail");
            }

            @Override
            public void onError(int code, String msg) {
                KLog.d("XtingSkillManager onError: code_" + code + " msg_" + msg);
                if (mContext != null) {
                    XMToast.toastException(mContext, mContext.getString(R.string.play_fail));
                }
            }
        });
    }

}
