package com.medlinker.base.common;

import java.io.Serializable;

/**
 * 处理异步Promise回调
 *
 * @param <T>
 */
public interface CommonCallBack<T> extends Serializable {

    /**
     * 回调
     *
     * @param object
     */
    void onCallBack(T object);
}
