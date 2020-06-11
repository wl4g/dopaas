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
package com.wl4g.devops.dguid.worker;

import java.util.Random;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.wl4g.devops.dguid.baidu.utils.DockerUtils;
import com.wl4g.devops.dguid.util.NetUtils;
import com.wl4g.devops.dguid.worker.entity.WorkerNode;
import com.wl4g.devops.dguid.worker.registory.WorkerNodeRegistory;

/**
 * DB编号分配器(利用数据库来管理)
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年2月10日
 * @since
 */
public class DisposableWorkerIdAssigner implements WorkerIdAssigner {
	private static final Logger LOGGER = LoggerFactory.getLogger(DisposableWorkerIdAssigner.class);

	@Resource
	private WorkerNodeRegistory workerNodeDAO;

	/**
	 * Assign worker id base on database.
	 * <p>
	 * If there is host name & port in the environment, we considered that the
	 * node runs in Docker container<br>
	 * Otherwise, the node runs on an actual machine.
	 * 
	 * @return assigned worker id
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public long assignWorkerId() {
		// build worker node entity
		WorkerNode workerNodeEntity = buildWorkerNode();

		// add worker node for new (ignore the same IP + PORT)
		workerNodeDAO.addWorkerNode(workerNodeEntity);
		LOGGER.info("Add worker node:" + workerNodeEntity);

		return workerNodeEntity.getId();
	}

	/**
	 * Build worker node entity by IP and PORT
	 */
	private WorkerNode buildWorkerNode() {
		WorkerNode workerNodeEntity = new WorkerNode();
		if (DockerUtils.isDocker()) {
			workerNodeEntity.setType(WorkerNodeType.CONTAINER.value());
			workerNodeEntity.setHostName(DockerUtils.getDockerHost());
			workerNodeEntity.setPort(DockerUtils.getDockerPort());

		} else {
			workerNodeEntity.setType(WorkerNodeType.ACTUAL.value());
			workerNodeEntity.setHostName(NetUtils.getLocalInetAddress().getHostAddress());
			workerNodeEntity.setPort(System.currentTimeMillis() + "-" + RANDOM.nextInt(100000));
		}

		return workerNodeEntity;
	}

	private static final Random RANDOM = new Random();
}