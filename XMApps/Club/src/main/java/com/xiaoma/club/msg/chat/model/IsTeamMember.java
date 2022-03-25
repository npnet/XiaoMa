package com.xiaoma.club.msg.chat.model;

/**
 * 简介:
 *
 * @author lingyan
 */
public class IsTeamMember {
    private IsMember data;
    private int resultCode;
    private String resultMessage;

    public IsMember getData() {
        return data;
    }

    public void setData(IsMember data) {
        this.data = data;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public  class IsMember{
        private boolean isInCarTeam;

        public boolean isInCarTeam() {
            return isInCarTeam;
        }

        public void setInCarTeam(boolean inCarTeam) {
            isInCarTeam = inCarTeam;
        }
    }

}
