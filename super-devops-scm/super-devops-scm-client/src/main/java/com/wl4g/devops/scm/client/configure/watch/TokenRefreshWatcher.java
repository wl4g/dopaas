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
package com.wl4g.devops.scm.client.configure.watch;

import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.scm.client.configure.ScmPropertySourceLocator;
import com.wl4g.devops.scm.client.configure.refresh.ScmContextRefresher;
import com.wl4g.devops.scm.common.bean.ScmMetaInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
public class TokenRefreshWatcher extends AbstractRefreshWatcher implements TreeCacheListener {

	private String path;
	private CuratorFramework context;
	private TreeCache treeCache;

	public TokenRefreshWatcher(ScmContextRefresher refresher, String path, CuratorFramework context) {
		super(refresher);
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
			context.createContainers(path);

			// Start path value-change listener.
			treeCache = TreeCache.newBuilder(context, path).build();
			treeCache.getListenable().addListener(this);
			treeCache.start();

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
			log.debug("Zk watch event for : {}", event);
		}

		TreeCacheEvent.Type eventType = event.getType();
		if (eventType == NODE_ADDED || eventType == NODE_UPDATED) {
			ChildData data = event.getData();
			if (data != null) {
				String json  = new String(data.getData(), StandardCharsets.UTF_8);
				log.info(json);
				ScmMetaInfo scmMetaInfo = JacksonUtils.parseJSON(json,ScmMetaInfo.class);
				ScmPropertySourceLocator.token = scmMetaInfo.getToken();
				//TODO

				//super.doExecute(this, data.getData(), getEventDesc(event));
			}
		}
	}

	private String getEventDesc(TreeCacheEvent event) {
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