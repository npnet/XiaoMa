package com.iflytek.speech.tts;

import android.media.AudioFormat;
import android.media.AudioTrack;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.iflytek.speech.ITtsListener;
import com.iflytek.speech.NativeHandle;
import com.iflytek.speech.libisstts;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/27
 * Desc：
 */
public class TtsPlayer implements ITtsListener {
    private static int cnt = 0;
    private static int iInitState = -1;
    private String tag;
    private static final int mSampleRateInHz = 16000;
    private static final int mChannelConfig = 4;
    private static final int mAudioFormat = 2;
    private final Lock mAudioTrackLock;
    private AudioTrack mAudioTrack;
    private int mMinBufferSizeInBytes;
    private ITTSListener mITTSListener;
    private int nPreTextIndex;
    private boolean mOnDataReadyFlag;
    private final NativeHandle mNativeHandle;
    private int mAudioTrackSteamState;
    private final Object mWorkingThreadSyncObj;
    private final TtsPlayer.AudioWriteWorkingFunc mAudioWriteWorkingFunc;
    private Thread mThreadAudioWrite;
    private String ttsID;

    public TtsPlayer(String strResDir) {
        this.tag = "TtsPlayer_" + cnt;
        this.mAudioTrackLock = new ReentrantLock();
        this.mAudioTrack = null;
        this.mMinBufferSizeInBytes = 0;
        this.mITTSListener = null;
        this.nPreTextIndex = -1;
        this.mOnDataReadyFlag = false;
        this.mNativeHandle = new NativeHandle();
        this.mAudioTrackSteamState = 0;
        this.mWorkingThreadSyncObj = new Object();
        this.mAudioWriteWorkingFunc = new TtsPlayer.AudioWriteWorkingFunc();
        this.mThreadAudioWrite = null;
        Log.d(this.tag, "new TtsPlayer");
        Log.d(this.tag, "initRes start");
        iInitState = libisstts.initRes(strResDir, 1);
        Log.d(this.tag, "initRes end, ret is " + iInitState);
    }

    public void setListener(ITTSListener iTTSListener) {
        this.mITTSListener = iTTSListener;
    }

    public int Init(int streamType) {
        ++cnt;
        Log.d(this.tag, "Init");

        try {
            libisstts.create(this.mNativeHandle, this);
            if (this.mNativeHandle.err_ret != 0) {
                Log.d(this.tag, "libisstts.create failed. ret = " + this.mNativeHandle.err_ret);
                return this.mNativeHandle.err_ret;
            } else {
                this.mMinBufferSizeInBytes = AudioTrack.getMinBufferSize(16000, AudioFormat.CHANNEL_OUT_FRONT_LEFT, AudioFormat.ENCODING_PCM_16BIT);
                Log.d(this.tag, "mMinBufferSizeInBytes=" + this.mMinBufferSizeInBytes + ".");
                if (this.mMinBufferSizeInBytes <= 0) {
                    Log.e(this.tag, "Error: AudioTrack.getMinBufferSize(16000, 4, 2) ret " + this.mMinBufferSizeInBytes);
                    return 10106;
                } else {
                    if (this.mAudioTrack == null) {
                        this.mAudioTrack = new AudioTrack(streamType, 16000, AudioFormat.CHANNEL_OUT_FRONT_LEFT, AudioFormat.ENCODING_PCM_16BIT, this.mMinBufferSizeInBytes, AudioTrack.MODE_STREAM);
                        if (this.mAudioTrack == null || this.mAudioTrack.getState() != 1) {
                            Log.e(this.tag, "Error: Can't init AudioRecord!");
                            return -1;
                        }

                        Log.d(this.tag, "new AudioTrack(streamType=" + streamType + ")");
                        this.mAudioTrack.setNotificationMarkerPosition(0);
                    }

                    this.mAudioTrackSteamState = 0;
                    if (this.mThreadAudioWrite == null) {
                        this.mAudioWriteWorkingFunc.clearExitFlag();
                        this.mThreadAudioWrite = new Thread(this.mAudioWriteWorkingFunc, "mThreadAudioWrite");
                        this.mThreadAudioWrite.start();
                    }

                    return 0;
                }
            }
        } catch (IllegalArgumentException var3) {
            return 10106;
        }
    }

