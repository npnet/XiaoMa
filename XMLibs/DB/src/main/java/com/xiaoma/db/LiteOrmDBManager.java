package com.xiaoma.db;

import android.content.Context;
import android.text.TextUtils;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.xiaoma.config.ConfigManager;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * LiteOrm 方法实现
 *
 * @author zs
 * @date 2018/9/11 0011.
 */
class LiteOrmDBManager implements IDatabase {
    private String dbPath;

    private LiteOrm mLiteOrm;

    LiteOrmDBManager() {
    }

    LiteOrmDBManager(String path) {
        if (TextUtils.isEmpty(path) || path.trim().isEmpty()) {
            throw new IllegalArgumentException("path is empty");
        }
        this.dbPath = path;
    }

    @Override
    public boolean isDBManagerInitSuccess() {
        return mLiteOrm != null;
    }

    @Override
    public void init(Context context) {
        realInit(context, false);
    }

    @Override
    public void initCascade(Context context) {
        realInit(context, true);
    }

    private void realInit(Context context, boolean isCascade) {
        DataBaseConfig config = new DataBaseConfig(context);
        if (TextUtils.isEmpty(dbPath)) {
            config.dbName = DBConstants.DB_NAME;
        } else {
            config.dbName = dbPath + File.separator + DBConstants.DB_NAME;
        }
        config.dbVersion = DBConstants.DB_VERSION;
        config.onUpdateListener = null;
        if (!isCascade) {
            mLiteOrm = LiteOrm.newSingleInstance(config);
        } else {
            mLiteOrm = LiteOrm.newCascadeInstance(config);
        }
        mLiteOrm.setDebugged(ConfigManager.ApkConfig.isDebug());
    }

    @Override
    public <T> long save(T t) {
        return mLiteOrm.save(t);
    }

    @Override
    public <T> long saveAll(List<T> list) {
        return mLiteOrm.save(list);
    }

    @Override
    public <T> long insert(T t) {
        return mLiteOrm.insert(t);
    }

    @Override
    public <T> long insert(List<T> list) {
        return mLiteOrm.insert(list);
    }

    @Override
    public <T> long update(T t) {
        return mLiteOrm.update(t);
    }

    @Override
    public <T> long update(T t, HashMap<String, Object> changeEntityMap) {
        return mLiteOrm.update(t, new ColumnsValue(changeEntityMap), ConflictAlgorithm.Replace);
    }

    @Override
    public long update(WhereBuilder whereBuilder, HashMap<String, Object> changeEntityMap) {
        return mLiteOrm.update(whereBuilder, new ColumnsValue(changeEntityMap), ConflictAlgorithm.Replace);
    }

    @Override
    public <T> long update(List<T> list) {
        return mLiteOrm.update(list);
    }

    @Override
    public <T> long delete(T t) {
        return mLiteOrm.delete(t);
    }

    @Override
    public <T> long delete(List<T> list) {
        return mLiteOrm.delete(list);
    }

    @Override
    public <T> long delete(Class<T> clazz) {
        return mLiteOrm.delete(clazz);
    }

    @Override
    public <T> long deleteAll(Class<T> clazz) {
        return mLiteOrm.deleteAll(clazz);
    }

    @Override
    public <T> T queryById(long id, Class<T> clazz) {
        return mLiteOrm.queryById(id, clazz);
    }

    @Override
    public <T> T queryById(String id, Class<T> clazz) {
        return mLiteOrm.queryById(id, clazz);
    }

    @Override
    public <T> List<T> queryAll(Class<T> clazz) {
        return mLiteOrm.query(clazz);
    }

    @Override
    public <T> List<T> queryByWhere(Class<T> clazz, String field, String value) {
        return mLiteOrm.query(new QueryBuilder<>(clazz).where(field + "=?", new String[]{value}));
    }

    @Override
    public <T> List<T> queryByWhere(Class<T> clazz, String[] fields, String[][] values) {
        if (fields.length != values.length) {
            throw new IllegalArgumentException();
        }
        QueryBuilder<T> queryBuilder = new QueryBuilder<>(clazz);
        for (int i = 0; i < fields.length; i++) {
            if (i == 0) {
                queryBuilder.where(fields[i] + "=?", values[i]);
            } else {
                queryBuilder.whereAppendAnd().whereAppend(fields[i] + "=?", values[i]);
            }
        }
        return mLiteOrm.query(queryBuilder);
    }

    @Override
    public <T> List<T> queryByWhere(Class<T> clazz, QueryBuilder<T> queryBuilder) {
        return mLiteOrm.query(queryBuilder);
    }

    @Override
    public <T> List<T> queryByWhere(Class<T> clazz, String field, String[] value, int start, int length) {
        return mLiteOrm.query(new QueryBuilder<>(clazz).where(field + "=?", value).limit(start, length));
    }

    @Override
    public <T> List<T> queryLimit(Class<T> clazz, int start, int length) {
        QueryBuilder<T> builder = new QueryBuilder<>(clazz);
        builder.limit(start, length);
        return mLiteOrm.query(builder);
    }

    @Override
    public <T> List<T> queryData(QueryBuilder<T> queryBuilder) {
        return mLiteOrm.query(queryBuilder);
    }

    @Override
    public <T> long queryCount(Class<T> clazz) {
        return mLiteOrm.queryCount(clazz);
    }

    @Override
    public boolean deleteDatabase() {
        return mLiteOrm.deleteDatabase();
    }
}
