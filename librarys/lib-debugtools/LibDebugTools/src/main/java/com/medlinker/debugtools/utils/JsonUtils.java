package com.medlinker.debugtools.utils;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/5 3:26 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class JsonUtils {

    public static String formatString(String text, String line) {

        StringBuilder json = new StringBuilder();
        String indentString = "";

        for (int i = 0; i < text.length(); i++) {
            char letter = text.charAt(i);
            switch (letter) {
                case '{':
                case '[':
                    json.append(line + indentString + letter + line);
                    indentString = indentString + "\t";
                    json.append(indentString);
                    break;
                case '}':
                case ']':
                    indentString = indentString.replaceFirst("\t", "");
                    json.append(line + indentString + letter);
                    break;
                case ',':
                    json.append(letter + line + indentString);
                    break;

                default:
                    json.append(letter);
                    break;
            }
        }

        return json.toString();
    }
}
