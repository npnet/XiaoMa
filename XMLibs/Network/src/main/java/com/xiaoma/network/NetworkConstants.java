package com.xiaoma.network;

import com.xiaoma.network.cache.CacheEntity;
import com.xiaoma.network.cache.CacheMode;

import java.util.logging.Level;

/**
 * Created by youthyj on 2018/9/7.
 */
public class NetworkConstants {
    private NetworkConstants() throws Exception {
        throw new Exception();
    }

    public static boolean NEED_TOKEN = true;
    public static final String LOG_NAME = "XmHttp";
    public static final Level LOG_LEVEL = Level.INFO;
    public static final int RETRY_COUNT = 3; //默认请求次数
    public static final long CACHE_TIME = CacheEntity.CACHE_NEVER_EXPIRE; //全局的缓存过期时间
    public static final CacheMode CACHE_MODE = CacheMode.REQUEST_FAILED_READ_CACHE; //全局的缓存模式
    public static final String CLIENT_PASSWORD = "XiaoMaLiXing001";
}

