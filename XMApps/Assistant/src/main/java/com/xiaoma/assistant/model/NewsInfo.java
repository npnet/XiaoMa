package com.xiaoma.assistant.model;

import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/20
 * Desc:
 */
public class NewsInfo {

    /**
     * data : {"allPages":2069804,"currentPage":2,"allNum":2069804,"maxResult":1,"contentlist":[{"link":"http://stock.jrj.com.cn/2018/10/15121025203832.shtml","channelName":"财经最新","id":"3933a9ac469672bd05d6368fe0944103","source":"金融界","title":"午间公告：福田投控拟入股腾邦国际；康达新材控股股东、实控人及其一致行动人拟变更","pubDate":"2018-10-15 14:38:42","content":"康达新材控股股东、实控人及其一致行动人拟变更，今日起停牌","havePic":true,"channelId":"5572a109b3cdc86cf39001e0","desc":"10月15日午间公告精选：腾邦国际停牌系福田投控拟通过适当方式持有腾邦国际股份，并探讨成为第一大股东的可能性。康达新材控股股东、实控人及其一致行动人拟变更，今日起停牌；银泰资源前三季预盈约4.6亿元-4.9亿元，同比增约93.18%-105.78%；东方财富预计前三季盈利约7.8亿元\u20148.3亿元，同比增逾七成；利尔化学全资子公司广安利尔年产1000吨丙炔氟草胺原药生产线及配套设施建设项目已投产。","imageurls":[{"width":0,"height":0,"url":"http://chart.jrjimg.cn/pngdata/kpic/pic430/600577.png"},{"width":0,"height":0,"url":"http://img.jrjimg.cn/2018/10/20181015052731096.jpg"}]}]}
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
        /**
         * allPages : 2069804
         * currentPage : 2
         * allNum : 2069804
         * maxResult : 1
         * contentlist : [{"link":"http://stock.jrj.com.cn/2018/10/15121025203832.shtml","channelName":"财经最新","id":"3933a9ac469672bd05d6368fe0944103","source":"金融界","title":"午间公告：福田投控拟入股腾邦国际；康达新材控股股东、实控人及其一致行动人拟变更","pubDate":"2018-10-15 14:38:42","content":"康达新材控股股东、实控人及其一致行动人拟变更，今日起停牌","havePic":true,"channelId":"5572a109b3cdc86cf39001e0","desc":"10月15日午间公告精选：腾邦国际停牌系福田投控拟通过适当方式持有腾邦国际股份，并探讨成为第一大股东的可能性。康达新材控股股东、实控人及其一致行动人拟变更，今日起停牌；银泰资源前三季预盈约4.6亿元-4.9亿元，同比增约93.18%-105.78%；东方财富预计前三季盈利约7.8亿元\u20148.3亿元，同比增逾七成；利尔化学全资子公司广安利尔年产1000吨丙炔氟草胺原药生产线及配套设施建设项目已投产。","imageurls":[{"width":0,"height":0,"url":"http://chart.jrjimg.cn/pngdata/kpic/pic430/600577.png"},{"width":0,"height":0,"url":"http://img.jrjimg.cn/2018/10/20181015052731096.jpg"}]}]
         */

        private int allPages;
        private int currentPage;
        private int allNum;
        private int maxResult;
        private List<ContentlistBean> contentlist;

        public int getAllPages() {
            return allPages;
        }

        public void setAllPages(int allPages) {
            this.allPages = allPages;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getAllNum() {
            return allNum;
        }

        public void setAllNum(int allNum) {
            this.allNum = allNum;
        }

        public int getMaxResult() {
            return maxResult;
        }

        public void setMaxResult(int maxResult) {
            this.maxResult = maxResult;
        }

        public List<ContentlistBean> getContentlist() {
            return contentlist;
        }

        public void setContentlist(List<ContentlistBean> contentlist) {
            this.contentlist = contentlist;
        }

        public static class ContentlistBean {
            /**
             * link : http://stock.jrj.com.cn/2018/10/15121025203832.shtml
             * channelName : 财经最新
             * id : 3933a9ac469672bd05d6368fe0944103
             * source : 金融界
             * title : 午间公告：福田投控拟入股腾邦国际；康达新材控股股东、实控人及其一致行动人拟变更
             * pubDate : 2018-10-15 14:38:42
             * content : 康达新材控股股东、实控人及其一致行动人拟变更，今日起停牌
             * havePic : true
             * channelId : 5572a109b3cdc86cf39001e0
             * desc : 10月15日午间公告精选：腾邦国际停牌系福田投控拟通过适当方式持有腾邦国际股份，并探讨成为第一大股东的可能性。康达新材控股股东、实控人及其一致行动人拟变更，今日起停牌；银泰资源前三季预盈约4.6亿元-4.9亿元，同比增约93.18%-105.78%；东方财富预计前三季盈利约7.8亿元—8.3亿元，同比增逾七成；利尔化学全资子公司广安利尔年产1000吨丙炔氟草胺原药生产线及配套设施建设项目已投产。
             * imageurls : [{"width":0,"height":0,"url":"http://chart.jrjimg.cn/pngdata/kpic/pic430/600577.png"},{"width":0,"height":0,"url":"http://img.jrjimg.cn/2018/10/20181015052731096.jpg"}]
             */

            private String link;
            private String channelName;
            private String id;
            private String source;
            private String title;
            private String pubDate;
            private String content;
            private boolean havePic;
            private String channelId;
            private String desc;
            private List<ImageurlsBean> imageurls;

            public String getLink() {
                return link;
            }

            public void setLink(String link) {
                this.link = link;
            }

            public String getChannelName() {
                return channelName;
            }

            public void setChannelName(String channelName) {
                this.channelName = channelName;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getPubDate() {
                return pubDate;
            }

            public void setPubDate(String pubDate) {
                this.pubDate = pubDate;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public boolean isHavePic() {
                return havePic;
            }

            public void setHavePic(boolean havePic) {
                this.havePic = havePic;
            }

            public String getChannelId() {
                return channelId;
            }

            public void setChannelId(String channelId) {
                this.channelId = channelId;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public List<ImageurlsBean> getImageurls() {
                return imageurls;
            }

            public void setImageurls(List<ImageurlsBean> imageurls) {
                this.imageurls = imageurls;
            }

            public static class ImageurlsBean {
                /**
                 * width : 0
                 * height : 0
                 * url : http://chart.jrjimg.cn/pngdata/kpic/pic430/600577.png
                 */

                private int width;
                private int height;
                private String url;

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }
            }
        }
    }
}
