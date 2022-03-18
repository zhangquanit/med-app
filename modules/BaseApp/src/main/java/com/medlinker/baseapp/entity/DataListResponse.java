package com.medlinker.baseapp.entity;

import androidx.annotation.Keep;

import java.util.List;

/**
 * 列表数据
 * @param <T>
 */
@Keep
public class DataListResponse<T> {
    public List<T> list;
}