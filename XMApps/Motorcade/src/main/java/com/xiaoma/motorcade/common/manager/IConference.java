package com.xiaoma.motorcade.common.manager;

import com.hyphenate.EMConferenceListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMConference;
import com.hyphenate.chat.EMConferenceMember;
import com.hyphenate.chat.EMConferenceStream;
import com.hyphenate.chat.EMStreamParam;

import java.util.List;
import java.util.Map;

/**
 * Created by ZYao.
 * Date ：2019/1/29 0029
 */
public interface IConference {

    void addConferenceListener(EMConferenceListener listener);

    void removeConferenceListener(EMConferenceListener listener);

    void getConferenceInfo(String conferenceId, String pass,EMValueCallBack<EMConference> callback);

    void createAndJoinConference(final EMValueCallBack<EMConference> callback);

    void joinConference(final String confId, final EMValueCallBack<EMConference> callback);

    void inviteUserToJoinConference(final String confId, final String username, final String extension, final EMValueCallBack callback);

    void destroyConference(final EMValueCallBack callback);

    void exitConference(final EMValueCallBack callback);

    void publish(EMStreamParam param, final EMValueCallBack<String> callback);

    void unpublish(String pubStreamId, final EMValueCallBack<String> callback);

    void subscribe(final EMConferenceStream stream, final EMValueCallBack<String> callback);

    void updateSubscribe(final EMConferenceStream stream, final EMValueCallBack<String> callback);

    void unsubscribe(final EMConferenceStream stream, final EMValueCallBack<String> callback);

    void startMonitorSpeaker(int interval);

    void stopMonitorSpeaker();

    void setConferenceMode(EMConferenceListener.ConferenceMode mode);

    void closeVoiceTransfer();

    void openVoiceTransfer();

    void enableStatistics(boolean enable);

    List<EMConferenceMember> getConferenceMemberList();

    Map<String, EMConferenceStream> getAvailableStreamMap();

    Map<String, EMConferenceStream> getSubscribedStreamMap();


}
