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
package com.wl4g.devops.umc.derby;

import com.wl4g.components.core.bean.umc.model.proto.MetricModel;
import com.wl4g.devops.umc.store.MetricStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

/**
 * Derby foundation store
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public class DerbyMetricStore implements MetricStore {

	final JdbcTemplate jdbcTemplate;

	public DerbyMetricStore(JdbcTemplate jdbcTemplate) {
		Assert.notNull(jdbcTemplate, "JdbcTemplate must not be null");
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public boolean save(MetricModel.MetricAggregate aggregate) {
		return false;
	}
}