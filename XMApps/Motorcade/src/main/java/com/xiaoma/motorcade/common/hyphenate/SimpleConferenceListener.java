package com.xiaoma.motorcade.common.hyphenate;

import com.hyphenate.EMConferenceListener;
import com.hyphenate.chat.EMConferenceManager;
import com.hyphenate.chat.EMConferenceMember;
import com.hyphenate.chat.EMConferenceStream;
import com.hyphenate.chat.EMStreamStatistics;

import java.util.List;

/**
 * Author: loren
 * Date: 2019/4/15 0015
 */

public class SimpleConferenceListener implements EMConferenceListener{
    @Override
    public void onMemberJoined(EMConferenceMember emConferenceMember) {

    }

    @Override
    public void onMemberExited(EMConferenceMember emConferenceMember) {

    }

    @Override
    public void onStreamAdded(EMConferenceStream emConferenceStream) {

    }

    @Override
    public void onStreamRemoved(EMConferenceStream emConferenceStream) {

    }

    @Override
    public void onStreamUpdate(EMConferenceStream emConferenceStream) {

    }

    @Override
    public void onPassiveLeave(int i, String s) {

    }

    @Override
    public void onConferenceState(ConferenceState conferenceState) {

    }

    @Override
    public void onStreamStatistics(EMStreamStatistics emStreamStatistics) {

    }

    @Override
    public void onStreamSetup(String s) {

    }

    @Override
    public void onSpeakers(List<String> list) {

    }

    @Override
    public void onReceiveInvite(String s, String s1, String s2) {

    }

    @Override
    public void onRoleChanged(EMConferenceManager.EMConferenceRole emConferenceRole) {

    }
}
