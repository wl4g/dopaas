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
package com.wl4g.devops.components.tools.common.cli;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.wl4g.devops.components.tools.common.cli.annotation.PropertyDescription;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.devops.components.tools.common.lang.Assert2.*;
import static com.wl4g.devops.components.tools.common.reflect.TypeUtils2.isSimpleType;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * Standard formatter
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月2日
 * @since
 */
public class StandardFormatter extends HelpFormatter {
	private Object object;
	private boolean hasAttribute;
	private int cellRow;

	public StandardFormatter(Object object) {
		notNull(object, "object must not be null");
		this.object = object;
		this.hasAttribute = true;
		this.cellRow = 2;
	}

	public StandardFormatter hasAttribute(boolean hasAttribute) {
		this.hasAttribute = hasAttribute;
		return this;
	}

	public StandardFormatter cellRow(int cellRow) {
		this.cellRow = cellRow;
		return this;
	}

	public String format() {
		List<ValueWrap> resultSet = new ArrayList<>();
		extractFullBean(resultSet);
		StringBuffer formated = new StringBuffer();
		for (int i = 0; i < resultSet.size(); i++) {
			ValueWrap v = resultSet.get(i);
			if (hasAttribute) {
				formated.append(v.getName());
				formated.append(" ");
			}
			formated.append("[");
			formated.append(v.getDesc());
			formated.append("]: ");
			formated.append(v.getValue());
			formated.append("\t\t");
			if (i > 0 && ((i + 1) % cellRow) == 0) {
				formated.append("\n");
			}
		}
		return formated.toString();
	}

	private void extractFullBean(List<ValueWrap> resultSet) {
		Class<?> cls = object.getClass();
		do {
			extractFlatBean(cls, resultSet);
		} while ((cls = cls.getSuperclass()) != null);
	}

	private void extractFlatBean(Class<?> clazz, List<ValueWrap> resultSet) {
		notNull(clazz, "The clazz must be null");
		try {
			for (Field f : clazz.getDeclaredFields()) {
				String fname = f.getName();
				f.setAccessible(true);
				Object value = f.get(object);
				PropertyDescription desc = f.getAnnotation(PropertyDescription.class);
				if (desc != null) { // Filter property
					if (isSimpleType(f.getType())) {
						resultSet.add(new ValueWrap(fname, value, desc.value()));
					} else
						extractFlatBean(f.getType(), resultSet);
				}
			}
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Get usage format string
	 * 
	 * @param argname
	 * @param options
	 * @return
	 */
	public static String getHelpFormat(String argname, Options options) {
		return getHelpFormat(argname, options, EMPTY);
	}

	/**
	 * Get usage format string
	 * 
	 * @param argname
	 * @param options
	 * @param help
	 * @param lastLine
	 * @return
	 */
	public static String getHelpFormat(String argname, Options options, String help) {
		hasText(argname, "Argname is empty");
		notNull(options, String.format("No command: '%s' args options", argname));
		help = trimToEmpty(help);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(out);

		HelpFormatter fmt = new HelpFormatter();
		fmt.setSyntaxPrefix("Usage: ");
		String hit = (options != null && !options.getOptions().isEmpty()) ? "[OPTIONS ...] <VALUE>" : EMPTY;
		fmt.printHelp(pw, 128, String.format("%s %s\n  %s", argname, hit, help), null, options, 2, fmt.getDescPadding(), null,
				false);
		pw.flush();

		return new String(out.toByteArray(), UTF_8);
	}

	/**
	 * Value wrapper
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年5月2日
	 * @since
	 */
	class ValueWrap implements Serializable {
		private static final long serialVersionUID = -5349480039109094085L;

		private String name;
		private Object value;
		private String desc;

		public ValueWrap() {
			super();
		}

		public ValueWrap(String name, Object value, String desc) {
			super();
			this.name = name;
			this.value = value;
			this.desc = desc;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		@Override
		public String toString() {
			return "ValueWrap [value=" + value + ", desc=" + desc + "]";
		}

	}

}