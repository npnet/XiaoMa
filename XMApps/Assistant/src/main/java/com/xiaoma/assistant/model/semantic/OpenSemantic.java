package com.xiaoma.assistant.model.semantic;

import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiuboxiang on 2019/7/29 17:12
 * Desc:
 */
@Table("Assistant_OpenSemantic")
public class OpenSemantic {

    /**
     * list : [{"id":11,"channelId":"AA1090","question":"我好热","createDate":1562845102000,"skillId":3,"result":"{\"rc\":0,\"operation\":\"INSTRUCTION\",\"service\":\"airControl\",\"text\":\"我好热\",\"semantic\":{\"slots\":{\"insType\":\"OPEN\",\"device\":null,\"temperature\":null,\"fanSpeed\":null}}}","enableStatus":1},{"id":12,"channelId":"AA1090","question":"关掉空调","createDate":1562845173000,"skillId":4,"result":"{\"rc\":0,\"operation\":\"INSTRUCTION\",\"service\":\"airControl\",\"text\":\"关掉空调\",\"semantic\":{\"slots\":{\"insType\":\"CLOSE\",\"device\":null,\"temperature\":null,\"fanSpeed\":null}}}","enableStatus":1},{"id":13,"channelId":"AA1090","question":"温度调高","createDate":1563335286000,"skillId":5,"result":"{\"rc\":0,\"operation\":\"SET\",\"service\":\"airControl\",\"text\":\"温度调高\",\"semantic\":{\"slots\":{\"insType\":null,\"device\":\"空调\",\"temperature\":\"PLUS\",\"fanSpeed\":null}}}","enableStatus":1},{"id":14,"channelId":"AA1090","question":"我要拍照","createDate":1563336768000,"skillId":22,"result":"{\"rc\":0,\"operation\":\"INSTRUCTION\",\"service\":\"cmd\",\"text\":\"我要拍照\",\"semantic\":{\"slots\":{\"insType\":\"TAKE_PHOTO\",\"name\":null,\"content\":null,\"source\":null,\"target\":null,\"category\":null,\"tag\":null}}}","enableStatus":1}]
     * md5String : 6ebf7671ee4db782982c1b5e9ae2ccb2
     */

    @PrimaryKey(AssignType.BY_MYSELF)
    private long saveTime;
    private String md5String;
    @Mapping(Relation.OneToMany)
    private ArrayList<ListBean> list;

    public long getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }

    public String getMd5String() {
        return md5String;
    }

    public void setMd5String(String md5String) {
        this.md5String = md5String;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(ArrayList<ListBean> list) {
        this.list = list;
    }

    @Table("Assistant_OpenSemantic_ListBean")
    public static class ListBean {
        /**
         * id : 11
         * channelId : AA1090
         * question : 我好热
         * createDate : 1562845102000
         * skillId : 3
         * result : {"rc":0,"operation":"INSTRUCTION","service":"airControl","text":"我好热","semantic":{"slots":{"insType":"OPEN","device":null,"temperature":null,"fanSpeed":null}}}
         * enableStatus : 1
         */

        @PrimaryKey(AssignType.BY_MYSELF)
        private int id;
        private String channelId;
        private String question;
        private long createDate;
        private int skillId;
        private String result;
        private int enableStatus;

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

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public long getCreateDate() {
            return createDate;
        }

        public void setCreateDate(long createDate) {
            this.createDate = createDate;
        }

        public int getSkillId() {
            return skillId;
        }

        public void setSkillId(int skillId) {
            this.skillId = skillId;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public int getEnableStatus() {
            return enableStatus;
        }

        public void setEnableStatus(int enableStatus) {
            this.enableStatus = enableStatus;
        }
    }

}
