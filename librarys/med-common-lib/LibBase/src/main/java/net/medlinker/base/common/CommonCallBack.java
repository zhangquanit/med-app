package net.medlinker.base.common;

/**
 * 处理异步Promise回调
 */
public interface CommonCallBack<T> {

    /**
     * 回调
     */
    void onCallBack(T object);
}
