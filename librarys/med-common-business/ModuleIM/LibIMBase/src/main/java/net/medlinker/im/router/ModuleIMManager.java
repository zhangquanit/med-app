package net.medlinker.im.router;


/**
 * @author hmy
 * @time 2020/9/22 14:01
 */
public enum ModuleIMManager {

    INSTANCE;

    private ModuleRealmService mRealmService;
    private ModuleIMService mImService;
    private ModuleMsgService mMsgService;

    public ModuleIMManager setRealmService(ModuleRealmService realmService) {
        this.mRealmService = realmService;
        return this;
    }

    public ModuleIMManager setIMService(ModuleIMService service) {
        mImService = service;
        return this;
    }

    public ModuleIMManager setMsgService(ModuleMsgService service) {
        mMsgService = service;
        return this;
    }

    public ModuleRealmService getRealmService() {
        return mRealmService;
    }

    public ModuleIMService getIMService() {
        return mImService;
    }

    public ModuleMsgService getMsgService() {
        return mMsgService;
    }
}
