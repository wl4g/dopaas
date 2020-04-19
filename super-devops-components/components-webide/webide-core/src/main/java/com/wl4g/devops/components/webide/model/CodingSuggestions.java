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
package com.wl4g.devops.components.webide.model;

import static com.wl4g.devops.tool.common.lang.Assert2.hasTextOf;

import com.wl4g.devops.tool.common.annotation.Reserved;

/**
 * {@link CodingSuggestions}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年4月19日 v1.0.0
 * @see
 */
@Reserved
public class CodingSuggestions {

	/**
	 * Label.
	 */
	private String label;

	/**
	 * Insert text.
	 */
	private String insertText;

	/**
	 * Documentation.
	 */
	private String documentation;

	public CodingSuggestions() {
		super();
	}

	public CodingSuggestions(String label, String insertText, String documentation) {
		setLabel(label);
		setInsertText(insertText);
		setDocumentation(documentation);
	}

	public String getLabel() {
		return label;
	}

	public CodingSuggestions setLabel(String label) {
		hasTextOf(label, "label");
		this.label = label;
		return this;
	}

	public String getInsertText() {
		return insertText;
	}

	public CodingSuggestions setInsertText(String insertText) {
		hasTextOf(insertText, "insertText");
		this.insertText = insertText;
		return this;
	}

	public String getDocumentation() {
		return documentation;
	}

	public CodingSuggestions setDocumentation(String documentation) {
		// hasTextOf(documentation, "documentation");
		this.documentation = documentation;
		return this;
	}

}
