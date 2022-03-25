package com.xiaoma.pet.common.manager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.pet.common.annotation.NodeAttr;
import com.xiaoma.pet.common.annotation.ResourceType;
import com.xiaoma.pet.common.annotation.XmlNode;
import com.xiaoma.pet.common.callback.IResourceConfigResult;
import com.xiaoma.pet.common.callback.OnFindXmlTagCallback;
import com.xiaoma.pet.model.AssetInfo;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.log.KLog;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Gillben
 * date: 2019/2/14 0014
 */
final class FileHandleCenter {

    private static final String TAG = FileHandleCenter.class.getName();
    private static final String ENCODE_FORMAT = "UTF-8";
    private static final String UNITY_XML_FILE = "UnityAssetPath.xml";
    private static final String UNITY_ASSET_FOLDER = "UnityAsset";
    private static final String VERSION_FOLDER = "APPVersion_";
    private static final String ASSET_LEVEL = "Level";
    private static final String ASSET_PET = "Pet";
    private static final String ASSET_SCENE = "Scene";
    private static final String ASSET_DECORATOR = "Decorator";
    private static final String ASSET_GIFT = "Gift";

    private Context mContext;
    private String rootPath;
    private File xmlPathFile;
    private File assetFolder;
    private File appVersionFolder;


    FileHandleCenter(Context context) {
        this.mContext = context.getApplicationContext();
        rootPath = Objects.requireNonNull(mContext.getExternalFilesDir(null)).getPath();
        KLog.d(TAG, rootPath);

        initUnityAssetXmlDocument();
        createAssetFolder();
        createAppVersionFolderForAsset();
    }


