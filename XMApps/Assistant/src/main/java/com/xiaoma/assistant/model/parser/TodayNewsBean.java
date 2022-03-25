package com.xiaoma.assistant.model.parser;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @Author ZiXu Huang
 * @Data 2019/3/4
 */
public class TodayNewsBean {

    /**
     * answer : {"text":"这是一条关于要闻的新闻。\u201c建议春节假期延长到15天\u201d，有人不太同意","type":"T"}
     * array_index : 0
     * cid : cida1624662@dx000a0fd81858000000
     * data : {"result":[{"category":"要闻","description":"近日，\u201c春节假期是否该延长\u201d的话题引发网友热议。     据合肥在线报道，全国人大代表 李小莉 建议将春节假期由 现在7天延长到10-15天。","id":"347747073","picUrl":"http://n1.itc.cn/img7/adapt/wb/recom/2019/03/04/155167701884554188_180_180.PNG","title":"\u201c建议春节假期延长到15天\u201d，有人不太同意"},{"category":"要闻","description":"【环球时报-环球网报道 记者白云怡】据阿根廷《号角报》2日报道，该国海岸警卫队1日夜间在本国海域追捕一艘涉嫌\u201c非法捕捞\u201d的中国渔船。海警在追捕3小时后向中","id":"347756086","picUrl":"http://n1.itc.cn/img7/adapt/wb/recom/2019/03/04/155167997519592419_180_180.PNG","title":"阿根廷再向我渔船开火，中国渔政部门回应"},{"category":"要闻","description":"朝鲜最高领导人金正恩的专列正在驶向平壤。    韩国媒体韩联社援引消息人士称，朝鲜最高领导人金正恩的专列3月2日从越南出发后，4日晨7点经过中国天津，","id":"347709398","picUrl":"http://n1.itc.cn/img7/adapt/wb/list_pic/2019/03/04/155166914693874396_180_180.JPEG","title":"韩媒：金正恩专列将以最短路线直驶平壤"},{"category":"要闻","description":"视频显示，一名红衣男子持械与警方对峙。视频截图    新京报讯（记者 张彤）3月3日13时许，辽宁沈阳一男子持刀具及伸缩棍与警方对峙，后追打多民警","id":"347750099","picUrl":"http://n1.itc.cn/img7/adapt/wb/recom/2019/03/04/155167832078950317_180_180.PNG","title":"沈阳一男子持械伤警 鸣枪示警无效后被击伤不治"},{"category":"要闻","description":"这几天，网友发现微信     出现了神奇的翻译现象     比如                     微博网友晒图     网友们玩得很开心","id":"347747080","picUrl":"http://n1.itc.cn/img7/adapt/wb/recom/2019/03/04/155167701922838549_180_180.PNG","title":"微信翻译出bug，这些翻译是认真的吗？"},{"category":"要闻","description":"图片来源：视觉中国    文|财经 赵天宇   \u201c去年初的时候，一瓶也就几块钱。现在连进价都快30元了，还是缺货。\u201d2019年2月20日，北京市大","id":"347745829","picUrl":"http://n1.itc.cn/img7/adapt/wb/recom/2019/03/04/155167708757364062_180_180.PNG","title":"部分常用药涨价数倍：环保限产、原料药垄断，药企怕赔钱"},{"category":"要闻","description":"秦升看望多拉多。    2019赛季中超终于拉开了帷幕，首轮比赛就嗅出了最熟悉的味道\u2014\u2014红牌、罚款、下放预备队\u2026\u2026    在中超四巨头纷纷取得开","id":"347743837","picUrl":"http://n1.itc.cn/img7/adapt/wb/recom/2019/03/04/155167618614594031_180_180.PNG","title":"红牌、断腿、下放，罚款！中超第一轮，中国足球的黑镜头齐了"},{"category":"要闻","description":"刘某穿着印有\u201c义薄云天\u201d字样的鞋子到火车站送朋友。本文图片 浙江铁警微信公号    在浙江金华市打工的贵州籍男子刘某穿着印有\u201c义薄云天\u201d字样","id":"347729079","picUrl":"http://n1.itc.cn/img7/adapt/wb/recom/2019/03/04/155167334610677434_180_180.PNG","title":"在逃人员穿\u201c义薄云天\u201d鞋为人送行，在火车站被警察盘问落网"},{"category":"要闻","description":"十三届全国人大二次会议3月4日（星期一）上午11时15分在人民大会堂新闻发布厅举行新闻发布会，由大会发言人就大会议程和人大工作相关的问题回答中外记者提","id":"347738456","picUrl":"http://n1.itc.cn/img7/adapt/wb/recom/2019/03/04/155167440805584616_180_180.PNG","title":"张业遂：中国一贯鼓励和倡导企业在海外经营中严格遵守当地的法律法规"},{"category":"要闻","description":"3月4日，十三届全国人大二次会议在北京人民大会堂举行新闻发布会。大会发言人张业遂就大会议程和人大工作相关的问题回答中外记者提问。澎湃新闻记者","id":"347740938","picUrl":"http://n1.itc.cn/img7/adapt/wb/recom/2019/03/04/155167529070267068_180_180.PNG","title":"本届全国人大二次会议发言人张业遂：一位资深外交官的新角色"},{"category":"要闻","description":"2019年3月3日，美国阿拉巴马州多森，当地发生龙卷风。图片来源：视觉中国    当地时间周日（3月3日）下午，美国东部阿拉巴马和乔治亚州遭","id":"347720275","picUrl":"http://n1.itc.cn/img7/adapt/wb/recom/2019/03/04/155167000334187707_180_180.PNG","title":"十余场龙卷风登陆美国东部，阿拉巴马州至少14人丧生"},{"category":"要闻","description":"文 |  星辰   Shamima Begum的荷兰丈夫Yago Riedijk希望带她和刚出生的孩子回荷兰与他一起生活。此前，Riedijk曾表示","id":"347732957","picUrl":"http://n1.itc.cn/img7/adapt/wb/recom/2019/03/04/155167265080887119_180_180.PNG","title":"嫁给荷兰恐怖分子的15岁英国IS新娘：自己选择结婚"},{"category":"要闻","description":"新京报快讯（记者 沙璐）3月5日，李克强总理将作政府工作报告。2019年的政府工作报告有多少字，会有哪些重磅措施？中国政府网于昨天和今天发布了《 起草背后","id":"347740044","picUrl":"http://n1.itc.cn/img7/adapt/wb/recom/2019/03/04/155167498962597888_180_180.PNG","title":"揭秘政府工作报告起草幕后：有时争论得面红耳赤"},{"category":"要闻","description":"3月3日消息，两会期间格力电器董事长董明珠接受了多家媒体采访，分别谈到了提高个税起征点，以及格力的接班人、芯片业务、5G手机和海外市场等热点话题。","id":"347722276","picUrl":"http://n1.itc.cn/img7/adapt/wb/recom/2019/03/04/155167045362557586_180_180.PNG","title":"董明珠：个税起征点最好提升到1万 芯片一定会搞下去"},{"category":"要闻","description":"新京报讯（记者 张赫）3月4日，39岁的韩国歌手、演员李贞贤在社交平台发布亲笔信，宣布了将于4月7日结婚的喜讯。李贞贤说，\u201c遇到给了我无限勇气和毫不吝惜的","id":"347720394","picUrl":"http://n1.itc.cn/img7/adapt/wb/recom/2019/03/04/155167001159312939_180_180.PNG","title":"39岁韩星李贞贤宣布4月7日结婚，丈夫为圈外人"},{"category":"要闻","description":"新京报快讯（记者 谢莲）据美国有线电视新闻网（CNN）报道，当地时间3月3日，经阿尔及利亚现总统布特弗利卡竞选经理扎莱尼确认，布特弗利卡将参加4月18","id":"347717162","picUrl":"http://n1.itc.cn/img7/adapt/wb/recom/2019/03/04/155166956188086684_180_180.PNG","title":"82岁的阿尔及利亚现总统寻求第五任期，数千人抗议"},{"category":"要闻","description":"（观察者网讯）近日因宣布支持英国进行第二轮脱欧公投的英国最大在野党工党党魁科尔宾（Jeremy Corbyn），周日（3日）在造访伦敦北部选取时被鸡蛋","id":"347713983","picUrl":"http://n1.itc.cn/img7/adapt/wb/recom/2019/03/04/155166955997318269_180_180.PNG","title":"支持二次脱欧公投，英工党党魁被扔鸡蛋"},{"category":"要闻","description":"中新网客户端北京3月4日电(记者 李金磊)4日，中国股市继续飘红，两市双双高开，沪指开盘一举站上3000点关口，再创这一轮反弹新高。   其中，开盘沪","id":"347699104","picUrl":"http://n1.itc.cn/img7/adapt/wb/recom/2019/03/04/155166615331296420_180_180.PNG","title":"沪指重新站上3000点关口 后市行情还会继续涨吗？"},{"category":"要闻","description":"来源：人民日报   十八大以来   六年中的全国两会   习近平多次参加审议讨论   与代表委员们共商国是   留下不少暖心的话     他说，什么地","id":"347688041","picUrl":"http://n1.itc.cn/img7/adapt/wb/recom/2019/03/04/155166423449751892_180_180.PNG","title":"微视频｜这六年，习近平两会上的暖心话"},{"category":"要闻","description":"3月1日，加拿大总理特鲁多改组内阁，这已经是他一个多月来第二次改组内阁了。  根据此次改组，原农业和农产食品部长麦考利去填了辞职的威尔逊－雷布尔德退","id":"347671811","picUrl":"http://n1.itc.cn/img7/adapt/wb/list_pic/2019/03/04/155166096867192965_180_180.JPEG","title":"加拿大总理特鲁多摊上大事了！官商勾结还干预司法，一个多月两次组阁"}]}
     * demand_semantic : {"datetime.date":"今日","service":"news"}
     * dialog_stat : dataInvalid
     * engine_time : 30.892
     * operation : PLAY
     * orig_semantic : {"slots":{"datetime.date":"今日"}}
     * rc : 0
     * save_history : true
     * score : 0
     * search_semantic : {"datetime.date":"今日","service":"news"}
     * semantic : {"slots":{"datetime":{"date":"2019-03-04","dateOrig":"今日","type":"DT_BASIC"}}}
     * service : news
     * sid : cida1624662@dx000a0fd819c1010009
     * text : 今日新闻
     * used_state : {"state":"playing","state_key":"fg::news::default::playing"}
     * uuid : cida1624662@dx000a0fd819c1010009
     */

