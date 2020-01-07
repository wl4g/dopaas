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
package com.wl4g.devops.djob.core.job;

public enum JobAttributeDefine {

	CRON("cron"),

	SHARDING_TOTAL_COUNT("shardingTotalCount"),

	SHARDING_ITEM_PARAMETERS("shardingItemParameters"),

	JOB_PARAMETER("jobParameter"),

	MONITOR_EXECUTION("monitorExecution"),

	MONITOR_PORT("monitorPort"),

	FAILOVER("failover"),

	MAX_TIME_DIFF_SECONDS("maxTimeDiffSeconds"),

	MISFIRE("misfire"),

	JOB_SHARDING_STRATEGY_CLASS("jobShardingStrategyClass"),

	DESCRIPTION("description"),

	DISABLED("disabled"),

	OVERWRITE("overwrite"),

	LISTENER("listener"),

	DISTRIBUTED_LISTENER("distributedListener"),

	DISTRIBUTED_LISTENER_STARTED_TIMEOUT_MILLISECONDS("startedTimeoutMilliseconds"),

	DISTRIBUTED_LISTENER_COMPLETED_TIMEOUT_MILLISECONDS("completedTimeoutMilliseconds"),

	EXECUTOR_SERVICE_HANDLER("executorServiceHandler"),

	JOB_EXCEPTION_HANDLER("jobExceptionHandler"),

	EVENT_TRACE_RDB_DATA_SOURCE("eventTraceRdbDataSource"),

	RECONCILE_INTERVAL_MINUTES("reconcileIntervalMinutes"),

	SCRIPT_COMMAND_LINE("scriptCommandLine"),

	STREAMING_PROCESS("streamingProcess");

	final private String attributeName;

	private JobAttributeDefine(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getAttributeName() {
		return attributeName;
	}

}