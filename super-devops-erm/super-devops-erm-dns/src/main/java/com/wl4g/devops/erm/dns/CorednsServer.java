package com.wl4g.devops.erm.dns;

import com.wl4g.devops.common.bean.erm.DnsPrivateDomain;
import com.wl4g.devops.common.bean.erm.DnsPrivateResolution;
import com.wl4g.devops.components.tools.common.lang.Assert2;
import com.wl4g.devops.components.tools.common.serialize.JacksonUtils;
import com.wl4g.devops.erm.dns.config.DnsProperties;
import com.wl4g.devops.erm.dns.model.ResolveType;
import com.wl4g.devops.support.redis.JedisService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vjay
 * @date 2020-07-02 11:41:00
 */
public class CorednsServer implements DnsServerInterface {

    @Autowired
    private JedisService jedisService;

    @Autowired
    private DnsProperties dnsProperties;

    @Override
    public void putDomian(DnsPrivateDomain domian) {
        String domain = domian.getZone();
        int catcheSecond = getDistanceSecondOfTwoDate(new Date(),domian.getDueDate());
        List<DnsPrivateResolution> dnsPrivateResolutions = domian.getDnsPrivateResolutions();
        Map<String,String> hosts = new HashMap();
        for(DnsPrivateResolution dnsPrivateResolution : dnsPrivateResolutions){
            Map map = buildMap(dnsPrivateResolution);
            hosts.put(dnsPrivateResolution.getHost(), JacksonUtils.toJSONString(map));
        }
        delDomain(domain);
        put(domain, hosts, catcheSecond);
    }

    @Override
    public void putHost(String domain, DnsPrivateResolution dnsPrivateResolution) {
        Map<String,String> hosts = new HashMap();
        Map map = buildMap(dnsPrivateResolution);
        hosts.put(dnsPrivateResolution.getHost(), JacksonUtils.toJSONString(map));
        put(domain, hosts, 0);
    }


    private void put(String domian, Map hosts, int cacheSeconds) {
        jedisService.setMap(assemblKey(domian), hosts, cacheSeconds);
    }

    @Override
    public void delhost(String domian, String host) {
        jedisService.mapRemove(assemblKey(domian), host);
    }

    @Override
    public void delDomain(String domian) {
        jedisService.del(assemblKey(domian));
    }

    private String assemblKey(String domian){
        return dnsProperties.getPrefix()+domian+"."+dnsProperties.getSuffix();
    }

    private Map buildMap(DnsPrivateResolution dnsPrivateResolution) {
        Assert2.notNullOf(dnsPrivateResolution, "dnsPrivateResolution");
        Assert2.hasTextOf(dnsPrivateResolution.getResolveType(), "resolveType");
        String resolveType = dnsPrivateResolution.getResolveType().toLowerCase();
        Map result = new HashMap();
        Map obj = new HashMap();

        switch (ResolveType.of(resolveType)) {
            case A:
                obj.put("ip4", dnsPrivateResolution.getValue());
                obj.put("ttl", dnsPrivateResolution.getTtl());
                result.put("a", obj);
                break;
            case AAAA:
                obj.put("ip6", dnsPrivateResolution.getValue());
                obj.put("ttl", dnsPrivateResolution.getTtl());
                result.put("aaaa", obj);
                break;
            case CNAME:
                obj.put("host", dnsPrivateResolution.getValue());
                obj.put("ttl", dnsPrivateResolution.getTtl());
                result.put("cname", obj);
                break;
            case TXT:
                obj.put("text", dnsPrivateResolution.getValue());
                obj.put("ttl", dnsPrivateResolution.getTtl());
                result.put("txt", obj);
                break;
            case NS:
                obj.put("host", dnsPrivateResolution.getValue());
                obj.put("ttl", dnsPrivateResolution.getTtl());
                result.put("ns", obj);
                break;
            case MX:
                obj.put("host", dnsPrivateResolution.getValue());
                obj.put("priority", dnsPrivateResolution.getPriority());
                obj.put("ttl", dnsPrivateResolution.getTtl());
                result.put("mx", obj);
                break;
            case SRV:
                //主机名+空格+优先级+空格+权重+空格+端口。
                String value = dnsPrivateResolution.getValue();
                String[] split = value.split("\\s+");
                Assert2.isTrue(split.length == 4, "SRV value is illegal");
                obj.put("host", split[0]);
                obj.put("port", split[3]);
                obj.put("priority", split[1]);
                obj.put("weight", split[2]);
                obj.put("ttl", dnsPrivateResolution.getTtl());
                result.put("srv", obj);
                break;
            case SOA:
                String value1 = dnsPrivateResolution.getValue();
                String[] split1 = value1.split("\\s+");
                Assert2.isTrue(split1.length == 5, "SOA value is illegal");
                obj.put("mbox", split1[0]);
                obj.put("ns", split1[1]);
                obj.put("refresh", split1[2]);
                obj.put("retry", split1[3]);
                obj.put("expire", split1[4]);
                obj.put("ttl", dnsPrivateResolution.getTtl());
                result.put("soa", obj);
                break;
            default:
                break;
        }
        return result;
    }

    private static int getDistanceSecondOfTwoDate(Date before, Date after) {
        long beforeTime = before.getTime();
        long afterTime = after.getTime();
        long result = ((afterTime - beforeTime) / (1000));
        if(result > Integer.MAX_VALUE){
            result = 0;
        }
        return (int) result;
    }
}
