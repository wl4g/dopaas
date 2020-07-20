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
package com.wl4g.devops.components.tools.common.remoting.standard;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

import com.wl4g.devops.components.tools.common.annotation.Nullable;
import com.wl4g.devops.components.tools.common.collection.CollectionUtils2;
import com.wl4g.devops.components.tools.common.collection.multimap.LinkedCaseInsensitiveMap;
import com.wl4g.devops.components.tools.common.lang.Assert2;
import com.wl4g.devops.components.tools.common.lang.ObjectUtils;
import com.wl4g.devops.components.tools.common.lang.StringUtils2;

/**
 * Represents a MIME Type, as originally defined in RFC 2046 and subsequently
 * used in other Internet protocols including HTTP.
 *
 * Consists of a {@linkplain #getType() type} and a {@linkplain #getSubtype()
 * subtype}. Also has functionality to parse MIME Type values from a
 * {@code String} using {@link #valueOf(String)}. For more parsing options see
 * {@link MimeTypeUtils}.
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 * @since 4.0
 * @see MimeTypeUtils
 */
public class HttpMimeType implements Comparable<HttpMimeType>, Serializable {

	private static final long serialVersionUID = 4085923477777865903L;

	protected static final String WILDCARD_TYPE = "*";

	private static final String PARAM_CHARSET = "charset";

	private static final BitSet TOKEN;

	static {
		// variable names refer to RFC 2616, section 2.2
		BitSet ctl = new BitSet(128);
		for (int i = 0; i <= 31; i++) {
			ctl.set(i);
		}
		ctl.set(127);

		BitSet separators = new BitSet(128);
		separators.set('(');
		separators.set(')');
		separators.set('<');
		separators.set('>');
		separators.set('@');
		separators.set(',');
		separators.set(';');
		separators.set(':');
		separators.set('\\');
		separators.set('\"');
		separators.set('/');
		separators.set('[');
		separators.set(']');
		separators.set('?');
		separators.set('=');
		separators.set('{');
		separators.set('}');
		separators.set(' ');
		separators.set('\t');

		TOKEN = new BitSet(128);
		TOKEN.set(0, 128);
		TOKEN.andNot(ctl);
		TOKEN.andNot(separators);
	}

	private final String type;

	private final String subtype;

	private final Map<String, String> parameters;

	@Nullable
	private volatile String toStringValue;

	/**
	 * Create a new {@code MimeType} for the given primary type.
	 * <p>
	 * The {@linkplain #getSubtype() subtype} is set to <code>"&#42;"</code>,
	 * and the parameters are empty.
	 * 
	 * @param type
	 *            the primary type
	 * @throws IllegalArgumentException
	 *             if any of the parameters contains illegal characters
	 */
	public HttpMimeType(String type) {
		this(type, WILDCARD_TYPE);
	}

	/**
	 * Create a new {@code MimeType} for the given primary type and subtype.
	 * <p>
	 * The parameters are empty.
	 * 
	 * @param type
	 *            the primary type
	 * @param subtype
	 *            the subtype
	 * @throws IllegalArgumentException
	 *             if any of the parameters contains illegal characters
	 */
	public HttpMimeType(String type, String subtype) {
		this(type, subtype, Collections.emptyMap());
	}

	/**
	 * Create a new {@code MimeType} for the given type, subtype, and character
	 * set.
	 * 
	 * @param type
	 *            the primary type
	 * @param subtype
	 *            the subtype
	 * @param charset
	 *            the character set
	 * @throws IllegalArgumentException
	 *             if any of the parameters contains illegal characters
	 */
	public HttpMimeType(String type, String subtype, Charset charset) {
		this(type, subtype, Collections.singletonMap(PARAM_CHARSET, charset.name()));
	}

	/**
	 * Copy-constructor that copies the type, subtype, parameters of the given
	 * {@code MimeType}, and allows to set the specified character set.
	 * 
	 * @param other
	 *            the other MimeType
	 * @param charset
	 *            the character set
	 * @throws IllegalArgumentException
	 *             if any of the parameters contains illegal characters
	 * @since 4.3
	 */
	public HttpMimeType(HttpMimeType other, Charset charset) {
		this(other.getType(), other.getSubtype(), addCharsetParameter(charset, other.getParameters()));
	}

	/**
	 * Copy-constructor that copies the type and subtype of the given
	 * {@code MimeType}, and allows for different parameter.
	 * 
	 * @param other
	 *            the other MimeType
	 * @param parameters
	 *            the parameters (may be {@code null})
	 * @throws IllegalArgumentException
	 *             if any of the parameters contains illegal characters
	 */
	public HttpMimeType(HttpMimeType other, @Nullable Map<String, String> parameters) {
		this(other.getType(), other.getSubtype(), parameters);
	}

