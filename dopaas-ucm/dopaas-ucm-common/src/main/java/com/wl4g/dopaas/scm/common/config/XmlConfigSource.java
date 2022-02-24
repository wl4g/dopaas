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
package com.wl4g.dopaas.scm.common.config;

import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;

import java.util.List;
import java.util.Map;

import com.wl4g.dopaas.scm.common.model.AbstractConfigInfo.ConfigProfile;

import lombok.Getter;
import lombok.Setter;

/**
 * {@link XmlConfigSource}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-08-15
 * @sine v1.0.0
 * @see
 */
@Getter
public class XmlConfigSource extends AbstractConfigSource {

	private static final long serialVersionUID = -7806121596630535330L;

	@Override
	public void doRead(ConfigProfile profile, String sourceContent) {
		throw new UnsupportedOperationException();
	}

	/** Configuration source typeof map */
	private XmlNode root;

	/**
	 * {@link XmlNode}
	 * 
	 * @see
	 */
	@Getter
	@Setter
	public static class XmlNode {

		/** Xml node name. */
		private String name;

		/** Xml node attributes. */
		private Map<String, String> attributes;

		/** Xml children nodes. */
		private List<XmlNode> children;

		@Override
		public String toString() {
			return getClass().getSimpleName().concat(" - ").concat(toJSONString(this));
		}

	}

}