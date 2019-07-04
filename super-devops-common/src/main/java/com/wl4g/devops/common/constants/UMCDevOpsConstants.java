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
package com.wl4g.devops.common.constants;

/**
 * DevOps UMC Constants.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月13日
 * @since
 */
public abstract class UMCDevOpsConstants extends DevOpsConstants {

	//
	// Common
	//

	/** Administrator dashboard's base URI. */
	final public static String URI_DASHBOARD = "/dashboard/";

	/** Receiver endpoint based URI */
	final public static String URI_RECEIVER = "/receiver";

	/** Physical total */
	final public static String URI_PHYSICAL = "physical";

	/** Docker */
	final public static String URI_VIRTUAL_DOCKER = "docker";

	/** Redis */
	final public static String URI_REDIS = "redis";

	/** Zookeeper */
	final public static String URI_ZOOKEEPER = "zookeeper";

	/** Kafka */
	final public static String URI_KAFKA = "kafka";

	/** Kafka */
	final public static String URI_METRIC = "metric";

	//
	// Server KAFKA client
	//

	/** KAFKA receiver topic pattern */
	final public static String TOPIC_RECEIVE_PATTERN = "umc_agent_metrics";

	//
	// Server openTSDB metric
	//

	/** tag -- id */
	final public static String TAG_ID = "id";
	/** tag -- disk : mount */
	// final public static String TAG_DISK_MOUNT_POINT="mountPoint";
	/** tag -- disk : device */
	final public static String TAG_DISK_DEVICE = "device";
	/** tag -- net : port */
	final public static String TAG_DISK_NET_PORT = "port";

	/** tag -- docker : containerId */
	final public static String TAG_DOCKER_CONTAINER_ID = "containerId";
	/** tag -- docker : name */
	final public static String TAG_DOCKER_NAME = "name";

	/** metric -- cpu */
	final public static String METRIC_CPU = "basic.cpu";

	/** metric -- mem : total */
	final public static String METRIC_MEM_TOTAL = "basic.mem.total";
	/** metric -- mem : free */
	final public static String METRIC_MEM_FREE = "basic.mem.free";
	/** metric -- mem : used percent */
	final public static String METRIC_MEM_USED_PERCENT = "basic.mem.usedPercent";
	/** metric -- mem : used */
	final public static String METRIC_MEM_USED = "basic.mem.used";
	/** metric -- mem cached */
	final public static String METRIC_MEM_CACHE = "basic.mem.cached";
	/** metric -- mem buffers */
	final public static String METRIC_MEM_BUFFERS = "basic.mem.buffers";

	/** metric -- disk : total */
	final public static String METRIC_DISK_TOTAL = "basic.disk.total";
	/** metric -- disk : free */
	final public static String METRIC_DISK_FREE = "basic.disk.free";
	/** metric -- disk : used */
	final public static String METRIC_DISK_USED = "basic.disk.used";
	/** metric -- disk : used Percent */
	final public static String METRIC_DISK_USED_PERCENT = "basic.disk.usedPercent";
	/** metric -- disk : inodes Physical */
	final public static String METRIC_DISK_INODES_TOTAL = "basic.disk.inodesTotal";
	/** metric -- disk : inodes Used */
	final public static String METRIC_DISK_INODES_USED = "basic.disk.inodesUsed";
	/** metric -- disk : inodes Free */
	final public static String METRIC_DISK_INODES_FREE = "basic.disk.inodesFree";
	/** metric -- disk : inodes Used Percent */
	final public static String METRIC_DISK_INODES_USED_PERCENT = "basic.disk.inodesUsedPercent";

	/** metric -- net : up */
	final public static String METRIC_NET_UP = "basic.net.up";
	/** metric -- net : down */
	final public static String METRIC_NET_DOWN = "basic.net.down";
	/** metric -- net : count */
	final public static String METRIC_NET_COUNT = "basic.net.count";
	/** metric -- net : estab */
	final public static String METRIC_NET_ESTAB = "basic.net.estab";
	/** metric -- net : closeWait */
	final public static String METRIC_NET_CLOSE_WAIT = "basic.net.closeWait";
	/** metric -- net : timeWait */
	final public static String METRIC_NET_TIME_WAIT = "basic.net.timeWait";
	/** metric -- net : close */
	final public static String METRIC_NET_CLOSE = "basic.net.close";
	/** metric -- net : listen */
	final public static String METRIC_NET_LISTEN = "basic.net.listen";
	/** metric -- net : closing */
	final public static String METRIC_NET_CLOSING = "basic.net.closing";

	/** metric -- docker : cpu.perc */
	final public static String METRIC_DOCKER_CPU = "docker.cpu.perc";
	/** metric -- docker : mem.usage */
	final public static String METRIC_DOCKER_MEM_USAGE = "docker.mem.usage";
	/** metric -- docker : mem.perc */
	final public static String METRIC_DOCKER_MEM_PERC = "docker.mem.perc";
	/** metric -- docker : net.in */
	final public static String METRIC_DOCKER_NET_IN = "docker.net.in";
	/** metric -- docker : net.out */
	final public static String METRIC_DOCKER_NET_OUT = "docker.net.out";
	/** metric -- docker : block.in */
	final public static String METRIC_DOCKER_BLOCK_IN = "docker.block.in";
	/** metric -- docker : block.out */
	final public static String METRIC_DOCKER_BLOCK_OUT = "docker.block.out";

}