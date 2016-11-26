package com.forfan.bigbang.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangyan-pd on 2016/11/25.
 */

public class RegexUtil {

    public static boolean isEnglish(String charaString){
        return charaString.matches("^[a-zA-Z]*-*[a-zA-Z]*");
    }

    public static boolean isPositiveInteger(String orginal) {
        return isMatch("^\\+{0,1}[1-9]\\d*", orginal);
    }

    public static boolean isNegativeInteger(String orginal) {
        return isMatch("^-[1-9]\\d*", orginal);
    }

    public static boolean isWholeNumber(String orginal) {
        return isMatch("[+-]{0,1}0", orginal) || isPositiveInteger(orginal) || isNegativeInteger(orginal);
    }

    public static boolean isPositiveDecimal(String orginal){
        return isMatch("\\+{0,1}[0]\\.[1-9]*|\\+{0,1}[1-9]\\d*\\.\\d*", orginal);
    }

    public static boolean isNegativeDecimal(String orginal){
        return isMatch("^-[0]\\.[1-9]*|^-[1-9]\\d*\\.\\d*", orginal);
    }

    public static boolean isDecimal(String orginal){
        return isMatch("[-+]{0,1}\\d+\\.\\d*|[-+]{0,1}\\d*\\.\\d+", orginal);
    }

    public static boolean isNumber(String orginal){
        return isWholeNumber(orginal) || isDecimal(orginal) || isNegativeDecimal(orginal) || isPositiveDecimal(orginal);
    }

    private static boolean isMatch(String regex, String orginal){
        if (orginal == null || orginal.trim().equals("")) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher isNum = pattern.matcher(orginal);
        return isNum.matches();
    }


//    // 根据Unicode编码完美的判断中文汉字和符号
//    public static boolean isChinese(char c) {
//        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
//        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
//                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
//                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
//                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
//                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
//                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
//                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
//            return true;
//        }
//        return false;
//    }
    /**
     * 输入的字符是否是汉字
     * @param a char
     * @return boolean
     */
    public static boolean isChinese(char a) {
        int v = (int)a;
        return (v >=19968 && v <= 171941);
    }
    public static boolean isSymbol(char a){
        String s = a+"";
       return s.matches("[,\\./:\"\\\\\\[\\]\\|`~!@#\\$%\\^&\\*\\(\\)_\\+=<->\\?;'，。、；：‘’“”【】《》？\\{\\}！￥…（）—=]");
    }
}
