/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.erm.dns.handler;

import com.wl4g.components.common.collection.CollectionUtils2;
import com.wl4g.components.common.lang.Assert2;
import com.wl4g.components.common.serialize.JacksonUtils;
import com.wl4g.components.core.bean.erm.DnsPrivateResolution;
import com.wl4g.components.core.bean.erm.DnsPrivateZone;
import com.wl4g.components.support.redis.jedis.JedisService;
import com.wl4g.devops.erm.dns.config.DnsProperties;
import com.wl4g.devops.erm.dns.handler.stardand.ResolvingType;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import static com.wl4g.components.common.collection.CollectionUtils2.isEmpty;
import static com.wl4g.components.common.lang.Assert2.isTrue;
import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.*;

/**
 * {@link JedisCorednsZoneHandler}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @date 2020-07-02 11:41:00
 * @see
 */
public class JedisCorednsZoneHandler implements DnsZoneHandler {

	@Autowired
	private DnsProperties config;

	@Autowired
	private JedisService jedisService;

	// TODO Coredns resolving record using bean!!!
	@SuppressWarnings({ "rawtypes" })
	@Override
	public void putDomian(DnsPrivateZone dnsPrivateZone) {
		String zone = dnsPrivateZone.getZone();
		int catcheSecond = getDistanceSecondOfTwoDate(new Date(), dnsPrivateZone.getDueDate());
		List<DnsPrivateResolution> dnsPrivateResolutions = dnsPrivateZone.getDnsPrivateResolutions();
		Map<String, String> hosts = new HashMap<>();
		for (DnsPrivateResolution privateRecord : dnsPrivateResolutions) {
			Map map = buildMap(privateRecord);
			hosts.put(privateRecord.getHost(), toJSONString(map));
		}
		delDomain(zone);
		put(zone, hosts, catcheSecond);
	}

	// TODO Coredns resolving record using bean!!!
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void putHost(DnsPrivateZone privateZone, DnsPrivateResolution privateRecord) {
		Map hosts = new HashMap<>();
		Map map = buildMap(privateRecord);
		int catcheSecond = getDistanceSecondOfTwoDate(new Date(), privateZone.getDueDate());
		hosts.put(privateRecord.getHost(), JacksonUtils.toJSONString(map));
		put(privateZone.getZone(), hosts, catcheSecond);
	}

	// TODO Coredns resolving record using bean!!!
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void put(String zone, Map hosts, int cacheSeconds) {
		if (!isBlank(zone) && !isEmpty(hosts)) {
			jedisService.setMap(getZoneStoreKey(zone), hosts, cacheSeconds);
		}
	}

	// TODO Coredns resolving record using bean!!!
	@Override
	public void delhost(String domian, String host) {
		jedisService.mapRemove(getZoneStoreKey(domian), host);
	}

