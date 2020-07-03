package com.wl4g.devops.erm.dns.config;

import org.apache.commons.lang3.StringUtils;

public class DnsProperties {

    private String prefix;

    private String suffix;

    public String getPrefix() {
        if(StringUtils.isNoneBlank(prefix)){
            return prefix;
        }
        return "";
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        if(StringUtils.isNoneBlank(suffix)){
            return suffix;
        }
        return "";
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
