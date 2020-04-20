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

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wl4g.devops.iam.common.security.xss.html.HTMLParser;

public class htmlTreeParser extends TreeParser {
	public static final String[] tokenNames = new String[] { "<invalid>", "<EOR>", "<DOWN>", "<UP>", "TAG_START_OPEN",
			"TAG_END_OPEN", "TAG_CLOSE", "TAG_EMPTY_CLOSE", "WS", "QUOTECHAR", "WSCHAR", "ATTR_VALUE", "PCDATA", "LETTER",
			"NAMECHAR", "GENERIC_ID", "DIGIT", "SPECIALCHAR", "Tokens", "ELEMENT", "ATTRIBUTE", "SETTING" };
	public static final int TAG_CLOSE = 6;
	public static final int LETTER = 13;
	public static final int ATTRIBUTE = 20;
	public static final int TAG_END_OPEN = 5;
	public static final int WSCHAR = 10;
	public static final int EOF = -1;
	public static final int Tokens = 18;
	public static final int NAMECHAR = 14;
	public static final int PCDATA = 12;
	public static final int TAG_EMPTY_CLOSE = 7;
	public static final int WS = 8;
	public static final int SETTING = 21;
	public static final int SPECIALCHAR = 17;
	public static final int GENERIC_ID = 15;
	public static final int ELEMENT = 19;
	public static final int ATTR_VALUE = 11;
	public static final int DIGIT = 16;
	public static final int QUOTECHAR = 9;
	public static final int TAG_START_OPEN = 4;

	protected Logger log = LoggerFactory.getLogger(getClass());

	public htmlTreeParser(TreeNodeStream input) {
		super(input);
	}

	@Override
	public void emitErrorMessage(String msg) {
		if (log.isDebugEnabled()) {
			log.debug(msg);
		}
	}

	@Override
	public void recoverFromMismatchedToken(IntStream input, RecognitionException e, int ttype, BitSet follow)
			throws RecognitionException {
		if (log.isDebugEnabled()) {
			log.debug("BR.recoverFromMismatchedToken");
		}

		// if next token is what we are looking for then "delete" this token
		if (input.LA(2) == ttype) {
			reportError(e);
			/*
			 * System.err.println("recoverFromMismatchedToken deleting "+input.
			 * LT(1)+ " since "+input.LT(2)+" is what we want");
			 */
			beginResync();
			input.consume(); // simply delete extra token
			endResync();
			input.consume(); // move past ttype token as if all were ok
			return;
		}
		if (!recoverFromMismatchedElement(input, e, follow)) {
			throw e;
		}
	}

	public String[] getTokenNames() {
		return tokenNames;
	}

	public String getGrammarFileName() {
		return "/workspace/xssprotect/trunk/grammar/htmlTreeParser.g";
	}

