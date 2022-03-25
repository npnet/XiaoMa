package com.xiaoma.cariflytek.ivw;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.iflytek.mvw.IMvwListener;
import com.iflytek.mvw.MvwSession;
import com.iflytek.speech.ISSErrors;
import com.iflytek.speech.NativeHandle;
import com.iflytek.speech.libissmvw;
import com.xiaoma.cariflytek.R;
import com.xiaoma.cariflytek.WakeUpInfo;
import com.xiaoma.cariflytek.iat.VrAidlServiceManager;
import com.xiaoma.cariflytek.iat.VrAidlServiceManager;
import com.xiaoma.utils.AssetUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.XmProperties;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;
import com.xiaoma.vr.VoiceConfigManager;
import com.xiaoma.vr.VrConfig;
import com.xiaoma.vr.ivw.BaseWakeup;
import com.xiaoma.vr.ivw.IHandleWakeupWord;
import com.xiaoma.vr.ivw.IWakeupWordRegister;
import com.xiaoma.vr.ivw.OnWakeUpListener;
import com.xiaoma.vr.recorder.RecordConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.xiaoma.vr.VoiceConfigManager.VOICE_WAKEUP_WORD;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/22
 * Desc:离线讯飞唤醒类
 */

public class LxXfWakeup extends BaseWakeup implements IHandleWakeupWord, IWakeupWordRegister {
    private static final String TAG = LxXfWakeup.class.getSimpleName();
    public MvwSession firstIvw;
    private String firstResPath = VrConfig.IFLY_TEK_RES + "/mvw/FirstRes";

    private boolean initSuccess = false;
    //语音助手用的唤醒词
    private List<String> xmWakeUpWordList = new ArrayList<>();
    //免唤醒的唤醒词
    private List<String> otherWakeUpWordList = new ArrayList<>();
    private IMvwListener firstMvwListener = new IMvwListener() {

        @Override
        public void onVwWakeup(int nMvwScene, int nMvwId, int nMvwScore, String lParam) {
            Log.d(TAG, String.format("WakeupLog onVwWakeup lParam:%s", lParam));
            onWakeup(nMvwScene, nMvwId, lParam);
        }

        @Override
        public void onVwInited(boolean state, int errId) {
            KLog.d(TAG, "onVwInit");
            initSuccess = state;
            if (state) {
                //firstIvw.setThreshold(1, 0, 50);
                setOneShotWakeUpWords();

                firstIvw.setParam(MvwSession.ISS_MVW_PARAM_AEC, MvwSession.ISS_MVW_PARAM_VALUE_OFF);
                firstIvw.setParam(MvwSession.ISS_MVW_PARAM_LSA, MvwSession.ISS_MVW_PARAM_VALUE_OFF);

                //设置唤醒词阈值(-3 到 3)
                //firstIvw.setParam("mvw_threshold_level", "3");
                initWordList();
            } else {
                if (errId == ISSErrors.REMOTE_EXCEPTION) {
                    firstIvw.initService();
                }
            }
        }
    };

    private void onWakeup(int nMvwScene, int nMvwId, String lParam) {
        if (!TextUtils.isEmpty(lParam)) {
            WakeUpInfo wakeUpParam = GsonHelper.fromJson(lParam, WakeUpInfo.class);
            wakeUpParam.setLeft(true);
            //默认唤醒词
            if (wakeUpParam == null) return;
            VrAidlServiceManager.getInstance().onWakeUp(wakeUpParam);
        }
    }


    @Override
    public void init(Context context) {
        this.context = context;
        if (firstIvw == null) {
            firstIvw = new MvwSession(context, firstMvwListener, firstResPath);
        }
    }


    @Override
    public void setOnWakeUpListener(OnWakeUpListener onWakeUpListener) {
        this.onWakeUpListener = onWakeUpListener;
    }

    private void initWordList() {
        List<String> wordList = new ArrayList<>();
        String defaultWord = VrConfig.defaultWakeupWord;
        String uid = VrAidlServiceManager.getInstance().getUid();
        String wakeupWord = XmProperties.build(uid).get(VrConfig.WAKE_UP_WORD_KEY, "");
        KLog.d(TAG, "wake up word json-----" + wakeupWord);
        if (!TextUtils.isEmpty(wakeupWord)) {
            wordList.add(GsonHelper.fromJson(wakeupWord, String.class));
            if (ListUtils.isEmpty(wordList)) {
                wordList.clear();
                wordList.add(0, defaultWord);
            }
        }

        otherWakeUpWordList.clear();
        addWakeUpWords();
        setMvwWord(wordList);
    }

    private void addWakeUpWords() {
        addWakeUpWords("GlobalKeyWord.json", "GlobalControlKeyWord.json");
    }

