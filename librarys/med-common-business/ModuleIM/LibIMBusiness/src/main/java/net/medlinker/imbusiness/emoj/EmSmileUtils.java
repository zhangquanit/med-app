package net.medlinker.imbusiness.emoj;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.Spannable.Factory;


import com.medlinker.lib.utils.MedDimenUtil;

import net.medlinker.imbusiness.router.ModuleIMBusinessManager;
import net.medlinker.imbusiness.view.VerticalImageSpan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class EmSmileUtils {
    public static final String DELETE_KEY = "em_delete_delete_expression";

    private static final Factory SPANNABLE_FACTORY = Factory.getInstance();
    private static final Map<Pattern, Object> EMOTICONS = new HashMap<>();

    private static EmEaGroupEntity emojDefaultDatas;
    private static EmEaGroupEntity emojADatas;

    static {
        emojDefaultDatas = getDefaultEmojDatas();
        emojADatas = getEaEmojDatas();
        List<EmojiconEntity> emojicons = emojDefaultDatas.getEmojiconList();
        for (int i = 0; i < emojicons.size(); i++) {
            addPattern(emojicons.get(i).getEmojiText(), emojicons.get(i).getIcon());
        }
    }

    /**
     *
     */
    public static EmEaGroupEntity getDefaultEmojDatas() {
        if (null == emojDefaultDatas) {
            return EmEmojData.getData();
        }
        return emojDefaultDatas;
    }

    /**
     * @return
     */
    public static EmEaGroupEntity getEaEmojDatas() {
        if (null == emojADatas) {
            return EmEaGroupData.getData();
        }
        return emojADatas;
    }


    /**
     * add text and icon to the map
     *
     * @param emojiText -- text of emoji
     * @param icon      -- resource id or local path
     */
    public static void addPattern(String emojiText, Object icon) {
        EMOTICONS.put(Pattern.compile(Pattern.quote(emojiText)), icon);
    }


    /**
     * replace existing spannable with smiles
     *
     * @param context
     * @param spannable
     * @return
     */
    public static boolean addSmiles(Context context, Spannable spannable) {
        boolean hasChanges = false;
        for (Entry<Pattern, Object> entry : EMOTICONS.entrySet()) {
            Matcher matcher = entry.getKey().matcher(spannable);
            while (matcher.find()) {
                boolean set = true;
                for (VerticalImageSpan span : spannable.getSpans(matcher.start(), matcher.end(), VerticalImageSpan.class)) {
                    if (spannable.getSpanStart(span) >= matcher.start()
                            && spannable.getSpanEnd(span) <= matcher.end()) {
                        spannable.removeSpan(span);
                    } else {
                        set = false;
                        break;
                    }
                }
                if (set) {
                    hasChanges = true;
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), (Integer) entry.getValue());
                    int size = MedDimenUtil.sp2px(ModuleIMBusinessManager.INSTANCE.getApplication(), 16) * 13 / 10;
                    if (null != bitmap) {
                        Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                        VerticalImageSpan span = new VerticalImageSpan(context, scaleBitmap);
                        spannable.setSpan(span, matcher.start(), matcher.end(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        }

        return hasChanges;
    }

    /**
     * @param context
     * @param text
     * @return
     */
    public static Spannable getSmiledText(Context context, CharSequence text) {
        if (null == text) {
            text = "";
        }
        Spannable spannable = SPANNABLE_FACTORY.newSpannable(text);
        addSmiles(context, spannable);
        return spannable;
    }

    /**
     * @param emojiconIdentityCode
     * @return
     */
    public static EmojiconEntity getEaEmojData(long emojiconIdentityCode) {
        EmEaGroupEntity data = getEaEmojDatas();
        for (EmojiconEntity emojicon : data.getEmojiconList()) {
            if (emojicon.getIdentityCode() == emojiconIdentityCode) {
                return emojicon;
            }
        }
        return null;
    }


}
