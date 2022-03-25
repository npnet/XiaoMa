package com.xiaoma.openiflytek.tts;

import android.os.Bundle;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.jakewharton.disklrucache.DiskLruCache;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.utils.MD5Utils;
import com.xiaoma.utils.constant.VrConstants;
import com.xiaoma.utils.log.KLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/6/11
 * 语音合成缓存
 */

class OpenTtsCacheHandler {
    private static final String CACHE_DIR = VrConstants.TTS_CACHE_DIR;
    private static final int CACHE_SIZE = 30 * 1024 * 1024;
    private static final int CACHE_INDEX = 0;
    //流缓冲区最大的空间
    private static final int MAX_BUFFER_SIZE = 512 * 1024;
    private static DiskLruCache sDiskLruCache;

    static {
        try {
            sDiskLruCache = DiskLruCache.open(new File(CACHE_DIR),
                    ConfigManager.ApkConfig.getBuildVersionCode(),
                    1, CACHE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SpeechSynthesizer mSpeechSynthesizer;

    OpenTtsCacheHandler(SpeechSynthesizer mSpeechSynthesizer) {
        this.mSpeechSynthesizer = mSpeechSynthesizer;
    }

    public int put(String content, String voicer, final SynthesizerListener listener) {
        if (sDiskLruCache == null) {
            listener.onCompleted(new SpeechError(new IOException("lru cache open failed")));
            return -1;
        }
        final String cacheKey = makeCacheKey(content, voicer);
        File cacheTmpFile = null;
        try {
            cacheTmpFile = File.createTempFile(cacheKey, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (cacheTmpFile == null)
            return ErrorCode.ERROR_TTS_CREATE_HANDLE_FAILED;
        return mSpeechSynthesizer.synthesizeToUri(content, cacheTmpFile.getAbsolutePath(),
                new CacheSynthesizerListener(cacheKey, cacheTmpFile, listener));
    }

    public boolean remove(String content, String voicer) {
        try {
            return sDiskLruCache != null && sDiskLruCache.remove(makeCacheKey(content, voicer));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean clear() {
        try {
            sDiskLruCache.delete();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public FileInputStream get(String content, String voicer) {
        if (sDiskLruCache == null)
            return null;
        String key = makeCacheKey(content, voicer);
        try {
            DiskLruCache.Snapshot snapshot = sDiskLruCache.get(key);
            if (snapshot != null)
                return (FileInputStream) snapshot.getInputStream(CACHE_INDEX);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String makeCacheKey(String content, String voicer) {
        String src = voicer + "_" + content;
        return MD5Utils.getMD5String(src);
    }

    private class CacheSynthesizerListener implements SynthesizerListener {
        private String cacheKey;
        private File cacheTmpFile;
        private SynthesizerListener listener;

        CacheSynthesizerListener(String cacheKey, File cacheTmpFile, SynthesizerListener listener) {
            this.cacheKey = cacheKey;
            this.cacheTmpFile = cacheTmpFile;
            this.listener = listener;
        }

        @Override
        public void onSpeakBegin() {
            listener.onSpeakBegin();
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            listener.onBufferProgress(percent, beginPos, endPos, info);
        }

        @Override
        public void onSpeakPaused() {
            listener.onSpeakPaused();
        }

        @Override
        public void onSpeakResumed() {
            listener.onSpeakResumed();
        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {
            listener.onSpeakProgress(i, i1, i2);
        }

        @Override
        public void onCompleted(SpeechError speechError) {
            if (speechError == null) {
                FileInputStream input = null;
                DiskLruCache.Editor editor = null;
                try {
                    editor = sDiskLruCache.edit(cacheKey);
                    OutputStream output = editor.newOutputStream(CACHE_INDEX);
                    input = new FileInputStream(cacheTmpFile);
                    byte[] buf = new byte[Math.min(input.available(), MAX_BUFFER_SIZE)];
                    int len;
                    while ((len = input.read(buf)) > 0) {
                        output.write(buf, 0, len);
                    }
                    output.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    speechError = new SpeechError(e);
                } finally {
                    try {
                        if (input != null) {
                            input.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (!cacheTmpFile.delete()) {
                        KLog.w("tts tmp file delete failed");
                    }
                    try {
                        if (editor != null) {
                            editor.commit();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            listener.onCompleted(speechError);
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
            listener.onEvent(i, i1, i2, bundle);
        }
    }
}
