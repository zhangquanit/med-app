package net.medlinker.im.realm;

import io.realm.RealmObject;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description
 * @time 2017/3/1
 */
public class RealmString extends RealmObject {

    private String string;

    public RealmString() {
    }

    public RealmString(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
