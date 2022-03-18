package net.medlinker.im.realm;

import io.realm.RealmObject;

/**
 * @author hmy
 * @time 2020/10/12 19:02
 */
public class MsgExtEntity extends RealmObject {
    private boolean is_assistant;

    public boolean isAssistant() {
        return is_assistant;
    }

    public void setIsAssistant(boolean isAssistant) {
        this.is_assistant = isAssistant;
    }
}
