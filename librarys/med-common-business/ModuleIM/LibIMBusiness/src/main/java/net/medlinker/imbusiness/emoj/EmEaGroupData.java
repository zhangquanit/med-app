package net.medlinker.imbusiness.emoj;

import net.medlinker.imbusiness.R;
import net.medlinker.imbusiness.router.ModuleIMBusinessManager;

import java.util.Arrays;

public class EmEaGroupData {

    public static final int eTag = 1001;//表情标识
    public static final String ea_1 = "飞吻";
    public static final String ea_2 = "颤抖";
    public static final String ea_3 = "吃货";
    public static final String ea_4 = "给力";
    public static final String ea_5 = "看好戏";
    public static final String ea_6 = "抠鼻";
    public static final String ea_7 = "调皮";
    public static final String ea_8 = "心塞";
    public static final String ea_9 = "便秘";
    public static final String ea_10 = "尴尬";
    public static final String ea_11 = "害怕";
    public static final String ea_12 = "奸笑";
    public static final String ea_13 = "惊呆";
    public static final String ea_14 = "你走";
    public static final String ea_15 = "痛哭";
    public static final String ea_16 = "无力";
    public static final String ea_17 = "遵命";
    public static final String ea_18 = "等等";


    private static int[] icons = new int[]{
            R.drawable.ea_1,
            R.drawable.ea_2,
            R.drawable.ea_3,
            R.drawable.ea_4,
            R.drawable.ea_5,
            R.drawable.ea_6,
            R.drawable.ea_7,
            R.drawable.ea_8,
            R.drawable.ea_9,
            R.drawable.ea_10,
            R.drawable.ea_11,
            R.drawable.ea_12,
            R.drawable.ea_13,
            R.drawable.ea_14,
            R.drawable.ea_15,
            R.drawable.ea_16,
            R.drawable.ea_17,
            R.drawable.ea_18,

    };

    private static String[] emojNames = new String[]{
            ea_1,
            ea_2,
            ea_3,
            ea_4,
            ea_5,
            ea_6,
            ea_7,
            ea_8,
            ea_9,
            ea_10,
            ea_11,
            ea_12,
            ea_13,
            ea_14,
            ea_15,
            ea_16,
            ea_17,
            ea_18,
    };

    private static String[] emojiconNames = new String[]{
            "ea_1",
            "ea_2",
            "ea_3",
            "ea_4",
            "ea_5",
            "ea_6",
            "ea_7",
            "ea_8",
            "ea_9",
            "ea_10",
            "ea_11",
            "ea_12",
            "ea_13",
            "ea_14",
            "ea_15",
            "ea_16",
            "ea_17",
            "ea_18",
    };


    private static final EmEaGroupEntity DATA = createData();

    private static EmEaGroupEntity createData() {
//        final String EMOJ_URL = CommonDataUtil.getInstance().getExpressionUrl(BaseApplication.getApplication());
        EmEaGroupEntity emojiconGroupEntity = new EmEaGroupEntity();
        EmojiconEntity[] datas = new EmojiconEntity[icons.length];
        for (int i = 0; i < icons.length; i++) {
            datas[i] = new EmojiconEntity(icons[i], null, EmojiconEntity.Type.BIG_EXPRESSION);
            datas[i].setName(emojNames[i]);
            datas[i].setIdentityCode(eTag + i);
//            datas[i].setIconPath(EMOJ_URL + "/" + emojiconNames[i] + ".png");
        }
        emojiconGroupEntity.setEmojiconList(Arrays.asList(datas));
        emojiconGroupEntity.setName(ModuleIMBusinessManager.INSTANCE.getApplication().getResources().getString(R.string.em_a_text));
        emojiconGroupEntity.setType(EmojiconEntity.Type.BIG_EXPRESSION);
        return emojiconGroupEntity;
    }


    public static EmEaGroupEntity getData() {
        return DATA;
    }
}
