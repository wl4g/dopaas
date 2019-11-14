package com.wl4g.devops.share.service;

import com.wl4g.devops.common.bean.share.AppHost;

import java.util.List;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
public interface HostService {

	List<AppHost> list(String name, String hostname, Integer idcId);
}
