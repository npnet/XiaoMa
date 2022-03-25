package com.xiaoma.songname.model;

/**
 * Created by Thomas on 2018/11/7 0007
 */

public class SongNameBean {
    /**
     * songSubject : {"id":1,"channelId":"AA1080","createDate":1555496558139,"modifyDate":1555496558139,"voiceUrl":"http://www.carbuyin.net/by3/userHeader/default/subject1.mwv","subjectNum":1,"subjectPoint":1,"subjectTip":"第三个字是风","subjectAnswer":"大海风"}
     */

    private SongSubjectBean songSubject;

    public SongSubjectBean getSongSubject() {
        return songSubject;
    }

    public void setSongSubject(SongSubjectBean songSubject) {
        this.songSubject = songSubject;
    }

    public static class SongSubjectBean {
        /**
         * id : 1
         * channelId : AA1080
         * createDate : 1555496558139
         * modifyDate : 1555496558139
         * voiceUrl : http://www.carbuyin.net/by3/userHeader/default/subject1.mwv
         * subjectNum : 1
         * subjectPoint : 1
         * subjectTip : 第三个字是风
         * subjectAnswer : 大海风
         */

        private int id;
        private String channelId;
        private long createDate;
        private long modifyDate;
        private String voiceUrl;
        private int subjectNum;
        private int subjectPoint;
        private String subjectTip;
        private String subjectAnswer;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public long getCreateDate() {
            return createDate;
        }

        public void setCreateDate(long createDate) {
            this.createDate = createDate;
        }

        public long getModifyDate() {
            return modifyDate;
        }

        public void setModifyDate(long modifyDate) {
            this.modifyDate = modifyDate;
        }

        public String getVoiceUrl() {
            return voiceUrl;
        }

        public void setVoiceUrl(String voiceUrl) {
            this.voiceUrl = voiceUrl;
        }

        public int getSubjectNum() {
            return subjectNum;
        }

        public void setSubjectNum(int subjectNum) {
            this.subjectNum = subjectNum;
        }

        public int getSubjectPoint() {
            return subjectPoint;
        }

        public void setSubjectPoint(int subjectPoint) {
            this.subjectPoint = subjectPoint;
        }

        public String getSubjectTip() {
            return subjectTip;
        }

        public void setSubjectTip(String subjectTip) {
            this.subjectTip = subjectTip;
        }

        public String getSubjectAnswer() {
            return subjectAnswer;
        }

        public void setSubjectAnswer(String subjectAnswer) {
            this.subjectAnswer = subjectAnswer;
        }
    }
}
