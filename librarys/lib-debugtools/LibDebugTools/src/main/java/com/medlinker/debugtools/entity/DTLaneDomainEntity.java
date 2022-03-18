package com.medlinker.debugtools.entity;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * @author: pengdaosong
 * @CreateTime: 2021/3/4 10:53 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class DTLaneDomainEntity {
    @SerializedName("errcode")
    private int code;
    @SerializedName("errmsg")
    private String msg;
    @SerializedName("data")
    private Map<String,String> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return code == 0;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