    /**
     * 通过具体关卡、资源节点、版本号确定资源路径
     *
     * @param levelVersion 关卡版本
     * @param tag          对应节点
     */
    void findPathForTag(String levelVersion,
                        String tag,
                        OnFindXmlTagCallback callback) {

        if (xmlPathFile == null || !xmlPathFile.exists()) {
            KLog.w(TAG, "xmlPathFile not exists.");
            callback.failure("xmlPathFile not exists.");
            return;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlPathFile);
            Node rootNode = document.getElementsByTagName(XmlNode.app).item(0);
            if (rootNode == null) {
                callback.failure("rootNode is null.");
                return;
            }

            //确定关卡(父节点)是否存在
            Element levelElement = (Element) checkExistsVersion(document, XmlNode.level, levelVersion);
            if (levelElement != null) {
                //查找对应资源的path
                Element actuallyElement = (Element) checkChildNodeExistsInParent(document, tag, levelVersion);
                if (actuallyElement != null) {
                    String path = actuallyElement.getAttribute(NodeAttr.path);
                    callback.findPath(path);
                } else {
                    callback.failure(tag + " node is not find.");
                }
            } else {
                callback.failure("Parent node is null with version " + levelVersion);
            }
        } catch (Exception e) {
            callback.failure("Parser xmlPathFile failed.");
        }

    }


    /**
     * 校验文件是否存在
     *
     * @param type         资源类型
     * @param resourceName 资源名称
     */
    boolean checkFileExists(int type, String resourceName) {
        String parentPath = getActuallyResourcePath(type);
        if (parentPath == null || TextUtils.isEmpty(resourceName)) {
            KLog.w(TAG, "parentPath: " + parentPath +
                    " ====== " + "resourceName: " + resourceName);
            return false;
        }

        String filePath = parentPath + File.separator + resourceName;
        File file = new File(filePath);
        return file.exists();
    }


    String getResourcePath(int type, String resourceName) {
        if (TextUtils.isEmpty(resourceName)) {
            KLog.w(TAG, "resourceName is null.");
            return null;
        }

        String parentPath = getActuallyResourcePath(type);
        if (parentPath == null || TextUtils.isEmpty(resourceName)) {
            KLog.w(TAG, "parentPath is not exists.");
            return null;
        }

        String filePath = parentPath + File.separator + resourceName;
        File file = new File(filePath);
        return file.getAbsolutePath();
    }


    /**
     * 返回xml文件路径
     */
    String getUnityXmlPath() {
        return xmlPathFile == null ? null : xmlPathFile.getAbsolutePath();
    }


    /**
     * clean xml文件
     */
    void cleanXml() {
        deleteOldAsset(xmlPathFile);
        initUnityAssetXmlDocument();
    }


    /**
     * 直接更新xml中某个资源信息
     */
    void directSaveXMLNode(AssetInfo assetInfo) {
        saveAssetFilePathToXml(assetInfo);
    }


    /**
     * 保存Unity AB文件
     *
     * @param assetInfo 资源信息
     * @param assetName 资源名
     * @param rawFile   源文件
     */
    void pullAssetBundle(AssetInfo assetInfo, String assetName, File rawFile, IResourceConfigResult resourceConfigResult) {

        if (appVersionFolder == null || !appVersionFolder.exists() || !appVersionFolder.isDirectory()) {
            KLog.w(TAG, "pullAssetBundleSaveToLocal: appVersionFolder field is illegal.");
            return;
        }

        String assetFilePath = getActuallyResourcePath(assetInfo.getAssetType());
        handleAssetBundle(assetFilePath, assetName, rawFile, assetInfo, resourceConfigResult);
    }


    private void handleAssetBundle(String folderPath,
                                   String fileName,
                                   final File rawFile,
                                   final AssetInfo assetInfo,
                                   @NonNull final IResourceConfigResult resourceConfigResult) {

        if (TextUtils.isEmpty(folderPath) || TextUtils.isEmpty(fileName)) {
            KLog.w(TAG, "Path or fileName param is null");
            return;
        }

        String actuallyPath = folderPath + File.separator + fileName;
        final File destFile = new File(actuallyPath);
        if (destFile.exists()) {
            destFile.delete();
        }

        if (rawFile == null || !rawFile.exists()) {
            KLog.w(TAG, "rawFile is null or not exists.");
            return;
        }

        //复制文件至unity调用路径
        resourceConfigResult.start();
        ThreadDispatcher.getDispatcher().post(new Runnable() {
            @Override
            public void run() {
                final boolean copySuccess = FileUtils.copy(rawFile, destFile);
                if (copySuccess) {
                    assetInfo.setValue(destFile.getAbsolutePath());
                    saveAssetFilePathToXml(assetInfo);
                    //删除临时文件
                    rawFile.delete();
                }

                ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                    @Override
                    public void run() {
                        resourceConfigResult.result(copySuccess);
                    }
                });
            }
        });
    }


    private void saveAssetFilePathToXml(AssetInfo assetInfo) {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlPathFile);
            Node rootNode = document.getElementsByTagName(XmlNode.app).item(0);
            if (rootNode == null) {
                KLog.w(TAG, "root node is null.");
                return;
            }

            //先校验父节点是否存在
            Element appElement = (Element) checkExistsVersion(document, XmlNode.level, assetInfo.getLevelVersion());
            if (appElement != null) {
                //校验子节点是否存在
                Element childElement = (Element) checkChildNodeExistsInParent(document, assetInfo.getTagNode(), assetInfo.getLevelVersion());
                if (childElement != null) {
                    childElement.setAttribute(assetInfo.getAttr(), assetInfo.getValue());
                } else {
                    Element temp = document.createElement(assetInfo.getTagNode());
                    temp.setAttribute(assetInfo.getAttr(), assetInfo.getValue());
                    appElement.appendChild(temp);
                }
            } else {
                Element parent = document.createElement(XmlNode.level);
                parent.setAttribute(NodeAttr.version, assetInfo.getLevelVersion());
                Element tempChild = document.createElement(assetInfo.getTagNode());
                tempChild.setAttribute(assetInfo.getAttr(), assetInfo.getValue());
                parent.appendChild(tempChild);
                rootNode.appendChild(parent);
            }

            //刷新xml
            refreshXML(document);
        } catch (Exception e) {
            KLog.w(TAG, "Save asset file and convert xml exception.------ tag："
                    + assetInfo.getTagNode() + "  value: " + assetInfo.getValue());
        }
    }


    /**
     * 刷新目标xml
     */
    private void refreshXML(Document document) throws Exception {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        //设置换行
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(xmlPathFile));
    }


    /**
     * 删除旧版Asset
     */
    private void deleteOldAsset(File oldFile) {
        if (oldFile == null || !oldFile.exists()) {
            return;
        }
        oldFile.delete();
    }


    /**
     * 查找对应版本节点
     *
     * @param document 目标xml
     * @param tag      节点名
     * @param version  当前存入的版本号
     */
    private Node checkExistsVersion(Document document, String tag, String version) {
        if (document == null) {
            return null;
        }

        Node curNode = null;
        NodeList nodeList = document.getElementsByTagName(tag);
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node tempNode = nodeList.item(i);
                if (tempNode instanceof Element) {
                    String attrValue = ((Element) tempNode).getAttribute(NodeAttr.version);
                    if (attrValue != null && attrValue.equals(version)) {
                        curNode = tempNode;
                        break;
                    }
                }
            }
        }
        return curNode;
    }


    private Node checkChildNodeExistsInParent(Document document, String childNodeTag, String parentVersion) {
        if (document == null || TextUtils.isEmpty(childNodeTag)) {
            return null;
        }

        NodeList nodeList = document.getElementsByTagName(childNodeTag);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            Element parent = (Element) childNode.getParentNode();
            if (parent != null && parentVersion.equals(parent.getAttribute(NodeAttr.version))) {
                return childNode;
            }
        }
        return null;
    }


    private void initUnityAssetXmlDocument() {
        try {
            xmlPathFile = new File(rootPath, UNITY_XML_FILE);
            //xml文件已存在，不再初始化
            if (xmlPathFile.exists()) {
                return;
            }
            xmlPathFile.createNewFile();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            //初始化节点
            Element appNode = document.createElement(XmlNode.app);
            appNode.setAttribute(NodeAttr.version, "1");

            Element levelNode = document.createElement(XmlNode.level);
            levelNode.setAttribute(NodeAttr.version, "1");
            Element petNode = document.createElement(XmlNode.pet);
            Element sceneNode = document.createElement(XmlNode.scene);
            Element decoratorNode = document.createElement(XmlNode.decorator);
            Element giftNode = document.createElement(XmlNode.gift);

            levelNode.appendChild(petNode);
            levelNode.appendChild(sceneNode);
            levelNode.appendChild(decoratorNode);
            levelNode.appendChild(giftNode);
            appNode.appendChild(levelNode);
            document.appendChild(appNode);
            refreshXML(document);
        } catch (Exception e) {
            e.printStackTrace();
            KLog.e(TAG, "DocumentBuilder create failed.");
        }
    }


    private String getActuallyResourcePath(int type) {
        String assetFilePath = null;
        switch (type) {
            case ResourceType.LEVEL:
                assetFilePath = createAssetDir(ASSET_LEVEL).getPath();
                break;

            case ResourceType.PET:
                assetFilePath = createAssetDir(ASSET_PET).getPath();
                break;

            case ResourceType.SCENE:
                assetFilePath = createAssetDir(ASSET_SCENE).getPath();
                break;

            case ResourceType.DECORATOR:
                assetFilePath = createAssetDir(ASSET_DECORATOR).getPath();
                break;

            case ResourceType.GIFT:
                assetFilePath = createAssetDir(ASSET_GIFT).getPath();
                break;
        }
        return assetFilePath;
    }


    private void createAssetFolder() {
        assetFolder = createFolder(rootPath + File.separator + UNITY_ASSET_FOLDER);
    }


    private void createAppVersionFolderForAsset() {
        //以app版本号作为资源文件目录名
        String versionName = getAppVersionCode();
        String appVersionPath = assetFolder.getPath() + File.separator + VERSION_FOLDER + versionName;
        if (!TextUtils.isEmpty(appVersionPath)) {
            appVersionFolder = createFolder(appVersionPath);
        }
    }

    private File createFolder(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        } else if (!file.isDirectory()) {
            file.delete();
            file.mkdir();
        }
        Log.d(TAG, file.getAbsolutePath());
        return file;
    }


    private File createAssetDir(String assetDirName) {
        if (TextUtils.isEmpty(assetDirName)) {
            throw new IllegalArgumentException("assetDirName is null.");
        }
        String dirPath = appVersionFolder.getPath() + File.separator + assetDirName;
        return createFolder(dirPath);
    }


    private String getAppVersionCode() {
        String versionName = "";
        PackageManager packageManager = mContext.getPackageManager();
        try {
            //以app版本号作为资源文件目录名
            PackageInfo packageInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

}
