package com.xiaoma.musicrec.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author zs
 * @date 2018/3/2 0002.
 */

public class Music implements Serializable {

    /**
     * external_ids : {}
     * play_offset_ms : 18462
     * release_date : 2009-01-01
     * artists : [{"name":"胡歌"}]
     * external_metadata : {}
     * title : 美丽的神话 (《神话》电视剧片尾曲)
     * duration_ms : 331000
     * album : {"name":"神话 电视剧原声带"}
     * acrid : 5c00627becb76c3cbd99784e45e19c08
     * result_from : 1
     * score : 73
     */

    private ExternalIdsBean external_ids;
    private int play_offset_ms;
    private String release_date;
    private ExternalMetadataBean external_metadata;
    private String title;
    private int duration_ms;
    private AlbumBean album;
    private String acrid;
    private int result_from;
    private int score;
    private List<ArtistsBean> artists;

    public ExternalIdsBean getExternal_ids() {
        return external_ids;
    }

    public void setExternal_ids(ExternalIdsBean external_ids) {
        this.external_ids = external_ids;
    }

    public int getPlay_offset_ms() {
        return play_offset_ms;
    }

    public void setPlay_offset_ms(int play_offset_ms) {
        this.play_offset_ms = play_offset_ms;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public ExternalMetadataBean getExternal_metadata() {
        return external_metadata;
    }

    public void setExternal_metadata(ExternalMetadataBean external_metadata) {
        this.external_metadata = external_metadata;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration_ms() {
        return duration_ms;
    }

    public void setDuration_ms(int duration_ms) {
        this.duration_ms = duration_ms;
    }

    public AlbumBean getAlbum() {
        return album;
    }

    public void setAlbum(AlbumBean album) {
        this.album = album;
    }

    public String getAcrid() {
        return acrid;
    }

    public void setAcrid(String acrid) {
        this.acrid = acrid;
    }

    public int getResult_from() {
        return result_from;
    }

    public void setResult_from(int result_from) {
        this.result_from = result_from;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<ArtistsBean> getArtists() {
        return artists;
    }

    public void setArtists(List<ArtistsBean> artists) {
        this.artists = artists;
    }

    public static class ExternalIdsBean {
    }

    public static class ExternalMetadataBean {
    }

    public static class AlbumBean {
        /**
         * name : 神话 电视剧原声带
         *image: https://cn-api.acrcloud.com/v1/metadata/image/ace196cb5fd67eb940a3d5665df44124.jpg
         */

        private String name;

        private String image;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class ArtistsBean {
        /**
         * name : 胡歌
         */

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
