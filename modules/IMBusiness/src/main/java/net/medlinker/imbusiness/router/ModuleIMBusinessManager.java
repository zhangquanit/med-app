package net.medlinker.imbusiness.router;

import android.app.Application;

/**
 * @author hmy
 * @time 2020/10/3 15:31
 */
public enum ModuleIMBusinessManager {
    INSTANCE;

    private Application application;
    private boolean isDebug;

    private ModuleIMBusinessService businessService;

    public Application getApplication() {
        return application;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public ModuleIMBusinessManager setDebug(boolean debug) {
        isDebug = debug;
        return this;
    }

    public ModuleIMBusinessManager setApplication(Application application) {
        this.application = application;
        return this;
    }

    public ModuleIMBusinessManager setBusinessService(ModuleIMBusinessService businessService) {
        this.businessService = businessService;
        return this;
    }

    public ModuleIMBusinessService getBusinessService() {
        return businessService;
    }
}
