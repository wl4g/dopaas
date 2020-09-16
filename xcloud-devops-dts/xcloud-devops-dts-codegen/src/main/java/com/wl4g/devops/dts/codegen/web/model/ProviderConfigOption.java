package com.wl4g.devops.dts.codegen.web.model;

import com.wl4g.devops.dts.codegen.engine.GeneratorProvider;

/**
 * @author vjay
 * @date 2020-09-16 16:05:00
 */
public class ProviderConfigOption extends GeneratorProvider.ExtraConfigSupport.ConfigOption {

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