	/**
	 * Create a new {@code MimeType} for the given type, subtype, and
	 * parameters.
	 * 
	 * @param type
	 *            the primary type
	 * @param subtype
	 *            the subtype
	 * @param parameters
	 *            the parameters (may be {@code null})
	 * @throws IllegalArgumentException
	 *             if any of the parameters contains illegal characters
	 */
	public HttpMimeType(String type, String subtype, @Nullable Map<String, String> parameters) {
		Assert2.hasLength(type, "'type' must not be empty");
		Assert2.hasLength(subtype, "'subtype' must not be empty");
		checkToken(type);
		checkToken(subtype);
		this.type = type.toLowerCase(Locale.ENGLISH);
		this.subtype = subtype.toLowerCase(Locale.ENGLISH);
		if (!CollectionUtils2.isEmpty(parameters)) {
			Map<String, String> map = new LinkedCaseInsensitiveMap<>(parameters.size(), Locale.ENGLISH);
			parameters.forEach((attribute, value) -> {
				checkParameters(attribute, value);
				map.put(attribute, value);
			});
			this.parameters = Collections.unmodifiableMap(map);
		} else {
			this.parameters = Collections.emptyMap();
		}
	}

	/**
	 * Checks the given token string for illegal characters, as defined in RFC
	 * 2616, section 2.2.
	 * 
	 * @throws IllegalArgumentException
	 *             in case of illegal characters
	 * @see <a href="https://tools.ietf.org/html/rfc2616#section-2.2">HTTP 1.1,
	 *      section 2.2</a>
	 */
	private void checkToken(String token) {
		for (int i = 0; i < token.length(); i++) {
			char ch = token.charAt(i);
			if (!TOKEN.get(ch)) {
				throw new IllegalArgumentException("Invalid token character '" + ch + "' in token \"" + token + "\"");
			}
		}
	}

	protected void checkParameters(String attribute, String value) {
		Assert2.hasLength(attribute, "'attribute' must not be empty");
		Assert2.hasLength(value, "'value' must not be empty");
		checkToken(attribute);
		if (PARAM_CHARSET.equals(attribute)) {
			value = unquote(value);
			Charset.forName(value);
		} else if (!isQuotedString(value)) {
			checkToken(value);
		}
	}

	private boolean isQuotedString(String s) {
		if (s.length() < 2) {
			return false;
		} else {
			return ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")));
		}
	}

	protected String unquote(String s) {
		return (isQuotedString(s) ? s.substring(1, s.length() - 1) : s);
	}

	/**
	 * Indicates whether the {@linkplain #getType() type} is the wildcard
	 * character <code>&#42;</code> or not.
	 */
	public boolean isWildcardType() {
		return WILDCARD_TYPE.equals(getType());
	}

	/**
	 * Indicates whether the {@linkplain #getSubtype() subtype} is the wildcard
	 * character <code>&#42;</code> or the wildcard character followed by a
	 * suffix (e.g. <code>&#42;+xml</code>).
	 * 
	 * @return whether the subtype is a wildcard
	 */
	public boolean isWildcardSubtype() {
		return WILDCARD_TYPE.equals(getSubtype()) || getSubtype().startsWith("*+");
	}

	/**
	 * Indicates whether this MIME Type is concrete, i.e. whether neither the
	 * type nor the subtype is a wildcard character <code>&#42;</code>.
	 * 
	 * @return whether this MIME Type is concrete
	 */
	public boolean isConcrete() {
		return !isWildcardType() && !isWildcardSubtype();
	}

	/**
	 * Return the primary type.
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Return the subtype.
	 */
	public String getSubtype() {
		return this.subtype;
	}

	/**
	 * Return the character set, as indicated by a {@code charset} parameter, if
	 * any.
	 * 
	 * @return the character set, or {@code null} if not available
	 * @since 4.3
	 */
	@Nullable
	public Charset getCharset() {
		String charset = getParameter(PARAM_CHARSET);
		return (charset != null ? Charset.forName(unquote(charset)) : null);
	}

	/**
	 * Return a generic parameter value, given a parameter name.
	 * 
	 * @param name
	 *            the parameter name
	 * @return the parameter value, or {@code null} if not present
	 */
	@Nullable
	public String getParameter(String name) {
		return this.parameters.get(name);
	}

	/**
	 * Return all generic parameter values.
	 * 
	 * @return a read-only map (possibly empty, never {@code null})
	 */
	public Map<String, String> getParameters() {
		return this.parameters;
	}

