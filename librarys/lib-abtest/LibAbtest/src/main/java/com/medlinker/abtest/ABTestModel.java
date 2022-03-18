package com.medlinker.abtest;

public class ABTestModel {
    //应用id
    private String appId;
    //试验id
    private String expId;
    //试验分组id
    private String groupId;
    //试验变量key
    private String variableKey;
    //试验变量类型
    private String variableType;
    //试验变量值
    private String variableValue;
    //神策事件名称，医联使用GrowingIO
    private String sensorEventKey;

    public String getExpId() {
        return expId;
    }

    public void setExpId(String expId) {
        this.expId = expId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getVariableKey() {
        return variableKey;
    }

    public void setVariableKey(String variableKey) {
        this.variableKey = variableKey;
    }

    public String getVariableType() {
        return variableType;
    }

    public void setVariableType(String variableType) {
        this.variableType = variableType;
    }

    public String getVariableValue() {
        return variableValue;
    }

    public void setVariableValue(String variableValue) {
        this.variableValue = variableValue;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSensorEventKey() {
        return sensorEventKey;
    }

    public void setSensorEventKey(String sensorEventKey) {
        this.sensorEventKey = sensorEventKey;
    }
}
