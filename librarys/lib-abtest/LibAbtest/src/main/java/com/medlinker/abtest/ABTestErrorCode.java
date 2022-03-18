package com.medlinker.abtest;

public enum ABTestErrorCode {
    UNKNOWN("unknown error, please check the error code", -1),
    HTTP_TIMEOUT("http request timeout ", 1),
    HTTP_RESPONSE_ERROR("http response error", 2),
    APP_ID_EMPTY("appId is null", 3),
    SESSION_EMPTY("session is null", 4),
    EXP_ID_EMPTY("expIds is null", 5),
    SERVICE_ERROR("service error,maybe expId not exist", 180103000),
    NOT_LOGIN("not login", 101);


    private String reason;
    private int value;

    ABTestErrorCode(String reason, int value) {
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

    public static ABTestErrorCode valueOf(int value) {
        for (ABTestErrorCode errorCode : ABTestErrorCode.values()) {
            if (errorCode.value == value) {
                return errorCode;
            }
        }
        UNKNOWN.setValue(value);
        return UNKNOWN;
    }

    @Override
    public String toString() {
        return "ABTestErrorCode{" + "code: " + value + ", reason: \'" + reason + "\'}";
    }
}
