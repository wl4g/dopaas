package com.wl4g.devops.umc.store.opentsdb.client.bean.request;

/**
 * api地址
 *
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2019/2/23 下午12:49
 * @Version: 1.0
 */
public enum Api {

    /***
     * path对应api地址
     */
    PUT("/api/put"),
    PUT_DETAIL("/api/put?details=true"),
    QUERY("/api/query"),
    LAST("/api/query/last"),
    SUGGEST("/api/suggest");

    private String path;

    Api(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
