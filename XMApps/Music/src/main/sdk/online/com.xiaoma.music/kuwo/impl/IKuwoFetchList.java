package com.xiaoma.music.kuwo.impl;

import com.xiaoma.music.kuwo.listener.OnAudioFetchListener;
import com.xiaoma.music.kuwo.model.XMAlbumInfo;
import com.xiaoma.music.kuwo.model.XMArtistInfo;
import com.xiaoma.music.kuwo.model.XMBaseQukuItem;
import com.xiaoma.music.kuwo.model.XMBillboardInfo;
import com.xiaoma.music.kuwo.model.XMCategoryListInfo;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.kuwo.model.XMSongListInfo;

import java.util.List;

import cn.kuwo.open.ImageSize;

/**
 * Created by ZYao.
 * Date ：2018/10/15 0015
 */
public interface IKuwoFetchList {
    /**
     * 获取推荐歌单
     *
     * @param listener 回调接口
     */
    void fetchRecommendSongList(OnAudioFetchListener<List<XMBaseQukuItem>> listener);


    /**
     * 获取分类
     *
     * @param listener 接收结果的回调
     */
    void fetchMusicCategories(OnAudioFetchListener<List<XMBaseQukuItem>> listener);


    /**
     * 获取排行榜信息
     *
     * @param listener 结果返回回调
     */
    void fetchBillBroad(OnAudioFetchListener<List<XMBillboardInfo>> listener);


    /**
     * 获取全部歌手
     *
     * @param isOrderByHot 是否根据热度排，true表示根据热度，false表示默认排序既根据名称
     * @param page         页码，从0开始
     * @param count        每页显示的数量
     * @param listener     接收结果的回调
     **/
    void fetchAllArtist(boolean isOrderByHot, int page, int count, OnAudioFetchListener listener);


    /**
     * 根据分类获取歌手
     *
     * @param type     歌手分类 @see cn.kuwo.open.base.ArtistType
     * @param page     页码
     * @param count    每页的数量
     * @param listener 结果回调
     **/
    void fetchArtistByType(@IKuwoConstant.IArtistType int type, int page, int count, OnAudioFetchListener listener);


    /**
     * 获取电台列表
     *
     * @param listener 结果返回回调
     */
    void fetchRadio(OnAudioFetchListener listener);

    /**
     * 搜索接口
     *
     * @param key      搜索的关键字
     * @param type     搜索类型 @see cn.kuwo.open.base.SearchType
     * @param page     页码从0开始
     * @param count    每页的数目
     * @param listener 数据回调接口
     */
    void search(String key, @IKuwoConstant.ISearchType int type, int page, int count, OnAudioFetchListener listener);

    /**
     * 获取每日推荐
     */
    void fetchDailyRecommend(OnAudioFetchListener listener);

    /**
     * 获取列表类型包含的内容
     *
     * @param item     列表 可能是歌单（@see cn.kuwo.base.bean.quku.SongListInfo）
     *                 或者排行榜(@see  cn.kuwo.base.bean.quku.BillboardInfo)
     *                 或者是分类歌单(@see cn.kuwo.base.bean.quku.CategoryListInfo)
     * @param page     页码从0开始
     * @param count    每页的数量
     * @param listener 接收结果的回调
     */
    void fetchCategoryListMusic(XMCategoryListInfo item, int page, int count, OnAudioFetchListener<List<XMSongListInfo>> listener);

    void fetchSongListMusic(XMSongListInfo item, int page, int count, OnAudioFetchListener listener);

    void fetchBillboardMusic(XMBillboardInfo item, int page, int count, OnAudioFetchListener<List<XMMusic>> listener);

    /**
     * 获取歌手的歌曲
     *
     * @param artist   歌手
     * @param page     页码
     * @param count    每页的数量
     * @param listener 接收结果的回调
     */
    void fetchArtistMusic(XMArtistInfo artist, int page, int count, OnAudioFetchListener listener);

    /**
     * 获取歌手的专辑
     *
     * @param artist   歌手
     * @param page     页码
     * @param count    每页的数量
     * @param listener 接收结果的回调
     */
    void fetchArtistAlbum(XMArtistInfo artist, int page, int count, OnAudioFetchListener listener);

    /**
     * 获取专辑里面的歌曲
     *
     * @param album    专辑信息
     * @param page     页码
     * @param count    每页的数量
     * @param listener 接收结果的回调
     */
    void fetchAlbumMusic(XMAlbumInfo album, int page, int count, OnAudioFetchListener<List<XMMusic>> listener);

    /**
     * 获取相似歌曲
     *
     * @param pMusic      相似歌曲的歌曲信息
     * @param pMusicCount 歌曲数量，最多不超过这个数值
     * @param listener    回调接口
     */
    void fetchSimilarSong(XMMusic pMusic, int pMusicCount, OnAudioFetchListener<List<XMMusic>> listener);

    /**
     * 获取歌曲的歌词
     *
     * @param music    需要获取歌词的歌曲信息
     * @param listener 接收请求结果
     **/
    void fetchLyric(final XMMusic music, OnAudioFetchListener<String> listener);

    /**
     * 获取歌曲图片
     *
     * @param pMusic    要获取图片的歌曲
     * @param pListener 结果回调
     * @param pSize     图片尺寸 {@link ImageSize}
     */
    void fetchImage(final XMMusic pMusic, OnAudioFetchListener<String> pListener, @IKuwoConstant.IImageSize int pSize);

    /**
     * 获取搜索热词的接口
     *
     * @param listener 结果回调
     */
    void fetchSearchHotKeywords(OnAudioFetchListener<List<String>> listener);

    /**
     * 获取热门歌单
     *
     * @param listener 接口回调
     */
    void fetchHotSongList(int page, int count, OnAudioFetchListener listener);

    /**
     * 获取最新歌单
     *
     * @param listener 接口回调
     */
    void fetchNewSongList(int page, int count, OnAudioFetchListener listener);


    /**
     * 获取搜索提示
     *
     * @param keywords 提示的关键字
     * @param listener 结果回调
     */
    void fetchSearchTips(String keywords, OnAudioFetchListener listener);

    void fetchMusicById(long id, OnAudioFetchListener<XMMusic> listener);

    void fetchMusicByIds(List<Long> ids, OnAudioFetchListener<List<XMMusic>> listener);
}
