package com.wl4g.devops.common.enums;

/**
 * @author vjay
 * @date 2019-07-05 19:13:00
 */
public enum  AggregatorEnum {

    AVG("avg"),LAST("last"),MAX("max"),MIN("min"),SUM("sum");

    private String value;

    public String getValue() {
        return value;
    }

    AggregatorEnum(String value) {
        this.value = value;
    }

    public static AggregatorEnum safeOf(String operator) {
        for (AggregatorEnum t : values()) {
            if (operator==(t.getValue())) {
                return t;
            }
        }
        return null;
    }
}