	// $ANTLR start document
	// /workspace/xssprotect/trunk/grammar/htmlTreeParser.g:15:1: document :
	// element ;
	public final void document() throws RecognitionException {
		try {
			// /workspace/xssprotect/trunk/grammar/htmlTreeParser.g:15:10: (
			// element )
			// /workspace/xssprotect/trunk/grammar/htmlTreeParser.g:15:12:
			// element
			{
				pushFollow(FOLLOW_element_in_document43);
				element();
				_fsp--;

			}

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return;
	}
	// $ANTLR end document

	// $ANTLR start element
	// /workspace/xssprotect/trunk/grammar/htmlTreeParser.g:17:1: element : ^(
	// ELEMENT name= GENERIC_ID ( ^( ATTRIBUTE attrName= GENERIC_ID value=
	// ATTR_VALUE ) )* ( ^( SETTING attrName= GENERIC_ID ) )* ( element | text=
	// PCDATA )* ) ;
	public final void element() throws RecognitionException {
		Tree name = null;
		Tree attrName = null;
		Tree value = null;
		Tree text = null;

		try {
			// /workspace/xssprotect/trunk/grammar/htmlTreeParser.g:18:5: ( ^(
			// ELEMENT name= GENERIC_ID ( ^( ATTRIBUTE attrName= GENERIC_ID
			// value= ATTR_VALUE ) )* ( ^( SETTING attrName= GENERIC_ID ) )* (
			// element | text= PCDATA )* ) )
			// /workspace/xssprotect/trunk/grammar/htmlTreeParser.g:18:7: ^(
			// ELEMENT name= GENERIC_ID ( ^( ATTRIBUTE attrName= GENERIC_ID
			// value= ATTR_VALUE ) )* ( ^( SETTING attrName= GENERIC_ID ) )* (
			// element | text= PCDATA )* )
			{
				match(input, ELEMENT, FOLLOW_ELEMENT_in_element58);

				match(input, Token.DOWN, null);
				name = (Tree) input.LT(1);
				match(input, GENERIC_ID, FOLLOW_GENERIC_ID_in_element62);
				HTMLParser.openTag(name.getText());
				// /workspace/xssprotect/trunk/grammar/htmlTreeParser.g:20:13: (
				// ^( ATTRIBUTE attrName= GENERIC_ID value= ATTR_VALUE ) )*
				loop1: do {
					int alt1 = 2;
					int LA1_0 = input.LA(1);

					if ((LA1_0 == ATTRIBUTE)) {
						alt1 = 1;
					}

					switch (alt1) {
					case 1:
					// /workspace/xssprotect/trunk/grammar/htmlTreeParser.g:21:17:
					// ^( ATTRIBUTE attrName= GENERIC_ID value= ATTR_VALUE )
					{
						match(input, ATTRIBUTE, FOLLOW_ATTRIBUTE_in_element111);

						match(input, Token.DOWN, null);
						attrName = (Tree) input.LT(1);
						match(input, GENERIC_ID, FOLLOW_GENERIC_ID_in_element115);
						value = (Tree) input.LT(1);
						match(input, ATTR_VALUE, FOLLOW_ATTR_VALUE_in_element119);

						match(input, Token.UP, null);
						HTMLParser.addAttribute(attrName.getText(), value.getText());

					}
						break;

					default:
						break loop1;
					}
				} while (true);

				// /workspace/xssprotect/trunk/grammar/htmlTreeParser.g:24:13: (
				// ^( SETTING attrName= GENERIC_ID ) )*
				loop2: do {
					int alt2 = 2;
					int LA2_0 = input.LA(1);

					if ((LA2_0 == SETTING)) {
						alt2 = 1;
					}

					switch (alt2) {
					case 1:
					// /workspace/xssprotect/trunk/grammar/htmlTreeParser.g:25:15:
					// ^( SETTING attrName= GENERIC_ID )
					{
						match(input, SETTING, FOLLOW_SETTING_in_element186);

						match(input, Token.DOWN, null);
						attrName = (Tree) input.LT(1);
						match(input, GENERIC_ID, FOLLOW_GENERIC_ID_in_element190);

						match(input, Token.UP, null);
						HTMLParser.addAttribute(attrName.getText(), "");

					}
						break;

					default:
						break loop2;
					}
				} while (true);

				HTMLParser.finishAttributes();
				// /workspace/xssprotect/trunk/grammar/htmlTreeParser.g:29:13: (
				// element | text= PCDATA )*
				loop3: do {
					int alt3 = 3;
					int LA3_0 = input.LA(1);

					if ((LA3_0 == ELEMENT)) {
						alt3 = 1;
					} else if ((LA3_0 == PCDATA)) {
						alt3 = 2;
					}

					switch (alt3) {
					case 1:
					// /workspace/xssprotect/trunk/grammar/htmlTreeParser.g:29:14:
					// element
					{
						pushFollow(FOLLOW_element_in_element254);
						element();
						_fsp--;

					}
						break;
					case 2:
					// /workspace/xssprotect/trunk/grammar/htmlTreeParser.g:30:15:
					// text= PCDATA
					{
						text = (Tree) input.LT(1);
						match(input, PCDATA, FOLLOW_PCDATA_in_element272);
						HTMLParser.addText(text.getText());

					}
						break;

					default:
						break loop3;
					}
				} while (true);

				HTMLParser.closeTag(name.getText());

				match(input, Token.UP, null);

			}

		} catch (IOException ioe) {

			ioe.printStackTrace();

		} finally {
		}
		return;
	}
	// $ANTLR end element

	public static final BitSet FOLLOW_element_in_document43 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_ELEMENT_in_element58 = new BitSet(new long[] { 0x0000000000000004L });
	public static final BitSet FOLLOW_GENERIC_ID_in_element62 = new BitSet(new long[] { 0x0000000000381008L });
	public static final BitSet FOLLOW_ATTRIBUTE_in_element111 = new BitSet(new long[] { 0x0000000000000004L });
	public static final BitSet FOLLOW_GENERIC_ID_in_element115 = new BitSet(new long[] { 0x0000000000000800L });
	public static final BitSet FOLLOW_ATTR_VALUE_in_element119 = new BitSet(new long[] { 0x0000000000000008L });
	public static final BitSet FOLLOW_SETTING_in_element186 = new BitSet(new long[] { 0x0000000000000004L });
	public static final BitSet FOLLOW_GENERIC_ID_in_element190 = new BitSet(new long[] { 0x0000000000000008L });
	public static final BitSet FOLLOW_element_in_element254 = new BitSet(new long[] { 0x0000000000081008L });
	public static final BitSet FOLLOW_PCDATA_in_element272 = new BitSet(new long[] { 0x0000000000081008L });

}