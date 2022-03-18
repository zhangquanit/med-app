package com.medlinker.base.widget.badgeview;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 红点状态
 *
 * @author hmy
 */
public class BadgeViewStatus {

    public static final String DEFAULT = "default";
    private ConcurrentHashMap<String, Boolean> mPointMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> mNumMap = new ConcurrentHashMap<>();

    public void reset() {
        mPointMap.clear();
        mNumMap.clear();
    }

    public void setPoint(String badgeViewTag, boolean isShow) {
        mPointMap.put(badgeViewTag == null ? DEFAULT : badgeViewTag, isShow);
    }

    public void setNum(String badgeViewTag, int num) {
        mNumMap.put(badgeViewTag == null ? DEFAULT : badgeViewTag, num);
    }

    /**
     * @return 是否显示红点
     */
    public boolean isShowPoint(String[] badgeTags) {
        if (badgeTags != null && badgeTags.length > 0) {
            for (String tag : badgeTags) {
                if (mPointMap.containsKey(tag)) {
                    if (mPointMap.get(tag)) {
                        return true;
                    }
                }
            }
            return false;
        }
        Set<Map.Entry<String, Boolean>> entrySet = mPointMap.entrySet();
        for (Map.Entry<String, Boolean> entry : entrySet) {
            if (entry.getValue()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return 红点数字
     */
    public int getNum(String[] badgeTags) {
        int num = 0;
        if (badgeTags != null && badgeTags.length > 0) {
            for (String tag : badgeTags) {
                if (mNumMap.containsKey(tag)) {
                    num += mNumMap.get(tag);
                }
            }
            return num;
        }
        Set<Map.Entry<String, Integer>> entrySet = mNumMap.entrySet();
        for (Map.Entry<String, Integer> entry : entrySet) {
            num += entry.getValue();
        }
        return num;
    }
}
