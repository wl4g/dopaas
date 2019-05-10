/*
 * Copyright 2015 the original author or authors.
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
 * Note: This file is part of the XSS Protect library, This course 
 * is borrowed from the xssProtect framework (thank you very much)  
 * and has been revised. We don't want to reinvent the steering 
 * wheel of the great work they do, but we don't want to force every devops-iam 
 * user to rely on xssProtect. Under the Apache 2.0 license, the original 
 * copyright declaration and all authors and copyright information remain unchanged.
 */
package com.wl4g.devops.iam.common.attacks.xss.html;

import java.io.IOException;
import java.io.Writer;

/**
 * Holder class for text within a node. Text within a node is the text between
 * the
 * <p>
 * and
 * </p>
 * tags.
 * 
 * @author gt
 *
 */
public class TextNode extends Node implements IHTMLVisitor {

	private StringBuilder text = new StringBuilder();
	private String tagName = null;

	public TextNode(String tagName, String text) {
		super();
		this.text.append(text);
	}

	// Add text to the node
	public void addText(String text) {
		this.text.append(text);
	}

	// Return the text
	public String getText() {
		return text.toString();
	}

	// Write this node to the stream
	public void writeAll(Writer writer, IHTMLFilter htmlFilter, boolean convertIntoValidXML, boolean filterText)
			throws IOException {

		if (filterText) {
			return;
		}

		if (htmlFilter != null) {
			String newText = htmlFilter.modifyNodeText(tagName, getText());
			writer.append(newText);
		} else {
			writer.append(getText());
		}
	}
}