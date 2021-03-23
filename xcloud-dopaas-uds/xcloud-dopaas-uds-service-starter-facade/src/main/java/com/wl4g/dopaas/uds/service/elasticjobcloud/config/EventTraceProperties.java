/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wl4g.dopaas.uds.service.elasticjobcloud.config;

import java.util.Optional;

import javax.sql.DataSource;

import org.apache.shardingsphere.elasticjob.tracing.api.TracingConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.base.Strings;

import lombok.Setter;

@Setter
@Component
@ConfigurationProperties(prefix = "event.trace")
public final class EventTraceProperties {

	private String rdbDriver;

	private String rdbUrl;

	private String rdbUsername;

	private String rdbPassword;

	public Optional<TracingConfiguration<DataSource>> getTracingConfiguration() {
		if (!Strings.isNullOrEmpty(rdbDriver) && !Strings.isNullOrEmpty(rdbUrl) && !Strings.isNullOrEmpty(rdbUsername)) {
			DruidDataSource dataSource = new DruidDataSource();
			dataSource.setDriverClassName(rdbDriver);
			dataSource.setUrl(rdbUrl);
			dataSource.setUsername(rdbUsername);
			dataSource.setPassword(rdbPassword);
			return Optional.of(new TracingConfiguration<>("RDB", dataSource));
		}
		return Optional.empty();
	}

}
