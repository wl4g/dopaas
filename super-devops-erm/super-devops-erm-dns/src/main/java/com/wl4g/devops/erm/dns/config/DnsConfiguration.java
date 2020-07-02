package com.wl4g.devops.erm.dns.config;

import com.wl4g.devops.erm.dns.CorednsServer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DnsConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "dns")
    public DnsProperties dnsProperties() {
        return new DnsProperties();
    }

    @Bean
    public CorednsServer corednsServer(){
        return new CorednsServer();
    }
}
