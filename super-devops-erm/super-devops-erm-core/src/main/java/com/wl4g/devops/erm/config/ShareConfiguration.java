package com.wl4g.devops.erm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShareConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "fs")
    public FsProperties fsProperties() {
        return new FsProperties();
    }
}
