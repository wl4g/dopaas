package com.wl4g.devops.scm.service;

import com.wl4g.devops.common.bean.scm.*;

import java.util.List;
import java.util.Map;

/**
 * 历史版本Service接口
 * 
 * @author sut
 * @date 2018年9月20日
 */
public interface HistoryService {

	public void insert(HistoryOfDetail historyOfDetail);

	public void insertDetail(ReleaseDetail detail);

	public void releaseRollback(ConfigVersionList agl);

	public boolean delete(ReleaseHistory history);

	public boolean versionDelete(Version history);

	public boolean versionUpdate(Version history);

	public List<ReleaseHistory> select(String of_id, String of_type, String updateDate, String createDate, int status);

	public List<ConfigVersionList> list(ConfigVersionList agl);

	public List<VersionList> versionList(Map<String, Object> param);

	public List<ReleaseHistoryList> historylist(ReleaseHistoryList agl);

	public boolean updateHistory(ReleaseDetail detail);

	// 插入轨迹
	public boolean insertReleDetail(ReleaseDetail detail);

	public ReleaseDetail reledetailselect(ReleaseDetail releaseDetail);
}
