package com.wl4g.devops.ci.pmplatform.model.redmine;

/**
 * @author vjay
 * @date 2020-01-03 14:17:00
 */
public class BaseModel {

    private int total_count;
    private int offset;
    private int limit;

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
