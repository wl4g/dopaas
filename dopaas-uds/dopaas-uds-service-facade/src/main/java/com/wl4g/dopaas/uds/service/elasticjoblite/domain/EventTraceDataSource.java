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

package com.wl4g.dopaas.uds.service.elasticjoblite.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.wl4g.dopaas.uds.service.elasticjoblite.exception.JdbcDriverNotFoundException;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Event tracing data source.
 */
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
@Slf4j
public final class EventTraceDataSource {

	private final EventTraceDataSourceConfig eventTraceDataSourceConfiguration;

	/**
	 * Initialize data source.
	 */
	public void init() {
		log.debug("ElasticJob: data source init, connection url is: {}.", eventTraceDataSourceConfiguration.getUrl());
		try {
			Class.forName(eventTraceDataSourceConfiguration.getDriver());
			DriverManager.getConnection(eventTraceDataSourceConfiguration.getUrl(),
					eventTraceDataSourceConfiguration.getUsername(), eventTraceDataSourceConfiguration.getPassword());
		} catch (final SQLException ex) {
			throw new RuntimeException(ex);
		} catch (final ClassNotFoundException ex) {
			throw new JdbcDriverNotFoundException(ex.getLocalizedMessage());
		}
	}
}
