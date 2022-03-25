package com.xiaoma.club.msg.chat.repo;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.club.common.network.ClubRequestManager;
import com.xiaoma.club.common.repo.ModelRepo;
import com.xiaoma.club.msg.chat.model.FaceQuickInfo;
import com.xiaoma.model.XMResult;
import com.xiaoma.network.callback.CallbackWrapper;
import com.xiaoma.network.callback.SimpleCallback;
import com.xiaoma.thread.Priority;
import com.xiaoma.utils.GsonHelper;

import java.util.List;

/**
 * Created by LKF on 2019-2-13 0013.
 */
@Dao
public abstract class FaceRepo extends ModelRepo<FaceQuickInfo> {
    private static final String TYPE_FACE_LIST = "1";

    @Query("SELECT * FROM  facequickinfo")
    abstract public FaceQuickInfo[] get();

    @Query("DELETE FROM facequickinfo")
    public abstract void deleteAll();

    public void fetchFaceQuickList(SimpleCallback<List<FaceQuickInfo>> callback, Priority priority) {
        ClubRequestManager.requestFaceQuickList(TYPE_FACE_LIST, new CallbackWrapper<List<FaceQuickInfo>>(priority, callback) {
            @Override
            public List<FaceQuickInfo> parse(String data) {
                List<FaceQuickInfo> faceQuickInfos = null;
                final XMResult<List<FaceQuickInfo>> result = GsonHelper.fromJson(data, new TypeToken<XMResult<List<FaceQuickInfo>>>() {
                }.getType());
                if (result != null && result.isSuccess()) {
                    faceQuickInfos = result.getData();
                    if (faceQuickInfos != null) {
                        deleteAll();
                        insertAll(faceQuickInfos);
                    }
                }
                return faceQuickInfos;
            }
        });
    }

    @Override
    protected String getTableName() {
        return FaceQuickInfo.class.getSimpleName();
    }
}
