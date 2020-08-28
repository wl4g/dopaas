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
package com.wl4g.devops.scm.client.repository;

import static com.wl4g.components.common.lang.Assert2.notEmptyOf;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.wl4g.devops.scm.common.config.ScmPropertySource;
import com.wl4g.devops.scm.common.model.ReleaseConfigInfo;

import lombok.Getter;

/**
 * {@link ReleasePropertySourceWrapper}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-28
 * @since
 */
@Getter
public class ReleasePropertySourceWrapper {

	/**
	 * Origin release of {@link ReleaseConfigInfo}
	 */
	private final ReleaseConfigInfo release;

	/**
	 * List of {@link ScmPropertySource}
	 */
	private final List<ScmPropertySource> sources;

	public ReleasePropertySourceWrapper(@NotNull ReleaseConfigInfo release, @NotEmpty List<ScmPropertySource> sources) {
		notNullOf(release, "release");
		notEmptyOf(sources, "sources");
		this.release = release;
		this.sources = sources;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName().concat(" - ").concat(toJSONString(this));
	}

}
