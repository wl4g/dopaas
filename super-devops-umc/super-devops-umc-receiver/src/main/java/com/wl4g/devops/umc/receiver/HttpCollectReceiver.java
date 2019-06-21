package com.wl4g.devops.umc.receiver;

import com.wl4g.devops.common.bean.umc.model.physical.Physical;
import com.wl4g.devops.common.bean.umc.model.third.Kafka;
import com.wl4g.devops.common.bean.umc.model.third.Redis;
import com.wl4g.devops.common.bean.umc.model.third.Zookeeper;
import com.wl4g.devops.common.bean.umc.model.virtual.Docker;
import com.wl4g.devops.umc.store.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.*;

/**
 * HTTP collection receiver
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
@ResponseBody
@com.wl4g.devops.umc.annotation.HttpCollectReceiver
public class HttpCollectReceiver extends AbstractCollectReceiver {

	public HttpCollectReceiver(PhysicalMetricStore pStore, VirtualMetricStore vStore, RedisMetricStore rStore, ZookeeperMetricStore zStore, KafkaMetricStore kStore) {
		super(pStore, vStore, rStore,zStore,kStore);
	}

	//
	// Physical receiver
	//
	@RequestMapping(URI_PHYSICAL)
	public void physicalReceive(@RequestBody Physical physical) {
		putPhysical(physical);
	}


	//
	// Virtual receiver
	//
	@RequestMapping(URI_VIRTUAL_DOCKER)
	public void dockerReceive(@RequestBody Docker docker) {
		putVirtualDocker(docker);
	}


	/**
	 * Redis
	 */
	@RequestMapping(URI_REDIS)
	public void redisReceive(@RequestBody Redis redis) {
		putRedis(redis);
	}

	/**
	 * Zookeeper
	 */
	@RequestMapping(URI_ZOOKEEPER)
	public void zookeeperReceive(@RequestBody Zookeeper zookeeper) {
		putZookeeper(zookeeper);
	}

	/**
	 * Kafka
	 */
	@RequestMapping(URI_KAFKA)
	public void kafkaReceive(@RequestBody Kafka kafka) {
		putKafka(kafka);
	}



}
