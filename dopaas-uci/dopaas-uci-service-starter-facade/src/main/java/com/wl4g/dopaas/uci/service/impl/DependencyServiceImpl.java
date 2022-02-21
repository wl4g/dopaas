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
package com.wl4g.dopaas.uci.service.impl;

import com.wl4g.dopaas.uci.data.DependencyDao;
import com.wl4g.dopaas.uci.service.DependencyService;
import com.wl4g.dopaas.common.bean.uci.Dependency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

/**
 * Dependency service implements
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-22 11:39:00
 */
@Service
public class DependencyServiceImpl implements DependencyService {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	private @Autowired DependencyDao dependencyDao;

	/**
	 * Get hierarchy project dependency.
	 *
	 * @param projectId
	 * @param sets
	 * @return
	 */
	public LinkedHashSet<Dependency> getHierarchyDependencys(Long projectId, LinkedHashSet<Dependency> sets) {
		if (Objects.isNull(sets)) {
			sets = new LinkedHashSet<>();
		}
		List<Dependency> dependencies = dependencyDao.getParentsByProjectId(projectId);
		if (dependencies != null && dependencies.size() > 0) {
			for (Dependency depd : dependencies) {
				getHierarchyDependencys(depd.getDependentId(), sets);
				sets.add(depd);
			}
		}
		return sets;
	}

}