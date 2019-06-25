package com.wl4g.devops.common.bean.umc.model.third;

import com.wl4g.devops.common.bean.umc.model.Base;

import java.util.Map;

/**
 * @author vjay
 * @date 2019-06-20 15:52:00
 */
public class ZookeeperStatInfo extends Base {
	private static final long serialVersionUID = -6260009722603808542L;
	private Map<String, String> properties;

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
}