	@Override
	public void delDomain(String domian) {
		jedisService.del(getZoneStoreKey(domian));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map buildMap(DnsPrivateResolution privateResolute) {
		Assert2.notNullOf(privateResolute, "dnsPrivateResolution");
		Assert2.hasTextOf(privateResolute.getResolveType(), "resolveType");
		String resolveType = privateResolute.getResolveType().toLowerCase();
		Map zoneRecords = new HashMap<>();
		Map record = new HashMap<>();

		switch (ResolvingType.of(resolveType)) {
		case A:
			record.put("ip", privateResolute.getValue());
			record.put("ttl", privateResolute.getTtl());
			List<Map<String, String>> a = new ArrayList<>();
			a.add(record);
			zoneRecords.put("a", a);
			break;
		case AAAA:
			record.put("ip", privateResolute.getValue());
			record.put("ttl", privateResolute.getTtl());
			List<Map<String, String>> aaaa = new ArrayList<>();
			aaaa.add(record);
			zoneRecords.put("aaaa", aaaa);
			break;
		case CNAME:
			record.put("host", privateResolute.getValue());
			record.put("ttl", privateResolute.getTtl());
			List<Map<String, String>> cname = new ArrayList<>();
			cname.add(record);
			zoneRecords.put("cname", cname);
			break;
		case TXT:
			record.put("text", privateResolute.getValue());
			record.put("ttl", privateResolute.getTtl());
			List<Map<String, String>> txt = new ArrayList<>();
			txt.add(record);
			zoneRecords.put("txt", txt);
			break;
		case NS:
			record.put("host", privateResolute.getValue());
			record.put("ttl", privateResolute.getTtl());
			List<Map<String, String>> ns = new ArrayList<>();
			ns.add(record);
			zoneRecords.put("ns", ns);
			break;
		case MX:
			record.put("host", privateResolute.getValue());
			record.put("priority", privateResolute.getPriority());
			record.put("ttl", privateResolute.getTtl());
			List<Map<String, String>> mx = new ArrayList<>();
			mx.add(record);
			zoneRecords.put("mx", mx);
			break;
		case SRV:
			// 主机名+空格+优先级+空格+权重+空格+端口。
			String value = privateResolute.getValue();
			String[] split = value.split("\\s+");
			Assert2.isTrue(split.length == 4, "SRV value is illegal");
			record.put("host", split[0]);
			record.put("port", split[3]);
			record.put("priority", split[1]);
			record.put("weight", split[2]);
			record.put("ttl", privateResolute.getTtl());
			List<Map<String, String>> srv = new ArrayList<>();
			srv.add(record);
			zoneRecords.put("srv", srv);
			break;
		case SOA:
			String value1 = privateResolute.getValue();
			String[] split1 = value1.split("\\s+");
			isTrue(split1.length == 5, "SOA value is illegal");
			record.put("mbox", split1[0]);
			record.put("ns", split1[1]);
			record.put("refresh", split1[2]);
			record.put("retry", split1[3]);
			record.put("expire", split1[4]);
			record.put("ttl", privateResolute.getTtl());
			zoneRecords.put("soa", record);
			break;
		default:
			break;
		}
		return zoneRecords;
	}

	@Override
	public void addDnsPrivateBlacklist(String blacklist, String whitelist) {
		if (!isBlank(blacklist)) {
			jedisService.setSetAdd(getZoneBlackListStoreKey(), blacklist);
		}
		if (!isBlank(whitelist)) {
			jedisService.setSetAdd(getZoneWhiteListStoreKey(), whitelist);
		}
	}

	@Override
	public void removeDnsPrivateBlacklist(String black, String white) {
		if (StringUtils.isNotBlank(black)) {
			jedisService.delSetMember(DEFAULT_ZONE_BLACKLIST_SUFFIX, black);
		}
		if (StringUtils.isNotBlank(white)) {
			jedisService.delSetMember(DEFAULT_ZONE_WHITELIST_SUFFIX, white);
		}
	}

	@Override
	public void reloadDnsPrivateBlacklist(Set<String> blacks, Set<String> whites) {
		jedisService.del(DEFAULT_ZONE_BLACKLIST_SUFFIX);
		jedisService.del(DEFAULT_ZONE_WHITELIST_SUFFIX);
		if (!CollectionUtils2.isEmpty(blacks)) {
			jedisService.setSet(DEFAULT_ZONE_BLACKLIST_SUFFIX, blacks, 0);
		}
		if (!CollectionUtils2.isEmpty(whites)) {
			jedisService.setSet(DEFAULT_ZONE_WHITELIST_SUFFIX, whites, 0);
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

	/**
	 * Gets zone store cache key.
	 * 
	 * @param zone
	 * @return
	 */
	private String getZoneStoreKey(String zone) {
		return config.getPrefix().concat(":").concat(zone).concat(".");
	}

	/**
	 * Gets zone blacklist store cache key.
	 * 
	 * @return
	 */
	private String getZoneBlackListStoreKey() {
		return config.getPrefix().concat(DEFAULT_ZONE_BLACKLIST_SUFFIX);
	}

	/**
	 * Gets zone whitelist store cache key.
	 * 
	 * @return
	 */
	private String getZoneWhiteListStoreKey() {
		return config.getPrefix().concat(DEFAULT_ZONE_WHITELIST_SUFFIX);
	}

	final private static String DEFAULT_ZONE_BLACKLIST_SUFFIX = ":dns:blacklist";
	final private static String DEFAULT_ZONE_WHITELIST_SUFFIX = ":dns:whitelist";

}