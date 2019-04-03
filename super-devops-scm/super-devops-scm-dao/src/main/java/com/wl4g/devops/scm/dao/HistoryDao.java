package com.wl4g.devops.scm.dao;

import com.wl4g.devops.common.bean.scm.*;

import java.util.List;
import java.util.Map;

/**
 * 应用组管理DAO接口
 * 
 * @author sut
 * @date 2018年9月20日
 */
public interface HistoryDao {

	public Long insert(HistoryOfDetail historyOfDetail);

	public long insertDetail(ReleaseDetail detail);

	public Version versionselect(Version history);

	public boolean delete(ReleaseHistory history);

	public boolean versionDelete(Version history);

	public boolean versionUpdate(Version history);

	public List<ReleaseHistory> select(String of_id, String of_type, String updateDate, String createDate, int status);

	public List<ConfigVersionList> list(ConfigVersionList agl);

	public List<VersionList> versionList(Map<String, Object> param);

	public boolean updateHistory(ReleaseDetail detail);

	public List<ReleaseHistoryList> historylist(ReleaseHistoryList agl);

	// 插入轨迹
	public boolean insertReleDetail(ReleaseDetail releaseDetail);

	public ReleaseDetail reledetailselect(ReleaseDetail releaseDetail);
}
