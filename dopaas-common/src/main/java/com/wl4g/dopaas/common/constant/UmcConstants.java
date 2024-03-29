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
package com.wl4g.dopaas.common.constant;

/**
 * UMC(Unified Monitoring Center) Constants.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月13日
 * @since
 */
public abstract class UmcConstants extends DoPaaSConstants {

    public static final String KEY_UMC_CONFIG_PREFIX = KEY_DOPAAS_BASE_PREFIX + ".umc";
    public static final String KEY_UMC_CLIENT_PREFIX = KEY_UMC_CONFIG_PREFIX + ".client";

    //
    // UMC client metrics constants definition.
    //

    /** Administrator dashboard's base URI. */
    public static final String SCRAPE_COLLECTOR_PREFIX = KEY_UMC_CLIENT_PREFIX + ".scrape";

    //
    // ----------------------------------------------------------------------
    //

    public static final String INFO_PREFIX = "sba_event_";

    //
    // UMC admin definition.
    //

    /** Administrator dashboard's base URI. */
    public static final String URI_ADMIN_HOME = "/dashboard";

    //
    // UMC receiver definition.(corresponding to agent collector)
    //

    /** Receiver end-point based URI */
    public static final String URI_HTTP_RECEIVER_BASE = "/receiver";

    /** HTTP receiver metric end-point URI.(corresponding to agent collector) */
    public static final String URI_HTTP_RECEIVER_ENDPOINT = "metric";

    //
    // UMC alarm definition.
    //

    /**
     * UMC Alarm template prefix key corresponding to a collector.
     */
    public static final String KEY_CACHE_ALARM_TPLS = "umc_alarm_tpls_";

    /**
     * UMC alarm metric value in time window queue key prefix.
     */
    public static final String KEY_CACHE_ALARM_METRIC_QUEUE = "umc_alarm_queue_";

    /**
     * Simulation UMC alarm metric value in time window queue key prefix.
     */
    public static final String KEY_CACHE_ALARM_METRIC_QUEUE_SIMULATE = "umc_alarm_queue_simulate_";

    /**
     * KAFKA receiver metric topic pattern.(corresponding to agent collector)
     */
    public static final String TOPIC_KAFKA_RECEIVE_PATTERN = "umc_agent_metrics";

    //
    // UMC watch definition.
    //

    /** Watch fetch sharding cache key. */
    public static final String KEY_CACHE_FETCH_META = "umc_fetch_meta_sharding_";

    //
    // UMC serial store definition.(corresponding to agent collector)
    //

    /** tag -- id */
    public static final String TAG_ID = "id";
    /** tag -- disk : mount */
    // public static final String TAG_DISK_MOUNT_POINT="mountPoint";
    /** tag -- disk : device */
    public static final String TAG_DISK_DEVICE = "device";
    /** tag -- net : port */
    public static final String TAG_DISK_NET_PORT = "port";

    /** tag -- docker : containerId */
    public static final String TAG_DOCKER_CONTAINER_ID = "containerId";
    /** tag -- docker : name */
    public static final String TAG_DOCKER_NAME = "name";

    /** metric -- cpu */
    public static final String METRIC_CPU = "basic.cpu";

    /** metric -- mem : total */
    public static final String METRIC_MEM_TOTAL = "basic.mem.total";
    /** metric -- mem : free */
    public static final String METRIC_MEM_FREE = "basic.mem.free";
    /** metric -- mem : used percent */
    public static final String METRIC_MEM_USED_PERCENT = "basic.mem.usedPercent";
    /** metric -- mem : used */
    public static final String METRIC_MEM_USED = "basic.mem.used";
    /** metric -- mem cached */
    public static final String METRIC_MEM_CACHE = "basic.mem.cached";
    /** metric -- mem buffers */
    public static final String METRIC_MEM_BUFFERS = "basic.mem.buffers";

    /** metric -- disk : total */
    public static final String METRIC_DISK_TOTAL = "basic.disk.total";
    /** metric -- disk : free */
    public static final String METRIC_DISK_FREE = "basic.disk.free";
    /** metric -- disk : used */
    public static final String METRIC_DISK_USED = "basic.disk.used";
    /** metric -- disk : used Percent */
    public static final String METRIC_DISK_USED_PERCENT = "basic.disk.usedPercent";
    /** metric -- disk : inodes Physical */
    public static final String METRIC_DISK_INODES_TOTAL = "basic.disk.inodesTotal";
    /** metric -- disk : inodes Used */
    public static final String METRIC_DISK_INODES_USED = "basic.disk.inodesUsed";
    /** metric -- disk : inodes Free */
    public static final String METRIC_DISK_INODES_FREE = "basic.disk.inodesFree";
    /** metric -- disk : inodes Used Percent */
    public static final String METRIC_DISK_INODES_USED_PERCENT = "basic.disk.inodesUsedPercent";

    /** metric -- net : up */
    public static final String METRIC_NET_UP = "basic.net.up";
    /** metric -- net : down */
    public static final String METRIC_NET_DOWN = "basic.net.down";
    /** metric -- net : count */
    public static final String METRIC_NET_COUNT = "basic.net.count";
    /** metric -- net : estab */
    public static final String METRIC_NET_ESTAB = "basic.net.estab";
    /** metric -- net : closeWait */
    public static final String METRIC_NET_CLOSE_WAIT = "basic.net.closeWait";
    /** metric -- net : timeWait */
    public static final String METRIC_NET_TIME_WAIT = "basic.net.timeWait";
    /** metric -- net : close */
    public static final String METRIC_NET_CLOSE = "basic.net.close";
    /** metric -- net : listen */
    public static final String METRIC_NET_LISTEN = "basic.net.listen";
    /** metric -- net : closing */
    public static final String METRIC_NET_CLOSING = "basic.net.closing";

    /** metric -- docker : cpu.perc */
    public static final String METRIC_DOCKER_CPU = "docker.cpu.perc";
    /** metric -- docker : mem.usage */
    public static final String METRIC_DOCKER_MEM_USAGE = "docker.mem.usage";
    /** metric -- docker : mem.perc */
    public static final String METRIC_DOCKER_MEM_PERC = "docker.mem.perc";
    /** metric -- docker : net.in */
    public static final String METRIC_DOCKER_NET_IN = "docker.net.in";
    /** metric -- docker : net.out */
    public static final String METRIC_DOCKER_NET_OUT = "docker.net.out";
    /** metric -- docker : block.in */
    public static final String METRIC_DOCKER_BLOCK_IN = "docker.block.in";
    /** metric -- docker : block.out */
    public static final String METRIC_DOCKER_BLOCK_OUT = "docker.block.out";

    /* alarm limit */
    public static final String ALARM_LIMIT_PHONE = "alarm_limit_phone";
    public static final String ALARM_LIMIT_DINGTALK = "alarm_limit_dingtalk";
    public static final String ALARM_LIMIT_FACEBOOK = "alarm_limit_facebook";
    public static final String ALARM_LIMIT_TWITTER = "alarm_limit_twitter";
    public static final String ALARM_LIMIT_WECHAT = "alarm_limit_wechat";

    /* alarm notification contact status */
    public static final String ALARM_SATUS_SEND = "1";
    public static final String ALARM_SATUS_UNSEND = "2";
    public static final String ALARM_SATUS_ACCEPTED = "3";
    public static final String ALARM_SATUS_UNACCEPTED = "4";

}