package com.xiaoma.assistant.handler;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.assistant.Assistant;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.BisMvwSTKS;
import com.xiaoma.assistant.model.OptionWord;
import com.xiaoma.assistant.model.Serial;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.utils.AssetUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/16
 * Desc：选择的命令词
 */
public class AssistantWordHandler {

    public static final int RECOGNIZE_RESULT_ERROR = -1;//识别错误
    public static final int RECOGNIZE_RESULT_LAST = 100;//最后一个
    public static final int RECOGNIZE_RESULT_CANCEL = 101;//取消
    public static final int RECOGNIZE_RESULT_PREVIOUS = 110;//上一页
    public static final int RECOGNIZE_RESULT_NEXT = 111;//下一页
    public static final int RECOGNIZE_RESULT_INDEX_START = 8000;//8000以后的值用来作为切换页数指令（eg：第一页）的id
    public static final int RECOGNIZE_RESULT_INDEX_LAST = 1005;//
    public static final int RECOGNIZE_RESULT_SORT_BY_PRICE = 112;//按价格排序
    public static final int RECOGNIZE_RESULT_SORT_BY_DISTANCE = 113;//按距离排序
    public static final int RECOGNIZE_RESULT_SORT_BY_STAR_LEVEL = 114;//按星级排序
    public static final int RECOGNIZE_RESULT_SORT_BY_SCORE = 115;//按评分排序
    public static final int RECOGNIZE_RESULT_FILTER_BY_STAR_LEVEL = 116;//按指定星级过滤
    public static final int RECOGNIZE_RESULT_FILTER_BY_PRICE_MIN = 117;//最便宜的
    public static final int RECOGNIZE_RESULT_FILTER_BY_PRICE_MAX = 118;//最贵的
    public static final int RECOGNIZE_RESULT_FILTER_BY_SCORE_MAX = 119;//评分最高的
    public static final int RECOGNIZE_RESULT_FILTER_BY_DISTANCE_MIN = 120;//距离最近的
    public static final int RECOGNIZE_RESULT_FILTER_BY_DISTANCE_MAX = 121;//距离最远的
    public static final int RECOGNIZE_RESULT_FILTER_BY_HEAD_NUMBER = 122;//按手机号前三位过滤
    public static final int RECOGNIZE_RESULT_CONFIRM_OPERATION = 123;//确定
    public static final int RECOGNIZE_RESULT_CANCEL_OPERATION = 124;//取消
    public static final int RECOGNIZE_RESULT_CORRECT = 125;//纠正

    private Context context;
    static private List<OptionWord> recognitionWordList = new ArrayList<>();

    public AssistantWordHandler(Context context) {
        if (isWordListInited() || context == null) {
            return;
        }
        this.context = context;
        KLog.d("AssistantWordHandler init word allList from json file");
        String textFromAsset = AssetUtils.getTextFromAsset(context, "config/OptionWord.json");
        try {
            JSONObject jsonObject = new JSONObject(textFromAsset);
            String wordList = jsonObject.getString("options");
            Type type = new TypeToken<List<OptionWord>>() {
            }.getType();
            recognitionWordList = GsonHelper.fromJson(wordList, type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isWordListInited() {
        if (recognitionWordList == null) {
            return false;
        }
        return recognitionWordList.size() != 0;
    }

    public List<OptionWord> getWordList() {
        return recognitionWordList;
    }


    public int recognizeOptionIndex(String input) {
        KLog.json(input);

        BisMvwSTKS stks = GsonHelper.fromJson(input, BisMvwSTKS.class);
        if (stks != null && !TextUtils.isEmpty(stks.getBisMvwSTKS())) {
            if (stks.getSelectList() != null && stks.getSelectList().getList() != null &&
                    stks.getSelectList().getList().size() > 0) {
                String text = stks.getText();
                int id = stks.getSelectList().getList().get(0).getId() - 1;
                //参考IatScenario getDefaultCmd
                if (context == null) {
                    context = Assistant.getContext();
                }
                if (id <= 10 && text.contains(context.getString(R.string.assistant_word_key_p5))) {
                    return RECOGNIZE_RESULT_ERROR;
                }
                return id;
            }
        } else {
            LxParseResult lxParseResult = LxParseResult.fromJson(input);
            if (lxParseResult.isCmdAciton()) {
                String slot = lxParseResult.getSlots();
                try {
                    JSONObject slots = new JSONObject(slot);
                    if (slots != null && slots.has("name")) {
                        String name = slots.getString("name");
                        if (context.getString(R.string.assistant_cancel).equals(name) ||
                                context.getString(R.string.assistant_exit).equals(name) ||
                                context.getString(R.string.assistant_close).equals(name) ||
                                context.getString(R.string.assistant_back).equals(name)) {
                            return RECOGNIZE_RESULT_CANCEL;
                        } else if (context.getString(R.string.assistant_next_page).equals(name)) {
                            return RECOGNIZE_RESULT_NEXT;
                        } else if (context.getString(R.string.assistant_pre_page).equals(name)) {
                            return RECOGNIZE_RESULT_PREVIOUS;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (lxParseResult.isItemSelectAciton()) {
                String result = lxParseResult.getSlots();
                SelectSlots slots = GsonHelper.fromJson(result, SelectSlots.class);
                if (slots != null && slots.getSerial() != null && slots.getSerial().isSelectType()) {
                    Serial serial = slots.getSerial();
                    if (serial.isStartByZero()) {
                        int index = serial.getOffset() - 1;
                        if (index > RECOGNIZE_RESULT_LAST) {
                            index = RECOGNIZE_RESULT_LAST;
                        }
                        return index;
                    } else if (serial.isStartByMAX()) {
                        if (serial.getOffset() == 1) {
                            return RECOGNIZE_RESULT_LAST;
                        }
                    }
                }
            }
        }


        return RECOGNIZE_RESULT_ERROR;
    }


    class SelectSlots {
        private Serial serial;

        public Serial getSerial() {
            return serial;
        }

        public void setSerial(Serial serial) {
            this.serial = serial;
        }
    }
}
