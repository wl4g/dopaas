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
package com.wl4g.devops.iam.common.security.xss.html;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TagNode extends Node implements IHTMLVisitor {
	private String name;
	private List<Attribute> attributes = new ArrayList<Attribute>();
	private List<Node> childNodes = new ArrayList<Node>();

	private static final Set<String> IMMEDIATE_CLOSE_TAGS = new HashSet<>();

	static {
		IMMEDIATE_CLOSE_TAGS.add("br");
		IMMEDIATE_CLOSE_TAGS.add("hr");
		IMMEDIATE_CLOSE_TAGS.add("input");
		IMMEDIATE_CLOSE_TAGS.add("img");
	}

	public TagNode(String name) {
		super();
		this.name = name;
	}

	public void addAttribute(String name, String value) {
		attributes.add(new Attribute(name, value));
	}

	public void addNode(Node node) {
		childNodes.add(node);
		node.setPrevNode(this);
	}

	public String getName() {
		return name;
	}

	public void writeAll(Writer writer, IHTMLFilter filter, boolean convertIntoValidXML, boolean filterText) throws IOException {
		boolean filterAttribute = false;
		boolean filterTag = false;
		String attrValue = null;

		if (filter != null) {
			filterTag = filter.filterTag(name);
		}

		if (!filterTag) {
			writer.append("<");
			writer.append(name);

			for (Attribute a : attributes) {

				attrValue = a.getValue();

				if (filter != null) {
					filterAttribute = filter.filterAttribute(name, a.getName(), a.getValue());
					attrValue = filter.modifyAttributeValue(name, a.getName(), a.getValue());
				}
				if (!filterAttribute) {
					writeAttributeValue(writer, a, attrValue);
				}
			}

			if ((shouldBeClosedImmediately()) && (convertIntoValidXML)) {
				writer.append("/>");
			} else {
				writer.append(">");
			}
		} else {
			filterText = true;
		}

		for (Node a : childNodes) {
			a.writeAll(writer, filter, convertIntoValidXML, filterText);
		}

		if (!filterTag) {
			if (!shouldBeClosedImmediately()) {
				writer.append("</");
				writer.append(name);
				writer.append(">");
			}
		}
	}

	boolean shouldBeClosedImmediately() {
		if (IMMEDIATE_CLOSE_TAGS.contains(name)) {
			return true;
		}
		return false;
	}

	boolean mayContainOtherTags() {
		if (IMMEDIATE_CLOSE_TAGS.contains(name)) {
			return false;
		}
		return true;
	}

	private void writeAttributeValue(Writer writer, Attribute a, String attrValue) throws IOException {
		writer.append(" ");
		writer.append(a.getName());

		char sep = getAttributeSeparator(attrValue);

		if ((attrValue != null) && (attrValue.length() > 0)) {
			writer.append("=");
			writer.append(sep);
			writer.append(attrValue);
			writer.append(sep);
		}
	}

	private char getAttributeSeparator(String attrValue) {
		if (!attrValue.contains("\"")) {
			return '"';
		}
		if (!attrValue.contains("'")) {
			return '\'';
		}
		if (!attrValue.contains("`")) {
			return '`';
		}
		return '"';
	}
}