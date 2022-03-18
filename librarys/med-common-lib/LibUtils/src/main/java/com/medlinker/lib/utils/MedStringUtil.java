package com.medlinker.lib.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: KindyFung.
 * CreateTime:  2015/11/2 18:16
 * Email：fangjing@medlinker.com.
 * Description:
 */
public class MedStringUtil {
    private static final String TAG = MedStringUtil.class.getSimpleName();


    /**
     * 定义script的正则表达式
     */
    private static final String REGEX_SCRIPT = "<script[^>]*?>[\\s\\S]*?<\\/script>";
    /**
     * 定义style的正则表达式
     */
    private static final String REGEX_STYLE = "<style[^>]*?>[\\s\\S]*?<\\/style>";
    /**
     * 定义HTML标签的正则表达式
     */
    private static final String REGEX_HTML = "<[^>]+>";
    /**
     * 定义空格回车换行符
     */
    private static final String REGEX_SPACE = "\\s*|\t|\r|\n";

    public static String num2Percent(float num) {
        NumberFormat format = NumberFormat.getPercentInstance();
        format.setMinimumFractionDigits(1);
        format.setRoundingMode(RoundingMode.DOWN);
        return format.format(num);
    }


    public static String getRandomString(int length) {
        String KeyString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        int len = KeyString.length();
        for (int i = 0; i < length; i++) {
            sb.append(KeyString.charAt((int) Math.round(Math.random() * (len - 1))));
        }
        return sb.toString();
    }

