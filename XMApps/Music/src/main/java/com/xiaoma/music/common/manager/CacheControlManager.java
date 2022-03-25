package com.xiaoma.music.common.manager;

import android.util.Base64;

import com.xiaoma.db.DBManager;
import com.xiaoma.music.common.model.BaseCacheInfo;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.log.KLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: loren
 * Date: 2018/11/12 0012
 */

public class CacheControlManager<T extends BaseCacheInfo, K> {

    public static CacheControlManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final CacheControlManager instance = new CacheControlManager();
    }

    public void getCache(final CacheCallBack callBack, final Class<T> clazz) {
        ThreadDispatcher.getDispatcher().post(new Runnable() {
            @Override
            public void run() {
                List<BaseCacheInfo> baseCacheInfoList = (List<BaseCacheInfo>) DBManager.getInstance().getDBManager().queryAll(clazz);
                if (baseCacheInfoList != null && !baseCacheInfoList.isEmpty()) {
                    List<K> newList = new ArrayList<>();
                    for (int i = 0; i < baseCacheInfoList.size(); i++) {
                        byte[] bytes = Base64.decode(baseCacheInfoList.get(i).getCacheStream(), Base64.DEFAULT);
                        ByteArrayInputStream bis;
                        ObjectInputStream ois = null;
                        K obj = null;
                        try {
                            bis = new ByteArrayInputStream(bytes);
                            ois = new ObjectInputStream(bis);
                            obj = (K) ois.readObject();
                            newList.add(obj);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } finally {
                            if (ois != null) {
                                try {
                                    ois.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    if (!newList.isEmpty()) {
                        KLog.i("The Cache get success: " + newList.size());
                        callBack.onSuccess(newList);
                    } else {
                        KLog.i("The Cache is empty");
                        callBack.onFailed();
                    }
                } else {
                    KLog.i("The Cache is empty");
                    callBack.onFailed();
                }
            }
        });
    }

    public void saveCache(final List<K> cacheList, final BaseCacheInfo info, final List<BaseCacheInfo> cacheInfos) {
        ThreadDispatcher.getDispatcher().post(new Runnable() {
            @Override
            public void run() {
                //先delete原有数据
                DBManager.getInstance().getDBManager().deleteAll(info.getClass());
                ByteArrayOutputStream bos;
                ObjectOutputStream oos = null;
                for (int i = 0; i < cacheList.size(); i++) {
                    try {
                        BaseCacheInfo mInfo = info.newInstance();
                        bos = new ByteArrayOutputStream();
                        oos = new ObjectOutputStream(bos);
                        oos.writeObject(cacheList.get(i));
                        byte[] bytes = bos.toByteArray();
                        String objStr = Base64.encodeToString(bytes, Base64.DEFAULT);
                        mInfo.setCacheStream(objStr);
                        cacheInfos.add(mInfo);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (oos != null) {
                            try {
                                oos.flush();
                                oos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                KLog.i("Save Cache: " + cacheInfos.get(0).getClass() + " size:" + cacheInfos.size());
                DBManager.getInstance().getDBManager().saveAll(cacheInfos);
            }
        });
    }

    public interface CacheCallBack<K> {
        void onSuccess(List<K> list);

        void onFailed();
    }
}
