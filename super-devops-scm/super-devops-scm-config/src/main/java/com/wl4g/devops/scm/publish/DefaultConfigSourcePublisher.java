/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.scm.publish;

import java.util.Iterator;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Charsets;
import com.wl4g.devops.common.bean.scm.model.PreRelease;
import com.wl4g.devops.common.bean.scm.model.GenericInfo.ReleaseInstance;
import com.wl4g.devops.common.exception.scm.ConfigureReleaseZkSetException;
import com.wl4g.devops.scm.common.utils.ScmUtils;

/**
 * Default configuration property source publisher.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年10月26日
 * @since
 */
public class DefaultConfigSourcePublisher implements ConfigSourcePublisher {
	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private CuratorFramework context;

	/**
	 * Release new configuration version.
	 * 
	 * @param pre
	 *            Basic release information.
	 * @param pre
	 *            Cluster nodes/instance(localHost:httpPort) information
	 */
	public void release(PreRelease pre) {
		if (log.isTraceEnabled()) {
			log.trace("Configuring release instances information : {}", pre);
		}

		boolean released = false;
		String path = "";
		try {
			// Validation.
			pre.validation(true, true);

			// Open zookeeper transaction.
			CuratorTransaction transaction = this.context.inTransaction();
			for (Iterator<ReleaseInstance> it = pre.getInstances().iterator(); it.hasNext();) {
				ReleaseInstance instance = it.next();

				// 1.2 Get configuration discovery path.
				path = this.determineConfigDiscoveryPath(pre, instance);

				// 1.3 Set configuration version.
				CuratorTransactionBridge bridge = transaction.setData().forPath(path,
						pre.getMeta().asText().getBytes(Charsets.UTF_8));

				// 1.4 Commit transaction.
				if (!it.hasNext()) {
					bridge.and().commit();
				}

				if (log.isDebugEnabled()) {
					log.debug("Release configuration to Zk, instance: {}", instance);
				}
				// Mark released.
				released = true;
			}
		} catch (Exception e) {
			throw new ConfigureReleaseZkSetException(
					String.format("ZNode path :'%s', %s", path, ExceptionUtils.getRootCauseMessage(e)));
		}

		if (released && log.isInfoEnabled()) {
			log.info("Release configuration submitted. {}", pre);
		}
	}

	private String determineConfigDiscoveryPath(PreRelease pre, ReleaseInstance instance) {
		return ScmUtils.genZkConfigPath(pre, instance);
	}

}