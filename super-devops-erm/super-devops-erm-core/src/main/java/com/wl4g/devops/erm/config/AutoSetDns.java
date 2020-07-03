package com.wl4g.devops.erm.config;

import com.wl4g.devops.erm.service.DnsPrivateZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author vjay
 * @date 2019-04-01 17:51:00
 */
@Component
public class AutoSetDns implements ApplicationRunner {

    @Autowired
    private DnsPrivateZoneService dnsPrivateDomainService;

    @Override
    public void run(ApplicationArguments var) throws Exception {
        try {
            //WeConfig.getAccessToken();
            dnsPrivateDomainService.loadDnsAtStart();
        }catch (Exception e){
            throw e;
        }
    }
}


