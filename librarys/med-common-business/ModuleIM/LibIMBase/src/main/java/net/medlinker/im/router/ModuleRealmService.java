package net.medlinker.im.router;

import java.util.List;

/**
 * @author hmy
 * @time 12/2/21 20:01
 */
public interface ModuleRealmService {

    int getSchemaVersion();

    String getRealmName();

    List<Object> getRealmModule();
}
