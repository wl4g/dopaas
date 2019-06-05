/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.dao.scm;

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