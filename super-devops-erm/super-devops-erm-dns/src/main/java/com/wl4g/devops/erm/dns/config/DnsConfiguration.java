package com.wl4g.devops.erm.dns.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.erm.dns.handler.JedisCorednsStoreHandler;

@Configuration
public class DnsConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "dns")
    public DnsProperties dnsProperties() {
        return new DnsProperties();
    }

    @Bean
    public JedisCorednsStoreHandler corednsServer(){
        return new JedisCorednsStoreHandler();
    }
}
