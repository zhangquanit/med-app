package com.medlinker.debugtools.entity;

/**
 * @author: pengdaosong
 * @CreateTime: 2021/3/3 4:35 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class DTLaneListEntity {
    private String id;

    private String name;

    private String apps;

    private String creator;

    private String brief;

    private String namespace;

    private int status;

    private String process;

    private String domains_json;

    private String error;

    private String create_at;

    private String update_at;

    private String envs_json;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setApps(String apps) {
        this.apps = apps;
    }

    public String getApps() {
        return this.apps;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getBrief() {
        return this.brief;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getProcess() {
        return this.process;
    }

    public void setDomains_json(String domains_json) {
        this.domains_json = domains_json;
    }

    public String getDomains_json() {
        return this.domains_json;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return this.error;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public String getCreate_at() {
        return this.create_at;
    }

    public void setUpdate_at(String update_at) {
        this.update_at = update_at;
    }

    public String getUpdate_at() {
        return this.update_at;
    }

    public void setEnvs_json(String envs_json) {
        this.envs_json = envs_json;
    }

    public String getEnvs_json() {
        return this.envs_json;
    }
}
