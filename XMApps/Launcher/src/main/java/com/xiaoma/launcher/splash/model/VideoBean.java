package com.xiaoma.launcher.splash.model;

import java.util.List;

public class VideoBean {


    /**
     * data : {"list":[{"id":11,"date":"2019-12-25","festival":"圣诞节","greetings":"圣诞节快乐","video":"https://www.baidu.com/","video_size":"129","video_time":"60","md5string":"..."},{"id":16,"date":"2019-06-07","festival":"端午节","greetings":"端午节快乐","video":"https://www.baidu.com/","video_size":"129","video_time":"60","md5string":"..."},{"id":19,"date":"2019-10-01","festival":"国庆节","greetings":"国庆节快乐","video":"https://www.baidu.com/","video_size":"129","video_time":"60","md5string":"..."}]}
     * resultCode : 1
     * resultMessage : 操作成功
     */

    private DataBean data;
    private String resultCode;
    private String resultMessage;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public static class DataBean {
        private List<ListBean> list;

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }
    }
}
