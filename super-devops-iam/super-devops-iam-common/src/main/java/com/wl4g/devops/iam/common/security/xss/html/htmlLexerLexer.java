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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class htmlLexerLexer extends Lexer {
	public static final int PCDATA = 12;
	public static final int TAG_EMPTY_CLOSE = 7;
	public static final int WS = 8;
	public static final int TAG_CLOSE = 6;
	public static final int SPECIALCHAR = 17;
	public static final int LETTER = 13;
	public static final int GENERIC_ID = 15;
	public static final int ATTR_VALUE = 11;
	public static final int TAG_END_OPEN = 5;
	public static final int DIGIT = 16;
	public static final int WSCHAR = 10;
	public static final int QUOTECHAR = 9;
	public static final int Tokens = 18;
	public static final int EOF = -1;
	public static final int TAG_START_OPEN = 4;
	public static final int NAMECHAR = 14;

	boolean tagMode = false;

	protected Logger log = LoggerFactory.getLogger(getClass());

	public htmlLexerLexer() {
		;
	}

	public htmlLexerLexer(CharStream input) {
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

	public String getGrammarFileName() {
		return "/workspace/xssprotect/trunk/grammar/htmlLexer.g";
	}

	// $ANTLR start TAG_START_OPEN
	public final void mTAG_START_OPEN() throws RecognitionException {
		try {
			int _type = TAG_START_OPEN;
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:11:16: ({...}? =>
			// '<' )
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:11:18: {...}? =>
			// '<'
			{
				if (!(!tagMode)) {
					throw new FailedPredicateException(input, "TAG_START_OPEN", " !tagMode ");
				}
				match('<');
				tagMode = true;

			}

			this.type = _type;
		} finally {
		}
	}
	// $ANTLR end TAG_START_OPEN

	// $ANTLR start TAG_END_OPEN
	public final void mTAG_END_OPEN() throws RecognitionException {
		try {
			int _type = TAG_END_OPEN;
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:12:14: ({...}? =>
			// '</' )
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:12:16: {...}? =>
			// '</'
			{
				if (!(!tagMode)) {
					throw new FailedPredicateException(input, "TAG_END_OPEN", " !tagMode ");
				}
				match("</");

				tagMode = true;

			}

			this.type = _type;
		} finally {
		}
	}
	// $ANTLR end TAG_END_OPEN

	// $ANTLR start TAG_CLOSE
	public final void mTAG_CLOSE() throws RecognitionException {
		try {
			int _type = TAG_CLOSE;
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:13:11: ({...}? =>
			// '>' )
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:13:13: {...}? =>
			// '>'
			{
				if (!(tagMode)) {
					throw new FailedPredicateException(input, "TAG_CLOSE", " tagMode ");
				}
				match('>');
				tagMode = false;

			}

			this.type = _type;
		} finally {
		}
	}
	// $ANTLR end TAG_CLOSE

	// $ANTLR start TAG_EMPTY_CLOSE
	public final void mTAG_EMPTY_CLOSE() throws RecognitionException {
		try {
			int _type = TAG_EMPTY_CLOSE;
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:14:17: ({...}? =>
			// '/>' )
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:14:19: {...}? =>
			// '/>'
			{
				if (!(tagMode)) {
					throw new FailedPredicateException(input, "TAG_EMPTY_CLOSE", " tagMode ");
				}
				match("/>");

				tagMode = false;

			}

			this.type = _type;
		} finally {
		}
	}
	// $ANTLR end TAG_EMPTY_CLOSE

	// $ANTLR start ATTR_VALUE
	public final void mATTR_VALUE() throws RecognitionException {
		try {
			int _type = ATTR_VALUE;
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:22:12: ({...}? =>
			// ( ( WS )* '=' ( WS )* ) ( '\"' (~ '\"' )* '\"' | '\\'' (~ '\\''
			// )* '\\'' | '`' (~ '`' )* '`' | ~ QUOTECHAR (~ WSCHAR )* ) )
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:22:14: {...}? =>
			// ( ( WS )* '=' ( WS )* ) ( '\"' (~ '\"' )* '\"' | '\\'' (~ '\\''
			// )* '\\'' | '`' (~ '`' )* '`' | ~ QUOTECHAR (~ WSCHAR )* )
			{
				if (!(tagMode)) {
					throw new FailedPredicateException(input, "ATTR_VALUE", " tagMode ");
				}
				// /workspace/xssprotect/trunk/grammar/htmlLexer.g:23:3: ( ( WS
				// )* '=' ( WS )* )
				// /workspace/xssprotect/trunk/grammar/htmlLexer.g:23:5: ( WS )*
				// '=' ( WS )*
				{
					// /workspace/xssprotect/trunk/grammar/htmlLexer.g:23:5: (
					// WS )*
					loop1: do {
						int alt1 = 2;
						int LA1_0 = input.LA(1);

						if (((LA1_0 >= '\t' && LA1_0 <= '\n') || (LA1_0 >= '\f' && LA1_0 <= '\r') || LA1_0 == ' ') && (tagMode)) {
							alt1 = 1;
						}

						switch (alt1) {
						case 1:
						// /workspace/xssprotect/trunk/grammar/htmlLexer.g:23:5:
						// WS
						{
							mWS();

						}
							break;

						default:
							break loop1;
						}
					} while (true);

					match('=');
					// /workspace/xssprotect/trunk/grammar/htmlLexer.g:23:13: (
					// WS )*
					loop2: do {
						int alt2 = 2;
						alt2 = dfa2.predict(input);
						switch (alt2) {
						case 1:
						// /workspace/xssprotect/trunk/grammar/htmlLexer.g:23:13:
						// WS
						{
							mWS();

						}
							break;

						default:
							break loop2;
						}
					} while (true);

				}

				// /workspace/xssprotect/trunk/grammar/htmlLexer.g:24:3: ( '\"'
				// (~ '\"' )* '\"' | '\\'' (~ '\\'' )* '\\'' | '`' (~ '`' )* '`'
				// | ~ QUOTECHAR (~ WSCHAR )* )
				int alt7 = 4;
				int LA7_0 = input.LA(1);

				if ((LA7_0 == '\"')) {
					alt7 = 1;
				} else if ((LA7_0 == '\'')) {
					alt7 = 2;
				} else if ((LA7_0 == '`')) {
					alt7 = 3;
				} else if (((LA7_0 >= '\u0000' && LA7_0 <= '!') || (LA7_0 >= '#' && LA7_0 <= '&')
						|| (LA7_0 >= '(' && LA7_0 <= '_') || (LA7_0 >= 'a' && LA7_0 <= '\uFFFE'))) {
					alt7 = 4;
				} else {
					NoViableAltException nvae = new NoViableAltException(
							"24:3: ( '\"' (~ '\"' )* '\"' | '\\'' (~ '\\'' )* '\\'' | '`' (~ '`' )* '`' | ~ QUOTECHAR (~ WSCHAR )* )",
							7, 0, input);

					throw nvae;
				}
				switch (alt7) {
				case 1:
				// /workspace/xssprotect/trunk/grammar/htmlLexer.g:25:3: '\"' (~
				// '\"' )* '\"'
				{
					match('\"');
					// /workspace/xssprotect/trunk/grammar/htmlLexer.g:25:7: (~
					// '\"' )*
					loop3: do {
						int alt3 = 2;
						int LA3_0 = input.LA(1);

						if (((LA3_0 >= '\u0000' && LA3_0 <= '!') || (LA3_0 >= '#' && LA3_0 <= '\uFFFE'))) {
							alt3 = 1;
						}

						switch (alt3) {
						case 1:
						// /workspace/xssprotect/trunk/grammar/htmlLexer.g:25:8:
						// ~ '\"'
						{
							if ((input.LA(1) >= '\u0000' && input.LA(1) <= '!')
									|| (input.LA(1) >= '#' && input.LA(1) <= '\uFFFE')) {
								input.consume();

							} else {
								MismatchedSetException mse = new MismatchedSetException(null, input);
								recover(mse);
								throw mse;
							}

						}
							break;

						default:
							break loop3;
						}
					} while (true);

					match('\"');

				}
					break;
				case 2:
				// /workspace/xssprotect/trunk/grammar/htmlLexer.g:26:5: '\\''
				// (~ '\\'' )* '\\''
				{
					match('\'');
					// /workspace/xssprotect/trunk/grammar/htmlLexer.g:26:10: (~
					// '\\'' )*
					loop4: do {
						int alt4 = 2;
						int LA4_0 = input.LA(1);

						if (((LA4_0 >= '\u0000' && LA4_0 <= '&') || (LA4_0 >= '(' && LA4_0 <= '\uFFFE'))) {
							alt4 = 1;
						}

						switch (alt4) {
						case 1:
						// /workspace/xssprotect/trunk/grammar/htmlLexer.g:26:11:
						// ~ '\\''
						{
							if ((input.LA(1) >= '\u0000' && input.LA(1) <= '&')
									|| (input.LA(1) >= '(' && input.LA(1) <= '\uFFFE')) {
								input.consume();

							} else {
								MismatchedSetException mse = new MismatchedSetException(null, input);
								recover(mse);
								throw mse;
							}

						}
							break;

						default:
							break loop4;
						}
					} while (true);

					match('\'');

				}
					break;
				case 3:
				// /workspace/xssprotect/trunk/grammar/htmlLexer.g:27:5: '`' (~
				// '`' )* '`'
				{
					match('`');
					// /workspace/xssprotect/trunk/grammar/htmlLexer.g:27:9: (~
					// '`' )*
					loop5: do {
						int alt5 = 2;
						int LA5_0 = input.LA(1);

						if (((LA5_0 >= '\u0000' && LA5_0 <= '_') || (LA5_0 >= 'a' && LA5_0 <= '\uFFFE'))) {
							alt5 = 1;
						}

						switch (alt5) {
						case 1:
						// /workspace/xssprotect/trunk/grammar/htmlLexer.g:27:10:
						// ~ '`'
						{
							if ((input.LA(1) >= '\u0000' && input.LA(1) <= '_')
									|| (input.LA(1) >= 'a' && input.LA(1) <= '\uFFFE')) {
								input.consume();

							} else {
								MismatchedSetException mse = new MismatchedSetException(null, input);
								recover(mse);
								throw mse;
							}

						}
							break;

						default:
							break loop5;
						}
					} while (true);

					match('`');

				}
					break;
				case 4:
				// /workspace/xssprotect/trunk/grammar/htmlLexer.g:28:5: ~
				// QUOTECHAR (~ WSCHAR )*
				{
					if ((input.LA(1) >= '\u0000' && input.LA(1) <= '\b') || (input.LA(1) >= '\n' && input.LA(1) <= '\uFFFE')) {
						input.consume();

					} else {
						MismatchedSetException mse = new MismatchedSetException(null, input);
						recover(mse);
						throw mse;
					}

					// /workspace/xssprotect/trunk/grammar/htmlLexer.g:28:16: (~
					// WSCHAR )*
					loop6: do {
						int alt6 = 2;
						int LA6_0 = input.LA(1);

						if (((LA6_0 >= '!' && LA6_0 <= ';') || LA6_0 == '=' || (LA6_0 >= '?' && LA6_0 <= '\uFFFE'))) {
							alt6 = 1;
						}

						switch (alt6) {
						case 1:
						// /workspace/xssprotect/trunk/grammar/htmlLexer.g:28:17:
						// ~ WSCHAR
						{
							if ((input.LA(1) >= '\u0000' && input.LA(1) <= '\t')
									|| (input.LA(1) >= '\u000B' && input.LA(1) <= '\uFFFE')) {
								input.consume();

							} else {
								MismatchedSetException mse = new MismatchedSetException(null, input);
								recover(mse);
								throw mse;
							}

						}
							break;

						default:
							break loop6;
						}
					} while (true);

				}
					break;

				}

			}

			this.type = _type;
		} finally {
		}
	}
	// $ANTLR end ATTR_VALUE

	// $ANTLR start PCDATA
	public final void mPCDATA() throws RecognitionException {
		try {
			int _type = PCDATA;
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:32:8: ({...}? =>
			// ( options {greedy=true; } : ~ '<' )+ )
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:32:10: {...}? =>
			// ( options {greedy=true; } : ~ '<' )+
			{
				if (!(!tagMode)) {
					throw new FailedPredicateException(input, "PCDATA", " !tagMode ");
				}
				// /workspace/xssprotect/trunk/grammar/htmlLexer.g:32:26: (
				// options {greedy=true; } : ~ '<' )+
				int cnt8 = 0;
				loop8: do {
					int alt8 = 2;
					int LA8_0 = input.LA(1);

					if (((LA8_0 >= '\u0000' && LA8_0 <= ';') || (LA8_0 >= '=' && LA8_0 <= '\uFFFE'))) {
						alt8 = 1;
					}

					switch (alt8) {
					case 1:
					// /workspace/xssprotect/trunk/grammar/htmlLexer.g:32:52: ~
					// '<'
					{
						if ((input.LA(1) >= '\u0000' && input.LA(1) <= ';') || (input.LA(1) >= '=' && input.LA(1) <= '\uFFFE')) {
							input.consume();

						} else {
							MismatchedSetException mse = new MismatchedSetException(null, input);
							recover(mse);
							throw mse;
						}

					}
						break;

					default:
						if (cnt8 >= 1)
							break loop8;
						EarlyExitException eee = new EarlyExitException(8, input);
						throw eee;
					}
					cnt8++;
				} while (true);

			}

			this.type = _type;
		} finally {
		}
	}
	// $ANTLR end PCDATA

	// $ANTLR start GENERIC_ID
	public final void mGENERIC_ID() throws RecognitionException {
		try {
			int _type = GENERIC_ID;
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:35:5: ({...}? =>
			// ( LETTER | '_' | ':' ) ( options {greedy=true; } : NAMECHAR )* )
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:35:7: {...}? => (
			// LETTER | '_' | ':' ) ( options {greedy=true; } : NAMECHAR )*
			{
				if (!(tagMode)) {
					throw new FailedPredicateException(input, "GENERIC_ID", " tagMode ");
				}
				if (input.LA(1) == ':' || (input.LA(1) >= 'A' && input.LA(1) <= 'Z') || input.LA(1) == '_'
						|| (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
					input.consume();

				} else {
					MismatchedSetException mse = new MismatchedSetException(null, input);
					recover(mse);
					throw mse;
				}

				// /workspace/xssprotect/trunk/grammar/htmlLexer.g:36:29: (
				// options {greedy=true; } : NAMECHAR )*
				loop9: do {
					int alt9 = 2;
					int LA9_0 = input.LA(1);

					if (((LA9_0 >= '-' && LA9_0 <= '.') || (LA9_0 >= '0' && LA9_0 <= ':') || (LA9_0 >= 'A' && LA9_0 <= 'Z')
							|| LA9_0 == '_' || (LA9_0 >= 'a' && LA9_0 <= 'z'))) {
						alt9 = 1;
					}

					switch (alt9) {
					case 1:
						// /workspace/xssprotect/trunk/grammar/htmlLexer.g:36:56:
						// NAMECHAR
						mNAMECHAR();
						break;
					default:
						break loop9;
					}
				} while (true);

			}

			this.type = _type;
		} finally {
		}
	}
	// $ANTLR end GENERIC_ID

	// $ANTLR start NAMECHAR
	public final void mNAMECHAR() throws RecognitionException {
		try {
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:40:5: ( LETTER |
			// DIGIT | '.' | '-' | '_' | ':' )
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:
			{
				if ((input.LA(1) >= '-' && input.LA(1) <= '.') || (input.LA(1) >= '0' && input.LA(1) <= ':')
						|| (input.LA(1) >= 'A' && input.LA(1) <= 'Z') || input.LA(1) == '_'
						|| (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
					input.consume();

				} else {
					MismatchedSetException mse = new MismatchedSetException(null, input);
					recover(mse);
					throw mse;
				}

			}

		} finally {
		}
	}
	// $ANTLR end NAMECHAR

	// $ANTLR start SPECIALCHAR
	public final void mSPECIALCHAR() throws RecognitionException {
		try {
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:44:2: ( ' ' |
			// '\\t' | '\\u000C' | '>' | '\\'' | '\"' )
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:
			{
				if (input.LA(1) == '\t' || input.LA(1) == '\f' || input.LA(1) == ' ' || input.LA(1) == '\"' || input.LA(1) == '\''
						|| input.LA(1) == '>') {
					input.consume();

				} else {
					MismatchedSetException mse = new MismatchedSetException(null, input);
					recover(mse);
					throw mse;
				}

			}

		} finally {
		}
	}
	// $ANTLR end SPECIALCHAR

	// $ANTLR start WSCHAR
	public final void mWSCHAR() throws RecognitionException {
		try {
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:48:2: ( '\\u0000'
			// .. '\\u0020' | '/>' | '>' | '<' )
			int alt10 = 4;
			switch (input.LA(1)) {
			case '\u0000':
			case '\u0001':
			case '\u0002':
			case '\u0003':
			case '\u0004':
			case '\u0005':
			case '\u0006':
			case '\u0007':
			case '\b':
			case '\t':
			case '\n':
			case '\u000B':
			case '\f':
			case '\r':
			case '\u000E':
			case '\u000F':
			case '\u0010':
			case '\u0011':
			case '\u0012':
			case '\u0013':
			case '\u0014':
			case '\u0015':
			case '\u0016':
			case '\u0017':
			case '\u0018':
			case '\u0019':
			case '\u001A':
			case '\u001B':
			case '\u001C':
			case '\u001D':
			case '\u001E':
			case '\u001F':
			case ' ': {
				alt10 = 1;
			}
				break;
			case '/': {
				alt10 = 2;
			}
				break;
			case '>': {
				alt10 = 3;
			}
				break;
			case '<': {
				alt10 = 4;
			}
				break;
			default:
				NoViableAltException nvae = new NoViableAltException(
						"47:10: fragment WSCHAR : ( '\\u0000' .. '\\u0020' | '/>' | '>' | '<' );", 10, 0, input);

				throw nvae;
			}

			switch (alt10) {
			case 1:
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:48:4: '\\u0000'
			// .. '\\u0020'
			{
				matchRange('\u0000', ' ');

			}
				break;
			case 2:
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:48:25: '/>'
			{
				match("/>");

			}
				break;
			case 3:
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:48:32: '>'
			{
				match('>');

			}
				break;
			case 4:
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:48:38: '<'
			{
				match('<');

			}
				break;

			}
		} finally {
		}
	}
	// $ANTLR end WSCHAR

	// $ANTLR start QUOTECHAR
	public final void mQUOTECHAR() throws RecognitionException {
		try {
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:52:2: ( '\\'' |
			// '\"' | '`' )
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:
			{
				if (input.LA(1) == '\"' || input.LA(1) == '\'' || input.LA(1) == '`') {
					input.consume();

				} else {
					MismatchedSetException mse = new MismatchedSetException(null, input);
					recover(mse);
					throw mse;
				}

			}

		} finally {
		}
	}
	// $ANTLR end QUOTECHAR

	// $ANTLR start DIGIT
	public final void mDIGIT() throws RecognitionException {
		try {
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:56:2: ( '0' ..
			// '9' )
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:56:4: '0' .. '9'
			{
				matchRange('0', '9');

			}

		} finally {
		}
	}
	// $ANTLR end DIGIT

	// $ANTLR start LETTER
	public final void mLETTER() throws RecognitionException {
		try {
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:60:2: ( 'a' ..
			// 'z' | 'A' .. 'Z' )
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:
			{
				if ((input.LA(1) >= 'A' && input.LA(1) <= 'Z') || (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
					input.consume();

				} else {
					MismatchedSetException mse = new MismatchedSetException(null, input);
					recover(mse);
					throw mse;
				}

			}

		} finally {
		}
	}
	// $ANTLR end LETTER

	// $ANTLR start WS
	public final void mWS() throws RecognitionException {
		try {
			int _type = WS;
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:64:5: ({...}? =>
			// ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' ) )
			// /workspace/xssprotect/trunk/grammar/htmlLexer.g:64:8: {...}? => (
			// ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )
			{
				if (!(tagMode)) {
					throw new FailedPredicateException(input, "WS", " tagMode ");
				}
				if ((input.LA(1) >= '\t' && input.LA(1) <= '\n') || (input.LA(1) >= '\f' && input.LA(1) <= '\r')
						|| input.LA(1) == ' ') {
					input.consume();

				} else {
					MismatchedSetException mse = new MismatchedSetException(null, input);
					recover(mse);
					throw mse;
				}

				channel = 99;

			}

			this.type = _type;
		} finally {
		}
	}
	// $ANTLR end WS

	public void mTokens() throws RecognitionException {
		// /workspace/xssprotect/trunk/grammar/htmlLexer.g:1:8: ( TAG_START_OPEN
		// | TAG_END_OPEN | TAG_CLOSE | TAG_EMPTY_CLOSE | ATTR_VALUE | PCDATA |
		// GENERIC_ID | WS )
		int alt11 = 8;
		alt11 = dfa11.predict(input);
		switch (alt11) {
		case 1:
		// /workspace/xssprotect/trunk/grammar/htmlLexer.g:1:10: TAG_START_OPEN
		{
			mTAG_START_OPEN();

		}
			break;
		case 2:
		// /workspace/xssprotect/trunk/grammar/htmlLexer.g:1:25: TAG_END_OPEN
		{
			mTAG_END_OPEN();

		}
			break;
		case 3:
		// /workspace/xssprotect/trunk/grammar/htmlLexer.g:1:38: TAG_CLOSE
		{
			mTAG_CLOSE();

		}
			break;
		case 4:
		// /workspace/xssprotect/trunk/grammar/htmlLexer.g:1:48: TAG_EMPTY_CLOSE
		{
			mTAG_EMPTY_CLOSE();

		}
			break;
		case 5:
		// /workspace/xssprotect/trunk/grammar/htmlLexer.g:1:64: ATTR_VALUE
		{
			mATTR_VALUE();

		}
			break;
		case 6:
		// /workspace/xssprotect/trunk/grammar/htmlLexer.g:1:75: PCDATA
		{
			mPCDATA();

		}
			break;
		case 7:
		// /workspace/xssprotect/trunk/grammar/htmlLexer.g:1:82: GENERIC_ID
		{
			mGENERIC_ID();

		}
			break;
		case 8:
		// /workspace/xssprotect/trunk/grammar/htmlLexer.g:1:93: WS
		{
			mWS();

		}
			break;

		}

	}

	protected DFA2 dfa2 = new DFA2(this);
	protected DFA11 dfa11 = new DFA11(this);
	static final String DFA2_eotS = "\2\uffff\4\1\2\uffff\1\1\1\uffff\1\1\1\uffff\1\1\1\uffff";
	static final String DFA2_eofS = "\16\uffff";
	static final String DFA2_minS = "\1\0\1\uffff\5\0\1\uffff\6\0";
	static final String DFA2_maxS = "\1\ufffe\1\uffff\4\ufffe\1\0\1\uffff\1\ufffe\1\0\1\ufffe\1\0\1\ufffe" + "\1\0";
	static final String DFA2_acceptS = "\1\uffff\1\2\5\uffff\1\1\6\uffff";
	static final String DFA2_specialS = "\2\uffff\1\2\1\1\1\7\1\10\1\6\1\uffff\1\5\1\4\1\11\1\0\1\3\1\12}>";
	static final String[] DFA2_transitionS = { "\11\1\2\2\1\1\2\2\22\1\1\2\uffde\1", "",
			"\41\7\1\6\1\3\4\6\1\4\24\6\1\7\1\6\1\7\41\6\1\5\uff9e\6", "\41\7\1\10\1\11\31\10\1\7\1\10\1\7\uffc0\10",
			"\41\7\6\12\1\13\24\12\1\7\1\12\1\7\uffc0\12", "\41\7\33\14\1\7\1\14\1\7\41\14\1\15\uff9e\14", "\1\uffff", "",
			"\41\7\1\10\1\11\31\10\1\7\1\10\1\7\uffc0\10", "\1\uffff", "\41\7\6\12\1\13\24\12\1\7\1\12\1\7\uffc0\12", "\1\uffff",
			"\41\7\33\14\1\7\1\14\1\7\41\14\1\15\uff9e\14", "\1\uffff" };

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
			return "()* loopback of 23:13: ( WS )*";
		}

		public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
			int _s = s;
			switch (s) {
			case 0:
				int index2_11 = input.index();
				input.rewind();
				s = -1;
				if ((tagMode)) {
					s = 7;
				}

				else if ((true)) {
					s = 1;
				}

				input.seek(index2_11);
				if (s >= 0)
					return s;
				break;
			case 1:
				int LA2_3 = input.LA(1);

				int index2_3 = input.index();
				input.rewind();
				s = -1;
				if ((LA2_3 == '!' || (LA2_3 >= '#' && LA2_3 <= ';') || LA2_3 == '=' || (LA2_3 >= '?' && LA2_3 <= '\uFFFE'))) {
					s = 8;
				}

				else if ((LA2_3 == '\"')) {
					s = 9;
				}

				else if (((LA2_3 >= '\u0000' && LA2_3 <= ' ') || LA2_3 == '<' || LA2_3 == '>') && (tagMode)) {
					s = 7;
				}

				else
					s = 1;

				input.seek(index2_3);
				if (s >= 0)
					return s;
				break;
			case 2:
				int LA2_2 = input.LA(1);

				int index2_2 = input.index();
				input.rewind();
				s = -1;
				if ((LA2_2 == '\"')) {
					s = 3;
				}

				else if ((LA2_2 == '\'')) {
					s = 4;
				}

				else if ((LA2_2 == '`')) {
					s = 5;
				}

				else if ((LA2_2 == '!' || (LA2_2 >= '#' && LA2_2 <= '&') || (LA2_2 >= '(' && LA2_2 <= ';') || LA2_2 == '='
						|| (LA2_2 >= '?' && LA2_2 <= '_') || (LA2_2 >= 'a' && LA2_2 <= '\uFFFE'))) {
					s = 6;
				}

				else if (((LA2_2 >= '\u0000' && LA2_2 <= ' ') || LA2_2 == '<' || LA2_2 == '>') && (tagMode)) {
					s = 7;
				}

				else
					s = 1;

				input.seek(index2_2);
				if (s >= 0)
					return s;
				break;
			case 3:
				int LA2_12 = input.LA(1);

				int index2_12 = input.index();
				input.rewind();
				s = -1;
				if ((LA2_12 == '`')) {
					s = 13;
				}

				else if (((LA2_12 >= '!' && LA2_12 <= ';') || LA2_12 == '=' || (LA2_12 >= '?' && LA2_12 <= '_')
						|| (LA2_12 >= 'a' && LA2_12 <= '\uFFFE'))) {
					s = 12;
				}

				else if (((LA2_12 >= '\u0000' && LA2_12 <= ' ') || LA2_12 == '<' || LA2_12 == '>') && (tagMode)) {
					s = 7;
				}

				else
					s = 1;

				input.seek(index2_12);
				if (s >= 0)
					return s;
				break;
			case 4:
				int index2_9 = input.index();
				input.rewind();
				s = -1;
				if ((tagMode)) {
					s = 7;
				}

				else if ((true)) {
					s = 1;
				}

				input.seek(index2_9);
				if (s >= 0)
					return s;
				break;
			case 5:
				int LA2_8 = input.LA(1);

				int index2_8 = input.index();
				input.rewind();
				s = -1;
				if ((LA2_8 == '\"')) {
					s = 9;
				}

				else if ((LA2_8 == '!' || (LA2_8 >= '#' && LA2_8 <= ';') || LA2_8 == '='
						|| (LA2_8 >= '?' && LA2_8 <= '\uFFFE'))) {
					s = 8;
				}

				else if (((LA2_8 >= '\u0000' && LA2_8 <= ' ') || LA2_8 == '<' || LA2_8 == '>') && (tagMode)) {
					s = 7;
				}

				else
					s = 1;

				input.seek(index2_8);
				if (s >= 0)
					return s;
				break;
			case 6:
				int index2_6 = input.index();
				input.rewind();
				s = -1;
				if ((tagMode)) {
					s = 7;
				}

				else if ((true)) {
					s = 1;
				}

				input.seek(index2_6);
				if (s >= 0)
					return s;
				break;
			case 7:
				int LA2_4 = input.LA(1);

				int index2_4 = input.index();
				input.rewind();
				s = -1;
				if (((LA2_4 >= '!' && LA2_4 <= '&') || (LA2_4 >= '(' && LA2_4 <= ';') || LA2_4 == '='
						|| (LA2_4 >= '?' && LA2_4 <= '\uFFFE'))) {
					s = 10;
				}

				else if ((LA2_4 == '\'')) {
					s = 11;
				}

				else if (((LA2_4 >= '\u0000' && LA2_4 <= ' ') || LA2_4 == '<' || LA2_4 == '>') && (tagMode)) {
					s = 7;
				}

				else
					s = 1;

				input.seek(index2_4);
				if (s >= 0)
					return s;
				break;
			case 8:
				int LA2_5 = input.LA(1);

				int index2_5 = input.index();
				input.rewind();
				s = -1;
				if (((LA2_5 >= '!' && LA2_5 <= ';') || LA2_5 == '=' || (LA2_5 >= '?' && LA2_5 <= '_')
						|| (LA2_5 >= 'a' && LA2_5 <= '\uFFFE'))) {
					s = 12;
				}

				else if ((LA2_5 == '`')) {
					s = 13;
				}

				else if (((LA2_5 >= '\u0000' && LA2_5 <= ' ') || LA2_5 == '<' || LA2_5 == '>') && (tagMode)) {
					s = 7;
				}

				else
					s = 1;

				input.seek(index2_5);
				if (s >= 0)
					return s;
				break;
			case 9:
				int LA2_10 = input.LA(1);

				int index2_10 = input.index();
				input.rewind();
				s = -1;
				if ((LA2_10 == '\'')) {
					s = 11;
				}

				else if (((LA2_10 >= '!' && LA2_10 <= '&') || (LA2_10 >= '(' && LA2_10 <= ';') || LA2_10 == '='
						|| (LA2_10 >= '?' && LA2_10 <= '\uFFFE'))) {
					s = 10;
				}

				else if (((LA2_10 >= '\u0000' && LA2_10 <= ' ') || LA2_10 == '<' || LA2_10 == '>') && (tagMode)) {
					s = 7;
				}

				else
					s = 1;

				input.seek(index2_10);
				if (s >= 0)
					return s;
				break;
			case 10:
				int index2_13 = input.index();
				input.rewind();
				s = -1;
				if ((tagMode)) {
					s = 7;
				}

				else if ((true)) {
					s = 1;
				}

				input.seek(index2_13);
				if (s >= 0)
					return s;
				break;
			}
			NoViableAltException nvae = new NoViableAltException(getDescription(), 2, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	static final String DFA11_eotS = "\1\uffff\1\11\1\12\1\7\1\14\1\7\1\25\4\uffff\1\27\1\uffff\1\7\1"
			+ "\32\3\7\1\32\1\uffff\1\25\4\uffff\1\32\1\uffff\3\32\1\7\1\32\1\7" + "\1\32\1\7\2\32\2\uffff\6\32";
	static final String DFA11_eofS = "\55\uffff";
	static final String DFA11_minS = "\1\0\1\57\1\0\1\76\3\0\3\uffff\3\0\1\11\5\0\1\uffff\2\0\1\uffff"
			+ "\1\0\1\uffff\14\0\2\uffff\6\0";
	static final String DFA11_maxS = "\1\ufffe\1\57\1\ufffe\1\76\3\ufffe\3\uffff\1\0\1\ufffe\1\0\1\75"
			+ "\5\ufffe\1\uffff\1\ufffe\1\0\1\uffff\1\0\1\uffff\1\ufffe\1\0\12" + "\ufffe\2\uffff\6\ufffe";
	static final String DFA11_acceptS = "\7\uffff\1\6\1\2\1\1\11\uffff\1\5\2\uffff\1\3\1\uffff\1\10\14\uffff"
			+ "\1\7\1\4\6\uffff";
	static final String DFA11_specialS = "\1\5\1\15\1\11\1\42\1\17\1\3\1\16\3\uffff\1\37\1\0\1\33\1\21\1\23"
			+ "\1\40\1\34\1\4\1\36\1\uffff\1\27\1\44\1\uffff\1\43\1\uffff\1\26"
			+ "\1\41\1\25\1\14\1\7\1\10\1\22\1\35\1\31\1\1\1\24\1\30\2\uffff\1" + "\12\1\13\1\32\1\20\1\2\1\6}>";
	static final String[] DFA11_transitionS = {
			"\11\7\2\4\1\7\2\4\22\7\1\4\16\7\1\3\12\7\1\6\1\7\1\1\1\5\1\2" + "\2\7\32\6\4\7\1\6\1\7\32\6\uff84\7", "\1\10",
			"\74\7\1\uffff\uffc2\7", "\1\13", "\11\7\2\15\1\7\2\15\22\7\1\15\33\7\1\uffff\1\5\uffc1\7",
			"\11\22\2\16\1\22\2\16\22\22\1\16\1\22\1\17\4\22\1\20\24\22\1" + "\23\43\22\1\21\uff9e\22",
			"\55\7\2\24\1\7\13\24\1\7\1\uffff\4\7\32\24\4\7\1\24\1\7\32\24" + "\uff84\7", "", "", "", "\1\uffff",
			"\74\7\1\uffff\uffc2\7", "\1\uffff", "\2\15\1\uffff\2\15\22\uffff\1\15\34\uffff\1\5",
			"\11\22\2\16\1\22\2\16\22\22\1\16\1\35\1\31\4\35\1\33\24\35\1" + "\23\1\35\1\22\41\35\1\34\uff9e\35",
			"\42\36\1\37\31\36\1\23\uffc2\36", "\47\40\1\41\24\40\1\23\uffc2\40", "\74\42\1\23\43\42\1\43\uff9e\42",
			"\41\7\33\44\1\uffff\1\44\1\7\uffc0\44", "",
			"\55\7\2\24\1\7\13\24\1\7\1\uffff\4\7\32\24\4\7\1\24\1\7\32\24" + "\uff84\7", "\1\uffff", "", "\1\uffff", "",
			"\41\36\1\47\1\50\31\47\1\23\1\47\1\36\uffc0\47", "\1\uffff", "\41\40\6\51\1\52\24\51\1\23\1\51\1\40\uffc0\51",
			"\41\42\33\53\1\23\1\53\1\42\41\53\1\54\uff9e\53", "\41\7\33\44\1\uffff\1\44\1\7\uffc0\44",
			"\42\36\1\37\31\36\1\23\uffc2\36", "\74\7\1\uffff\uffc2\7", "\47\40\1\41\24\40\1\23\uffc2\40",
			"\74\7\1\uffff\uffc2\7", "\74\42\1\23\43\42\1\43\uff9e\42", "\74\7\1\uffff\uffc2\7",
			"\41\7\33\44\1\uffff\1\44\1\7\uffc0\44", "", "", "\41\36\1\47\1\50\31\47\1\23\1\47\1\36\uffc0\47",
			"\41\7\33\44\1\uffff\1\44\1\7\uffc0\44", "\41\40\6\51\1\52\24\51\1\23\1\51\1\40\uffc0\51",
			"\41\7\33\44\1\uffff\1\44\1\7\uffc0\44", "\41\42\33\53\1\23\1\53\1\42\41\53\1\54\uff9e\53",
			"\41\7\33\44\1\uffff\1\44\1\7\uffc0\44" };

	static final short[] DFA11_eot = DFA.unpackEncodedString(DFA11_eotS);
	static final short[] DFA11_eof = DFA.unpackEncodedString(DFA11_eofS);
	static final char[] DFA11_min = DFA.unpackEncodedStringToUnsignedChars(DFA11_minS);
	static final char[] DFA11_max = DFA.unpackEncodedStringToUnsignedChars(DFA11_maxS);
	static final short[] DFA11_accept = DFA.unpackEncodedString(DFA11_acceptS);
	static final short[] DFA11_special = DFA.unpackEncodedString(DFA11_specialS);
	static final short[][] DFA11_transition;

	static {
		int numStates = DFA11_transitionS.length;
		DFA11_transition = new short[numStates][];
		for (int i = 0; i < numStates; i++) {
			DFA11_transition[i] = DFA.unpackEncodedString(DFA11_transitionS[i]);
		}
	}

	class DFA11 extends DFA {

		public DFA11(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 11;
			this.eot = DFA11_eot;
			this.eof = DFA11_eof;
			this.min = DFA11_min;
			this.max = DFA11_max;
			this.accept = DFA11_accept;
			this.special = DFA11_special;
			this.transition = DFA11_transition;
		}

		public String getDescription() {
			return "1:1: Tokens : ( TAG_START_OPEN | TAG_END_OPEN | TAG_CLOSE | TAG_EMPTY_CLOSE | ATTR_VALUE | PCDATA | GENERIC_ID | WS );";
		}

		public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
			int _s = s;
			switch (s) {
			case 0:
				int LA11_11 = input.LA(1);

				int index11_11 = input.index();
				input.rewind();
				s = -1;
				if (((LA11_11 >= '\u0000' && LA11_11 <= ';') || (LA11_11 >= '=' && LA11_11 <= '\uFFFE')) && (!tagMode)) {
					s = 7;
				}

				else
					s = 23;

				input.seek(index11_11);
				if (s >= 0)
					return s;
				break;
			case 1:
				int LA11_34 = input.LA(1);

				int index11_34 = input.index();
				input.rewind();
				s = -1;
				if ((LA11_34 == '`') && ((!tagMode || tagMode))) {
					s = 35;
				}

				else if (((LA11_34 >= '\u0000' && LA11_34 <= ';') || (LA11_34 >= '=' && LA11_34 <= '_')
						|| (LA11_34 >= 'a' && LA11_34 <= '\uFFFE')) && ((!tagMode || tagMode))) {
					s = 34;
				}

				else if ((LA11_34 == '<') && (tagMode)) {
					s = 19;
				}

				else
					s = 7;

				input.seek(index11_34);
				if (s >= 0)
					return s;
				break;
			case 2:
				int LA11_43 = input.LA(1);

				int index11_43 = input.index();
				input.rewind();
				s = -1;
				if ((LA11_43 == '`') && ((!tagMode || tagMode))) {
					s = 44;
				}

				else if (((LA11_43 >= '\u0000' && LA11_43 <= ' ') || LA11_43 == '>') && ((!tagMode || tagMode))) {
					s = 34;
				}

				else if (((LA11_43 >= '!' && LA11_43 <= ';') || LA11_43 == '=' || (LA11_43 >= '?' && LA11_43 <= '_')
						|| (LA11_43 >= 'a' && LA11_43 <= '\uFFFE')) && ((!tagMode || tagMode))) {
					s = 43;
				}

				else if ((LA11_43 == '<') && (tagMode)) {
					s = 19;
				}

				else
					s = 26;

				input.seek(index11_43);
				if (s >= 0)
					return s;
				break;
			case 3:
				int LA11_5 = input.LA(1);

				int index11_5 = input.index();
				input.rewind();
				s = -1;
				if (((LA11_5 >= '\t' && LA11_5 <= '\n') || (LA11_5 >= '\f' && LA11_5 <= '\r') || LA11_5 == ' ')
						&& ((!tagMode || tagMode))) {
					s = 14;
				}

				else if ((LA11_5 == '\"') && ((!tagMode || tagMode))) {
					s = 15;
				}

				else if ((LA11_5 == '\'') && ((!tagMode || tagMode))) {
					s = 16;
				}

				else if ((LA11_5 == '`') && ((!tagMode || tagMode))) {
					s = 17;
				}

				else if (((LA11_5 >= '\u0000' && LA11_5 <= '\b') || LA11_5 == '\u000B'
						|| (LA11_5 >= '\u000E' && LA11_5 <= '\u001F') || LA11_5 == '!' || (LA11_5 >= '#' && LA11_5 <= '&')
						|| (LA11_5 >= '(' && LA11_5 <= ';') || (LA11_5 >= '=' && LA11_5 <= '_')
						|| (LA11_5 >= 'a' && LA11_5 <= '\uFFFE')) && ((!tagMode || tagMode))) {
					s = 18;
				}

				else if ((LA11_5 == '<') && (tagMode)) {
					s = 19;
				}

				else
					s = 7;

				input.seek(index11_5);
				if (s >= 0)
					return s;
				break;
			case 4:
				int LA11_17 = input.LA(1);

				int index11_17 = input.index();
				input.rewind();
				s = -1;
				if (((LA11_17 >= '\u0000' && LA11_17 <= ';') || (LA11_17 >= '=' && LA11_17 <= '_')
						|| (LA11_17 >= 'a' && LA11_17 <= '\uFFFE')) && ((!tagMode || tagMode))) {
					s = 34;
				}

				else if ((LA11_17 == '`') && ((!tagMode || tagMode))) {
					s = 35;
				}

				else if ((LA11_17 == '<') && (tagMode)) {
					s = 19;
				}

				else
					s = 7;

				input.seek(index11_17);
				if (s >= 0)
					return s;
				break;
			case 5:
				int LA11_0 = input.LA(1);

				int index11_0 = input.index();
				input.rewind();
				s = -1;
				if ((LA11_0 == '<') && (!tagMode)) {
					s = 1;
				}

				else if ((LA11_0 == '>') && ((!tagMode || tagMode))) {
					s = 2;
				}

				else if ((LA11_0 == '/') && ((!tagMode || tagMode))) {
					s = 3;
				}

				else if (((LA11_0 >= '\t' && LA11_0 <= '\n') || (LA11_0 >= '\f' && LA11_0 <= '\r') || LA11_0 == ' ')
						&& ((!tagMode || tagMode))) {
					s = 4;
				}

				else if ((LA11_0 == '=') && ((!tagMode || tagMode))) {
					s = 5;
				}

				else if ((LA11_0 == ':' || (LA11_0 >= 'A' && LA11_0 <= 'Z') || LA11_0 == '_' || (LA11_0 >= 'a' && LA11_0 <= 'z'))
						&& ((!tagMode || tagMode))) {
					s = 6;
				}

				else if (((LA11_0 >= '\u0000' && LA11_0 <= '\b') || LA11_0 == '\u000B'
						|| (LA11_0 >= '\u000E' && LA11_0 <= '\u001F') || (LA11_0 >= '!' && LA11_0 <= '.')
						|| (LA11_0 >= '0' && LA11_0 <= '9') || LA11_0 == ';' || (LA11_0 >= '?' && LA11_0 <= '@')
						|| (LA11_0 >= '[' && LA11_0 <= '^') || LA11_0 == '`' || (LA11_0 >= '{' && LA11_0 <= '\uFFFE'))
						&& (!tagMode)) {
					s = 7;
				}

				input.seek(index11_0);
				if (s >= 0)
					return s;
				break;
			case 6:
				int LA11_44 = input.LA(1);

				int index11_44 = input.index();
				input.rewind();
				s = -1;
				if (((LA11_44 >= '!' && LA11_44 <= ';') || LA11_44 == '=' || (LA11_44 >= '?' && LA11_44 <= '\uFFFE'))
						&& ((!tagMode || tagMode))) {
					s = 36;
				}

				else if (((LA11_44 >= '\u0000' && LA11_44 <= ' ') || LA11_44 == '>') && (!tagMode)) {
					s = 7;
				}

				else
					s = 26;

				input.seek(index11_44);
				if (s >= 0)
					return s;
				break;
			case 7:
				int LA11_29 = input.LA(1);

				int index11_29 = input.index();
				input.rewind();
				s = -1;
				if (((LA11_29 >= '!' && LA11_29 <= ';') || LA11_29 == '=' || (LA11_29 >= '?' && LA11_29 <= '\uFFFE'))
						&& ((!tagMode || tagMode))) {
					s = 36;
				}

				else if (((LA11_29 >= '\u0000' && LA11_29 <= ' ') || LA11_29 == '>') && (!tagMode)) {
					s = 7;
				}

				else
					s = 26;

				input.seek(index11_29);
				if (s >= 0)
					return s;
				break;
			case 8:
				int LA11_30 = input.LA(1);

				int index11_30 = input.index();
				input.rewind();
				s = -1;
				if ((LA11_30 == '\"') && ((!tagMode || tagMode))) {
					s = 31;
				}

				else if (((LA11_30 >= '\u0000' && LA11_30 <= '!') || (LA11_30 >= '#' && LA11_30 <= ';')
						|| (LA11_30 >= '=' && LA11_30 <= '\uFFFE')) && ((!tagMode || tagMode))) {
					s = 30;
				}

				else if ((LA11_30 == '<') && (tagMode)) {
					s = 19;
				}

				else
					s = 7;

				input.seek(index11_30);
				if (s >= 0)
					return s;
				break;
			case 9:
				int LA11_2 = input.LA(1);

				int index11_2 = input.index();
				input.rewind();
				s = -1;
				if (((LA11_2 >= '\u0000' && LA11_2 <= ';') || (LA11_2 >= '=' && LA11_2 <= '\uFFFE')) && (!tagMode)) {
					s = 7;
				}

				else
					s = 10;

				input.seek(index11_2);
				if (s >= 0)
					return s;
				break;
			case 10:
				int LA11_39 = input.LA(1);

				int index11_39 = input.index();
				input.rewind();
				s = -1;
				if ((LA11_39 == '\"') && ((!tagMode || tagMode))) {
					s = 40;
				}

				else if ((LA11_39 == '!' || (LA11_39 >= '#' && LA11_39 <= ';') || LA11_39 == '='
						|| (LA11_39 >= '?' && LA11_39 <= '\uFFFE')) && ((!tagMode || tagMode))) {
					s = 39;
				}

				else if (((LA11_39 >= '\u0000' && LA11_39 <= ' ') || LA11_39 == '>') && ((!tagMode || tagMode))) {
					s = 30;
				}

				else if ((LA11_39 == '<') && (tagMode)) {
					s = 19;
				}

				else
					s = 26;

				input.seek(index11_39);
				if (s >= 0)
					return s;
				break;
			case 11:
				int LA11_40 = input.LA(1);

				int index11_40 = input.index();
				input.rewind();
				s = -1;
				if (((LA11_40 >= '!' && LA11_40 <= ';') || LA11_40 == '=' || (LA11_40 >= '?' && LA11_40 <= '\uFFFE'))
						&& ((!tagMode || tagMode))) {
					s = 36;
				}

				else if (((LA11_40 >= '\u0000' && LA11_40 <= ' ') || LA11_40 == '>') && (!tagMode)) {
					s = 7;
				}

				else
					s = 26;

				input.seek(index11_40);
				if (s >= 0)
					return s;
				break;
			case 12:
				int LA11_28 = input.LA(1);

				int index11_28 = input.index();
				input.rewind();
				s = -1;
				if (((LA11_28 >= '!' && LA11_28 <= ';') || LA11_28 == '=' || (LA11_28 >= '?' && LA11_28 <= '_')
						|| (LA11_28 >= 'a' && LA11_28 <= '\uFFFE')) && ((!tagMode || tagMode))) {
					s = 43;
				}

				else if (((LA11_28 >= '\u0000' && LA11_28 <= ' ') || LA11_28 == '>') && ((!tagMode || tagMode))) {
					s = 34;
				}

				else if ((LA11_28 == '`') && ((!tagMode || tagMode))) {
					s = 44;
				}

				else if ((LA11_28 == '<') && (tagMode)) {
					s = 19;
				}

				else
					s = 26;

				input.seek(index11_28);
				if (s >= 0)
					return s;
				break;
			case 13:
				int LA11_1 = input.LA(1);

				int index11_1 = input.index();
				input.rewind();
				s = -1;
				if ((LA11_1 == '/') && (!tagMode)) {
					s = 8;
				}

				else
					s = 9;

				input.seek(index11_1);
				if (s >= 0)
					return s;
				break;
			case 14:
				int LA11_6 = input.LA(1);

				int index11_6 = input.index();
				input.rewind();
				s = -1;
				if (((LA11_6 >= '-' && LA11_6 <= '.') || (LA11_6 >= '0' && LA11_6 <= ':') || (LA11_6 >= 'A' && LA11_6 <= 'Z')
						|| LA11_6 == '_' || (LA11_6 >= 'a' && LA11_6 <= 'z')) && ((!tagMode || tagMode))) {
					s = 20;
				}

				else if (((LA11_6 >= '\u0000' && LA11_6 <= ',') || LA11_6 == '/' || LA11_6 == ';'
						|| (LA11_6 >= '=' && LA11_6 <= '@') || (LA11_6 >= '[' && LA11_6 <= '^') || LA11_6 == '`'
						|| (LA11_6 >= '{' && LA11_6 <= '\uFFFE')) && (!tagMode)) {
					s = 7;
				}

				else
					s = 21;

				input.seek(index11_6);
				if (s >= 0)
					return s;
				break;
			case 15:
				int LA11_4 = input.LA(1);

				int index11_4 = input.index();
				input.rewind();
				s = -1;
				if ((LA11_4 == '=') && ((!tagMode || tagMode))) {
					s = 5;
				}

				else if (((LA11_4 >= '\t' && LA11_4 <= '\n') || (LA11_4 >= '\f' && LA11_4 <= '\r') || LA11_4 == ' ')
						&& ((!tagMode || tagMode))) {
					s = 13;
				}

				else if (((LA11_4 >= '\u0000' && LA11_4 <= '\b') || LA11_4 == '\u000B'
						|| (LA11_4 >= '\u000E' && LA11_4 <= '\u001F') || (LA11_4 >= '!' && LA11_4 <= ';')
						|| (LA11_4 >= '>' && LA11_4 <= '\uFFFE')) && (!tagMode)) {
					s = 7;
				}

				else
					s = 12;

				input.seek(index11_4);
				if (s >= 0)
					return s;
				break;
			case 16:
				int LA11_42 = input.LA(1);

				int index11_42 = input.index();
				input.rewind();
				s = -1;
				if (((LA11_42 >= '!' && LA11_42 <= ';') || LA11_42 == '=' || (LA11_42 >= '?' && LA11_42 <= '\uFFFE'))
						&& ((!tagMode || tagMode))) {
					s = 36;
				}

				else if (((LA11_42 >= '\u0000' && LA11_42 <= ' ') || LA11_42 == '>') && (!tagMode)) {
					s = 7;
				}

				else
					s = 26;

				input.seek(index11_42);
				if (s >= 0)
					return s;
				break;
			case 17:
				int LA11_13 = input.LA(1);

				int index11_13 = input.index();
				input.rewind();
				s = -1;
				if ((LA11_13 == '=') && ((!tagMode || tagMode))) {
					s = 5;
				}

				else if (((LA11_13 >= '\t' && LA11_13 <= '\n') || (LA11_13 >= '\f' && LA11_13 <= '\r') || LA11_13 == ' ')
						&& ((!tagMode || tagMode))) {
					s = 13;
				}

				else
					s = 7;

				input.seek(index11_13);
				if (s >= 0)
					return s;
				break;
			case 18:
				int LA11_31 = input.LA(1);

				int index11_31 = input.index();
				input.rewind();
				s = -1;
				if (((LA11_31 >= '\u0000' && LA11_31 <= ';') || (LA11_31 >= '=' && LA11_31 <= '\uFFFE')) && (!tagMode)) {
					s = 7;
				}

				else
					s = 26;

				input.seek(index11_31);
				if (s >= 0)
					return s;
				break;
			case 19:
				int LA11_14 = input.LA(1);

				int index11_14 = input.index();
				input.rewind();
				s = -1;
				if ((LA11_14 == '\"') && ((!tagMode || tagMode))) {
					s = 25;
				}

				else if (((LA11_14 >= '\t' && LA11_14 <= '\n') || (LA11_14 >= '\f' && LA11_14 <= '\r') || LA11_14 == ' ')
						&& ((!tagMode || tagMode))) {
					s = 14;
				}

				else if ((LA11_14 == '\'') && ((!tagMode || tagMode))) {
					s = 27;
				}

				else if ((LA11_14 == '`') && ((!tagMode || tagMode))) {
					s = 28;
				}

				else if ((LA11_14 == '!' || (LA11_14 >= '#' && LA11_14 <= '&') || (LA11_14 >= '(' && LA11_14 <= ';')
						|| LA11_14 == '=' || (LA11_14 >= '?' && LA11_14 <= '_') || (LA11_14 >= 'a' && LA11_14 <= '\uFFFE'))
						&& ((!tagMode || tagMode))) {
					s = 29;
				}

				else if ((LA11_14 == '<') && (tagMode)) {
					s = 19;
				}

				else if (((LA11_14 >= '\u0000' && LA11_14 <= '\b') || LA11_14 == '\u000B'
						|| (LA11_14 >= '\u000E' && LA11_14 <= '\u001F') || LA11_14 == '>') && ((!tagMode || tagMode))) {
					s = 18;
				}

				else
					s = 26;

				input.seek(index11_14);
				if (s >= 0)
					return s;
				break;
			case 20:
				int LA11_35 = input.LA(1);

				int index11_35 = input.index();
				input.rewind();
				s = -1;
				if (((LA11_35 >= '\u0000' && LA11_35 <= ';') || (LA11_35 >= '=' && LA11_35 <= '\uFFFE')) && (!tagMode)) {
					s = 7;
				}

				else
					s = 26;

				input.seek(index11_35);
				if (s >= 0)
					return s;
				break;
			case 21:
				int LA11_27 = input.LA(1);

				int index11_27 = input.index();
				input.rewind();
				s = -1;
				if (((LA11_27 >= '!' && LA11_27 <= '&') || (LA11_27 >= '(' && LA11_27 <= ';') || LA11_27 == '='
						|| (LA11_27 >= '?' && LA11_27 <= '\uFFFE')) && ((!tagMode || tagMode))) {
					s = 41;
				}

				else if ((LA11_27 == '\'') && ((!tagMode || tagMode))) {
					s = 42;
				}

				else if (((LA11_27 >= '\u0000' && LA11_27 <= ' ') || LA11_27 == '>') && ((!tagMode || tagMode))) {
					s = 32;
				}

				else if ((LA11_27 == '<') && (tagMode)) {
					s = 19;
				}

				else
					s = 26;

				input.seek(index11_27);
				if (s >= 0)
					return s;
				break;
			case 22:
				int LA11_25 = input.LA(1);

				int index11_25 = input.index();
				input.rewind();
				s = -1;
				if ((LA11_25 == '!' || (LA11_25 >= '#' && LA11_25 <= ';') || LA11_25 == '='
						|| (LA11_25 >= '?' && LA11_25 <= '\uFFFE')) && ((!tagMode || tagMode))) {
					s = 39;
				}

				else if ((LA11_25 == '\"') && ((!tagMode || tagMode))) {
					s = 40;
				}

				else if (((LA11_25 >= '\u0000' && LA11_25 <= ' ') || LA11_25 == '>') && ((!tagMode || tagMode))) {
					s = 30;
				}

				else if ((LA11_25 == '<') && (tagMode)) {
					s = 19;
				}

				else
					s = 26;

				input.seek(index11_25);
				if (s >= 0)
					return s;
				break;
			case 23:
				int LA11_20 = input.LA(1);

				int index11_20 = input.index();
				input.rewind();
				s = -1;
				if (((LA11_20 >= '-' && LA11_20 <= '.') || (LA11_20 >= '0' && LA11_20 <= ':')
						|| (LA11_20 >= 'A' && LA11_20 <= 'Z') || LA11_20 == '_' || (LA11_20 >= 'a' && LA11_20 <= 'z'))
						&& ((!tagMode || tagMode))) {
					s = 20;
				}

				else if (((LA11_20 >= '\u0000' && LA11_20 <= ',') || LA11_20 == '/' || LA11_20 == ';'
						|| (LA11_20 >= '=' && LA11_20 <= '@') || (LA11_20 >= '[' && LA11_20 <= '^') || LA11_20 == '`'
						|| (LA11_20 >= '{' && LA11_20 <= '\uFFFE')) && (!tagMode)) {
					s = 7;
				}

				else
					s = 21;

				input.seek(index11_20);
				if (s >= 0)
					return s;
				break;
			case 24:
				int LA11_36 = input.LA(1);

				int index11_36 = input.index();
				input.rewind();
				s = -1;
				if (((LA11_36 >= '!' && LA11_36 <= ';') || LA11_36 == '=' || (LA11_36 >= '?' && LA11_36 <= '\uFFFE'))
						&& ((!tagMode || tagMode))) {
					s = 36;
				}

				else if (((LA11_36 >= '\u0000' && LA11_36 <= ' ') || LA11_36 == '>') && (!tagMode)) {
					s = 7;
				}

				else
					s = 26;

				input.seek(index11_36);
				if (s >= 0)
					return s;
				break;
			case 25:
				int LA11_33 = input.LA(1);

				int index11_33 = input.index();
				input.rewind();
				s = -1;
				if (((LA11_33 >= '\u0000' && LA11_33 <= ';') || (LA11_33 >= '=' && LA11_33 <= '\uFFFE')) && (!tagMode)) {
					s = 7;
				}

				else
					s = 26;

				input.seek(index11_33);
				if (s >= 0)
					return s;
				break;
			case 26:
				int LA11_41 = input.LA(1);

				int index11_41 = input.index();
				input.rewind();
				s = -1;
				if ((LA11_41 == '\'') && ((!tagMode || tagMode))) {
					s = 42;
				}

				else if (((LA11_41 >= '!' && LA11_41 <= '&') || (LA11_41 >= '(' && LA11_41 <= ';') || LA11_41 == '='
						|| (LA11_41 >= '?' && LA11_41 <= '\uFFFE')) && ((!tagMode || tagMode))) {
					s = 41;
				}

				else if (((LA11_41 >= '\u0000' && LA11_41 <= ' ') || LA11_41 == '>') && ((!tagMode || tagMode))) {
					s = 32;
				}

				else if ((LA11_41 == '<') && (tagMode)) {
					s = 19;
				}

				else
					s = 26;

				input.seek(index11_41);
				if (s >= 0)
					return s;
				break;
			case 27:
				int index11_12 = input.index();
				input.rewind();
				s = -1;
				if ((!tagMode)) {
					s = 7;
				}

				else if ((tagMode)) {
					s = 24;
				}

				input.seek(index11_12);
				if (s >= 0)
					return s;
				break;
			case 28:
				int LA11_16 = input.LA(1);

				int index11_16 = input.index();
				input.rewind();
				s = -1;
				if (((LA11_16 >= '\u0000' && LA11_16 <= '&') || (LA11_16 >= '(' && LA11_16 <= ';')
						|| (LA11_16 >= '=' && LA11_16 <= '\uFFFE')) && ((!tagMode || tagMode))) {
					s = 32;
				}

				else if ((LA11_16 == '\'') && ((!tagMode || tagMode))) {
					s = 33;
				}

				else if ((LA11_16 == '<') && (tagMode)) {
					s = 19;
				}

				else
					s = 7;

				input.seek(index11_16);
				if (s >= 0)
					return s;
				break;
			case 29:
				int LA11_32 = input.LA(1);

				int index11_32 = input.index();
				input.rewind();
				s = -1;
				if ((LA11_32 == '\'') && ((!tagMode || tagMode))) {
					s = 33;
				}

				else if (((LA11_32 >= '\u0000' && LA11_32 <= '&') || (LA11_32 >= '(' && LA11_32 <= ';')
						|| (LA11_32 >= '=' && LA11_32 <= '\uFFFE')) && ((!tagMode || tagMode))) {
					s = 32;
				}

				else if ((LA11_32 == '<') && (tagMode)) {
					s = 19;
				}

				else
					s = 7;

				input.seek(index11_32);
				if (s >= 0)
					return s;
				break;
			case 30:
				int LA11_18 = input.LA(1);

				int index11_18 = input.index();
				input.rewind();
				s = -1;
				if (((LA11_18 >= '!' && LA11_18 <= ';') || LA11_18 == '=' || (LA11_18 >= '?' && LA11_18 <= '\uFFFE'))
						&& ((!tagMode || tagMode))) {
					s = 36;
				}

				else if (((LA11_18 >= '\u0000' && LA11_18 <= ' ') || LA11_18 == '>') && (!tagMode)) {
					s = 7;
				}

				else
					s = 26;

				input.seek(index11_18);
				if (s >= 0)
					return s;
				break;
			case 31:
				int index11_10 = input.index();
				input.rewind();
				s = -1;
				if ((tagMode)) {
					s = 22;
				}

				else if ((!tagMode)) {
					s = 7;
				}

				input.seek(index11_10);
				if (s >= 0)
					return s;
				break;
			case 32:
				int LA11_15 = input.LA(1);

				int index11_15 = input.index();
				input.rewind();
				s = -1;
				if (((LA11_15 >= '\u0000' && LA11_15 <= '!') || (LA11_15 >= '#' && LA11_15 <= ';')
						|| (LA11_15 >= '=' && LA11_15 <= '\uFFFE')) && ((!tagMode || tagMode))) {
					s = 30;
				}

				else if ((LA11_15 == '\"') && ((!tagMode || tagMode))) {
					s = 31;
				}

				else if ((LA11_15 == '<') && (tagMode)) {
					s = 19;
				}

				else
					s = 7;

				input.seek(index11_15);
				if (s >= 0)
					return s;
				break;
			case 33:
				int index11_26 = input.index();
				input.rewind();
				s = -1;
				if ((tagMode)) {
					s = 19;
				}

				else if ((!tagMode)) {
					s = 7;
				}

				input.seek(index11_26);
				if (s >= 0)
					return s;
				break;
			case 34:
				int LA11_3 = input.LA(1);

				int index11_3 = input.index();
				input.rewind();
				s = -1;
				if ((LA11_3 == '>') && ((!tagMode || tagMode))) {
					s = 11;
				}

				else
					s = 7;

				input.seek(index11_3);
				if (s >= 0)
					return s;
				break;
			case 35:
				int index11_23 = input.index();
				input.rewind();
				s = -1;
				if ((tagMode)) {
					s = 38;
				}

				else if ((!tagMode)) {
					s = 7;
				}

				input.seek(index11_23);
				if (s >= 0)
					return s;
				break;
			case 36:
				int index11_21 = input.index();
				input.rewind();
				s = -1;
				if ((!tagMode)) {
					s = 7;
				}

				else if ((tagMode)) {
					s = 37;
				}

				input.seek(index11_21);
				if (s >= 0)
					return s;
				break;
			}
			NoViableAltException nvae = new NoViableAltException(getDescription(), 11, _s, input);
			error(nvae);
			throw nvae;
		}
	}

}