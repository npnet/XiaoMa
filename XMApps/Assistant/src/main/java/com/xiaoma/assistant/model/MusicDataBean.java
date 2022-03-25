package com.xiaoma.assistant.model;

import java.util.List;

/**
 * Created by qiuboxiang on 2019/8/13 1:27
 * Desc:
 */
public class MusicDataBean {

    /**
     * debug : {"deadline":600,"url":"http://content.xfyun.cn/music/query?&song=%E5%90%BB%E5%88%AB&exclude=%7B%22singer%22%3A%22%E5%BC%A0%E5%AD%A6%E5%8F%8B%22%7D&operation=PLAY&append=%7B%22sid%22%3A%22cida1620c1f%40dx000a10acec7301001e%22%2C%22condition%22%3A%22main-musicX%22%2C%22caller%22%3A%22187837596%22%7D&appId=5c0f61ba&deviceId=6882092694×tamp=1565630596000&token=750e9c8040f681f95cdd5793daac4eee","used time":29}
     * inherit : 0
     * isCached : 0
     * priority : 0
     * result : [{"albumname":"为爱付出","fuzzy_score":1,"itemid":"6470424","movienames":[],"neatsongname":["吻别"],"programname":"","publishtime":1422979200,"singeraliasnames":["NINI","Nini"],"singerids":["82807"],"singernames":["庄妮"],"songname":"吻别","source":"iflytek","tagnames":[""]},{"albumname":"爱如此神奇","fuzzy_score":1,"itemid":"9369041","movienames":[],"neatsongname":["吻别"],"programname":"","publishtime":820425600,"singeraliasnames":["AndyLau","刘主席","华Dee","华仔","华哥","华神"],"singerids":["25000"],"singernames":["刘德华"],"songname":"吻别","source":"iflytek","tagnames":["国语","流行","80后","怀旧","经典","伤感","夜晚","安静",""]},{"albumname":"浪漫萨克斯五男人篇","fuzzy_score":1,"itemid":"56921547","movienames":[],"neatsongname":["吻别"],"programname":"","publishtime":1220198400,"singeraliasnames":[],"singerids":["173846"],"singernames":["群星"],"songname":"吻别","source":"iflytek","tagnames":[""]},{"albumname":"声声醉4","fuzzy_score":1,"itemid":"1665662","movienames":[],"neatsongname":["吻别"],"programname":"","publishtime":1141142400,"singeraliasnames":["Liu Fang"],"singerids":["2119691"],"singernames":["刘芳"],"songname":"吻别","source":"iflytek","tagnames":["流行","放松","驾车","怀旧","经典","安静","夜晚","伤感","民谣",""]},{"albumname":"蔡琴《金声演奏厅》","fuzzy_score":1,"itemid":"57855834","movienames":[],"neatsongname":["吻别"],"programname":"","publishtime":1193587200,"singeraliasnames":["Tsai Chin"],"singerids":["128074"],"singernames":["蔡琴"],"songname":"吻别","source":"iflytek","tagnames":[""]}]
     * sem_score : {"song":{"lcs":1,"pos":"ps","txt":"吻别"},"top":0}
     */

    private List<ResultBean> result;

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * albumname : 为爱付出
         * fuzzy_score : 1
         * itemid : 6470424
         * movienames : []
         * neatsongname : ["吻别"]
         * programname :
         * publishtime : 1422979200
         * singeraliasnames : ["NINI","Nini"]
         * singerids : ["82807"]
         * singernames : ["庄妮"]
         * songname : 吻别
         * source : iflytek
         * tagnames : [""]
         */

        private String albumname;
        private String itemid;
        private String programname;
        private String songname;
        private String source;
        private List<?> movienames;
        private List<String> neatsongname;
        private List<String> singeraliasnames;
        private List<String> singerids;
        private List<String> singernames;
        private List<String> tagnames;

        public String getAlbumname() {
            return albumname;
        }

        public void setAlbumname(String albumname) {
            this.albumname = albumname;
        }

        public String getItemid() {
            return itemid;
        }

        public void setItemid(String itemid) {
            this.itemid = itemid;
        }

        public String getProgramname() {
            return programname;
        }

        public void setProgramname(String programname) {
            this.programname = programname;
        }

        public String getSongname() {
            return songname;
        }

        public void setSongname(String songname) {
            this.songname = songname;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public List<?> getMovienames() {
            return movienames;
        }

        public void setMovienames(List<?> movienames) {
            this.movienames = movienames;
        }

        public List<String> getNeatsongname() {
            return neatsongname;
        }

        public void setNeatsongname(List<String> neatsongname) {
            this.neatsongname = neatsongname;
        }

        public List<String> getSingeraliasnames() {
            return singeraliasnames;
        }

        public void setSingeraliasnames(List<String> singeraliasnames) {
            this.singeraliasnames = singeraliasnames;
        }

        public List<String> getSingerids() {
            return singerids;
        }

        public void setSingerids(List<String> singerids) {
            this.singerids = singerids;
        }

        public List<String> getSingernames() {
            return singernames;
        }

        public void setSingernames(List<String> singernames) {
            this.singernames = singernames;
        }

        public List<String> getTagnames() {
            return tagnames;
        }

        public void setTagnames(List<String> tagnames) {
            this.tagnames = tagnames;
        }
    }
}