    public int SetParam(int nParam, int nValue) {
        Log.d(this.tag, "SetParam");
        if (this.mNativeHandle.native_point == 0L) {
            return 10000;
        } else {
            libisstts.setParam(this.mNativeHandle, nParam, nValue);
            return this.mNativeHandle.err_ret;
        }
    }

    public int SetParamEx(int nParam, String StrValue) {
        Log.d(this.tag, "SetParamEx");
        if (this.mNativeHandle.native_point == 0L) {
            return 10000;
        } else {
            libisstts.setParamEx(this.mNativeHandle, nParam, StrValue);
            return this.mNativeHandle.err_ret;
        }
    }

    public int Start(String text, String id) {
        Log.d("XmTtsManager", "TtsPlayer Start ");
        if (this.mAudioTrack != null && this.mThreadAudioWrite != null && this.mNativeHandle.native_point != 0L) {
            this.Stop();
            if (this.mAudioTrackSteamState == 3) {
                Log.d("XmTtsManager", "TtsPlayer Start error AudioTrackSteamState = 3");
                return 10000;
            } else {
                this.nPreTextIndex = -1;
                this.mOnDataReadyFlag = false;
                this.ttsID = id;
                Log.d("XmTtsManager", String.format("TtsPlayer Start text %s", text));
                text = handlePronunciation(text);
                libisstts.start(this.mNativeHandle, text);
                if (this.mNativeHandle.err_ret != 0) {
                    return this.mNativeHandle.err_ret;
                } else {
                    this.mAudioTrackSteamState = 1;
                    this.mAudioTrackLock.lock();
                    if (this.mAudioTrack != null) {
                        this.mAudioTrack.setNotificationMarkerPosition(0);
                        this.mAudioTrack.pause();
                        this.mAudioTrack.flush();
                        this.mAudioTrack.stop();
                        this.mAudioTrack.play();
                    }

                    this.mAudioTrackLock.unlock();
                    Object var2 = this.mWorkingThreadSyncObj;
                    synchronized (this.mWorkingThreadSyncObj) {
                        this.mWorkingThreadSyncObj.notifyAll();
                        return 0;
                    }
                }
            }
        } else {
            Log.d("XmTtsManager", "TtsPlayer Start error ");
            if (this.mAudioTrack == null) {
                Log.d("XmTtsManager", "mAudioTrack == null");
            } else if (this.mThreadAudioWrite == null) {
                Log.d("XmTtsManager", "mThreadAudioWrite == null");
            } else if (this.mNativeHandle.native_point == 0L) {
                Log.d("XmTtsManager", "mNativeHandle.native_point == 0");
            } else {
                Log.d("XmTtsManager", " error 10000");
            }

            return 10000;
        }
    }

