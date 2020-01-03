package com.wl4g.devops.ci.pmplatform.constant;


/**
 * @author vjay
 * @date 2020-01-03 14:41:00
 */
public enum  PlatformEnum {

    Redmine,// Redmine
    Other// other
    ;


    public static PlatformEnum of(String s) {
        PlatformEnum wh = safeOf(s);
        if (wh == null) {
            throw new IllegalArgumentException(String.format("Illegal PlatformType '%s'", s));
        }
        return wh;
    }


    public static PlatformEnum safeOf(String s) {
        for (PlatformEnum t : values()) {
            if (t.name().equalsIgnoreCase(s)) {
                return t;
            }
        }
        return null;
    }

}
