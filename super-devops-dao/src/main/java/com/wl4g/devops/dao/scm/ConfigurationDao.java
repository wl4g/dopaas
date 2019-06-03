/*
 * Copyright 2015 the original author or authors.
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
import com.wl4g.devops.common.bean.scm.model.GetRelease;
import com.wl4g.devops.common.bean.scm.model.ReportInfo;

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

	public ConfigSourceBean findSource(GetRelease get);

	public void updateReleaseDetail(ReportInfo report);
}