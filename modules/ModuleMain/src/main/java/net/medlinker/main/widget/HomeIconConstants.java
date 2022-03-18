package net.medlinker.main.widget;

import net.medlinker.main.R;

public class HomeIconConstants {

    private static final int[] HOME = {};

    private static final int[] MINE = {};

    public static int[] getIconList(int iconID) {
        if (iconID == R.mipmap.main_tab_home_n) {
            return HOME;
        } else if (iconID == R.mipmap.main_tab_mine_n) {
            return MINE;
        }
        return null;
    }
}
