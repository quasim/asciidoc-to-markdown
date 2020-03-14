package com.jdd.apidoc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtils {

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static String repeat(String str, int repeat) {
        StringBuffer buffer = new StringBuffer(repeat * str.length());
        for (int i = 0; i < repeat; i++) {
            buffer.append(str);
        }
        return buffer.toString();
    }

    public static String wrap(final String str, final String wrapWith) {

        if (isEmpty(str) || isEmpty(wrapWith)) {
            return str;
        }

        return wrapWith.concat(str).concat(wrapWith);
    }

    /**
     * 重复某个字符串并通过分界符连接
     *
     * <pre>
     * StringUtils.repeatAndJoin("?", 5, ",")   = "?,?,?,?,?"
     * StringUtils.repeatAndJoin("?", 0, ",")   = ""
     * StringUtils.repeatAndJoin("?", 5, null) = "?????"
     * </pre>
     *
     * @param str 被重复的字符串
     * @param count 数量
     * @param conjunction 分界符
     * @return 连接后的字符串
     */
    public static String repeatAndJoin(CharSequence str, int count, CharSequence conjunction) {
        if (count <= 0) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        while (count-- > 0) {
            if (isFirst) {
                isFirst = false;
            } else if (!isEmpty(conjunction)) {
                builder.append(conjunction);
            }
            builder.append(str);
        }
        return builder.toString();
    }

    /**
     * 判断字符串是否包含中文.
     *
     * @param str 被检查的字符串
     * @return 是|否
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

}
