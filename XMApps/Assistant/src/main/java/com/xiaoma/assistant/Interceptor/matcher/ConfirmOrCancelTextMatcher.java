package com.xiaoma.assistant.Interceptor.matcher;

import android.text.TextUtils;
import android.util.Log;
import com.google.gson.reflect.TypeToken;
import com.xiaoma.assistant.Interceptor.manager.InterceptorManager;
import com.xiaoma.assistant.model.ConfirmWord;
import com.xiaoma.utils.AssetUtils;
import com.xiaoma.utils.GsonHelper;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiuboxiang on 2019/4/12 16:54
 * Desc:
 */
public class ConfirmOrCancelTextMatcher implements TextMatcher {

    private List<ConfirmWord> recognitionWordList = new ArrayList<>();
    private boolean confirm;

    public ConfirmOrCancelTextMatcher() {
        String queryTextFromAsset = AssetUtils.getTextFromAsset(InterceptorManager.getInstance().getContext(), "config/ConfirmWord.json");
        try {
            JSONObject jsonObject = new JSONObject(queryTextFromAsset);
            String wordList = jsonObject.getString("options");
            Type type = new TypeToken<List<ConfirmWord>>() {
            }.getType();
            recognitionWordList = GsonHelper.fromJson(wordList, type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean match(String text) {
        int result = recognizeQueryViolation(text);
        confirm = (result == 0);
        Log.d("QBX", "ConfirmOrCancelTextMatcher: " + confirm);
        return result >= 0;
    }

    private int recognizeQueryViolation(String input) {
        if (recognitionWordList == null || recognitionWordList.size() <= 0) {
            return -1;
        }

        for (int i = 0; i < recognitionWordList.size(); i++) {
            if (isInputContainsKeywords(input, recognitionWordList.get(i))) {
                return recognitionWordList.get(i).getAgree();
            }
        }
        return -1;
    }

    private boolean isInputContainsKeywords(String input, ConfirmWord optionWord) {
        if (optionWord == null || TextUtils.isEmpty(optionWord.getKeyWord())) {
            return false;
        }
        String keyWord = optionWord.getKeyWord();
        if (optionWord.isStartWith()) {
            return input.startsWith(keyWord);
        } else {
            String[] split = keyWord.split("\\|");
            if (split.length == 0) {
                return false;
            }
            boolean contain = false;
            for (String aSplit : split) {
                if (aSplit.isEmpty()) {
                    continue;
                }
                if (input.equals(aSplit)) {
                    contain = true;
                    break;
                }
            }
            return contain;
        }
    }

    public boolean isConfirm() {
        return confirm;
    }

    public ConfirmOrCancelTextMatcher addKeyword(String keyword, boolean isConfirm) {
        recognitionWordList.add(new ConfirmWord(keyword, isConfirm ? 0 : 1));
        return this;
    }

    public ConfirmOrCancelTextMatcher addStartWithKeyword(String keyword, boolean isConfirm) {
        recognitionWordList.add(new ConfirmWord(keyword, isConfirm, true));
        return this;
    }

}
