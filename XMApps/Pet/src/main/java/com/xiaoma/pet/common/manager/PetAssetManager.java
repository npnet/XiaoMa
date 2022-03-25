package com.xiaoma.pet.common.manager;

import android.content.Context;

import com.xiaoma.pet.common.callback.IResourceConfigResult;
import com.xiaoma.pet.common.callback.OnFindXmlTagCallback;
import com.xiaoma.pet.model.AssetInfo;

import java.io.File;

/**
 * Created by Gillben
 * date: 2019/2/14 0014
 */
public final class PetAssetManager {

    private static PetAssetManager mPetAssetManager;
    private FileHandleCenter fileHandleCenter;

    private PetAssetManager(Context context) {
        fileHandleCenter = new FileHandleCenter(context);
    }

    public static void init(Context context) {
        if (mPetAssetManager == null) {
            synchronized (PetAssetManager.class) {
                if (mPetAssetManager == null) {
                    mPetAssetManager = new PetAssetManager(context);
                }
            }
        }
    }


    public static PetAssetManager getInstance() {
        return mPetAssetManager;
    }


    /**
     * 保存Unity ab文件
     *
     * @param assetInfo    资源信息
     * @param assetName    文件名
     * @param rawFile      服务器下载源文件
     * @param configResult 配置结果回调
     */
    public void pullAssetBundleSaveToLocal(AssetInfo assetInfo, String assetName, File rawFile, IResourceConfigResult configResult) {
        fileHandleCenter.pullAssetBundle(assetInfo, assetName, rawFile, configResult);
    }


    /**
     * 校验资源文件是否存在
     *
     * @param type         资源类型
     * @param resourceName 资源名称
     */
    public boolean checkFileExists(int type, String resourceName) {
        return fileHandleCenter.checkFileExists(type, resourceName);
    }


    /**
     * 利用标签获取资源文件路径
     *
     * @param levelVersion 所在关卡版本
     * @param tag          在xml中对应路径的标签
     * @param callback     在UI线程回调
     */
    public void findAssetPathFromXML(String levelVersion,
                                     String tag,
                                     OnFindXmlTagCallback callback) {
        fileHandleCenter.findPathForTag(levelVersion, tag, callback);
    }


    public String getResourcePath(int type, String resourceName) {
        return fileHandleCenter.getResourcePath(type, resourceName);
    }


    public void directSaveXMLNode(AssetInfo assetInfo) {
        fileHandleCenter.directSaveXMLNode(assetInfo);
    }


    /**
     * 返回xml路径
     */
    public String getUnityXmlPath() {
        return fileHandleCenter.getUnityXmlPath();
    }


    public void cleanXml() {
        fileHandleCenter.cleanXml();
    }
}
