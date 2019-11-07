package com.wl4g.devops.iam.common.web.model;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

import com.google.common.annotations.Beta;

import static com.wl4g.devops.common.utils.serialize.JacksonUtils.toJSONString;

/**
 * Sessions query model.
 *
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-31
 * @since
 */
@Beta
public class SessionQueryModel implements Serializable {
    private static final long serialVersionUID = 5766036036946339544L;

    /**
     * Scan cursor.
     */
    @NotBlank(message = "Invalid argument cursor.(e.g. cursor=0@0)")
    private String cursor = "0@0";

    /**
     * Page size.
     */
    private int limit = 200;

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return toJSONString(this);
    }

}
