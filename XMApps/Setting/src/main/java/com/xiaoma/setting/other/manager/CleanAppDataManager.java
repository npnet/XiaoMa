package com.xiaoma.setting.other.manager;

import android.content.Context;
import android.util.Log;

import com.xiaoma.utils.cleaner.AppCleaner;
import com.xiaoma.utils.log.KLog;

import java.util.Stack;

/**
 * @Author: ZiXu Huang
 * @Data: 2019/7/11
 * @Desc:
 */
public class CleanAppDataManager extends AppCleaner.DeleteCache {
    private static CleanAppDataManager instance;
    private Stack packageStack;
    private String curOperateApp;
    private Context context;
    private boolean excuteToLastOne;
    private OnDeleteCallback callback;

    private enum CleanType{
        CacheFile,
        DataFile
    }
    private CleanType curCleanType;

    public static CleanAppDataManager getInstance(){
        if (instance == null) {
            instance = new CleanAppDataManager();
        }
        return instance;
    }

    public void deleteData(Context context, String[] appList){
        this.context = context;
        prepareData(appList);
        excuteDelete(CleanType.CacheFile);
    }

    private void excuteDelete(CleanType type) {
        curCleanType = type;
        if (curCleanType == CleanType.CacheFile) {
            if (packageStack.size() == 1) {
                excuteToLastOne = true;
                deleteSelfSp(context);
            }
            curOperateApp = (String) packageStack.pop();
            try {
                AppCleaner.deleteCacheFile(context, curOperateApp, this);
            } catch (Exception e) {
                e.printStackTrace();
                if (excuteToLastOne) {
                    deleteSelfSp(context);
                    if (callback != null) {
                        excuteToLastOne = false;
                        curCleanType = null;
//                        callback.onDeleteFinish();
                    }
                } else {
                    excuteDelete(CleanType.CacheFile );
                }
            }
        } else if (curCleanType == CleanType.DataFile) {
            try {
                AppCleaner.deleteDataFile(context, curOperateApp, this);
            } catch (Exception e) {
                e.printStackTrace();
                if (excuteToLastOne) {
                    if (callback != null) {
                        excuteToLastOne = false;
                        curCleanType = null;
//                        callback.onDeleteFinish();
                    }
                } else {
                    excuteDelete(CleanType.CacheFile);
                }
            }
        }
    }

    private void deleteSelfSp(  Context context){
        AppCleaner.deleteSelfSp(context);
    }

    private boolean deleteXmFold(){
        return AppCleaner.deleteXiaoMaFolder(context);
    }

    private void prepareData(String[] appList) {
        packageStack = new Stack<String>();
        for (String packageName : appList) {
            packageStack.push(packageName);
        }
    }

    @Override
    public void onRemoveCompleted(String packageName, boolean succeeded) {
        if (curCleanType == CleanType.CacheFile) {
            KLog.d("hzx","删除App " + packageName + ", 结果: " + succeeded);
            excuteDelete(CleanType.DataFile);
        } else if (curCleanType == CleanType.DataFile) {
            KLog.d("hzx","删除App " + packageName + ", 结果: " + succeeded);
            if (excuteToLastOne) {
                boolean b = deleteXmFold();
                KLog.d("hzx","删除小马文件夹结果: " + b);
                if (callback != null) {
                    excuteToLastOne = false;
                    curCleanType = null;
                    callback.onDeleteFinish();
                }
            } else {
                excuteDelete(CleanType.CacheFile);
            }
        }
    }

    public void setOnDeleteCallback(OnDeleteCallback callback){
        this.callback = callback;
    }

    public interface OnDeleteCallback{
        void onDeleteFinish();
    }
}
