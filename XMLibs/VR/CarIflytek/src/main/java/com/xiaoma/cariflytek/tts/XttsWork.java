package com.xiaoma.cariflytek.tts;

import android.os.Environment;
import android.text.TextUtils;

import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.ZipUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author: iSun
 * @date: 2019/6/21 0021
 */
public class XttsWork implements Runnable {
    //商城tts资源路径
    private static final String resZipPath = Environment.getExternalStorageDirectory() + "/XTTS/";
    private static final String xttsResPath = Environment.getExternalStorageDirectory() + "/iflytek/res/tts/";
    private static final String cfgPath = Environment.getExternalStorageDirectory() + "/iflytek/res/tts/isstts.cfg";
    private static final String bakPath = Environment.getExternalStorageDirectory() + "/iflytek/res/tts/isstts.bak";

    private int resultId;//请求返回值ID
    private String fileName;//文件名
    private int roleId;//发言人ID 需要与新增配置同名
    private String roleName;//角色名称 需要与zip文件中的*.data *.irf文件同名
    private ITtsWorkListener listener;
    private String TAG = XttsWork.class.getSimpleName();

    public static String getResZipPath() {
        return resZipPath;
    }

    public XttsWork(int resultId, String fileName, int roleId, String roleName, ITtsWorkListener listener) {
        this.resultId = resultId;
        this.fileName = fileName;
        this.roleId = roleId;
        this.roleName = roleName;
        this.listener = listener;
    }

    @Override
    public void run() {
        boolean result = false;
        if (listener != null) {
            listener.onStart();
        }

        if (!checkNull()) {
            result = unTtsZipFile();
            if (!result) {
                resetTtsConfig();
            }
        }

        if (listener != null) {
            listener.onEnd(result, roleId);
        }


    }

    private boolean checkNull() {
        return TextUtils.isEmpty(fileName);
    }

    /**
     * 备份TTS配置文件
     *
     * @return
     */
    private boolean backupTtsConfig() {
        boolean result = false;

        File cfgFile = new File(cfgPath);
        File bakFile = new File(bakPath);
        if (bakFile.exists() && cfgFile.exists()) {
            bakFile.delete();
        }
        result = FileUtils.copy(cfgFile, bakFile);
        if (result && bakFile.exists()) {
            result = FileUtils.delete(cfgFile);
        }
        return result;
    }

    /**
     * 还原TTS配置
     *
     * @return
     */
    private boolean resetTtsConfig() {
        File conFile = new File(cfgPath);
        File backFile = new File(bakPath);
        boolean copy = FileUtils.copy(backFile, conFile);
        return copy;
    }

    private boolean unTtsZipFile() {
        boolean result = false;
        //解压文件 释放 jiajia.irf、jiajia.dat
        String prefix = getFilePrefixName(this.fileName);
        String xttsPath = resZipPath + this.fileName;
        File file = new File(xttsPath);
        if (file.isFile() && file.exists() && !TextUtils.isEmpty(prefix)) {
            //解压
            try {
                ZipUtils.unzipFile(xttsPath, xttsResPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            File temp = new File(xttsResPath + prefix);
            //copy
            result = copyFiles(temp, true);
        }
        return result;
    }

    private boolean copyFiles(File temp, boolean isDelSrc) {
        boolean result = false;
        boolean delete = true;
        boolean backupResult = true;
        if (!temp.exists() || !temp.isDirectory()) {
            return result;
        }
        File[] files = temp.listFiles();
        if (checkXttsFiles(files)) {
            //TTS文件已经存在 删除释放的临时文件
            FileUtils.delete(temp);
            return true;
        } else {
            backupResult = backupTtsConfig();
        }
        if (backupResult) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    // 复制文件
                    File target = new File(xttsResPath + files[i].getName());
                    boolean copyResult = FileUtils.copy(files[i], target);
                    if (!copyResult) {
                        //失败
                        result = false;
                        break;
                    } else {
                        result = true;
                    }
                }
                if (files[i].isDirectory()) {
                    // 目录
                }
            }
            if (isDelSrc) {
                //删除临时文件
                delete = FileUtils.delete(temp);
            }
        }
        return result && delete;
    }

    private boolean checkXttsFiles(File[] files) {
        boolean result = true;
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            if (!new File(xttsResPath + name).exists() && !"isstts.cfg".equals(name)) {
                result = false;
                break;
            }
        }
        return result;
    }

    public String getFilePrefixName(String fileName) {
        if (!TextUtils.isEmpty(fileName)) {
            int end = fileName.lastIndexOf(".");
            if (end > 0 && end < fileName.length()) {
                return fileName.substring(0, end);
            }
        }
        return null;
    }
}
