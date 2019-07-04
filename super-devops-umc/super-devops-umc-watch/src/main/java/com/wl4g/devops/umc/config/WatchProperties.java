package com.wl4g.devops.umc.config;

import com.wl4g.devops.support.task.GenericTaskRunner.TaskProperties;

/**
 * Watch properties .
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2017年11月16日
 * @since
 */
public class WatchProperties extends TaskProperties {
	private static final long serialVersionUID = 5508561234067054195L;

	private String zkServers;

	private String namespace;

	private String cron;

	private int totalCount;

	private String itemParams;

	private int fetchCacheTime = -1;

	public String getZkServers() {
		return zkServers;
	}

	public void setZkServers(String zkServers) {
		this.zkServers = zkServers;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public String getItemParams() {
		return itemParams;
	}

	public void setItemParams(String itemParams) {
		this.itemParams = itemParams;
	}

	public int getFetchCacheTime() {
		return fetchCacheTime;
	}

	public void setFetchCacheTime(int fetchCacheTime) {
		this.fetchCacheTime = fetchCacheTime;
	}

}