    private String handlePronunciation(String strTxt) {
        Map<Character, Pair<Character, Integer>> map = new HashMap<Character, Pair<Character, Integer>>();
        map.put('ā', Pair.create('a', 1));
        map.put('á', Pair.create('a', 2));
        map.put('ǎ', Pair.create('a', 3));
        map.put('à', Pair.create('a', 4));
        map.put('ō', Pair.create('o', 1));
        map.put('ó', Pair.create('o', 2));
        map.put('ǒ', Pair.create('o', 3));
        map.put('ò', Pair.create('o', 4));
        map.put('ē', Pair.create('e', 1));
        map.put('é', Pair.create('e', 2));
        map.put('ě', Pair.create('e', 3));
        map.put('è', Pair.create('e', 4));
        map.put('ī', Pair.create('i', 1));
        map.put('í', Pair.create('i', 2));
        map.put('ǐ', Pair.create('i', 3));
        map.put('ì', Pair.create('i', 4));
        map.put('ū', Pair.create('u', 1));
        map.put('ú', Pair.create('u', 2));
        map.put('ǔ', Pair.create('u', 3));
        map.put('ù', Pair.create('u', 4));
        map.put('ǖ', Pair.create('ü', 1));
        map.put('ǘ', Pair.create('ü', 2));
        map.put('ǚ', Pair.create('ü', 3));
        map.put('ǜ', Pair.create('ü', 4));
        String newStr = "";
        StringBuffer sb = new StringBuffer();

        //weì转换成  烫[=wei4]
        Pattern pattern = Pattern.compile("[a-z]?[áǎàōóǒòēéěèīíǐìūúǔùǖǘǚǜ][a-z]?");
        Matcher m = pattern.matcher(strTxt);
        while (m.find()) {
            String matchStr = m.group();
            Pattern pattern1 = Pattern.compile("[áǎàōóǒòēéěèīíǐìūúǔùǖǘǚǜ]");
            Matcher m1 = pattern1.matcher(matchStr);
            if (m1.find()) {
                Character m1MatchStr = m1.group().charAt(0);
                int pitch = map.get(m1MatchStr).second;
                Character word = map.get(m1MatchStr).first;
                matchStr = m1.replaceAll(word.toString());
                String replace = "烫[=" + matchStr + pitch + "]";
                m.appendReplacement(sb, replace);
            }
            Log.d(tag, "newStr: " + newStr);
        }
        m.appendTail(sb);
        newStr = sb.toString();
        sb.setLength(0);

        //(yin1)转换成[=yin1]
        pattern = Pattern.compile("[\\(（][a-z]+[0-5][\\)）]");
        m = pattern.matcher(newStr);
        while (m.find()) {
            String matchStr = m.group();
            matchStr = matchStr.replaceAll("\\(|（", "[=");
            matchStr = matchStr.replaceAll("\\)|）", "]");
            m.appendReplacement(sb, matchStr);
            Log.d(tag, "newStr: " + newStr);
        }
        m.appendTail(sb);
        newStr = sb.toString();
        return newStr;
    }

    public int Pause() {
        Log.d(this.tag, "Pause");
        if (this.mAudioTrack != null && this.mThreadAudioWrite != null && this.mNativeHandle.native_point != 0L) {
            if (this.mAudioTrackSteamState == 3) {
                return 10000;
            } else if (this.mAudioTrackSteamState == 0) {
                return 10000;
            } else if (this.mAudioTrackSteamState == 2) {
                return 0;
            } else {
                this.mAudioTrackSteamState = 2;
                this.mAudioTrackLock.lock();
                if (this.mAudioTrack != null) {
                    this.mAudioTrack.pause();
                }

                this.mAudioTrackLock.unlock();
                Object var1 = this.mWorkingThreadSyncObj;
                synchronized (this.mWorkingThreadSyncObj) {
                    this.mWorkingThreadSyncObj.notifyAll();
                    return 0;
                }
            }
        } else {
            return 10000;
        }
    }

    public int Resume() {
        Log.d(this.tag, "Resume");
        if (this.mAudioTrack != null && this.mThreadAudioWrite != null && this.mNativeHandle.native_point != 0L) {
            if (this.mAudioTrackSteamState == 3) {
                return 10000;
            } else if (this.mAudioTrackSteamState == 0) {
                return 10000;
            } else if (this.mAudioTrackSteamState == 1) {
                return 0;
            } else {
                this.mAudioTrackSteamState = 1;
                this.mAudioTrackLock.lock();
                if (this.mAudioTrack != null) {
                    this.mAudioTrack.play();
                }

                this.mAudioTrackLock.unlock();
                Object var1 = this.mWorkingThreadSyncObj;
                synchronized (this.mWorkingThreadSyncObj) {
                    this.mWorkingThreadSyncObj.notifyAll();
                    return 0;
                }
            }
        } else {
            return 10000;
        }
    }

