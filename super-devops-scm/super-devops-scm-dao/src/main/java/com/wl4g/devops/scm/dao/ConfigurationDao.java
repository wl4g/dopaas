package com.wl4g.devops.scm.dao;

import com.wl4g.devops.common.bean.scm.*;
import com.wl4g.devops.common.bean.scm.model.GetReleaseModel;
import com.wl4g.devops.common.bean.scm.model.ReportModel;

import java.util.List;
import java.util.Map;

/**
 * 配置DAO接口
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月6日
 * @since
 */
public interface ConfigurationDao {

	public Long insert(VersionOfDetail vod);

	public boolean update(ConfigVersion vod);

	public List<ConfigVersionList> list(ConfigVersionList agl);

	public boolean insertDetail(Map<String, Object> vMap);

	public boolean deleteConfigGuration(String id);

	public boolean updateGuration(VersionContentBean instance);

	public void updateNode(Map<String, Object> nMap);

	public String nodeIsVersion(Map<String, Object> nvMap);

	public List<VersionContentBean> selectVersion(int id);

	public ConfigSourceBean findSource(GetReleaseModel getRelease);

	public void updateReleaseDetail(ReportModel report);
}