    private AnswerBean answer;
    private int array_index;
    private String cid;
    private DataBean data;
    private DemandSemanticBean demand_semantic;
    private String dialog_stat;
    private double engine_time;
    private String operation;
    private OrigSemanticBean orig_semantic;
    private int rc;
    private boolean save_history;
    private int score;
    private SearchSemanticBean search_semantic;
    private SemanticBean semantic;
    private String service;
    private String sid;
    private String text;
    private UsedStateBean used_state;
    private String uuid;

    public AnswerBean getAnswer() {
        return answer;
    }

    public void setAnswer(AnswerBean answer) {
        this.answer = answer;
    }

    public int getArray_index() {
        return array_index;
    }

    public void setArray_index(int array_index) {
        this.array_index = array_index;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public DemandSemanticBean getDemand_semantic() {
        return demand_semantic;
    }

    public void setDemand_semantic(DemandSemanticBean demand_semantic) {
        this.demand_semantic = demand_semantic;
    }

    public String getDialog_stat() {
        return dialog_stat;
    }

    public void setDialog_stat(String dialog_stat) {
        this.dialog_stat = dialog_stat;
    }

    public double getEngine_time() {
        return engine_time;
    }

    public void setEngine_time(double engine_time) {
        this.engine_time = engine_time;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public OrigSemanticBean getOrig_semantic() {
        return orig_semantic;
    }

    public void setOrig_semantic(OrigSemanticBean orig_semantic) {
        this.orig_semantic = orig_semantic;
    }

    public int getRc() {
        return rc;
    }

    public void setRc(int rc) {
        this.rc = rc;
    }

    public boolean isSave_history() {
        return save_history;
    }

    public void setSave_history(boolean save_history) {
        this.save_history = save_history;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public SearchSemanticBean getSearch_semantic() {
        return search_semantic;
    }

    public void setSearch_semantic(SearchSemanticBean search_semantic) {
        this.search_semantic = search_semantic;
    }

    public SemanticBean getSemantic() {
        return semantic;
    }

    public void setSemantic(SemanticBean semantic) {
        this.semantic = semantic;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public UsedStateBean getUsed_state() {
        return used_state;
    }

    public void setUsed_state(UsedStateBean used_state) {
        this.used_state = used_state;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public static class AnswerBean {
        /**
         * text : 这是一条关于要闻的新闻。“建议春节假期延长到15天”，有人不太同意
         * type : T
         */

        private String text;
        private String type;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class DataBean {
        private List<ResultBean> result;

        public List<ResultBean> getResult() {
            return result;
        }

        public void setResult(List<ResultBean> result) {
            this.result = result;
        }

        public static class ResultBean {
            /**
             * category : 要闻
             * description : 近日，“春节假期是否该延长”的话题引发网友热议。     据合肥在线报道，全国人大代表 李小莉 建议将春节假期由 现在7天延长到10-15天。
             * id : 347747073
             * picUrl : http://n1.itc.cn/img7/adapt/wb/recom/2019/03/04/155167701884554188_180_180.PNG
             * title : “建议春节假期延长到15天”，有人不太同意
             */

            private String category;
            private String description;
            private String id;
            private String picUrl;
            private String title;

            public String getCategory() {
                return category;
            }

            public void setCategory(String category) {
                this.category = category;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getPicUrl() {
                return picUrl;
            }

            public void setPicUrl(String picUrl) {
                this.picUrl = picUrl;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }
    }

    public static class DemandSemanticBean {
        @SerializedName("datetime.date")
        private String _$DatetimeDate173; // FIXME check this code
        private String service;

        public String get_$DatetimeDate173() {
            return _$DatetimeDate173;
        }

        public void set_$DatetimeDate173(String _$DatetimeDate173) {
            this._$DatetimeDate173 = _$DatetimeDate173;
        }

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }
    }

    public static class OrigSemanticBean {
        /**
         * slots : {"datetime.date":"今日"}
         */

        private SlotsBean slots;

        public SlotsBean getSlots() {
            return slots;
        }

        public void setSlots(SlotsBean slots) {
            this.slots = slots;
        }

        public static class SlotsBean {
            @SerializedName("datetime.date")
            private String _$DatetimeDate117; // FIXME check this code

            public String get_$DatetimeDate117() {
                return _$DatetimeDate117;
            }

            public void set_$DatetimeDate117(String _$DatetimeDate117) {
                this._$DatetimeDate117 = _$DatetimeDate117;
            }
        }
    }

    public static class SearchSemanticBean {
        @SerializedName("datetime.date")
        private String _$DatetimeDate23; // FIXME check this code
        private String service;

        public String get_$DatetimeDate23() {
            return _$DatetimeDate23;
        }

        public void set_$DatetimeDate23(String _$DatetimeDate23) {
            this._$DatetimeDate23 = _$DatetimeDate23;
        }

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }
    }

    public static class SemanticBean {
        /**
         * slots : {"datetime":{"date":"2019-03-04","dateOrig":"今日","type":"DT_BASIC"}}
         */

        private SlotsBeanX slots;

        public SlotsBeanX getSlots() {
            return slots;
        }

        public void setSlots(SlotsBeanX slots) {
            this.slots = slots;
        }

        public static class SlotsBeanX {
            /**
             * datetime : {"date":"2019-03-04","dateOrig":"今日","type":"DT_BASIC"}
             */

            private DatetimeBean datetime;

            public DatetimeBean getDatetime() {
                return datetime;
            }

            public void setDatetime(DatetimeBean datetime) {
                this.datetime = datetime;
            }

            public static class DatetimeBean {
                /**
                 * date : 2019-03-04
                 * dateOrig : 今日
                 * type : DT_BASIC
                 */

                private String date;
                private String dateOrig;
                private String type;

                public String getDate() {
                    return date;
                }

                public void setDate(String date) {
                    this.date = date;
                }

                public String getDateOrig() {
                    return dateOrig;
                }

                public void setDateOrig(String dateOrig) {
                    this.dateOrig = dateOrig;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }
            }
        }
    }

    public static class UsedStateBean {
        /**
         * state : playing
         * state_key : fg::news::default::playing
         */

        private String state;
        private String state_key;

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getState_key() {
            return state_key;
        }

        public void setState_key(String state_key) {
            this.state_key = state_key;
        }
    }
}
