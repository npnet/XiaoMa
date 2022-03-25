package com.xiaoma.motorcade.common.manager;

import com.hyphenate.EMConferenceListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConference;
import com.hyphenate.chat.EMConferenceManager;
import com.hyphenate.chat.EMConferenceMember;
import com.hyphenate.chat.EMConferenceStream;
import com.hyphenate.chat.EMStreamParam;

import java.util.List;
import java.util.Map;

/**
 * Created by ZYao.
 * Date ：2019/1/29 0029
 */
public class ConferenceManager implements IConference {

    private static final EMConferenceManager.EMConferenceType XM_CONFERENCE_TYPE = EMConferenceManager.EMConferenceType.LargeCommunication;

    private static final String XM_CONFERENCE_DEFAULT_PASSWORD = "xiaoma";
    private EMConferenceManager conferenceManager;

    public static ConferenceManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final ConferenceManager instance = new ConferenceManager();
    }

    public ConferenceManager() {
        conferenceManager = EMClient.getInstance().conferenceManager();
    }

    @Override
    public void addConferenceListener(EMConferenceListener listener) {
        conferenceManager.addConferenceListener(listener);
    }

    @Override
    public void removeConferenceListener(EMConferenceListener listener) {
        conferenceManager.addConferenceListener(listener);
    }

    @Override
    public void getConferenceInfo(String conferenceId, String pass, EMValueCallBack<EMConference> callback) {
        conferenceManager.getConferenceInfo(conferenceId, pass, callback);
    }

    @Override
    public void createAndJoinConference(EMValueCallBack<EMConference> callback) {
        conferenceManager.createAndJoinConference(XM_CONFERENCE_TYPE, XM_CONFERENCE_DEFAULT_PASSWORD, callback);
    }

    @Override
    public void joinConference(String confId, EMValueCallBack<EMConference> callback) {
        conferenceManager.joinConference(confId, XM_CONFERENCE_DEFAULT_PASSWORD, callback);
    }

    @Override
    public void inviteUserToJoinConference(String confId, String userName, String extension, EMValueCallBack callback) {
        conferenceManager.inviteUserToJoinConference(confId, XM_CONFERENCE_DEFAULT_PASSWORD, userName, extension, callback);
    }

    @Override
    public void destroyConference(EMValueCallBack callback) {
        conferenceManager.destroyConference(callback);
    }

    @Override
    public void exitConference(EMValueCallBack callback) {
        conferenceManager.exitConference(callback);
    }

    @Override
    public void publish(EMStreamParam param, EMValueCallBack<String> callback) {
        conferenceManager.publish(param, callback);
    }

    @Override
    public void unpublish(String pubStreamId, EMValueCallBack<String> callback) {
        conferenceManager.unpublish(pubStreamId, callback);
    }

    @Override
    public void subscribe(EMConferenceStream stream, EMValueCallBack<String> callback) {
        conferenceManager.subscribe(stream, null, callback);
    }

    @Override
    public void updateSubscribe(EMConferenceStream stream, EMValueCallBack<String> callback) {
        conferenceManager.updateSubscribe(stream, null, callback);
    }

    @Override
    public void unsubscribe(EMConferenceStream stream, EMValueCallBack<String> callback) {
        conferenceManager.unsubscribe(stream, callback);
    }

    @Override
    public void startMonitorSpeaker(int interval) {
        conferenceManager.startMonitorSpeaker(interval);
    }

    @Override
    public void stopMonitorSpeaker() {
        conferenceManager.stopMonitorSpeaker();
    }

    @Override
    public void setConferenceMode(EMConferenceListener.ConferenceMode mode) {
        conferenceManager.setConferenceMode(mode);
    }

    @Override
    public void closeVoiceTransfer() {
        conferenceManager.closeVoiceTransfer();
    }

    @Override
    public void openVoiceTransfer() {
        conferenceManager.openVoiceTransfer();
    }

    @Override
    public void enableStatistics(boolean enable) {
        conferenceManager.enableStatistics(enable);
    }

    @Override
    public List<EMConferenceMember> getConferenceMemberList() {
        return conferenceManager.getConferenceMemberList();
    }

    @Override
    public Map<String, EMConferenceStream> getAvailableStreamMap() {
        return conferenceManager.getAvailableStreamMap();
    }

    @Override
    public Map<String, EMConferenceStream> getSubscribedStreamMap() {
        return conferenceManager.getSubscribedStreamMap();
    }
}
