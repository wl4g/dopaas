package com.wl4g.devops.erm.dns;

import com.wl4g.devops.common.bean.erm.DnsPrivateZone;
import com.wl4g.devops.common.bean.erm.DnsPrivateResolution;
import com.wl4g.devops.components.tools.common.collection.CollectionUtils2;
import com.wl4g.devops.components.tools.common.lang.Assert2;
import com.wl4g.devops.components.tools.common.serialize.JacksonUtils;
import com.wl4g.devops.erm.dns.config.DnsProperties;
import com.wl4g.devops.erm.dns.model.ResolveType;
import com.wl4g.devops.support.redis.JedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author vjay
 * @date 2020-07-02 11:41:00
 */
public class CorednsServer implements DnsServerInterface {

    final private static String DNS_BLACKLIST_KEY = "_dns_blacklist";
    final private static String DNS_WHITELIST_KEY = "_dns_whitelist";

    @Autowired
    private JedisService jedisService;

    @Autowired
    private DnsProperties dnsProperties;

    @Override
    public void putDomian(DnsPrivateZone domian) {
        String domain = domian.getZone();
        int catcheSecond = getDistanceSecondOfTwoDate(new Date(), domian.getDueDate());
        List<DnsPrivateResolution> dnsPrivateResolutions = domian.getDnsPrivateResolutions();
        Map<String, String> hosts = new HashMap();
        for (DnsPrivateResolution dnsPrivateResolution : dnsPrivateResolutions) {
            Map map = buildMap(dnsPrivateResolution);
            hosts.put(dnsPrivateResolution.getHost(), JacksonUtils.toJSONString(map));
        }
        delDomain(domain);
        put(domain, hosts, catcheSecond);
    }

    @Override
    public void putHost(String domain, DnsPrivateResolution dnsPrivateResolution) {
        Map<String, String> hosts = new HashMap();
        Map map = buildMap(dnsPrivateResolution);
        hosts.put(dnsPrivateResolution.getHost(), JacksonUtils.toJSONString(map));
        put(domain, hosts, 0);
    }


    private void put(String domian, Map hosts, int cacheSeconds) {
        if(StringUtils.isNoneBlank(domian) && !CollectionUtils2.isEmpty(hosts)){
            jedisService.setMap(assemblKey(domian), hosts, cacheSeconds);
        }
    }

    @Override
    public void delhost(String domian, String host) {
        jedisService.mapRemove(assemblKey(domian), host);
    }

    @Override
    public void delDomain(String domian) {
        jedisService.del(assemblKey(domian));
    }

    private String assemblKey(String domian) {
        return dnsProperties.getPrefix() + domian + "." + dnsProperties.getSuffix();
    }

    private Map buildMap(DnsPrivateResolution dnsPrivateResolution) {
        Assert2.notNullOf(dnsPrivateResolution, "dnsPrivateResolution");
        Assert2.hasTextOf(dnsPrivateResolution.getResolveType(), "resolveType");
        String resolveType = dnsPrivateResolution.getResolveType().toLowerCase();
        Map result = new HashMap();
        Map obj = new HashMap();

        switch (ResolveType.of(resolveType)) {
            case A:
                obj.put("ip", dnsPrivateResolution.getValue());
                obj.put("ttl", dnsPrivateResolution.getTtl());
                ArrayList<Map> a = new ArrayList<>();
                a.add(obj);
                result.put("a", a);
                break;
            case AAAA:
                obj.put("ip", dnsPrivateResolution.getValue());
                obj.put("ttl", dnsPrivateResolution.getTtl());
                ArrayList<Map> aaaa = new ArrayList<>();
                aaaa.add(obj);
                result.put("aaaa", aaaa);
                break;
            case CNAME:
                obj.put("host", dnsPrivateResolution.getValue());
                obj.put("ttl", dnsPrivateResolution.getTtl());
                ArrayList<Map> cname = new ArrayList<>();
                cname.add(obj);
                result.put("cname", cname);
                break;
            case TXT:
                obj.put("text", dnsPrivateResolution.getValue());
                obj.put("ttl", dnsPrivateResolution.getTtl());
                ArrayList<Map> txt = new ArrayList<>();
                txt.add(obj);
                result.put("txt", txt);
                break;
            case NS:
                obj.put("host", dnsPrivateResolution.getValue());
                obj.put("ttl", dnsPrivateResolution.getTtl());
                ArrayList<Map> ns = new ArrayList<>();
                ns.add(obj);
                result.put("ns", ns);
                break;
            case MX:
                obj.put("host", dnsPrivateResolution.getValue());
                obj.put("priority", dnsPrivateResolution.getPriority());
                obj.put("ttl", dnsPrivateResolution.getTtl());
                ArrayList<Map> mx = new ArrayList<>();
                mx.add(obj);
                result.put("mx", mx);
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
                ArrayList<Map> srv = new ArrayList<>();
                srv.add(obj);
                result.put("srv", srv);
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

    @Override
    public void addDnsPrivateBlacklist(String black,String white){
        if(StringUtils.isNotBlank(black)){
            jedisService.setSetAdd(DNS_BLACKLIST_KEY,black);
        }
        if(StringUtils.isNotBlank(white)){
            jedisService.setSetAdd(DNS_WHITELIST_KEY,white);
        }
    }

    @Override
    public void removeDnsPrivateBlacklist(String black,String white){
        if(StringUtils.isNotBlank(black)){
            jedisService.delSetMember(DNS_BLACKLIST_KEY,black);
        }
        if(StringUtils.isNotBlank(white)){
            jedisService.delSetMember(DNS_WHITELIST_KEY,white);
        }
    }

    @Override
    public void reloadDnsPrivateBlacklist(Set<String> blacks,Set<String> whites){
        jedisService.del(DNS_BLACKLIST_KEY);
        jedisService.del(DNS_WHITELIST_KEY);
        if(!CollectionUtils2.isEmpty(blacks)){
            jedisService.setSet(DNS_BLACKLIST_KEY,blacks,0);
        }
        if(!CollectionUtils2.isEmpty(whites)){
            jedisService.setSet(DNS_WHITELIST_KEY,whites,0);
        }
    }

    private static int getDistanceSecondOfTwoDate(Date before, Date after) {
        long beforeTime = before.getTime();
        long afterTime = after.getTime();
        long result = ((afterTime - beforeTime) / (1000));
        if (result > Integer.MAX_VALUE || result < 0) {
            result = 0;
        }
        return (int) result;
    }
}
