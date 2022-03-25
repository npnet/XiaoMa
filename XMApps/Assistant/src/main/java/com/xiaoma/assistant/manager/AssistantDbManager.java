package com.xiaoma.assistant.manager;

import android.util.Log;
import com.xiaoma.assistant.model.semantic.OpenSemantic;
import com.xiaoma.db.DBManager;
import com.xiaoma.db.IDatabase;
import com.xiaoma.login.LoginManager;
import com.xiaoma.utils.ListUtils;
import java.util.List;

/**
 * Created by qiuboxiang on 2019/7/29 19:35
 * Desc:
 */
public class AssistantDbManager {

    public final String TAG = getClass().getSimpleName();
    private static AssistantDbManager mInstance;

    public static AssistantDbManager getInstance() {
        if (mInstance == null) {
            synchronized (AssistantDbManager.class) {
                if (mInstance == null) {
                    mInstance = new AssistantDbManager();
                }
            }
        }
        return mInstance;
    }

    private static IDatabase getDBManager() {
        if (LoginManager.getInstance().isUserLogin()) {
            return DBManager.getInstance().getUserDBManager(LoginManager.getInstance().getLoginUserId());
        }
        return null;
    }

    public OpenSemantic queryOpenSemantic() {
        if (getDBManager() == null) return null;
        try {
            List<OpenSemantic> openSemantics = getDBManager().queryAll(OpenSemantic.class);
            if (!ListUtils.isEmpty(openSemantics)) {
                return openSemantics.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveOpenSemantic(OpenSemantic openSemantic) {
        if (getDBManager() == null) return;
        try {
            Log.d(TAG, "saveOpenSemantic: " + getDBManager().save(openSemantic));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteOpenSemantic(OpenSemantic openSemantic) {
        if (getDBManager() == null) return;
        try {
            Log.d(TAG, "deleteOpenSemantic: " + getDBManager().delete(openSemantic));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
