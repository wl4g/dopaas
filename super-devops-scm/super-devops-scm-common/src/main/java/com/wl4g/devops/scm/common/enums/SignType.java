package com.wl4g.devops.scm.common.enums;

/**
 * @author vjay
 * @date 2019-06-05 17:47:00
 */
public enum SignType {

    AES(1),
    DES(2),
    RSA(3);

    final private int type;

    SignType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }


    public static SignType of(String type) {
        SignType wh = safeOf(type);
        if (wh == null) {
            throw new IllegalArgumentException(String.format("Illegal action '%s'", type));
        }
        return wh;
    }

    public static SignType safeOf(String type) {
        for (SignType t : values()) {
            if (String.valueOf(type).equalsIgnoreCase(t.name())) {
                return t;
            }
        }
        return null;
    }






}
