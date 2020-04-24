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
 * {@link MonacoCodingSuggestions}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年4月19日 v1.0.0
 * @see
 */
@Reserved
public class MonacoCodingSuggestions extends CodingSuggestions {

	/**
	 * List of available values:
	 * 
	 * <pre>
	 * "Method"
	 * "Function"
	 * "Constructor"
	 * "Field"
	 * "Variable"
	 * "Class"
	 * "Struct"
	 * "Interface"
	 * "Module"
	 * "Property"
	 * "Event"
	 * "Operator"
	 * "Unit"
	 * "Value"
	 * "Constant"
	 * "Enum"
	 * "EnumMember"
	 * "Keyword"
	 * "Text"
	 * "Color"
	 * "File"
	 * "Reference"
	 * "Customcolor"
	 * "Folder"
	 * "TypeParameter"
	 * "Snippet"
	 * </pre>
	 * 
	 * @see monaco.languages.CompletionItemKind.Text
	 */
	private String kind;

	/**
	 * InsertTextRules
	 * 
	 * <pre>
	 * 1: "KeepWhitespace"
	 * 4: "InsertAsSnippet"
	 * </pre>
	 */
	private int insertTextRules;

	public MonacoCodingSuggestions() {
	}

	public MonacoCodingSuggestions(String label, String insertText) {
		this(label, insertText, null);
	}

	public MonacoCodingSuggestions(String label, String insertText, String documentation) {
		super(label, insertText, documentation);
		setKind("Text");
		setInsertTextRules(4);
	}

	public String getKind() {
		return kind;
	}

	public MonacoCodingSuggestions setKind(String kind) {
		hasTextOf(kind, "kind");
		this.kind = kind;
		return this;
	}

	public int getInsertTextRules() {
		return insertTextRules;
	}

	public MonacoCodingSuggestions setInsertTextRules(int insertTextRules) {
		this.insertTextRules = insertTextRules;
		return this;
	}

}
