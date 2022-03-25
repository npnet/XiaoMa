package com.xiaoma.hotfix.model;

/**
 * @author youthyJ
 * @date 2018/11/15
 */
public class PatchDownloadInfo {
    private String downloadUrl;
    private String fileName;
    private String patchMD5;
    private PatchConfig servicePatchConfig;

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPatchMD5() {
        return patchMD5;
    }

    public void setPatchMD5(String patchMD5) {
        this.patchMD5 = patchMD5;
    }

    public PatchConfig getServicePatchConfig() {
        return servicePatchConfig;
    }

    public void setServicePatchConfig(PatchConfig servicePatchConfig) {
        this.servicePatchConfig = servicePatchConfig;
    }
}