    /**
     * 封装请求参数
     *
     * @param params 请求参数
     * @param encode 编码格式，例如'UTF-8'
     * @return
     */
    public static String encodeParams(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();
        // 判断参数是否为空
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                try {
                    if (TextUtils.isEmpty(entry.getValue())) {
                        stringBuffer.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                    } else {
                        stringBuffer.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), encode)).append("&");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            if (stringBuffer != null && stringBuffer.length() > 0) {
                stringBuffer.deleteCharAt(stringBuffer.length() - 1);
            }
        }
        return stringBuffer.toString();
    }

    public static String appendParams(String host, Map<String, String> params) {
        if (TextUtils.isEmpty(host) || params == null || params.isEmpty()) {
            return host;
        }
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(host);
        if (host.contains("?")) {
            sBuilder.append("&");
        } else {
            sBuilder.append("?");
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            try {
                if (TextUtils.isEmpty(entry.getValue())) {
                    sBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                } else {
                    sBuilder.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8")).append("&");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (sBuilder.length() > 0) {
            sBuilder.deleteCharAt(sBuilder.length() - 1);
        }
        return sBuilder.toString();
    }

    /**
     * 判断是否为正确的手机号
     *
     * @param phone
     * @return
     */
    public static boolean isPhoneNo(String phone) {
        return !TextUtils.isEmpty(phone) && phone.startsWith("1") && phone.length() == 11;
    }

    public static boolean isMobileNO(String mobiles) {
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        }
        Pattern p = Pattern.compile("^((1[0-9][0-9])\\d{8}$)");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 判断是否包含手机号码
     *
     * @param content
     * @return
     */
    public static boolean isContainMobileNo(String content) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }
        Pattern p = Pattern.compile("((1[3-8][0-9])\\d{8})");
        Matcher m = p.matcher(content);
        return m.find();
    }


    /**
     * 隐藏手机号中间四位
     *
     * @param number 手机号码
     * @return
     */
    public static String buildHiddenMobile(String number) {
        if (null == number || number.length() < 11) {
            return number;
        }
        return number.substring(0, 3) + "****" + number.substring(7, number.length());
    }

    /**
     * 获取字符串长度
     *
     * @param value
     * @return
     */
    public static int getCharLength(String value) {
        if (TextUtils.isEmpty(value)) {
            return 0;
        }
        int valueLength = 0;
        String chineseChar = "[\u0391-\uFFE5]";
        // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (int i = 0; i < value.length(); i++) {
            // 获取一个字符
            String temp = value.substring(i, i + 1);
            // 判断是否为中文字符
            if (temp.matches(chineseChar)) {
                // 中文字符长度为2
                valueLength += 2;
            } else {
                // 非中文字符长度为1
                valueLength += 1;
            }
        }
        return valueLength;
    }

    /**
     * 判断是否包含中文
     *
     * @param value
     * @return
     */
    public static boolean hasChineseChar(String value) {
        if (TextUtils.isEmpty(value)) {
            return false;
        }
        String chineseChar = "[\u0391-\uFFE5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chineseChar)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 判断字符串是否为手机号
     *
     * @param str 字符串
     * @return 是否为手机号
     */
    public static boolean isMobile(String str) {
        return str.matches("^((1[358][0-9])|(14[57])|(17[0678]))\\d{8}$");
    }


    /**
     * 判断是否为密码，不能包含特殊字符，空格、制表符、中文
     *
     * @param value 字符串，不能包含特殊字符，空格、制表符、中文
     * @return 是否为密码
     */
    public static boolean isPassword(String value) {
        if (TextUtils.isEmpty(value)) {
            return false;
        }
        return !((value.contains(" ") || value.contains("\t") || value.contains("\n") || value.contains("\r")) || hasChineseChar(value));
    }

    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    /**
     * 判断字符串是否为字母
     */
    public static boolean isLetter(String str) {
        Pattern p = Pattern.compile("[a-zA-Z]");
        Matcher m = p.matcher(str);

        return m.matches();
    }

    public static String[] getSearchKeyWords(String key) {
        String[] keys = new String[]{key};
        if (key.contains(",")) {
            keys = key.split(",");
        } else if (key.contains(" ")) {
            keys = key.split(" ");
        } else if (key.contains("、")) {
            keys = key.split("、");
        }
        return keys;
    }

    public static void copyToClipboard(Context context, CharSequence str) {
        ClipboardManager clipboard = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText("simple text", str);
        // Set the clipboard's primary clip.
        clipboard.setPrimaryClip(clip);
    }

    private static class NoLineClickSpan extends android.text.style.ClickableSpan {
        int color;

        public NoLineClickSpan(int color) {
            super();
            this.color = color;
        }


        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(color);
            ds.setUnderlineText(false); //去掉下划线
        }

        @Override
        public void onClick(View widget) {
        }
    }

    public static void setSpan(final TextView textView, String text) {

        Spannable span = (Spannable) Html.fromHtml(text);
        span.setSpan(new NoLineClickSpan(textView.getCurrentTextColor()), 0, span.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(span);
        textView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
    }



    /**
     * 去读raw 文件
     *
     * @param resId
     * @return
     */
    public static String readFromRaw(Context context, int resId) {
        String result = "";
        try {
            InputStream input = context.getResources().openRawResource(resId);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = input.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
            output.close();
            input.close();
            return output.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断一个string是否只包含空格
     */
    public static boolean isAllBlankSpace(String str) {
        for (int i = 0; i < str.length(); i++) {
            char value = str.charAt(i);
            if (!(" ".equals(str.substring(i, i + 1)) || value == '\n')) {
                return false;
            }
        }

        return true;
    }

    public static String unicodeToString(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }

        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }


    public static String delHTMLTag(String htmlStr) {
        if (htmlStr == null || htmlStr.length() == 0) {
            return htmlStr;
        }
        // 过滤script标签
        Pattern p_script = Pattern.compile(REGEX_SCRIPT, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll("");
        // 过滤style标签
        Pattern p_style = Pattern.compile(REGEX_STYLE, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll("");
        // 过滤html标签
        Pattern p_html = Pattern.compile(REGEX_HTML, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll("");
        // 过滤空格回车标签
        Pattern p_space = Pattern.compile(REGEX_SPACE, Pattern.CASE_INSENSITIVE);
        Matcher m_space = p_space.matcher(htmlStr);
        htmlStr = m_space.replaceAll("");
        return htmlStr.trim();
    }

    public static boolean isEmptyOrNull(String str) {
        return null == str || str.equals("");

    }

    public static boolean isEmpty(String str) {
        return isEmptyOrNull(str) || str.trim().equals("") || "null".equalsIgnoreCase(str.trim());
    }

    /**
     *
     */
    public static String subZeroAndDot(float f) {
        return subZeroAndDot(String.valueOf(f));
    }

    /**
     * 使用java正则表达式去掉多余的.与0
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", ""); //去掉多余的0
            s = s.replaceAll("[.]$", ""); //如最后一位是.则去掉
        }
        return s;
    }

}