    private void addWakeUpWords(String... fileNames) {
        for (String fileName : fileNames) {
            String textFromAsset = AssetUtils.getTextFromAsset(context, fileName);
            List<String> strings = GsonHelper.fromJsonToList(textFromAsset, String[].class);
            if (strings != null && !strings.isEmpty()) {
                otherWakeUpWordList.addAll(strings);
            }
        }
    }


    private int setMvwWord(List<String> wordList) {
        xmWakeUpWordList.clear();
        xmWakeUpWordList.addAll(wordList);
        xmWakeUpWordList.remove(VrConfig.defaultWakeupWord);
        int srCode = setIvwWakeUpWord();
        Log.d("LxXfWakeUp", "set wake up code :" + srCode);
        return srCode;
    }


    private int setIvwWakeUpWord() {
        try {
            JSONObject objRoot = new JSONObject();
            JSONArray objArr = new JSONArray();
            //此处将设置的免唤醒词和自定义名字的唤醒词通过id来区分开来，在回调中好根据回调的id进行判断
            int nIndex = 0;
            Iterator<String> it = otherWakeUpWordList.iterator();
            while (it.hasNext()) {
                JSONObject objWord = new JSONObject();
                objWord.put("KeyWordId", nIndex);
                objWord.put("KeyWord", it.next());
                objWord.put("DefaultThreshold40", 0);
                nIndex++;
                objArr.put(objWord);
            }

            //1000以上为自定义唤醒词，比如奔腾你好之类的
            nIndex = 0;
            Iterator<String> itw = xmWakeUpWordList.iterator();
            while (itw.hasNext()) {
                JSONObject objWord = new JSONObject();
                String wakeUpWord = itw.next();
                wakeUpWord = replaceOneShotWord(wakeUpWord);
                objWord.put("KeyWordId", nIndex + otherWakeUpWordList.size());
                objWord.put("KeyWord", wakeUpWord);
                objWord.put("DefaultThreshold40", 0);
                nIndex++;
                objArr.put(objWord);
            }
            objRoot.put("Keywords", objArr);
            KLog.json(objRoot.toString());
            //讯飞建议将自定义唤醒词设置到场景64中
            int code = firstIvw.setMvwKeyWords(64, objRoot.toString());
            startWakeup();
            Log.d("LxXfWakeUp", "set wake up code: " + code);
            return code;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("LxXfWakeUp", "set wake up code exception e: " + e.getMessage());
        }
        return -1;
    }


    @Override
    public void startWakeup() {
        KLog.d(TAG, "startWakeup");
        startIvwWakeup();
        startOneShotWakeup();
    }

    private void startIvwWakeup() {
        if (firstIvw != null) {
            int id = firstIvw.start(VrConfig.isOneShot ? VrConfig.MvwSceneOneshot : VrConfig.MvwSceneNormal);
            Log.d(TAG, String.format("WakeupLog class:%s  instance:ivw1 start code:%s", getClass().getSimpleName(), id));
            if (id != ISSErrors.ISS_SUCCESS) {
                Log.e("LxXfWakeUp", "firstIvw SessionStart error");
            }
        } else {
            Log.d(TAG, String.format("WakeupLog class:%s  instance:ivw1 is null", getClass().getSimpleName()));
        }
    }


    private void startOneShotWakeup() {
//        LxIatManager.getInstance().startOneShotIat(false);
    }


    @Override
    public void stopWakeup() {
        KLog.d(TAG, "stopWakeup");
        if (firstIvw != null) {
            firstIvw.stop();
        }
    }


    @Override
    public void appendAudioData(byte[] buffer, int start, int byteCount) {
        appendIvwAudioData(buffer);
    }

    @Override
    public void appendAudioData(byte[] buffer, byte[] bufferLeft, byte[] bufferRight) {
        //双通道
        appendIvwAudioData(bufferRight);
    }


    public void appendIvwAudioData(byte[] buffer) {
        if (firstIvw != null) {
            firstIvw.appendAudioData(buffer);
        }
    }


    public void appendAudioData(Bundle buffer) {
        byte[] buffers = buffer.getByteArray(RecordConstants.BUFFER_ALL);
        appendIvwAudioData(buffers);
    }


    @Override
    public boolean setWakeupWord(String word) {
        List<String> wordList = new ArrayList<>();
        if (word.length() >= 2 && word.length() <= 6) {
            wordList.add(word);
        } else {
            return false;
        }
        int code = setMvwWord(wordList);
        if (code == 0) {
            String cacheWordList = GsonHelper.toJson(wordList);
            KLog.json(cacheWordList);
            String uid = VrAidlServiceManager.getInstance().getUid();
            XmProperties.build(uid).put(VrConfig.WAKE_UP_WORD_KEY, wordList.get(0));
        }
        return code == 0;
    }


    @Override
    public void setIvwThreshold(int curThresh) {

    }


