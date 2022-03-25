package com.xiaoma.assistant.model.parser;

/**
 * Created by qiuboxiang on 2019/2/22 14:57
 * Desc:
 */
public class ProgramSlots {

    /**
     * chapter : {"direct":"-","offset":"1","ref":"MAX","type":"SPOT"}
     * program : 罗辑思维
     */

    public ChapterBean chapter;
    public String presenter;
    public String famous;
    public String mediaSource;
    public String program;
    public String tags;
    public String insType;
    public String index;

    public class ChapterBean {
        /**
         * direct : -
         * offset : 1
         * ref : MAX
         * type : SPOT
         */

        public String direct;
        public String offset;
        public String ref;
        public String type;
    }
}
