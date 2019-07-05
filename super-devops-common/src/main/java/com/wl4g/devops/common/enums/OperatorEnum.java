package com.wl4g.devops.common.enums;

/**
 * @author vjay
 * @date 2019-07-05 10:13:00
 */
public enum OperatorEnum {

    EQ(1),GT(2),GTE(3),LT(4),LTE(5);

    private int value;

    public int getValue() {
        return value;
    }

    OperatorEnum(int value) {
        this.value = value;
    }

    public static OperatorEnum safeOf(int operator) {
        for (OperatorEnum t : values()) {
            if (operator==(t.getValue())) {
                return t;
            }
        }
        return null;
    }

}
