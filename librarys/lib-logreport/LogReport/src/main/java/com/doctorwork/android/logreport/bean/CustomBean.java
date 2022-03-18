package com.doctorwork.android.logreport.bean;

/**
 * @author : HuBoChao
 * @date : 2020/12/2
 * @desc : 埋点类
 */
public class CustomBean<T> {
    /**
     * 自定义事件key
     */
    private String name;
    /**
     * 任意对象都可以，服务端最终会以文本形式展示
     */
    private T content;
    /**
     * 日志时间
     */
    private long createTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
