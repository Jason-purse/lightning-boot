package com.jianyue.lightning.boot.starter.util;

public class StringUtils {
    public static String normalize(String path) {
        return normalize(path, false);
    }

    /**
     * 格式化为绝对路径
     */
    public static String normalize(String path, boolean needLeaf) {
        return "/" + org.springframework.util.StringUtils.trimTrailingCharacter(org.springframework.util.StringUtils.trimLeadingCharacter(path, '/'), '/') + (needLeaf ? "/" : "");
    }

    public static String rightNormalize(String path, boolean needLeaf) {
        return org.springframework.util.StringUtils.trimTrailingCharacter(org.springframework.util.StringUtils.trimLeadingCharacter(path, '/'), '/') + (needLeaf ? "/" : "");
    }

    public static String leftNormalize(String path, boolean needLeaf) {
        return  (needLeaf ? "/" : "") + org.springframework.util.StringUtils.trimTrailingCharacter(org.springframework.util.StringUtils.trimLeadingCharacter(path, '/'), '/');
    }
}
