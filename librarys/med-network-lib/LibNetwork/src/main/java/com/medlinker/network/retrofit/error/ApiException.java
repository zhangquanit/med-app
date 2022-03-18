package com.medlinker.network.retrofit.error;

/**
 * @author hmy
 * @time 2021/1/5 15:43
 */
public class ApiException extends RuntimeException {

    private int code;
    private String errMsg;

    public ApiException(String detailMessage) {
        super(detailMessage);
        errMsg = detailMessage;
    }

    public ApiException(int code, String msg) {
        super(msg);
        this.code = code;
        errMsg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return errMsg;
    }
}
