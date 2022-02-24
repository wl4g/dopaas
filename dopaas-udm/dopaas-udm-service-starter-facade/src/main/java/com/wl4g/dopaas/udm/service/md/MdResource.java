/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.udm.service.md;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static java.util.Objects.isNull;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;

import com.wl4g.infra.common.lang.StringUtils2;

/**
 * Rendering template resource wrapper.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @author Vjay
 * @version v1.0 2020-09-16
 * @since
 */
public final class MdResource implements Serializable {
	private static final long serialVersionUID = 4336198329362479594L;

	/**
	 * Raw template resource filename.</br>
	 * for example:
	 * 
	 * <pre>
	 * ....../bean/MyUser.java.ftl
	 * </pre>
	 */
	private final String rawFilename;

	/**
	 * Template resource short filename.</br>
	 * for example:
	 * 
	 * <pre>
	 * ....../bean/MyUser.java.ftl => MyUser.java.ftl
	 * </pre>
	 */
	private final String shortFilename;

	/**
	 * Template file content bytes.
	 */
	private final byte[] content;

	/**
	 * Constructor
	 */
	public MdResource(@NotBlank String rawFilename, @Nullable byte[] content) {
		this.rawFilename = hasTextOf(rawFilename, "rawFilename");
		this.shortFilename = hasTextOf(StringUtils2.getFilename(rawFilename), "filename");
		this.content = content;
	}

	@NotBlank
	public final String getRawFilename() {
		return rawFilename;
	}

	@NotBlank
	public final String getShortFilename() {
		return shortFilename;
	}

	@Nullable
	public final byte[] getContent() {
		return content;
	}

	@Nullable
	public final String getContentAsString() {
		return isNull(content) ? null : new String(content, UTF_8);
	}

}