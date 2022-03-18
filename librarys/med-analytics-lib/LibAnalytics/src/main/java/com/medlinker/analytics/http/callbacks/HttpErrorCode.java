package com.medlinker.analytics.http.callbacks;


public enum HttpErrorCode {

    /**
     * 未知错误
     */
    UNKNOWN_ERROR("unknown error, please check the error code", -1),
    HTTP_TIMEOUT_ERROR("http request timeout ", 1),
    HTTP_RESPONSE_ERROR("http response error", 2);


    private String reason;
    private int value;

    HttpErrorCode(String reason, int value) {
        this.reason = reason;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getReason() {
        return reason;
    }

    public static HttpErrorCode valueOf(int value) {
        for (HttpErrorCode errorCode : HttpErrorCode.values()) {
            if (errorCode.value == value) {
                return errorCode;
            }
        }
        UNKNOWN_ERROR.setValue(value);
        return UNKNOWN_ERROR;
    }

    @Override
    public String toString() {
        return "HttpErrorCode{" + "code: " + value + ", reason: \'" + reason + "\'}";
    }
}
