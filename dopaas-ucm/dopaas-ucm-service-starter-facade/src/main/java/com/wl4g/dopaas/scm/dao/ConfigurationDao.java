/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.ucm.dao;

import com.wl4g.dopaas.ucm.bean.*;
import com.wl4g.dopaas.ucm.common.model.FetchReleaseConfigRequest;
import com.wl4g.dopaas.ucm.common.model.ReportChangedRequest;

import java.util.List;
import java.util.Map;

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

    public ConfigSourceBean findSource(FetchReleaseConfigRequest get);

    public void updateReleaseDetail(ReportChangedRequest report);
}