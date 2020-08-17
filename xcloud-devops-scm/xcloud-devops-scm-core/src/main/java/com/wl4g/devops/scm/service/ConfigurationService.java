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
package com.wl4g.devops.scm.service;

import com.wl4g.devops.scm.bean.*;
import com.wl4g.devops.scm.common.command.*;

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

	public ConfigSourceBean findSource(WatchCommand getRelease);

	public void updateReleaseDetail(ReportCommand report);
}