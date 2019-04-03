/*
 * Copyright 2013-2016 the original author or authors.
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
package com.wl4g.devops.scm.client.configure.watch;

import java.nio.charset.Charset;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;

import static org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type.NODE_ADDED;
import static org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type.NODE_UPDATED;

/**
 * Class that registers a {@link TreeCache} for each context. It publishes
 * events upon element change in Zookeeper.
 *
 * @author Spencer Gibb
 * @since 1.0.0
 * @see {@link org.springframework.cloud.zookeeper.config.ConfigWatcher
 *      ConfigWatcher}
 */
public class ZookeeperRefreshWatcher extends AbstractRefreshWatcher implements TreeCacheListener {

	private String path;
	private CuratorFramework context;
	private TreeCache treeCache;

	public ZookeeperRefreshWatcher(String path, CuratorFramework context) {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		this.path = path;
		this.context = context;
	}

	@Override
	public void doStart() {
		try {
			// If there is no path, create as a container.
			this.context.createContainers(this.path);

			// Start path value-change listener.
			this.treeCache = TreeCache.newBuilder(this.context, this.path).build();
			this.treeCache.getListenable().addListener(this);
			this.treeCache.start();

			// no race condition since
			// ZookeeperAutoConfiguration.curatorFramework
			// calls curator.blockUntilConnected

		} catch (Exception e) {
			log.error("Error initializing listener for path " + path, e);
		}
	}

	@Override
	public void close() {
		this.treeCache.close();
		this.treeCache = null;
	}

	@Override
	public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Zk watch event. {}", event);
		}

		TreeCacheEvent.Type eventType = event.getType();
		if (eventType == NODE_ADDED || eventType == NODE_UPDATED) {
			ChildData data = event.getData();
			if (data != null) {
				super.doExecute(this, data.getData(), this.getEventDesc(event));
			}
		}
	}

	public String getEventDesc(TreeCacheEvent event) {
		StringBuilder out = new StringBuilder();
		out.append("type=").append(event.getType());
		out.append(", path=").append(event.getData().getPath());
		byte[] data = event.getData().getData();
		if (data != null && data.length > 0) {
			out.append(", data=").append(new String(data, Charset.forName("UTF-8")));
		}
		return out.toString();
	}

	@Override
	public String toString() {
		return "ZookeeperRefreshWatcher [path=" + path + "]";
	}

}
