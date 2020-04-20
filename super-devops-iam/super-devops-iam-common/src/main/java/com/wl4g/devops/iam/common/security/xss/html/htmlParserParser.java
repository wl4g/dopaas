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

import org.antlr.runtime.*;
import java.util.Stack;

import org.antlr.runtime.tree.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class htmlParserParser extends Parser {
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

	protected static class ElementScope_scope {
		String currentElementName;
	}

	protected Logger log = LoggerFactory.getLogger(getClass());

	protected Stack<ElementScope_scope> ElementScope_stack = new Stack<>();

	public htmlParserParser(TokenStream input) {
		super(input);
	}

	protected TreeAdaptor adaptor = new CommonTreeAdaptor();

	public void setTreeAdaptor(TreeAdaptor adaptor) {
		this.adaptor = adaptor;
	}

	public TreeAdaptor getTreeAdaptor() {
		return adaptor;
	}

	public String[] getTokenNames() {
		return tokenNames;
	}

	public String getGrammarFileName() {
		return "/workspace/xssprotect/trunk/grammar/htmlParser.g";
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

	public static class document_return extends ParserRuleReturnScope {
		Object tree;

		public Object getTree() {
			return tree;
		}
	};

	// $ANTLR start document
	// /workspace/xssprotect/trunk/grammar/htmlParser.g:24:1: document : element
	// ;
	public final document_return document() throws RecognitionException {
		document_return retval = new document_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		element_return element1 = null;

		try {
			// /workspace/xssprotect/trunk/grammar/htmlParser.g:24:10: ( element
			// )
			// /workspace/xssprotect/trunk/grammar/htmlParser.g:24:12: element
			{
				root_0 = (Object) adaptor.nil();

				pushFollow(FOLLOW_element_in_document75);
				element1 = element();
				_fsp--;

				adaptor.addChild(root_0, element1.getTree());

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object) adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}
	// $ANTLR end document

	public static class element_return extends ParserRuleReturnScope {
		Object tree;

		public Object getTree() {
			return tree;
		}
	};

	// $ANTLR start element
	// /workspace/xssprotect/trunk/grammar/htmlParser.g:26:1: element : (
	// startTag ( element | PCDATA )* endTag | emptyElement ) ;
	public final element_return element() throws RecognitionException {
		ElementScope_stack.push(new ElementScope_scope());

		element_return retval = new element_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token PCDATA4 = null;
		startTag_return startTag2 = null;

		element_return element3 = null;

		emptyElement_return emptyElement6 = null;

		Object PCDATA4_tree = null;

		try {
			// /workspace/xssprotect/trunk/grammar/htmlParser.g:28:5: ( (
			// startTag ( element | PCDATA )* endTag | emptyElement ) )
			// /workspace/xssprotect/trunk/grammar/htmlParser.g:28:7: ( startTag
			// ( element | PCDATA )* endTag | emptyElement )
			{
				root_0 = (Object) adaptor.nil();

				// /workspace/xssprotect/trunk/grammar/htmlParser.g:28:7: (
				// startTag ( element | PCDATA )* endTag | emptyElement )
				int alt2 = 2;
				alt2 = dfa2.predict(input);
				switch (alt2) {
				case 1:
				// /workspace/xssprotect/trunk/grammar/htmlParser.g:28:9:
				// startTag ( element | PCDATA )* endTag
				{
					pushFollow(FOLLOW_startTag_in_element95);
					startTag2 = startTag();
					_fsp--;

					root_0 = (Object) adaptor.becomeRoot(startTag2.getTree(), root_0);
					// /workspace/xssprotect/trunk/grammar/htmlParser.g:29:13: (
					// element | PCDATA )*
					loop1: do {
						int alt1 = 3;
						int LA1_0 = input.LA(1);

						if ((LA1_0 == TAG_START_OPEN)) {
							alt1 = 1;
						} else if ((LA1_0 == PCDATA)) {
							alt1 = 2;
						}

						switch (alt1) {
						case 1:
						// /workspace/xssprotect/trunk/grammar/htmlParser.g:29:14:
						// element
						{
							pushFollow(FOLLOW_element_in_element111);
							element3 = element();
							_fsp--;

							adaptor.addChild(root_0, element3.getTree());

						}
							break;
						case 2:
						// /workspace/xssprotect/trunk/grammar/htmlParser.g:30:15:
						// PCDATA
						{
							PCDATA4 = (Token) input.LT(1);
							match(input, PCDATA, FOLLOW_PCDATA_in_element127);
							PCDATA4_tree = (Object) adaptor.create(PCDATA4);
							adaptor.addChild(root_0, PCDATA4_tree);

						}
							break;

						default:
							break loop1;
						}
					} while (true);

					pushFollow(FOLLOW_endTag_in_element156);
					endTag();
					_fsp--;

				}
					break;
				case 2:
				// /workspace/xssprotect/trunk/grammar/htmlParser.g:33:11:
				// emptyElement
				{
					pushFollow(FOLLOW_emptyElement_in_element169);
					emptyElement6 = emptyElement();
					_fsp--;

					adaptor.addChild(root_0, emptyElement6.getTree());

				}
					break;

				}

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object) adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
			ElementScope_stack.pop();

		}
		return retval;
	}
	// $ANTLR end element

	public static class startTag_return extends ParserRuleReturnScope {
		Object tree;

		public Object getTree() {
			return tree;
		}
	};

	// $ANTLR start startTag
	// /workspace/xssprotect/trunk/grammar/htmlParser.g:37:1: startTag :
	// TAG_START_OPEN GENERIC_ID ( attribute )* ( setting )* TAG_CLOSE -> ^(
	// ELEMENT GENERIC_ID ( attribute )* ( setting )* ) ;
	public final startTag_return startTag() throws RecognitionException {
		startTag_return retval = new startTag_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token TAG_START_OPEN7 = null;
		Token GENERIC_ID8 = null;
		Token TAG_CLOSE11 = null;
		attribute_return attribute9 = null;

		setting_return setting10 = null;

		RewriteRuleTokenStream stream_TAG_CLOSE = new RewriteRuleTokenStream(adaptor, "token TAG_CLOSE");
		RewriteRuleTokenStream stream_TAG_START_OPEN = new RewriteRuleTokenStream(adaptor, "token TAG_START_OPEN");
		RewriteRuleTokenStream stream_GENERIC_ID = new RewriteRuleTokenStream(adaptor, "token GENERIC_ID");
		RewriteRuleSubtreeStream stream_setting = new RewriteRuleSubtreeStream(adaptor, "rule setting");
		RewriteRuleSubtreeStream stream_attribute = new RewriteRuleSubtreeStream(adaptor, "rule attribute");
		try {
			// /workspace/xssprotect/trunk/grammar/htmlParser.g:38:5: (
			// TAG_START_OPEN GENERIC_ID ( attribute )* ( setting )* TAG_CLOSE
			// -> ^( ELEMENT GENERIC_ID ( attribute )* ( setting )* ) )
			// /workspace/xssprotect/trunk/grammar/htmlParser.g:38:7:
			// TAG_START_OPEN GENERIC_ID ( attribute )* ( setting )* TAG_CLOSE
			TAG_START_OPEN7 = (Token) input.LT(1);
			match(input, TAG_START_OPEN, FOLLOW_TAG_START_OPEN_in_startTag194);
			stream_TAG_START_OPEN.add(TAG_START_OPEN7);

			GENERIC_ID8 = (Token) input.LT(1);
			match(input, GENERIC_ID, FOLLOW_GENERIC_ID_in_startTag196);
			stream_GENERIC_ID.add(GENERIC_ID8);

			// /workspace/xssprotect/trunk/grammar/htmlParser.g:38:33: (
			// attribute )*
			loop3: do {
				int alt3 = 2;
				int LA3_0 = input.LA(1);

				if ((LA3_0 == GENERIC_ID)) {
					int LA3_1 = input.LA(2);

					if ((LA3_1 == ATTR_VALUE)) {
						alt3 = 1;
					}

				}

				switch (alt3) {
				case 1:
				// /workspace/xssprotect/trunk/grammar/htmlParser.g:38:33:
				// attribute
				{
					pushFollow(FOLLOW_attribute_in_startTag198);
					attribute9 = attribute();
					_fsp--;

					stream_attribute.add(attribute9.getTree());

				}
					break;

				default:
					break loop3;
				}
			} while (true);

			// /workspace/xssprotect/trunk/grammar/htmlParser.g:38:44: (
			// setting )*
			loop4: do {
				int alt4 = 2;
				int LA4_0 = input.LA(1);

				if ((LA4_0 == GENERIC_ID)) {
					alt4 = 1;
				}

				switch (alt4) {
				case 1:
				// /workspace/xssprotect/trunk/grammar/htmlParser.g:38:44:
				// setting
				{
					pushFollow(FOLLOW_setting_in_startTag201);
					setting10 = setting();
					_fsp--;

					stream_setting.add(setting10.getTree());

				}
					break;

				default:
					break loop4;
				}
			} while (true);

			TAG_CLOSE11 = (Token) input.LT(1);
			match(input, TAG_CLOSE, FOLLOW_TAG_CLOSE_in_startTag204);
			stream_TAG_CLOSE.add(TAG_CLOSE11);

			((ElementScope_scope) ElementScope_stack.peek()).currentElementName = GENERIC_ID8.getText();

			// AST REWRITE
			// elements: GENERIC_ID, setting, attribute
			// token labels:
			// rule labels: retval
			// token list labels:
			// rule list labels:
			retval.tree = root_0;

			root_0 = (Object) adaptor.nil();
			// 40:9: -> ^( ELEMENT GENERIC_ID ( attribute )* ( setting )* )
			{
				// /workspace/xssprotect/trunk/grammar/htmlParser.g:40:12:
				// ^( ELEMENT GENERIC_ID ( attribute )* ( setting )* )
				{
					Object root_1 = (Object) adaptor.nil();
					root_1 = (Object) adaptor.becomeRoot(adaptor.create(ELEMENT, "ELEMENT"), root_1);

					adaptor.addChild(root_1, stream_GENERIC_ID.next());
					// /workspace/xssprotect/trunk/grammar/htmlParser.g:40:33:
					// ( attribute )*
					while (stream_attribute.hasNext()) {
						adaptor.addChild(root_1, stream_attribute.next());

					}
					stream_attribute.reset();
					// /workspace/xssprotect/trunk/grammar/htmlParser.g:40:44:
					// ( setting )*
					while (stream_setting.hasNext()) {
						adaptor.addChild(root_1, stream_setting.next());

					}
					stream_setting.reset();

					adaptor.addChild(root_0, root_1);
				}

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object) adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}
	// $ANTLR end startTag

	public static class attribute_return extends ParserRuleReturnScope {
		Object tree;

		public Object getTree() {
			return tree;
		}
	};

	// $ANTLR start attribute
	// /workspace/xssprotect/trunk/grammar/htmlParser.g:43:1: attribute :
	// GENERIC_ID ATTR_VALUE -> ^( ATTRIBUTE GENERIC_ID ATTR_VALUE ) ;
	public final attribute_return attribute() throws RecognitionException {
		attribute_return retval = new attribute_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token GENERIC_ID12 = null;
		Token ATTR_VALUE13 = null;

		RewriteRuleTokenStream stream_ATTR_VALUE = new RewriteRuleTokenStream(adaptor, "token ATTR_VALUE");
		RewriteRuleTokenStream stream_GENERIC_ID = new RewriteRuleTokenStream(adaptor, "token GENERIC_ID");

		try {
			// /workspace/xssprotect/trunk/grammar/htmlParser.g:43:11: (
			// GENERIC_ID ATTR_VALUE -> ^( ATTRIBUTE GENERIC_ID ATTR_VALUE ) )
			// /workspace/xssprotect/trunk/grammar/htmlParser.g:43:13:
			// GENERIC_ID ATTR_VALUE
			{
				GENERIC_ID12 = (Token) input.LT(1);
				match(input, GENERIC_ID, FOLLOW_GENERIC_ID_in_attribute254);
				stream_GENERIC_ID.add(GENERIC_ID12);

				ATTR_VALUE13 = (Token) input.LT(1);
				match(input, ATTR_VALUE, FOLLOW_ATTR_VALUE_in_attribute256);
				stream_ATTR_VALUE.add(ATTR_VALUE13);

				// AST REWRITE
				// elements: ATTR_VALUE, GENERIC_ID
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				retval.tree = root_0;
				root_0 = (Object) adaptor.nil();
				// 43:35: -> ^( ATTRIBUTE GENERIC_ID ATTR_VALUE )
				{
					// /workspace/xssprotect/trunk/grammar/htmlParser.g:43:38:
					// ^( ATTRIBUTE GENERIC_ID ATTR_VALUE )
					{
						Object root_1 = (Object) adaptor.nil();
						root_1 = (Object) adaptor.becomeRoot(adaptor.create(ATTRIBUTE, "ATTRIBUTE"), root_1);

						adaptor.addChild(root_1, stream_GENERIC_ID.next());
						adaptor.addChild(root_1, stream_ATTR_VALUE.next());

						adaptor.addChild(root_0, root_1);
					}

				}

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object) adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}
	// $ANTLR end attribute

	public static class setting_return extends ParserRuleReturnScope {
		Object tree;

		public Object getTree() {
			return tree;
		}
	};

	// $ANTLR start setting
	// /workspace/xssprotect/trunk/grammar/htmlParser.g:45:1: setting :
	// GENERIC_ID -> ^( SETTING GENERIC_ID ) ;
	public final setting_return setting() throws RecognitionException {
		setting_return retval = new setting_return();
		retval.start = input.LT(1);

		Object root_0 = null;
		Token GENERIC_ID14 = null;
		RewriteRuleTokenStream stream_GENERIC_ID = new RewriteRuleTokenStream(adaptor, "token GENERIC_ID");
		try {
			// /workspace/xssprotect/trunk/grammar/htmlParser.g:45:9: (
			// GENERIC_ID -> ^( SETTING GENERIC_ID ) )
			// /workspace/xssprotect/trunk/grammar/htmlParser.g:45:11:
			// GENERIC_ID
			{
				GENERIC_ID14 = (Token) input.LT(1);
				match(input, GENERIC_ID, FOLLOW_GENERIC_ID_in_setting276);
				stream_GENERIC_ID.add(GENERIC_ID14);

				// AST REWRITE
				// elements: GENERIC_ID
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				retval.tree = root_0;
				root_0 = (Object) adaptor.nil();
				// 45:22: -> ^( SETTING GENERIC_ID )
				{
					// /workspace/xssprotect/trunk/grammar/htmlParser.g:45:25:
					// ^( SETTING GENERIC_ID )
					{
						Object root_1 = (Object) adaptor.nil();
						root_1 = (Object) adaptor.becomeRoot(adaptor.create(SETTING, "SETTING"), root_1);

						adaptor.addChild(root_1, stream_GENERIC_ID.next());

						adaptor.addChild(root_0, root_1);
					}

				}

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object) adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}
	// $ANTLR end setting

	public static class endTag_return extends ParserRuleReturnScope {
		Object tree;

		public Object getTree() {
			return tree;
		}
	};

	// $ANTLR start endTag
	// /workspace/xssprotect/trunk/grammar/htmlParser.g:47:1: endTag : {...}?
	// TAG_END_OPEN GENERIC_ID TAG_CLOSE ;
	public final endTag_return endTag() throws RecognitionException {
		endTag_return retval = new endTag_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token TAG_END_OPEN15 = null;
		Token GENERIC_ID16 = null;
		Token TAG_CLOSE17 = null;

		Object TAG_END_OPEN15_tree = null;
		Object GENERIC_ID16_tree = null;
		Object TAG_CLOSE17_tree = null;

		try {
			// /workspace/xssprotect/trunk/grammar/htmlParser.g:48:5: ({...}?
			// TAG_END_OPEN GENERIC_ID TAG_CLOSE )
			// /workspace/xssprotect/trunk/grammar/htmlParser.g:48:7: {...}?
			// TAG_END_OPEN GENERIC_ID TAG_CLOSE
			{
				root_0 = (Object) adaptor.nil();

				if (!(((ElementScope_scope) ElementScope_stack.peek()).currentElementName.equals(input.LT(2).getText()))) {
					throw new FailedPredicateException(input, "endTag",
							" $ElementScope::currentElementName.equals(input.LT(2).getText()) ");
				}
				TAG_END_OPEN15 = (Token) input.LT(1);
				match(input, TAG_END_OPEN, FOLLOW_TAG_END_OPEN_in_endTag307);
				TAG_END_OPEN15_tree = (Object) adaptor.create(TAG_END_OPEN15);
				adaptor.addChild(root_0, TAG_END_OPEN15_tree);

				GENERIC_ID16 = (Token) input.LT(1);
				match(input, GENERIC_ID, FOLLOW_GENERIC_ID_in_endTag309);
				GENERIC_ID16_tree = (Object) adaptor.create(GENERIC_ID16);
				adaptor.addChild(root_0, GENERIC_ID16_tree);

				TAG_CLOSE17 = (Token) input.LT(1);
				match(input, TAG_CLOSE, FOLLOW_TAG_CLOSE_in_endTag311);
				TAG_CLOSE17_tree = (Object) adaptor.create(TAG_CLOSE17);
				adaptor.addChild(root_0, TAG_CLOSE17_tree);

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object) adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		} catch (FailedPredicateException fpe) {

			String hdr = getErrorHeader(fpe);
			String msg = "end tag (" + input.LT(2).getText() + ") does not match start tag ("
					+ ((ElementScope_scope) ElementScope_stack.peek()).currentElementName + ") currently open, closing it anyway";
			emitErrorMessage(hdr + " " + msg);
			// consumeUntil(input, TAG_CLOSE);
			// input.consume();

		} finally {
		}
		return retval;
	}
	// $ANTLR end endTag

	public static class emptyElement_return extends ParserRuleReturnScope {
		Object tree;

		public Object getTree() {
			return tree;
		}
	};

	// $ANTLR start emptyElement
	// /workspace/xssprotect/trunk/grammar/htmlParser.g:62:1: emptyElement :
	// TAG_START_OPEN GENERIC_ID ( attribute )* ( setting )* TAG_EMPTY_CLOSE ->
	// ^( ELEMENT GENERIC_ID ( attribute )* ( setting )* ) ;
	public final emptyElement_return emptyElement() throws RecognitionException {
		emptyElement_return retval = new emptyElement_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token TAG_START_OPEN18 = null;
		Token GENERIC_ID19 = null;
		Token TAG_EMPTY_CLOSE22 = null;
		attribute_return attribute20 = null;
		setting_return setting21 = null;

		RewriteRuleTokenStream stream_TAG_EMPTY_CLOSE = new RewriteRuleTokenStream(adaptor, "token TAG_EMPTY_CLOSE");
		RewriteRuleTokenStream stream_TAG_START_OPEN = new RewriteRuleTokenStream(adaptor, "token TAG_START_OPEN");
		RewriteRuleTokenStream stream_GENERIC_ID = new RewriteRuleTokenStream(adaptor, "token GENERIC_ID");
		RewriteRuleSubtreeStream stream_setting = new RewriteRuleSubtreeStream(adaptor, "rule setting");
		RewriteRuleSubtreeStream stream_attribute = new RewriteRuleSubtreeStream(adaptor, "rule attribute");
		try {
			// /workspace/xssprotect/trunk/grammar/htmlParser.g:62:14: (
			// TAG_START_OPEN GENERIC_ID ( attribute )* ( setting )*
			// TAG_EMPTY_CLOSE -> ^( ELEMENT GENERIC_ID ( attribute )* ( setting
			// )* ) )
			// /workspace/xssprotect/trunk/grammar/htmlParser.g:62:16:
			// TAG_START_OPEN GENERIC_ID ( attribute )* ( setting )*
			// TAG_EMPTY_CLOSE
			{
				TAG_START_OPEN18 = (Token) input.LT(1);
				match(input, TAG_START_OPEN, FOLLOW_TAG_START_OPEN_in_emptyElement331);
				stream_TAG_START_OPEN.add(TAG_START_OPEN18);

				GENERIC_ID19 = (Token) input.LT(1);
				match(input, GENERIC_ID, FOLLOW_GENERIC_ID_in_emptyElement333);
				stream_GENERIC_ID.add(GENERIC_ID19);

				// /workspace/xssprotect/trunk/grammar/htmlParser.g:62:42: (
				// attribute )*
				loop5: do {
					int alt5 = 2;
					int LA5_0 = input.LA(1);

					if ((LA5_0 == GENERIC_ID)) {
						int LA5_1 = input.LA(2);

						if ((LA5_1 == ATTR_VALUE)) {
							alt5 = 1;
						}

					}

					switch (alt5) {
					case 1:
					// /workspace/xssprotect/trunk/grammar/htmlParser.g:62:42:
					// attribute
					{
						pushFollow(FOLLOW_attribute_in_emptyElement335);
						attribute20 = attribute();
						_fsp--;

						stream_attribute.add(attribute20.getTree());

					}
						break;

					default:
						break loop5;
					}
				} while (true);

				// /workspace/xssprotect/trunk/grammar/htmlParser.g:62:53: (
				// setting )*
				loop6: do {
					int alt6 = 2;
					int LA6_0 = input.LA(1);

					if ((LA6_0 == GENERIC_ID)) {
						alt6 = 1;
					}

					switch (alt6) {
					case 1:
					// /workspace/xssprotect/trunk/grammar/htmlParser.g:62:53:
					// setting
					{
						pushFollow(FOLLOW_setting_in_emptyElement338);
						setting21 = setting();
						_fsp--;

						stream_setting.add(setting21.getTree());

					}
						break;

					default:
						break loop6;
					}
				} while (true);

				TAG_EMPTY_CLOSE22 = (Token) input.LT(1);
				match(input, TAG_EMPTY_CLOSE, FOLLOW_TAG_EMPTY_CLOSE_in_emptyElement341);
				stream_TAG_EMPTY_CLOSE.add(TAG_EMPTY_CLOSE22);

				// AST REWRITE
				// elements: setting, attribute, GENERIC_ID
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				retval.tree = root_0;
				root_0 = (Object) adaptor.nil();
				// 63:9: -> ^( ELEMENT GENERIC_ID ( attribute )* ( setting )* )
				{
					// /workspace/xssprotect/trunk/grammar/htmlParser.g:63:12:
					// ^( ELEMENT GENERIC_ID ( attribute )* ( setting )* )
					{
						Object root_1 = (Object) adaptor.nil();
						root_1 = (Object) adaptor.becomeRoot(adaptor.create(ELEMENT, "ELEMENT"), root_1);

						adaptor.addChild(root_1, stream_GENERIC_ID.next());
						// /workspace/xssprotect/trunk/grammar/htmlParser.g:63:33:
						// ( attribute )*
						while (stream_attribute.hasNext()) {
							adaptor.addChild(root_1, stream_attribute.next());

						}
						stream_attribute.reset();
						// /workspace/xssprotect/trunk/grammar/htmlParser.g:63:44:
						// ( setting )*
						while (stream_setting.hasNext()) {
							adaptor.addChild(root_1, stream_setting.next());

						}
						stream_setting.reset();

						adaptor.addChild(root_0, root_1);
					}

				}

			}

			retval.stop = input.LT(-1);

			retval.tree = (Object) adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
		} finally {
		}
		return retval;
	}
	// $ANTLR end emptyElement

	protected DFA2 dfa2 = new DFA2(this);
	static final String DFA2_eotS = "\10\uffff";
	static final String DFA2_eofS = "\10\uffff";
	static final String DFA2_minS = "\1\4\1\17\2\6\2\uffff\2\6";
	static final String DFA2_maxS = "\1\4\3\17\2\uffff\2\17";
	static final String DFA2_acceptS = "\4\uffff\1\1\1\2\2\uffff";
	static final String DFA2_specialS = "\10\uffff}>";
	static final String[] DFA2_transitionS = { "\1\1", "\1\2", "\1\4\1\5\7\uffff\1\3", "\1\4\1\5\3\uffff\1\6\3\uffff\1\7", "", "",
			"\1\4\1\5\7\uffff\1\3", "\1\4\1\5\7\uffff\1\7" };

	static final short[] DFA2_eot = DFA.unpackEncodedString(DFA2_eotS);
	static final short[] DFA2_eof = DFA.unpackEncodedString(DFA2_eofS);
	static final char[] DFA2_min = DFA.unpackEncodedStringToUnsignedChars(DFA2_minS);
	static final char[] DFA2_max = DFA.unpackEncodedStringToUnsignedChars(DFA2_maxS);
	static final short[] DFA2_accept = DFA.unpackEncodedString(DFA2_acceptS);
	static final short[] DFA2_special = DFA.unpackEncodedString(DFA2_specialS);
	static final short[][] DFA2_transition;

	static {
		int numStates = DFA2_transitionS.length;
		DFA2_transition = new short[numStates][];
		for (int i = 0; i < numStates; i++) {
			DFA2_transition[i] = DFA.unpackEncodedString(DFA2_transitionS[i]);
		}
	}

	class DFA2 extends DFA {

		public DFA2(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 2;
			this.eot = DFA2_eot;
			this.eof = DFA2_eof;
			this.min = DFA2_min;
			this.max = DFA2_max;
			this.accept = DFA2_accept;
			this.special = DFA2_special;
			this.transition = DFA2_transition;
		}

		public String getDescription() {
			return "28:7: ( startTag ( element | PCDATA )* endTag | emptyElement )";
		}
	}

	public static final BitSet FOLLOW_element_in_document75 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_startTag_in_element95 = new BitSet(new long[] { 0x0000000000001030L });
	public static final BitSet FOLLOW_element_in_element111 = new BitSet(new long[] { 0x0000000000001030L });
	public static final BitSet FOLLOW_PCDATA_in_element127 = new BitSet(new long[] { 0x0000000000001030L });
	public static final BitSet FOLLOW_endTag_in_element156 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_emptyElement_in_element169 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_TAG_START_OPEN_in_startTag194 = new BitSet(new long[] { 0x0000000000008000L });
	public static final BitSet FOLLOW_GENERIC_ID_in_startTag196 = new BitSet(new long[] { 0x0000000000008040L });
	public static final BitSet FOLLOW_attribute_in_startTag198 = new BitSet(new long[] { 0x0000000000008040L });
	public static final BitSet FOLLOW_setting_in_startTag201 = new BitSet(new long[] { 0x0000000000008040L });
	public static final BitSet FOLLOW_TAG_CLOSE_in_startTag204 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_GENERIC_ID_in_attribute254 = new BitSet(new long[] { 0x0000000000000800L });
	public static final BitSet FOLLOW_ATTR_VALUE_in_attribute256 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_GENERIC_ID_in_setting276 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_TAG_END_OPEN_in_endTag307 = new BitSet(new long[] { 0x0000000000008000L });
	public static final BitSet FOLLOW_GENERIC_ID_in_endTag309 = new BitSet(new long[] { 0x0000000000000040L });
	public static final BitSet FOLLOW_TAG_CLOSE_in_endTag311 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_TAG_START_OPEN_in_emptyElement331 = new BitSet(new long[] { 0x0000000000008000L });
	public static final BitSet FOLLOW_GENERIC_ID_in_emptyElement333 = new BitSet(new long[] { 0x0000000000008080L });
	public static final BitSet FOLLOW_attribute_in_emptyElement335 = new BitSet(new long[] { 0x0000000000008080L });
	public static final BitSet FOLLOW_setting_in_emptyElement338 = new BitSet(new long[] { 0x0000000000008080L });
	public static final BitSet FOLLOW_TAG_EMPTY_CLOSE_in_emptyElement341 = new BitSet(new long[] { 0x0000000000000002L });

}