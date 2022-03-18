package com.medlinker.reactnative.codepush.logreport;

public class RNCodePushLog {
    private String TAG = "RNCodePushLog";
    private boolean isNeedReport;
    private boolean isReload;
    private boolean checkUpdate;
    private String assetsFileHash;
    private String decryptError;
    private String initUpdateAfterRestart;
    private String failedHash;
    private String metaInfoByPath;
    private String setJsBundle;
    private String reloadBundle;
    private String assetsMetaInfo;
    private String downloadError;
    private boolean decryptSuccess;
    private boolean hasStartDecrypt;
    private String clearUpdateError;

    public void setAssetsFileHash(String assetsFileHash) {
        this.assetsFileHash = assetsFileHash;
        this.isNeedReport = true;
    }

    public void setDecryptError(String decryptError) {
        this.decryptError = decryptError;
        this.isNeedReport = true;
    }

    public boolean isNeedReport() {
        return isNeedReport;
    }

    public void setInitUpdateAfterRestart(String initUpdateAfterRestart) {
        this.initUpdateAfterRestart = initUpdateAfterRestart;
        this.isNeedReport = true;
    }

    public void setFailedHash(String failedHash) {
        this.failedHash = failedHash;
        this.isNeedReport = true;
    }

    public void setMetaInfoByPath(String metaInfoByPath) {
        this.metaInfoByPath = metaInfoByPath;
        this.isNeedReport = true;
    }

    public void setReload(boolean reload) {
        isReload = reload;
    }

    public void setCheckUpdate(boolean checkUpdate) {
        this.checkUpdate = checkUpdate;
    }

    public void setSetJsBundle(String setJsBundle) {
        this.setJsBundle = setJsBundle;
        this.isNeedReport = true;
    }

    public void setReloadBundle(String reloadBundle) {
        this.reloadBundle = reloadBundle;
        this.isNeedReport = true;
    }

    public void setAssetsMetaInfo(String assetsMetaInfo) {
        this.assetsMetaInfo = assetsMetaInfo;
        this.isNeedReport = true;
    }

    public void setDownloadError(String downloadError) {
        this.downloadError = downloadError;
        this.isNeedReport = true;
    }

    public void setDecryptSuccess(boolean decryptSuccess) {
        this.decryptSuccess = decryptSuccess;
    }

    public void setHasStartDecrypt(boolean hasStartDecrypt) {
        this.hasStartDecrypt = hasStartDecrypt;
    }

    public void setClearUpdateError(String clearUpdateError) {
        this.isNeedReport = true;
        this.clearUpdateError = clearUpdateError;
    }

    public void clear() {
        isNeedReport = false;
        isReload = false;
        checkUpdate = false;
        decryptSuccess = false;
        hasStartDecrypt = false;
        assetsFileHash = "";
        decryptError = "";
        initUpdateAfterRestart = "";
        failedHash = "";
        metaInfoByPath = "";
        setJsBundle = "";
        reloadBundle = "";
        assetsMetaInfo = "";
        downloadError = "";
        clearUpdateError = "";
    }
}
