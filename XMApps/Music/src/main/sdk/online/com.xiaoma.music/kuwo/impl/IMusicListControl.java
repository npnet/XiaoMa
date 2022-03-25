package com.xiaoma.music.kuwo.impl;

import com.xiaoma.music.kuwo.model.XMListType;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.kuwo.model.XMMusicList;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import cn.kuwo.base.bean.Music;

/**
 * Created by ZYao.
 * Date ：2018/10/16 0016
 */
public interface IMusicListControl {
    /**
     * 插入列表。有些类型的列表只能有一个，重复插入会失败。列表不能重名。名称要符合规范。否则都会失败。
     *
     * @param t
     * @param listName 名称不能包括除下划线以外的特殊字符（\/"*<>:?&|;,空格和回车字符），长度不能超过40个字符
     * @return
     */
    XMMusicList insertList(final XMListType t, final String listName);

    /**
     * 插入列表。有些类型的列表只能有一个，重复插入会失败。列表不能重名,不能有特殊字符,这里自动为列表重命名。
     *
     * @param t
     * @param listName 名称不能包括除下划线以外的特殊字符（\/"*<>:?&|;,空格和回车字符），长度不能超过40个字符
     * @return
     */
    XMMusicList insertListAutoRename(final XMListType t, final String listName);

    /**
     * 删除该类型的所有列表
     *
     * @param t
     * @return
     */
    boolean deleteList(final XMListType t);

    /**
     * 删除指定名称的列表
     *
     * @param listName
     * @return
     */
    boolean deleteList(final String listName);

    /**
     * 按名字查找列表, 失败返回null
     *
     * @param listName
     * @return
     */
    XMMusicList getList(final String listName);

    /**
     * 按类型查找列表, 只适用于只能有唯一一个列表的类型。
     *
     * @param t
     * @return
     */
    XMMusicList getUniqueList(XMListType t);

    /**
     * 获取指定类型的所有列表，不能保存和修改返回的集合
     *
     * @param t
     * @return
     */
    Collection<XMMusicList> getList(final XMListType t);

    /**
     * 获取指定类型的所有列表名字，不能保存和修改返回的集合
     *
     * @param t
     * @return
     */
    Collection<String> getListName(final XMListType t);

    /**
     * 获取用户可以添加歌曲的列表。
     *
     * @return
     */
    Collection<String> getInsertableMusicListName();

    /**
     * 获取所有列表，不能保存和修改返回的集合
     *
     * @return
     */
    Collection<XMMusicList> getAllList();

    /**
     * 获取需要显示的列表.临时列表,删除列表等都不返回.并且返回结果就是显示顺序.
     *
     * @return
     */
    List<XMMusicList> getShowList();


    //**********************************************************************
    //歌曲方法
    //**********************************************************************

    /**
     * 在列表末尾插入一首歌曲的拷贝，并删除前面于他重复的歌曲。返回歌曲插入的索引。失败了返回-1。
     *
     * @param listName
     * @param music
     * @return
     */
    int insertMusic(final String listName, final XMMusic music);

    /**
     * 在列表末尾插入一批歌曲的拷贝，并删除前面于他重复的歌曲。返回第一首歌曲插入的索引。失败了返回-1
     *
     * @param listName
     * @param music
     * @return
     */
    int insertMusic(final String listName, final List<XMMusic> music);

    /**
     * 在指定位置插入一首歌曲的拷贝，与现有歌曲重复的歌曲不插入，返回第一首歌曲插入的索引。失败了返回-1。
     *
     * @param listName
     * @param music
     * @param position
     * @return
     */
    int insertMusic(final String listName, final XMMusic music, final int position);

    /**
     * 在指定位置插入一批歌曲的拷贝，与现有歌曲重复的歌曲不插入，返回第一首歌曲插入的索引。失败了返回-1 , 超过容量上限返回 -2。
     *
     * @param listName
     * @param music
     * @param position
     * @return
     */
    int insertMusic(final String listName, final List<XMMusic> music, final int position);

    /**
     * 删除列表的所有歌曲
     *
     * @param listName
     * @return
     */
    boolean deleteMusic(final String listName);

    /**
     * 删除列表指定位置的一首歌曲
     *
     * @param listName
     * @param position
     * @return
     */
    boolean deleteMusic(final String listName, final int position);

    /**
     * 删除列表指定区间的一批歌曲
     *
     * @param listName
     * @param start
     * @param count
     * @return
     */
    boolean deleteMusic(final String listName, final int start, final int count);

    /**
     * 删除一批指定位置的歌曲
     *
     * @param listName
     * @param position
     * @return
     */
    boolean deleteMusic(final String listName, final Collection<Integer> position);

    /**
     * 删除指定的歌曲对象
     *
     * @param listName
     * @param music
     * @return
     */
    boolean deleteMusic(final String listName, final XMMusic music);

    /**
     * 删除一批指定的歌曲
     *
     * @param listName
     * @param musics
     * @return
     */
    boolean deleteMusic(final String listName, List<XMMusic> musics);

    /**
     * 删除指定的歌曲内容
     *
     * @param listName
     * @param music
     * @return 删除的歌曲数量
     */
    int deleteMusicEx(final String listName, final XMMusic music);

    /**
     * 删除一批指定的歌曲内容
     *
     * @param listName
     * @param musics
     * @return 删除的歌曲数量
     */
    int deleteMusicEx(final String listName, final List<XMMusic> musics);

    /**
     * 查找某首歌曲对象在列表中的位置,没有找到返回-1;
     *
     * @param listName
     * @param music
     * @return
     */
    int indexOf(final String listName, final XMMusic music);

    /**
     * 对列表中的歌曲排序,可以自己定义排序器。也可以使用下面的排序对象。nameComparator  dateComparator artistComparator
     *
     * @param listName
     * @param comparator
     * @return
     */
    boolean sortMusic(final String listName, final Comparator<Music> comparator);

    /**
     * 手动云同步,云同步内部会自动调用同步，尽量不要手动调用
     *
     * @return 如果没有网络或者用户未登录返回false
     */
    boolean syn();
}
