package com.medlinker.network.retrofit;

/**
 * @author hmy
 * @time 2021/1/4 15:49
 */
public class BusinessCodeEntity {
    private boolean isSuccess; //是否请求成功
    private int code; //业务code

    public BusinessCodeEntity(boolean isSuccess, int code) {
        this.isSuccess = isSuccess;
        this.code = code;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
