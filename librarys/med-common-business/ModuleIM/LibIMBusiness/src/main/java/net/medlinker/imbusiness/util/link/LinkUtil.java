package net.medlinker.imbusiness.util.link;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.URLSpan;
import android.text.util.Linkify;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:yangjiantao@medlinker.net">Jiantao.Yang</a>
 * @version 3.1
 * @description
 * @time 2016/3/1 19:32
 */
public class LinkUtil {

    /**
     * "@"正则表达式
     * 格式：@用户名(uid)
     **/
    private static final Pattern PATTERN_MENTION = Pattern
            .compile("(@[\\w\\p{Punct}\\p{InCJKUnifiedIdeographs}]{1,32})\\((\\d+)\\)");

    /**
     * "@"正则表达式
     * 格式：@用户名(uid)
     **/
    private static final Pattern PATTERN_MENTION_NEW = Pattern
            .compile("(@[\\w\\p{Punct}\\p{InCJKUnifiedIdeographs}]{1,32})[^\\s]");

//    private static final Pattern PATTERN_URL = Pattern.compile("^((https|http)?://)"
//            + "+(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" + "(([0-9]{1,3}\\.[^\u4e00-\u9fa5\\s]*){3}[0-9]{1,3}" + "|"
//            + "([0-9a-z_!~*'()-]+\\.[^\u4e00-\u9fa5\\s]*)*" + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\.[^\u4e00-\u9fa5\\s]*" + "[a-z]{2,6})" + "(:[0-9]{1,4})?" + "((/?)|"
//            + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$");

    //    private static final Pattern PATTERN_URL_NEW = Pattern.compile("((https|http|Http|Https)?://[^\\u4e00-\\u9fa5\\s]*" + "|" + HOST_NAME + ")");
    //            Pattern.compile("(http://|https://|www){0,1}[^\u4e00-\u9fa5\\s]*?\\.(com|net|cn|me|tw|fr)[^\u4e00-\u9fa5\\s]*");
//Pattern.compile("((https|http|Http|Https)?://(www)?[^\\u4e00-\\u9fa5\\s]*?\\.(com|net|cn|me|tw|fr)[^\\u4e00-\\u9fa5\\s]*" +  ")");//"|" + HOST_NAME +
// FIXME: 2016/4/14 这里没匹配IP地址
    private static final Pattern PATTERN_URL_NEW = Pattern.compile("(http://|Http://|www){0,1}[^\\u4e00-\\u9fa5\\s]+\\.(com|net|cn|me|tw|fr)[^\\u4e00-\\u9fa5\\s]*");
    private static final Pattern PATTERN_URL_NEW_HTTPS = Pattern.compile("(https://|Https://|www){0,1}[^\\u4e00-\\u9fa5\\s]+\\.(com|net|cn|me|tw|fr)[^\\u4e00-\\u9fa5\\s]*");
    private static final String SCHEME_MENTION = "mention://";
    private static final String SCHEME_HTTP_URL = "http://";
    private static final String SCHEME_HTTPS_URL = "https://";

    /**
     * @return
     * @deprecated 解析@好友string
     */
    public static SpannableString parseStr(Context context, CharSequence txt, int mentionTxtColor) {
        if (TextUtils.isEmpty(txt))
            return null;
        SpannableString source = SpannableString.valueOf(txt);
        Linkify.addLinks(source, PATTERN_MENTION, SCHEME_MENTION);

        URLSpan[] urlSpans = source.getSpans(0, source.length(), URLSpan.class);
        for (URLSpan urlSpan : urlSpans) {
            String url = urlSpan.getURL();
            CharacterStyle newSpan = null;
            /*
             * 处理@好友Span 好友的格式为@用户名(uid),我们UI中显示只显示@用户名，所以自定义Span进行处理
             */
            if (url.startsWith(SCHEME_MENTION)) {
                Matcher m = PATTERN_MENTION.matcher(url);
                if (m.find()) {
                    String userName = m.group(1);
                    int uid = Integer.parseInt(m.group(2));
                    newSpan = new MentionSpan(context, userName, uid, mentionTxtColor);
                }
            }
            int start = source.getSpanStart(urlSpan);
            int end = source.getSpanEnd(urlSpan);
            source.removeSpan(urlSpan);
            if (newSpan != null) {
                source.setSpan(newSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return source;
    }

    /**
     * @deprecated
     */
    public static SpannableString parseStr(Context context, CharSequence txt) {
        return parseStr(context, txt, 0);
    }

    public static SpannableString createMentionSpan(Context context, CharSequence txt) {
        if (TextUtils.isEmpty(txt))
            return null;
        SpannableString source = SpannableString.valueOf(txt);

        MentionSpan span = new MentionSpan(context, txt);
        source.setSpan(span, 0, source.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return source;
    }

    public static SpannableString addLink(Context context, CharSequence txt, boolean self) {
        if (TextUtils.isEmpty(txt))
            return null;
        SpannableString source = SpannableString.valueOf(txt);

        Linkify.addLinks(source, Linkify.WEB_URLS);
        Linkify.addLinks(source, PhoneNumberSpan.PATTERN_PHONE, PhoneNumberSpan.SCHEME_PHONE);
        URLSpan[] urlSpans = source.getSpans(0, source.length(), URLSpan.class);
        for (URLSpan urlSpan : urlSpans) {
            String url = urlSpan.getURL();
            CharacterStyle newSpan = null;
            /*
             * 处理@好友Span 好友的格式为@用户名(uid),我们UI中显示只显示@用户名，所以自定义Span进行处理
             */
            if (url.startsWith(SCHEME_MENTION)) {
                Matcher m = PATTERN_MENTION.matcher(url);
                if (m.find()) {
                    String userName = m.group(1);
                    int uid = Integer.parseInt(m.group(2));
                    newSpan = new MentionSpan(context, userName, uid);
                }
            } else if (url.startsWith(SCHEME_HTTP_URL) || url.startsWith(SCHEME_HTTPS_URL)) {
                newSpan = new UrlSpan(url, self);
            } else if (url.startsWith(PhoneNumberSpan.SCHEME_PHONE)) {
                newSpan = new PhoneNumberSpan(url, self);
            }
            int start = source.getSpanStart(urlSpan);
            int end = source.getSpanEnd(urlSpan);
            source.removeSpan(urlSpan);
            if (newSpan != null) {
                source.setSpan(newSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return source;
    }

    public static SpannableString addLink(Context context, CharSequence txt) {
        return addLink(context, txt, false);
    }

}
