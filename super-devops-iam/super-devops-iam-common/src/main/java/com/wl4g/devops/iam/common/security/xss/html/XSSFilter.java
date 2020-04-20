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

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of a relatively simple XSS filter. This implementation removes
 * dangerous tags and attributes from tags. It does not verify the validity of
 * URL's (that may contain links to JavaScript for example). It does not remove
 * all event handlers that may still contain XSS vulnerabilities.
 * <p>
 * Embedded objects are removed because those may contain XSS vulnerabilities in
 * their own scripting language (ActionScript for Flash for example).
 * <p>
 * Feel free to derive your own implementation from this file.
 *
 * @author gt
 */
public class XSSFilter implements IHTMLFilter {

	private static final Set<String> FORBIDDEN_TAGS = new HashSet<String>();

	// The tags to be removed. Case insensitive of course.
	static {
		FORBIDDEN_TAGS.add("script");
		FORBIDDEN_TAGS.add("embed");
		FORBIDDEN_TAGS.add("object");
		FORBIDDEN_TAGS.add("layer");
		FORBIDDEN_TAGS.add("style");
		FORBIDDEN_TAGS.add("meta");
		FORBIDDEN_TAGS.add("iframe");
		FORBIDDEN_TAGS.add("frame");
		FORBIDDEN_TAGS.add("link");
		FORBIDDEN_TAGS.add("import");
		FORBIDDEN_TAGS.add("xml");
	}

	/**
	 * This function is called to determine if an attribute should be filtered
	 * or not.
	 *
	 * @param tagName
	 *            The name of the tag the attribute belongs to
	 * @param attrName
	 *            The name of the attribute to be filtered
	 * @param attrValue
	 *            The value of the attribute
	 */
	public boolean filterAttribute(String tagName, String attrName, String attrValue) {
		if (attrName.toLowerCase().startsWith("on")) {
			return true;
		}

		return isScriptedAttributeValue(attrValue);
	}

	/**
	 * This method is called to determine if a tag should be filtered
	 *
	 * @param tagName
	 *            The name of the tag that was parsed
	 */
	public boolean filterTag(String tagName) {
		if (FORBIDDEN_TAGS.contains(tagName)) {
			return true;
		}
		return false;
	}

	/**
	 * This method is called to modify attribute values, if required
	 *
	 * @param tagName
	 *            The name of the tag the attribute belongs to
	 * @param attrName
	 *            The name of the attribute within the tag
	 * @param attrValue
	 *            The value of the attribute
	 */
	public String modifyAttributeValue(String tagName, String attrName, String attrValue) {
		return attrValue;
	}

	/**
	 * This method is called to be able to modify the text of a node.
	 *
	 * @param tagName
	 *            The name of the tag where the text is part of.
	 * @param text
	 *            The value of the text within the tagnode (within
	 *            <tag>...</tag>)
	 */
	public String modifyNodeText(String tagName, String text) {
		return text;
	}

	/**
	 * Private method that determines if an attribute value is scripted
	 * (potentially loaded with an XSS attack vector).
	 *
	 * @param attrValue
	 *            The value of the attribute
	 * @return "true" if the attribute is scripted. "false" if not.
	 */
	private boolean isScriptedAttributeValue(String attrValue) {
		attrValue = decode(attrValue);
		attrValue = attrValue.trim().toLowerCase();

		if (attrValue.contains("javascript:")) {
			return true;
		}
		if (attrValue.contains("mocha:")) {
			return true;
		}
		if (attrValue.contains("eval")) {
			return true;
		}
		if (attrValue.contains("vbscript:")) {
			return true;
		}
		if (attrValue.contains("livescript:")) {
			return true;
		}
		if (attrValue.contains("expression(")) {
			return true;
		}
		if (attrValue.contains("url(")) {
			return true;
		}
		if (attrValue.contains("&{")) {
			return true;
		}
		if (attrValue.contains("&#")) {
			return true;
		}
		return false;
	}

	/**
	 * Private method to remove control characters from the value
	 *
	 * @param value
	 *            The value being modified
	 * @return The value free from control characters
	 */
	private String decode(String value) {
		value = value.replace("\u0000", "");
		value = value.replace("\u0001", "");
		value = value.replace("\u0002", "");
		value = value.replace("\u0003", "");
		value = value.replace("\u0004", "");
		value = value.replace("\u0005", "");
		value = value.replace("\u0006", "");
		value = value.replace("\u0007", "");
		value = value.replace("\u0008", "");
		value = value.replace("\u0009", "");
		value = value.replace("\n", "");
		value = value.replace("\u000B", "");
		value = value.replace("\u000C", "");
		value = value.replace("\r", "");
		value = value.replace("\u000E", "");
		value = value.replace("\u000F", "");
		value = value.replace("\u0010", "");
		value = value.replace("\u0011", "");
		value = value.replace("\u0012", "");
		value = value.replace("\u0013", "");
		value = value.replace("\u0014", "");
		value = value.replace("\u0015", "");
		value = value.replace("\u0016", "");
		value = value.replace("\u0017", "");
		value = value.replace("\u0018", "");
		value = value.replace("\u0019", "");
		value = value.replace("\u001A", "");
		value = value.replace("\u001B", "");
		value = value.replace("\u001C", "");
		value = value.replace("\u001D", "");
		value = value.replace("\u001E", "");
		value = value.replace("\u001F", "");
		return value;
	}
}