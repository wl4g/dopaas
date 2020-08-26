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
package com.wl4g.devops.scm.common.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.lang.Assert2.notEmptyOf;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ReleaseConfigInfo}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2018-08-11
 * @since
 */
@Getter
@Setter
public class ReleaseConfigInfo extends AbstractConfigInfo {
	final private static long serialVersionUID = -4016863811283064989L;

	/**
	 * Pulish configuration to nodes.
	 */
	@NotNull
	@NotEmpty
	private List<ConfigNode> nodes = new ArrayList<>();

	/** {@link ReleaseConfigSource} */
	@NotNull
	@NotEmpty
	private List<ReleaseConfigSource> releases = new ArrayList<>(2);

	public ReleaseConfigInfo() {
		super();
	}

	@Override
	public void validate(boolean versionValidate, boolean releaseValidate) {
		super.validate(versionValidate, releaseValidate);
		notEmptyOf(getReleases(), "propertySources");
		getReleases().stream().forEach(rs -> rs.validate());
	}

	/**
	 * {@link ReleaseConfigSource}
	 *
	 * @since
	 */
	@Getter
	@Setter
	public static class ReleaseConfigSource {

		/**
		 * Configuration property source profile.
		 */
		private ConfigProfile profile;

		/**
		 * Configuration property source content text.
		 */
		private String sourceContent;

		@Override
		public String toString() {
			return getClass().getSimpleName().concat(" - ").concat(toJSONString(this));
		}

		public void validate() {
			notNullOf(getProfile(), "profile");
			getProfile().validate();
			hasTextOf(getSourceContent(), "sourceContent");
		}

	}

}