package com.medlinker.debugtools.config;

import com.medlinker.debugtools.base.CallBack;
import com.medlinker.debugtools.fun.lane.DTLaneStorage;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author: pengdaosong
 * @CreateTime: 2021/3/8 4:20 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class DTLaneConfig {
    public static final String ONLINE = "online";
    public static final String QA = "qa";
    public static final String PRE = "pre";

    private List<String> mLaneAuthority;
    private Map<String, String> mQADomains;
    private Map<String, String> mOnlineDomains;
    private Map<String, String> mPreDomains;
    private String mAuthorization;

    private CallBack<DTLaneStorage.LaneData> mCallBack;

    private String mLaneDomain = "https://swimlane.medlinker.com";

    public String getAuthorization() {
        return mAuthorization;
    }

    public DTLaneConfig authorization(String authorization) {
        mAuthorization = authorization;
        return this;
    }

    public DTLaneConfig laneAuthority(List<String> laneAuthority){
        Objects.requireNonNull(laneAuthority);
        this.mLaneAuthority = laneAuthority;
        return this;
    }

    public Collection<String> getLaneAuthority(){
        return mLaneAuthority;
    }

    public String getLaneDomain() {
        return mLaneDomain;
    }

    public void laneDomain(String laneDomain) {
        Objects.requireNonNull(laneDomain);
        this.mLaneDomain = laneDomain;
    }

    public DTLaneConfig qADomains(Map<String, String> map) {
        Objects.requireNonNull(map);
        mQADomains = map;
        return this;
    }

    public Map<String, String> getQADomains() {
        return mQADomains;
    }

    /**
     * 用于切换到线上环境使用
     *
     * @param map
     * @return
     */
    public DTLaneConfig onlineDomains(Map<String, String> map) {
        Objects.requireNonNull(map);
        mOnlineDomains = map;
        return this;
    }

    public Map<String, String> getOnlineDomains() {
        return mOnlineDomains;
    }

    public DTLaneConfig preDomains(Map<String, String> map) {
        Objects.requireNonNull(map);
        mPreDomains = map;
        return this;
    }

    public Map<String, String> getPreDomains() {
        return mPreDomains;
    }

    public CallBack<DTLaneStorage.LaneData> getCallBack() {
        return mCallBack;
    }

    public DTLaneConfig callBack(CallBack<DTLaneStorage.LaneData> mCallBack) {
        this.mCallBack = mCallBack;
        return this;
    }
}