    public int Stop() {
        Log.d(this.tag, "Stop");
        if (this.mAudioTrack != null && this.mThreadAudioWrite != null && this.mNativeHandle.native_point != 0L) {
            if (this.mAudioTrackSteamState == 3) {
                return 10000;
            } else if (this.mAudioTrackSteamState == 0) {
                return 0;
            } else {
                this.mAudioTrackSteamState = 0;
                libisstts.stop(this.mNativeHandle);
                this.mAudioTrackLock.lock();
                if (this.mAudioTrack != null) {
                    Log.d(this.tag, "mAudioTrack.pause() before");
                    this.mAudioTrack.pause();
                    Log.d(this.tag, "mAudioTrack.pause() after");
                    Log.d(this.tag, "mAudioTrack.flush() before");
                    this.mAudioTrack.flush();
                    Log.d(this.tag, "mAudioTrack.flush() after");
                    Log.d(this.tag, "mAudioTrack.stop() before");
                    this.mAudioTrack.stop();
                    Log.d(this.tag, "mAudioTrack.stop() after");
                }

                this.mAudioTrackLock.unlock();
                Object var1 = this.mWorkingThreadSyncObj;
                synchronized (this.mWorkingThreadSyncObj) {
                    this.mWorkingThreadSyncObj.notifyAll();
                }

                if (this.mITTSListener != null) {
                    this.mITTSListener.onTTSPlayInterrupted(ttsID);
                }

                return 0;
            }
        } else {
            return 10000;
        }
    }

    public int Release() {
        Log.d(this.tag, "Release");
        this.mAudioTrackSteamState = 3;
        this.mAudioWriteWorkingFunc.setExitFlag();
        if (this.mThreadAudioWrite != null) {
            try {
                this.mThreadAudioWrite.join();
                this.mThreadAudioWrite = null;
            } catch (InterruptedException var2) {
                var2.printStackTrace();
            }
        }

        libisstts.destroy(this.mNativeHandle);
        return 0;
    }

    public int GetInitState() {
        return iInitState;
    }

    public void onDataReady() {
        Log.d(this.tag, "onDataReady");
        this.mOnDataReadyFlag = true;
        if (this.mITTSListener != null) {
            this.mITTSListener.onTTSPlayBegin(ttsID);
        }

        Object var1 = this.mWorkingThreadSyncObj;
        synchronized (this.mWorkingThreadSyncObj) {
            this.mWorkingThreadSyncObj.notifyAll();
        }
    }

    public void onProgress(int nTextIndex, int nTextLen) {
        if (this.nPreTextIndex < nTextIndex) {
            Log.d(this.tag, "onProgress(" + nTextIndex + ", " + nTextLen + ")");
            if (this.mITTSListener != null) {
                this.mITTSListener.onTTSProgressReturn(nTextIndex, nTextLen);
            }
        }

        this.nPreTextIndex = nTextIndex;
    }

    private class AudioWriteWorkingFunc implements Runnable {
        private static final String tag = "AudioWriteWorkingFunc";
        private boolean mExitFlag;

        private AudioWriteWorkingFunc() {
            this.mExitFlag = false;
        }

        public void clearExitFlag() {
            this.mExitFlag = false;
        }

        public void setExitFlag() {
            this.mExitFlag = true;
            synchronized (TtsPlayer.this.mWorkingThreadSyncObj) {
                TtsPlayer.this.mWorkingThreadSyncObj.notifyAll();
            }
        }

