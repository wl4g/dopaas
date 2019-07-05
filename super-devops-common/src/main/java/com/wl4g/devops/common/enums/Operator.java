package com.wl4g.devops.common.enums;

/**
 * @author vjay
 * @date 2019-07-05 10:13:00
 */
public enum  Operator{

    EQ(1),GT(2),GTE(3),LT(4),LTE(5);

    private int value;

    public int getValue() {
        return value;
    }

    Operator(int value) {
        this.value = value;
    }

    public static Operator safeOf(int operator) {
        for (Operator t : values()) {
            if (operator==(t.getValue())) {
                return t;
            }
        }
        return null;
    }

}
