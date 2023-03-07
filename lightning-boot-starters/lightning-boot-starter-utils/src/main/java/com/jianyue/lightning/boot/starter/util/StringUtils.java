package com.jianyue.lightning.boot.starter.util;

public class StringUtils {
    public static String normalize(String path) {
        return normalize(path, false);
    }

    public static String normalize(String path, boolean needLeaf) {
        return "/" + org.springframework.util.StringUtils.trimTrailingCharacter(org.springframework.util.StringUtils.trimLeadingCharacter(path, '/'), '/') + (needLeaf ? "/" : "");

    }
}
