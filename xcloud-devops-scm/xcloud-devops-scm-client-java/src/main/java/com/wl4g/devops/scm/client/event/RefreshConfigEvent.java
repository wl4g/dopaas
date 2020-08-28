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
package com.wl4g.devops.scm.client.event;

import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static java.util.Objects.nonNull;

import java.util.List;
import java.util.Set;

import com.wl4g.devops.scm.client.repository.RefreshConfigRepository;
import com.wl4g.devops.scm.client.repository.ReleasePropertySourceWrapper;
import com.wl4g.devops.scm.common.config.ScmPropertySource;
import com.wl4g.devops.scm.common.model.ReleaseConfigInfo;
import com.wl4g.components.common.annotation.Nullable;
import com.wl4g.devops.scm.client.event.RefreshConfigEvent.RefreshContext;

/**
 * {@link RefreshConfigEvent}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-18
 * @since
 */
public class RefreshConfigEvent extends GenericScmEvent<RefreshContext> {
	private static final long serialVersionUID = 1026288899828948496L;

	public RefreshConfigEvent(RefreshContext context) {
		super(context);
	}

	/**
	 * {@link RefreshContext}
	 *
	 * @since
	 */
	public static class RefreshContext {

		/** {@link ReleaseConfigInfo} */
		private final ReleasePropertySourceWrapper release;

		/** {@link RefreshConfigRepository} */
		protected final RefreshConfigRepository repository;

		public RefreshContext(ReleasePropertySourceWrapper release, RefreshConfigRepository repository) {
			notNullOf(release, "release");
			notNullOf(repository, "repository");
			this.release = release;
			this.repository = repository;
		}

		/** Gets current refreshing sources of {@link ScmPropertySource} */
		public List<ScmPropertySource> getSources() {
			return release.getSources();
		}

		/**
		 * Commit changed property config keys.
		 * 
		 * @param changedKeys
		 * @param source
		 */
		public void commitChanged(@Nullable Set<String> changedKeys) {
			if (nonNull(changedKeys)) {
				repository.saveChanged(changedKeys, release);
			} else {
				// TODO ...
			}

		}

	}

}
