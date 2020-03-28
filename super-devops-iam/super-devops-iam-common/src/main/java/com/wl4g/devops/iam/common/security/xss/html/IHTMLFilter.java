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

public interface IHTMLFilter {
	/**
	 * This method can filter tags from a particular document. Notice that this
	 * method will still call the filter method for its child elements. It is
	 * possible that this causes invalid HTML to be generated
	 * 
	 * @param tagName
	 *            The name of the tag currently processed
	 * @return "true" to filter the tag (not write to output), "false" to add it
	 *         to the output
	 */
	public abstract boolean filterTag(String tagName);

	/**
	 * This method determines if the attribute will be filtered from the tag.
	 * 
	 * @param tagName
	 *            The name of the tag where the attribute is located
	 * @param attrName
	 *            The name of the attribute that can be filtered.
	 * @param attrValue
	 *            The value of the attribute that can be filtered.
	 * @return "true" to filter out, "false" to leave it in.
	 */
	public abstract boolean filterAttribute(String tagName, String attrName, String attrValue);

	/**
	 * This method can modify (specifically filter) the attribute value. Use it
	 * to clean up XSS attacks and so forth.
	 * 
	 * @param tagName
	 *            The name of the tag where the attribute is located
	 * @param attrName
	 *            The name of the attribute that can be filtered.
	 * @param value
	 *            The value of the attribute
	 * @return The modified attribute value
	 */
	public abstract String modifyAttributeValue(String tagName, String attrName, String value);

	/**
	 * This method can modify the text body of a tag. Use it to clean up the
	 * text or process the text body differently. Notice that this method does
	 * not contain other child element texts.
	 * 
	 * @param tagName
	 *            The name of the tag in which the text is located
	 * @param text
	 *            The text of the body.
	 * @return The modified text.
	 */
	public abstract String modifyNodeText(String tagName, String text);
}