    @Override
    public boolean resetWakeupWord() {
        List<String> wordList = new ArrayList<>();
        if (!VrConfig.hasCustomizeWord) {
            String defaultWord = VrConfig.defaultWakeupWord;
            wordList.add(defaultWord);
        }

        //初始化缓存中的唤醒词
        String uid = VrAidlServiceManager.getInstance().getUid();
        XmProperties.build(uid).put(VrConfig.WAKE_UP_WORD_KEY, "");

        int code = setMvwWord(wordList);
        return code == 0;
    }

    @Override
    public List<String> getWakeupWord() {
        ArrayList<String> wakeUpWord = new ArrayList<>();
        wakeUpWord.addAll(xmWakeUpWordList);
        return wakeUpWord;
    }


    private String replaceOneShotWord(String wakeUpWords) {
        return wakeUpWords.replaceAll("0", "零")
                .replaceAll("1", "一")
                .replaceAll("2", "二")
                .replaceAll("3", "三")
                .replaceAll("4", "四")
                .replaceAll("5", "五")
                .replaceAll("6", "六")
                .replaceAll("7", "七")
                .replaceAll("8", "八")
                .replaceAll("9", "九");
    }


    private int addOneShotWakeupWord(List<String> wordList) {
        otherWakeUpWordList.addAll(wordList);
        int srCode = setIvwWakeUpWord();
        KLog.d(TAG, "set iat wake up code :" + srCode);
        KLog.d(TAG, "otherWakeUpWordList:" + otherWakeUpWordList);
        return srCode;
    }


    private int removeOneShotWakeupWord(List<String> wordList) {
        for (String word : wordList) {
            otherWakeUpWordList.remove(word);
        }
        int srCode = setIvwWakeUpWord();
        KLog.d(TAG, "set iat wake up code :" + srCode);
        KLog.d(TAG, "otherWakeUpWordList:" + otherWakeUpWordList);
        return srCode;
    }


    @Override
    public boolean registerOneShotWakeupWord(List<String> wakeupWord) {
        List<String> duplicateWakeupWord = removeDuplicate(wakeupWord);
        KLog.d(TAG, "registerOneShotWakeupWord : " + wakeupWord);
        if (ListUtils.isEmpty(duplicateWakeupWord)) {
            return false;
        }
        int code = addOneShotWakeupWord(duplicateWakeupWord);
        return code == 0;
    }

    private int setOneShotWakeUpWords() {
        try {
            JSONObject objRoot = new JSONObject();
            JSONArray objArr = new JSONArray();
            List<String> oneshotWords = new ArrayList<>();
            oneshotWords.add(context.getString(R.string.call_for));
            oneshotWords.add(context.getString(R.string.navigate_to));
            oneshotWords.add(context.getString(R.string.want_to_listen));
            oneshotWords.add(context.getString(R.string.like_to_listen));
            oneshotWords.add(context.getString(R.string.want_to_play));
            oneshotWords.add(context.getString(R.string.want_to_query));
            for (int nIndex = 0; nIndex < oneshotWords.size(); nIndex++) {
                JSONObject objWord = new JSONObject();
                String word = oneshotWords.get(nIndex);
                objWord.put("KeyWordId", nIndex);
                objWord.put("KeyWord", word);
                objWord.put("DefaultThreshold40", 0);
                objArr.put(objWord);
            }
            objRoot.put("Keywords", objArr);
            objRoot.put("KeywordsType", 1);
            return setWakeUpWords(objRoot.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    private int setWakeUpWords(final String json) {
        if (TextUtils.isEmpty(json)) return -1;
        int srMvwKeyWordCode = -1;
        if (initSuccess) {
            srMvwKeyWordCode = firstIvw.setMvwKeyWords(128, json);
            startWakeup();
            KLog.json(json);
        }
        KLog.d(TAG, "set wake up code: " + srMvwKeyWordCode);
        return srMvwKeyWordCode;
    }

    @Override
    public boolean unregisterOneShotWakeupWord(List<String> wakeupWord) {
        KLog.d(TAG, "unregisterOneShotWakeupWord : " + wakeupWord);
        if (ListUtils.isEmpty(wakeupWord)) {
            return false;
        }
        int code = removeOneShotWakeupWord(wakeupWord);
        return code == 0;
    }


    //去除重复唤醒词
    public static List<String> removeDuplicate(List<String> list) {
        Set set = new LinkedHashSet<String>();
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return list;
    }


    public void destroy() {
        //ivw destroy
    }

    public boolean destroyIvw() {
        if (firstIvw == null)
            return false;
        firstIvw.stop();
        firstIvw.release();
        int result4 = libissmvw.stop(new NativeHandle());
        int result5 = libissmvw.destroy(new NativeHandle());
        firstIvw = null;
        KLog.e(TAG, "destroy xf ivw --->" + result4 + ", " + result5);
        return 0 == result4 && 0 == result5;
    }
}



