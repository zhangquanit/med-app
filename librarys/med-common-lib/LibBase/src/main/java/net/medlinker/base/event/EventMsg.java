package net.medlinker.base.event;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class EventMsg implements Serializable {
    public int what;
    public int arg1;
    public int arg2;
    public Object obj;

    public EventMsg(int what) {
        this(what, null);
    }

    public EventMsg(int what, Object obj) {
        this.what = what;
        this.obj = obj;
    }
}