	/**
	 * Indicate whether this MIME Type includes the given MIME Type.
	 * <p>
	 * For instance, {@code text/*} includes {@code text/plain} and
	 * {@code text/html}, and {@code application/*+xml} includes
	 * {@code application/soap+xml}, etc. This method is <b>not</b> symmetric.
	 * 
	 * @param other
	 *            the reference MIME Type with which to compare
	 * @return {@code true} if this MIME Type includes the given MIME Type;
	 *         {@code false} otherwise
	 */
	public boolean includes(@Nullable HttpMimeType other) {
		if (other == null) {
			return false;
		}
		if (isWildcardType()) {
			// */* includes anything
			return true;
		} else if (getType().equals(other.getType())) {
			if (getSubtype().equals(other.getSubtype())) {
				return true;
			}
			if (isWildcardSubtype()) {
				// Wildcard with suffix, e.g. application/*+xml
				int thisPlusIdx = getSubtype().lastIndexOf('+');
				if (thisPlusIdx == -1) {
					return true;
				} else {
					// application/*+xml includes application/soap+xml
					int otherPlusIdx = other.getSubtype().lastIndexOf('+');
					if (otherPlusIdx != -1) {
						String thisSubtypeNoSuffix = getSubtype().substring(0, thisPlusIdx);
						String thisSubtypeSuffix = getSubtype().substring(thisPlusIdx + 1);
						String otherSubtypeSuffix = other.getSubtype().substring(otherPlusIdx + 1);
						if (thisSubtypeSuffix.equals(otherSubtypeSuffix) && WILDCARD_TYPE.equals(thisSubtypeNoSuffix)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Indicate whether this MIME Type is compatible with the given MIME Type.
	 * <p>
	 * For instance, {@code text/*} is compatible with {@code text/plain},
	 * {@code text/html}, and vice versa. In effect, this method is similar to
	 * {@link #includes}, except that it <b>is</b> symmetric.
	 * 
	 * @param other
	 *            the reference MIME Type with which to compare
	 * @return {@code true} if this MIME Type is compatible with the given MIME
	 *         Type; {@code false} otherwise
	 */
	public boolean isCompatibleWith(@Nullable HttpMimeType other) {
		if (other == null) {
			return false;
		}
		if (isWildcardType() || other.isWildcardType()) {
			return true;
		} else if (getType().equals(other.getType())) {
			if (getSubtype().equals(other.getSubtype())) {
				return true;
			}
			// Wildcard with suffix? e.g. application/*+xml
			if (isWildcardSubtype() || other.isWildcardSubtype()) {
				int thisPlusIdx = getSubtype().lastIndexOf('+');
				int otherPlusIdx = other.getSubtype().lastIndexOf('+');
				if (thisPlusIdx == -1 && otherPlusIdx == -1) {
					return true;
				} else if (thisPlusIdx != -1 && otherPlusIdx != -1) {
					String thisSubtypeNoSuffix = getSubtype().substring(0, thisPlusIdx);
					String otherSubtypeNoSuffix = other.getSubtype().substring(0, otherPlusIdx);
					String thisSubtypeSuffix = getSubtype().substring(thisPlusIdx + 1);
					String otherSubtypeSuffix = other.getSubtype().substring(otherPlusIdx + 1);
					if (thisSubtypeSuffix.equals(otherSubtypeSuffix)
							&& (WILDCARD_TYPE.equals(thisSubtypeNoSuffix) || WILDCARD_TYPE.equals(otherSubtypeNoSuffix))) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Similar to {@link #equals(Object)} but based on the type and subtype
	 * only, i.e. ignoring parameters.
	 * 
	 * @param other
	 *            the other mime type to compare to
	 * @return whether the two mime types have the same type and subtype
	 * @since 5.1.4
	 */
	public boolean equalsTypeAndSubtype(@Nullable HttpMimeType other) {
		if (other == null) {
			return false;
		}
		return this.type.equalsIgnoreCase(other.type) && this.subtype.equalsIgnoreCase(other.subtype);
	}

	/**
	 * Unlike {@link Collection#contains(Object)} which relies on
	 * {@link HttpMimeType#equals(Object)}, this method only checks the type and
	 * the subtype, but otherwise ignores parameters.
	 * 
	 * @param mimeTypes
	 *            the list of mime types to perform the check against
	 * @return whether the list contains the given mime type
	 * @since 5.1.4
	 */
	public boolean isPresentIn(Collection<? extends HttpMimeType> mimeTypes) {
		for (HttpMimeType mimeType : mimeTypes) {
			if (mimeType.equalsTypeAndSubtype(this)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean equals(@Nullable Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof HttpMimeType)) {
			return false;
		}
		HttpMimeType otherType = (HttpMimeType) other;
		return (this.type.equalsIgnoreCase(otherType.type) && this.subtype.equalsIgnoreCase(otherType.subtype)
				&& parametersAreEqual(otherType));
	}

	/**
	 * Determine if the parameters in this {@code MimeType} and the supplied
	 * {@code MimeType} are equal, performing case-insensitive comparisons for
	 * {@link Charset Charsets}.
	 * 
	 * @since 4.2
	 */
	private boolean parametersAreEqual(HttpMimeType other) {
		if (this.parameters.size() != other.parameters.size()) {
			return false;
		}

		for (Map.Entry<String, String> entry : this.parameters.entrySet()) {
			String key = entry.getKey();
			if (!other.parameters.containsKey(key)) {
				return false;
			}
			if (PARAM_CHARSET.equals(key)) {
				if (!ObjectUtils.nullSafeEquals(getCharset(), other.getCharset())) {
					return false;
				}
			} else if (!ObjectUtils.nullSafeEquals(entry.getValue(), other.parameters.get(key))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = this.type.hashCode();
		result = 31 * result + this.subtype.hashCode();
		result = 31 * result + this.parameters.hashCode();
		return result;
	}

	@Override
	public String toString() {
		String value = this.toStringValue;
		if (value == null) {
			StringBuilder builder = new StringBuilder();
			appendTo(builder);
			value = builder.toString();
			this.toStringValue = value;
		}
		return value;
	}

	protected void appendTo(StringBuilder builder) {
		builder.append(this.type);
		builder.append('/');
		builder.append(this.subtype);
		appendTo(this.parameters, builder);
	}

	private void appendTo(Map<String, String> map, StringBuilder builder) {
		map.forEach((key, val) -> {
			builder.append(';');
			builder.append(key);
			builder.append('=');
			builder.append(val);
		});
	}

	/**
	 * Compares this MIME Type to another alphabetically.
	 * 
	 * @param other
	 *            the MIME Type to compare to
	 * @see MimeTypeUtils#sortBySpecificity(List)
	 */
	@Override
	public int compareTo(HttpMimeType other) {
		int comp = getType().compareToIgnoreCase(other.getType());
		if (comp != 0) {
			return comp;
		}
		comp = getSubtype().compareToIgnoreCase(other.getSubtype());
		if (comp != 0) {
			return comp;
		}
		comp = getParameters().size() - other.getParameters().size();
		if (comp != 0) {
			return comp;
		}

		TreeSet<String> thisAttributes = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		thisAttributes.addAll(getParameters().keySet());
		TreeSet<String> otherAttributes = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		otherAttributes.addAll(other.getParameters().keySet());
		Iterator<String> thisAttributesIterator = thisAttributes.iterator();
		Iterator<String> otherAttributesIterator = otherAttributes.iterator();

		while (thisAttributesIterator.hasNext()) {
			String thisAttribute = thisAttributesIterator.next();
			String otherAttribute = otherAttributesIterator.next();
			comp = thisAttribute.compareToIgnoreCase(otherAttribute);
			if (comp != 0) {
				return comp;
			}
			if (PARAM_CHARSET.equals(thisAttribute)) {
				Charset thisCharset = getCharset();
				Charset otherCharset = other.getCharset();
				if (thisCharset != otherCharset) {
					if (thisCharset == null) {
						return -1;
					}
					if (otherCharset == null) {
						return 1;
					}
					comp = thisCharset.compareTo(otherCharset);
					if (comp != 0) {
						return comp;
					}
				}
			} else {
				String thisValue = getParameters().get(thisAttribute);
				String otherValue = other.getParameters().get(otherAttribute);
				if (otherValue == null) {
					otherValue = "";
				}
				comp = thisValue.compareTo(otherValue);
				if (comp != 0) {
					return comp;
				}
			}
		}

		return 0;
	}

	/**
	 * Parse the given String value into a {@code MimeType} object, with this
	 * method name following the 'valueOf' naming convention (as supported by
	 * {@link org.springframework.core.convert.ConversionService}.
	 * 
	 * @see MimeTypeUtils#parseMimeType(String)
	 */
	public static HttpMimeType valueOf(String value) {
		return MimeTypeUtils.parseMimeType(value);
	}

	private static Map<String, String> addCharsetParameter(Charset charset, Map<String, String> parameters) {
		Map<String, String> map = new LinkedHashMap<>(parameters);
		map.put(PARAM_CHARSET, charset.name());
		return map;
	}

	/**
	 * Comparator to sort {@link HttpMimeType MimeTypes} in order of
	 * specificity.
	 *
	 * @param <T>
	 *            the type of mime types that may be compared by this comparator
	 */
	public static class SpecificityComparator<T extends HttpMimeType> implements Comparator<T> {

		@Override
		public int compare(T mimeType1, T mimeType2) {
			if (mimeType1.isWildcardType() && !mimeType2.isWildcardType()) { // */*
																				// <
																				// audio/*
				return 1;
			} else if (mimeType2.isWildcardType() && !mimeType1.isWildcardType()) { // audio/*
																					// >
																					// */*
				return -1;
			} else if (!mimeType1.getType().equals(mimeType2.getType())) { // audio/basic
																			// ==
																			// text/html
				return 0;
			} else { // mediaType1.getType().equals(mediaType2.getType())
				if (mimeType1.isWildcardSubtype() && !mimeType2.isWildcardSubtype()) { // audio/*
																						// <
																						// audio/basic
					return 1;
				} else if (mimeType2.isWildcardSubtype() && !mimeType1.isWildcardSubtype()) { // audio/basic
																								// >
																								// audio/*
					return -1;
				} else if (!mimeType1.getSubtype().equals(mimeType2.getSubtype())) { // audio/basic
																						// ==
																						// audio/wave
					return 0;
				} else { // mediaType2.getSubtype().equals(mediaType2.getSubtype())
					return compareParameters(mimeType1, mimeType2);
				}
			}
		}

		protected int compareParameters(T mimeType1, T mimeType2) {
			int paramsSize1 = mimeType1.getParameters().size();
			int paramsSize2 = mimeType2.getParameters().size();
			return Integer.compare(paramsSize2, paramsSize1); // audio/basic;level=1
																// < audio/basic
		}
	}

	/**
	 * Miscellaneous {@link HttpMimeType} utility methods.
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020年7月2日
	 * @since
	 */
	public abstract static class MimeTypeUtils {

		private static final byte[] BOUNDARY_CHARS = new byte[] { '-', '_', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'a',
				'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
				'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
				'V', 'W', 'X', 'Y', 'Z' };

		private static final Random RND = new SecureRandom();

		private static Charset US_ASCII = Charset.forName("US-ASCII");

		/**
		 * Comparator used by {@link #sortBySpecificity(List)}.
		 */
		public static final Comparator<HttpMimeType> SPECIFICITY_COMPARATOR = new SpecificityComparator<HttpMimeType>();

		/**
		 * Public constant mime type that includes all media ranges (i.e.
		 * "&#42;/&#42;").
		 */
		public static final HttpMimeType ALL;

		/**
		 * A String equivalent of {@link MimeTypeUtils#ALL}.
		 */
		public static final String ALL_VALUE = "*/*";

		/**
		 * Public constant mime type for {@code application/atom+xml}.
		 * 
		 * @deprecated as of 4.3.6, in favor of {@code MediaType} constants
		 */
		@Deprecated
		public final static HttpMimeType APPLICATION_ATOM_XML;

		/**
		 * A String equivalent of {@link MimeTypeUtils#APPLICATION_ATOM_XML}.
		 * 
		 * @deprecated as of 4.3.6, in favor of {@code MediaType} constants
		 */
		@Deprecated
		public final static String APPLICATION_ATOM_XML_VALUE = "application/atom+xml";

		/**
		 * Public constant mime type for
		 * {@code application/x-www-form-urlencoded}.
		 * 
		 * @deprecated as of 4.3.6, in favor of {@code MediaType} constants
		 */
		@Deprecated
		public final static HttpMimeType APPLICATION_FORM_URLENCODED;

		/**
		 * A String equivalent of
		 * {@link MimeTypeUtils#APPLICATION_FORM_URLENCODED}.
		 * 
		 * @deprecated as of 4.3.6, in favor of {@code MediaType} constants
		 */
		@Deprecated
		public final static String APPLICATION_FORM_URLENCODED_VALUE = "application/x-www-form-urlencoded";

		/**
		 * Public constant mime type for {@code application/json}.
		 */
		public final static HttpMimeType APPLICATION_JSON;

		/**
		 * A String equivalent of {@link MimeTypeUtils#APPLICATION_JSON}.
		 */
		public final static String APPLICATION_JSON_VALUE = "application/json";

		/**
		 * Public constant mime type for {@code application/octet-stream}.
		 */
		public final static HttpMimeType APPLICATION_OCTET_STREAM;

		/**
		 * A String equivalent of
		 * {@link MimeTypeUtils#APPLICATION_OCTET_STREAM}.
		 */
		public final static String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";

		/**
		 * Public constant mime type for {@code application/xhtml+xml}.
		 * 
		 * @deprecated as of 4.3.6, in favor of {@code MediaType} constants
		 */
		@Deprecated
		public final static HttpMimeType APPLICATION_XHTML_XML;

		/**
		 * A String equivalent of {@link MimeTypeUtils#APPLICATION_XHTML_XML}.
		 * 
		 * @deprecated as of 4.3.6, in favor of {@code MediaType} constants
		 */
		@Deprecated
		public final static String APPLICATION_XHTML_XML_VALUE = "application/xhtml+xml";

		/**
		 * Public constant mime type for {@code application/xml}.
		 */
		public final static HttpMimeType APPLICATION_XML;

		/**
		 * A String equivalent of {@link MimeTypeUtils#APPLICATION_XML}.
		 */
		public final static String APPLICATION_XML_VALUE = "application/xml";

		/**
		 * Public constant mime type for {@code image/gif}.
		 */
		public final static HttpMimeType IMAGE_GIF;

		/**
		 * A String equivalent of {@link MimeTypeUtils#IMAGE_GIF}.
		 */
		public final static String IMAGE_GIF_VALUE = "image/gif";

		/**
		 * Public constant mime type for {@code image/jpeg}.
		 */
		public final static HttpMimeType IMAGE_JPEG;

		/**
		 * A String equivalent of {@link MimeTypeUtils#IMAGE_JPEG}.
		 */
		public final static String IMAGE_JPEG_VALUE = "image/jpeg";

		/**
		 * Public constant mime type for {@code image/png}.
		 */
		public final static HttpMimeType IMAGE_PNG;

		/**
		 * A String equivalent of {@link MimeTypeUtils#IMAGE_PNG}.
		 */
		public final static String IMAGE_PNG_VALUE = "image/png";

		/**
		 * Public constant mime type for {@code multipart/form-data}.
		 * 
		 * @deprecated as of 4.3.6, in favor of {@code MediaType} constants
		 */
		@Deprecated
		public final static HttpMimeType MULTIPART_FORM_DATA;

		/**
		 * A String equivalent of {@link MimeTypeUtils#MULTIPART_FORM_DATA}.
		 * 
		 * @deprecated as of 4.3.6, in favor of {@code MediaType} constants
		 */
		@Deprecated
		public final static String MULTIPART_FORM_DATA_VALUE = "multipart/form-data";

		/**
		 * Public constant mime type for {@code text/html}.
		 */
		public final static HttpMimeType TEXT_HTML;

		/**
		 * A String equivalent of {@link MimeTypeUtils#TEXT_HTML}.
		 */
		public final static String TEXT_HTML_VALUE = "text/html";

		/**
		 * Public constant mime type for {@code text/plain}.
		 */
		public final static HttpMimeType TEXT_PLAIN;

		/**
		 * A String equivalent of {@link MimeTypeUtils#TEXT_PLAIN}.
		 */
		public final static String TEXT_PLAIN_VALUE = "text/plain";

		/**
		 * Public constant mime type for {@code text/xml}.
		 */
		public final static HttpMimeType TEXT_XML;

		/**
		 * A String equivalent of {@link MimeTypeUtils#TEXT_XML}.
		 */
		public final static String TEXT_XML_VALUE = "text/xml";

		static {
			ALL = HttpMimeType.valueOf(ALL_VALUE);
			APPLICATION_ATOM_XML = HttpMimeType.valueOf(APPLICATION_ATOM_XML_VALUE);
			APPLICATION_FORM_URLENCODED = HttpMimeType.valueOf(APPLICATION_FORM_URLENCODED_VALUE);
			APPLICATION_JSON = HttpMimeType.valueOf(APPLICATION_JSON_VALUE);
			APPLICATION_OCTET_STREAM = HttpMimeType.valueOf(APPLICATION_OCTET_STREAM_VALUE);
			APPLICATION_XHTML_XML = HttpMimeType.valueOf(APPLICATION_XHTML_XML_VALUE);
			APPLICATION_XML = HttpMimeType.valueOf(APPLICATION_XML_VALUE);
			IMAGE_GIF = HttpMimeType.valueOf(IMAGE_GIF_VALUE);
			IMAGE_JPEG = HttpMimeType.valueOf(IMAGE_JPEG_VALUE);
			IMAGE_PNG = HttpMimeType.valueOf(IMAGE_PNG_VALUE);
			MULTIPART_FORM_DATA = HttpMimeType.valueOf(MULTIPART_FORM_DATA_VALUE);
			TEXT_HTML = HttpMimeType.valueOf(TEXT_HTML_VALUE);
			TEXT_PLAIN = HttpMimeType.valueOf(TEXT_PLAIN_VALUE);
			TEXT_XML = HttpMimeType.valueOf(TEXT_XML_VALUE);
		}

		/**
		 * Parse the given String into a single {@code MimeType}.
		 * 
		 * @param mimeType
		 *            the string to parse
		 * @return the mime type
		 * @throws InvalidMimeTypeException
		 *             if the string cannot be parsed
		 */
		public static HttpMimeType parseMimeType(String mimeType) {
			if (isBlank(mimeType)) {
				throw new InvalidMimeTypeException(mimeType, "'mimeType' must not be empty");
			}

			int index = mimeType.indexOf(';');
			String fullType = (index >= 0 ? mimeType.substring(0, index) : mimeType).trim();
			if (fullType.isEmpty()) {
				throw new InvalidMimeTypeException(mimeType, "'mimeType' must not be empty");
			}

			// java.net.HttpURLConnection returns a *; q=.2 Accept header
			if (HttpMimeType.WILDCARD_TYPE.equals(fullType)) {
				fullType = "*/*";
			}
			int subIndex = fullType.indexOf('/');
			if (subIndex == -1) {
				throw new InvalidMimeTypeException(mimeType, "does not contain '/'");
			}
			if (subIndex == fullType.length() - 1) {
				throw new InvalidMimeTypeException(mimeType, "does not contain subtype after '/'");
			}
			String type = fullType.substring(0, subIndex);
			String subtype = fullType.substring(subIndex + 1, fullType.length());
			if (HttpMimeType.WILDCARD_TYPE.equals(type) && !HttpMimeType.WILDCARD_TYPE.equals(subtype)) {
				throw new InvalidMimeTypeException(mimeType, "wildcard type is legal only in '*/*' (all mime types)");
			}

			Map<String, String> parameters = null;
			do {
				int nextIndex = index + 1;
				boolean quoted = false;
				while (nextIndex < mimeType.length()) {
					char ch = mimeType.charAt(nextIndex);
					if (ch == ';') {
						if (!quoted) {
							break;
						}
					} else if (ch == '"') {
						quoted = !quoted;
					}
					nextIndex++;
				}
				String parameter = mimeType.substring(index + 1, nextIndex).trim();
				if (parameter.length() > 0) {
					if (parameters == null) {
						parameters = new LinkedHashMap<String, String>(4);
					}
					int eqIndex = parameter.indexOf('=');
					if (eqIndex >= 0) {
						String attribute = parameter.substring(0, eqIndex).trim();
						String value = parameter.substring(eqIndex + 1, parameter.length()).trim();
						parameters.put(attribute, value);
					}
				}
				index = nextIndex;
			} while (index < mimeType.length());

			try {
				return new HttpMimeType(type, subtype, parameters);
			} catch (UnsupportedCharsetException ex) {
				throw new InvalidMimeTypeException(mimeType, "unsupported charset '" + ex.getCharsetName() + "'");
			} catch (IllegalArgumentException ex) {
				throw new InvalidMimeTypeException(mimeType, ex.getMessage());
			}
		}

		/**
		 * Parse the given, comma-separated string into a list of
		 * {@code MimeType} objects.
		 * 
		 * @param mimeTypes
		 *            the string to parse
		 * @return the list of mime types
		 * @throws IllegalArgumentException
		 *             if the string cannot be parsed
		 */
		public static List<HttpMimeType> parseMimeTypes(String mimeTypes) {
			if (isBlank(mimeTypes)) {
				return Collections.emptyList();
			}
			String[] tokens = StringUtils2.tokenizeToStringArray(mimeTypes, ",");
			List<HttpMimeType> result = new ArrayList<HttpMimeType>(tokens.length);
			for (String token : tokens) {
				result.add(parseMimeType(token));
			}
			return result;
		}

		/**
		 * Tokenize the given comma-separated string of {@code MimeType} objects
		 * into a {@code List<String>}. Unlike simple tokenization by ",", this
		 * method takes into account quoted parameters.
		 * 
		 * @param mimeTypes
		 *            the string to tokenize
		 * @return the list of tokens
		 * @since 5.1.3
		 */
		public static List<String> tokenize(String mimeTypes) {
			if (isBlank(mimeTypes)) {
				return Collections.emptyList();
			}
			List<String> tokens = new ArrayList<>();
			boolean inQuotes = false;
			int startIndex = 0;
			int i = 0;
			while (i < mimeTypes.length()) {
				switch (mimeTypes.charAt(i)) {
				case '"':
					inQuotes = !inQuotes;
					break;
				case ',':
					if (!inQuotes) {
						tokens.add(mimeTypes.substring(startIndex, i));
						startIndex = i + 1;
					}
					break;
				case '\\':
					i++;
					break;
				}
				i++;
			}
			tokens.add(mimeTypes.substring(startIndex));
			return tokens;
		}

		/**
		 * Return a string representation of the given list of {@code MimeType}
		 * objects.
		 * 
		 * @param mimeTypes
		 *            the string to parse
		 * @return the list of mime types
		 * @throws IllegalArgumentException
		 *             if the String cannot be parsed
		 */
		public static String toString(Collection<? extends HttpMimeType> mimeTypes) {
			StringBuilder builder = new StringBuilder();
			for (Iterator<? extends HttpMimeType> iterator = mimeTypes.iterator(); iterator.hasNext();) {
				HttpMimeType mimeType = iterator.next();
				mimeType.appendTo(builder);
				if (iterator.hasNext()) {
					builder.append(", ");
				}
			}
			return builder.toString();
		}

		/**
		 * Sorts the given list of {@code MimeType} objects by specificity.
		 * <p>
		 * Given two mime types:
		 * <ol>
		 * <li>if either mime type has a
		 * {@linkplain HttpMimeType#isWildcardType() wildcard type}, then the
		 * mime type without the wildcard is ordered before the other.</li>
		 * <li>if the two mime types have different
		 * {@linkplain HttpMimeType#getType() types}, then they are considered
		 * equal and remain their current order.</li>
		 * <li>if either mime type has a
		 * {@linkplain HttpMimeType#isWildcardSubtype() wildcard subtype} , then
		 * the mime type without the wildcard is sorted before the other.</li>
		 * <li>if the two mime types have different
		 * {@linkplain HttpMimeType#getSubtype() subtypes}, then they are
		 * considered equal and remain their current order.</li>
		 * <li>if the two mime types have a different amount of
		 * {@linkplain HttpMimeType#getParameter(String) parameters}, then the
		 * mime type with the most parameters is ordered before the other.</li>
		 * </ol>
		 * <p>
		 * For example: <blockquote>audio/basic &lt; audio/* &lt;
		 * *&#047;*</blockquote> <blockquote>audio/basic;level=1 &lt;
		 * audio/basic</blockquote> <blockquote>audio/basic ==
		 * text/html</blockquote> <blockquote>audio/basic ==
		 * audio/wave</blockquote>
		 * 
		 * @param mimeTypes
		 *            the list of mime types to be sorted
		 * @see <a href="http://tools.ietf.org/html/rfc7231#section-5.3.2">HTTP
		 *      1.1: Semantics and Content, section 5.3.2</a>
		 */
		public static void sortBySpecificity(List<HttpMimeType> mimeTypes) {
			Assert2.notNull(mimeTypes, "'mimeTypes' must not be null");
			if (mimeTypes.size() > 1) {
				Collections.sort(mimeTypes, SPECIFICITY_COMPARATOR);
			}
		}

		/**
		 * Generate a random MIME boundary as bytes, often used in multipart
		 * mime types.
		 */
		public static byte[] generateMultipartBoundary() {
			byte[] boundary = new byte[RND.nextInt(11) + 30];
			for (int i = 0; i < boundary.length; i++) {
				boundary[i] = BOUNDARY_CHARS[RND.nextInt(BOUNDARY_CHARS.length)];
			}
			return boundary;
		}

		/**
		 * Generate a random MIME boundary as String, often used in multipart
		 * mime types.
		 */
		public static String generateMultipartBoundaryString() {
			return new String(generateMultipartBoundary(), US_ASCII);
		}

		/**
		 * Exception thrown from {@link MimeTypeUtils#parseMimeType(String)} in
		 * case of encountering an invalid content type specification String.
		 *
		 * @since 4.0
		 */
		@SuppressWarnings("serial")
		public static class InvalidMimeTypeException extends IllegalArgumentException {

			private final String mimeType;

			/**
			 * Create a new InvalidContentTypeException for the given content
			 * type.
			 * 
			 * @param mimeType
			 *            the offending media type
			 * @param message
			 *            a detail message indicating the invalid part
			 */
			public InvalidMimeTypeException(String mimeType, String message) {
				super("Invalid mime type \"" + mimeType + "\": " + message);
				this.mimeType = mimeType;
			}

			/**
			 * Return the offending content type.
			 */
			public String getMimeType() {
				return this.mimeType;
			}

		}

		/**
		 * Comparator to sort {@link HttpMimeType MimeTypes} in order of
		 * specificity.
		 *
		 * @param <T>
		 *            the type of mime types that may be compared by this
		 *            comparator
		 */
		public static class SpecificityComparator<T extends HttpMimeType> implements Comparator<T> {

			@Override
			public int compare(T mimeType1, T mimeType2) {
				if (mimeType1.isWildcardType() && !mimeType2.isWildcardType()) { // */*
																					// <
																					// audio/*
					return 1;
				} else if (mimeType2.isWildcardType() && !mimeType1.isWildcardType()) { // audio/*
																						// >
																						// */*
					return -1;
				} else if (!mimeType1.getType().equals(mimeType2.getType())) { // audio/basic
																				// ==
																				// text/html
					return 0;
				} else { // mediaType1.getType().equals(mediaType2.getType())
					if (mimeType1.isWildcardSubtype() && !mimeType2.isWildcardSubtype()) { // audio/*
																							// <
																							// audio/basic
						return 1;
					} else if (mimeType2.isWildcardSubtype() && !mimeType1.isWildcardSubtype()) { // audio/basic
																									// >
																									// audio/*
						return -1;
					} else if (!mimeType1.getSubtype().equals(mimeType2.getSubtype())) { // audio/basic
																							// ==
																							// audio/wave
						return 0;
					} else { // mediaType2.getSubtype().equals(mediaType2.getSubtype())
						return compareParameters(mimeType1, mimeType2);
					}
				}
			}

			protected int compareParameters(T mimeType1, T mimeType2) {
				int paramsSize1 = mimeType1.getParameters().size();
				int paramsSize2 = mimeType2.getParameters().size();
				return Integer.compare(paramsSize2, paramsSize1); // audio/basic;level=1
																	// <
																	// audio/basic
			}
		}

	}

}