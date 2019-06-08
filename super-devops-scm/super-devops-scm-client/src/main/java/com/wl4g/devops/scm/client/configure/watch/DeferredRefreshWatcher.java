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

import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.scm.client.configure.refresh.ScmContextRefresher;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;

import java.nio.charset.Charset;

/**
 * Class that registers a {@link TreeCache} for each context. It publishes
 * events upon element change in Zookeeper.
 *
 * @author Spencer Gibb
 * @since 1.0.0
 * @see {@link org.springframework.cloud.zookeeper.config.ConfigWatcher
 *      ConfigWatcher}
 */
public class DeferredRefreshWatcher extends AbstractDeferredRefreshWatcher {

	private String path;


	public DeferredRefreshWatcher(ScmContextRefresher refresher, String path, ScmClientProperties scmClientProperties) {
		super(refresher,scmClientProperties);
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		this.path = path;
	}

	@Override
	public void doStart() {
		//nothing
	}

	@Override
	public void close() {

	}


	public void watch(){
		while(true){
			boolean needRefresh = super.watch(path);
			if(needRefresh){
				super.doExecute();
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