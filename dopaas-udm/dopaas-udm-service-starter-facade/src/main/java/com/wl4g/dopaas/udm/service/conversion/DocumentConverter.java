/*
 * Copyright (C) 2017 ~ 2025 the original author or authors.
 * <Wanglsir@gmail.com, 983708408@qq.com> Technology CO.LTD.
 * All rights reserved.
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
 * 
 * Reference to website: http://wl4g.com
 */
package com.wl4g.dopaas.udm.service.conversion;

import com.wl4g.infra.core.framework.operator.Operator;
import com.wl4g.dopaas.common.bean.udm.model.XCloudDocumentModel;
import com.wl4g.dopaas.udm.service.conversion.DocumentConverter.ConverterProviderKind;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static org.springframework.util.Assert.notNull;

/**
 * {@link DocumentConverter}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-16
 * @sine v1.0
 * @see
 */
public interface DocumentConverter<T> extends Operator<ConverterProviderKind> {

	default XCloudDocumentModel convertFrom(String documentJson) {
		throw new UnsupportedOperationException();
	}

	default XCloudDocumentModel convertFrom(T document) {
		throw new UnsupportedOperationException();
	}

	default String convertToJson(XCloudDocumentModel document) throws IOException {
		throw new UnsupportedOperationException();
	}

	default T convertTo(XCloudDocumentModel document) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Documention converter provider definitions.
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version v1.0 2020-12-16
	 * @sine v1.0
	 * @see
	 */
	public static enum ConverterProviderKind {

		/** Document converter for swagger2. */
		SWAGGER2(1),

		/** Document converter for oas3. */
		OAS3(3),

		/** Document converter for rap2. */
		RAP2(2),

		/** Document converter for rap1. */
		RAP1(4);

		final private int value;

		private ConverterProviderKind(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		/**
		 * Safe converter string to {@link ConverterProviderKind}
		 * 
		 * @param provider
		 * @return
		 */
		final public static ConverterProviderKind safeOf(Integer provider) {
			if (isNull(provider)) {
				return null;
			}
			for (ConverterProviderKind t : values()) {
				if (provider.intValue() == t.getValue()) {
					return t;
				}
			}
			return null;
		}

		/**
		 * Converter string to {@link ConverterProviderKind}
		 * 
		 * @param provider
		 * @return
		 */
		final public static ConverterProviderKind of(Integer provider) {
			ConverterProviderKind type = safeOf(provider);
			notNull(type, String.format("Unsupported document converter provider for %s", provider));
			return type;
		}

		final public static List<String> getNames() {
			List<String> names = new ArrayList<>();
			for (ConverterProviderKind t : values()) {
				names.add(t.name());
			}
			return names;
		}

	}

}