        public void run() {
            Log.d("AudioWriteWorkingFunc", "AudioWriteWorkingFunc In.");
            if (TtsPlayer.this.mAudioTrack != null && TtsPlayer.this.mNativeHandle.native_point != 0L && TtsPlayer.this.mMinBufferSizeInBytes != 0) {
                byte[] buffer = new byte[TtsPlayer.this.mMinBufferSizeInBytes];
                Log.d("AudioWriteWorkingFunc", "mBufferOnceSizeInBytes is " + TtsPlayer.this.mMinBufferSizeInBytes);

                while (true) {
                    while (!this.mExitFlag) {
                        if (TtsPlayer.this.mAudioTrackSteamState == 1 && TtsPlayer.this.mOnDataReadyFlag) {
                            int[] buffer_size = new int[1];
                            libisstts.getAudioData(TtsPlayer.this.mNativeHandle, buffer, TtsPlayer.this.mMinBufferSizeInBytes, buffer_size);
                            if (TtsPlayer.this.mNativeHandle.err_ret == 10004) {
                                Log.d("AudioWriteWorkingFunc", "libisstts.getAudioData Completed.");
                                TtsPlayer.this.mAudioTrackSteamState = 0;
                                if (TtsPlayer.this.mITTSListener != null) {
                                    TtsPlayer.this.mAudioTrackLock.lock();
                                    Log.d("AudioWriteWorkingFunc", "mAudioTrack.stop() before");
                                    TtsPlayer.this.mAudioTrack.stop();
                                    Log.d("AudioWriteWorkingFunc", "mAudioTrack.stop() after");
                                    Log.d("AudioWriteWorkingFunc", "mAudioTrack.flush() before");
                                    TtsPlayer.this.mAudioTrack.flush();
                                    Log.d("AudioWriteWorkingFunc", "mAudioTrack.flush() after");
                                    TtsPlayer.this.mAudioTrackLock.unlock();
                                    Log.d("AudioWriteWorkingFunc", "mITTSListener.onTTSPlayCompleted() before");
                                    TtsPlayer.this.mITTSListener.onTTSPlayCompleted(ttsID);
                                    Log.d("AudioWriteWorkingFunc", "mITTSListener.onTTSPlayCompleted() after");
                                }
                            } else if (buffer_size[0] > 0) {
                                TtsPlayer.this.mAudioTrackLock.lock();
                                int ret = TtsPlayer.this.mAudioTrack.write(buffer, 0, buffer_size[0]);
                                TtsPlayer.this.mAudioTrackLock.unlock();
                                if (ret < 0) {
                                    Log.e("AudioWriteWorkingFunc", "mAudioTrack.write(size=" + buffer_size[0] + ") ret " + ret);
                                    TtsPlayer.this.mAudioTrackSteamState = 0;
                                    Thread.yield();
                                }
                            } else {
                                synchronized (TtsPlayer.this.mWorkingThreadSyncObj) {
                                    try {
                                        Log.d("AudioWriteWorkingFunc", "Before wait(5)");
                                        TtsPlayer.this.mWorkingThreadSyncObj.wait(5L);
                                        Log.d("AudioWriteWorkingFunc", "After wait(5)");
                                    } catch (InterruptedException var8) {
                                        var8.printStackTrace();
                                    }
                                }
                            }
                        } else {
                            synchronized (TtsPlayer.this.mWorkingThreadSyncObj) {
                                if (TtsPlayer.this.mAudioTrackSteamState != 1 || !TtsPlayer.this.mOnDataReadyFlag) {
                                    try {
                                        TtsPlayer.this.mWorkingThreadSyncObj.wait(5L);
                                    } catch (InterruptedException var7) {
                                        var7.printStackTrace();
                                    }
                                }
                            }
                        }
                    }

                    Log.d("AudioWriteWorkingFunc", "mExitFlag is true");
                    break;
                }
            } else {
                Log.e("AudioWriteWorkingFunc", "mAudioTrack==null || mNativeHandle.native_point == 0 || mMinBufferSizeInBytes==0, this should never happen.");
            }

            TtsPlayer.this.mAudioTrackLock.lock();
            if (TtsPlayer.this.mAudioTrack != null) {
                TtsPlayer.this.mAudioTrack.release();
                TtsPlayer.this.mAudioTrack = null;
            }

            TtsPlayer.this.mAudioTrackLock.unlock();
            Log.d("AudioWriteWorkingFunc", "AudioWriteWorkingFunc Out.");
        }
    }

    private class AudioTrackSteamState {
        public static final int STREAM_STOPPED = 0;
        public static final int STREAM_RUNNING = 1;
        public static final int STREAM_PAUSED = 2;
        public static final int STREAM_RELEASED = 3;

        private AudioTrackSteamState() {
        }
    }
}

