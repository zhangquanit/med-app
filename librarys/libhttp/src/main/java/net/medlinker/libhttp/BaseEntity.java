package net.medlinker.libhttp;/**
 * Created by kuiwen on 2015/10/20.
 */


import androidx.annotation.Keep;

/**
 * @author <a href="mailto:zhangkuiwen@medlinker.net">Kuiwen.Zhang</a>
 * @version 1.0
 * @description 功能描述
 * @time 2015/10/20 18:46
 **/
@Keep
public class BaseEntity<T> {
    public int errcode;
    public String errmsg;
    public T data;

    public boolean isSuccess() {
        return errcode == 0;
    }
    @Override
    public String toString() {
        return "BaseEntity{" +
                "code=" + errcode +
                ", msg='" + errmsg + '\'' +
                ", data=" + data +
                '}';
    }
}
