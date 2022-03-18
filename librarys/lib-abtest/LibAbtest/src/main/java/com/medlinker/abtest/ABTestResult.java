package com.medlinker.abtest;

public class ABTestResult {
    private int errCode;
    private String errMsg;
    private ABTestModel data;

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public ABTestModel getData() {
        return data;
    }

    public void setData(ABTestModel data) {
        this.data = data;
    }
}
