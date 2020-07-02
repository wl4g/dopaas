package com.wl4g.devops.erm.dns;

import com.wl4g.devops.common.bean.erm.DnsPrivateDomain;
import com.wl4g.devops.common.bean.erm.DnsPrivateResolution;

/**
 * @author vjay
 * @date 2020-07-02 11:40:00
 */
public interface DnsServerInterface {

    void putDomian(DnsPrivateDomain domian);

    void putHost(String domain, DnsPrivateResolution resolution);

    void delhost(String domian, String host);

    void delDomain(String domian);


}
