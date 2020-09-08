package com.wl4g.devops.dts.codegen.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author vjay
 * @date 2020-09-08 18:11:00
 */
public class ParseUtils {

    private static Pattern linePattern = Pattern.compile("_(\\w)");


    public static String tableName2className(String tableName){
        if(StringUtils.isBlank(tableName)){
            return tableName;
        }
        int i = tableName.indexOf("_");
        if(i>=0){
            String sub = tableName.substring(i+1,tableName.length());
            return lineToHump(sub);
        }
        return tableName;
    }

    public static String tableName2ClassName(String tableName){
        if(StringUtils.isBlank(tableName)){
            return tableName;
        }
        int i = tableName.indexOf("_");
        if(i>=0){
            String sub = tableName.substring(i+1,tableName.length());
            return captureName(lineToHump(sub));
        }
        return tableName;
    }

    /**
     * 下划线转驼峰
     */
    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 首字母大写
     */
    public static String captureName(String name) {
        char[] cs = name.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

    public static void main(String[] args){
        System.out.println(tableName2className("ci_pipeline_test"));
        System.out.println(tableName2ClassName("ci_pipeline_test"));
    }


}
