package com.xiaoma.assistant.model;

import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/13
 * Desc：帮助界面
 */
public class HelpBean {

    /**
     * title : 电台
     * example : 如：我想听逻辑思维
     * more : [{"content":"我想听逻辑思维"},{"content":"我想听逻辑思维"},{"content":"我想听逻辑思维"},{"content":"我想听逻辑思维"},{"content":"我想听逻辑思维"},{"content":"我想听逻辑思维"},{"content":"我想听逻辑思维"},{"content":"我想听逻辑思维"}]
     */

    private String title;
    private String example;
    private List<MoreBean> more;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public List<MoreBean> getMore() {
        return more;
    }

    public void setMore(List<MoreBean> more) {
        this.more = more;
    }

    public static class MoreBean {
        /**
         * content : 我想听逻辑思维
         */

        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
