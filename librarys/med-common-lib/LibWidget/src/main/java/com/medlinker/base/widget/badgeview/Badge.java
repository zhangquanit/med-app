package com.medlinker.base.widget.badgeview;

import android.view.View;

/**
 * BadgeView 接口
 *
 * @author hmy
 */
public interface Badge {

    /***/
    Badge setBadgeNumber(int badgeNum);

    /***/
    int getBadgeNumber();

    /***/
    Badge setBadgeText(String badgeText);

    /***/
    Badge setBadgeBackgroundColor(int color);

    /***/
    Badge setBadgePadding(int padding);

    /***/
    Badge setGravityOffset(float offset, boolean isDpValue);

    /***/
    Badge setGravityOffset(float offsetX, float offsetY, boolean isDpValue);

    /***/
    Badge setTextColor(int color);

    /***/
    Badge setTextSize(float textSize);

    Badge setPointRadius(int radius);

    /***/
    Badge setGravity(int gravity);

    /***/
    Badge setBadgeStyle(@BadgeView.BadgeStyle int style);

    /***/
    Badge show();

    /***/
    Badge hide();

    Badge setVisibility(boolean isShow);

    Badge setBadgeTag(String... tags);

    Badge refresh(BadgeViewStatus badgeViewStatus);

    /***/
    BadgeView bindTarget(View view);

    /***/
    View getTargetView();

}
