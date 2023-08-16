package com.hongying.utils;

import org.apache.commons.lang3.StringEscapeUtils;

public class StringUtil {
    /**
     * description: 字符串  转 Unicode --- 使用工具类实现一键转换
     */
    public static String strToUnicode(String str) {
        return StringEscapeUtils.escapeJava(str);
    }

    /**
     * description:  Unicode 转字符串  --- 使用工具类实现一键转换
     */
    public static String unicodeToStr(String unicode) {
        return StringEscapeUtils.unescapeJava(unicode);
    }
}
