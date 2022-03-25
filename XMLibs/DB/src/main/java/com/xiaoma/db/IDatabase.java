package com.xiaoma.db;

import android.content.Context;

import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;

import java.util.HashMap;
import java.util.List;

/**
 * 数据库方法接口
 *
 * @author zs
 * @date 2018/9/11 0011.
 */
public interface IDatabase {

    void init(Context context);

    boolean isDBManagerInitSuccess();

    /**
     * 支持级联操作的初始化
     */
    void initCascade(Context context);

    /**
     * 插入一条记录,当主键重复时,会替换原有记录
     */
    <T> long save(T t);

    /**
     * 插入多条记录,当主键重复时,会替换原有记录
     */
    <T> long saveAll(List<T> list);

    /**
     * 插入一条记录
     * 注意:当主键重复时,会插入失败,注意和{@link #save(Object)}的区别
     */
    <T> long insert(T t);

    /**
     * 插入多条记录
     * 注意:当主键重复时,会插入失败,注意和{@link #saveAll(List)}的区别
     */
    <T> long insert(List<T> list);

    /**
     * 更新一条记录
     */
    <T> long update(T t);

    /**
     * 更新一条记录的字段
     */
    <T> long update(T t, HashMap<String, Object> changeEntityMap);

    /**
     * 根据满足的条件，更新对应的记录的字段
     */
    long update(WhereBuilder whereBuilder, HashMap<String, Object> changeEntityMap);

    /**
     * 更新所有记录
     */
    <T> long update(List<T> list);

    /**
     * 删除一条数据
     */
    <T> long delete(T t);

    /**
     * 删除集合中的数据
     */
    <T> long delete(List<T> list);

    /**
     * 清除表内的记录
     */
    <T> long delete(Class<T> clazz);

    /**
     * 清除所有与之有关联的记录
     */
    <T> long deleteAll(Class<T> clazz);

    /**
     * 根据id查询
     */
    <T> T queryById(long id, Class<T> clazz);

    /**
     * 根据id查询
     */
    <T> T queryById(String id, Class<T> clazz);

    /**
     * 查询所有
     */
    <T> List<T> queryAll(Class<T> clazz);

    /**
     * 查询某字段等于value的值
     */
    <T> List<T> queryByWhere(Class<T> clazz, String field, String value);

    /**
     * 查询多个字段等于对应value值 的对象
     */
    <T> List<T> queryByWhere(Class<T> clazz, String[] fields, String[][] values);

    /**
     * 自定义查询
     */
    <T> List<T> queryByWhere(Class<T> clazz, QueryBuilder<T> queryBuilder);

    /**
     * 查询某字段等于value的值,可以指定从1-20，即分页
     */
    <T> List<T> queryByWhere(Class<T> clazz, String field, String[] value, int start, int length);

    <T> List<T> queryLimit(Class<T> clazz, int start, int length);

    <T> List<T> queryData(QueryBuilder<T> queryBuilder);

    <T> long queryCount(Class<T> clazz);

    /**
     * 删除数据库
     */
    boolean deleteDatabase();
}
