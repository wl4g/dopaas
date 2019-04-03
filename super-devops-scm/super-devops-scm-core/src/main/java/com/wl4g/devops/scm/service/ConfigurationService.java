package com.wl4g.devops.scm.service;

import com.wl4g.devops.common.bean.scm.*;
import com.wl4g.devops.common.bean.scm.model.*;

import java.util.List;

/**
 * 配置管理Service接口
 * 
 * @date 2018年9月20日
 */
public interface ConfigurationService {

	public void configure(VersionOfDetail vod);

	public boolean update(ConfigVersion vod);

	public List<ConfigVersionList> list(ConfigVersionList agl);

	public boolean deleteGuration(String id);

	public boolean updateGuration(VersionContentBean guration);

	public List<VersionContentBean> selectVersion(int id);

	public ConfigSourceBean findSource(GetReleaseModel getRelease);

	public void updateReleaseDetail(ReportModel report);
}
